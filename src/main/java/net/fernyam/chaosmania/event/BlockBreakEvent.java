package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.level.BlockEvent;

public class BlockBreakEvent {

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if(!ConfigMod.DISABLE_BREAK_BLOCK.get()) return;

        Player player = event.getPlayer();


        if (true) {
            event.setCanceled(true);  // Отменяем поломку
        }

    }
}