package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.data.settings.SettingsManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.fernyam.chaosmania.data.settings.SettingsManager.*;

public enum ToggleButtonType {
    BLOCK_PLACE("Контроль установки",
            SettingsManager::toggleGlobalBlockPlace,
            uuid -> getBlockSettings(uuid).isBlockPlaceControlEnabled()),

    BLOCK_BREAK("Контроль ломания",
            SettingsManager::toggleGlobalBlockBreak,
            uuid -> getBlockSettings(uuid).isBlockBreakControlEnabled()),

    ITEM_DROP("Контроль выбрасывания",
            SettingsManager::toggleGlobalItemDrop,
            uuid -> getItemSettings(uuid).isItemDropControlEnabled()),

    ITEM_PICKUP("Контроль подбора",
            SettingsManager::toggleGlobalItemPickup,
            uuid -> getItemSettings(uuid).isItemPickupControlEnabled()),

    SEED_PLANT("Контроль посадки",
            SettingsManager::toggleGlobalSeedPlanting,
            uuid -> getSeedSettings(uuid).isSeedPlantControlEnabled()),

    VILLAGER_TRADE("Контроль торговли",
            SettingsManager::toggleGlobalVillagerTrade,
            uuid -> getVillagerSettings(uuid).isVillagerTradeControlEnabled()),

    WANDERING_TRADER("Контроль торговли со странствующими",
            SettingsManager::toggleGlobalWanderingTraderTrade,
            uuid -> getVillagerSettings(uuid).isWanderingTraderControlEnabled()),

    MOD_LOADING("Контроль за модами",
            SettingsManager::toggleGlobalModLoad,
            uuid -> getModSettings(uuid).isModLoadControlEnabled());

    private final String displayName;
    private final Consumer<String> toggleAction;
    private final Function<String, Boolean> stateGetter;

    ToggleButtonType(String displayName, Consumer<String> toggleAction, Function<String, Boolean> stateGetter) {
        this.displayName = displayName;
        this.toggleAction = toggleAction;
        this.stateGetter = stateGetter;
    }

    public Button createButton(String uuid, int x, int y, int width, int height) {
        return Button.builder(
                Component.literal(displayName + ": " + (stateGetter.apply(uuid) ? "§aВКЛ" : "§cВЫКЛ")),
                button -> {
                    toggleAction.accept(uuid);

                    var text = Component.literal(displayName + ": " + (stateGetter.apply(uuid) ? "§aВКЛ" : "§cВЫКЛ"));
                    button.setMessage(text);
                    button.setTooltip(Tooltip.create(text));
                }
        ).bounds(x, y, width, height)
                .tooltip(Tooltip.create(Component.literal(displayName + ": " + (stateGetter.apply(uuid) ? "§aВКЛ" : "§cВЫКЛ"))))
                .build();
    }
}