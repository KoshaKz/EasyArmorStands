package me.m56738.easyarmorstands.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Unified scheduler wrapper for both Paper and Folia servers.
 * Automatically detects the server type and uses the appropriate scheduler.
 * Based on FerrumCore's Scheduler implementation.
 */
public final class FoliaScheduler {

    private static final boolean IS_FOLIA;
    private static final org.bukkit.plugin.Plugin PLUGIN;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        IS_FOLIA = folia;
        PLUGIN = Bukkit.getPluginManager().getPlugins()[0]; // Will be set properly during plugin initialization
    }

    private static org.bukkit.plugin.Plugin plugin;

    /**
     * Initialize the scheduler with the plugin instance.
     * Must be called during plugin onEnable().
     */
    public static void initialize(@NotNull org.bukkit.plugin.Plugin pluginInstance) {
        plugin = pluginInstance;
    }

    /**
     * Check if the server is running Folia.
     */
    public static boolean isFolia() {
        return IS_FOLIA;
    }

    // ==================== SYNC EXECUTION ====================

    /**
     * Execute a task synchronously.
     */
    public static void run(@NotNull Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    /**
     * Execute a region-specific task at a location.
     */
    public static @NotNull ScheduledTaskWrapper runRegion(@NotNull Location location, @NotNull Runnable runnable) {
        if (IS_FOLIA) {
            ScheduledTask task = Bukkit.getRegionScheduler().run(
                    plugin,
                    location,
                    t -> runnable.run()
            );
            return new ScheduledTaskWrapper(task);
        } else {
            return new ScheduledTaskWrapper(Bukkit.getScheduler().runTask(plugin, runnable));
        }
    }

    /**
     * Run a repeating task at a specific region.
     */
    public static @NotNull ScheduledTaskWrapper runRegionTimer(
            @NotNull Location location,
            @NotNull Runnable runnable,
            long delay,
            long period
    ) {
        if (IS_FOLIA) {
            ScheduledTask task = Bukkit.getRegionScheduler().runAtFixedRate(
                    plugin,
                    location,
                    t -> runnable.run(),
                    Math.max(1, delay),
                    Math.max(1, period)
            );
            return new ScheduledTaskWrapper(task);
        } else {
            return new ScheduledTaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period));
        }
    }

    /**
     * Run a delayed task at a specific region.
     */
    public static @NotNull ScheduledTaskWrapper runRegionLater(
            @NotNull Location location,
            @NotNull Runnable runnable,
            long delay
    ) {
        if (IS_FOLIA) {
            ScheduledTask task = Bukkit.getRegionScheduler().runDelayed(
                    plugin,
                    location,
                    t -> runnable.run(),
                    Math.max(1, delay)
            );
            return new ScheduledTaskWrapper(task);
        } else {
            return new ScheduledTaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay));
        }
    }

    // ==================== ASYNC EXECUTION ====================

    /**
     * Run a task asynchronously.
     */
    public static void runAsync(@NotNull Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runNow(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    /**
     * Run a delayed async task.
     */
    public static @NotNull ScheduledTaskWrapper runAsyncLater(@NotNull Runnable runnable, long delay) {
        if (IS_FOLIA) {
            ScheduledTask task = Bukkit.getAsyncScheduler().runDelayed(plugin, t -> runnable.run(), delay * 50); // Convert ticks to ms
            return new ScheduledTaskWrapper(task);
        } else {
            return new ScheduledTaskWrapper(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay));
        }
    }

    /**
     * Run a repeating async task.
     */
    public static @NotNull ScheduledTaskWrapper runAsyncTimer(
            @NotNull Runnable runnable,
            long delay,
            long period
    ) {
        if (IS_FOLIA) {
            ScheduledTask task = Bukkit.getAsyncScheduler().runAtFixedRate(
                    plugin,
                    t -> runnable.run(),
                    delay * 50, // Convert to ms
                    period * 50 // Convert to ms
            );
            return new ScheduledTaskWrapper(task);
        } else {
            return new ScheduledTaskWrapper(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period));
        }
    }

    // ==================== DELAYED TASKS ====================

    /**
     * Run a delayed task.
     */
    public static @NotNull ScheduledTaskWrapper runLater(@NotNull Runnable runnable, long delay) {
        if (IS_FOLIA) {
            ScheduledTask task = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> runnable.run(), delay);
            return new ScheduledTaskWrapper(task);
        } else {
            return new ScheduledTaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay));
        }
    }

    /**
     * Run a delayed task with BukkitRunnable.
     */
    public static @NotNull ScheduledTaskWrapper runLater(@NotNull BukkitRunnable runnable, long delay) {
        if (!IS_FOLIA) {
            return new ScheduledTaskWrapper(runnable.runTaskLater(plugin, delay));
        }

        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runDelayed(
                plugin,
                t -> runnable.run(),
                delay
        );
        return new ScheduledTaskWrapper(task);
    }

    // ==================== TIMER TASKS ====================

    /**
     * Run a repeating task.
     */
    public static @NotNull ScheduledTaskWrapper runTimer(@NotNull Runnable runnable, long delay, long period) {
        if (IS_FOLIA) {
            ScheduledTask task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    plugin,
                    t -> runnable.run(),
                    Math.max(1, delay),
                    period
            );
            return new ScheduledTaskWrapper(task);
        } else {
            return new ScheduledTaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period));
        }
    }

    /**
     * Run a repeating task with BukkitRunnable.
     */
    public static @NotNull ScheduledTaskWrapper runTimer(@NotNull BukkitRunnable runnable, long delay, long period) {
        if (!IS_FOLIA) {
            return new ScheduledTaskWrapper(runnable.runTaskTimer(plugin, delay, period));
        }

        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                plugin,
                t -> runnable.run(),
                Math.max(1, delay),
                period
        );
        return new ScheduledTaskWrapper(task);
    }

    /**
     * Run a task while a condition is true.
     */
    public static void runWhile(@NotNull BooleanSupplier condition, long delay, long period) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    plugin,
                    task -> {
                        if (!condition.getAsBoolean()) {
                            task.cancel();
                        }
                    },
                    Math.max(1, delay),
                    Math.max(1, period)
            );
            return;
        }

        final BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {
                    if (!condition.getAsBoolean()) {
                        task[0].cancel();
                    }
                },
                delay,
                period
        );
    }

    // ==================== TASK WRAPPER ====================

    /**
     * Unified wrapper for scheduled tasks (Folia ScheduledTask or Bukkit BukkitTask).
     */
    public static class ScheduledTaskWrapper {
        @Nullable private final ScheduledTask foliaTask;
        @Nullable private final BukkitTask bukkitTask;

        /**
         * Wrap a Folia ScheduledTask.
         */
        public ScheduledTaskWrapper(@NotNull ScheduledTask foliaTask) {
            this.foliaTask = foliaTask;
            this.bukkitTask = null;
        }

        /**
         * Wrap a Bukkit BukkitTask.
         */
        public ScheduledTaskWrapper(@NotNull BukkitTask bukkitTask) {
            this.foliaTask = null;
            this.bukkitTask = bukkitTask;
        }

        /**
         * Cancel the task.
         */
        public void cancel() {
            if (foliaTask != null) {
                foliaTask.cancel();
            } else if (bukkitTask != null) {
                bukkitTask.cancel();
            }
        }

        /**
         * Check if the task is cancelled.
         */
        public boolean isCancelled() {
            if (foliaTask != null) return foliaTask.isCancelled();
            if (bukkitTask != null) return bukkitTask.isCancelled();
            return true;
        }

        /**
         * Get the underlying Folia task (if available).
         */
        @Nullable
        public ScheduledTask getFoliaTask() {
            return foliaTask;
        }

        /**
         * Get the underlying Bukkit task (if available).
         */
        @Nullable
        public BukkitTask getBukkitTask() {
            return bukkitTask;
        }
    }
}
