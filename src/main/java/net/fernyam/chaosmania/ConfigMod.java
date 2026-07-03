package net.fernyam.chaosmania;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ConfigMod {

    public static final ModConfigSpec SPEC;



    static {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        BUILDER.comment("Gameplay Settings").push("gameplay");



        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}