package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class FarmingConfigScreen extends Screen {
    private final Screen parentScreen;
    private Button plantingButton;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 20;

    public FarmingConfigScreen(Screen parentScreen) {
        super(Component.literal("§6ChaosMania §7- Настройки фермерства"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 4;

        // Кнопка: Запрет посадки семян
        plantingButton = this.addRenderableWidget(Button.builder(
                getPlantingText(),
                button -> togglePlanting()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Кнопка: Назад
        this.addRenderableWidget(Button.builder(
                Component.literal("← Назад"),
                button -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    private Component getPlantingText() {
        boolean enabled = ConfigMod.DISABLE_SEED_PLANTING.get();
        if (enabled) {
            return Component.literal("§c❌ Запрет посадки семян: ВКЛ");
        } else {
            return Component.literal("§a✅ Запрет посадки семян: ВЫКЛ");
        }
    }

    private void togglePlanting() {
        boolean current = ConfigMod.DISABLE_SEED_PLANTING.get();
        ConfigMod.DISABLE_SEED_PLANTING.set(!current);
        ConfigMod.SPEC.save();
        plantingButton.setMessage(getPlantingText());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawString(
                this.font,
                this.title,
                this.width / 2 - this.font.width(this.title) / 2,
                20,
                0xFFFFFF,
                true
        );

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parentScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}