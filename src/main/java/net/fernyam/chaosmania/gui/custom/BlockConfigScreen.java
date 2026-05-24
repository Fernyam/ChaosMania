//package net.fernyam.chaosmania.gui.custom;
//
//import net.fernyam.chaosmania.ConfigMod;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.network.chat.Component;
//
//public class BlockConfigScreen extends Screen
//{
//    private final Screen parentScreen;
//    private Button breakBlockButton;
//    private Button placeBlockButton;
//
//
//    private static final int BUTTON_WIDTH = 200;
//    private static final int BUTTON_HEIGHT = 20;
//    private static final int BUTTON_SPACING = 20;
//
//    public BlockConfigScreen(Screen parentScreen) {
//        super(Component.literal("§6ChaosMania §7- Настройки блоков"));
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
//        // Кнопка: Запрет размножения
//        breakBlockButton = this.addRenderableWidget(Button.builder(
//                getBreakText(),
//                button -> toggleBreaking()
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        placeBlockButton = this.addRenderableWidget(Button.builder(
//                getPlaceText(),
//                button -> togglePlacing()
//        ).bounds(centerX - BUTTON_WIDTH / 2, startY + BUTTON_HEIGHT + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//
//        // Кнопка: Назад
//        this.addRenderableWidget(Button.builder(
//                Component.literal("← Назад"),
//                button -> this.minecraft.setScreen(parentScreen)
//        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build());
//    }
//
//    private Component getBreakText() {
//        boolean enabled = ConfigMod.DISABLE_BREAK_BLOCK.get();
//        if (enabled) {
//            return Component.literal("§c❌ Запрет ломать блоки: ВКЛ");
//        } else {
//            return Component.literal("§a✅ Запрет ломать блоки: ВЫКЛ");
//        }
//    }
//
//    private Component getPlaceText() {
//        boolean enabled = ConfigMod.DISABLE_PLACE_BLOCK.get();
//        if (enabled) {
//            return Component.literal("§c❌ Запрет ставить блоки: ВКЛ");
//        } else {
//            return Component.literal("§a✅ Запрет ставить блоки: ВЫКЛ");
//        }
//    }
//
//    private void toggleBreaking() {
//        boolean current = ConfigMod.DISABLE_BREAK_BLOCK.get();
//        ConfigMod.DISABLE_BREAK_BLOCK.set(!current);
//        ConfigMod.SPEC.save();
//        breakBlockButton.setMessage(getBreakText());
//    }
//
//    private void togglePlacing() {
//        boolean current = ConfigMod.DISABLE_PLACE_BLOCK.get();
//        ConfigMod.DISABLE_PLACE_BLOCK.set(!current);
//        ConfigMod.SPEC.save();
//        placeBlockButton.setMessage(getPlaceText());
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
