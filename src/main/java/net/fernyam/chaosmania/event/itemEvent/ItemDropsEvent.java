package net.fernyam.chaosmania.event.itemEvent;

//import net.minecraft.world.entity.player.Player;
import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

import java.util.UUID;

public class ItemDropsEvent {

    public static void onItemToss(ItemTossEvent event) {

        PlayerSettings ALLPlayerSetting = JSONSettingCreate.GetPlayerSettingsOfUUID(UUID.fromString(JSONSettingCreate.ALL_UUID_PLAYER));
        PlayerSettings playerSettings = JSONSettingCreate.GetPlayerSettingsOfUUID(event.getPlayer().getUUID());

        if(playerSettings == null || ALLPlayerSetting == null) return;

        if(!playerSettings.getDisableDropItem()) return;

        if(playerSettings.canDropItem(BuiltInRegistries.ITEM.getKey(event.getEntity().getItem().getItem()).toString()))
        {
            event.setCanceled(true);
        }
        else
        {
            if(!ALLPlayerSetting.getDisableDropItem()) return;
            if (ALLPlayerSetting.canDropItem(BuiltInRegistries.ITEM.getKey(event.getEntity().getItem().getItem()).toString()))
            {
                event.setCanceled(true);
            }
        }


    }
}