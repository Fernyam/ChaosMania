package net.fernyam.chaosmania.event.blockEvent;

import net.fernyam.chaosmania.util.PermissionHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.level.BlockEvent;

public class BlockPlaceEvent {
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!PermissionHelper.canPlaceBlock(player, event.getPlacedBlock().getBlock())) {
                event.setCanceled(true);
            }
        }
    }
}