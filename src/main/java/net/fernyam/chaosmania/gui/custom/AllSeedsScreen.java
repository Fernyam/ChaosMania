package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.data.JSONSettingManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static net.fernyam.chaosmania.ModConstants.*;

public class AllSeedsScreen extends BaseSelectionScreen<Item> {

    public AllSeedsScreen(PlayerInfoData player, MainSettingScreen parentScreen) {
        super(player, parentScreen);
    }

    @Override
    public Component getScreenTitle() {
        return Component.literal("Добавление семян в настройки игрока " +
                (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName()));
    }

    @Override
    protected Collection<Item> getAllElements() {
        Set<Item> seedsSet = new HashSet<>();

        // Все предметы из тега c:seeds
        BuiltInRegistries.ITEM.getTag(C_SEEDS).ifPresent(tag -> {
            for (Holder<Item> holder : tag) {
                seedsSet.add(holder.value());
            }
        });

        // Все предметы из тега c:crops
        BuiltInRegistries.ITEM.getTag(C_CROPS).ifPresent(tag -> {
            for (Holder<Item> holder : tag) {
                seedsSet.add(holder.value());
            }
        });

        // Все предметы из тега minecraft:flowers
        BuiltInRegistries.ITEM.getTag(MINECRAFT_FLOWERS).ifPresent(tag -> {
            for (Holder<Item> holder : tag) {
                seedsSet.add(holder.value());
            }
        });

        return seedsSet;
    }

    @Override
    protected String getElementId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    @Override
    protected String getElementName(Item item) {
        return new ItemStack(item).getHoverName().getString();
    }

    @Override
    protected void addElementToPlayer(Item item) {
        JSONSettingManager.toggleSeedInList(player.getUuid(), item);
    }

    @Override
    protected void removeElementFromPlayer(Item item) {
        JSONSettingManager.toggleSeedInList(player.getUuid(), item);
    }

    @Override
    protected boolean isElementInPlayerSettings(Item item) {
        var settings = JSONSettingManager.getSettings(player.getUuid());
        return settings != null && settings.isSeedExists(getElementId(item));
    }

    @Override
    protected boolean shouldDisplayElement(Item item) {
        return true;
    }

    @Override
    protected boolean isSpecialIcon() {
        return false;
    }

    @Override
    protected void renderExtra(GuiGraphics gui, int left, int top, int width, int height, Item item) {
        ItemStack stack = new ItemStack(item);
        gui.renderItem(stack, left + 4, top + 14);
        gui.renderItemDecorations(minecraft.font, stack, left + 4, top + 14);
    }
}