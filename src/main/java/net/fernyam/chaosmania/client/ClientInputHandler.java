package net.fernyam.chaosmania.client;

import net.fernyam.chaosmania.gui.LoggingScreen;

import net.fernyam.chaosmania.gui.custom.AllBlocksScreen;
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
        if (KeyBindings.EXAMPLE_KEY.consumeClick()) {
            openLoggingScreen();
        }
    }

    private static void openLoggingScreen() {
        Minecraft mc = Minecraft.getInstance();

        // Создаём твой LoggingScreen, передаём текущий экран как parent
        AllBlocksScreen screen = new AllBlocksScreen();

        // Открываем экран
        mc.setScreen(screen);
    }
}