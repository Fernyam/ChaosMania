package net.fernyam.chaosmania.event.blockEvent;

import net.fernyam.chaosmania.util.PermissionHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class BlockClickEvent
{
    public static void RightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!PermissionHelper.canRightClickBlock(player, event.getLevel().getBlockState(event.getPos()).getBlock())) {
                event.setCanceled(true);
            }
        }
    }

    public static void LeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!PermissionHelper.canLeftClickBlock(player, event.getLevel().getBlockState(event.getPos()).getBlock())) {
                event.setCanceled(true);
            }
        }
    }

}
