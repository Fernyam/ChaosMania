package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;
import static net.fernyam.chaosmania.data.JSONSettingCreate.*;

public class AllBlocksScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");
    private static final int LIST_WIDTH = 200;
    private static final Logger log = LoggerFactory.getLogger(AllBlocksScreen.class);
    private static UUID uuidSelectedPlayer = null;

    private ScrollingPlayerList playerList;      // Слева - игроки
    private ScrollingBlockList blockList; // Справа - блоки (появляется после выбора игрока)
    private ScrollingItemList itemList;
    private EditBox searchBlockBox;
    private EditBox searchItemBox;

    private List<BlockEntry> allBlocks;
    private List<BlockEntry> filteredBlocks;
    private String lastBlockSearch = "";

    private List<ItemEntry> allItems;
    private List<ItemEntry> filteredItems;
    private String lastItemSearch = "";

    private boolean showBlockList = false;       // Показывать ли список блоков
    private boolean isShowItemList = false;      // Показывать ли список предметов



    public AllBlocksScreen() {
        super(Component.literal("§Настройка игроков"));
        loadAllBlocks();
        loadAllItems();
    }

    private void loadAllBlocks() {
        allBlocks = new ArrayList<>();

        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof BlockItem)) continue;
            Block block = ((BlockItem) item).getBlock();
            if (block == Blocks.AIR) continue;
            allBlocks.add(new BlockEntry(block, new ItemStack(item)));
        }

        allBlocks.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        filteredBlocks = new ArrayList<>(allBlocks);
    }

    private void loadAllItems() {
        allItems = new ArrayList<>();

        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BlockItem) continue; // Пропускаем блоки, только предметы
            allItems.add(new ItemEntry(new ItemStack(item)));
        }

        allItems.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        filteredItems = new ArrayList<>(allItems);
    }

    private List<String> getOnlinePlayers() {
        List<String> players = new ArrayList<>();

        // Добавляем специальную запись "ВСЕМ" в начало списка
        players.add("§6§l[ ВСЕМ ]");

        // Получаем список игроков с сервера
        if (minecraft != null && minecraft.getConnection() != null) {
            for (PlayerInfo playerInfo : minecraft.getConnection().getListedOnlinePlayers()) {
                players.add(playerInfo.getProfile().getName());
            }
        }

        // Если одиночная игра или не удалось получить список - добавляем текущего игрока
        if (players.size() == 1 && minecraft != null && minecraft.player != null) { // Только если есть только "ВСЕМ"
            players.add(minecraft.player.getName().getString());
        }

        return players;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int centerX = this.width / 2;
        int listY = 60;
        int listHeight = this.height - 110;

        // Расстояние между списками
        int gap = 20;
        int leftListX = centerX - LIST_WIDTH - gap;   // Список игроков (слева)
        int rightListX = centerX + gap;                // Список блоков (справа)

        // Список игроков (слева) - всегда виден
        this.playerList = new ScrollingPlayerList(
                leftListX + 20,
                listY,
                LIST_WIDTH - 60,
                listHeight,
                this
        );

        if(uuidSelectedPlayer != null  && !showBlockList && !isShowItemList)
        {

            this.addRenderableWidget( Button.builder(
                    Component.literal("Настройка блоков"),
                    button -> {
                        showBlockList = true;
                        isShowItemList = false;
                        init();
                    }
            ).bounds(leftListX + 180, this.height - 180, 200 , 20).build());

            //В реализации
            this.addRenderableWidget(Button.builder(
                    Component.literal("Настройка предметов"),
                    button -> {
                        isShowItemList = true;
                        showBlockList = false;
                        init();
                    }
            ).bounds(leftListX + 180, this.height - 150, 200 , 20).build());

            //Пока нет реализации
            this.addRenderableWidget(Button.builder(
                    Component.literal("Настройка возможностей"),
                    button -> this.onClose()
            ).bounds(leftListX + 180, this.height - 120, 200 , 20).build());
        }

        if (showBlockList) {
            this.blockList = new ScrollingBlockList(
                    rightListX - 60,
                    listY,
                    LIST_WIDTH + 40,
                    listHeight,
                    this
            );

            // Поле поиска (над списком блоков)
            this.searchBlockBox = new EditBox(
                    this.font,
                    rightListX - 60,
                    this.height - 35,
                    LIST_WIDTH + 40,
                    20,
                    Component.empty()
            );

            this.addRenderableWidget(Button.builder(
                    Component.literal("⚙"),
                    button -> JSONSettingCreate.IsDisableBreakBlock(uuidSelectedPlayer)
            ).bounds(leftListX + 400, 36, 17 , 17).build());

            this.addRenderableWidget(Button.builder(
                    Component.literal("⚙"),
                    button -> JSONSettingCreate.IsDisablePlaceBlock(uuidSelectedPlayer)
            ).bounds(leftListX + 375, 36, 17 , 17).build());

            this.searchBlockBox.setFocused(true);
            this.searchBlockBox.setCanLoseFocus(true);
            this.searchBlockBox.setMaxLength(50);

            // Обновляем список блоков
            blockList.updateEntries(filteredBlocks);
            this.addRenderableWidget(searchBlockBox);
            this.addRenderableWidget(blockList);
        }

        if(isShowItemList)
        {
            this.itemList = new ScrollingItemList(
                    rightListX - 60,
                    listY,
                    LIST_WIDTH + 40,
                    listHeight,
                    this
            );

            // Поле поиска для предметов
            this.searchItemBox = new EditBox(
                    this.font,
                    rightListX - 60,
                    this.height - 35,
                    LIST_WIDTH + 40,
                    20,
                    Component.empty()
            );

            this.addRenderableWidget(Button.builder(
                    Component.literal("⚙"),
                    button -> JSONSettingCreate.IsDisableItemDrop(uuidSelectedPlayer)
            ).bounds(leftListX + 400, 36, 17 , 17).build());

            this.addRenderableWidget(Button.builder(
                    Component.literal("⚙"),
                    button -> JSONSettingCreate.IsDisableItemPickup(uuidSelectedPlayer)
            ).bounds(leftListX + 375, 36, 17 , 17).build());

            this.searchItemBox.setFocused(true);
            this.searchItemBox.setCanLoseFocus(true);
            this.searchItemBox.setMaxLength(50);

            // Обновляем список предметов
            itemList.updateEntries(filteredItems);
            this.addRenderableWidget(searchItemBox);
            this.addRenderableWidget(itemList);
        }


        // Кнопка закрытия (по центру внизу)
        this.addRenderableWidget(Button.builder(
                Component.literal("§cЗакрыть"),
                button -> this.onClose()
        ).bounds(leftListX + 20, this.height - 35, 150 - 10, 20).build());

        // Обновляем список игроков
        playerList.updateEntries(getOnlinePlayers());
        this.addRenderableWidget(playerList);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Рисуем тёмный фон
        guiGraphics.fill(0, 0, this.width, this.height, 0xCC000000);

        // 2. Рисуем фоновую текстуру
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.3F);
        guiGraphics.blit(BACKGROUND_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        // 3. Заголовок
        guiGraphics.drawString(
                this.font,
                this.title,
                this.width / 2 - this.font.width(this.title) / 2,
                15,
                0xFFFF00,
                true
        );

        // 4. Надпись "Игроки онлайн" (слева)
        String playersTitle = "§lИгроки онлайн: §r";
        guiGraphics.drawString(
                this.font,
                Component.literal(playersTitle),
                this.width / 2 - LIST_WIDTH,
                40,
                0xAAAAAA,
                false
        );

        // 5. Если выбран игрок - показываем информацию и надпись над списком
        if (uuidSelectedPlayer != null) {
            if (showBlockList) {
                String blocksTitle = "§lСписок блоков: §r" + filteredBlocks.size() + " / " + allBlocks.size();
                guiGraphics.drawString(
                        this.font,
                        Component.literal(blocksTitle),
                        this.width / 2 - 40,
                        40,
                        0xAAAAAA,
                        false
                );
            } else if (isShowItemList) {
                String itemsTitle = "§lСписок предметов: §r" + filteredItems.size() + " / " + allItems.size();
                guiGraphics.drawString(
                        this.font,
                        Component.literal(itemsTitle),
                        this.width / 2 - 40,
                        40,
                        0xAAAAAA,
                        false
                );
            }
        }

        // 6. Рисуем все виджеты
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 7. Подсказка в полях поиска
        if (showBlockList && searchBlockBox != null && !searchBlockBox.isFocused() && searchBlockBox.getValue().isEmpty()) {
            guiGraphics.drawString(
                    this.font,
                    "Поиск блоков...",
                    searchBlockBox.getX() + 4,
                    searchBlockBox.getY() + 6,
                    0x888888,
                    false
            );
        }

        if (isShowItemList && searchItemBox != null && !searchItemBox.isFocused() && searchItemBox.getValue().isEmpty()) {
            guiGraphics.drawString(
                    this.font,
                    "Поиск предметов...",
                    searchItemBox.getX() + 4,
                    searchItemBox.getY() + 6,
                    0x888888,
                    false
            );
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Отключаем стандартный фон
    }

    @Override
    public void tick() {
        super.tick();

        if (showBlockList && searchBlockBox != null && !searchBlockBox.getValue().equals(lastBlockSearch)) {
            updateBlockSearch();
        }

        if (isShowItemList && searchItemBox != null && !searchItemBox.getValue().equals(lastItemSearch)) {
            updateItemSearch();
        }
    }

    private void updateBlockSearch() {
        lastBlockSearch = searchBlockBox.getValue();

        if (lastBlockSearch.isEmpty()) {
            filteredBlocks = new ArrayList<>(allBlocks);
        } else {
            filteredBlocks = allBlocks.stream()
                    .filter(entry -> entry.getName().toLowerCase().contains(lastBlockSearch.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (blockList != null) {
            blockList.updateEntries(filteredBlocks);
            blockList.setScrollAmount(0);
        }
    }

    private void updateItemSearch() {
        lastItemSearch = searchItemBox.getValue();

        if (lastItemSearch.isEmpty()) {
            filteredItems = new ArrayList<>(allItems);
        } else {
            filteredItems = allItems.stream()
                    .filter(entry -> entry.getName().toLowerCase().contains(lastItemSearch.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (itemList != null) {
            itemList.updateEntries(filteredItems);
            itemList.setScrollAmount(0);
        }
    }

    private void selectPlayer(String playerName) {
        showBlockList = false;
        isShowItemList = false;

        if (playerName.equals("§6§l[ ВСЕМ ]")) {
            uuidSelectedPlayer = UUID.fromString(All_UUID_PLAYER);
        } else {
            // На клиенте ищем по имени
            if (minecraft != null && minecraft.getConnection() != null) {
                for (PlayerInfo playerInfo : minecraft.getConnection().getListedOnlinePlayers()) {
                    if (playerInfo.getProfile().getName().equals(playerName)) {
                        uuidSelectedPlayer = playerInfo.getProfile().getId();
                        break;
                    }
                }
            }
        }
        this.init();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            uuidSelectedPlayer = null;
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

    // ==================== ВНУТРЕННИЕ КЛАССЫ ====================

    private static class BlockEntry {
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

    private static class ItemEntry {
        private final ItemStack itemStack;
        private final String name;

        ItemEntry(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.name = itemStack.getHoverName().getString();
        }

        ItemStack getItemStack() { return itemStack; }
        String getName() { return name; }
    }

    // ==================== СПИСОК ИГРОКОВ (СЛЕВА) ====================

    private static class ScrollingPlayerList extends ObjectSelectionList<ScrollingPlayerList.PlayerSlot> {
        private static final int SLOT_HEIGHT = 30;
        private final AllBlocksScreen parent;

        ScrollingPlayerList(int x, int y, int width, int height, AllBlocksScreen parent) {
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

        void updateEntries(List<String> players) {
            this.clearEntries();
            players.forEach(player -> this.addEntry(new PlayerSlot(parent, player)));
        }

        class PlayerSlot extends ObjectSelectionList.Entry<PlayerSlot> {
            private final AllBlocksScreen parent;
            private final String playerName;
            private boolean isSelected;

            PlayerSlot(AllBlocksScreen parent, String playerName) {
                this.parent = parent;
                this.playerName = playerName;
                this.isSelected = false;
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                // Проверяем, выбран ли этот игрок
                if (playerName.equals("§6§l[ ВСЕМ ]")) {
                    this.isSelected = UUID.fromString(All_UUID_PLAYER).equals(uuidSelectedPlayer);
                } else {
                    // На клиенте нельзя получить ServerPlayer!
                    // Используем данные из клиента
                    if (parent.minecraft != null && parent.minecraft.getConnection() != null) {
                        for (PlayerInfo playerInfo : parent.minecraft.getConnection().getListedOnlinePlayers()) {
                            if (playerInfo.getProfile().getName().equals(playerName)) {
                                this.isSelected = playerInfo.getProfile().getId().equals(uuidSelectedPlayer);
                                break;
                            }
                        }
                    }
                }

                // Фон если выбран
                if (isSelected) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFAA00);
                }
                // Фон при наведении (если не выбран)
                else if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                // Специальный фон для ALL_PLAYERS
                if (playerName.equals("§6§l[ ВСЕМ ]")) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x2266FF66);
                }

                // Аватар игрока (заглушка) - для ALL_PLAYERS рисуем звезду вместо головы
                if (playerName.equals("§6§l[ ВСЕМ ]")) {
                    guiGraphics.fill(left + 4, top + 4, left + 24, top + 24, 0xFFAA44AA);
                    // Рисуем звёздочку
                    int centerX = left + 14;
                    int centerY = top + 14;
                    guiGraphics.fill(centerX - 1, centerY - 6, centerX + 1, centerY + 6, 0xFFFFFF);
                    guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
                } else {
                    guiGraphics.fill(left + 4, top + 4, left + 24, top + 24, 0xFF44AA44);
                }

                // Имя игрока
                Font font = parent.minecraft.font;
                int color = isSelected ? 0xFFFFAA : 0xFFFFFF;

                // Рисуем форматированный текст
                Component displayName;
                if (playerName.equals("§6§l[ ВСЕМ ]")) {
                    displayName = Component.literal("§6§l[ ВСЕМ ]");
                } else {
                    displayName = Component.literal(playerName);
                }

                guiGraphics.drawString(font, displayName, left + 30, top + (height - 8) / 2, color, false);

                // Галочка если выбран
                if (isSelected) {
                    guiGraphics.drawString(font, "✓", left + width - 15, top + (height - 8) / 2, 0x00FF00, false);
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                // Выбираем игрока
                parent.selectPlayer(playerName);
                return true;
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(playerName);
            }
        }
    }

    // ==================== СПИСОК БЛОКОВ ====================

    private static class ScrollingBlockList extends ObjectSelectionList<ScrollingBlockList.BlockSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final AllBlocksScreen parent;

        ScrollingBlockList(int x, int y, int width, int height, AllBlocksScreen parent) {
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

        void updateEntries(List<BlockEntry> blocks) {
            this.clearEntries();
            blocks.forEach(block -> this.addEntry(new BlockSlot(parent, block)));
        }

        static class BlockSlot extends ObjectSelectionList.Entry<BlockSlot> {
            private final AllBlocksScreen parent;
            private final BlockEntry block;
            private int currentButton1X;
            private int currentButton1Y;
            private int currentButton2X;
            private int currentButton2Y;

            BlockSlot(AllBlocksScreen parent, BlockEntry block) {
                this.parent = parent;
                this.block = block;
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
                if (parent.minecraft != null && parent.minecraft.player != null) {
                    ElementToDontPlaceBlock(uuidSelectedPlayer, block.getBlock());
                }
                // Обновляем GUI чтобы показать изменения
                parent.init();
            }

            private void onSecondButtonClick() {
                playClickSound();
                if (parent.minecraft != null && parent.minecraft.player != null) {
                    ElementToDontBreakBlock(uuidSelectedPlayer, block.getBlock());
                }
                // Обновляем GUI чтобы показать изменения
                parent.init();
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                Font font = parent.minecraft.font;
                ItemStack stack = block.getItemStack();
                String name = block.getName();

                // Фон при наведении
                if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                // Иконка блока
                guiGraphics.renderItem(stack, left + 4, top + 14);
                guiGraphics.renderItemDecorations(font, stack, left + 4, top + 14);

                // Название блока
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                // ID блока
                ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block.getBlock());
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
                currentButton2X = button2X;
                currentButton2Y = buttonY;

                // Первая кнопка (запрет размещения)
                if(JSONSettingCreate.IsElementInDontPlaceBlockList(uuidSelectedPlayer , block.getBlock()))
                {
                    guiGraphics.fill(button1X, buttonY, button1X + buttonWidth, buttonY + buttonHeight, 0xFF701825);
                    guiGraphics.fill(button1X + 1, buttonY + 1, button1X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF701825);
                }
                else
                {
                    guiGraphics.fill(button1X, buttonY, button1X + buttonWidth, buttonY + buttonHeight, 0xFF226622);
                    guiGraphics.fill(button1X + 1, buttonY + 1, button1X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF226622);
                }

                int centerX = button1X + buttonWidth / 2;
                int centerY = buttonY + buttonHeight / 2;
                guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
                guiGraphics.fill(centerX - 1, centerY - 4, centerX + 1, centerY + 4, 0xFFFFFF);

                // Вторая кнопка (запрет разрушения)
                if(JSONSettingCreate.IsElementInDontBreakBlockList(uuidSelectedPlayer , block.getBlock())) {
                    guiGraphics.fill(button2X, buttonY, button2X + buttonWidth, buttonY + buttonHeight, 0xFF701825);
                    guiGraphics.fill(button2X + 1, buttonY + 1, button2X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF701825);
                }
                else
                {
                    guiGraphics.fill(button2X, buttonY, button2X + buttonWidth, buttonY + buttonHeight, 0xFF226622);
                    guiGraphics.fill(button2X + 1, buttonY + 1, button2X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF226622);
                }

                centerX = button2X + buttonWidth / 2;
                centerY = buttonY + buttonHeight / 2;
                guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                int buttonSize = 28;

                if (mouseX >= currentButton1X && mouseX <= currentButton1X + buttonSize &&
                        mouseY >= currentButton1Y && mouseY <= currentButton1Y + buttonSize) {
                    onFirstButtonClick();
                    return true;
                }

                if (mouseX >= currentButton2X && mouseX <= currentButton2X + buttonSize &&
                        mouseY >= currentButton2Y && mouseY <= currentButton2Y + buttonSize) {
                    onSecondButtonClick();
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

    // ==================== СПИСОК ПРЕДМЕТОВ ====================

    private static class ScrollingItemList extends ObjectSelectionList<ScrollingItemList.ItemSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final AllBlocksScreen parent;

        ScrollingItemList(int x, int y, int width, int height, AllBlocksScreen parent) {
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
            items.forEach(item -> this.addEntry(new ItemSlot(parent, item)));
        }

        static class ItemSlot extends ObjectSelectionList.Entry<ItemSlot> {
            private final AllBlocksScreen parent;
            private final ItemEntry item;
            private int currentButton1X;
            private int currentButton1Y;
            private int currentButton2X;
            private int currentButton2Y;

            ItemSlot(AllBlocksScreen parent, ItemEntry item) {
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
                if (parent.minecraft != null && parent.minecraft.player != null) {
                    ElementToDontDropItem(uuidSelectedPlayer, item.getItemStack().getItem());
                }
                parent.init();
            }

            private void onSecondButtonClick() {
                playClickSound();
                if (parent.minecraft != null && parent.minecraft.player != null) {
                    JSONSettingCreate.ElementToDontPuckupItem(uuidSelectedPlayer, item.getItemStack().getItem());
                }
                parent.init();
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

                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.ITEM.getKey(item.getItemStack().getItem());
                String idString = key.toString();
                if (font.width(idString) > width - 160) {
                    idString = font.plainSubstrByWidth(idString, width - 160) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                int buttonWidth = 28;
                int buttonHeight = 28;
                int gap = 5;
                int button1X = left + width - (buttonWidth * 2 + gap) - 4;
                int button2X = left + width - buttonWidth - 4;
                int buttonY = top + (height - buttonHeight) / 2;

                currentButton1X = button1X;
                currentButton1Y = buttonY;
                currentButton2X = button2X;
                currentButton2Y = buttonY;

                // Первая кнопка (запрет выбрасывания)
                if(JSONSettingCreate.IsElementInDontDropItemList(uuidSelectedPlayer , item.getItemStack().getItem()))
                {
                    guiGraphics.fill(button1X, buttonY, button1X + buttonWidth, buttonY + buttonHeight, 0xFF701825);
                    guiGraphics.fill(button1X + 1, buttonY + 1, button1X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF701825);
                }
                else
                {
                    guiGraphics.fill(button1X, buttonY, button1X + buttonWidth, buttonY + buttonHeight, 0xFF226622);
                    guiGraphics.fill(button1X + 1, buttonY + 1, button1X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF226622);
                }

                int centerX = button1X + buttonWidth / 2;
                int centerY = buttonY + buttonHeight / 2;
                guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
                guiGraphics.fill(centerX - 1, centerY - 4, centerX + 1, centerY + 4, 0xFFFFFF);

                // Вторая кнопка (запрет подбора)
                if(JSONSettingCreate.IsElementInDontPuckupItemList(uuidSelectedPlayer , item.getItemStack().getItem())) {
                    guiGraphics.fill(button2X, buttonY, button2X + buttonWidth, buttonY + buttonHeight, 0xFF701825);
                    guiGraphics.fill(button2X + 1, buttonY + 1, button2X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF701825);
                }
                else
                {
                    guiGraphics.fill(button2X, buttonY, button2X + buttonWidth, buttonY + buttonHeight, 0xFF226622);
                    guiGraphics.fill(button2X + 1, buttonY + 1, button2X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF226622);
                }

                centerX = button2X + buttonWidth / 2;
                centerY = buttonY + buttonHeight / 2;
                guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                int buttonSize = 28;

                if (mouseX >= currentButton1X && mouseX <= currentButton1X + buttonSize &&
                        mouseY >= currentButton1Y && mouseY <= currentButton1Y + buttonSize) {
                    onFirstButtonClick();
                    return true;
                }

                if (mouseX >= currentButton2X && mouseX <= currentButton2X + buttonSize &&
                        mouseY >= currentButton2Y && mouseY <= currentButton2Y + buttonSize) {
                    onSecondButtonClick();
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