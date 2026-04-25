package net.fernyam.chaosmania.gui;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;

    // Константы для кнопок
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 30;
    private static final int BUTTON_SPACING = 20;

    // Компоненты
    private Button breedingButton;
    private Button plantingButton;
    private Button tradingButton;

    private StringWidget statusLabel;

    public ConfigScreen(Screen parentScreen) {
        super(Component.literal("ChaosMania Configuration"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 4;

        // Кнопка для кормления/размножения животных
        // Кнопка 1: Размножение
        breedingButton = addRenderableWidget(Button.builder(
                getBreedingButtonText(),
                button -> toggleBreeding()
        ).bounds(centerX - BUTTON_WIDTH / 2,
                startY,                                                   // ← 1-я кнопка
                BUTTON_WIDTH,
                BUTTON_HEIGHT).build());

// Кнопка 2: Посадка семян
        plantingButton = addRenderableWidget(Button.builder(
                getPlantingButtonText(),
                button -> togglePlanting()
        ).bounds(centerX - BUTTON_WIDTH / 2,
                startY + BUTTON_HEIGHT + BUTTON_SPACING,                  // ← 2-я кнопка
                BUTTON_WIDTH,
                BUTTON_HEIGHT).build());

// Кнопка 3: Торговля с жителями
        tradingButton = addRenderableWidget(Button.builder(
                getTradingText(),
                button -> toggleTrading()
        ).bounds(centerX - BUTTON_WIDTH / 2,
                startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2,            // ← 3-я кнопка (без +10!)
                BUTTON_WIDTH,
                BUTTON_HEIGHT).build());

        // Статус текст
        statusLabel = new StringWidget(
                centerX - 150,
                startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2 + 20,
                300,
                25,
                Component.literal("Настройки сохранены!"),
                this.font
        );
        statusLabel.active = false;
        statusLabel.visible = false;
        addRenderableWidget(statusLabel);

        // Кнопка закрытия
        addRenderableWidget(Button.builder(
                Component.literal("Закрыть"),
                button -> onClose()
        ).bounds(centerX - 75, this.height - 40, 150, 20).build());

    }

    private Component getBreedingButtonText() {
        boolean enabled = ConfigMod.DISABLE_ANIMAL_BREEDING.get();
        return enabled ?
                Component.literal(" Запретить размножение (ВКЛ)") :
                Component.literal(" Разрешить размножение (ВЫКЛ)");
    }

    private Component getPlantingButtonText() {
        boolean enabled = ConfigMod.DISABLE_SEED_PLANTING.get();
        return enabled ?
                Component.literal(" Запретить посадку семян (ВКЛ)") :
                Component.literal(" Разрешить посадку семян (ВЫКЛ)");
    }

    private Component getTradingText() {
        boolean enabled = ConfigMod.DISABLE_VILLAGER_TRADING.get();
        return enabled ?
                Component.literal(" Запретить торговлю (ВКЛ)") :
                Component.literal(" Разрешить торговлю (ВЫКЛ)");
    }

    private void toggleBreeding() {
        boolean currentValue = ConfigMod.DISABLE_ANIMAL_BREEDING.get();
        ConfigMod.DISABLE_ANIMAL_BREEDING.set(!currentValue);
        ConfigMod.SPEC.save();

        // Обновляем текст кнопки
        breedingButton.setMessage(getBreedingButtonText());
        showStatusMessage("Размножение " + (!currentValue ? "запрещено" : "разрешено"));
    }

    private void togglePlanting() {
        boolean currentValue = ConfigMod.DISABLE_SEED_PLANTING.get();
        ConfigMod.DISABLE_SEED_PLANTING.set(!currentValue);
        ConfigMod.SPEC.save();

        // Обновляем текст кнопки
        plantingButton.setMessage(getPlantingButtonText());
        showStatusMessage("Торговля " + (!currentValue ? "запрещена" : "разрешена"));
    }

    private void toggleTrading() {
        boolean currentValue = ConfigMod.DISABLE_VILLAGER_TRADING.get();
        ConfigMod.DISABLE_VILLAGER_TRADING.set(!currentValue);
        ConfigMod.SPEC.save();
        tradingButton.setMessage(getTradingText());
        showStatusMessage(" Торговля с жителями " + (!currentValue ? "запрещена" : "разрешена"));
    }

    private void showStatusMessage(String message) {
        statusLabel.setMessage(Component.literal("✓ " + message));
        statusLabel.visible = true;

        // Скрываем сообщение через 2 секунды
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                statusLabel.visible = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Заголовок
        guiGraphics.drawString(this.font, this.title, this.width / 2 - this.font.width(this.title) / 2, 20, 0xFFFFFF, true);


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