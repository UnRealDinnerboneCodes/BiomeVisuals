package com.owen1212055.biomevisuals.nms;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.owen1212055.biomevisuals.api.types.biome.effect.ParticleDataSerializer;
import net.minecraft.core.particles.ParticleTypes;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_20_R3.CraftParticle;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ParticleDataSerializerImpl implements ParticleDataSerializer {
    @Override
    public JsonObject serialize(Particle particle, Object data) {
        JsonElement jsonElement = ParticleTypes.CODEC.encode(CraftParticle.createParticleParam(particle, data), JsonOps.INSTANCE, JsonOps.INSTANCE.empty())
                .get()
                .orThrow();
        return jsonElement.getAsJsonObject();
    }
}
