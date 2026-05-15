package net.fernyam.chaosmania.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.UUID;

public class PlayerSettings {
    private String name;
    private String uuidPlayer;

    private boolean isDisablePlaceBlock;
    private boolean isDisableBreakBlock;
    private List<String> dontPlaceBlockList;
    private List<String> dontBreakBlockList;

    private  boolean isDisableItemDrop;
    private  boolean isDisableItemPickup;
    private List<String> dontDropItemList;
    private List<String> dontPuckupItemList;

    private boolean isDisablePlantingSeed;
    private boolean isDisableVillagerTrading;


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

    public void setDisablePlantingSeed(boolean disablePlantingSeed) {
        isDisablePlantingSeed = disablePlantingSeed;
    }

    public void setDisableVillagerTrading(boolean disableVillagerTrading) {
        isDisableVillagerTrading = disableVillagerTrading;
    }

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
}