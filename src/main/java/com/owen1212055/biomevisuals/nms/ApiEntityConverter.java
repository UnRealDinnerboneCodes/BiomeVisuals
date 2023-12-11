package com.owen1212055.biomevisuals.nms;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.owen1212055.biomevisuals.api.types.biome.effect.*;
import com.unrealdinnerbone.crafty.particle.CraftyParticle;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_20_R3.CraftSound;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.Objects;

@ApiStatus.Internal

public class ApiEntityConverter {

    private static final Field probabilityField;

    static {
        try {
            probabilityField = AmbientParticleSettings.class.getDeclaredField(Mappings.AMBIENT_PARTICLE_PROBABILITY);
            probabilityField.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonElement serialize(AdditionSound sound) {
        AmbientAdditionsSettings settings = new AmbientAdditionsSettings(Holder.direct(CraftSound.bukkitToMinecraft(sound.soundEvent())), sound.tickChance());

        return encode(AmbientAdditionsSettings.CODEC, settings);
    }

    public static AdditionSound deserializeAdditionSound(JsonElement sound) {
        AmbientAdditionsSettings settings = decode(AmbientAdditionsSettings.CODEC, sound);

        return AdditionSound.of(Objects.requireNonNull(nmsSoundEventToBukkit(settings.getSoundEvent().value())), settings.getTickChance());
    }

    public static JsonElement serialize(AmbientParticle particle) {
        ParticleOptions options = CraftParticle.createParticleParam(particle.particle().getParticle(), particle.particle().getData());
        AmbientParticleSettings settings = new AmbientParticleSettings(options, particle.probability());

        return encode(AmbientParticleSettings.CODEC, settings);
    }

    public static AmbientParticle deserializeAmbientParticle(JsonElement particle) {
        AmbientParticleSettings settings = decode(AmbientParticleSettings.CODEC, particle);

        ParticleOptions options = settings.getOptions();

        CraftyParticle craftyParticle = new CraftyParticle(options);

        try {
            return AmbientParticle.of(craftyParticle, (float) probabilityField.get(settings));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static Color fromVec3(Vector3f vec3) {
        return Color.fromRGB((int) (vec3.x() * 255), (int) (vec3.y() * 255), (int) (vec3.z() * 255));
    }



    public static JsonElement serialize(AmbientSound ambientSound) {
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(new ResourceLocation(ambientSound.getSoundEvent().getKey().toString()));

        return encode(SoundEvent.CODEC, Holder.direct(soundEvent));
    }

    public static AmbientSound deserializeAmbientSound(JsonElement ambientSound) {
        Holder<SoundEvent> soundEvent = decode(SoundEvent.CODEC, ambientSound);

        return AmbientSound.of(Objects.requireNonNull(nmsSoundEventToBukkit(soundEvent.value())));
    }

    public static JsonElement serialize(MoodSound moodSound) {
        AmbientMoodSettings settings = new AmbientMoodSettings(Holder.direct(CraftSound.bukkitToMinecraft(moodSound.soundEvent())), moodSound.tickDelay(), moodSound.blockSearchExtent(), moodSound.soundPositionOffset());

        return encode(AmbientMoodSettings.CODEC, settings);
    }

    public static MoodSound deserializeMoodSound(JsonElement moodSound) {
        AmbientMoodSettings settings = decode(AmbientMoodSettings.CODEC, moodSound);

        return MoodSound.of(Objects.requireNonNull(nmsSoundEventToBukkit(settings.getSoundEvent().value())), settings.getTickDelay(), settings.getBlockSearchExtent(), settings.getSoundPositionOffset());
    }

    public static JsonElement serialize(Music music) {
        net.minecraft.sounds.Music nmsMusic = new net.minecraft.sounds.Music(Holder.direct(CraftSound.bukkitToMinecraft(music.getSoundEvent())), music.getMinDelay(), music.getMaxDelay(), music.replaceCurrentMusic());

        return encode(net.minecraft.sounds.Music.CODEC, nmsMusic);
    }

    public static Music deserializeMusic(JsonElement music) {
        net.minecraft.sounds.Music nmsMusic = decode(net.minecraft.sounds.Music.CODEC, music);

        return Music.of(Objects.requireNonNull(nmsSoundEventToBukkit(nmsMusic.getEvent().value())), nmsMusic.getMinDelay(), nmsMusic.getMaxDelay(), nmsMusic.replaceCurrentMusic());
    }

    // Workaround method for avoiding NPEs thrown when CraftSound#getBukkit tries to access the vanilla
    // registries (maybe due to async context or being called too early?)
    private static Sound nmsSoundEventToBukkit(SoundEvent soundEvent) {
        return Registry.SOUNDS.get(Objects.requireNonNull(CraftNamespacedKey.fromMinecraft(soundEvent.getLocation())));
    }

    private static <T> JsonElement encode(Codec<T> type, T object) {
        return type.encode(object, JsonOps.INSTANCE, JsonOps.INSTANCE.empty()).get().orThrow();
    }

    private static <T> T decode(Codec<T> type, JsonElement object) {
        DynamicOps<JsonElement> BUILTIN_CONTEXT_OPS = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        DataResult<Pair<T, JsonElement>> decode1 = type.decode(BUILTIN_CONTEXT_OPS, object);
        return decode1.map(Pair::getFirst).result().orElseThrow();
    }
}
