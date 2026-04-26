package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.ConfigMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class RecipesConfigScreen extends Screen {
    private final Screen parentScreen;
    private EditBox recipeInput;
    private Component statusMessage = null;
    private int statusTime = 0;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 20;

    public RecipesConfigScreen(Screen parentScreen) {
        super(Component.literal("§6ChaosMania §7- Запрещённые рецепты"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 4;

        // Текстовое поле для ввода ID рецепта
        recipeInput = new EditBox(
                this.font,
                centerX - 100,
                startY,
                200,
                20,
                Component.literal("Введите ID рецепта")
        );
        recipeInput.setMaxLength(100);
        recipeInput.setHint(Component.literal("пример: minecraft:diamond_sword"));
        this.addRenderableWidget(recipeInput);

        // Кнопка: Добавить рецепт
        this.addRenderableWidget(Button.builder(
                Component.literal("§a➕ Добавить рецепт"),
                button -> addRecipe()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + BUTTON_HEIGHT + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Кнопка: Очистить все
        this.addRenderableWidget(Button.builder(
                Component.literal("§c🗑️ Очистить все"),
                button -> clearAllRecipes()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Кнопка: Назад
        this.addRenderableWidget(Button.builder(
                Component.literal("← Назад"),
                button -> this.minecraft.setScreen(parentScreen)
        ).bounds(centerX - BUTTON_WIDTH / 2, this.height - 40, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    private void addRecipe() {
        String recipeId = recipeInput.getValue().trim().toLowerCase();

        if (recipeId.isEmpty()) {
            showStatusMessage(Component.literal("§cВведите ID рецепта!"));
            return;
        }

        // Добавляем minecraft: если нет namespace
        if (!recipeId.contains(":")) {
            recipeId = "minecraft:" + recipeId;
        }

        // Получаем текущий список
        List<? extends String> currentList = ConfigMod.FORBIDDEN_RECIPES.get();
        List<String> newList = new ArrayList<>(currentList);

        if (newList.contains(recipeId)) {
            showStatusMessage(Component.literal("§cРецепт уже в списке!"));
            return;
        }

        newList.add(recipeId);
        ConfigMod.FORBIDDEN_RECIPES.set(newList);
        ConfigMod.SPEC.save();

        showStatusMessage(Component.literal("§aДобавлен: " + recipeId));
        recipeInput.setValue("");
    }

    private void clearAllRecipes() {
        ConfigMod.FORBIDDEN_RECIPES.set(new ArrayList<>());
        ConfigMod.SPEC.save();
        showStatusMessage(Component.literal("§aСписок очищен!"));
    }

    private void showStatusMessage(Component message) {
        this.statusMessage = message;
        this.statusTime = 60;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Enter нажат в текстовом поле
        if ((keyCode == 257 || keyCode == 335) && recipeInput.isFocused()) {
            addRecipe();
            return true;
        }

        if (keyCode == 256) {
            this.onClose();
            return true;
        }

        if (recipeInput.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Заголовок
        guiGraphics.drawString(
                this.font,
                this.title,
                this.width / 2 - this.font.width(this.title) / 2,
                20,
                0xFFFFFF,
                true
        );

        // Показываем текущий список (первые несколько)
        List<? extends String> recipes = ConfigMod.FORBIDDEN_RECIPES.get();
        int startY = this.height / 4 + 100;

        guiGraphics.drawString(
                this.font,
                "§7Текущие запрещённые рецепты:",
                this.width / 2 - 150,
                startY - 15,
                0xAAAAAA,
                false
        );

        for (int i = 0; i < Math.min(recipes.size(), 8); i++) {
            String recipe = recipes.get(i);
            guiGraphics.drawString(
                    this.font,
                    "§c- " + recipe,
                    this.width / 2 - 150,
                    startY + i * 12,
                    0xFFFFFF,
                    false
            );
        }

        if (recipes.isEmpty()) {
            guiGraphics.drawString(
                    this.font,
                    "§7(пусто)",
                    this.width / 2 - 150,
                    startY,
                    0x888888,
                    false
            );
        }

        // Всплывающее сообщение
        if (statusMessage != null && statusTime > 0) {
            int textWidth = this.font.width(statusMessage);
            guiGraphics.drawString(
                    this.font,
                    statusMessage,
                    this.width / 2 - textWidth / 2,
                    this.height - 60,
                    0xFFFFFF,
                    true
            );
            statusTime--;
        } else {
            statusMessage = null;
        }

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