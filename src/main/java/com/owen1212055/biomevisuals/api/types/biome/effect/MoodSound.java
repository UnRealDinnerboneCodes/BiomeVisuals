package com.owen1212055.biomevisuals.api.types.biome.effect;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public record MoodSound(
        @NotNull @SerializedName("sound") Sound soundEvent,
        @SerializedName("tick_delay") int tickDelay,
        @SerializedName("block_search_extent") int blockSearchExtent,
        @SerializedName("offset") double soundPositionOffset) {


    public static MoodSound of(@NotNull Sound soundEvent, int tickDelay, int blockSearchExtent, double soundPositionOffset) {
        return new MoodSound(soundEvent, tickDelay, blockSearchExtent, soundPositionOffset);
    }


}
