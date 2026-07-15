package net.fernyam.chaosmania.gui.custom.screen;

import net.fernyam.chaosmania.data.settings.SettingsManager;
import net.fernyam.chaosmania.gui.custom.base.BaseSelectionScreen;
import net.fernyam.chaosmania.gui.custom.MainSettingScreen;
import net.fernyam.chaosmania.gui.PlayerInfoData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;

import java.util.Collection;
import java.util.stream.Collectors;

public class AllVillagerScreen extends BaseSelectionScreen<VillagerProfession> {

    public AllVillagerScreen(PlayerInfoData player, MainSettingScreen parentScreen) {
        super(player, parentScreen);
    }

    @Override
    public Component getScreenTitle() {
        return Component.literal("Добавление профессий жителей в настройки игрока " +
                (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName()));
    }

    @Override
    protected Collection<VillagerProfession> getAllElements() {
        return BuiltInRegistries.VILLAGER_PROFESSION.stream()
                .filter(profession -> {
                    ResourceLocation id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
                    String path = id.getPath();
                    return !path.equals("nitwit") && !path.equals("none");
                })
                .collect(Collectors.toList());
    }

    @Override
    protected String getElementId(VillagerProfession profession) {
        return BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
    }

    @Override
    protected String getElementName(VillagerProfession profession) {
        ResourceLocation id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
        String translationKey = "entity." + id.getNamespace() + ".villager." + id.getPath();
        Component translated = Component.translatable(translationKey);
        String result = translated.getString();

        if (result.equals(translationKey)) {
            String path = id.getPath();
            result = path.substring(0, 1).toUpperCase() + path.substring(1).replace("_", " ");
        }
        return result;
    }

    @Override
    protected void addElementToPlayer(VillagerProfession profession) {
        var settings = SettingsManager.getVillagerSettings(player.getUuid());
        if (settings != null) {
            String id = getElementId(profession);
            if (!settings.isProfessionExists(id)) {
                settings.addProfession(id);
                SettingsManager.saveVillagerSettings(player.getUuid());
            }
        }
    }

    @Override
    protected void removeElementFromPlayer(VillagerProfession profession) {
        var settings = SettingsManager.getVillagerSettings(player.getUuid());
        if (settings != null) {
            String id = getElementId(profession);
            if (settings.isProfessionExists(id)) {
                settings.removeProfession(id);
                SettingsManager.saveVillagerSettings(player.getUuid());
            }
        }
    }

    @Override
    protected boolean isElementInPlayerSettings(VillagerProfession profession) {
        var settings = SettingsManager.getVillagerSettings(player.getUuid());
        return settings != null && settings.isProfessionExists(getElementId(profession));
    }

    @Override
    protected boolean shouldDisplayElement(VillagerProfession profession) {
        return true;
    }

    @Override
    protected boolean isSpecialIcon() {
        return true;
    }

    @Override
    protected void renderExtra(GuiGraphics gui, int left, int top, int width, int height, VillagerProfession profession) {
        gui.drawString(minecraft.font, "👤", left + 8, top + 18, 0xAAAAAA, false);
    }
}