package net.fernyam.chaosmania.gui.custom.scrolls;

import net.fernyam.chaosmania.gui.custom.MainSettingScreen;
import net.fernyam.chaosmania.gui.custom.base.BaseScrollingList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.fernyam.chaosmania.util.SettingsHelper.*;

public class ScrollingMobList extends BaseScrollingList<MainSettingScreen.MobEntry, ScrollingMobList.MobSlot> {

    public ScrollingMobList(MainSettingScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void updateEntries(List<MainSettingScreen.MobEntry> entries) {
        this.clearEntries();
        if (entries != null) {
            entries.forEach(entry -> this.addEntry(new MobSlot(parent, entry)));
        }
    }

    public static class MobSlot extends Entry<MobSlot> {
        private final MainSettingScreen parent;
        private final MainSettingScreen.MobEntry entry;
        private final Button disableRightClickButton;
        private final Button disableLeftClickButton;

        public MobSlot(MainSettingScreen parent, MainSettingScreen.MobEntry entry) {
            this.parent = parent;
            this.entry = entry;

            String mobId = entry.getMobId();
            String uuid = MainSettingScreen.getSelectedPlayer() != null ? MainSettingScreen.getSelectedPlayer().getUuid() : null;

            this.disableRightClickButton = Button.builder(
                            Component.literal(uuid != null && canRightClickMob(uuid, mobId) ? "✔" : "✖"),
                            button -> {
                                if (uuid != null) {
                                    toggleMobRightClick(uuid, mobId);
                                    button.setMessage(Component.literal(canRightClickMob(uuid, mobId) ? "✔" : "✖"));
                                }
                            }
                    ).bounds(0, 0, 20, 20)
                    .tooltip(Tooltip.create(Component.literal("Можно ли взаимодействовать ПКМ")))
                    .build();

            this.disableLeftClickButton = Button.builder(
                            Component.literal(uuid != null && canLeftClickMob(uuid, mobId) ? "✔" : "✖"),
                            button -> {
                                if (uuid != null) {
                                    toggleMobLeftClick(uuid, mobId);
                                    button.setMessage(Component.literal(canLeftClickMob(uuid, mobId) ? "✔" : "✖"));
                                }
                            }
                    ).bounds(0, 0, 20, 20)
                    .tooltip(Tooltip.create(Component.literal("Можно ли взаимодействовать ЛКМ")))
                    .build();
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            var font = parent.getMinecraft().font;
            String id = entry.getMobId();
            String name = entry.getMobName();

            if (hovered) {
                guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
            }


            // Центрирование текста по Y
            int textStartY = top + (height - 24) / 2;

            if (font.width(name) > width - 90) {
                name = font.plainSubstrByWidth(name, width - 90) + "...";
            }
            guiGraphics.drawString(font, name, left + 28, textStartY, 0xFFFFFF, false);


            if (font.width(id) > width - 120) {
                id = font.plainSubstrByWidth(id, width - 120) + "...";
            }
            guiGraphics.drawString(font, id, left + 28, textStartY + 12, 0x888888, false);

            // Центрирование кнопок по Y
            int buttonWidth = 20;
            int buttonHeight = 20;
            int buttonX = left + width - buttonWidth - 35;
            int buttonY = top + (height - buttonHeight) / 2;

            disableRightClickButton.setX(buttonX);
            disableRightClickButton.setY(buttonY);

            disableLeftClickButton.setX(buttonX + 25);
            disableLeftClickButton.setY(buttonY);

            disableRightClickButton.render(guiGraphics, mouseX, mouseY, partialTick);
            disableLeftClickButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

//        @Override
//        public boolean mouseClicked(double mouseX, double mouseY, int button) {
//            return disableRightClickButton.mouseClicked(mouseX, mouseY, button) ||
//                    disableLeftClickButton.mouseClicked(mouseX, mouseY, button);
//        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal(entry.getMobName());
        }
    }
}