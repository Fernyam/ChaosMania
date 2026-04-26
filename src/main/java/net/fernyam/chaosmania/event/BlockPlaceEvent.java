package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.level.BlockEvent;

public class BlockPlaceEvent {

    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if(!ConfigMod.DISABLE_PLACE_BLOCK.get()) return;

        // Запрещаем установку взрывчатки
        if (true) {
            if (event.getEntity() instanceof Player) {
                event.setCanceled(true);  // Отменяем установку
            }
        }
    }
}