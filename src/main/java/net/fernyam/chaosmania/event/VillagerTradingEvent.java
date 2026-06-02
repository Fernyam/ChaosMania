package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.UUID;

public class VillagerTradingEvent {

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {


        if ( event.getEntity() instanceof ServerPlayer player)
        {
            PlayerSettings ALLPlayerSetting = JSONSettingCreate.GetPlayerSettingsOfUUID(UUID.fromString(JSONSettingCreate.ALL_UUID_PLAYER));
            PlayerSettings playerSettings = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUUID());

            if (playerSettings == null || ALLPlayerSetting == null) return;


            if (event.getTarget() instanceof Villager villager)
            {
                if (!playerSettings.getDisableTradingVillager()) return;

                VillagerProfession profession = villager.getVillagerData().getProfession();

                if(playerSettings.canTradeWithVillager(profession))
                {
                    event.setCanceled(true);
                }
                else
                {
                    if(!ALLPlayerSetting.getDisableTradingVillager()) return;

                    if(ALLPlayerSetting.canTradeWithVillager(profession))
                    {
                        event.setCanceled(true);
                    }
                }

            }

            if (event.getTarget() instanceof WanderingTrader)
            {
                if (!playerSettings.getDisableTradingWanderingTrader())
                {
                    event.setCanceled(true);
                }
                if (!ALLPlayerSetting.getDisableTradingWanderingTrader())
                {
                    event.setCanceled(true);
                }
            }
        }
    }

}