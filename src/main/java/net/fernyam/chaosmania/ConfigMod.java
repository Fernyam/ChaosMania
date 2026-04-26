package net.fernyam.chaosmania;

import com.mojang.datafixers.TypeRewriteRule;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ConfigMod {

    public static final ModConfigSpec.BooleanValue DISABLE_ANIMAL_BREEDING;
    public static final ModConfigSpec.BooleanValue DISABLE_SEED_PLANTING;
    public static final ModConfigSpec.BooleanValue DISABLE_VILLAGER_TRADING; // Новая настройка
    public static final ModConfigSpec.BooleanValue DISABLE_PICKUP_ITEM;

    // Список запрещённых рецептов (можно будет менять через конфиг)
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FORBIDDEN_RECIPES;

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

        DISABLE_PICKUP_ITEM = BUILDER
                .comment("Запретить подбирать предметы")
                        .define("disablePickupItem" , true);

        FORBIDDEN_RECIPES = BUILDER
                .comment("Список ID рецептов, которые нужно запретить")
                .defineListAllowEmpty("forbiddenRecipes",
                        List.of(
                                "minecraft:diamond_sword",
                                "minecraft:diamond_pickaxe",
                                "minecraft:golden_apple"
                        ),
                        entry -> entry instanceof String);


        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}