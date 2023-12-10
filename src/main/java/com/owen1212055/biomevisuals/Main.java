package com.owen1212055.biomevisuals;

import com.owen1212055.biomevisuals.api.APIHocks;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        APIHocks.init();
    }
}
