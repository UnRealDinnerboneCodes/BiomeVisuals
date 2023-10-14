package com.owen1212055.biomevisuals.api.types.biome.effect;

import com.google.gson.JsonObject;
import com.owen1212055.biomevisuals.nms.ParticleDataSerializerImpl;
import org.bukkit.Particle;

public interface ParticleDataSerializer {

    ParticleDataSerializer INSTANCE = new ParticleDataSerializerImpl();
    static ParticleDataSerializer of() {
        return INSTANCE;
    }

    JsonObject serialize(Particle particle, Object data);

}
