package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ConfigMod;
import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.common.util.TriState;

import java.util.List;

public class ItemPickupEvent
{
    public static void onItemPickupPre(ItemEntityPickupEvent.Pre event) {
        List<PlayerSettings> allSettings = JSONSettingCreate.loadSettings();


        PlayerSettings playerSettings = allSettings.stream()
                .filter(settings -> settings.getUuidPlayer().equals(event.getPlayer().getUUID().toString()))
                .findFirst()
                .orElse(null);


        if (playerSettings == null) return;

        if (!playerSettings.isDisableItemPickup()) return;

        if (playerSettings.getDontPuckupItemList().contains(BuiltInRegistries.ITEM.getKey(event.getItemEntity().getItem().getItem()).toString())) {
            event.setCanPickup(TriState.FALSE);
        }
    }
}
