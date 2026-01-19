    @Override
    public void onEnable() {
        new Metrics(this, 17911);
        adventure = BukkitAudiences.create(this);
        gizmos = BukkitGizmos.create(this);

        // Detect if running on Folia or Paper
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            getLogger().info("\u2705 [Folia Support] Running on Folia with multi-threaded scheduler");
        } catch (ClassNotFoundException e) {
            getLogger().info("\u2b55 [Folia Support] Running on Paper/Spigot with single-threaded scheduler");
        }

        loadConfig();
        messageManager = new MessageManager(this);
        messageManager.load(config);

        loadProperties();