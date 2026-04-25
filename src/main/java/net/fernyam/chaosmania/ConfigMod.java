package net.fernyam.chaosmania;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigMod {

    public static final ModConfigSpec.BooleanValue DISABLE_ANIMAL_BREEDING;
    public static final ModConfigSpec.BooleanValue DISABLE_SEED_PLANTING;
    public static final ModConfigSpec.BooleanValue DISABLE_VILLAGER_TRADING; // Новая настройка

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
                .define("disableVillagerTrading", true); // По умолчанию включено

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}