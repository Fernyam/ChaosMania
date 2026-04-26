package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.common.util.TriState;

public class ItemPickupEvent
{
    public static void onItemPickupPre(ItemEntityPickupEvent.Pre event) {
        if (!ConfigMod.DISABLE_PICKUP_ITEM.get()) return;

        ItemStack stack = event.getItemEntity().getItem();

        if(!stack.isEmpty())
        {
            if (stack.getItem() == net.minecraft.world.item.Items.ELYTRA) {
                // Запрещаем подбор
                event.setCanPickup(TriState.FALSE);
            }
        }
    }
}
