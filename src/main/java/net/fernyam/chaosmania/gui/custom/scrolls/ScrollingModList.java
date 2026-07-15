package net.fernyam.chaosmania.gui.custom.scrolls;

import net.fernyam.chaosmania.data.settings.SettingsManager;
import net.fernyam.chaosmania.gui.custom.base.BaseScrollingList;
import net.fernyam.chaosmania.gui.custom.MainSettingScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.fernyam.chaosmania.util.SettingsHelper.*;

public class ScrollingModList extends BaseScrollingList<MainSettingScreen.ModEntry, ScrollingModList.ModSlot> {

    private static final int SLOT_HEIGHT = 40;

    public ScrollingModList(MainSettingScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height , SLOT_HEIGHT);
    }

    @Override
    public void updateEntries(List<MainSettingScreen.ModEntry> entries) {
        this.clearEntries();
        if (entries != null) {
            entries.forEach(entry -> this.addEntry(new ModSlot(parent, entry)));
        }
    }

    public static class ModSlot extends Entry<ModSlot> {
        private final MainSettingScreen parent;
        private final MainSettingScreen.ModEntry entry;
        private final Button disableLoadButton;
        private final Button deleteModButton;

        public ModSlot(MainSettingScreen parent, MainSettingScreen.ModEntry entry) {
            this.parent = parent;
            this.entry = entry;

            String modId = entry.getModId();
            String uuid = MainSettingScreen.getSelectedPlayer() != null ? MainSettingScreen.getSelectedPlayer().getUuid() : null;

            this.disableLoadButton = Button.builder(
                            Component.literal(uuid != null && canLoadMod(uuid, modId) ? "✔" : "✖"),
                            button -> {
                                if (uuid != null) {
                                    toggleModLoad(uuid, modId);
                                    button.setMessage(Component.literal(canLoadMod(uuid, modId) ? "✔" : "✖"));
                                }
                            }
                    ).bounds(0, 0, 20, 20)
                    .tooltip(Tooltip.create(Component.literal("Можно ли играть с модом")))
                    .build();

            this.deleteModButton = Button.builder(
                    Component.literal("§c-"),
                    button -> {
                        var settings = SettingsManager.getModSettings(uuid);
                        if (settings != null) {
                            if (settings.isModExists(entry.getModId())) {
                                settings.removeMod(entry.getModId());
                                SettingsManager.saveModSettings(uuid);
                            }
                        }
                        parent.init();
                    }
            ).bounds(0, 0, 20, 20)
                    .tooltip(Tooltip.create(Component.literal("Удалить мод из настроек")))
                    .build();
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            var font = parent.getMinecraft().font;
            String modId = entry.getModId();

            if (hovered) {
                guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
            }

            // Центрирование текста по Y
            int textY = top + (height - 8) / 2; // 8 - высота строки

            if (font.width(modId) > width - 90) {
                modId = font.plainSubstrByWidth(modId, width - 90) + "...";
            }
            guiGraphics.drawString(font, modId, left + 28, textY, 0xFFFFFF, false);

            // Центрирование кнопок по Y
            int buttonWidth = 20;
            int buttonHeight = 20;
            int buttonX = left + width - buttonWidth - 35;
            int buttonY = top + (height - buttonHeight) / 2;

            disableLoadButton.setX(buttonX);
            disableLoadButton.setY(buttonY);

            deleteModButton.setX(buttonX + 25);
            deleteModButton.setY(buttonY);

            disableLoadButton.render(guiGraphics, mouseX, mouseY, partialTick);
            deleteModButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

//        @Override
//        public boolean mouseClicked(double mouseX, double mouseY, int button) {
//            return disableLoadButton.mouseClicked(mouseX, mouseY, button) ||
//                    deleteModButton.mouseClicked(mouseX , mouseY , button);
//        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal(entry.getModId());
        }
    }
}