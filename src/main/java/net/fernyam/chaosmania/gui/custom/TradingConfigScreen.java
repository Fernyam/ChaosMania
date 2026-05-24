//package net.fernyam.chaosmania.gui.custom;
//
//import net.fernyam.chaosmania.ConfigMod;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.network.chat.Component;
//
//public class TradingConfigScreen extends Screen {
//
//    private final Screen parentScreen;
//    private Button villagerTradingButton;
//    private Button wanderingTraderButton;
//
//
//    private static final int BUTTON_WIDTH = 200;
//    private static final int BUTTON_HEIGHT = 20;
//    private static final int BUTTON_SPACING = 20;
//
//    public TradingConfigScreen(Screen parentScreen) {
//        super(Component.literal("§6ChaosMania §7- Настройки торговли"));
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
//        // Кнопка: Запрет торговли с жителями
//        villagerTradingButton = this.addRenderableWidget(Button.builder(
//                getVillagerTradingText(),
//                button -> toggleVillagerTrading()
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        // Кнопка: Запрет торговли со странствующими торговцами (заглушка)
//        wanderingTraderButton = this.addRenderableWidget(Button.builder(
//                getWanderingTradingText(),
//                button -> toggleWanderingTrading()
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY + BUTTON_HEIGHT + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        // Кнопка: Назад
//        this.addRenderableWidget(Button.builder(
//                Component.literal("← Назад"),
//                button -> this.minecraft.setScreen(parentScreen)
//        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//    }
//
//    private Component getVillagerTradingText() {
//        boolean enabled = ConfigMod.DISABLE_VILLAGER_TRADING.get();
//        if (enabled) {
//            return Component.literal("§c❌ Торговля с жителями: ЗАПРЕЩЕНА");
//        } else {
//            return Component.literal("§a✅ Торговля с жителями: РАЗРЕШЕНА");
//        }
//    }
//
//    private Component getWanderingTradingText()
//    {
//        boolean enabled = ConfigMod.DISABLE_WANDER_TRADING.get();
//        if (enabled) {
//            return Component.literal("§c❌ Торговля с торговцами: ЗАПРЕЩЕНА");
//        } else {
//            return Component.literal("§a✅ Торговля с торговцами: РАЗРЕШЕНА");
//        }
//    }
//
//    private void toggleVillagerTrading() {
//        boolean current = ConfigMod.DISABLE_VILLAGER_TRADING.get();
//        ConfigMod.DISABLE_VILLAGER_TRADING.set(!current);
//        ConfigMod.SPEC.save();
//        villagerTradingButton.setMessage(getVillagerTradingText());
//    }
//
//    private void toggleWanderingTrading() {
//        boolean current = ConfigMod.DISABLE_WANDER_TRADING.get();
//        ConfigMod.DISABLE_WANDER_TRADING.set(!current);
//        ConfigMod.SPEC.save();
//        wanderingTraderButton.setMessage(getWanderingTradingText());
//    }
//
//    @Override
//    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
//
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