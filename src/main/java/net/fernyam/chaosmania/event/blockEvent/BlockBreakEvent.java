package net.fernyam.chaosmania.event.blockEvent;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

public class BlockBreakEvent {

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
//        if(event.getLevel().isClientSide()) return;
//
//
//
//
//
//        List<PlayerSettings> allSettings = JSONSettingCreate.loadSettings();
//
//
//
//        if (event.getPlayer() instanceof Player player) {
//            PlayerSettings playerSettings = allSettings.stream()
//                    .filter(settings -> settings.getUuidPlayer().equals(player.getUUID().toString()))
//                    .findFirst()
//                    .orElse(null);
//
//            if(playerSettings == null) return;
//            if(!playerSettings.isDisableBreakBlock()) return;
//
//            if(playerSettings.getDontBreakBlockList().contains(BuiltInRegistries.BLOCK.getKey(event.getState().getBlock()).toString()))
//            {
//                event.setCanceled(true);  // Отменяем установку
//            }
//        }
//

    }
}