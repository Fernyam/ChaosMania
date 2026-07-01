package net.fernyam.chaosmania.gui.custom;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.fernyam.chaosmania.data.settings.SettingsManager.*;

public class ScrollingBlockList extends BaseScrollingList<MainSettingScreen.BlockEntry, ScrollingBlockList.BlockSlot> {

    public ScrollingBlockList(MainSettingScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void updateEntries(List<MainSettingScreen.BlockEntry> entries) {
        this.clearEntries();
        if (entries != null) {
            entries.forEach(entry -> this.addEntry(new BlockSlot(parent, entry)));
        }
    }

    public static class BlockSlot extends ObjectSelectionList.Entry<BlockSlot> {
        private final MainSettingScreen parent;
        private final MainSettingScreen.BlockEntry entry;
        private final Button disablePlaceButton;
        private final Button disableBreakButton;

        public BlockSlot(MainSettingScreen parent, MainSettingScreen.BlockEntry entry) {
            this.parent = parent;
            this.entry = entry;

            String blockId = BuiltInRegistries.BLOCK.getKey(entry.getBlock()).toString();
            String uuid = MainSettingScreen.getSelectedPlayer() != null ? MainSettingScreen.getSelectedPlayer().getUuid() : null;

            this.disablePlaceButton = Button.builder(
                            Component.literal(uuid != null && canPlaceBlock(uuid, blockId) ? "✔" : "✖"),
                            button -> {
                                if (uuid != null) {
                                    toggleBlockPlace(uuid, blockId);
                                    button.setMessage(Component.literal(canPlaceBlock(uuid, blockId) ? "✔" : "✖"));
                                }
                            }
                    ).bounds(0, 0, 20, 20)
                    .tooltip(Tooltip.create(Component.literal("Можно ли устанавливать блок")))
                    .build();

            this.disableBreakButton = Button.builder(
                            Component.literal(uuid != null && canBreakBlock(uuid, blockId) ? "✔" : "✖"),
                            button -> {
                                if (uuid != null) {
                                    toggleBlockBreak(uuid, blockId);
                                    button.setMessage(Component.literal(canBreakBlock(uuid, blockId) ? "✔" : "✖"));
                                }
                            }
                    ).bounds(0, 0, 20, 20)
                    .tooltip(Tooltip.create(Component.literal("Можно ли ломать блок")))
                    .build();
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            var font = parent.getMinecraft().font;
            ItemStack stack = entry.getItemStack();
            String name = entry.getName();

            if (hovered) {
                guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
            }

            guiGraphics.renderItem(stack, left + 4, top + 14);
            guiGraphics.renderItemDecorations(font, stack, left + 4, top + 14);

            if (font.width(name) > width - 90) {
                name = font.plainSubstrByWidth(name, width - 90) + "...";
            }
            guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

            ResourceLocation key = BuiltInRegistries.BLOCK.getKey(entry.getBlock());
            String idString = key.toString();
            if (font.width(idString) > width - 120) {
                idString = font.plainSubstrByWidth(idString, width - 120) + "...";
            }
            guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

            int buttonWidth = 20;
            int buttonHeight = 20;
            int buttonX = left + width - buttonWidth - 35;
            int buttonY = top + (height - buttonHeight) / 2;

            disablePlaceButton.setX(buttonX);
            disablePlaceButton.setY(buttonY);

            disableBreakButton.setX(buttonX + 25);
            disableBreakButton.setY(buttonY);

            disablePlaceButton.render(guiGraphics, mouseX, mouseY, partialTick);
            disableBreakButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return disablePlaceButton.mouseClicked(mouseX, mouseY, button) ||
                    disableBreakButton.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal(entry.getName());
        }
    }
}