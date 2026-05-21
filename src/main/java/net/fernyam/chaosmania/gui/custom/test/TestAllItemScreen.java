package net.fernyam.chaosmania.gui.custom.test;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;


public class TestAllItemScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");

    private static PlayerInfoData player;

    public static TestScreen parentScreen;

    private static final int BUTTON_HEIGHT = 20;

    private static final int backgroundWidth = 216;
    private static final int backgroundHeight = 230;

    private EditBox searchAllItems;

    private ScrollingItemList itemListScroll;

    private List<ItemEntry> allItemsMasterList;
    private String currentItemSearchFilter = "";

    public TestAllItemScreen(PlayerInfoData player, TestScreen parentScreen) {
        super(Component.literal("Добавление предметов в настройки игрока " + (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName())));

        this.player = player;
        this.parentScreen = parentScreen;
        this.allItemsMasterList = new ArrayList<>();
        this.currentItemSearchFilter = "";

        if (player == null) onClose();

        loadAllItems();
    }

    private List<ItemEntry> filterItemsBySearch(List<ItemEntry> items, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(items);
        }

        String lowerCaseSearch = searchText.toLowerCase().trim();
        List<ItemEntry> filtered = new ArrayList<>();

        for (ItemEntry entry : items) {
            // Поиск по имени предмета
            if (entry.getName().toLowerCase().contains(lowerCaseSearch)) {
                filtered.add(entry);
            }
            // Поиск по ID предмета
            else {
                ResourceLocation key = BuiltInRegistries.ITEM.getKey(entry.getItem());
                String idString = key.toString().toLowerCase();
                if (idString.contains(lowerCaseSearch)) {
                    filtered.add(entry);
                }
            }
        }

        return filtered;
    }

    private void sortItemsWithActiveFirst(List<ItemEntry> items) {
        items.sort((a, b) -> {
            boolean aAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isItemExists(BuiltInRegistries.ITEM.getKey(a.getItem()).toString());
            boolean bAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isItemExists(BuiltInRegistries.ITEM.getKey(b.getItem()).toString());

            // Активные (добавленные) предметы идут первыми
            if (aAdded && !bAdded) {
                return -1;
            } else if (!aAdded && bAdded) {
                return 1;
            } else {
                // Если статус одинаковый, сортируем по имени
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }

    private void updateItemListWithFilter() {
        if (itemListScroll != null && allItemsMasterList != null) {
            List<ItemEntry> filtered = filterItemsBySearch(allItemsMasterList, currentItemSearchFilter);
            // Сортируем с учетом активных предметов
            sortItemsWithActiveFirst(filtered);
            itemListScroll.updateEntries(filtered);
        }
    }

    private void loadAllItems() {
        allItemsMasterList = new ArrayList<>();

        for (Item item : BuiltInRegistries.ITEM) {
            // Пропускаем блоки, оставляем только предметы
            if (item instanceof net.minecraft.world.item.BlockItem) continue;
            allItemsMasterList.add(new ItemEntry(item, new ItemStack(item)));
        }

        // Сортируем мастер-список с учетом активных предметов
        sortItemsWithActiveFirst(allItemsMasterList);

        // Применяем фильтр
        updateItemListWithFilter();
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        this.addRenderableWidget(Button.builder(
                Component.literal("← Назад"),
                button -> {
                    this.minecraft.setScreen(parentScreen);
                }

        ).bounds(centerX - this.backgroundWidth / 2, this.height - 25, this.backgroundWidth, BUTTON_HEIGHT).build());

        this.searchAllItems = new EditBox(getFontRender(), this.width / 2 - 98, this.height / 2 - 112 + 7, this.backgroundWidth - 20, 17, Component.literal("Поиск предметов"));

        // Добавляем слушатель изменений текста для поиска
        this.searchAllItems.setResponder(searchText -> {
            currentItemSearchFilter = searchText;
            updateItemListWithFilter();
        });

        this.itemListScroll = new ScrollingItemList(this.width / 2 - this.backgroundWidth / 2 + 10, this.height / 2 - 117 + 25 + 7, this.backgroundWidth - 20, this.backgroundHeight - 45, this);

        // Показываем все предметы с учетом фильтра и сортировки
        updateItemListWithFilter();

        this.addRenderableWidget(searchAllItems);
        this.addRenderableWidget(itemListScroll);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xCC000000);

        guiGraphics.blit(BACKGROUND_TEXTURE, this.width / 2 - this.backgroundWidth / 2, this.height / 2 - 125 + 7, 0, 0, this.backgroundWidth, this.backgroundHeight - 5, this.backgroundWidth, this.backgroundHeight);

        guiGraphics.drawString(
                this.font,
                this.title,
                this.width / 2 - this.font.width(this.title) / 2,
                5,
                0xFFFFFF,
                true
        );

        // Отображаем информацию о поиске
        if (!currentItemSearchFilter.isEmpty() && allItemsMasterList != null) {
            int resultsCount = itemListScroll != null ? itemListScroll.children().size() : 0;
            String info = String.format("Найдено: %d / %d", resultsCount, allItemsMasterList.size());
            guiGraphics.drawString(
                    this.font,
                    info,
                    this.width / 2 - 98,
                    this.height / 2 - 125 + 7 - 10,
                    0xAAAAAA,
                    false
            );
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Отключаем стандартный фон
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            player = null;
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public Font getFontRender() {
        return getMinecraft().font;
    }

    //==================================== Внутренние классы ================================

    public static class ItemEntry {
        private final Item item;
        private final ItemStack itemStack;
        private final String name;

        ItemEntry(Item item, ItemStack itemStack) {
            this.item = item;
            this.itemStack = itemStack;
            this.name = itemStack.getHoverName().getString();
        }

        Item getItem() { return item; }
        ItemStack getItemStack() { return itemStack; }
        String getName() { return name; }
    }

    //=================================================================


    private static class ScrollingItemList extends ObjectSelectionList<ScrollingItemList.ItemSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final TestAllItemScreen parent;

        ScrollingItemList(int x, int y, int width, int height, TestAllItemScreen parent) {
            super(Objects.requireNonNull(parent.minecraft), width, height, y, SLOT_HEIGHT);
            this.parent = parent;
            this.setX(x);
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getX() + this.width - 6;
        }

        void updateEntries(List<ItemEntry> items) {
            this.clearEntries();
            if (items != null) {
                items.forEach(item -> this.addEntry(new ItemSlot(parent, item)));
            }
        }

        static class ItemSlot extends ObjectSelectionList.Entry<ItemSlot> {
            private final TestAllItemScreen parent;
            private final ItemEntry item;
            private int currentButton1X;
            private int currentButton1Y;

            ItemSlot(TestAllItemScreen parent, ItemEntry item) {
                this.parent = parent;
                this.item = item;
            }

            private void playClickSound() {
                if (parent.minecraft != null && parent.minecraft.player != null) {
                    parent.minecraft.getSoundManager().play(
                            SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)
                    );
                }
            }

            private void onFirstButtonClick() {
                playClickSound();

                JSONSettingCreate.ElementToSettingItem(player.getUuid(), item.getItem());

                ChaosManiaMod.LOGGER.info(item.getName());

                // Обновляем список, чтобы показать изменение статуса кнопки и пересортировать предметы
                parent.updateItemListWithFilter();
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                Font font = parent.minecraft.font;
                ItemStack stack = item.getItemStack();
                String name = item.getName();

                // Фон при наведении
                if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                // Иконка предмета
                guiGraphics.renderItem(stack, left + 4, top + 14);
                guiGraphics.renderItemDecorations(font, stack, left + 4, top + 14);

                // Название предмета
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                // ID предмета
                ResourceLocation key = BuiltInRegistries.ITEM.getKey(item.getItem());
                String idString = key.toString();
                if (font.width(idString) > width - 160) {
                    idString = font.plainSubstrByWidth(idString, width - 160) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                // Две кнопки
                int buttonWidth = 28;
                int buttonHeight = 28;
                int gap = 5;
                int button1X = left + width - (buttonWidth * 2 + gap) - 4;
                int button2X = left + width - buttonWidth - 4;
                int buttonY = top + (height - buttonHeight) / 2;

                currentButton1X = button1X;
                currentButton1Y = buttonY;

                // Проверяем, добавлен ли уже предмет
                boolean isItemAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                        .isItemExists(BuiltInRegistries.ITEM.getKey(item.getItem()).toString());

                if (!isItemAdded) {
                    // Красная кнопка - предмет не добавлен
                    guiGraphics.fill(button1X, buttonY, button1X + buttonWidth, buttonY + buttonHeight, 0xFF701825);
                    guiGraphics.fill(button1X + 1, buttonY + 1, button1X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF701825);

                    // Рисуем плюсик
                    int centerX = button1X + buttonWidth / 2;
                    int centerY = buttonY + buttonHeight / 2;
                    guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
                    guiGraphics.fill(centerX - 1, centerY - 4, centerX + 1, centerY + 4, 0xFFFFFF);
                } else {
                    // Зеленая кнопка - предмет уже добавлен
                    guiGraphics.fill(button1X, buttonY, button1X + buttonWidth, buttonY + buttonHeight, 0xFF226622);
                    guiGraphics.fill(button1X + 1, buttonY + 1, button1X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF226622);

                    // Рисуем галочку
                    int centerX = button1X + buttonWidth / 2;
                    int centerY = buttonY + buttonHeight / 2;
                    guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
                    guiGraphics.fill(centerX - 1, centerY - 4, centerX + 1, centerY + 4, 0xFFFFFF);
                }

                // Вторая кнопка (пока не используется)
                int centerX2 = button2X + buttonWidth / 2;
                int centerY2 = buttonY + buttonHeight / 2;
                guiGraphics.fill(centerX2 - 6, centerY2 - 1, centerX2 + 6, centerY2 + 1, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                int buttonSize = 28;

                if (mouseX >= currentButton1X && mouseX <= currentButton1X + buttonSize &&
                        mouseY >= currentButton1Y && mouseY <= currentButton1Y + buttonSize) {
                    onFirstButtonClick();
                    return true;
                }

                return false;
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(item.getName());
            }
        }
    }
}