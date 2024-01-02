package com.owen1212055.biomevisuals.api.types.biome;

import com.owen1212055.biomevisuals.api.types.biome.effect.BiomeEffect;
import org.jetbrains.annotations.NotNull;

public class BiomeDataBuilder {

    private boolean hasPrecipitation;
    private Float temperature;
    private TemperatureModifier temperatureModifier;
    private Float downfall;
    private BiomeEffect effect;

    private BiomeDataBuilder() {
    }

    public static BiomeDataBuilder newBuilder() {
        return new BiomeDataBuilder();
    }


    /**
     * Sets the temperature of this biome.
     * <p>
     * If there is no foliage color override set, this value is used in juction with downfall to
     * determine the color.
     * High values may prevent precipitation from occuring.
     *
     * @param temperature temperature value (0-1)
     * @return self
     */
    public BiomeDataBuilder temperature(float temperature) {
        this.temperature = temperature;
        return this;
    }

    /**
     * Sets the temperature modifier of this biome.
     * <p>
     * This is used for determining temperature at certain coordinates.
     *
     * @param temperatureModifier temperature modifier
     * @return self
     */
    public BiomeDataBuilder temperatureModifier(TemperatureModifier temperatureModifier) {
        this.temperatureModifier = temperatureModifier;
        return this;
    }

    /**
     * Sets the downfall of this biome.
     * <p>
     * Used for determining humidity.
     * <p>
     * If there is no foliage color override set, this value is used in juction with downfall to
     * determine the color.
     * High values may prevent precipitation from occuring.
     *
     * @param downfall downfall (0-1)
     * @return self
     */
    public BiomeDataBuilder downfall(float downfall) {
        this.downfall = downfall;
        return this;
    }

    /**
     * Sets the biome effects for this biome.
     * <p>
     * Used for a variety of visual/auditory effects
     *
     * @param effect effect
     * @return self
     */
    public BiomeDataBuilder effect(BiomeEffect effect) {
        this.effect = effect;
        return this;
    }

    public BiomeDataBuilder precipitation(boolean precipitation) {
        this.hasPrecipitation = precipitation;
        return this;
    }

    @NotNull
    public BiomeData build() {
        return new BiomeData(hasPrecipitation, temperature, temperatureModifier, downfall, effect);
    }

}
