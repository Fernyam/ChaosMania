package net.fernyam.chaosmania.data;

import net.minecraft.core.registries.BuiltInRegistries;
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

    private  boolean isDisableItemItemDrop;
    private  boolean isDisableItemPickup;
    private List<String> dontDropItemList;
    private List<String> dontPuckupItemList;

    private boolean isDisablePlantingSeed;
    private boolean isDisableVillagerTrading;


    // Конструктор по умолчанию (нужен для Gson)
    public PlayerSettings() {}

    // Конструктор для удобного создания
    public PlayerSettings(String name, UUID uuidPlayer, boolean isDisablePlaceBlock,
                          boolean isDisableBreakBlock, List<String> dontPlaceBlockList,
                          List<String> dontBreakBlockList) {
        this.name = name;
        this.uuidPlayer = uuidPlayer.toString();
        this.isDisablePlaceBlock = isDisablePlaceBlock;
        this.isDisableBreakBlock = isDisableBreakBlock;
        this.dontPlaceBlockList = dontPlaceBlockList;
        this.dontBreakBlockList = dontBreakBlockList;
    }

    public PlayerSettings(String name, String uuidPlayer, boolean isDisablePlaceBlock,
                          boolean isDisableBreakBlock, List<String> dontPlaceBlockList,
                          List<String> dontBreakBlockList) {
        this.name = name;
        this.uuidPlayer = uuidPlayer;
        this.isDisablePlaceBlock = isDisablePlaceBlock;
        this.isDisableBreakBlock = isDisableBreakBlock;
        this.dontPlaceBlockList = dontPlaceBlockList;
        this.dontBreakBlockList = dontBreakBlockList;
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

    public void AddElementToDontBreakBlockList(Block block)
    {
        dontBreakBlockList.add(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public void RemoveElementToDontBreakBlockList(Block block)
    {
        dontBreakBlockList.remove(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public void AddElementToDontPlaceBlockList(Block block)
    {
        dontPlaceBlockList.add(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public void RemoveElementToDontPlaceBlockList(Block block)
    {
        dontPlaceBlockList.remove(BuiltInRegistries.BLOCK.getKey(block).toString());
    }
}