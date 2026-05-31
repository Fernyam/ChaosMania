package net.fernyam.chaosmania.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fernyam.chaosmania.ChaosManiaMod;
import net.minecraft.core.registries.BuiltInRegistries;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JSONSettingCreate {
    public static final String ALL_UUID_PLAYER = "00000000-0000-0000-0000-000000000000";

    private static final Logger LOGGER = LogManager.getLogger(ChaosManiaMod.MOD_ID);
    private static final Path STORE_FILE = FMLPaths.GAMEDIR.get().resolve(
            String.format("config/%s/player_setting.json", ChaosManiaMod.MOD_ID)
    );

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type SETTINGS_TYPE = new TypeToken<ArrayList<PlayerSettings>>(){}.getType();

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

        List<PlayerSettings> defaultSettings = getDefaultSettings();

        try (FileWriter writer = new FileWriter(STORE_FILE.toFile())) {
            GSON.toJson(defaultSettings, writer);
            LOGGER.info("Created default config file at: {}", STORE_FILE);
        } catch (IOException e) {
            LOGGER.error("Failed to create config file!", e);
        }
    }

    private static List<PlayerSettings> getDefaultSettings() {
        List<PlayerSettings> settings = new ArrayList<>();
        PlayerSettings defaultSettings = new PlayerSettings("ALL_PLAYER", ALL_UUID_PLAYER);
        settings.add(defaultSettings);
        return settings;
    }

    public static List<PlayerSettings> loadSettings() {
        if (!Files.exists(STORE_FILE)) {
            createJSON();
        }

        try {
            String json = Files.readString(STORE_FILE);
            List<PlayerSettings> settings = GSON.fromJson(json, SETTINGS_TYPE);

            // Инициализация списков, если они null (для совместимости со старыми файлами)
            for (PlayerSettings setting : settings) {
                if (setting.getBlockSettings() == null) {
                    java.lang.reflect.Field field = PlayerSettings.class.getDeclaredField("blockSettings");
                    field.setAccessible(true);
                    field.set(setting, new ArrayList<>());
                }
                if (setting.getItemSettings() == null) {
                    java.lang.reflect.Field field = PlayerSettings.class.getDeclaredField("itemSettings");
                    field.setAccessible(true);
                    field.set(setting, new ArrayList<>());
                }
            }

            return settings;
        } catch (Exception e) {
            LOGGER.error("Failed to load config file!", e);
            return new ArrayList<>();
        }
    }

    public static void saveSettings(List<PlayerSettings> settings) {
        try (FileWriter writer = new FileWriter(STORE_FILE.toFile())) {
            GSON.toJson(settings, writer);
            LOGGER.info("Saved config to: {}", STORE_FILE);
        } catch (IOException e) {
            LOGGER.error("Failed to save config file!", e);
        }
    }

    public static void addNewPlayer(String name, UUID uuid) {
        List<PlayerSettings> allSettings = loadSettings();

        boolean exists = allSettings.stream()
                .anyMatch(settings -> settings.getUuidPlayer().equals(uuid.toString()));

        if (!exists) {
            PlayerSettings newPlayer = new PlayerSettings(name, uuid);
            allSettings.add(newPlayer);
            saveSettings(allSettings);
            LOGGER.info("Added new player: {} with UUID: {}", name, uuid);
        } else {
            // Обновляем имя, если оно изменилось
            PlayerSettings existing = allSettings.stream()
                    .filter(settings -> settings.getUuidPlayer().equals(uuid.toString()))
                    .findFirst()
                    .orElse(null);
            if (existing != null && !existing.getName().equals(name)) {
                existing.setName(name);
                saveSettings(allSettings);
            }
        }
    }

    public static PlayerSettings GetPlayerSettingsOfUUID(UUID uuid) {
        List<PlayerSettings> allSettings = loadSettings();

        return allSettings.stream()
                .filter(settings -> settings.getUuidPlayer().equals(uuid.toString()))
                .findFirst()
                .orElse(null);
    }

    public static PlayerSettings GetPlayerSettingsOfUUIDAndListPlayerSettings(UUID uuid , List<PlayerSettings> allSettings) {

        return allSettings.stream()
                .filter(settings -> settings.getUuidPlayer().equals(uuid.toString()))
                .findFirst()
                .orElse(null);
    }


    // ==================== Работа с блоками ====================

    public static void ElementToSettingBlock(UUID uuid, Block block) {
        List<PlayerSettings> allSettings = loadSettings();



        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            String id = BuiltInRegistries.BLOCK.getKey(block).toString();
            if (settings.isBlockExists(id)) {
                settings.removeBlockElement(block);
            } else {
                settings.addBlockElement(block);
            }
            saveSettings(allSettings);
        }
    }

    public static void SwitchDisablePlaceBlock(UUID uuid, String idBlock) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleBlockPlace(idBlock);
            saveSettings(allSettings);
        }
    }

    public static void SwitchDisableBreakBlock(UUID uuid, String idBlock) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleBlockBreak(idBlock);
            saveSettings(allSettings);
        }
    }

    public static void SwitchGlobalDisablePlaceBlock(UUID uuid) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleDisablePlaceBlock();
            saveSettings(allSettings);
        }
    }

    public static void SwitchGlobalDisableBreakBlock(UUID uuid) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleDisableBreakBlock();
            saveSettings(allSettings);
        }
    }

    public static boolean canPlaceBlock(UUID uuid, String idBlock) {
        PlayerSettings settings = GetPlayerSettingsOfUUID(uuid);
        return settings != null && settings.canPlaceBlock(idBlock);
    }

    public static boolean canBreakBlock(UUID uuid, String idBlock) {
        PlayerSettings settings = GetPlayerSettingsOfUUID(uuid);
        return settings != null && settings.canBreakBlock(idBlock);
    }

    // ==================== Работа с предметами ====================


    public static void ElementToSettingItem(UUID uuid, Item item) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            String id = BuiltInRegistries.ITEM.getKey(item).toString();
            if (settings.isItemExists(id)) {
                settings.removeItemElement(item);
            } else {
                settings.addItemElement(item);
            }
            saveSettings(allSettings);
        }
    }

    public static void SwitchDisableItemDrop(UUID uuid, String idItem) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleItemDrop(idItem);
            saveSettings(allSettings);
        }
    }

    public static void SwitchDisableItemPickup(UUID uuid, String idItem) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleItemPickup(idItem);
            saveSettings(allSettings);
        }
    }

    public static void SwitchGlobalDisableItemDrop(UUID uuid) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleDisableItemDrop();
            saveSettings(allSettings);
        }
    }

    public static void SwitchGlobalDisableItemPickup(UUID uuid) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleDisableItemPickup();
            saveSettings(allSettings);
        }
    }

    public static boolean canDropItem(UUID uuid, String idItem) {
        PlayerSettings settings = GetPlayerSettingsOfUUID(uuid);
        return settings != null && settings.canDropItem(idItem);
    }

    public static boolean canPickupItem(UUID uuid, String idItem) {
        PlayerSettings settings = GetPlayerSettingsOfUUID(uuid);
        return settings != null && settings.canPickupItem(idItem);
    }

    public static boolean canDropItem(UUID uuid, Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        return canDropItem(uuid, id);
    }

    public static boolean canPickupItem(UUID uuid, Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        return canPickupItem(uuid, id);
    }

    //=================================== Семена ==========================

    public static void ElementToSettingSeed(UUID uuid, Item item) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            String id = BuiltInRegistries.ITEM.getKey(item).toString();
            if (settings.isPlantSeedExists(id)) {
                settings.removeSeedElement(item);
            } else {
                settings.addSeedElement(item);
            }
            saveSettings(allSettings);
        }
    }

    public static void SwitchDisableSeedPlan(UUID uuid, String idItem) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleSeedPlan(idItem);
            saveSettings(allSettings);
        }
    }

    public static void SwitchGlobalDisablePlanSeed(UUID uuid) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid , allSettings);
        if (settings != null) {
            settings.toggleDisablePlantingSeed();
            saveSettings(allSettings);
        }
    }


// ============================= Жители ====================================


    public static void SwitchGlobalDisableWanderingTraderTrade(UUID uuid) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid, allSettings);
        if (settings != null) {
            settings.toggleDisableTradingWanderingTrader();
            saveSettings(allSettings);
        }
    }

    // Глобальный переключатель запрета торговли
    public static void SwitchGlobalDisableVillagerTrade(UUID uuid) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid, allSettings);
        if (settings != null) {
            settings.toggleDisableTradingVillager();
            saveSettings(allSettings);
        }
    }

    // Переключатель для конкретной профессии
    public static void SwitchDisableVillagerTrade(UUID uuid, String professionId) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid, allSettings);
        if (settings != null) {
            settings.toggleVillagerTrade(professionId);
            saveSettings(allSettings);
        }
    }

    // Добавить профессию жителя (из AllVillagerScreen)
    public static void AddVillagerProfession(UUID uuid, String professionId) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid, allSettings);
        if (settings != null) {
            settings.addVillagerProfession(professionId);
            saveSettings(allSettings);
        }
    }

    // Удалить профессию жителя (из AllVillagerScreen)
    public static void RemoveVillagerProfession(UUID uuid, String professionId) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings settings = GetPlayerSettingsOfUUIDAndListPlayerSettings(uuid, allSettings);
        if (settings != null) {
            settings.removeVillagerProfession(professionId);
            saveSettings(allSettings);
        }
    }

    // Проверить, может ли игрок торговать с профессией
    public static boolean canTradeWithVillager(UUID uuid, String professionId) {
        PlayerSettings settings = GetPlayerSettingsOfUUID(uuid);
        return settings != null && settings.canTradeWithVillager(professionId);
    }


}