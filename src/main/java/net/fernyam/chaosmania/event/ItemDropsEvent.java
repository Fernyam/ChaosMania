package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import         net.minecraft.world.entity.item.ItemEntity;

import java.util.List;

public class ItemDropsEvent {

    public static void onItemToss(ItemTossEvent event) {

//
//
//        List<PlayerSettings> allSettings = JSONSettingCreate.loadSettings();
//
//
//            PlayerSettings playerSettings = allSettings.stream()
//                    .filter(settings -> settings.getUuidPlayer().equals(event.getPlayer().getUUID().toString()))
//                    .findFirst()
//                    .orElse(null);
//
//            if(playerSettings == null) return;
//
//            if(!playerSettings.isDisableItemDrop()) return;
//
//            if(playerSettings.getDontDropItemList().contains(BuiltInRegistries.ITEM.getKey(event.getEntity().getItem().getItem()).toString()))
//            {
//                event.setCanceled(true);
//                if(event.getPlayer() instanceof Player player)
//                {
//                    player.getInventory().add(event.getEntity().getItem());
//                }
//            }
    }
}