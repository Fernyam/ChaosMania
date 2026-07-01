package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.data.settings.SettingsManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Collection;
import java.util.stream.Collectors;

public class AllBlockScreen extends BaseSelectionScreen<Block> {

    public AllBlockScreen(PlayerInfoData player, MainSettingScreen parentScreen) {
        super(player, parentScreen);
    }

    @Override
    public Component getScreenTitle() {
        return Component.literal("Добавление блоков в настройки игрока " +
                (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName()));
    }

    @Override
    protected Collection<Block> getAllElements() {
        return BuiltInRegistries.BLOCK.stream()
                .filter(block -> block != Blocks.AIR)
                .filter(block -> block.asItem() instanceof BlockItem)
                .collect(Collectors.toList());
    }

    @Override
    protected String getElementId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    @Override
    protected String getElementName(Block block) {
        return block.getName().getString();
    }

    @Override
    protected void addElementToPlayer(Block block) {
        var settings = SettingsManager.getBlockSettings(player.getUuid());
        if (settings != null) {
            String id = getElementId(block);
            if (!settings.isBlockExists(id)) {
                settings.addBlock(id);
                SettingsManager.saveBlockSettings(player.getUuid());
            }
        }
    }

    @Override
    protected void removeElementFromPlayer(Block block) {
        var settings = SettingsManager.getBlockSettings(player.getUuid());
        if (settings != null) {
            String id = getElementId(block);
            if (settings.isBlockExists(id)) {
                settings.removeBlock(id);
                SettingsManager.saveBlockSettings(player.getUuid());
            }
        }
    }

    @Override
    protected boolean isElementInPlayerSettings(Block block) {
        var settings = SettingsManager.getBlockSettings(player.getUuid());
        return settings != null && settings.isBlockExists(getElementId(block));
    }

    @Override
    protected boolean shouldDisplayElement(Block block) {
        return true;
    }

    @Override
    protected boolean isSpecialIcon() {
        return false;
    }

    @Override
    protected void renderExtra(GuiGraphics gui, int left, int top, int width, int height, Block block) {
        ItemStack stack = new ItemStack(block.asItem());
        gui.renderItem(stack, left + 4, top + 14);
        gui.renderItemDecorations(minecraft.font, stack, left + 4, top + 14);
    }
}