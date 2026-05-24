package net.fernyam.chaosmania.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class PlayerSettings {

    private static class ItemSetting {
        private final String idItem;
        private boolean canDrop;
        private boolean canPickup;

        public ItemSetting(String idItem, boolean canDrop, boolean canPickup) {
            this.idItem = idItem;
            this.canDrop = canDrop;
            this.canPickup = canPickup;
        }

        public ItemSetting(String idItem) {
            this(idItem, false, false);
        }

        public String getIdItem() { return idItem; }
        public boolean canDrop() { return canDrop; }
        public boolean canPickup() { return canPickup; }
        public void setDrop(boolean canDrop) { this.canDrop = canDrop; }
        public void setPickup(boolean canPickup) { this.canPickup = canPickup; }
        public void toggleDrop() { this.canDrop = !this.canDrop; }
        public void togglePickup() { this.canPickup = !this.canPickup; }
    }

    private static class BlockSetting {
        private final String idBlock;
        private boolean canPlace;
        private boolean canBreak;

        public BlockSetting(String id, boolean canPlace, boolean canBreak) {
            this.idBlock = id;
            this.canPlace = canPlace;
            this.canBreak = canBreak;
        }

        public BlockSetting(String id) {
            this(id, false, false);
        }

        public String getIdBlock() { return idBlock; }
        public boolean canPlace() { return canPlace; }
        public boolean canBreak() { return canBreak; }
        public void setPlace(boolean canPlace) { this.canPlace = canPlace; }
        public void setBreak(boolean canBreak) { this.canBreak = canBreak; }
        public void togglePlace() { this.canPlace = !this.canPlace; }
        public void toggleBreak() { this.canBreak = !this.canBreak; }
    }

    private String name;
    private String uuidPlayer;

    private boolean isDisablePlaceBlock;
    private boolean isDisableBreakBlock;
    private List<BlockSetting> blockSettings;

    private  boolean isDisableItemDrop;
    private  boolean isDisableItemPickup;
    private List<ItemSetting> itemSettings;

//    private boolean isDisablePlantingSeed;
//    private boolean isDisableVillagerTrading;


    // Конструктор по умолчанию (нужен для Gson)
    public PlayerSettings() {}

    public PlayerSettings(String name, String uuidPlayer) {
        this.name = name;
        this.uuidPlayer = uuidPlayer;

        this.isDisablePlaceBlock = false;
        this.isDisableBreakBlock = false;


        this.blockSettings = new ArrayList<>();
        this.itemSettings = new ArrayList<>();
    }

    public PlayerSettings(String name, UUID uuidPlayer) {
        this.name = name;
        this.uuidPlayer = uuidPlayer.toString();

        this.isDisablePlaceBlock = false;
        this.isDisableBreakBlock = false;


        this.blockSettings = new ArrayList<>();
        this.itemSettings = new ArrayList<>();
    }


    // ==================== Геттеры и сеттеры ====================
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUuidPlayer() { return uuidPlayer; }
    public void setUuidPlayer(String uuidPlayer) { this.uuidPlayer = uuidPlayer; }
    public void setUuidPlayer(UUID uuidPlayer) { this.uuidPlayer = uuidPlayer.toString(); }

    public List<BlockSetting> getBlockSettings() { return blockSettings; }
    public List<ItemSetting> getItemSettings() { return itemSettings; }

    public void setDisablePlaceBlock(boolean disablePlaceBlock) { isDisablePlaceBlock = disablePlaceBlock; }
    public void setDisableBreakBlock(boolean disableBreakBlock) { isDisableBreakBlock = disableBreakBlock;  }

    public void setDisableItemDrop(boolean disableItemDrop) { isDisableItemDrop = disableItemDrop; }
    public void setDisableItemPickup(boolean disableItemPickup) { isDisableItemPickup = disableItemPickup; }

    public void toggleDisablePlaceBlock() { isDisablePlaceBlock = !isDisablePlaceBlock; }
    public void toggleDisableBreakBlock() { isDisableBreakBlock = !isDisableBreakBlock;  }

    public void toggleDisableItemDrop() { isDisableItemDrop = !isDisableItemDrop; }
    public void toggleDisableItemPickup() { isDisableItemPickup = !isDisableItemPickup; }




    public boolean getDisablePlaceBlock() { return isDisablePlaceBlock; }
    public boolean getDisableBreakBlock() { return isDisableBreakBlock;  }

    public boolean getDisableItemDrop() { return isDisableItemDrop; }
    public boolean getDisableItemPickup() { return isDisableItemPickup; }

    //    public void setDisablePlantingSeed(boolean disablePlantingSeed) {
//        isDisablePlantingSeed = disablePlantingSeed;
//    }
//
//    public void setDisableVillagerTrading(boolean disableVillagerTrading) {
//        isDisableVillagerTrading = disableVillagerTrading;
//    }


// ==================== Работа с блоками ====================

    private BlockSetting getOrCreateBlockSetting(String id) {
        for (BlockSetting setting : blockSettings) {
            if (setting.getIdBlock().equals(id)) {
                return setting;
            }
        }
        BlockSetting newSetting = new BlockSetting(id);
        blockSettings.add(newSetting);
        return newSetting;
    }

    public void addBlockElement(Block block) {
        String id = BuiltInRegistries.BLOCK.getKey(block).toString();
        if (!isBlockExists(id)) {
            blockSettings.add(new BlockSetting(id));
        }
    }

    public void addBlockElement(String id, boolean canPlace, boolean canBreak) {
        if (!isBlockExists(id)) {
            blockSettings.add(new BlockSetting(id, canPlace, canBreak));
        }
    }

    public boolean isBlockExists(String id) {
        return blockSettings.stream().anyMatch(setting -> setting.getIdBlock().equals(id));
    }

    public void removeBlockElement(Block block) {
        String id = BuiltInRegistries.BLOCK.getKey(block).toString();
        blockSettings.removeIf(setting -> setting.getIdBlock().equals(id));
    }

    public void toggleBlockPlace(String id) {
        getOrCreateBlockSetting(id).togglePlace();
    }

    public void toggleBlockBreak(String id) {
        getOrCreateBlockSetting(id).toggleBreak();
    }

    public boolean canPlaceBlock(String id) {
        return blockSettings.stream()
                .filter(setting -> setting.getIdBlock().equals(id))
                .findFirst()
                .map(BlockSetting::canPlace)
                .orElse(false);
    }

    public boolean canBreakBlock(String id) {
        return blockSettings.stream()
                .filter(setting -> setting.getIdBlock().equals(id))
                .findFirst()
                .map(BlockSetting::canBreak)
                .orElse(false);
    }

    public List<String> getAllBlockID()
    {
        List<String> listId = new ArrayList<>();

        for(BlockSetting settingBlock : blockSettings)
        {
            listId.add(settingBlock.getIdBlock());
        }

        return listId;
    }


    // ==================== Работа с предметами ====================

    private ItemSetting getOrCreateItemSetting(String id) {
        for (ItemSetting setting : itemSettings) {
            if (setting.getIdItem().equals(id)) {
                return setting;
            }
        }
        ItemSetting newSetting = new ItemSetting(id);
        itemSettings.add(newSetting);
        return newSetting;
    }

    public void addItemElement(Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        if (!isItemExists(id)) {
            itemSettings.add(new ItemSetting(id));
        }
    }

    public void addItemElement(String id, boolean canDrop, boolean canPickup) {
        if (!isItemExists(id)) {
            itemSettings.add(new ItemSetting(id, canDrop, canPickup));
        }
    }

    public boolean isItemExists(String id) {
        return itemSettings.stream().anyMatch(setting -> setting.getIdItem().equals(id));
    }

    public void removeItemElement(Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        itemSettings.removeIf(setting -> setting.getIdItem().equals(id));
    }

    public void setItemDropSetting(String id, boolean canDrop) {
        getOrCreateItemSetting(id).setDrop(canDrop);
    }

    public void setItemPickupSetting(String id, boolean canPickup) {
        getOrCreateItemSetting(id).setPickup(canPickup);
    }

    public void toggleItemDrop(String id) {
        getOrCreateItemSetting(id).toggleDrop();
    }

    public void toggleItemPickup(String id) {
        getOrCreateItemSetting(id).togglePickup();
    }

    public boolean canDropItem(String id) {
        return itemSettings.stream()
                .filter(setting -> setting.getIdItem().equals(id))
                .findFirst()
                .map(ItemSetting::canDrop)
                .orElse(false);
    }

    public boolean canPickupItem(String id) {
        return itemSettings.stream()
                .filter(setting -> setting.getIdItem().equals(id))
                .findFirst()
                .map(ItemSetting::canPickup)
                .orElse(false);
    }

    public List<String> getAllItemID()
    {
        List<String> listId = new ArrayList<>();

        for(ItemSetting settingItem : itemSettings)
        {
            listId.add(settingItem.getIdItem());
        }

        return listId;
    }
}