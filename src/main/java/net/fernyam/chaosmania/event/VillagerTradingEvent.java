package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ConfigMod;
import net.fernyam.chaosmania.ChaosManiaMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class VillagerTradingEvent {

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!ConfigMod.DISABLE_VILLAGER_TRADING.get()) {
            return;
        }

        if (event.getTarget() instanceof Villager) {
            event.setCanceled(true);
            if (!event.getLevel().isClientSide) {
                event.getEntity().sendSystemMessage(
                        Component.literal("§cТорговля с жителями запрещена на этом сервере!")
                );
            }
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!ConfigMod.DISABLE_VILLAGER_TRADING.get()) {
            return;
        }

        // Если игрок в GUI торговли
        if (event.getEntity().containerMenu instanceof net.minecraft.world.inventory.MerchantMenu) {
            event.getEntity().closeContainer();
            if (!event.getEntity().level().isClientSide) {
                event.getEntity().sendSystemMessage(
                        Component.literal("§cТорговля с жителями запрещена!")
                );
            }
        }
    }
}