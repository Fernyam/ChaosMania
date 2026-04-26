package net.fernyam.chaosmania;

import net.fernyam.chaosmania.event.*;
import net.fernyam.chaosmania.gui.MainConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;

@Mod(ChaosManiaMod.MOD_ID)
public class ChaosManiaMod {
    public static final String MOD_ID = "chaosmania";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ChaosManiaMod(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigMod.SPEC);

        // Регистрация экрана конфигурации (для клиента)
        if (Dist.CLIENT.isClient()) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                    (container, screen) -> new MainConfigScreen(screen));
        }

        // События
        NeoForge.EVENT_BUS.addListener(PlantingSeedsEvent::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(AnimalReproductionEvent::onEntityInteract);

        NeoForge.EVENT_BUS.addListener(VillagerTradingEvent::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(VillagerTradingEvent::onPlayerTick);

        NeoForge.EVENT_BUS.addListener(ItemPickupEvent::onItemPickupPre);

        NeoForge.EVENT_BUS.addListener(ItemDropsEvent::onItemToss);

        //NeoForge.EVENT_BUS.addListener(ItemCraftingEvent::onServerAboutToStart);

        NeoForge.EVENT_BUS.addListener(BlockPlaceEvent::onBlockPlace);
        NeoForge.EVENT_BUS.addListener(BlockBreakEvent::onBlockBreak);

        // Регистрация клиентских событий
        modEventBus.addListener(this::onClientSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Общая инициализация для сервера и клиента
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // Клиентская инициализация
        LOGGER.info("ChaosMania клиент инициализирован");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("ChaosMania сервер инициализирован");
    }
}