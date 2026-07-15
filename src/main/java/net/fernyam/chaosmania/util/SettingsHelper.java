package net.fernyam.chaosmania.util;

import net.fernyam.chaosmania.data.settings.SettingsManager;
import net.fernyam.chaosmania.data.settings.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class SettingsHelper {

    // ==================== БЛОКИ ====================

    public static void toggleBlockInList(String uuid, Block block) {
        if (block == null || uuid == null) return;
        String id = BuiltInRegistries.BLOCK.getKey(block).toString();

        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        if (settings == null) return;

        if (settings.isBlockExists(id)) {
            settings.removeBlock(id);
        } else {
            settings.addBlock(id);
        }
        SettingsManager.saveBlockSettings(uuid);
    }

    public static void toggleBlockPlace(String uuid, String blockId) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        if (settings == null) return;
        settings.toggleBlockPlace(blockId);
        SettingsManager.saveBlockSettings(uuid);
    }

    public static void toggleBlockBreak(String uuid, String blockId) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        if (settings == null) return;
        settings.toggleBlockBreak(blockId);
        SettingsManager.saveBlockSettings(uuid);
    }

    public static void toggleBlockRightClick(String uuid, String blockId) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        if (settings == null) return;
        settings.toggleBlockRightClick(blockId);
        SettingsManager.saveBlockSettings(uuid);
    }

    public static void toggleBlockLeftClick(String uuid, String blockId) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        if (settings == null) return;
        settings.toggleBlockLeftClick(blockId);
        SettingsManager.saveBlockSettings(uuid);
    }

    public static void toggleBlockPlaceControl(String uuid) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalPlaceBlock();
        SettingsManager.saveBlockSettings(uuid);
    }

    public static void toggleBlockBreakControl(String uuid) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalBreakBlock();
        SettingsManager.saveBlockSettings(uuid);
    }

    public static void toggleBlockRightClickControl(String uuid) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalRightClickBlock();
        SettingsManager.saveBlockSettings(uuid);
    }

    public static void toggleBlockLeftClickControl(String uuid) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalLeftClickBlock();
        SettingsManager.saveBlockSettings(uuid);
    }

    public static boolean canPlaceBlock(String uuid, String blockId) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        return settings != null && settings.canPlaceBlock(blockId);
    }

    public static boolean canBreakBlock(String uuid, String blockId) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        return settings != null && settings.canBreakBlock(blockId);
    }

    public static boolean canRightClickBlock(String uuid, String blockId) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        return settings != null && settings.canRightClickBlock(blockId);
    }

    public static boolean canLeftClickBlock(String uuid, String blockId) {
        BlockSettings settings = SettingsManager.getBlockSettings(uuid);
        return settings != null && settings.canLeftClickBlock(blockId);
    }

    // ==================== ПРЕДМЕТЫ ====================

    public static void toggleItemInList(String uuid, Item item) {
        if (item == null || uuid == null) return;
        String id = BuiltInRegistries.ITEM.getKey(item).toString();

        ItemSettings settings = SettingsManager.getItemSettings(uuid);
        if (settings == null) return;

        if (settings.isItemExists(id)) {
            settings.removeItem(id);
        } else {
            settings.addItem(id);
        }
        SettingsManager.saveItemSettings(uuid);
    }

    public static void toggleItemDrop(String uuid, String itemId) {
        ItemSettings settings = SettingsManager.getItemSettings(uuid);
        if (settings == null) return;
        settings.toggleItemDrop(itemId);
        SettingsManager.saveItemSettings(uuid);
    }

    public static void toggleItemPickup(String uuid, String itemId) {
        ItemSettings settings = SettingsManager.getItemSettings(uuid);
        if (settings == null) return;
        settings.toggleItemPickup(itemId);
        SettingsManager.saveItemSettings(uuid);
    }

    public static void toggleItemDropControl(String uuid) {
        ItemSettings settings = SettingsManager.getItemSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalDropItem();
        SettingsManager.saveItemSettings(uuid);
    }

    public static void toggleItemPickupControl(String uuid) {
        ItemSettings settings = SettingsManager.getItemSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalPickupItem();
        SettingsManager.saveItemSettings(uuid);
    }

    public static boolean canDropItem(String uuid, String itemId) {
        ItemSettings settings = SettingsManager.getItemSettings(uuid);
        return settings != null && settings.canDropItem(itemId);
    }

    public static boolean canPickupItem(String uuid, String itemId) {
        ItemSettings settings = SettingsManager.getItemSettings(uuid);
        return settings != null && settings.canPickupItem(itemId);
    }

    // ==================== СЕМЕНА ====================

    public static void toggleSeedInList(String uuid, Item seed) {
        if (seed == null || uuid == null) return;
        String id = BuiltInRegistries.ITEM.getKey(seed).toString();

        SeedSettings settings = SettingsManager.getSeedSettings(uuid);
        if (settings == null) return;

        if (settings.isSeedExists(id)) {
            settings.removeSeed(id);
        } else {
            settings.addSeed(id);
        }
        SettingsManager.saveSeedSettings(uuid);
    }

    public static void toggleSeedPlanting(String uuid, String seedId) {
        SeedSettings settings = SettingsManager.getSeedSettings(uuid);
        if (settings == null) return;
        settings.toggleSeedPlant(seedId);
        SettingsManager.saveSeedSettings(uuid);
    }

    public static void toggleSeedPlantControl(String uuid) {
        SeedSettings settings = SettingsManager.getSeedSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalPlantSeed();
        SettingsManager.saveSeedSettings(uuid);
    }

    public static boolean canPlantSeed(String uuid, String seedId) {
        SeedSettings settings = SettingsManager.getSeedSettings(uuid);
        return settings != null && settings.canPlantSeed(seedId);
    }

    // ==================== ЖИТЕЛИ ====================

    public static void toggleVillagerInList(String uuid, String professionId) {
        if (professionId == null || uuid == null) return;

        VillagerSettings settings = SettingsManager.getVillagerSettings(uuid);
        if (settings == null) return;

        if (settings.isProfessionExists(professionId)) {
            settings.removeProfession(professionId);
        } else {
            settings.addProfession(professionId);
        }
        SettingsManager.saveVillagerSettings(uuid);
    }

    public static void toggleVillagerTrade(String uuid, String professionId) {
        VillagerSettings settings = SettingsManager.getVillagerSettings(uuid);
        if (settings == null) return;
        settings.toggleProfessionTrade(professionId);
        SettingsManager.saveVillagerSettings(uuid);
    }

    public static void toggleVillagerTradeControl(String uuid) {
        VillagerSettings settings = SettingsManager.getVillagerSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalTradeVillager();
        SettingsManager.saveVillagerSettings(uuid);
    }

    public static void toggleWanderingTraderControl(String uuid) {
        VillagerSettings settings = SettingsManager.getVillagerSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalTradeWanderingTrader();
        SettingsManager.saveVillagerSettings(uuid);
    }

    public static boolean canTradeWithVillager(String uuid, String professionId) {
        VillagerSettings settings = SettingsManager.getVillagerSettings(uuid);
        return settings != null && settings.canTradeWithVillager(professionId);
    }

    public static boolean canTradeWithWanderingTrader(String uuid) {
        VillagerSettings settings = SettingsManager.getVillagerSettings(uuid);
        return settings != null && settings.canTradeWithWanderingTrader();
    }

    // ==================== ЖИВОТНЫЕ ====================

    public static void toggleMobInList(String uuid, EntityType<?> entityType) {
        if (entityType == null || uuid == null) return;
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();

        MobSettings settings = SettingsManager.getMobSettings(uuid);
        if (settings == null) return;

        if (settings.isMobExists(id)) {
            settings.removeMob(id);
        } else {
            settings.addMob(id);
        }
        SettingsManager.saveMobSettings(uuid);
    }

    public static void toggleMobRightClick(String uuid, String animalId) {
        MobSettings settings = SettingsManager.getMobSettings(uuid);
        if (settings == null) return;
        settings.toggleMobRightClick(animalId);
        SettingsManager.saveMobSettings(uuid);
    }

    public static void toggleMobLeftClick(String uuid, String animalId) {
        MobSettings settings = SettingsManager.getMobSettings(uuid);
        if (settings == null) return;
        settings.toggleMobLeftClick(animalId);
        SettingsManager.saveMobSettings(uuid);
    }

    public static void toggleMobRightClickControl(String uuid) {
        MobSettings settings = SettingsManager.getMobSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalRightClick();
        SettingsManager.saveMobSettings(uuid);
    }

    public static void toggleMobLeftClickControl(String uuid) {
        MobSettings settings = SettingsManager.getMobSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalLeftClick();
        SettingsManager.saveMobSettings(uuid);
    }

    public static boolean canRightClickMob(String uuid, String animalId) {
        MobSettings settings = SettingsManager.getMobSettings(uuid);
        return settings != null && settings.canRightClickMob(animalId);
    }

    public static boolean canLeftClickMob(String uuid, String animalId) {
        MobSettings settings = SettingsManager.getMobSettings(uuid);
        return settings != null && settings.canLeftClickMob(animalId);
    }

    // ==================== МОДЫ ====================

    public static void toggleModInList(String uuid, String modId) {
        if (modId == null || uuid == null) return;

        ModSettings settings = SettingsManager.getModSettings(uuid);
        if (settings == null) return;

        if (settings.isModExists(modId)) {
            settings.removeMod(modId);
        } else {
            settings.addMod(modId);
        }
        SettingsManager.saveModSettings(uuid);
    }

    public static void toggleModLoad(String uuid, String modId) {
        ModSettings settings = SettingsManager.getModSettings(uuid);
        if (settings == null) return;
        settings.toggleModLoad(modId);
        SettingsManager.saveModSettings(uuid);
    }

    public static void toggleModLoadControl(String uuid) {
        ModSettings settings = SettingsManager.getModSettings(uuid);
        if (settings == null) return;
        settings.toggleGlobalLoadMod();
        SettingsManager.saveModSettings(uuid);
    }

    public static boolean canLoadMod(String uuid, String modId) {
        ModSettings settings = SettingsManager.getModSettings(uuid);
        return settings != null && settings.canLoadMod(modId);
    }
}