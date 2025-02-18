package com.owen1212055.biomevisuals.nms;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.owen1212055.biomevisuals.api.RegistryType;
import com.owen1212055.biomevisuals.api.events.BiomeRegistrySendEvent;
import com.owen1212055.biomevisuals.api.events.RegistrySendEvent;
import com.owen1212055.biomevisuals.api.types.biome.BiomeData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
public record WrappedCodec(Codec<RegistryAccess> capturedCodec, DynamicOps<JsonElement> dynamicOps) implements Codec<RegistryAccess> {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public <T> DataResult<T> encode(RegistryAccess input, DynamicOps<T> ops, T prefix) {
        RegistryAccess.Frozen frozen = MinecraftServer.getServer().registryAccess();


        DataResult<JsonElement> dataResult = capturedCodec.encodeStart(dynamicOps, frozen);

        Optional<DataResult.PartialResult<JsonElement>> optionalError = dataResult.error();
        if (optionalError.isPresent()) {
            LOGGER.warn("Failed to encode to JSON: " + optionalError.get().message());
            LOGGER.info("Sending client default data instead to circumvent this.");
            return capturedCodec.encode(input, ops, prefix);
        }
//
        JsonObject mainRegistry = dataResult.get().orThrow().getAsJsonObject();
        // Iterate through hooked registry types
        try {
            hookIntoMainRegistry(mainRegistry);
        } catch (Throwable e) {
            e.printStackTrace();
            LOGGER.warn("Failed to encode hooked data: {}", e.getMessage());
            LOGGER.info("Sending client default data instead to circumvent this.");
            return capturedCodec.encode(input, ops, prefix);
        }

        // Decode it back into NMS type from json
        DataResult<Pair<RegistryAccess, JsonElement>> dataresult = capturedCodec.decode(dynamicOps, mainRegistry);
        // Fail?
        if (dataresult.error().isPresent()) {
            LOGGER.warn("Failed to decode hooked data: {}", dataresult.error().get().message());
            LOGGER.info("Sending client default data instead to circumvent this.");
            return capturedCodec.encode(input, ops, prefix);
        } else {
            // The captured codec of MC 1.19.3 expects an instance of ImmutableRegistryAccess
            RegistryAccess holder = dataresult.map(Pair::getFirst).result().orElseThrow().freeze();

            // If we good encode it to whatever
            return capturedCodec.encode(holder, ops, prefix);
        }
    }

    @Override
    public <T> DataResult<Pair<RegistryAccess, T>> decode(DynamicOps<T> ops, T input) {
        return capturedCodec.decode(ops, input);
    }

    private void hookIntoMainRegistry(JsonObject mainRegistry) {
        for (RegistryType hookedType : RegistryType.values()) {
            // Retrieve the registry array
            JsonArray registry = mainRegistry.get(hookedType.getKey().toString()).getAsJsonObject().getAsJsonArray("value");

            // Populate maps keyed by identifiers, because they are stored in an array and this allows us to fetch them nicely.
            Map<NamespacedKey, Object> registryEntries = new HashMap<>(registry.size());
            Map<NamespacedKey, JsonObject> registryEntryHolders = new HashMap<>(registry.size());
            Class<?> registryEntryDataType = hookedType.getDataType();
            for (JsonElement jsonElement : registry) {
                NamespacedKey namespacedKey = NamespacedKey.fromString(jsonElement.getAsJsonObject().get("name").getAsString());
                JsonObject registryEntry = jsonElement.getAsJsonObject().getAsJsonObject("element");
                Object registryEntryData = JsonAdapter.adapt(registryEntry, registryEntryDataType);

                registryEntries.put(namespacedKey, registryEntryData);
                registryEntryHolders.put(namespacedKey, jsonElement.getAsJsonObject());

                LOGGER.debug("Deserializing registry entry to strongly typed object: {}, {} -> {}", namespacedKey, registryEntry, registryEntryData);
            }

            // Let soundEvent handlers observe and modify the registry data.
            RegistrySendEvent<?> sendEvent;
            if (registryEntryDataType == BiomeData.class) {
                sendEvent = new BiomeRegistrySendEvent(uncheckedCastMap(registryEntries));
            } else {
                LOGGER.error("Unimplemented event for registry data type: {}. This is an internal plugin error that must be fixed", registryEntryDataType.getName());
                LOGGER.info("Sending client default data instead to circumvent this.");
                throw new UnsupportedOperationException();
            }

            Bukkit.getPluginManager().callEvent(sendEvent);

            // Apply changes made to the registry entries map only if the registry override soundEvent was not cancelled.
            if (sendEvent.isCancelled()) {
                continue;
            }

            for (Map.Entry<NamespacedKey, Object> overriddenEntry : registryEntries.entrySet()) {
                JsonObject registryEntryHolder = registryEntryHolders.get(overriddenEntry.getKey());

                // Only replace if present to prevent handlers that add new registry entries from having any effect.
                if (registryEntryHolder != null) {
                    JsonObject asJsonObject = registryEntryHolder.get("element").getAsJsonObject();
                    mergeObject(asJsonObject, JsonAdapter.adapt(overriddenEntry.getValue()));
                    registryEntryHolder.add("element", asJsonObject);
                }
            }

        }

    }

    private static void mergeObject(JsonObject into, JsonObject merging) {
        for (String overrideKey : merging.keySet()) {
            JsonElement element = merging.get(overrideKey);

            if (element.isJsonObject()) {
                JsonElement original = into.get(overrideKey);
                if (original != null && original.isJsonObject()) {
                    mergeObject(original.getAsJsonObject(), element.getAsJsonObject());
                } else {
                    into.add(overrideKey, element);
                }
            } else {
                into.add(overrideKey, element);
            }

        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> uncheckedCastMap(Map<?, ?> map) {
        return (Map<K, V>) map;
    }
}
