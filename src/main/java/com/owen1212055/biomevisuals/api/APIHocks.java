package com.owen1212055.biomevisuals.api;

import com.mojang.logging.LogUtils;
import com.owen1212055.biomevisuals.nms.hooks.RegistryHook;
import org.slf4j.Logger;

public class APIHocks
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static void init() {
        try {
            RegistryHook.injectCodec();
        }
        catch (Exception e) {
            LOGGER.error("Failed to inject codec for registry overriding", e);
        }
    }
}
