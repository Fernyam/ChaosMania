package net.fernyam.chaosmania.event.itemEvent;

import net.fernyam.chaosmania.util.PermissionHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

public class ItemDropsEvent {
    public static void onItemToss(ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            if (!PermissionHelper.canDropItem(player, event.getEntity().getItem().getItem())) {
                event.setCanceled(true);
            }
        }
    }
}