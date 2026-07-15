package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.util.PermissionHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


public class MobClickEvent
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickOnMobOne(PlayerInteractEvent.EntityInteractSpecific event) {
//        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!PermissionHelper.canRightClickMob(player, event.getTarget().getType())) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickOnMobTwo(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!PermissionHelper.canRightClickMob(player, event.getTarget().getType())) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
    }

    public static void onLeftClickOnMob(AttackEntityEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!PermissionHelper.canLeftClickMob(player, event.getTarget().getType())) {
                event.setCanceled(true);
            }
        }
    }
}
