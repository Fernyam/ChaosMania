package net.fernyam.chaosmania.event.blockEvent;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;
import java.util.UUID;

public class BlockBreakEvent {

    public static void onBlockBreak(BlockEvent.BreakEvent event) {

        if(event.getLevel().isClientSide()) return;


        if (event.getPlayer() instanceof Player player) {
            PlayerSettings ALLPlayerSetting = JSONSettingCreate.GetPlayerSettingsOfUUID(UUID.fromString(JSONSettingCreate.ALL_UUID_PLAYER));
            PlayerSettings playerSettings = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUUID());

            if(playerSettings == null || ALLPlayerSetting == null) return;

            if(!playerSettings.getDisableBreakBlock()) return;

            if(playerSettings.canBreakBlock(BuiltInRegistries.BLOCK.getKey(event.getState().getBlock()).toString()))
            {
                event.setCanceled(true);  // Отменяем установку
            }
            else
            {
                if(!ALLPlayerSetting.getDisableBreakBlock()) return;
                if (ALLPlayerSetting.canBreakBlock(BuiltInRegistries.BLOCK.getKey(event.getState().getBlock()).toString()))
                {
                    event.setCanceled(true);
                }
            }
        }
    }
}