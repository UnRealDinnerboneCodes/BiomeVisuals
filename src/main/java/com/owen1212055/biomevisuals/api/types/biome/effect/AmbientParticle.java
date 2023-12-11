package com.owen1212055.biomevisuals.api.types.biome.effect;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.gson.JsonObject;
import com.unrealdinnerbone.crafty.api.ParticleOption;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AmbientParticle(ParticleOption particle, float probability) {

    public static AmbientParticle of(ParticleOption builder, float probability) {
        return new AmbientParticle(builder, probability);
    }

}
