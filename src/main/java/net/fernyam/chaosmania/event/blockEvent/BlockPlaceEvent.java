package net.fernyam.chaosmania.event.blockEvent;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.UUID;

public class BlockPlaceEvent {


    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;

        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerSettings ALLPlayerSetting = JSONSettingCreate.GetPlayerSettingsOfUUID(UUID.fromString(JSONSettingCreate.ALL_UUID_PLAYER));
            PlayerSettings playerSettings = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUUID());

            if (playerSettings == null || ALLPlayerSetting == null) return;
            if (!playerSettings.getDisablePlaceBlock()) return;


            if (playerSettings.canPlaceBlock(BuiltInRegistries.BLOCK.getKey(event.getPlacedBlock().getBlock()).toString())) {
                event.setCanceled(true);
            }
            else
            {
                if(!ALLPlayerSetting.getDisablePlaceBlock()) return;
                if (ALLPlayerSetting.canPlaceBlock(BuiltInRegistries.BLOCK.getKey(event.getPlacedBlock().getBlock()).toString()))
                {
                    event.setCanceled(true);
                }
            }
        }

    }
}