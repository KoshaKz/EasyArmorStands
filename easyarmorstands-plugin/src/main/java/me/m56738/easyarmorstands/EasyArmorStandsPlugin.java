package me.m56738.easyarmorstands;

import me.m56738.easyarmorstands.addon.AddonRegistry;
import me.m56738.easyarmorstands.api.EasyArmorStandsAPI;
import me.m56738.easyarmorstands.api.element.Element;
import me.m56738.easyarmorstands.api.property.PropertyRegistry;
import me.m56738.easyarmorstands.api.property.builtin.BuiltinProperties;
import me.m56738.easyarmorstands.capability.CapabilityRegistry;
import me.m56738.easyarmorstands.capability.equipment.EquipmentCapabilityAdapter;
import me.m56738.easyarmorstands.command.EasCommand;
import me.m56738.easyarmorstands.config.Config;
import me.m56738.easyarmorstands.context.PluginContext;
import me.m56738.easyarmorstands.editor.EditorRegistry;
import me.m56738.easyarmorstands.element.EntityElementProviderRegistry;
import me.m56738.easyarmorstands.group.GroupRegistry;
import me.m56738.easyarmorstands.history.HistoryRegistry;
import me.m56738.easyarmorstands.item.ToolFactory;
import me.m56738.easyarmorstands.message.MessageRegistry;
import me.m56738.easyarmorstands.permission.Permissions;
import me.m56738.easyarmorstands.session.SessionManagerImpl;
import me.m56738.easyarmorstands.session.SessionListener;
import me.m56738.easyarmorstands.util.FoliaScheduler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.bukkit.Bukkit;
import org.bukkit.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EasyArmorStandsPlugin extends JavaPlugin {
    private static final int METRICS_ID = 17911;
    private static EasyArmorStandsPlugin instance;
    
    private BukkitAudiences adventure;
    private me.m56738.easyarmorstands.gizmos.BukkitGizmos gizmos;
    private ToolFactory toolFactory;
    private SessionManagerImpl sessionManager;
    private SessionListener sessionListener;
    private PluginContext context;
    private CapabilityRegistry capabilityRegistry;
    private EditorRegistry editorRegistry;
    private PropertyRegistry propertyRegistry;
    private MessageRegistry messageRegistry;
    private AddonRegistry addonRegistry;
    private EntityElementProviderRegistry entityElementProviderRegistry;
    private GroupRegistry groupRegistry;
    private HistoryRegistry historyRegistry;
    private Map<Player, me.m56738.easyarmorstands.history.History> playerHistories = new HashMap<>();
    private Config config;

    @Override
    public void onEnable() {
        // Initialize FoliaScheduler
        FoliaScheduler.initialize(this);
        
        instance = this;
        
        // Initialize Adventure
        adventure = BukkitAudiences.create(this);
        
        // Initialize Gizmos
        gizmos = me.m56738.easyarmorstands.gizmos.BukkitGizmos.create(this);
        
        // Load configuration
        config = new Config(this);
        config.load();
        
        // Initialize metrics
        new me.m56738.easyarmorstands.util.Metrics(this, METRICS_ID);
        
        // Initialize registries and managers
        capabilityRegistry = new CapabilityRegistry();
        editorRegistry = new EditorRegistry();
        propertyRegistry = new PropertyRegistry();
        messageRegistry = new MessageRegistry();
        addonRegistry = new AddonRegistry(this);
        entityElementProviderRegistry = new EntityElementProviderRegistry();
        groupRegistry = new GroupRegistry();
        historyRegistry = new HistoryRegistry();
        
        // Initialize context
        context = new PluginContext(this);
        
        // Initialize tool factory
        toolFactory = new ToolFactory(this);
        
        // Initialize session manager
        sessionManager = new SessionManagerImpl(this);
        sessionListener = new SessionListener(this, sessionManager);
        
        // Register built-in properties
        BuiltinProperties.register(propertyRegistry);
        
        // Register equipment capability
        capabilityRegistry.register(new EquipmentCapabilityAdapter());
        
        // Register command
        getCommand("eas").setExecutor(new EasCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(sessionListener, this);
        
        // Start session update task
        FoliaScheduler.scheduleRepeatingTask(this, () -> {
            sessionListener.update();
        }, 0, 1);
        
        getLogger().info("EasyArmorStands enabled!");
        if (FoliaScheduler.isFolia()) {
            getLogger().info("Running on Folia with multi-threaded scheduler");
        }
    }

    @Override
    public void onDisable() {
        // Cleanup
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        
        if (sessionManager != null) {
            sessionManager.close();
        }
        
        if (gizmos != null) {
            gizmos.close();
            gizmos = null;
        }
        
        instance = null;
    }

    public static EasyArmorStandsPlugin getInstance() {
        return instance;
    }

    public BukkitAudiences getAdventure() {
        return adventure;
    }

    public me.m56738.easyarmorstands.gizmos.BukkitGizmos getGizmos() {
        return gizmos;
    }

    public ToolFactory getToolFactory() {
        return toolFactory;
    }

    public SessionManagerImpl getSessionManager() {
        return sessionManager;
    }

    public PluginContext getContext() {
        return context;
    }

    public CapabilityRegistry getCapabilityRegistry() {
        return capabilityRegistry;
    }

    public <T> @Nullable T getCapability(Class<T> capabilityClass) {
        return capabilityRegistry.getCapability(capabilityClass);
    }

    public EditorRegistry getEditorRegistry() {
        return editorRegistry;
    }

    public PropertyRegistry getPropertyRegistry() {
        return propertyRegistry;
    }

    public MessageRegistry getMessageRegistry() {
        return messageRegistry;
    }

    public AddonRegistry getAddonRegistry() {
        return addonRegistry;
    }

    public EntityElementProviderRegistry entityElementProviderRegistry() {
        return entityElementProviderRegistry;
    }

    public GroupRegistry getGroupRegistry() {
        return groupRegistry;
    }

    public HistoryRegistry getHistoryRegistry() {
        return historyRegistry;
    }

    public Config getConfig() {
        return config;
    }

    public boolean isTool(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return false;
        }
        return toolFactory.isTool(itemStack);
    }

    public @NotNull me.m56738.easyarmorstands.history.History getHistory(Player player) {
        return playerHistories.computeIfAbsent(player, k -> new me.m56738.easyarmorstands.history.History());
    }

    public me.m56738.easyarmorstands.editor.Editor createEditor(Player player, Element element) {
        me.m56738.easyarmorstands.editor.Editor editor = editorRegistry.create(player, element, this);
        if (editor != null) {
            editor.open();
        }
        return editor;
    }
}
