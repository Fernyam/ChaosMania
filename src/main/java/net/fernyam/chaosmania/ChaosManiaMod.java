package net.fernyam.chaosmania;

import net.fernyam.chaosmania.client.ClientInputHandler;
import net.fernyam.chaosmania.client.KeyBindings;
import net.fernyam.chaosmania.data.JSONSettingManager;
import net.fernyam.chaosmania.data.settings.SettingsManager;
import net.fernyam.chaosmania.event.blockEvent.BlockBreakEvent;
import net.fernyam.chaosmania.event.blockEvent.BlockPlaceEvent;
import net.fernyam.chaosmania.event.itemEvent.ItemDropsEvent;
import net.fernyam.chaosmania.event.itemEvent.ItemPickupEvent;
import net.fernyam.chaosmania.event.PlantingSeedsEvent;
import net.fernyam.chaosmania.event.VillagerTradingEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.SubscribeEvent;  // ← Важно!
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;

@Mod(ChaosManiaMod.MOD_ID)
public class ChaosManiaMod {
    public static final String MOD_ID = "chaosmania";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ChaosManiaMod(IEventBus modEventBus, ModContainer modContainer) {
        // Регистрируем THIS класс для событий на NeoForge.EVENT_BUS
        NeoForge.EVENT_BUS.register(this);  // ← Теперь работает, так как есть @SubscribeEvent методы

        // Клиент
        modEventBus.addListener(KeyBindings::registerBindings);
        NeoForge.EVENT_BUS.addListener(ClientInputHandler::onClientTick);

        // События
        NeoForge.EVENT_BUS.addListener(PlantingSeedsEvent::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(VillagerTradingEvent::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(ItemPickupEvent::onItemPickupPre);
        NeoForge.EVENT_BUS.addListener(ItemDropsEvent::onItemToss);
        NeoForge.EVENT_BUS.addListener(BlockPlaceEvent::onBlockPlace);
        NeoForge.EVENT_BUS.addListener(BlockBreakEvent::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(this::onEntityJoinLevel);

        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        JSONSettingManager.getAllSettings();
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("ChaosMania клиент инициализирован");
    }

    // ← Добавьте аннотацию @SubscribeEvent
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        SettingsManager.loadCache();
    }

    // ← Добавьте аннотацию @SubscribeEvent
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

        JSONSettingManager.createJSON();

        Player player = event.getEntity();
        String uuid = player.getStringUUID();
        String name = player.getName().getString();

        SettingsManager.addNewPlayer(name, uuid);
    }

    // ← Добавьте аннотацию @SubscribeEvent
    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            JSONSettingManager.addNewPlayer(player.getName().getString(), player.getUUID().toString());
        }
    }
}