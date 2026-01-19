package me.m56738.easyarmorstands;

import me.m56738.easyarmorstands.util.FoliaScheduler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * EasyArmorStands main plugin class with Folia support
 */
public class EasyArmorStandsPlugin extends JavaPlugin {

    private static EasyArmorStandsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize FoliaScheduler for multi-threaded support
        FoliaScheduler.initialize(this);
        
        getLogger().info("EasyArmorStands enabled!");
        if (FoliaScheduler.isFolia()) {
            getLogger().info("🟢 Running on Folia with multi-threaded scheduler");
        } else {
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
