package net.fernyam.chaosmania.event;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


public class PlantingSeedsEvent
{
    public static final TagKey<Item> C_SEEDS =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("c:seeds"));

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if(!ConfigMod.DISABLE_SEED_PLANTING.get()) return;

        if (event.getSide().isClient()) {
            return;
        }

        ItemStack itemStack = event.getItemStack();
        BlockState targetBlock = event.getLevel().getBlockState(event.getPos());

        if(!itemStack.isEmpty() && !targetBlock.isEmpty())
        {
            if (targetBlock.is(Blocks.FARMLAND) && itemStack.is(C_SEEDS)) {
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
            }
        }
    }

}
