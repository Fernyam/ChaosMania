package net.fernyam.chaosmania.data.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.data.settings.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
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
    private static final Map<String, AnimalSettings> animalCache = new HashMap<>();
    private static final Map<String, ModSettings> modCache = new HashMap<>();

    private static final Path BLOCKS_PATH = CONFIG_DIR.resolve("blocks.json");
    private static final Path ITEMS_PATH = CONFIG_DIR.resolve("items.json");
    private static final Path SEEDS_PATH = CONFIG_DIR.resolve("seeds.json");
    private static final Path VILLAGERS_PATH = CONFIG_DIR.resolve("villagers.json");
    private static final Path ANIMALS_PATH = CONFIG_DIR.resolve("animals.json");
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
            animalCache.clear();
            modCache.clear();

            if (!Files.exists(CONFIG_DIR)) {
                createDefaultConfig();
            }

            // Загружаем все типы настроек
            loadSettings(BLOCKS_PATH, blockCache, new TypeToken<List<BlockSettings>>() {}.getType());
            loadSettings(ITEMS_PATH, itemCache, new TypeToken<List<ItemSettings>>() {}.getType());
            loadSettings(SEEDS_PATH, seedCache, new TypeToken<List<SeedSettings>>() {}.getType());
            loadSettings(VILLAGERS_PATH, villagerCache, new TypeToken<List<VillagerSettings>>() {}.getType());
            loadSettings(ANIMALS_PATH, animalCache, new TypeToken<List<AnimalSettings>>() {}.getType());
            loadSettings(MODS_PATH, modCache, new TypeToken<List<ModSettings>>() {}.getType());

            // Убеждаемся, что глобальные настройки существуют
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
        if (!animalCache.containsKey(ALL_PLAYER_UUID)) {
            animalCache.put(ALL_PLAYER_UUID, new AnimalSettings(ALL_PLAYER_UUID, ALL_PLAYER_NAME));
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
        saveSettings(ANIMALS_PATH, animalCache);
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

    public static AnimalSettings getAnimalSettings(String uuid) {
        loadCache();
        lock.readLock().lock();
        try {
            return animalCache.getOrDefault(uuid, animalCache.get(ALL_PLAYER_UUID));
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
            if (!animalCache.containsKey(uuid)) {
                animalCache.put(uuid, new AnimalSettings(uuid, name));
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
            animalCache.remove(uuid);
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

    public static void addAnimalPlayer(String uuid, AnimalSettings settings) {
        lock.writeLock().lock();
        try {
            animalCache.put(uuid, settings);
            saveSettings(ANIMALS_PATH, animalCache);
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

    public static Set<String> getAllAnimalPlayers() {
        return new HashSet<>(animalCache.keySet());
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

    public static void saveAnimalSettings(String uuid) {
        AnimalSettings settings = animalCache.get(uuid);
        if (settings != null) {
            saveSettings(ANIMALS_PATH, animalCache);
        }
    }

    public static void saveModSettings(String uuid) {
        ModSettings settings = modCache.get(uuid);
        if (settings != null) {
            saveSettings(MODS_PATH, modCache);
        }
    }


    // ==================== Методы для блоков ====================

    public static void toggleBlockInList(String uuid, Block block) {
        if (block == null || uuid == null) return;
        String id = BuiltInRegistries.BLOCK.getKey(block).toString();

        BlockSettings settings = getBlockSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            if (settings.isBlockExists(id)) {
                settings.removeBlock(id);
            } else {
                settings.addBlock(id);
            }
            saveSettings(BLOCKS_PATH, blockCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleBlockPlace(String uuid, String blockId) {
        BlockSettings settings = getBlockSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleBlockPlace(blockId);
            saveSettings(BLOCKS_PATH, blockCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleBlockBreak(String uuid, String blockId) {
        BlockSettings settings = getBlockSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleBlockBreak(blockId);
            saveSettings(BLOCKS_PATH, blockCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalBlockPlace(String uuid) {
        BlockSettings settings = getBlockSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalPlaceBlock();
            saveSettings(BLOCKS_PATH, blockCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalBlockBreak(String uuid) {
        BlockSettings settings = getBlockSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalBreakBlock();
            saveSettings(BLOCKS_PATH, blockCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static boolean canPlaceBlock(String uuid, String blockId) {
        BlockSettings settings = getBlockSettings(uuid);
        return settings != null && settings.canPlaceBlock(blockId);
    }

    public static boolean canBreakBlock(String uuid, String blockId) {
        BlockSettings settings = getBlockSettings(uuid);
        return settings != null && settings.canBreakBlock(blockId);
    }

    // ==================== Методы для предметов ====================

    public static void toggleItemInList(String uuid, Item item) {
        if (item == null || uuid == null) return;
        String id = BuiltInRegistries.ITEM.getKey(item).toString();

        ItemSettings settings = getItemSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            if (settings.isItemExists(id)) {
                settings.removeItem(id);
            } else {
                settings.addItem(id);
            }
            saveSettings(ITEMS_PATH, itemCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleItemDrop(String uuid, String itemId) {
        ItemSettings settings = getItemSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleItemDrop(itemId);
            saveSettings(ITEMS_PATH, itemCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleItemPickup(String uuid, String itemId) {
        ItemSettings settings = getItemSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleItemPickup(itemId);
            saveSettings(ITEMS_PATH, itemCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalItemDrop(String uuid) {
        ItemSettings settings = getItemSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalDropItem();
            saveSettings(ITEMS_PATH, itemCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalItemPickup(String uuid) {
        ItemSettings settings = getItemSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalPickupItem();
            saveSettings(ITEMS_PATH, itemCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static boolean canDropItem(String uuid, String itemId) {
        ItemSettings settings = getItemSettings(uuid);
        return settings != null && settings.canDropItem(itemId);
    }

    public static boolean canPickupItem(String uuid, String itemId) {
        ItemSettings settings = getItemSettings(uuid);
        return settings != null && settings.canPickupItem(itemId);
    }

    // ==================== Методы для семян ====================

    public static void toggleSeedInList(String uuid, Item seed) {
        if (seed == null || uuid == null) return;
        String id = BuiltInRegistries.ITEM.getKey(seed).toString();

        SeedSettings settings = getSeedSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            if (settings.isSeedExists(id)) {
                settings.removeSeed(id);
            } else {
                settings.addSeed(id);
            }
            saveSettings(SEEDS_PATH, seedCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleSeedPlanting(String uuid, String seedId) {
        SeedSettings settings = getSeedSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleSeedPlant(seedId);
            saveSettings(SEEDS_PATH, seedCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalSeedPlanting(String uuid) {
        SeedSettings settings = getSeedSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalPlantSeed();
            saveSettings(SEEDS_PATH, seedCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static boolean canPlantSeed(String uuid, String seedId) {
        SeedSettings settings = getSeedSettings(uuid);
        return settings != null && settings.canPlantSeed(seedId);
    }

    // ==================== Методы для жителей ====================

    public static void toggleVillagerInList(String uuid, String professionId) {
        if (professionId == null || uuid == null) return;

        VillagerSettings settings = getVillagerSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            if (settings.isProfessionExists(professionId)) {
                settings.removeProfession(professionId);
            } else {
                settings.addProfession(professionId);
            }
            saveSettings(VILLAGERS_PATH, villagerCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleVillagerTrade(String uuid, String professionId) {
        VillagerSettings settings = getVillagerSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleProfessionTrade(professionId);
            saveSettings(VILLAGERS_PATH, villagerCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalVillagerTrade(String uuid) {
        VillagerSettings settings = getVillagerSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalTradeVillager();
            saveSettings(VILLAGERS_PATH, villagerCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalWanderingTraderTrade(String uuid) {
        VillagerSettings settings = getVillagerSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalTradeWanderingTrader();
            saveSettings(VILLAGERS_PATH, villagerCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static boolean canTradeWithVillager(String uuid, String professionId) {
        VillagerSettings settings = getVillagerSettings(uuid);
        return settings != null && settings.canTradeWithVillager(professionId);
    }

    // ==================== Методы для животных ====================

    public static void toggleAnimalInList(String uuid, EntityType<?> entityType) {
        if (entityType == null || uuid == null) return;
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();

        AnimalSettings settings = getAnimalSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            if (settings.isAnimalExists(id)) {
                settings.removeAnimal(id);
            } else {
                settings.addAnimal(id);
            }
            saveSettings(ANIMALS_PATH, animalCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleAnimalBreed(String uuid, String animalId) {
        AnimalSettings settings = getAnimalSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleAnimalBreed(animalId);
            saveSettings(ANIMALS_PATH, animalCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleAnimalSpawn(String uuid, String animalId) {
        AnimalSettings settings = getAnimalSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleAnimalSpawn(animalId);
            saveSettings(ANIMALS_PATH, animalCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalAnimalBreed(String uuid) {
        AnimalSettings settings = getAnimalSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalBreedAnimal();
            saveSettings(ANIMALS_PATH, animalCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalAnimalSpawn(String uuid) {
        AnimalSettings settings = getAnimalSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalSpawnAnimal();
            saveSettings(ANIMALS_PATH, animalCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static boolean canBreedAnimal(String uuid, String animalId) {
        AnimalSettings settings = getAnimalSettings(uuid);
        return settings != null && settings.canBreedAnimal(animalId);
    }

    public static boolean canSpawnAnimal(String uuid, String animalId) {
        AnimalSettings settings = getAnimalSettings(uuid);
        return settings != null && settings.canSpawnAnimal(animalId);
    }

    // ==================== Методы для модов ====================

    public static void toggleModInList(String uuid, String modId) {
        if (modId == null || uuid == null) return;

        ModSettings settings = getModSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            if (settings.isModExists(modId)) {
                settings.removeMod(modId);
            } else {
                settings.addMod(modId);
            }
            saveSettings(MODS_PATH, modCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleModLoad(String uuid, String modId) {
        ModSettings settings = getModSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleModLoad(modId);
            saveSettings(MODS_PATH, modCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void toggleGlobalModLoad(String uuid) {
        ModSettings settings = getModSettings(uuid);
        if (settings == null) return;

        lock.writeLock().lock();
        try {
            settings.toggleGlobalLoadMod();
            saveSettings(MODS_PATH, modCache);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static boolean canLoadMod(String uuid, String modId) {
        ModSettings settings = getModSettings(uuid);
        return settings != null && settings.canLoadMod(modId);
    }
}