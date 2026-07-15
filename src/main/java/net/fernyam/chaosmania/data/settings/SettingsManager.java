package net.fernyam.chaosmania.data.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.data.settings.custom.*;
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

public class SettingsManager {
    public static final String ALL_PLAYER_UUID = "00000000-0000-0000-0000-000000000000";
    public static final String ALL_PLAYER_NAME = "§6§l[ВСЕМ]";

    private static final Logger LOGGER = LogManager.getLogger(ChaosManiaMod.MOD_ID);
    private static final Path CONFIG_DIR = FMLPaths.GAMEDIR.get().resolve(
            String.format("config/%s/", ChaosManiaMod.MOD_ID)
    );

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Кэши
    private static final Map<String, BlockSettings> blockCache = new HashMap<>();
    private static final Map<String, ItemSettings> itemCache = new HashMap<>();
    private static final Map<String, SeedSettings> seedCache = new HashMap<>();
    private static final Map<String, VillagerSettings> villagerCache = new HashMap<>();
    private static final Map<String, MobSettings> mobCache = new HashMap<>();
    private static final Map<String, ModSettings> modCache = new HashMap<>();

    private static final Path BLOCKS_PATH = CONFIG_DIR.resolve("blocks.json");
    private static final Path ITEMS_PATH = CONFIG_DIR.resolve("items.json");
    private static final Path SEEDS_PATH = CONFIG_DIR.resolve("seeds.json");
    private static final Path VILLAGERS_PATH = CONFIG_DIR.resolve("villagers.json");
    private static final Path MOBS_PATH = CONFIG_DIR.resolve("mob.json");
    private static final Path MODS_PATH = CONFIG_DIR.resolve("mods.json");

    private static boolean cacheLoaded = false;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // ==================== Загрузка и сохранение ====================

    public static void loadCache() {
        lock.writeLock().lock();
        try {
            if (cacheLoaded) return;

            blockCache.clear();
            itemCache.clear();
            seedCache.clear();
            villagerCache.clear();
            mobCache.clear();
            modCache.clear();

            if (!Files.exists(CONFIG_DIR)) {
                createDefaultConfig();
            }

            loadSettings(BLOCKS_PATH, blockCache, new TypeToken<List<BlockSettings>>() {}.getType());
            loadSettings(ITEMS_PATH, itemCache, new TypeToken<List<ItemSettings>>() {}.getType());
            loadSettings(SEEDS_PATH, seedCache, new TypeToken<List<SeedSettings>>() {}.getType());
            loadSettings(VILLAGERS_PATH, villagerCache, new TypeToken<List<VillagerSettings>>() {}.getType());
            loadSettings(MOBS_PATH, mobCache, new TypeToken<List<MobSettings>>() {}.getType());
            loadSettings(MODS_PATH, modCache, new TypeToken<List<ModSettings>>() {}.getType());

            ensureGlobalSettings();

            cacheLoaded = true;
            LOGGER.info("Loaded all settings");

        } catch (Exception e) {
            LOGGER.error("Failed to load config files!", e);
            createDefaultConfig();
            cacheLoaded = true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static <T extends BaseSettings> void loadSettings(Path file, Map<String, T> cache, Type type) {
        if (!Files.exists(file)) return;

        try {
            String json = Files.readString(file);
            List<T> list = GSON.fromJson(json, type);

            if (list != null) {
                for (T settings : list) {
                    cache.put(settings.getUuidPlayer(), settings);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load: {}", file, e);
        }
    }

    private static <T extends BaseSettings> void saveSettings(Path file, Map<String, T> cache) {
        try {
            if (Files.notExists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }

            List<T> list = new ArrayList<>(cache.values());
            try (FileWriter writer = new FileWriter(file.toFile())) {
                GSON.toJson(list, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save: {}", file, e);
        }
    }

    private static void ensureGlobalSettings() {
        if (!blockCache.containsKey(ALL_PLAYER_UUID)) {
            blockCache.put(ALL_PLAYER_UUID, new BlockSettings(ALL_PLAYER_UUID, ALL_PLAYER_NAME));
        }
        if (!itemCache.containsKey(ALL_PLAYER_UUID)) {
            itemCache.put(ALL_PLAYER_UUID, new ItemSettings(ALL_PLAYER_UUID, ALL_PLAYER_NAME));
        }
        if (!seedCache.containsKey(ALL_PLAYER_UUID)) {
            seedCache.put(ALL_PLAYER_UUID, new SeedSettings(ALL_PLAYER_UUID, ALL_PLAYER_NAME));
        }
        if (!villagerCache.containsKey(ALL_PLAYER_UUID)) {
            villagerCache.put(ALL_PLAYER_UUID, new VillagerSettings(ALL_PLAYER_UUID, ALL_PLAYER_NAME));
        }
        if (!mobCache.containsKey(ALL_PLAYER_UUID)) {
            mobCache.put(ALL_PLAYER_UUID, new MobSettings(ALL_PLAYER_UUID, ALL_PLAYER_NAME));
        }
        if (!modCache.containsKey(ALL_PLAYER_UUID)) {
            modCache.put(ALL_PLAYER_UUID, new ModSettings(ALL_PLAYER_UUID, ALL_PLAYER_NAME));
        }
    }

    public static void createDefaultConfig() {
        try {
            if (Files.notExists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }

            ensureGlobalSettings();
            saveAllSettings();

            LOGGER.info("Created default config at: {}", CONFIG_DIR);
        } catch (IOException e) {
            LOGGER.error("Failed to create default config!", e);
        }
    }

    private static void saveAllSettings() {
        saveSettings(BLOCKS_PATH, blockCache);
        saveSettings(ITEMS_PATH, itemCache);
        saveSettings(SEEDS_PATH, seedCache);
        saveSettings(VILLAGERS_PATH, villagerCache);
        saveSettings(MOBS_PATH, mobCache);
        saveSettings(MODS_PATH, modCache);
    }

    public static void reloadCache() {
        lock.writeLock().lock();
        try {
            cacheLoaded = false;
            loadCache();
            LOGGER.info("Cache reloaded");
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==================== Получение настроек ====================

    public static BlockSettings getBlockSettings(String uuid) {
        loadCache();
        lock.readLock().lock();
        try {
            return blockCache.getOrDefault(uuid, blockCache.get(ALL_PLAYER_UUID));
        } finally {
            lock.readLock().unlock();
        }
    }

    public static ItemSettings getItemSettings(String uuid) {
        loadCache();
        lock.readLock().lock();
        try {
            return itemCache.getOrDefault(uuid, itemCache.get(ALL_PLAYER_UUID));
        } finally {
            lock.readLock().unlock();
        }
    }

    public static SeedSettings getSeedSettings(String uuid) {
        loadCache();
        lock.readLock().lock();
        try {
            return seedCache.getOrDefault(uuid, seedCache.get(ALL_PLAYER_UUID));
        } finally {
            lock.readLock().unlock();
        }
    }

    public static VillagerSettings getVillagerSettings(String uuid) {
        loadCache();
        lock.readLock().lock();
        try {
            return villagerCache.getOrDefault(uuid, villagerCache.get(ALL_PLAYER_UUID));
        } finally {
            lock.readLock().unlock();
        }
    }

    public static MobSettings getMobSettings(String uuid) {
        loadCache();
        lock.readLock().lock();
        try {
            return mobCache.getOrDefault(uuid, mobCache.get(ALL_PLAYER_UUID));
        } finally {
            lock.readLock().unlock();
        }
    }

    public static ModSettings getModSettings(String uuid) {
        loadCache();
        lock.readLock().lock();
        try {
            return modCache.getOrDefault(uuid, modCache.get(ALL_PLAYER_UUID));
        } finally {
            lock.readLock().unlock();
        }
    }

    // ==================== Добавление/удаление игроков ====================

    public static void addNewPlayer(String name, String uuid) {
        if (uuid == null || ALL_PLAYER_UUID.equals(uuid)) return;

        loadCache();
        lock.writeLock().lock();
        try {
            if (!blockCache.containsKey(uuid)) {
                blockCache.put(uuid, new BlockSettings(uuid, name));
            }
            if (!itemCache.containsKey(uuid)) {
                itemCache.put(uuid, new ItemSettings(uuid, name));
            }
            if (!seedCache.containsKey(uuid)) {
                seedCache.put(uuid, new SeedSettings(uuid, name));
            }
            if (!villagerCache.containsKey(uuid)) {
                villagerCache.put(uuid, new VillagerSettings(uuid, name));
            }
            if (!mobCache.containsKey(uuid)) {
                mobCache.put(uuid, new MobSettings(uuid, name));
            }
            if (!modCache.containsKey(uuid)) {
                modCache.put(uuid, new ModSettings(uuid, name));
            }

            saveAllSettings();
            LOGGER.info("Added new player: {} with UUID: {}", name, uuid);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void removePlayer(String uuid) {
        if (uuid == null || ALL_PLAYER_UUID.equals(uuid)) return;

        loadCache();
        lock.writeLock().lock();
        try {
            blockCache.remove(uuid);
            itemCache.remove(uuid);
            seedCache.remove(uuid);
            villagerCache.remove(uuid);
            mobCache.remove(uuid);
            modCache.remove(uuid);

            saveAllSettings();
            LOGGER.info("Removed player with UUID: {}", uuid);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==================== Добавление игроков в конкретные типы ====================

    public static void addBlockPlayer(String uuid, BlockSettings settings) {
        lock.writeLock().lock();
        try {
            blockCache.put(uuid, settings);
            saveSettings(BLOCKS_PATH, blockCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void addItemPlayer(String uuid, ItemSettings settings) {
        lock.writeLock().lock();
        try {
            itemCache.put(uuid, settings);
            saveSettings(ITEMS_PATH, itemCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void addSeedPlayer(String uuid, SeedSettings settings) {
        lock.writeLock().lock();
        try {
            seedCache.put(uuid, settings);
            saveSettings(SEEDS_PATH, seedCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void addVillagerPlayer(String uuid, VillagerSettings settings) {
        lock.writeLock().lock();
        try {
            villagerCache.put(uuid, settings);
            saveSettings(VILLAGERS_PATH, villagerCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void addMobPlayer(String uuid, MobSettings settings) {
        lock.writeLock().lock();
        try {
            mobCache.put(uuid, settings);
            saveSettings(MOBS_PATH, mobCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void addModPlayer(String uuid, ModSettings settings) {
        lock.writeLock().lock();
        try {
            modCache.put(uuid, settings);
            saveSettings(MODS_PATH, modCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==================== Методы для получения списков игроков ====================

    public static Set<String> getAllBlockPlayers() {
        return new HashSet<>(blockCache.keySet());
    }

    public static Set<String> getAllItemPlayers() {
        return new HashSet<>(itemCache.keySet());
    }

    public static Set<String> getAllSeedPlayers() {
        return new HashSet<>(seedCache.keySet());
    }

    public static Set<String> getAllVillagerPlayers() {
        return new HashSet<>(villagerCache.keySet());
    }

    public static Set<String> getAllMobPlayers() {
        return new HashSet<>(mobCache.keySet());
    }

    public static Set<String> getAllModPlayers() {
        return new HashSet<>(modCache.keySet());
    }

    // ==================== Методы сохранения для конкретных типов ====================

    public static void saveBlockSettings(String uuid) {
        BlockSettings settings = blockCache.get(uuid);
        if (settings != null) {
            saveSettings(BLOCKS_PATH, blockCache);
        }
    }

    public static void saveItemSettings(String uuid) {
        ItemSettings settings = itemCache.get(uuid);
        if (settings != null) {
            saveSettings(ITEMS_PATH, itemCache);
        }
    }

    public static void saveSeedSettings(String uuid) {
        SeedSettings settings = seedCache.get(uuid);
        if (settings != null) {
            saveSettings(SEEDS_PATH, seedCache);
        }
    }

    public static void saveVillagerSettings(String uuid) {
        VillagerSettings settings = villagerCache.get(uuid);
        if (settings != null) {
            saveSettings(VILLAGERS_PATH, villagerCache);
        }
    }

    public static void saveMobSettings(String uuid) {
        MobSettings settings = mobCache.get(uuid);
        if (settings != null) {
            saveSettings(MOBS_PATH, mobCache);
        }
    }

    public static void saveModSettings(String uuid) {
        ModSettings settings = modCache.get(uuid);
        if (settings != null) {
            saveSettings(MODS_PATH, modCache);
        }
    }
}