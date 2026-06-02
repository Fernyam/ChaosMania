package net.fernyam.chaosmania.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fernyam.chaosmania.ChaosManiaMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JSONSettingManager {
    // Константы
    public static final UUID ALL_PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String ALL_PLAYER_NAME = "§6§l[ВСЕМ]";

    private static final Logger LOGGER = LogManager.getLogger(ChaosManiaMod.MOD_ID);
    private static final Path STORE_FILE = FMLPaths.GAMEDIR.get().resolve(
            String.format("config/%s/player_setting.json", ChaosManiaMod.MOD_ID)
    );

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type SETTINGS_TYPE = new TypeToken<ArrayList<PlayerSettings>>() {}.getType();

    // Кэш и потоко-безопасность
    private static Map<UUID, PlayerSettings> cache = new HashMap<>();
    private static boolean cacheLoaded = false;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // ==================== Инициализация и загрузка кэша ====================

    private static void loadCache() {
        lock.writeLock().lock();
        try {
            if (cacheLoaded) return;

            cache.clear();

            if (!Files.exists(STORE_FILE)) {
                createJSON();
            }

            try {
                String json = Files.readString(STORE_FILE);
                List<PlayerSettings> settingsList = GSON.fromJson(json, SETTINGS_TYPE);

                if (settingsList != null) {
                    for (PlayerSettings ps : settingsList) {
                        try {
                            UUID uuid = UUID.fromString(ps.getUuidPlayer());
                            ps.setUuid(uuid);
                            cache.put(uuid, ps);
                        } catch (IllegalArgumentException e) {
                            LOGGER.error("Invalid UUID in config: {}", ps.getUuidPlayer(), e);
                        }
                    }
                }

                // Убедимся, что глобальные настройки существуют
                if (!cache.containsKey(ALL_PLAYER_UUID)) {
                    PlayerSettings global = new PlayerSettings(ALL_PLAYER_NAME, ALL_PLAYER_UUID);
                    cache.put(ALL_PLAYER_UUID, global);
                    saveCacheToFile();
                }

                cacheLoaded = true;
                LOGGER.info("Loaded settings cache from {}, {} players loaded", STORE_FILE, cache.size());

            } catch (Exception e) {
                LOGGER.error("Failed to load config file!", e);
                // Создаём пустой кэш, чтобы не падать
                cache.put(ALL_PLAYER_UUID, new PlayerSettings(ALL_PLAYER_NAME, ALL_PLAYER_UUID));
                cacheLoaded = true;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static void saveCacheToFile() {
        lock.readLock().lock();
        try {
            Path configDir = STORE_FILE.getParent();
            if (Files.notExists(configDir)) {
                Files.createDirectories(configDir);
            }

            List<PlayerSettings> list = new ArrayList<>(cache.values());
            try (FileWriter writer = new FileWriter(STORE_FILE.toFile())) {
                GSON.toJson(list, writer);
                LOGGER.debug("Saved settings to {}", STORE_FILE);
            } catch (IOException e) {
                LOGGER.error("Failed to save config file!", e);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create config directory!", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void createJSON() {
        try {
            Path configDir = STORE_FILE.getParent();
            if (Files.notExists(configDir)) {
                Files.createDirectories(configDir);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create config directory!", e);
            return;
        }

        if (Files.exists(STORE_FILE)) {
            LOGGER.info("Config file already exists at: {}", STORE_FILE);
            return;
        }

        List<PlayerSettings> defaultSettings = new ArrayList<>();
        defaultSettings.add(new PlayerSettings(ALL_PLAYER_NAME, ALL_PLAYER_UUID));

        try (FileWriter writer = new FileWriter(STORE_FILE.toFile())) {
            GSON.toJson(defaultSettings, writer);
            LOGGER.info("Created default config file at: {}", STORE_FILE);
        } catch (IOException e) {
            LOGGER.error("Failed to create config file!", e);
        }

        // Сбросим кэш для перезагрузки
        cacheLoaded = false;
        loadCache();
    }

    public static void reloadCache() {
        lock.writeLock().lock();
        try {
            cacheLoaded = false;
            cache.clear();
            loadCache();
            LOGGER.info("Cache reloaded");
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==================== Публичные методы доступа ====================

    public static PlayerSettings getSettings(UUID uuid) {
        loadCache();
        lock.readLock().lock();
        try {
            return cache.get(uuid);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static Collection<PlayerSettings> getAllSettings() {
        loadCache();
        lock.readLock().lock();
        try {
            return new ArrayList<>(cache.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void updateSettings(PlayerSettings settings) {
        if (settings == null || settings.getUuid() == null) {
            LOGGER.error("Cannot update null settings or settings with null UUID");
            return;
        }

        loadCache();
        lock.writeLock().lock();
        try {
            cache.put(settings.getUuid(), settings);
            saveCacheToFile();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void addNewPlayer(String name, UUID uuid) {
        if (uuid == null || ALL_PLAYER_UUID.equals(uuid)) return;

        loadCache();
        lock.writeLock().lock();
        try {
            PlayerSettings existing = cache.get(uuid);
            if (existing == null) {
                PlayerSettings newPlayer = new PlayerSettings(name, uuid);
                cache.put(uuid, newPlayer);
                saveCacheToFile();
                LOGGER.info("Added new player: {} with UUID: {}", name, uuid);
            } else if (!existing.getName().equals(name)) {
                existing.setName(name);
                saveCacheToFile();
                LOGGER.debug("Updated player name: {} -> {}", existing.getName(), name);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void removePlayer(UUID uuid) {
        if (uuid == null || ALL_PLAYER_UUID.equals(uuid)) return;

        loadCache();
        lock.writeLock().lock();
        try {
            if (cache.remove(uuid) != null) {
                saveCacheToFile();
                LOGGER.info("Removed player with UUID: {}", uuid);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==================== Работа с блоками ====================

    public static void toggleBlockInList(UUID uuid, Block block) {
        if (block == null) return;

        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            String id = BuiltInRegistries.BLOCK.getKey(block).toString();
            if (settings.isBlockExists(id)) {
                settings.removeBlockElement(id);
            } else {
                settings.addBlockElement(id);
            }
            updateSettings(settings);
        }
    }

    public static void setBlockPlaceEnabled(UUID uuid, String blockId, boolean enabled) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.setBlockPlace(blockId, enabled);
            updateSettings(settings);
        }
    }

    public static void setBlockBreakEnabled(UUID uuid, String blockId, boolean enabled) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.setBlockBreak(blockId, enabled);
            updateSettings(settings);
        }
    }

    public static void toggleBlockPlace(UUID uuid, String blockId) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleBlockPlace(blockId);
            updateSettings(settings);
        }
    }

    public static void toggleBlockBreak(UUID uuid, String blockId) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleBlockBreak(blockId);
            updateSettings(settings);
        }
    }

    public static void toggleGlobalBlockPlace(UUID uuid) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleDisablePlaceBlock();
            updateSettings(settings);
        }
    }

    public static void toggleGlobalBlockBreak(UUID uuid) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleDisableBreakBlock();
            updateSettings(settings);
        }
    }

    public static boolean canPlaceBlock(UUID uuid, String blockId) {
        PlayerSettings settings = getSettings(uuid);
        return settings != null && settings.canPlaceBlock(blockId);
    }

    public static boolean canBreakBlock(UUID uuid, String blockId) {
        PlayerSettings settings = getSettings(uuid);
        return settings != null && settings.canBreakBlock(blockId);
    }

    // ==================== Работа с предметами ====================

    public static void toggleItemInList(UUID uuid, Item item) {
        if (item == null) return;

        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            String id = BuiltInRegistries.ITEM.getKey(item).toString();
            if (settings.isItemExists(id)) {
                settings.removeItemElement(id);
            } else {
                settings.addItemElement(id);
            }
            updateSettings(settings);
        }
    }

    public static void setItemDropEnabled(UUID uuid, String itemId, boolean enabled) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.setItemDropSetting(itemId, enabled);
            updateSettings(settings);
        }
    }

    public static void setItemPickupEnabled(UUID uuid, String itemId, boolean enabled) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.setItemPickupSetting(itemId, enabled);
            updateSettings(settings);
        }
    }

    public static void toggleItemDrop(UUID uuid, String itemId) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleItemDrop(itemId);
            updateSettings(settings);
        }
    }

    public static void toggleItemPickup(UUID uuid, String itemId) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleItemPickup(itemId);
            updateSettings(settings);
        }
    }

    public static void toggleGlobalItemDrop(UUID uuid) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleDisableItemDrop();
            updateSettings(settings);
        }
    }

    public static void toggleGlobalItemPickup(UUID uuid) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleDisableItemPickup();
            updateSettings(settings);
        }
    }

    public static boolean canDropItem(UUID uuid, String itemId) {
        PlayerSettings settings = getSettings(uuid);
        return settings != null && settings.canDropItem(itemId);
    }

    public static boolean canPickupItem(UUID uuid, String itemId) {
        PlayerSettings settings = getSettings(uuid);
        return settings != null && settings.canPickupItem(itemId);
    }

    public static boolean canDropItem(UUID uuid, Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        return canDropItem(uuid, id);
    }

    public static boolean canPickupItem(UUID uuid, Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        return canPickupItem(uuid, id);
    }

    // ==================== Работа с семенами ====================

    public static void toggleSeedInList(UUID uuid, Item seed) {
        if (seed == null) return;

        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            String id = BuiltInRegistries.ITEM.getKey(seed).toString();
            if (settings.isPlantSeedExists(id)) {
                settings.removeSeedElement(seed);
            } else {
                settings.addSeedElement(seed);
            }
            updateSettings(settings);
        }
    }

    public static void toggleSeedPlanting(UUID uuid, String seedId) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleSeedPlant(seedId);
            updateSettings(settings);
        }
    }

    public static void toggleGlobalSeedPlanting(UUID uuid) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleDisablePlantingSeed();
            updateSettings(settings);
        }
    }

    public static boolean canPlantSeed(UUID uuid, String seedId) {
        PlayerSettings settings = getSettings(uuid);
        return settings != null && settings.canPlanSeed(seedId);
    }

    // ==================== Работа с жителями ====================

    public static void addVillagerProfession(UUID uuid, String professionId) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.addVillagerProfession(professionId);
            updateSettings(settings);
        }
    }

    public static void removeVillagerProfession(UUID uuid, String professionId) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.removeVillagerProfession(professionId);
            updateSettings(settings);
        }
    }

    public static void addVillagerProfession(UUID uuid, VillagerProfession profession) {
        if (profession != null) {
            addVillagerProfession(uuid, BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString());
        }
    }

    public static void removeVillagerProfession(UUID uuid, VillagerProfession profession) {
        if (profession != null) {
            removeVillagerProfession(uuid, BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString());
        }
    }

    public static void toggleVillagerTrade(UUID uuid, String professionId) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleVillagerTrade(professionId);
            updateSettings(settings);
        }
    }

    public static void toggleGlobalVillagerTrade(UUID uuid) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleDisableTradingVillager();
            updateSettings(settings);
        }
    }

    public static void toggleGlobalWanderingTraderTrade(UUID uuid) {
        PlayerSettings settings = getSettings(uuid);
        if (settings != null) {
            settings.toggleDisableTradingWanderingTrader();
            updateSettings(settings);
        }
    }

    public static boolean canTradeWithVillager(UUID uuid, String professionId) {
        PlayerSettings settings = getSettings(uuid);
        return settings != null && settings.canTradeWithVillager(professionId);
    }

    public static boolean canTradeWithVillager(UUID uuid, VillagerProfession profession) {
        String id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
        return canTradeWithVillager(uuid, id);
    }

    public static boolean canTradeWithWanderingTrader(UUID uuid) {
        PlayerSettings settings = getSettings(uuid);
        return settings != null && !settings.getDisableTradingWanderingTrader();
    }

    // ==================== Утилиты для GUI ====================

    public static boolean isGlobalPlayer(UUID uuid) {
        return ALL_PLAYER_UUID.equals(uuid);
    }

    public static PlayerSettings getGlobalSettings() {
        return getSettings(ALL_PLAYER_UUID);
    }

}