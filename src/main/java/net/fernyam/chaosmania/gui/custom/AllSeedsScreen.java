package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;
import static net.fernyam.chaosmania.ModConstants.*;

public class AllSeedsScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");

    private static PlayerInfoData player;

    public static MainSettingScreen parentScreen;

    private static final int BUTTON_HEIGHT = 20;

    private static final int backgroundWidth = 216;
    private static final int backgroundHeight = 230;


    private EditBox searchAllSeeds;

    private ScrollingItemSeedList seedListScroll;

    private List<ItemSeedEntry> allSeedsMasterList;
    private String currentSeedsSearchFilter = "";

    private Button itemButton;
    private Button blockItemButton;

    SelectType selectType;


    // Типы поиска
    private enum SearchType {
        NAME,           // обычный поиск по имени
        ID,             // поиск по ID (префикс :)
        MOD_ID          // поиск по MOD_ID (префикс @)
    }

    private enum SelectType
    {
        ITEM,
        BLOCK
    }


    private static class ParsedSearchQuery {
        SearchType type;
        String query;

        ParsedSearchQuery(SearchType type, String query) {
            this.type = type;
            this.query = query.toLowerCase().trim();
        }
    }


    protected AllSeedsScreen(PlayerInfoData player, MainSettingScreen parentScreen) {
        super(Component.literal("Добавление настройку посадки " + (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName())));
        if (player == null) onClose();

        this.player = player;
        this.parentScreen = parentScreen;
        this.allSeedsMasterList = new ArrayList<>();
        this.currentSeedsSearchFilter = "";

        selectType = SelectType.ITEM;


        loadAllItems();
    }



    private ParsedSearchQuery parseSearchQuery(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ParsedSearchQuery(SearchType.NAME, "");
        }

        String trimmed = searchText.trim();

        if (trimmed.startsWith(":")) {
            String query = trimmed.substring(1).trim();
            return new ParsedSearchQuery(SearchType.ID, query);
        } else if (trimmed.startsWith("@")) {
            String query = trimmed.substring(1).trim();
            return new ParsedSearchQuery(SearchType.MOD_ID, query);
        } else {
            return new ParsedSearchQuery(SearchType.NAME, trimmed);
        }
    }

    private List<ItemSeedEntry> filterItemsBySearch(List<ItemSeedEntry> items, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(items);
        }

        ParsedSearchQuery parsed = parseSearchQuery(searchText);

        if (parsed.query.isEmpty()) {
            return new ArrayList<>(items);
        }

        List<ItemSeedEntry> filtered = new ArrayList<>();

        switch (parsed.type) {
            case NAME:
                for (ItemSeedEntry entry : items) {
                    if (entry.getName().toLowerCase().contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case ID:
                for (ItemSeedEntry entry : items) {
                    ResourceLocation key = BuiltInRegistries.ITEM.getKey(entry.getItem());
                    String path = key.getPath().toLowerCase();
                    if (path.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case MOD_ID:
                for (ItemSeedEntry entry : items) {
                    ResourceLocation key = BuiltInRegistries.ITEM.getKey(entry.getItem());
                    String modId = key.getNamespace().toLowerCase();
                    if (modId.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;
        }

        return filtered;
    }

    private void sortItemsWithActiveFirst(List<ItemSeedEntry> items) {
        items.sort((a, b) -> {
            boolean aAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isItemExists(BuiltInRegistries.ITEM.getKey(a.getItem()).toString());
            boolean bAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isItemExists(BuiltInRegistries.ITEM.getKey(b.getItem()).toString());

            if (aAdded && !bAdded) {
                return -1;
            } else if (!aAdded && bAdded) {
                return 1;
            } else {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }

    private void updateItemListWithFilter() {
        if (seedListScroll == null) return;


        if (allSeedsMasterList != null) {
            List<ItemSeedEntry> filtered = filterItemsBySearch(allSeedsMasterList, currentSeedsSearchFilter);
            sortItemsWithActiveFirst(filtered);
            seedListScroll.updateEntries(filtered);
        }

    }

    private void loadAllItems() {
        Set<ItemSeedEntry> allItemsSet = new HashSet<>();

        BuiltInRegistries.ITEM.getTag(C_SEEDS).ifPresent(tag -> {
            for (Holder<Item> holder : tag) {
                Item item = holder.value();
                ItemSeedEntry entry = new ItemSeedEntry(item, new ItemStack(item));
                allItemsSet.add(entry);
            }
        });

        BuiltInRegistries.ITEM.getTag(C_CROPS).ifPresent(tag -> {
            for (Holder<Item> holder : tag) {
                Item item = holder.value();
                ItemSeedEntry entry = new ItemSeedEntry(item, new ItemStack(item));
                if(!allItemsSet.contains(entry))
                {
                    allItemsSet.add(entry);
                }
            }
        });

        BuiltInRegistries.ITEM.getTag(MINECRAFT_FLOWERS).ifPresent(tag -> {
            for (Holder<Item> holder : tag) {
                Item item = holder.value();
                ItemSeedEntry entry = new ItemSeedEntry(item, new ItemStack(item));
                if(!allItemsSet.contains(entry))
                {
                    allItemsSet.add(entry);
                }
            }
        });

        allSeedsMasterList = new ArrayList<>(allItemsSet);
        sortItemsWithActiveFirst(allSeedsMasterList);
        updateItemListWithFilter();
    }


    private void switchToItemType() {
        selectType = SelectType.ITEM;
        itemButton.active = false;
        blockItemButton.active = true;
        updateItemListWithFilter();
    }

    private void switchToBlockItemType() {
        selectType = SelectType.BLOCK;
        itemButton.active = true;
        blockItemButton.active = false;
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

        this.itemButton = addRenderableWidget(Button.builder(
                Component.literal("Items"),
                button -> switchToItemType()

        ).bounds(this.width / 2 - this.backgroundWidth / 2 - 45, this.height / 2 - 117 + 25 + 7, 45, BUTTON_HEIGHT).build());

        // Кнопка для BlockItem
        this.blockItemButton = addRenderableWidget(Button.builder(
                Component.literal("Blocks"), //В реализации
                button -> switchToBlockItemType()
        ).bounds(this.width / 2 - this.backgroundWidth / 2 - 45, this.height / 2 - 117 + 25 + 7 + BUTTON_HEIGHT + 5, 45, BUTTON_HEIGHT).build());

        if (selectType == SelectType.ITEM) {
            itemButton.active = false;
            blockItemButton.active = true;
        } else {
            itemButton.active = true;
            blockItemButton.active = false;
        }

        this.searchAllSeeds = new EditBox(getFontRender(), this.width / 2 - 98, this.height / 2 - 112 + 7, this.backgroundWidth - 20, 17, Component.literal("Поиск... (:id | @modid)"));

        this.searchAllSeeds.setResponder(searchText -> {
            currentSeedsSearchFilter = searchText;

            // Меняем цвет текста в зависимости от префикса
            if (searchText != null && !searchText.isEmpty()) {
                if (searchText.startsWith(":")) {
                    searchAllSeeds.setTextColor(0xFFFF55); // Жёлтый
                } else if (searchText.startsWith("@")) {
                    searchAllSeeds.setTextColor(0x55FFFF); // Голубой
                } else {
                    searchAllSeeds.setTextColor(0xFFFFFF); // Белый
                }
            } else {
                searchAllSeeds.setTextColor(0xFFFFFF); // Белый
            }

            updateItemListWithFilter();
        });

        this.seedListScroll = new ScrollingItemSeedList(this.width / 2 - this.backgroundWidth / 2 + 10, this.height / 2 - 117 + 25 + 7, this.backgroundWidth - 20, this.backgroundHeight - 45, this);

        updateItemListWithFilter();

        this.addRenderableWidget(searchAllSeeds);
        this.addRenderableWidget(seedListScroll);
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
        if (keyCode == 256) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public Font getFontRender() {
        return getMinecraft().font;
    }


    //==========================================================================

    public static class ItemSeedEntry {
        private final Item item;
        private final ItemStack itemStack;
        private final String name;

        ItemSeedEntry(Item item, ItemStack itemStack) {
            this.item = item;
            this.itemStack = itemStack;
            this.name = itemStack.getHoverName().getString();
        }

        Item getItem() { return item; }
        ItemStack getItemStack() { return itemStack; }
        String getName() { return name; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            AllSeedsScreen.ItemSeedEntry that = (AllSeedsScreen.ItemSeedEntry) obj;
            return Objects.equals(item, that.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item);
        }
    }



    private static class ScrollingItemSeedList extends ObjectSelectionList<ScrollingItemSeedList.ItemSeedSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final AllSeedsScreen parent;

        ScrollingItemSeedList(int x, int y, int width, int height, AllSeedsScreen parent) {
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

        void updateEntries(List<ItemSeedEntry> items) {
            this.clearEntries();
            if (items != null) {
                items.forEach(item -> this.addEntry(new ItemSeedSlot(parent, item)));
            }
        }

        static class ItemSeedSlot extends ObjectSelectionList.Entry<ScrollingItemSeedList.ItemSeedSlot> {
            private final AllSeedsScreen parent;
            private final ItemSeedEntry item;
            private final Button AddItemButton;

            ItemSeedSlot(AllSeedsScreen parent, ItemSeedEntry item) {
                this.parent = parent;
                this.item = item;

                this.AddItemButton = Button.builder(
                                Component.literal(JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid()).isPlantSeedExists(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "§c-" : "§a+"),
                                button -> {
                                    JSONSettingCreate.ElementToSettingSeed(player.getUuid(), item.getItem());
                                    button.setMessage(Component.literal(JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid()).isPlantSeedExists(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "§c-" : "§a+"));
                                    parent.updateItemListWithFilter();
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Добавить предмет в список")))
                        .build();
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                Font font = parent.minecraft.font;
                ItemStack stack = item.getItemStack();
                String name = item.getName();

                if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                guiGraphics.renderItem(stack, left + 4, top + 14);
                guiGraphics.renderItemDecorations(font, stack, left + 4, top + 14);

                if (font.width(name) > width - 80) {
                    name = font.plainSubstrByWidth(name, width - 80) + "...";
                }
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.ITEM.getKey(item.getItem());
                String idString = key.toString();
                if (font.width(idString) > width - 100) {
                    idString = font.plainSubstrByWidth(idString, width - 100) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                int buttonWidth = 20;
                int buttonHeight = 20;
                int buttonX = left + width - buttonWidth - 35;
                int buttonY = top + (height - buttonHeight) / 2;

                AddItemButton.setX(buttonX + 20);
                AddItemButton.setY(buttonY);

                AddItemButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (AddItemButton.mouseClicked(mouseX, mouseY, button)) {
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
