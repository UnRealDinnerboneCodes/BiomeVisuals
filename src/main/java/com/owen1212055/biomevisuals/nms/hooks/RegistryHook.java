package com.owen1212055.biomevisuals.nms.hooks;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.owen1212055.biomevisuals.nms.Mappings;
import com.owen1212055.biomevisuals.nms.UnsafeUtils;
import com.owen1212055.biomevisuals.nms.WrappedCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

@ApiStatus.Internal
public class RegistryHook {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean hasInjected;
    public static void injectCodec() throws Exception {
        if(hasInjected) {
            return;
        }
        hasInjected = true;
        LOGGER.info("Injecting custom codec for registry overriding...");
        Codec<RegistryAccess> CAPTURED_CODEC = RegistrySynchronization.NETWORK_CODEC;  // Capture the codec stored in the variable, we will be replacing it.
        DynamicOps<JsonElement> BUILTIN_CONTEXT_OPS = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        Codec<RegistryAccess> INJECTED_CODEC = new WrappedCodec(CAPTURED_CODEC, BUILTIN_CONTEXT_OPS);
        // This declared field name comes from the Mojang-provided server JAR mappings. Verified to work
        UnsafeUtils.unsafeStaticSet(RegistrySynchronization.class.getDeclaredField(Mappings.NETWORK_CODEC), INJECTED_CODEC);
    }



}
