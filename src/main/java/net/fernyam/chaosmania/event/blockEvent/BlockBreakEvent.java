package net.fernyam.chaosmania.event.blockEvent;

import net.fernyam.chaosmania.util.PermissionHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.level.BlockEvent;

public class BlockBreakEvent {
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getPlayer() instanceof ServerPlayer player) {
            if (!PermissionHelper.canBreakBlock(player, event.getState().getBlock())) {
                event.setCanceled(true);
            }
        }
    }
}