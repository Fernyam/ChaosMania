package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.util.PermissionHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class PlantingSeedsEvent {
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            // Проверяем, что кликаем по грядке
            if (!event.getLevel().getBlockState(event.getPos()).is(Blocks.FARMLAND)) return;

            if (!PermissionHelper.canPlantSeed(player, event.getItemStack().getItem())) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
    }
}