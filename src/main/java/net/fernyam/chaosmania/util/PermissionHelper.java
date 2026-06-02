package net.fernyam.chaosmania.util;


import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.data.JSONSettingManager;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PermissionHelper {

    private static PlayerSettings getGlobal() {
        return JSONSettingManager.getSettings(JSONSettingManager.ALL_PLAYER_UUID);
    }

    private static PlayerSettings getPlayer(Player player) {
        return JSONSettingManager.getSettings(player.getUUID());
    }

    // ==================== Блоки ====================

    public static boolean canPlaceBlock(Player player, Block block) {
        if (player == null || block == null) return true;

        PlayerSettings global = getGlobal();
        PlayerSettings personal = getPlayer(player);
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();

        // 1. ЛИЧНЫЕ НАСТРОЙКИ (приоритет)
        if (personal != null && personal.isDisablePlaceBlock()) {
            if (personal.hasBlockSetting(blockId)) {
                return !personal.canPlaceBlock(blockId);  // true = разрешено, false = запрещено
            }
        }

        // 2. ГЛОБАЛЬНЫЕ НАСТРОЙКИ (fallback)
        if (global != null && global.isDisablePlaceBlock()) {
            if (global.hasBlockSetting(blockId)) {
                return !global.canPlaceBlock(blockId);
            }
        }

        return true;  // по умолчанию разрешено
    }

    public static boolean canBreakBlock(Player player, Block block) {
        if (player == null || block == null) return true;

        PlayerSettings global = getGlobal();
        PlayerSettings personal = getPlayer(player);
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();

        if (personal != null && personal.isDisableBreakBlock()) {
            if (personal.hasBlockSetting(blockId)) {
                return !personal.canBreakBlock(blockId);
            }
        }

        if (global != null && global.isDisableBreakBlock()) {
            if (global.hasBlockSetting(blockId)) {
                return !global.canBreakBlock(blockId);
            }
        }

        return true;
    }

    // ==================== Предметы ====================

    public static boolean canDropItem(Player player, Item item) {
        if (player == null || item == null) return true;

        PlayerSettings global = getGlobal();
        PlayerSettings personal = getPlayer(player);
        String itemId = BuiltInRegistries.ITEM.getKey(item).toString();

        if (personal != null && personal.isDisableDropItem()) {
            if (personal.hasItemSetting(itemId)) {
                return !personal.canDropItem(itemId);
            }
        }

        if (global != null && global.isDisableDropItem()) {
            if (global.hasItemSetting(itemId)) {
                return !global.canDropItem(itemId);
            }
        }

        return true;
    }

    public static boolean canPickupItem(Player player, Item item) {
        if (player == null || item == null) return true;

        PlayerSettings global = getGlobal();
        PlayerSettings personal = getPlayer(player);
        String itemId = BuiltInRegistries.ITEM.getKey(item).toString();

        if (personal != null && personal.isDisablePickupItem()) {
            if (personal.hasItemSetting(itemId)) {
                return !personal.canPickupItem(itemId);
            }
        }

        if (global != null && global.isDisablePickupItem()) {
            if (global.hasItemSetting(itemId)) {
                return !global.canPickupItem(itemId);
            }
        }

        return true;
    }

    // ==================== Семена ====================

    public static boolean canPlantSeed(Player player, Item seed) {
        if (player == null || seed == null) return true;

        PlayerSettings global = getGlobal();
        PlayerSettings personal = getPlayer(player);
        String seedId = BuiltInRegistries.ITEM.getKey(seed).toString();

        if (personal != null && personal.isDisablePlantingSeed()) {
            if (personal.hasSeedSetting(seedId)) {
                return !personal.canPlantSeed(seedId);
            }
        }

        if (global != null && global.isDisablePlantingSeed()) {
            if (global.hasSeedSetting(seedId)) {
                return !global.canPlantSeed(seedId);
            }
        }

        return true;
    }

    // ==================== Жители ====================

    public static boolean canTradeWithVillager(Player player, VillagerProfession profession) {

        if (player == null || profession == null) return true;

        PlayerSettings global = getGlobal();
        PlayerSettings personal = getPlayer(player);
        String professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();


        if (personal != null && personal.isDisableTradingVillager()) {
            if (personal.hasVillagerSetting(professionId)) {
                return personal.canTradeWithVillager(professionId);
            }
        }

        if (global != null && global.isDisableTradingVillager()) {
            if (global.hasVillagerSetting(professionId)) {
                return global.canTradeWithVillager(professionId);
            }
        }
        return true;
    }

    public static boolean canTradeWithWanderingTrader(Player player) {
        if (player == null) return true;

        PlayerSettings global = getGlobal();
        PlayerSettings personal = getPlayer(player);

        // Личные настройки (приоритет)
        if (personal != null && personal.isDisableTradingWanderingTrader()) {
            return false;
        }

        // Глобальные как fallback
        if (global != null && global.isDisableTradingWanderingTrader()) {
            return false;
        }

        return true;
    }
}