package net.fernyam.chaosmania.client;


import net.fernyam.chaosmania.gui.custom.MainSettingScreen;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.minecraft.client.Minecraft;

public class ClientInputHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        // Проверяем, что игрок существует и нет открытого экрана
        if (mc.player == null || mc.screen != null) return;

        // Проверяем нажатие клавиши (один раз за нажатие)
//        if (KeyBindings.EXAMPLE_KEY.consumeClick()) {
//            openLoggingScreen();
//        }

        if(KeyBindings.EXAMPLE_KEY_2.consumeClick())
        {
            openLoggingScreen_2();
        }

    }

//    private static void openLoggingScreen() {
//        Minecraft mc = Minecraft.getInstance();
//
//
//        AllBlocksScreen screen = new AllBlocksScreen();
//
//        // Открываем экран
//        mc.setScreen(screen);
//    }

    private static void openLoggingScreen_2() {
        Minecraft mc = Minecraft.getInstance();


        MainSettingScreen screen = new MainSettingScreen();

        // Открываем экран
        mc.setScreen(screen);
    }
}