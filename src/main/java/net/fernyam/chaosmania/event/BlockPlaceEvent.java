package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ConfigMod;
import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

public class BlockPlaceEvent {

    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
//        if(event.getLevel().isClientSide()) return;

        List<PlayerSettings> allSettings = JSONSettingCreate.loadSettings();



        if (event.getEntity() instanceof Player player) {
        PlayerSettings playerSettings = allSettings.stream()
                .filter(settings -> settings.getUuidPlayer().equals(player.getUUID().toString()))
                .findFirst()
                .orElse(null);

            if(playerSettings == null) return;
            if(!playerSettings.isDisablePlaceBlock()) return;

            if(playerSettings.getDontPlaceBlockList().contains(BuiltInRegistries.BLOCK.getKey(event.getPlacedBlock().getBlock()).toString()))
            {
                event.setCanceled(true);  // Отменяем установку
            }
        }
    }
}
