package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

public class ItemDropsEvent {

    public static void onItemToss(ItemTossEvent event) {
        if(!ConfigMod.DISABLE_ITEM_DROPS.get()) return;

        ItemStack tossedStack = event.getEntity().getItem();

        // Запрещаем выбрасывание элитр
        if (tossedStack.getItem() == Items.ELYTRA) {
            event.setCanceled(true);

        }
    }
}