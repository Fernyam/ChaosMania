package net.fernyam.chaosmania.util;

import net.fernyam.chaosmania.data.settings.*;
import net.fernyam.chaosmania.data.settings.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PermissionHelper {

    private static BlockSettings getGlobalBlock() {
        return SettingsManager.getBlockSettings(SettingsManager.ALL_PLAYER_UUID);
    }

    private static ItemSettings getGlobalItem() {
        return SettingsManager.getItemSettings(SettingsManager.ALL_PLAYER_UUID);
    }

    private static SeedSettings getGlobalSeed() {
        return SettingsManager.getSeedSettings(SettingsManager.ALL_PLAYER_UUID);
    }

    private static VillagerSettings getGlobalVillager() {
        return SettingsManager.getVillagerSettings(SettingsManager.ALL_PLAYER_UUID);
    }

    private static AnimalSettings getGlobalAnimal() {
        return SettingsManager.getAnimalSettings(SettingsManager.ALL_PLAYER_UUID);
    }

    private static BlockSettings getPlayerBlock(Player player) {
        return SettingsManager.getBlockSettings(player.getUUID().toString());
    }

    private static ItemSettings getPlayerItem(Player player) {
        return SettingsManager.getItemSettings(player.getUUID().toString());
    }

    private static SeedSettings getPlayerSeed(Player player) {
        return SettingsManager.getSeedSettings(player.getUUID().toString());
    }

    private static VillagerSettings getPlayerVillager(Player player) {
        return SettingsManager.getVillagerSettings(player.getUUID().toString());
    }

    private static AnimalSettings getPlayerAnimal(Player player) {
        return SettingsManager.getAnimalSettings(player.getUUID().toString());
    }

    // ==================== Блоки ====================

    public static boolean canPlaceBlock(Player player, Block block) {
        if (player == null || block == null) return true;

        BlockSettings personal = getPlayerBlock(player);
        BlockSettings global = getGlobalBlock();
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();

        if (personal != null && personal.isBlockPlaceControlEnabled()) {
            if (personal.isBlockExists(blockId)) {
                return personal.canPlaceBlock(blockId);
            }
        }

        if (global != null && global.isBlockPlaceControlEnabled()) {
            if (global.isBlockExists(blockId)) {
                return global.canPlaceBlock(blockId);
            }
        }

        return true;
    }

    public static boolean canBreakBlock(Player player, Block block) {
        if (player == null || block == null) return true;

        BlockSettings personal = getPlayerBlock(player);
        BlockSettings global = getGlobalBlock();
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();

        if (personal != null && personal.isBlockBreakControlEnabled()) {
            if (personal.isBlockExists(blockId)) {
                return personal.canBreakBlock(blockId);
            }
        }

        if (global != null && global.isBlockBreakControlEnabled()) {
            if (global.isBlockExists(blockId)) {
                return global.canBreakBlock(blockId);
            }
        }

        return true;
    }

    // ==================== Предметы ====================

    public static boolean canDropItem(Player player, Item item) {
        if (player == null || item == null) return true;

        ItemSettings personal = getPlayerItem(player);
        ItemSettings global = getGlobalItem();
        String itemId = BuiltInRegistries.ITEM.getKey(item).toString();

        if (personal != null && personal.isItemDropControlEnabled()) {
            if (personal.isItemExists(itemId)) {
                return personal.canDropItem(itemId);
            }
        }

        if (global != null && global.isItemDropControlEnabled()) {
            if (global.isItemExists(itemId)) {
                return global.canDropItem(itemId);
            }
        }

        return true;
    }

    public static boolean canPickupItem(Player player, Item item) {
        if (player == null || item == null) return true;

        ItemSettings personal = getPlayerItem(player);
        ItemSettings global = getGlobalItem();
        String itemId = BuiltInRegistries.ITEM.getKey(item).toString();

        if (personal != null && personal.isItemPickupControlEnabled()) {
            if (personal.isItemExists(itemId)) {
                return personal.canPickupItem(itemId);
            }
        }

        if (global != null && global.isItemPickupControlEnabled()) {
            if (global.isItemExists(itemId)) {
                return global.canPickupItem(itemId);
            }
        }

        return true;
    }

    // ==================== Семена ====================

    public static boolean canPlantSeed(Player player, Item seed) {
        if (player == null || seed == null) return true;

        SeedSettings personal = getPlayerSeed(player);
        SeedSettings global = getGlobalSeed();
        String seedId = BuiltInRegistries.ITEM.getKey(seed).toString();

        if (personal != null && personal.isSeedPlantControlEnabled()) {
            if (personal.isSeedExists(seedId)) {
                return personal.canPlantSeed(seedId);
            }
        }

        if (global != null && global.isSeedPlantControlEnabled()) {
            if (global.isSeedExists(seedId)) {
                return global.canPlantSeed(seedId);
            }
        }

        return true;
    }

    // ==================== Жители ====================

    public static boolean canTradeWithVillager(Player player, VillagerProfession profession) {
        if (player == null || profession == null) return true;

        VillagerSettings personal = getPlayerVillager(player);
        VillagerSettings global = getGlobalVillager();
        String professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();

        if (personal != null && personal.isVillagerTradeControlEnabled()) {
            if (personal.isProfessionExists(professionId)) {
                return personal.canTradeWithVillager(professionId);
            }
        }

        if (global != null && global.isVillagerTradeControlEnabled()) {
            if (global.isProfessionExists(professionId)) {
                return global.canTradeWithVillager(professionId);
            }
        }

        return true;
    }

    public static boolean canTradeWithWanderingTrader(Player player) {
        if (player == null) return true;

        VillagerSettings personal = getPlayerVillager(player);
        VillagerSettings global = getGlobalVillager();

        if (personal != null && personal.isWanderingTraderControlEnabled()) {
            return false;
        }

        if (global != null && global.isWanderingTraderControlEnabled()) {
            return false;
        }

        return true;
    }

    // ==================== Животные ====================

    public static boolean canBreedAnimal(Player player, EntityType<?> entityType) {
        if (player == null || entityType == null) return true;

        AnimalSettings personal = getPlayerAnimal(player);
        AnimalSettings global = getGlobalAnimal();
        String animalId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();

        if (personal != null && personal.isAnimalBreedControlEnabled()) {
            if (personal.isAnimalExists(animalId)) {
                return personal.canBreedAnimal(animalId);
            }
        }

        if (global != null && global.isAnimalBreedControlEnabled()) {
            if (global.isAnimalExists(animalId)) {
                return global.canBreedAnimal(animalId);
            }
        }

        return true;
    }

    public static boolean canSpawnAnimal(Player player, EntityType<?> entityType) {
        if (player == null || entityType == null) return true;

        AnimalSettings personal = getPlayerAnimal(player);
        AnimalSettings global = getGlobalAnimal();
        String animalId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();

        if (personal != null && personal.isAnimalSpawnControlEnabled()) {
            if (personal.isAnimalExists(animalId)) {
                return personal.canSpawnAnimal(animalId);
            }
        }

        if (global != null && global.isAnimalSpawnControlEnabled()) {
            if (global.isAnimalExists(animalId)) {
                return global.canSpawnAnimal(animalId);
            }
        }

        return true;
    }
}