package net.fernyam.chaosmania.gui.custom;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.fernyam.chaosmania.data.settings.SettingsManager.*;

public class ScrollingVillagerList extends BaseScrollingList<MainSettingScreen.ProfessionVillagerEntry, ScrollingVillagerList.VillagerSlot> {

    public ScrollingVillagerList(MainSettingScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void updateEntries(List<MainSettingScreen.ProfessionVillagerEntry> entries) {
        this.clearEntries();
        if (entries != null) {
            entries.forEach(entry -> this.addEntry(new VillagerSlot(parent, entry)));
        }
    }

    public static class VillagerSlot extends ObjectSelectionList.Entry<VillagerSlot> {
        private final MainSettingScreen parent;
        private final MainSettingScreen.ProfessionVillagerEntry entry;
        private final Button disableTradeButton;

        public VillagerSlot(MainSettingScreen parent, MainSettingScreen.ProfessionVillagerEntry entry) {
            this.parent = parent;
            this.entry = entry;

            String uuid = MainSettingScreen.getSelectedPlayer() != null ? MainSettingScreen.getSelectedPlayer().getUuid() : null;

            this.disableTradeButton = Button.builder(
                            Component.literal(uuid != null && canTradeWithVillager(uuid, entry.getId()) ? "✔" : "✖"),
                            button -> {
                                if (uuid != null) {
                                    toggleVillagerTrade(uuid, entry.getId());
                                    button.setMessage(Component.literal(canTradeWithVillager(uuid, entry.getId()) ? "✔" : "✖"));
                                }
                            }
                    ).bounds(0, 0, 20, 20)
                    .tooltip(Tooltip.create(Component.literal("Можно ли торговать")))
                    .build();
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            var font = parent.getMinecraft().font;
            String name = entry.getName();

            if (hovered) {
                guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
            }

            if (font.width(name) > width - 90) {
                name = font.plainSubstrByWidth(name, width - 90) + "...";
            }
            guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

            ResourceLocation key = BuiltInRegistries.VILLAGER_PROFESSION.getKey(entry.getProfession());
            String idString = key.toString();
            if (font.width(idString) > width - 120) {
                idString = font.plainSubstrByWidth(idString, width - 120) + "...";
            }
            guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

            int buttonWidth = 20;
            int buttonHeight = 20;
            int buttonX = left + width - buttonWidth - 35;
            int buttonY = top + (height - buttonHeight) / 2;

            disableTradeButton.setX(buttonX + 25);
            disableTradeButton.setY(buttonY);

            disableTradeButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return disableTradeButton.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal(entry.getName());
        }
    }
}