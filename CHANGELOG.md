# Changelog - EasyArmorStands Folia Edition

## Version 3.0.0-FOLIA (2026-01-19)

### Major Changes

#### ✨ Full Folia Support
- Added comprehensive Folia server support with unified scheduler implementation
- Automatic detection of server type (Paper/Spigot vs Folia) at runtime
- All scheduler operations compatible with both Paper and Folia threading models
- Region-aware task scheduling for Folia's regionalized architecture

#### 🎯 Version Restrictions
- **Removed** support for Minecraft versions prior to 1.21.10
- **Supported versions**: 1.21.10 - 1.21.11 (Paper and Folia)
- Simplified build configuration for maintenance and stability

### New Features

#### `FoliaScheduler` Utility Class
New unified scheduler wrapper (`me.m56738.easyarmorstands.util.FoliaScheduler`) with:
- Automatic server type detection
- Global region task scheduling
- Region-specific task scheduling
- Async task support
- Task wrapping for consistent API across platforms
- Backward compatibility with Bukkit scheduler API

### Technical Details

#### Build Configuration
- Updated to use Folia API 1.21.11-R0.1-SNAPSHOT
- Removed all version-specific source sets for versions < 1.21.10
- Cleaned up multi-version support infrastructure
- Java compatibility maintained at 8+ (compiled with 21)

#### Scheduler Migration
- Replaces Bukkit `runTaskTimer` with multi-threaded scheduler
- Uses `GlobalRegionScheduler` for global tasks on Folia
- Uses `RegionScheduler` for location-specific tasks on Folia
- Falls back to standard Bukkit scheduler on Paper/Spigot

#### API Changes
- New `ScheduledTaskWrapper` class for unified task management
- `FoliaScheduler.initialize(Plugin)` must be called during plugin enable
- `FoliaScheduler.isFolia()` utility method for runtime detection

### Performance Improvements
- Folia support enables true multi-threaded execution for better performance on high-player servers
- Region-based task execution reduces lock contention
- Async scheduler support for non-blocking operations

### Compatibility
- ✅ Paper 1.21.10 - 1.21.11
- ✅ Folia 1.21.11
- ✅ Spigot 1.21.10 - 1.21.11 (with Paper/Spigot-compatible API)

### Breaking Changes
- Dropped support for all Minecraft versions < 1.21.10
- Removed multi-version source sets (v1_8, v1_9, v1_10_2, ... v1_21_9)
- Hangar/Modrinth deployment now only targets 1.21.10-1.21.11

### Migration Guide

If upgrading from older versions:
1. Ensure your server is running Paper or Folia 1.21.10+
2. Backup your EasyArmorStands configuration
3. Replace the plugin JAR file
4. Clear the generated `v1_*` source directories (optional cleanup)
5. Restart your server

### Development Notes

- Folia support based on regionalized server architecture
- Single-threaded fallback ensures Paper/Spigot compatibility
- No external FoliaScheduler dependency required (built-in implementation)
- All existing command and functionality remains unchanged
- Region-aware operations handle location-based entity management

### Known Limitations

- Folia support is optimized for regionalized worlds
- Some cluster management features may behave differently on Folia
- Large coordinate operations may need task splitting on Folia

### Contributors
- KoshaKz - Folia support implementation and migration
- Original EasyArmorStands by 56738

---

**Note:** This is a fork focused on Folia support. For the original EasyArmorStands project, visit [56738/EasyArmorStands](https://github.com/56738/EasyArmorStands).
