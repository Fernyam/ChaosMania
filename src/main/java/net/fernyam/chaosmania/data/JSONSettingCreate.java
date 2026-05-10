package net.fernyam.chaosmania.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fernyam.chaosmania.ChaosManiaMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JSONSettingCreate {
    public static final String All_UUID_PLAYER = "00000000-0000-0000-0000-000000000000";

    private static final Logger LOGGER = LogManager.getLogger(ChaosManiaMod.MOD_ID);
    private static final Path STORE_FILE = FMLPaths.GAMEDIR.get().resolve(
            String.format("config/%s/player_setting.json", ChaosManiaMod.MOD_ID)
    );

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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

        PlayerSettings defaultSettings = new PlayerSettings(
                "ALL_PLAYER",
                All_UUID_PLAYER,
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );

        settings.add(defaultSettings);
        return settings;
    }

    public static List<PlayerSettings> loadSettings() {
        if (!Files.exists(STORE_FILE)) {
            createJSON();
        }

        try {
            String json = Files.readString(STORE_FILE);
            PlayerSettings[] settingsArray = GSON.fromJson(json, PlayerSettings[].class);
            return new ArrayList<>(Arrays.asList(settingsArray));
        } catch (IOException e) {
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
            PlayerSettings newPlayer = new PlayerSettings(
                    name,
                    uuid,
                    false,
                    false,
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            allSettings.add(newPlayer);
            saveSettings(allSettings);
            LOGGER.info("Added new player: {} with UUID: {}", name, uuid);
        }
    }

    public static void ElementToDontBreakBlock(UUID uuid, Block block) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings playerSettings = allSettings.stream()
                .filter(settings -> settings.getUuidPlayer().equals(uuid.toString()))
                .findFirst()
                .orElse(null);

        if (playerSettings != null) {
            if(playerSettings.getDontBreakBlockList().contains(BuiltInRegistries.BLOCK.getKey(block).toString()))
            {
                playerSettings.RemoveElementToDontBreakBlockList(block);
            }
            else
            {
                playerSettings.AddElementToDontBreakBlockList(block);
            }
            saveSettings(allSettings);
        }
    }

    public static void ElementToDontPlaceBlock(UUID uuid, Block block) {
        List<PlayerSettings> allSettings = loadSettings();

        PlayerSettings playerSettings = allSettings.stream()
                .filter(settings -> settings.getUuidPlayer().equals(uuid.toString()))
                .findFirst()
                .orElse(null);

        if (playerSettings != null) {
            if(playerSettings.getDontPlaceBlockList().contains(BuiltInRegistries.BLOCK.getKey(block).toString()))
            {
                playerSettings.RemoveElementToDontPlaceBlockList(block);
            }
            else
            {
                playerSettings.AddElementToDontPlaceBlockList(block);
            }
            saveSettings(allSettings);
        }
    }

    public static boolean IsElementInDontPlaceBlockList(UUID uuid, Block block)
    {
        List<PlayerSettings> allSettings = loadSettings();
        PlayerSettings playerSettings = allSettings.stream()
                .filter(settings -> settings.getUuidPlayer().equals(uuid.toString()))
                .findFirst()
                .orElse(null);
        if (playerSettings != null)
        {
            if(playerSettings.getDontPlaceBlockList().contains(BuiltInRegistries.BLOCK.getKey(block).toString()))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean IsElementInDontBreakBlockList(UUID uuid, Block block)
    {
        List<PlayerSettings> allSettings = loadSettings();
        PlayerSettings playerSettings = allSettings.stream()
                .filter(settings -> settings.getUuidPlayer().equals(uuid.toString()))
                .findFirst()
                .orElse(null);

        if (playerSettings != null)
        {
            if(playerSettings.getDontBreakBlockList().contains(BuiltInRegistries.BLOCK.getKey(block).toString()))
            {
                return true;
            }
        }

        return false;
    }

}