package net.fernyam.chaosmania.event.itemEvent;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

import java.util.UUID;

public class ItemPickupEvent
{
    public static void onItemPickupPre(ItemEntityPickupEvent.Pre event) {

        PlayerSettings ALLPlayerSetting = JSONSettingCreate.GetPlayerSettingsOfUUID(UUID.fromString(JSONSettingCreate.ALL_UUID_PLAYER));
        PlayerSettings playerSettings = JSONSettingCreate.GetPlayerSettingsOfUUID(event.getPlayer().getUUID());


        if (playerSettings == null || ALLPlayerSetting == null) return;

        if (!playerSettings.getDisablePickupItem()) return;

        if (playerSettings.canPickupItem(BuiltInRegistries.ITEM.getKey(event.getItemEntity().getItem().getItem()).toString())) {
            event.setCanPickup(TriState.FALSE);
        }
        else
        {
            if(!ALLPlayerSetting.getDisablePickupItem()) return;
            if (ALLPlayerSetting.canPickupItem(BuiltInRegistries.ITEM.getKey(event.getItemEntity().getItem().getItem()).toString()))
            {
                event.setCanPickup(TriState.FALSE);
            }
        }
    }
}
