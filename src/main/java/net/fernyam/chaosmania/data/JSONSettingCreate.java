//package net.fernyam.chaosmania.data;
//
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import net.fernyam.chaosmania.ChaosManiaMod;
//import net.fernyam.chaosmania.data.settings.custom.BlockSettings;
//import net.neoforged.fml.loading.FMLPaths;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.nio.file.Path;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
//public class JSONSettingCreate
//{
//    public static final String ALL_PLAYER_UUID = "00000000-0000-0000-0000-000000000000";
//    private static final String ALL_PLAYER_NAME = "§6§l[ВСЕМ]";
//
//    private static final Logger LOGGER = LogManager.getLogger(ChaosManiaMod.MOD_ID);
//
//    private static final Path CONFIG_DIR = FMLPaths.GAMEDIR.get().resolve(
//            String.format("config/%s/", ChaosManiaMod.MOD_ID)
//    );
//
//
//    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//
//    private static final Map<String, PlayerSettings> cache = new HashMap<>();
//    private static boolean cacheLoaded = false;
//    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//
//    private static final BlockSettings blockManager = new BlockSettings();
//}