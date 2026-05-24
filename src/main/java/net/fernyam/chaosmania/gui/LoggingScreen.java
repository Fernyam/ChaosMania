//package net.fernyam.chaosmania.gui;
//
//import net.fernyam.chaosmania.gui.custom.*;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.components.EditBox;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.network.chat.Component;
//
//public class LoggingScreen extends Screen
//{
//    private final Screen parentScreen;
//    private EditBox usernameField;
//    private EditBox passwordField;
//    private Component statusMessage = null;
//    private int statusTime = 0;
//
//    private String Loggin = "GeyTus";
//    private String Password = "1234";
//
//    private static final int BUTTON_WIDTH = 200;
//    private static final int BUTTON_HEIGHT = 20;
//    private static final int BUTTON_SPACING = 20;
//
//    public LoggingScreen(Screen parentScreen) {
//        super(Component.literal("§6ChaosMania §7- Вход в систему"));
//        this.parentScreen = parentScreen;
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//
//        int centerX = this.width / 2;
//        int startY = this.height / 4;
//
//        // Текстовое поле: Логин
//        usernameField = new EditBox(
//                this.font,
//                centerX - 100,
//                startY,
//                200,
//                20,
//                Component.literal("Логин")
//        );
//        usernameField.setMaxLength(50);
//        usernameField.setHint(Component.literal("Введите логин"));
//        this.addRenderableWidget(usernameField);
//
//        // Текстовое поле: Пароль (ПРОСТОЙ ВАРИАНТ - БЕЗ СКРЫТИЯ)
//        passwordField = new EditBox(
//                this.font,
//                centerX - 100,
//                startY + BUTTON_HEIGHT + BUTTON_SPACING,
//                200,
//                20,
//                Component.literal("Пароль")
//        );
//        passwordField.setMaxLength(50);
//        passwordField.setHint(Component.literal("Введите пароль"));
//        // НЕ используем setFormatter и setFilter - это вызывает вылет
//        this.addRenderableWidget(passwordField);
//
//        // Кнопка: Подтвердить
//        this.addRenderableWidget(Button.builder(
//                Component.literal("§a✓ Подтвердить"),
//                button -> checkLogin()
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2 + 10, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        // Кнопка: Закрыть
//        this.addRenderableWidget(Button.builder(
//                Component.literal("§cЗакрыть"),
//                button -> this.onClose()
//        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//    }
//
//    private void checkLogin() {
//        String username = usernameField.getValue();
//        String password = passwordField.getValue();
//
//        if (username.equals(Loggin) && password.equals(Password)) {
//            showStatusMessage(Component.literal("§aУспешный вход! Перенаправление..."));
//            this.minecraft.setScreen(new MainConfigScreen(parentScreen));
//        } else {
//            showStatusMessage(Component.literal("§cНеверный логин или пароль!"));
//            usernameField.setValue("");
//            passwordField.setValue("");
//            usernameField.setFocused(true);
//        }
//    }
//
//    private void showStatusMessage(Component message) {
//        this.statusMessage = message;
//        this.statusTime = 60;
//    }
//
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//
//
//
//        // ESC - закрыть
//        if (keyCode == 256) {
//            this.onClose();
//            return true;
//        }
//
//
//        // Передаём обработку текстовым полям
//        if (usernameField.keyPressed(keyCode, scanCode, modifiers)) {
//            return true;
//        }
//        if (passwordField.keyPressed(keyCode, scanCode, modifiers)) {
//            return true;
//        }
//
//        return super.keyPressed(keyCode, scanCode, modifiers);
//    }
//
//    @Override
//    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
//
//        // Заголовок
//        guiGraphics.drawString(
//                this.font,
//                this.title,
//                this.width / 2 - this.font.width(this.title) / 2,
//                20,
//                0xFFFFFF,
//                true
//        );
//
//        // Подпись для поля логина
//        guiGraphics.drawString(
//                this.font,
//                "Логин:",
//                this.width / 2 - 105,
//                this.height / 4 - 10,
//                0xAAAAAA,
//                false
//        );
//
//        // Подпись для поля пароля
//        guiGraphics.drawString(
//                this.font,
//                "Пароль:",
//                this.width / 2 - 105,
//                this.height / 4 + BUTTON_HEIGHT + BUTTON_SPACING - 10,
//                0xAAAAAA,
//                false
//        );
//
//        // Сообщение о статусе
//        if (statusMessage != null && statusTime > 0) {
//            int textWidth = this.font.width(statusMessage);
//            guiGraphics.drawString(
//                    this.font,
//                    statusMessage,
//                    this.width / 2 - textWidth / 2,
//                    this.height / 4 + (BUTTON_HEIGHT + BUTTON_SPACING) * 2 + 45,
//                    0xFFFFFF,
//                    true
//            );
//            statusTime--;
//        } else {
//            statusMessage = null;
//        }
//
//        super.render(guiGraphics, mouseX, mouseY, partialTick);
//    }
//
//    @Override
//    public void onClose() {
//        this.minecraft.setScreen(parentScreen);
//    }
//
//    @Override
//    public boolean isPauseScreen() {
//        return false;
//    }
//}