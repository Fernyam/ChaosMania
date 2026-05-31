package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.UUID;

import static net.fernyam.chaosmania.ModConstants.C_SEEDS;


public class PlantingSeedsEvent
{

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

        if (event.getSide().isClient()) {
            return;
        }

        ItemStack itemStack = event.getItemStack();
        BlockState targetBlock = event.getLevel().getBlockState(event.getPos());

        if(event.getEntity() instanceof ServerPlayer player)
        {
            PlayerSettings ALLPlayerSetting = JSONSettingCreate.GetPlayerSettingsOfUUID(UUID.fromString(JSONSettingCreate.ALL_UUID_PLAYER));
            PlayerSettings playerSettings = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUUID());

            if (playerSettings == null || ALLPlayerSetting == null) return;
            if (!playerSettings.getDisablePlantingSeed()) return;

            if (!itemStack.isEmpty() && !targetBlock.isEmpty()) {
                    if (targetBlock.is(Blocks.FARMLAND)) {

                        if(playerSettings.canPlanSeed(itemStack.getItem().toString())) {
                            event.setCanceled(true);
                            event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
                        }
                        else
                        {
                            if(!ALLPlayerSetting.getDisablePlantingSeed()) return;

                            if(ALLPlayerSetting.canPlanSeed(itemStack.getItem().toString()))
                            {
                                event.setCanceled(true);
                                event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
                            }
                        }
                    }
                }
            }
        }
    }


