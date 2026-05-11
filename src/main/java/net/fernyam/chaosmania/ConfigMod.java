package net.fernyam.chaosmania;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ConfigMod {


    public static final ModConfigSpec.BooleanValue DISABLE_ANIMAL_BREEDING;
    public static final ModConfigSpec.BooleanValue DISABLE_SEED_PLANTING;
    public static final ModConfigSpec.BooleanValue DISABLE_VILLAGER_TRADING;
    public static final ModConfigSpec.BooleanValue DISABLE_PICKUP_ITEM;
    public static final ModConfigSpec.BooleanValue DISABLE_WANDER_TRADING;
    public static final ModConfigSpec.BooleanValue DISABLE_BREAK_BLOCK;
    public static final ModConfigSpec.BooleanValue DISABLE_PLACE_BLOCK;
    public static final ModConfigSpec.BooleanValue DISABLE_ITEM_DROPS;



    public static final ModConfigSpec SPEC;



    static {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        BUILDER.comment("Gameplay Settings").push("gameplay");

        DISABLE_ANIMAL_BREEDING = BUILDER
                .comment("Запретить размножение животных")
                .define("disableAnimalBreeding", true);

        DISABLE_SEED_PLANTING = BUILDER
                .comment("Запретить посадку семян")
                .define("disableSeedPlanting", true);

        DISABLE_VILLAGER_TRADING = BUILDER
                .comment("Запретить торговлю с жителями")
                .define("disableVillagerTrading", true);

        DISABLE_WANDER_TRADING = BUILDER
                .comment("Запретить торговлю с торговцами")
                .define("disableWanderTrading", true);

        DISABLE_PICKUP_ITEM = BUILDER
                .comment("Запретить подбирать предметы")
                        .define("disablePickupItem" , true);

        DISABLE_ITEM_DROPS = BUILDER
                .comment("Запретить выбрасывать предметы")
                .define("disableItemDrops" , true);

        DISABLE_BREAK_BLOCK = BUILDER
                .comment("Запретить ломать блок")
                .define("disablePickupItem" , true);

        DISABLE_PLACE_BLOCK = BUILDER
                .comment("Запретить устанавливать блок")
                .define("disablePickupItem" , true);


        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}