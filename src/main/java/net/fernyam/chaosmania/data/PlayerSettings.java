package net.fernyam.chaosmania.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class PlayerSettings {

    public static class ItemSetting {
        public final String idItem;
        public boolean canDrop;    // Может ли предмет выпадать
        public boolean canPickup;  // Можно ли подбирать предмет


        public ItemSetting(String idItem, boolean canDrop, boolean canPickup) {
            this.idItem = idItem;
            this.canDrop = canDrop;
            this.canPickup = canPickup;
        }

        public ItemSetting(String idItem) {
            this(idItem, false, false);
        }
    }

    private static class BlockSetting
    {
        String idBlock;
        boolean isPlaceBlock;
        boolean isBreakBlock;

        public BlockSetting(String id , boolean isPlaceBlock , boolean isBreakBlock)
        {
            this.idBlock = id;
            this.isPlaceBlock = isPlaceBlock;
            this.isBreakBlock = isBreakBlock;
        }

        public BlockSetting(String id)
        {
            this.idBlock = id;
            isPlaceBlock = false;
            isBreakBlock = false;
        }
    }

    private String name;
    private String uuidPlayer;

    private boolean isDisablePlaceBlock;
    private boolean isDisableBreakBlock;
    private List<String> dontPlaceBlockList;
    private List<String> dontBreakBlockList;

    private List<BlockSetting> blockSettings;
    private List<ItemSetting> itemSettings;

    private  boolean isDisableItemDrop;
    private  boolean isDisableItemPickup;
    private List<String> dontDropItemList;
    private List<String> dontPuckupItemList;

//    private boolean isDisablePlantingSeed;
//    private boolean isDisableVillagerTrading;


    // Конструктор по умолчанию (нужен для Gson)
    public PlayerSettings() {}

    // Конструктор для удобного создания
    public PlayerSettings(String name, UUID uuidPlayer,
                          boolean isDisablePlaceBlock, boolean isDisableBreakBlock,
                          List<String> dontPlaceBlockList, List<String> dontBreakBlockList ,
                          boolean isDisableItemDrop, boolean isDisableItemPickup ,
                          List<String> dontDropItemList , List<String> dontPuckupItemList )
    {
        this.name = name;
        this.uuidPlayer = uuidPlayer.toString();

        this.isDisablePlaceBlock = isDisablePlaceBlock;
        this.isDisableBreakBlock = isDisableBreakBlock;
        this.dontPlaceBlockList = dontPlaceBlockList;
        this.dontBreakBlockList = dontBreakBlockList;

        this.isDisableItemDrop = isDisableItemDrop;
        this.isDisableItemPickup = isDisableItemPickup;

        this.dontDropItemList = dontDropItemList;
        this.dontPuckupItemList = dontPuckupItemList;

    }

    public PlayerSettings(String name, String uuidPlayer,
                          boolean isDisablePlaceBlock, boolean isDisableBreakBlock,
                          List<String> dontPlaceBlockList, List<String> dontBreakBlockList ,
                          boolean isDisableItemDrop, boolean isDisableItemPickup ,
                          List<String> dontDropItemList , List<String> dontPuckupItemList) {
        this.name = name;
        this.uuidPlayer = uuidPlayer;

        this.isDisablePlaceBlock = isDisablePlaceBlock;
        this.isDisableBreakBlock = isDisableBreakBlock;
        this.dontPlaceBlockList = dontPlaceBlockList;
        this.dontBreakBlockList = dontBreakBlockList;

        this.isDisableItemDrop = isDisableItemDrop;
        this.isDisableItemPickup = isDisableItemPickup;

        this.dontDropItemList = dontDropItemList;
        this.dontPuckupItemList = dontPuckupItemList;
    }


    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUuidPlayer() { return uuidPlayer; }
    public void setUuidPlayer(UUID uuidPlayer) { this.uuidPlayer = uuidPlayer.toString(); }

    public boolean isDisablePlaceBlock() { return isDisablePlaceBlock; }
    public void setDisablePlaceBlock(boolean disablePlaceBlock) { isDisablePlaceBlock = disablePlaceBlock; }

    public boolean isDisableBreakBlock() { return isDisableBreakBlock; }
    public void setDisableBreakBlock(boolean disableBreakBlock) { isDisableBreakBlock = disableBreakBlock; }

    public List<String> getDontPlaceBlockList() { return dontPlaceBlockList; }
    public void setDontPlaceBlockList(List<String> dontPlaceBlockList) { this.dontPlaceBlockList = dontPlaceBlockList; }

    public List<String> getDontBreakBlockList() { return dontBreakBlockList; }
    public void setDontBreakBlockList(List<String> dontBreakBlockList) { this.dontBreakBlockList = dontBreakBlockList; }


    public boolean isDisableItemDrop() {
        return isDisableItemDrop;
    }

    public void setDisableItemDrop(boolean disableItemItemDrop) {
        isDisableItemDrop = disableItemItemDrop;
    }

    public boolean isDisableItemPickup() {
        return isDisableItemPickup;
    }

    public void setDisableItemPickup(boolean disableItemPickup) {
        isDisableItemPickup = disableItemPickup;
    }

//    public void setDisablePlantingSeed(boolean disablePlantingSeed) {
//        isDisablePlantingSeed = disablePlantingSeed;
//    }
//
//    public void setDisableVillagerTrading(boolean disableVillagerTrading) {
//        isDisableVillagerTrading = disableVillagerTrading;
//    }

    public List<String> getDontDropItemList() {
        return dontDropItemList;
    }

    public void setDontDropItemList(List<String> dontDropItemList) {
        this.dontDropItemList = dontDropItemList;
    }

    public List<String> getDontPuckupItemList() {
        return dontPuckupItemList;
    }

    public void setDontPuckupItemList(List<String> dontPuckupItemList) {
        this.dontPuckupItemList = dontPuckupItemList;
    }


    public void AddElementToDontBreakBlockList(Block block)
    {
        if(dontBreakBlockList.contains(BuiltInRegistries.BLOCK.getKey(block).toString())) return;
        dontBreakBlockList.add(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public void RemoveElementToDontBreakBlockList(Block block)
    {
        dontBreakBlockList.remove(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public void AddElementToDontPlaceBlockList(Block block)
    {
        if(dontPlaceBlockList.contains(BuiltInRegistries.BLOCK.getKey(block).toString())) return;
        dontPlaceBlockList.add(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public void RemoveElementToDontPlaceBlockList(Block block)
    {
        dontPlaceBlockList.remove(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public void AddElementToDontDropItemList(Item item)
    {
        if(dontDropItemList.contains(BuiltInRegistries.ITEM.getKey(item).toString())) return;
        dontDropItemList.add(BuiltInRegistries.ITEM.getKey(item).toString());
    }

    public void RemoveElementToDontDropItemList(Item item)
    {
        dontDropItemList.remove(BuiltInRegistries.ITEM.getKey(item).toString());
    }

    public void AddElementToDontPuckupItemList(Item item)
    {
        if(dontPuckupItemList.contains(BuiltInRegistries.ITEM.getKey(item).toString())) return;
        dontPuckupItemList.add(BuiltInRegistries.ITEM.getKey(item).toString());
    }

    public void RemoveElementToDontPuckupItemList(Item item)
    {
        dontPuckupItemList.remove(BuiltInRegistries.ITEM.getKey(item).toString());
    }



    //=================================================================================


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



    public void addBlockElement(Block block)
    {
        String id = BuiltInRegistries.BLOCK.getKey(block).toString();

        if (!isBlockExists(id))
        {
            blockSettings.add(new BlockSetting(id));
        }
    }

    public void addBlockElement(String id, boolean isPlace, boolean isBreak)
    {

        if (!isBlockExists(id))
        {
            blockSettings.add(new BlockSetting(id, isPlace, isBreak));
        }
    }

    // Вспомогательный метод для проверки существования блока
    public boolean isBlockExists(String id)
    {
        for (BlockSetting setting : blockSettings)
        {
            if (setting.idBlock.equals(id))
            {
                return true;
            }
        }
        return false;
    }

    public void RemoveBlockElement(Block block)
    {
        String id = BuiltInRegistries.BLOCK.getKey(block).toString();

        blockSettings.removeIf(setting -> setting.idBlock.equals(id));
    }

    public void SetSettingPlaceToElementBlock(String id , boolean isPlace)
    {
        for (BlockSetting setting : blockSettings)
        {
            if (setting.idBlock.equals(id))
            {
                setting.isPlaceBlock = isPlace;
                return;
            }
        }
    }

    public void SetSettingBreakToElementBlock(String id , boolean isBreak)
    {
        for (BlockSetting setting : blockSettings)
        {
            if (setting.idBlock.equals(id))
            {
                setting.isBreakBlock = isBreak;
                return;
            }
        }
    }

    public boolean canPlaceBlock(String id)
    {
        for (BlockSetting setting : blockSettings)
        {
            if (setting.idBlock.equals(id))
            {
                return setting.isPlaceBlock;
            }
        }
        return false;
    }

    public boolean canBreakBlock(String id)
    {
        for (BlockSetting setting : blockSettings)
        {
            if (setting.idBlock.equals(id))
            {
                return setting.isBreakBlock;
            }
        }
        return false;
    }

    public void updateBlockElement(String id, boolean isPlace, boolean isBreak)
    {
        for (BlockSetting setting : blockSettings)
        {
            if (setting.idBlock.equals(id))
            {
                setting.isPlaceBlock = isPlace;
                setting.isBreakBlock = isBreak;
                return;
            }
        }
        // Если не найден, добавляем новый
        blockSettings.add(new BlockSetting(id, isPlace, isBreak));
    }

    public List<String> GetAllID()
    {
        List<String> arrayID = new ArrayList<>();
        for (BlockSetting blockSetting : blockSettings)
        {
            arrayID.add(blockSetting.idBlock);
        }
        return arrayID;
    }




    public void addItemElement(Item item)
    {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();

        if (!isItemExists(id))
        {
            itemSettings.add(new ItemSetting(id));
        }
    }

    public void addItemElement(String id, boolean canDrop, boolean canPickup)
    {
        if (!isItemExists(id))
        {
            itemSettings.add(new ItemSetting(id, canDrop, canPickup));
        }
    }

    // Вспомогательный метод для проверки существования предмета
    public boolean isItemExists(String id)
    {
        for (ItemSetting setting : itemSettings)
        {
            if (setting.idItem.equals(id))
            {
                return true;
            }
        }
        return false;
    }

    public void removeItemElement(Item item)
    {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();

        itemSettings.removeIf(setting -> setting.idItem.equals(id));
    }

    public void setSettingDropToElementItem(String id, boolean canDrop)
    {
        for (ItemSetting setting : itemSettings)
        {
            if (setting.idItem.equals(id))
            {
                setting.canDrop = canDrop;
                return;
            }
        }
    }

    public void setSettingPickupToElementItem(String id, boolean canPickup)
    {
        for (ItemSetting setting : itemSettings)
        {
            if (setting.idItem.equals(id))
            {
                setting.canPickup = canPickup;
                return;
            }
        }
    }

    public boolean canDropItem(String id)
    {
        for (ItemSetting setting : itemSettings)
        {
            if (setting.idItem.equals(id))
            {
                return setting.canDrop;
            }
        }
        return false; // По умолчанию предметы могут выпадать
    }

    public boolean canPickupItem(String id)
    {
        for (ItemSetting setting : itemSettings)
        {
            if (setting.idItem.equals(id))
            {
                return setting.canPickup;
            }
        }
        return false; // По умолчанию предметы можно подбирать
    }

    public List<String> getAllItemIDs()
    {
        List<String> arrayID = new ArrayList<>();
        for (ItemSetting itemSetting : itemSettings)
        {
            arrayID.add(itemSetting.idItem);
        }
        return arrayID;
    }
}