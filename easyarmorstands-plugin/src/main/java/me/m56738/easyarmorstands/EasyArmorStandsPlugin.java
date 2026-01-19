package me.m56738.easyarmorstands;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * EasyArmorStands main plugin class
 */
public class EasyArmorStandsPlugin extends JavaPlugin {

    private static EasyArmorStandsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("EasyArmorStands enabled!");
        
        // Detect if running on Folia or Paper
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            getLogger().info("🟢 Running on Folia with multi-threaded scheduler");
        } catch (ClassNotFoundException e) {
            getLogger().info("🔵 Running on Paper/Spigot with Bukkit scheduler");
        }
    }

    @Override
    public void onDisable() {
        instance = null;
        getLogger().info("EasyArmorStands disabled!");
    }

    public static EasyArmorStandsPlugin getInstance() {
        return instance;
    }
}
