package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.util.PermissionHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class VillagerTradingEvent {
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;



        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getTarget() instanceof Villager villager) {
                if (PermissionHelper.canTradeWithVillager(player, villager.getVillagerData().getProfession())) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                }
            }

            if (event.getTarget() instanceof WanderingTrader) {
                if (PermissionHelper.canTradeWithWanderingTrader(player)) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                }
            }
        }
    }
}