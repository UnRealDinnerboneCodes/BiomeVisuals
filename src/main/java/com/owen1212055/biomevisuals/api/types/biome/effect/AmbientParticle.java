package com.owen1212055.biomevisuals.api.types.biome.effect;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AmbientParticle(ParticleBuilder particle, float probability) {

    public static AmbientParticle of(ParticleBuilder builder, float probability) {
        return new AmbientParticle(builder, probability);
    }

}
