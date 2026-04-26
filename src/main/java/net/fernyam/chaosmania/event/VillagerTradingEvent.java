package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class VillagerTradingEvent {

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (ConfigMod.DISABLE_VILLAGER_TRADING.get())
        {
            if (event.getTarget() instanceof Villager)
            {
                event.setCanceled(true);
            }
        }

        if(ConfigMod.DISABLE_WANDER_TRADING.get())
        {
            if (event.getTarget() instanceof WanderingTrader)
            {
                event.setCanceled(true);
            }
        }


    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!ConfigMod.DISABLE_VILLAGER_TRADING.get() || !ConfigMod.DISABLE_WANDER_TRADING.get()) {
            return;
        }

        // Если игрок в GUI торговли
        if (event.getEntity().containerMenu instanceof net.minecraft.world.inventory.MerchantMenu)
        {
            event.getEntity().closeContainer();
        }
    }
}