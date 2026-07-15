package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.util.SettingsHelper;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.fernyam.chaosmania.data.settings.SettingsManager.*;

public enum ToggleButtonType {
    BLOCK_PLACE("Контроль установки",
            SettingsHelper::toggleBlockPlaceControl,
            uuid -> getBlockSettings(uuid).isBlockPlaceControlEnabled()),

    BLOCK_BREAK("Контроль ломания",
            SettingsHelper::toggleBlockBreakControl,
            uuid -> getBlockSettings(uuid).isBlockBreakControlEnabled()),

    BLOCK_RIGHT_CLICK("Контроль ПКМ",
            SettingsHelper::toggleBlockRightClickControl,
            uuid -> getBlockSettings(uuid).isBlockRightClickControlEnabled()),

    BLOCK_LEFT_CLICK("Контроль ЛКМ",
            SettingsHelper::toggleBlockLeftClickControl,
            uuid -> getBlockSettings(uuid).isBlockLeftClickControlEnabled()),

    ITEM_DROP("Контроль выбрасывания",
            SettingsHelper::toggleItemDropControl,
            uuid -> getItemSettings(uuid).isItemDropControlEnabled()),

    ITEM_PICKUP("Контроль подбора",
            SettingsHelper::toggleItemPickupControl,
            uuid -> getItemSettings(uuid).isItemPickupControlEnabled()),

    SEED_PLANT("Контроль посадки",
            SettingsHelper::toggleSeedPlantControl,
            uuid -> getSeedSettings(uuid).isSeedPlantControlEnabled()),

    VILLAGER_TRADE("Контроль торговли",
            SettingsHelper::toggleVillagerTradeControl,
            uuid -> getVillagerSettings(uuid).isVillagerTradeControlEnabled()),

    WANDERING_TRADER("Контроль странствующих",
            SettingsHelper::toggleWanderingTraderControl,
            uuid -> getVillagerSettings(uuid).isWanderingTraderControlEnabled()),

    MOB_RIGHT_CLICK("Контроль взаимодействия ПКМ",
            SettingsHelper::toggleMobRightClickControl,
            uuid -> getMobSettings(uuid).isMobRightClickControlEnabled()),

    MOB_LEFT_CLICK("Контроль взаимодействия ЛКМ",
            SettingsHelper::toggleMobLeftClickControl,
            uuid -> getMobSettings(uuid).isMobLeftClickControlEnabled()),

    MOD_LOAD("Контроль модов",
            SettingsHelper::toggleModLoadControl,
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