package net.fernyam.chaosmania.gui.custom;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScrollingPlayerList extends ObjectSelectionList<ScrollingPlayerList.PlayerSlot> {
    private static final int SLOT_HEIGHT = 30;
    private final MainSettingScreen parent;

    public ScrollingPlayerList(int x, int y, int width, int height, MainSettingScreen parent) {
        super(parent.getMinecraft(), width, height, y, SLOT_HEIGHT);
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

    public void updateEntries(List<PlayerInfoData> players) {
        this.clearEntries();
        if (players != null) {
            players.forEach(player -> this.addEntry(new PlayerSlot(parent, player)));
        }
    }

    public static class PlayerSlot extends ObjectSelectionList.Entry<PlayerSlot> {
        private final MainSettingScreen parent;
        private final PlayerInfoData player;
        private boolean isSelected;

        public PlayerSlot(MainSettingScreen parent, PlayerInfoData player) {
            this.parent = parent;
            this.player = player;
            this.isSelected = false;
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            PlayerInfoData selected = MainSettingScreen.getSelectedPlayer();
            this.isSelected = selected != null && selected.equals(player);

            if (isSelected) {
                guiGraphics.fill(left, top, left + width, top + height, 0x44FFAA00);
            } else if (hovered) {
                guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
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

            Font font = parent.getMinecraft().font;
            int color = isSelected ? 0xFFFFAA : 0xFFFFFF;

            Component displayName = player.isAllPlayers()
                    ? Component.literal("§6§l[ВСЕМ]")
                    : Component.literal(player.getName());

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