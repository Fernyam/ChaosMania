package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.data.JSONSettingManager;
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
                    // Исключаем безработного (none) и бездельника (nitwit)
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

        // Если перевод не найден, используем форматированный path
        if (result.equals(translationKey)) {
            String path = id.getPath();
            result = path.substring(0, 1).toUpperCase() + path.substring(1).replace("_", " ");
        }
        return result;
    }

    @Override
    protected void addElementToPlayer(VillagerProfession profession) {
        JSONSettingManager.addVillagerProfession(player.getUuid(), profession);
    }

    @Override
    protected void removeElementFromPlayer(VillagerProfession profession) {
        JSONSettingManager.removeVillagerProfession(player.getUuid(), profession);
    }

    @Override
    protected boolean isElementInPlayerSettings(VillagerProfession profession) {
        var settings = JSONSettingManager.getSettings(player.getUuid());
        return settings != null && settings.isVillagerProfessionExists(profession);
    }

    @Override
    protected boolean shouldDisplayElement(VillagerProfession profession) {
        return true;
    }

    @Override
    protected boolean isSpecialIcon() {
        return true; // У жителей нет иконки предмета, только текст
    }

    @Override
    protected void renderExtra(GuiGraphics gui, int left, int top, int width, int height, VillagerProfession profession) {
        // Для жителей ничего не рисуем (только текст)
        // При желании можно нарисовать иконку жителя
        gui.drawString(minecraft.font, "👤", left + 8, top + 18, 0xAAAAAA, false);
    }
}