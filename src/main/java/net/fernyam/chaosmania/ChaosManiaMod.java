package net.fernyam.chaosmania;

import net.fernyam.chaosmania.client.ClientInputHandler;
import net.fernyam.chaosmania.client.KeyBindings;
import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.event.*;
import net.fernyam.chaosmania.event.blockEvent.BlockBreakEvent;
import net.fernyam.chaosmania.event.blockEvent.BlockPlaceEvent;
import net.fernyam.chaosmania.event.itemEvent.ItemDropsEvent;
import net.fernyam.chaosmania.event.itemEvent.ItemPickupEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
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

        //Клиент
        modEventBus.addListener(KeyBindings::registerBindings);
        NeoForge.EVENT_BUS.addListener(ClientInputHandler::onClientTick);

        // События
        NeoForge.EVENT_BUS.addListener(PlantingSeedsEvent::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(AnimalReproductionEvent::onEntityInteract);

        NeoForge.EVENT_BUS.addListener(VillagerTradingEvent::onEntityInteract);

        NeoForge.EVENT_BUS.addListener(ItemPickupEvent::onItemPickupPre);

        NeoForge.EVENT_BUS.addListener(ItemDropsEvent::onItemToss);

        //NeoForge.EVENT_BUS.addListener(ItemCraftingEvent::onServerAboutToStart);

        NeoForge.EVENT_BUS.addListener(BlockPlaceEvent::onBlockPlace);
//        NeoForge.EVENT_BUS.addListener(BlockPlaceEvent::onBlockPlace);
        NeoForge.EVENT_BUS.addListener(BlockBreakEvent::onBlockBreak);

        NeoForge.EVENT_BUS.addListener(this::D);
        NeoForge.EVENT_BUS.addListener(this::onEntityJoinLevel);

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



    public void D(PlayerEvent.PlayerLoggedInEvent event)
    {
        JSONSettingCreate.createJSON();
    }

//    @SubscribeEvent
//    public void onPlayerJoinWorld(EntityJoinLevelEvent event) {
//        // Проверяем, что это клиент, сущность - игрок, и мы уже в игре
//        if (!event.getLevel().isClientSide()) {
//            return;
//        }
//
//        if (!(event.getEntity() instanceof Player player)) {
//            return;
//        }
//
//        // Проверяем, что это локальный игрок (не какой-то другой игрок на сервере)
//        if (player != Minecraft.getInstance().player) {
//            return;
//        }
//
//        // Открываем GUI только один раз (опционально)
//
//
//            // Откладываем открытие на 1 тик, чтобы мир полностью загрузился
//            Minecraft.getInstance().execute(() -> {
//                Minecraft.getInstance().setScreen(new AllBlocksScreen());
//            });
//
//    }

    public void onEntityJoinLevel(EntityJoinLevelEvent event) {

        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            JSONSettingCreate.addNewPlayer(
                    player.getName().getString(),
                    player.getUUID()
            );
        }
    }



}