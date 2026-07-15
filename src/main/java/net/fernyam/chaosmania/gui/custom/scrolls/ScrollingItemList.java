package net.fernyam.chaosmania.gui.custom.scrolls;

import net.fernyam.chaosmania.gui.custom.base.BaseScrollingList;
import net.fernyam.chaosmania.gui.custom.MainSettingScreen;
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

import static net.fernyam.chaosmania.util.SettingsHelper.*;

public class ScrollingItemList extends BaseScrollingList<MainSettingScreen.ItemEntry, ScrollingItemList.ItemSlot> {

    public ScrollingItemList(MainSettingScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void updateEntries(List<MainSettingScreen.ItemEntry> entries) {
        this.clearEntries();
        if (entries != null) {
            entries.forEach(entry -> this.addEntry(new ItemSlot(parent, entry)));
        }
    }

    public static class ItemSlot extends ObjectSelectionList.Entry<ItemSlot> {
        private final MainSettingScreen parent;
        private final MainSettingScreen.ItemEntry entry;
        private final Button disableDropButton;
        private final Button disablePickupButton;

        public ItemSlot(MainSettingScreen parent, MainSettingScreen.ItemEntry entry) {
            this.parent = parent;
            this.entry = entry;

            String itemId = BuiltInRegistries.ITEM.getKey(entry.getItem()).toString();
            String uuid = MainSettingScreen.getSelectedPlayer() != null ? MainSettingScreen.getSelectedPlayer().getUuid() : null;

            this.disableDropButton = Button.builder(
                            Component.literal(uuid != null && canDropItem(uuid, itemId) ? "✔" : "✖"),
                            button -> {
                                if (uuid != null) {
                                    toggleItemDrop(uuid, itemId);
                                    button.setMessage(Component.literal(canDropItem(uuid, itemId) ? "✔" : "✖"));
                                }
                            }
                    ).bounds(0, 0, 20, 20)
                    .tooltip(Tooltip.create(Component.literal("Можно ли выбрасывать предмет")))
                    .build();

            this.disablePickupButton = Button.builder(
                            Component.literal(uuid != null && canPickupItem(uuid, itemId) ? "✔" : "✖"),
                            button -> {
                                if (uuid != null) {
                                    toggleItemPickup(uuid, itemId);
                                    button.setMessage(Component.literal(canPickupItem(uuid, itemId) ? "✔" : "✖"));
                                }
                            }
                    ).bounds(0, 0, 20, 20)
                    .tooltip(Tooltip.create(Component.literal("Можно ли подбирать предмет")))
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

            // Центрирование иконки по Y
            int iconSize = 16;
            int iconY = top + (height - iconSize) / 2;
            guiGraphics.renderItem(stack, left + 4, iconY);
            guiGraphics.renderItemDecorations(font, stack, left + 4, iconY);

            // Центрирование текста по Y
            int textStartY = top + (height - 24) / 2;

            if (font.width(name) > width - 90) {
                name = font.plainSubstrByWidth(name, width - 90) + "...";
            }
            guiGraphics.drawString(font, name, left + 28, textStartY, 0xFFFFFF, false);

            ResourceLocation key = BuiltInRegistries.ITEM.getKey(entry.getItem());
            String idString = key.toString();
            if (font.width(idString) > width - 120) {
                idString = font.plainSubstrByWidth(idString, width - 120) + "...";
            }
            guiGraphics.drawString(font, idString, left + 28, textStartY + 12, 0x888888, false);

            // Центрирование кнопок по Y
            int buttonWidth = 20;
            int buttonHeight = 20;
            int buttonX = left + width - buttonWidth - 35;
            int buttonY = top + (height - buttonHeight) / 2;

            disableDropButton.setX(buttonX);
            disableDropButton.setY(buttonY);

            disablePickupButton.setX(buttonX + 25);
            disablePickupButton.setY(buttonY);

            disableDropButton.render(guiGraphics, mouseX, mouseY, partialTick);
            disablePickupButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

//        @Override
//        public boolean mouseClicked(double mouseX, double mouseY, int button) {
//            return disableDropButton.mouseClicked(mouseX, mouseY, button) ||
//                    disablePickupButton.mouseClicked(mouseX, mouseY, button);
//        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal(entry.getName());
        }
    }
}