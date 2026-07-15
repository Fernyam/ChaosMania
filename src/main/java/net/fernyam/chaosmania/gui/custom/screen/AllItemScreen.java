package net.fernyam.chaosmania.gui.custom.screen;

import net.fernyam.chaosmania.data.settings.SettingsManager;
import net.fernyam.chaosmania.gui.custom.base.BaseSelectionScreen;
import net.fernyam.chaosmania.gui.custom.MainSettingScreen;
import net.fernyam.chaosmania.gui.PlayerInfoData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.stream.Collectors;

public class AllItemScreen extends BaseSelectionScreen<Item> {

    private enum DisplayType {
        ITEMS,
        BLOCKS
    }

    private DisplayType currentDisplayType = DisplayType.ITEMS;
    private Button itemsButton;
    private Button blocksButton;

    public AllItemScreen(PlayerInfoData player, MainSettingScreen parentScreen) {
        super(player, parentScreen);
    }

    @Override
    public Component getScreenTitle() {
        return Component.literal("Добавление предметов в настройки игрока " +
                (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName()));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = width / 2;
        int buttonY = height / 2 - 117 + 25 + 7;
        int buttonWidth = 45;

        this.itemsButton = addRenderableWidget(Button.builder(
                Component.literal("Items"),
                button -> switchToItems()
        ).bounds(centerX - BACKGROUND_WIDTH / 2 - buttonWidth, buttonY, buttonWidth, BUTTON_HEIGHT).build());

        this.blocksButton = addRenderableWidget(Button.builder(
                Component.literal("Blocks"),
                button -> switchToBlocks()
        ).bounds(centerX - BACKGROUND_WIDTH / 2 - buttonWidth, buttonY + BUTTON_HEIGHT + 5, buttonWidth, BUTTON_HEIGHT).build());

        updateButtonStates();
    }

    private void switchToItems() {
        currentDisplayType = DisplayType.ITEMS;
        updateButtonStates();
        loadAndUpdateEntries();
    }

    private void switchToBlocks() {
        currentDisplayType = DisplayType.BLOCKS;
        updateButtonStates();
        loadAndUpdateEntries();
    }

    private void updateButtonStates() {
        if (itemsButton != null) {
            itemsButton.active = (currentDisplayType != DisplayType.ITEMS);
        }
        if (blocksButton != null) {
            blocksButton.active = (currentDisplayType != DisplayType.BLOCKS);
        }
    }

    @Override
    protected Collection<Item> getAllElements() {
        if (currentDisplayType == DisplayType.ITEMS) {
            return BuiltInRegistries.ITEM.stream()
                    .filter(item -> !(item instanceof BlockItem))
                    .collect(Collectors.toList());
        } else {
            return BuiltInRegistries.ITEM.stream()
                    .filter(item -> item instanceof BlockItem)
                    .collect(Collectors.toList());
        }
    }

    @Override
    protected String getElementId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    @Override
    protected String getElementName(Item item) {
        return new ItemStack(item).getHoverName().getString();
    }

    @Override
    protected void addElementToPlayer(Item item) {
        var settings = SettingsManager.getItemSettings(player.getUuid());
        if (settings != null) {
            String id = getElementId(item);
            if (!settings.isItemExists(id)) {
                settings.addItem(id);
                SettingsManager.saveItemSettings(player.getUuid());
            }
        }
    }

    @Override
    protected void removeElementFromPlayer(Item item) {
        var settings = SettingsManager.getItemSettings(player.getUuid());
        if (settings != null) {
            String id = getElementId(item);
            if (settings.isItemExists(id)) {
                settings.removeItem(id);
                SettingsManager.saveItemSettings(player.getUuid());
            }
        }
    }

    @Override
    protected boolean isElementInPlayerSettings(Item item) {
        var settings = SettingsManager.getItemSettings(player.getUuid());
        return settings != null && settings.isItemExists(getElementId(item));
    }

    @Override
    protected boolean shouldDisplayElement(Item item) {
        return true;
    }

    @Override
    protected boolean isSpecialIcon() {
        return false;
    }

    @Override
    protected void renderExtra(GuiGraphics gui, int left, int top, int width, int height, Item item) {
        ItemStack stack = new ItemStack(item);
        gui.renderItem(stack, left + 4, top + 14);
        gui.renderItemDecorations(minecraft.font, stack, left + 4, top + 14);
    }
}