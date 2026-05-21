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


public class TestAllBlockScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");

    private static PlayerInfoData player;

    public static TestScreen parentScreen;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 20;

    private static final int backgroundWidth = 216;
    private static final int backgroundHeight = 230;

    private EditBox searchAllBlock;

    private ScrollingBlockList blockListScroll;

    private List<TestScreen.BlockEntry> allBlocksMasterList;
    private String currentBlockSearchFilter = "";

    public TestAllBlockScreen(PlayerInfoData player, TestScreen parentScreen) {
        super(Component.literal("Добавление блоков в настройки игрока " + (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName())));

        this.player = player;
        this.parentScreen = parentScreen;
        this.allBlocksMasterList = new ArrayList<>();
        this.currentBlockSearchFilter = "";

        if (player == null) onClose();

        loadAllBlocks();
    }

    private List<TestScreen.BlockEntry> filterBlocksBySearch(List<TestScreen.BlockEntry> blocks, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(blocks);
        }

        String lowerCaseSearch = searchText.toLowerCase().trim();
        List<TestScreen.BlockEntry> filtered = new ArrayList<>();

        for (TestScreen.BlockEntry entry : blocks) {
            // Поиск по имени блока
            if (entry.getName().toLowerCase().contains(lowerCaseSearch)) {
                filtered.add(entry);
            }
            // Поиск по ID блока
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

    private void sortBlocksWithActiveFirst(List<TestScreen.BlockEntry> blocks) {
        blocks.sort((a, b) -> {
            boolean aAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isBlockExists(BuiltInRegistries.BLOCK.getKey(a.getBlock()).toString());
            boolean bAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isBlockExists(BuiltInRegistries.BLOCK.getKey(b.getBlock()).toString());

            // Активные (добавленные) блоки идут первыми
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

    private void updateBlockListWithFilter() {
        if (blockListScroll != null && allBlocksMasterList != null) {
            List<TestScreen.BlockEntry> filtered = filterBlocksBySearch(allBlocksMasterList, currentBlockSearchFilter);
            // Сортируем с учетом активных блоков
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
            allBlocksMasterList.add(new TestScreen.BlockEntry(block, new ItemStack(item)));
        }

        // Сортируем мастер-список с учетом активных блоков
        sortBlocksWithActiveFirst(allBlocksMasterList);

        // Применяем фильтр
        updateBlockListWithFilter();
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height - this.height / 90;

        this.addRenderableWidget(Button.builder(
                Component.literal("← Назад"),
                button -> {
                    this.minecraft.setScreen(parentScreen);
                }

        ).bounds(centerX - this.backgroundWidth / 2, this.height - 25, this.backgroundWidth, BUTTON_HEIGHT).build());

        this.searchAllBlock = new EditBox(getFontRender(), this.width / 2 - 98, this.height / 2 - 112 + 7, this.backgroundWidth - 20, 17, Component.literal("Поиск блоков"));

        // Добавляем слушатель изменений текста для поиска
        this.searchAllBlock.setResponder(searchText -> {
            currentBlockSearchFilter = searchText;
            updateBlockListWithFilter();
        });

        this.blockListScroll = new ScrollingBlockList(this.width / 2 - this.backgroundWidth / 2 + 10, this.height / 2 - 117 + 25 + 7, this.backgroundWidth - 20, this.backgroundHeight - 45, this);

        // Показываем все блоки с учетом фильтра и сортировки
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
        if (keyCode == 256) { // ESC
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
        private final TestAllBlockScreen parent;

        ScrollingBlockList(int x, int y, int width, int height, TestAllBlockScreen parent) {
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

        void updateEntries(List<TestScreen.BlockEntry> blocks) {
            this.clearEntries();
            if (blocks != null) {
                blocks.forEach(block -> this.addEntry(new ScrollingBlockList.BlockSlot(parent, block)));
            }
        }

        static class BlockSlot extends ObjectSelectionList.Entry<ScrollingBlockList.BlockSlot> {
            private final TestAllBlockScreen parent;
            private final TestScreen.BlockEntry block;
            private int currentButton1X;
            private int currentButton1Y;

            BlockSlot(TestAllBlockScreen parent, TestScreen.BlockEntry block) {
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

                JSONSettingCreate.ElementToSettingBlock(player.getUuid(), block.getBlock());

                ChaosManiaMod.LOGGER.info(block.getName());

                // Обновляем список, чтобы показать изменение статуса кнопки и пересортировать блоки
                parent.updateBlockListWithFilter();
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

                // Проверяем, добавлен ли уже блок
                boolean isBlockAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                        .isBlockExists(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString());

                if(!isBlockAdded)
                {
                    // Красная кнопка - блок не добавлен
                    guiGraphics.fill(button1X, buttonY, button1X + buttonWidth, buttonY + buttonHeight, 0xFF701825);
                    guiGraphics.fill(button1X + 1, buttonY + 1, button1X + buttonWidth - 1, buttonY + buttonHeight - 1, 0xFF701825);

                    // Рисуем плюсик
                    int centerX = button1X + buttonWidth / 2;
                    int centerY = buttonY + buttonHeight / 2;
                    guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
                    guiGraphics.fill(centerX - 1, centerY - 4, centerX + 1, centerY + 4, 0xFFFFFF);
                }
                else
                {
                    // Зеленая кнопка - блок уже добавлен
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
                return Component.literal(block.getName());
            }
        }
    }
}