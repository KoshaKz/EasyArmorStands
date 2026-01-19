    @Override
    public void onEnable() {
        // Initialize FoliaScheduler
        me.m56738.easyarmorstands.util.FoliaScheduler.initialize(this);
        
        new Metrics(this, 17911);
        adventure = BukkitAudiences.create(this);
        gizmos = BukkitGizmos.create(this);