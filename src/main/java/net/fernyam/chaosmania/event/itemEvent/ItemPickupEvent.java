package net.fernyam.chaosmania.event.itemEvent;

import net.fernyam.chaosmania.util.PermissionHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

public class ItemPickupEvent {
    public static void onItemPickupPre(ItemEntityPickupEvent.Pre event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            if (!PermissionHelper.canPickupItem(player, event.getItemEntity().getItem().getItem())) {
                event.setCanPickup(TriState.FALSE);
            }
        }
    }
}