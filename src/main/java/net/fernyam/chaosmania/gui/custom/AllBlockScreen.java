package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;


public class AllBlockScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");

    private static PlayerInfoData player;

    public static MainSettingScreen parentScreen;

    private static final int BUTTON_HEIGHT = 20;

    private static final int backgroundWidth = 216;
    private static final int backgroundHeight = 230;

    private EditBox searchAllBlock;

    private ScrollingBlockList blockListScroll;

    private List<MainSettingScreen.BlockEntry> allBlocksMasterList;
    private String currentBlockSearchFilter = "";

    // Типы поиска
    private enum SearchType {
        NAME,           // обычный поиск по имени
        ID,             // поиск по ID (префикс :)
        MOD_ID          // поиск по MOD_ID (префикс @)
    }

    private static class ParsedSearchQuery {
        SearchType type;
        String query;

        ParsedSearchQuery(SearchType type, String query) {
            this.type = type;
            this.query = query.toLowerCase().trim();
        }
    }

    public AllBlockScreen(PlayerInfoData player, MainSettingScreen parentScreen) {
        super(Component.literal("Добавление блоков в настройки игрока " + (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName())));

        this.player = player;
        this.parentScreen = parentScreen;
        this.allBlocksMasterList = new ArrayList<>();
        this.currentBlockSearchFilter = "";

        if (player == null) onClose();

        loadAllBlocks();
    }

    // ==================== Методы для поиска ====================

    private ParsedSearchQuery parseSearchQuery(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ParsedSearchQuery(SearchType.NAME, "");
        }

        String trimmed = searchText.trim();

        if (trimmed.startsWith(":")) {
            // Поиск по ID (часть после :)
            String query = trimmed.substring(1).trim();
            return new ParsedSearchQuery(SearchType.ID, query);
        }
        else if (trimmed.startsWith("@")) {
            // Поиск по MOD_ID (часть до :)
            String query = trimmed.substring(1).trim();
            return new ParsedSearchQuery(SearchType.MOD_ID, query);
        }
        else {
            // Обычный поиск по имени
            return new ParsedSearchQuery(SearchType.NAME, trimmed);
        }
    }

    private List<MainSettingScreen.BlockEntry> filterBlocksBySearch(List<MainSettingScreen.BlockEntry> blocks, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(blocks);
        }

        ParsedSearchQuery parsed = parseSearchQuery(searchText);

        if (parsed.query.isEmpty()) {
            return new ArrayList<>(blocks);
        }

        List<MainSettingScreen.BlockEntry> filtered = new ArrayList<>();

        switch (parsed.type) {
            case NAME:
                // Поиск по имени блока
                for (MainSettingScreen.BlockEntry entry : blocks) {
                    if (entry.getName().toLowerCase().contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case ID:
                // Поиск только по PATH части ID (после :)
                for (MainSettingScreen.BlockEntry entry : blocks) {
                    ResourceLocation key = BuiltInRegistries.BLOCK.getKey(entry.getBlock());
                    String path = key.getPath().toLowerCase();
                    if (path.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case MOD_ID:
                // Поиск по MOD_ID (часть до :)
                for (MainSettingScreen.BlockEntry entry : blocks) {
                    ResourceLocation key = BuiltInRegistries.BLOCK.getKey(entry.getBlock());
                    String modId = key.getNamespace().toLowerCase();
                    if (modId.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;
        }

        return filtered;
    }

    private void sortBlocksWithActiveFirst(List<MainSettingScreen.BlockEntry> blocks) {
        blocks.sort((a, b) -> {
            boolean aAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isBlockExists(BuiltInRegistries.BLOCK.getKey(a.getBlock()).toString());
            boolean bAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isBlockExists(BuiltInRegistries.BLOCK.getKey(b.getBlock()).toString());

            if (aAdded && !bAdded) {
                return -1;
            } else if (!aAdded && bAdded) {
                return 1;
            } else {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }

    private void updateBlockListWithFilter() {
        if (blockListScroll != null && allBlocksMasterList != null) {
            List<MainSettingScreen.BlockEntry> filtered = filterBlocksBySearch(allBlocksMasterList, currentBlockSearchFilter);
            sortBlocksWithActiveFirst(filtered);
            blockListScroll.updateEntries(filtered);
        }
    }

    private void loadAllBlocks() {
        allBlocksMasterList = new ArrayList<>();

        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof BlockItem)) continue;
            Block block = ((BlockItem) item).getBlock();
            if (block == Blocks.AIR) continue;
            allBlocksMasterList.add(new MainSettingScreen.BlockEntry(block, new ItemStack(item)));
        }

        sortBlocksWithActiveFirst(allBlocksMasterList);
        updateBlockListWithFilter();
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

        this.searchAllBlock = new EditBox(getFontRender(), this.width / 2 - 98, this.height / 2 - 112 + 7, this.backgroundWidth - 20, 17, Component.literal("Поиск блоков... (:id | @modid)"));

        this.searchAllBlock.setResponder(searchText -> {
            currentBlockSearchFilter = searchText;

            // Меняем цвет текста в зависимости от префикса
            if (searchText != null && !searchText.isEmpty()) {
                if (searchText.startsWith(":")) {
                    searchAllBlock.setTextColor(0xFFFF55); // Жёлтый
                } else if (searchText.startsWith("@")) {
                    searchAllBlock.setTextColor(0x55FFFF); // Голубой
                } else {
                    searchAllBlock.setTextColor(0xFFFFFF); // Белый
                }
            } else {
                searchAllBlock.setTextColor(0xFFFFFF); // Белый
            }

            updateBlockListWithFilter();
        });

        this.blockListScroll = new ScrollingBlockList(this.width / 2 - this.backgroundWidth / 2 + 10, this.height / 2 - 117 + 25 + 7, this.backgroundWidth - 20, this.backgroundHeight - 45, this);

        updateBlockListWithFilter();

        this.addRenderableWidget(searchAllBlock);
        this.addRenderableWidget(blockListScroll);
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

    //=================================================================

    private static class ScrollingBlockList extends ObjectSelectionList<ScrollingBlockList.BlockSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final AllBlockScreen parent;

        ScrollingBlockList(int x, int y, int width, int height, AllBlockScreen parent) {
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

        void updateEntries(List<MainSettingScreen.BlockEntry> blocks) {
            this.clearEntries();
            if (blocks != null) {
                blocks.forEach(block -> this.addEntry(new BlockSlot(parent, block)));
            }
        }

        static class BlockSlot extends ObjectSelectionList.Entry<BlockSlot> {
            private final AllBlockScreen parent;
            private final MainSettingScreen.BlockEntry block;
            private final Button AddBlockButton;

            BlockSlot(AllBlockScreen parent, MainSettingScreen.BlockEntry block) {
                this.parent = parent;
                this.block = block;

                this.AddBlockButton = Button.builder(
                                Component.literal(JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid()).isBlockExists(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "§c-" : "§a+"),
                                button -> {
                                    button.setMessage(Component.literal(JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid()).isBlockExists(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "§c-" : "§a+"));
                                    onButtonClick();
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Добавить блок в список")))
                        .build();
            }

            private void onButtonClick() {
                JSONSettingCreate.ElementToSettingBlock(player.getUuid(), block.getBlock());
                parent.updateBlockListWithFilter();
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

                if (font.width(name) > width - 80) {
                    name = font.plainSubstrByWidth(name, width - 80) + "...";
                }
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block.getBlock());
                String idString = key.toString();
                if (font.width(idString) > width - 100) {
                    idString = font.plainSubstrByWidth(idString, width - 100) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                int buttonWidth = 20;
                int buttonHeight = 20;
                int buttonX = left + width - buttonWidth - 35;
                int buttonY = top + (height - buttonHeight) / 2;

                AddBlockButton.setX(buttonX + 20);
                AddBlockButton.setY(buttonY);

                AddBlockButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (AddBlockButton.mouseClicked(mouseX, mouseY, button)) {
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
}