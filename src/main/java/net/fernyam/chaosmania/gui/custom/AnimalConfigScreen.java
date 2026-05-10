package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class AnimalConfigScreen extends Screen {
    private final Screen parentScreen;
    private Button breedingButton;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 20;

    public AnimalConfigScreen(Screen parentScreen) {
        super(Component.literal("§6ChaosMania §7- Настройки животных"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 4;

        // Кнопка: Запрет размножения
        breedingButton = this.addRenderableWidget(Button.builder(
                getBreedingText(),
                button -> toggleBreeding()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Кнопка: Назад
        this.addRenderableWidget(Button.builder(
                Component.literal("← Назад"),
                button -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    private Component getBreedingText() {
        boolean enabled = ConfigMod.DISABLE_ANIMAL_BREEDING.get();
        if (enabled) {
            return Component.literal("§c❌ Запрет размножения: ВКЛ");
        } else {
            return Component.literal("§a✅ Запрет размножения: ВЫКЛ");
        }
    }

    private void toggleBreeding() {
        boolean current = ConfigMod.DISABLE_ANIMAL_BREEDING.get();
        ConfigMod.DISABLE_ANIMAL_BREEDING.set(!current);
        ConfigMod.SPEC.save();
        breedingButton.setMessage(getBreedingText());
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