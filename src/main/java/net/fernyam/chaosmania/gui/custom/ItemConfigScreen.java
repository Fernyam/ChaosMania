package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ItemConfigScreen extends Screen
{
    private final Screen parentScreen;
    private Button dropItemButton;
    private Button pickupItemButton;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 20;

    public ItemConfigScreen(Screen parentScreen) {
        super(Component.literal("§6ChaosMania §7- Настройка предметов"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 4;

        // Кнопка: Запрет размножения
        dropItemButton = this.addRenderableWidget(Button.builder(
                getDropText(),
                button -> toggleDropping()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        pickupItemButton = this.addRenderableWidget(Button.builder(
                getPickupText(),
                button -> togglePickling()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + BUTTON_HEIGHT + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Кнопка: Назад
        this.addRenderableWidget(Button.builder(
                Component.literal("← Назад"),
                button -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    private Component getDropText() {
        boolean enabled = ConfigMod.DISABLE_ITEM_DROPS.get();
        if (enabled) {
            return Component.literal("§c❌ Запрет выбрасывать предметы: ВКЛ");
        } else {
            return Component.literal("§a✅ Запрет выбрасывать предметы: ВЫКЛ");
        }
    }

    private Component getPickupText() {
        boolean enabled = ConfigMod.DISABLE_PICKUP_ITEM.get();
        if (enabled) {
            return Component.literal("§c❌ Запрет подбирать предметы: ВКЛ");
        } else {
            return Component.literal("§a✅ Запрет подбирать предметы: ВЫКЛ");
        }
    }

    private void toggleDropping() {
        boolean current = ConfigMod.DISABLE_ITEM_DROPS.get();
        ConfigMod.DISABLE_ITEM_DROPS.set(!current);
        ConfigMod.SPEC.save();
        dropItemButton.setMessage(getDropText());
    }

    private void togglePickling() {
        boolean current = ConfigMod.DISABLE_PICKUP_ITEM.get();
        ConfigMod.DISABLE_PICKUP_ITEM.set(!current);
        ConfigMod.SPEC.save();
        pickupItemButton.setMessage(getPickupText());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
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
