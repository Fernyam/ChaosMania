//package net.fernyam.chaosmania.gui;
//
//import net.fernyam.chaosmania.gui.custom.*;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.network.chat.Component;
//
//public class MainConfigScreen extends Screen {
//    private final Screen parentScreen;
//
//    private static final int BUTTON_WIDTH = 200;
//    private static final int BUTTON_HEIGHT = 20;
//    private static final int BUTTON_SPACING = 20;
//
//    public MainConfigScreen(Screen parentScreen) {
//        super(Component.literal("§6ChaosMania §7- Главное меню"));
//        this.parentScreen = parentScreen;
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//
//        int centerX = this.width / 2;
//        int startY = this.height / 8;
//
//        // Кнопка: Настройки животных
//        this.addRenderableWidget(Button.builder(
//                Component.literal("§e🐾 Животные"),
//                button -> this.minecraft.setScreen(new AnimalConfigScreen(this))
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        // Кнопка: Настройки фермерства
//        this.addRenderableWidget(Button.builder(
//                Component.literal("§a🌾 Фермерство"),
//                button -> this.minecraft.setScreen(new FarmingConfigScreen(this))
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY + BUTTON_HEIGHT + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        // Кнопка: Настройки торговли
//        this.addRenderableWidget(Button.builder(
//                Component.literal("§6💰 Торговля"),
//                button -> this.minecraft.setScreen(new TradingConfigScreen(this))
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        this.addRenderableWidget(Button.builder(
//                Component.literal("§6⬜ Блоки"),
//                button -> this.minecraft.setScreen(new BlockConfigScreen(this))
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 3, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        this.addRenderableWidget(Button.builder(
//                Component.literal("§6\uD83D\uDC8E Предметы"),
//                button -> this.minecraft.setScreen(new ItemConfigScreen(this))
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 4, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        // Кнопка: Запрещённые рецепты
////        this.addRenderableWidget(Button.builder(
////                Component.literal("§c📖 Запрещённые рецепты"),
////                button -> this.minecraft.setScreen(new RecipesConfigScreen(this))
////        ).bounds(centerX - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 3, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        // Кнопка: Закрыть
//        this.addRenderableWidget(Button.builder(
//                Component.literal("Закрыть"),
//                button -> this.onClose()
//        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build());
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