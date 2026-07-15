package net.fernyam.chaosmania.gui.custom.screen;

import net.fernyam.chaosmania.data.settings.SettingsManager;
import net.fernyam.chaosmania.gui.PlayerInfoData;
import net.fernyam.chaosmania.gui.custom.MainSettingScreen;
import net.fernyam.chaosmania.gui.custom.base.BaseSelectionScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.Collection;
import java.util.stream.Collectors;

public class AllMobScreen extends BaseSelectionScreen<EntityType<?>>
{
    public AllMobScreen(PlayerInfoData player, MainSettingScreen parentScreen) {
        super(player, parentScreen);
    }

    @Override
    public Component getScreenTitle() {
        return Component.literal("Добавление мобов в настройки игрока " +
                (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName()));
    }

    @Override
    protected Collection<EntityType<?>> getAllElements() {

        return BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(entityType -> entityType.getCategory() != MobCategory.MISC)
                .collect(Collectors.toList());
    }

    @Override
    protected String getElementId(EntityType<?> mob) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(mob).toString();
    }

    @Override
    protected String getElementName(EntityType<?> mob) {
        return mob.getDescription().getString();
    }

    @Override
    protected void addElementToPlayer(EntityType<?> mob) {
        var settings = SettingsManager.getMobSettings(player.getUuid());
        if (settings != null) {
            String id = getElementId(mob);
            if (!settings.isMobExists(id)) {
                settings.addMob(id);
                SettingsManager.saveMobSettings(player.getUuid());
            }
        }
    }

    @Override
    protected void removeElementFromPlayer(EntityType<?> mob) {
        var settings = SettingsManager.getMobSettings(player.getUuid());
        if (settings != null) {
            String id = getElementId(mob);
            if (settings.isMobExists(id)) {
                settings.removeMob(id);
                SettingsManager.saveMobSettings(player.getUuid());
            }
        }
    }

    @Override
    protected boolean isElementInPlayerSettings(EntityType<?> mob) {
        var settings = SettingsManager.getMobSettings(player.getUuid());
        return settings != null && settings.isMobExists(getElementId(mob));
    }

    @Override
    protected boolean shouldDisplayElement(EntityType<?> block) {
        return true;
    }

    @Override
    protected boolean isSpecialIcon() {
        return false;
    }

    @Override
    protected void renderExtra(GuiGraphics gui, int left, int top, int width, int height, EntityType<?> block) {
        gui.drawString(minecraft.font, "👤", left + 8, top + 18, 0xAAAAAA, false);
    }
}
