    @Override
    public void onLoad() {
        Permissions.registerAll();

        instance = this;
        EasyArmorStandsInitializer.initialize(this);
        loader.load();

        propertyTypeRegistry = new PropertyTypeRegistryImpl();
        new DefaultPropertyTypes(propertyTypeRegistry);

        ArmorStandElementType armorStandElementType = new ArmorStandElementType();
        entityElementProviderRegistry = new EntityElementProviderRegistryImpl();
        entityElementProviderRegistry.register(new ArmorStandElementProvider(armorStandElementType));
        entityElementProviderRegistry.register(new SimpleEntityElementProvider());

        menuSlotTypeRegistry = new MenuSlotTypeRegistryImpl();
        menuSlotTypeRegistry.register(new EntityCopySlotType());
        menuSlotTypeRegistry.register(new ArmorStandPartSlotType());
        menuSlotTypeRegistry.register(new ArmorStandPositionSlotType());
        menuSlotTypeRegistry.register(new ArmorStandSpawnSlotType(armorStandElementType));
        menuSlotTypeRegistry.register(new BackgroundSlotType());
        menuSlotTypeRegistry.register(new ColorAxisSlotType());
        menuSlotTypeRegistry.register(new ColorAxisChangeSlotType());
        menuSlotTypeRegistry.register(new ColorIndicatorSlotType());
        menuSlotTypeRegistry.register(new ColorPickerSlotType());
        menuSlotTypeRegistry.register(new ColorPresetSlotType());
        menuSlotTypeRegistry.register(new DestroySlotType());
        menuSlotTypeRegistry.register(new PropertySlotType());
        menuSlotTypeRegistry.register(new FallbackSlotType(Key.key("easyarmorstands", "spawn/item_display")));
        menuSlotTypeRegistry.register(new FallbackSlotType(Key.key("easyarmorstands", "spawn/block_display")));
        menuSlotTypeRegistry.register(new FallbackSlotType(Key.key("easyarmorstands", "spawn/text_display")));
        menuSlotTypeRegistry.register(new FallbackSlotType(Key.key("easyarmorstands", "spawn/interaction")));
        menuSlotTypeRegistry.register(new FallbackSlotType(Key.key("easyarmorstands", "spawn/mannequin")));
        menuSlotTypeRegistry.register(new FallbackSlotType(Key.key("easyarmorstands:traincarts/model_browser")));
        menuSlotTypeRegistry.register(new FallbackSlotType(Key.key("easyarmorstands:headdatabase")));

        MannequinCapability mannequinCapability = getCapability(MannequinCapability.class);
        if (mannequinCapability != null) {
            MannequinElementType<?> type = mannequinCapability.createElementType();
            entityElementProviderRegistry.register(new MannequinElementProvider<>(type, mannequinCapability));
            menuSlotTypeRegistry.register(new MannequinSpawnSlotType(type));
        }

        menuProvider = new MenuProviderImpl();

        regionPrivilegeManager = new RegionListenerManager();

        addonManager = new AddonManager(getLogger());
        addonManager.load(getClassLoader());
    }