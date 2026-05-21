package net.fernyam.chaosmania.gui.custom.test;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;
import static net.fernyam.chaosmania.data.JSONSettingCreate.*;

enum ButtonSelect {
    BlockSetting,
    ItemSetting,
    SpecialSetting
}

// Класс для хранения информации об игроке
class PlayerInfoData {
    private final UUID uuid;
    private final String name;
    private final boolean isAllPlayers;

    public static final PlayerInfoData ALL_PLAYERS = new PlayerInfoData(UUID.fromString(All_UUID_PLAYER), "§6§l[ВСЕМ]", true);

    public PlayerInfoData(UUID uuid, String name) {
        this(uuid, name, false);
    }

    private PlayerInfoData(UUID uuid, String name, boolean isAllPlayers) {
        this.uuid = uuid;
        this.name = name;
        this.isAllPlayers = isAllPlayers;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public boolean isAllPlayers() { return isAllPlayers; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerInfoData that = (PlayerInfoData) obj;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}

public class TestScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");

    private static PlayerInfoData selectedPlayer;
    private static ButtonSelect selectButton;

    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 5;

    private static final int backgroundWidth = 216;
    private static final int backgroundHeight = 230;

    private ScrollingPlayerList playerListScroll;
    private ScrollingBlockList blockListScroll;
    private ScrollingItemList itemListScroll;

    private EditBox searchAllSelectBlock;

    private Button BlockButton;
    private Button ItemButton;
    private Button SpecialSettingButton;

    private Button LogButton;

    // Блоки
    private List<BlockEntry> allBlocksMasterList;
    private String currentBlockSearchFilter = "";

    // Предметы
    private List<ItemEntry> allItemsMasterList;
    private String currentItemSearchFilter = "";

    public TestScreen() {
        super(Component.empty());
        selectedPlayer = null;
        selectButton = null;
        allBlocksMasterList = new ArrayList<>();
        allItemsMasterList = new ArrayList<>();
        currentBlockSearchFilter = "";
        currentItemSearchFilter = "";
    }

    // ==================== Методы для блоков ====================

    private List<BlockEntry> filterBlocksBySearch(List<BlockEntry> blocks, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(blocks);
        }

        String lowerCaseSearch = searchText.toLowerCase().trim();
        List<BlockEntry> filtered = new ArrayList<>();

        for (BlockEntry entry : blocks) {
            if (entry.getName().toLowerCase().contains(lowerCaseSearch)) {
                filtered.add(entry);
            }
            else {
                ResourceLocation key = BuiltInRegistries.BLOCK.getKey(entry.getBlock());
                String idString = key.toString().toLowerCase();
                if (idString.contains(lowerCaseSearch)) {
                    filtered.add(entry);
                }
            }
        }

        return filtered;
    }

    private void loadSelectBlockList() {
        allBlocksMasterList = new ArrayList<>();

        if (selectedPlayer == null) return;

        var allID = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).GetAllID();

        for (String id : allID) {
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null && BuiltInRegistries.BLOCK.containsKey(location)) {
                allBlocksMasterList.add(new BlockEntry(
                        BuiltInRegistries.BLOCK.get(location),
                        new ItemStack(BuiltInRegistries.BLOCK.get(location).asItem())
                ));
            }
        }

        updateBlockListWithFilter();
    }

    private void updateBlockListWithFilter() {
        if (blockListScroll != null && allBlocksMasterList != null) {
            List<BlockEntry> filtered = filterBlocksBySearch(allBlocksMasterList, currentBlockSearchFilter);
            blockListScroll.updateEntries(filtered);
        }
    }

    // ==================== Методы для предметов ====================

    private List<ItemEntry> filterItemsBySearch(List<ItemEntry> items, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(items);
        }

        String lowerCaseSearch = searchText.toLowerCase().trim();
        List<ItemEntry> filtered = new ArrayList<>();

        for (ItemEntry entry : items) {
            if (entry.getName().toLowerCase().contains(lowerCaseSearch)) {
                filtered.add(entry);
            }
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

    private void loadSelectItemList() {
        allItemsMasterList = new ArrayList<>();

        if (selectedPlayer == null) return;

        var allID = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getAllItemIDs();

        for (String id : allID) {
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null && BuiltInRegistries.ITEM.containsKey(location)) {
                allItemsMasterList.add(new ItemEntry(
                        BuiltInRegistries.ITEM.get(location),
                        new ItemStack(BuiltInRegistries.ITEM.get(location))
                ));
            }
        }

        updateItemListWithFilter();
    }

    private void updateItemListWithFilter() {
        if (itemListScroll != null && allItemsMasterList != null) {
            List<ItemEntry> filtered = filterItemsBySearch(allItemsMasterList, currentItemSearchFilter);
            itemListScroll.updateEntries(filtered);
        }
    }

    // ==================== Общие методы ====================

    private List<PlayerInfoData> getOnlinePlayers() {
        List<PlayerInfoData> players = new ArrayList<>();

        players.add(PlayerInfoData.ALL_PLAYERS);

        if (minecraft != null && minecraft.getConnection() != null) {
            for (PlayerInfo playerInfo : minecraft.getConnection().getListedOnlinePlayers()) {
                players.add(new PlayerInfoData(playerInfo.getProfile().getId(), playerInfo.getProfile().getName()));
            }
        }

        if (players.size() == 1 && minecraft != null && minecraft.player != null) {
            players.add(new PlayerInfoData(minecraft.player.getUUID(), minecraft.player.getName().getString()));
        }

        return players;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();

        int buttonWidth = 150;
        int totalWidth = buttonWidth * 3 + BUTTON_SPACING * 2;
        int startX = (getHeight() - totalWidth) / 2;
        int startY = 5;

        BlockButton = Button.builder(
                Component.literal("Настройка блоков"),
                button -> {
                    selectButton = ButtonSelect.BlockSetting;
                    updateButtonStates();
                    init();
                }
        ).bounds(startX, startY, buttonWidth, BUTTON_HEIGHT).build();

        ItemButton = Button.builder(
                Component.literal("Настройка предметов"),
                button -> {
                    selectButton = ButtonSelect.ItemSetting;
                    updateButtonStates();
                    init();
                }
        ).bounds(startX + buttonWidth + BUTTON_SPACING, startY, buttonWidth, BUTTON_HEIGHT).build();

        SpecialSettingButton = Button.builder(
                Component.literal("Спец возможности"),
                button -> {
                    selectButton = ButtonSelect.SpecialSetting;
                    updateButtonStates();
                    init();
                }
        ).bounds(startX + (buttonWidth + BUTTON_SPACING) * 2, startY, buttonWidth, BUTTON_HEIGHT).build();

        LogButton = Button.builder(
                Component.literal("L"),
                button -> {
                    //В Реализации
                }
        ).bounds(8, getHeight() - 20, 12, 12).build();

        this.addRenderableWidget(BlockButton);
        this.addRenderableWidget(ItemButton);
        this.addRenderableWidget(SpecialSettingButton);
        this.addRenderableWidget(LogButton);

        this.playerListScroll = new ScrollingPlayerList(getHeight() / 2 - 108 - 98, getHeight() / 2 - 94, 100, getHeight() / 2 + 20, this);

        this.blockListScroll = new ScrollingBlockList(getHeight() / 2 - 102, getHeight() / 2 - 71, this.backgroundWidth - 15, this.backgroundHeight - 40, this);
        this.itemListScroll = new ScrollingItemList(getHeight() / 2 - 102, getHeight() / 2 - 71, this.backgroundWidth - 15, this.backgroundHeight - 40, this);

        this.searchAllSelectBlock = new EditBox(getFontRender(), getHeight() / 2 - 100, getHeight() / 2 - 90, this.backgroundWidth - 19, 17, Component.literal("Поиск..."));

        if (selectButton != null) {
            if (selectedPlayer != null) {
                if (this.selectButton == ButtonSelect.BlockSetting) {
                    loadSelectBlockList();

                    this.searchAllSelectBlock.setResponder(searchText -> {
                        currentBlockSearchFilter = searchText;
                        updateBlockListWithFilter();
                    });

                    addRenderableWidget(Button.builder(
                            Component.literal("Add Block"),
                            button -> {
                                this.minecraft.setScreen(new TestAllBlockScreen(selectedPlayer, this));
                            }
                    ).bounds(getHeight() / 2 + 105, getHeight() / 2 - 50, buttonWidth - 37, BUTTON_HEIGHT + 8).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Запрет на установку: " + (JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).isDisablePlaceBlock() ? "§aВКЛ" : "§cВЫКЛ")),
                            button -> {
                                boolean current = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).isDisablePlaceBlock();
                                JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).setDisablePlaceBlock(!current);
                                button.setMessage(Component.literal("Запрет на установку: " + (!current ? "§aВКЛ" : "§cВЫКЛ")));
                            }
                    ).bounds(getHeight() / 2 + 105, getHeight() / 2 - 18, buttonWidth - 37, BUTTON_HEIGHT + 3).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Запрет на ломание: " + (JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).isDisableBreakBlock() ? "§aВКЛ" : "§cВЫКЛ")),
                            button -> {
                                boolean current = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).isDisableBreakBlock();
                                JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).setDisableBreakBlock(!current);
                                button.setMessage(Component.literal("Запрет на ломание: " + (!current ? "§aВКЛ" : "§cВЫКЛ")));
                            }
                    ).bounds(getHeight() / 2 + 105, getHeight() / 2 + 12, buttonWidth - 37, BUTTON_HEIGHT + 3).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Close"),
                            button -> onClose()
                    ).bounds(getHeight() / 2 + 105, getHeight() / 2 + 73, buttonWidth - 37, BUTTON_HEIGHT).build());

                    this.addRenderableWidget(searchAllSelectBlock);
                    this.addRenderableWidget(blockListScroll);
                }
                else if (this.selectButton == ButtonSelect.ItemSetting) {
                    ChaosManiaMod.LOGGER.info("1");
                    loadSelectItemList();
                    ChaosManiaMod.LOGGER.info("2");

                    this.searchAllSelectBlock.setResponder(searchText -> {
                        currentItemSearchFilter = searchText;
                        updateItemListWithFilter();
                    });

                    ChaosManiaMod.LOGGER.info("3");

                    addRenderableWidget(Button.builder(
                            Component.literal("Add Item"),
                            button -> {
                                this.minecraft.setScreen(new TestAllItemScreen(selectedPlayer, this));
                            }
                    ).bounds(getHeight() / 2 + 105, getHeight() / 2 - 50, buttonWidth - 37, BUTTON_HEIGHT + 8).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Close"),
                            button -> onClose()
                    ).bounds(getHeight() / 2 + 105, getHeight() / 2 + 73, buttonWidth - 37, BUTTON_HEIGHT).build());

                    ChaosManiaMod.LOGGER.info("4");

                    this.addRenderableWidget(searchAllSelectBlock);
                    this.addRenderableWidget(itemListScroll);

                    ChaosManiaMod.LOGGER.info("5");
                }
            }

            playerListScroll.updateEntries(getOnlinePlayers());
            this.addRenderableWidget(playerListScroll);
        }

        updateButtonStates();
    }

    private void updateButtonStates() {
        if (BlockButton != null) {
            BlockButton.active = selectButton != ButtonSelect.BlockSetting;
        }
        if (ItemButton != null) {
            ItemButton.active = selectButton != ButtonSelect.ItemSetting;
        }
        if (SpecialSettingButton != null) {
            SpecialSettingButton.active = selectButton != ButtonSelect.SpecialSetting;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, getHeight(), getHeight(), 0xCC000000);

        if (selectButton == null) {
            String Title = "§lВыберите категорию возможностей §r";
            guiGraphics.drawString(
                    this.font,
                    Component.literal(Title),
                    getHeight() / 2 - this.font.width(Title) / 2,
                    getHeight() / 2,
                    0xE1A12D,
                    false
            );
        } else {
            guiGraphics.blit(BACKGROUND_TEXTURE, getHeight() / 2 + 75, getHeight() / 2 - 155 / 2, 0, 0, 150, 180, 150, 180);
            guiGraphics.blit(BACKGROUND_TEXTURE, getHeight() / 2 - 108, getHeight() / 2 - 100, 0, 0, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);

            if (selectedPlayer == null) {
                String Title = "§lВыберите игрока §r";
                guiGraphics.drawString(
                        this.font,
                        Component.literal(Title),
                        getHeight() / 2 - this.font.width(Title) / 2,
                        getHeight() / 2,
                        0x7F7D7A,
                        false
                );
            } else {
                String Title = "§5§lTools §r";
                guiGraphics.drawString(
                        this.font,
                        Component.literal(Title),
                        getHeight() / 2 - this.font.width(Title) / 2 + 123,
                        getHeight() / 2 - 65,
                        0xE1A12D,
                        false
                );
            }
        }

        if (selectButton == ButtonSelect.BlockSetting && selectedPlayer != null && !currentBlockSearchFilter.isEmpty() && allBlocksMasterList != null) {
            int resultsCount = blockListScroll != null ? blockListScroll.children().size() : 0;
            String info = String.format("Найдено: %d / %d", resultsCount, allBlocksMasterList.size());
            guiGraphics.drawString(
                    this.font,
                    info,
                    getHeight() / 2 - 100,
                    getHeight() / 2 - 105,
                    0xAAAAAA,
                    false
            );
        }

        if (selectButton == ButtonSelect.ItemSetting && selectedPlayer != null && !currentItemSearchFilter.isEmpty() && allItemsMasterList != null) {
            int resultsCount = itemListScroll != null ? itemListScroll.children().size() : 0;
            String info = String.format("Найдено: %d / %d", resultsCount, allItemsMasterList.size());
            guiGraphics.drawString(
                    this.font,
                    info,
                    getHeight() / 2 - 100,
                    getHeight() / 2 - 105,
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
            selectedPlayer = null;
            selectButton = null;
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

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private void selectPlayer(PlayerInfoData player) {
        selectedPlayer = player;
        currentBlockSearchFilter = "";
        currentItemSearchFilter = "";
        if (searchAllSelectBlock != null) {
            searchAllSelectBlock.setValue("");
        }
        this.init();
    }

    //==================================== Внутренние классы ================================

    public static class BlockEntry {
        private final Block block;
        private final ItemStack itemStack;
        private final String name;

        BlockEntry(Block block, ItemStack itemStack) {
            this.block = block;
            this.itemStack = itemStack;
            this.name = itemStack.getHoverName().getString();
        }

        Block getBlock() { return block; }
        ItemStack getItemStack() { return itemStack; }
        String getName() { return name; }
    }

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

    //========================================= Скроллы ===========================================

    private static class ScrollingPlayerList extends ObjectSelectionList<ScrollingPlayerList.PlayerSlot> {
        private static final int SLOT_HEIGHT = 30;
        private final TestScreen parent;

        ScrollingPlayerList(int x, int y, int width, int height, TestScreen parent) {
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

        void updateEntries(List<PlayerInfoData> players) {
            this.clearEntries();
            players.forEach(player -> this.addEntry(new PlayerSlot(parent, player)));
        }

        class PlayerSlot extends ObjectSelectionList.Entry<PlayerSlot> {
            private final TestScreen parent;
            private final PlayerInfoData player;
            private boolean isSelected;

            PlayerSlot(TestScreen parent, PlayerInfoData player) {
                this.parent = parent;
                this.player = player;
                this.isSelected = false;
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                this.isSelected = selectedPlayer != null && selectedPlayer.equals(player);

                if (isSelected) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFAA00);
                } else if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                if (player.isAllPlayers()) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x2266FF66);
                }

                if (player.isAllPlayers()) {
                    guiGraphics.fill(left + 4, top + 4, left + 24, top + 24, 0xFFAA44AA);
                    int centerX = left + 14;
                    int centerY = top + 14;
                    guiGraphics.fill(centerX - 1, centerY - 6, centerX + 1, centerY + 6, 0xFFFFFF);
                    guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
                } else {
                    guiGraphics.fill(left + 4, top + 4, left + 24, top + 24, 0xFF44AA44);
                }

                Font font = parent.minecraft.font;
                int color = isSelected ? 0xFFFFAA : 0xFFFFFF;

                Component displayName;
                if (player.isAllPlayers()) {
                    displayName = Component.literal("§6§l[ВСЕМ]");
                } else {
                    displayName = Component.literal(player.getName());
                }

                guiGraphics.drawString(font, displayName, left + 30, top + (height - 8) / 2, color, false);

                if (isSelected) {
                    guiGraphics.drawString(font, "✓", left + width - 15, top + (height - 8) / 2, 0x00FF00, false);
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                parent.selectPlayer(player);
                return true;
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(player.isAllPlayers() ? "[ВСЕМ]" : player.getName());
            }
        }
    }

    private static class ScrollingBlockList extends ObjectSelectionList<ScrollingBlockList.BlockSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final TestScreen parent;

        ScrollingBlockList(int x, int y, int width, int height, TestScreen parent) {
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
            return this.getX() + getHeight() - 6;
        }

        void updateEntries(List<BlockEntry> blocks) {
            this.clearEntries();
            if (blocks != null) {
                blocks.forEach(block -> this.addEntry(new BlockSlot(parent, block)));
            }
        }

        static class BlockSlot extends ObjectSelectionList.Entry<BlockSlot> {
            private final TestScreen parent;
            private final BlockEntry block;
            private final Button DisableplaceItemButton;
            private final Button DisableBreakItemButton;

            BlockSlot(TestScreen parent, BlockEntry block) {
                this.parent = parent;
                this.block = block;

                // Создаем нормальную кнопку
                this.DisableplaceItemButton = Button.builder(
                        Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPlaceBlock(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "✖" : "✔"),
                        button -> {
                            button.setMessage(Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPlaceBlock(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "✖" : "✔"));
                            IsPlaceSetting();
                        }
                ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить устанавливать блоки")))
                        .build();

                this.DisableBreakItemButton = Button.builder(
                        Component.literal( !JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canBreakBlock(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "✖" : "✔"),
                        button -> {
                            button.setMessage(Component.literal( !JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canBreakBlock(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "✖" : "✔"));
                            onBreakClick();
                        }
                ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить ломать блоки")))
                        .build();
            }

            private void IsPlaceSetting() {
                if (parent.minecraft != null && parent.minecraft.player != null && selectedPlayer != null) {
                    JSONSettingCreate.SwitchDisablePlaceBlock(selectedPlayer.getUuid() , BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString());
                    parent.updateItemListWithFilter();
                }
            }

            private void onBreakClick() {
                if (parent.minecraft != null && parent.minecraft.player != null && selectedPlayer != null) {
                    JSONSettingCreate.SwitchDisableBreakBlock(selectedPlayer.getUuid() , BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString());
                    parent.updateBlockListWithFilter();
                }
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                Font font = parent.minecraft.font;
                ItemStack stack = block.getItemStack();
                String name = block.getName();

                if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                guiGraphics.renderItem(stack, left + 4, top + 14);
                guiGraphics.renderItemDecorations(font, stack, left + 4, top + 14);

                if (font.width(name) > width - 90) {
                    name = font.plainSubstrByWidth(name, width - 90) + "...";
                }
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block.getBlock());
                String idString = key.toString();
                if (font.width(idString) > width - 120) {
                    idString = font.plainSubstrByWidth(idString, width - 120) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                // Позиционируем кнопку справа
                int buttonWidth = 20;
                int buttonHeight = 20;
                int buttonX = left + width - buttonWidth - 35;
                int buttonY = top + (height - buttonHeight) / 2;

                DisableplaceItemButton.setX(buttonX);
                DisableplaceItemButton.setY(buttonY);

                DisableBreakItemButton.setX(buttonX + 25);
                DisableBreakItemButton.setY(buttonY);

                // Рендерим кнопку
                DisableplaceItemButton.render(guiGraphics, mouseX, mouseY, partialTick);
                DisableBreakItemButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                // Сначала проверяем клик по кнопке
                if (DisableplaceItemButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                else if(DisableBreakItemButton.mouseClicked(mouseX, mouseY, button))
                {
                    return true;
                }
                return false;
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(block.getName());
            }

        }
    }

    private static class ScrollingItemList extends ObjectSelectionList<ScrollingItemList.ItemSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final TestScreen parent;

        ScrollingItemList(int x, int y, int width, int height, TestScreen parent) {
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
            private final TestScreen parent;
            private final ItemEntry item;
            private final Button DisableDropItemButton;
            private final Button DisablePikupItemButton;

            ItemSlot(TestScreen parent, ItemEntry item) {
                this.parent = parent;
                this.item = item;

                // Создаем нормальную кнопку
                this.DisableDropItemButton = Button.builder(
                        Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canDropItem(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"),
                        button ->
                        {
                            button.setMessage(Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canDropItem(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"));
                            IsDropSetting();
                        }
                ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить выбрасывать предмет")))
                        .build();


                this.DisablePikupItemButton = Button.builder(
                                Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPickupItem(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"),
                                button -> {
                                    button.setMessage(Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPickupItem(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"));
                                    IsPickupSetting();
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить подбирать предмет")))
                        .build();
            }

            private void IsDropSetting() {
                if (parent.minecraft != null && parent.minecraft.player != null && selectedPlayer != null) {
                    JSONSettingCreate.SwitchDisableItemDrop(selectedPlayer.getUuid() , BuiltInRegistries.ITEM.getKey(item.getItem()).toString());
                    //JSONSettingCreate.RemoveItemElement(selectedPlayer.getUuid(), item.getItem());
                    parent.updateItemListWithFilter();
                }
            }

            private void IsPickupSetting() {
                if (parent.minecraft != null && parent.minecraft.player != null && selectedPlayer != null) {
                    JSONSettingCreate.SwitchDisableItemPickup(selectedPlayer.getUuid() , BuiltInRegistries.ITEM.getKey(item.getItem()).toString());
                    //JSONSettingCreate.RemoveItemElement(selectedPlayer.getUuid(), item.getItem());
                    parent.updateItemListWithFilter();
                }
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

                if (font.width(name) > width - 90) {
                    name = font.plainSubstrByWidth(name, width - 90) + "...";
                }
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.ITEM.getKey(item.getItem());
                String idString = key.toString();
                if (font.width(idString) > width - 120) {
                    idString = font.plainSubstrByWidth(idString, width - 120) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                // Позиционируем кнопку справа
                int buttonWidth = 20;
                int buttonHeight = 20;
                int buttonX = left + width - buttonWidth - 35;
                int buttonY = top + (height - buttonHeight) / 2;

                DisableDropItemButton.setX(buttonX);
                DisableDropItemButton.setY(buttonY);

                DisablePikupItemButton.setX(buttonX + 25);
                DisablePikupItemButton.setY(buttonY);

                // Рендерим кнопки
                DisableDropItemButton.render(guiGraphics, mouseX, mouseY, partialTick);
                DisablePikupItemButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                // Сначала проверяем клик по кнопке
                if (DisableDropItemButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                else if (DisablePikupItemButton.mouseClicked(mouseX ,mouseY , button))
                {
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