package net.fernyam.chaosmania;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModConstants
{
    public static final TagKey<Item> C_SEEDS =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("c:seeds"));

    public static final TagKey<Item> C_CROPS =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("c:crops"));

    public static final TagKey<Item> MINECRAFT_FLOWERS =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:flowers"));


    public static final TagKey<Block> C_VEGETABLES =
            TagKey.create(Registries.BLOCK, ResourceLocation.parse("c:vegetables"));

}
