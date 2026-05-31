package net.fernyam.chaosmania.data;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerSettings {

    public enum ListType {
        BLACK_LIST,
        WHITE_LIST
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


    private static class SeedSetting {
        private final String idSeed;
        private boolean canPlant;

        public SeedSetting(String idItem, boolean canPlant ) {
            this.idSeed = idItem;
            this.canPlant = canPlant;
        }

        public SeedSetting(String idSeed) {
            this(idSeed, false);
        }

        public String getIdSeed() { return idSeed; }
        public boolean canPlant() { return canPlant; }
        public void setPlant(boolean canPlant) { this.canPlant = canPlant; }
        public void togglePlant() { this.canPlant = !this.canPlant; }
    }

    // ==================== Внутренний класс VillagerSetting ====================
    private static class VillagerSetting {
        private final String id;        // ResourceLocation профессии, например "minecraft:farmer"
        private String name;            // Отображаемое имя профессии
        private boolean canTradeVillager;       // Может ли торговать

        public VillagerSetting(String id, String name, boolean canTradeVillager ) {
            this.id = id;
            this.name = name;
            this.canTradeVillager = canTradeVillager;
        }

        public VillagerSetting(String id, String name) {
            this(id, name, false );
        }

        public VillagerSetting(String id) {
            this(id, getProfessionDisplayName(id), false );
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public boolean canTrade() { return canTradeVillager; }
        public void setTrade(boolean canTrade) { this.canTradeVillager = canTrade; }
        public void toggleTrade() { this.canTradeVillager = !this.canTradeVillager; }
    }

    // ==================== Вспомогательный метод для получения имени профессии ====================
    private static String getProfessionDisplayName(String professionId) {
        try {
            ResourceLocation id = ResourceLocation.parse(professionId);
            VillagerProfession profession = BuiltInRegistries.VILLAGER_PROFESSION.get(id);
            if (profession != null) {
                // Получаем локализованное имя
                return Component.translatable(profession.toString()).getString();
            }
        } catch (Exception e) {
            ChaosManiaMod.LOGGER.error("Не удалось получить имя профессии: " + professionId, e);
        }
        return professionId; // fallback
    }

    private String name;
    private String uuidPlayer;

    private ListType DisablePlaseBlockListType;
    private ListType DisableBreakBlockListType;
    private boolean isDisablePlaceBlock;
    private boolean isDisableBreakBlock;
    private List<BlockSetting> blockSettings;

    private ListType DisableDropItemListType;
    private ListType DisablePickupItemListType;
    private boolean isDisableDropItem;
    private boolean isDisablePickupItem;
    private List<ItemSetting> itemSettings;

    private ListType DisablePlantingSeedListType;
    private boolean isDisablePlantingSeed;
    private List<SeedSetting> seedSettings;

    private ListType DisableTradingVillagerListType;
    private boolean isDisableTradingVillager;
    private boolean isDisableTradingWanderingTrader;
    private List<VillagerSetting> villagerSetting;

    // Конструктор по умолчанию (нужен для Gson)
    public PlayerSettings() {}

    public PlayerSettings(String name, String uuidPlayer) {
        this.name = name;
        this.uuidPlayer = uuidPlayer;

        this.isDisablePlaceBlock = false;
        this.isDisableBreakBlock = false;
        this.isDisableDropItem = false;
        this.isDisablePickupItem = false;
        this.isDisablePlantingSeed = false;
        this.isDisableTradingVillager = false;
        this.isDisableTradingWanderingTrader = false;

        this.blockSettings = new ArrayList<>();
        this.itemSettings = new ArrayList<>();
        this.seedSettings = new ArrayList<>();
        this.villagerSetting = new ArrayList<>();

        this.DisableDropItemListType = ListType.BLACK_LIST;
        this.DisablePickupItemListType = ListType.BLACK_LIST;
        this.DisablePlaseBlockListType = ListType.BLACK_LIST;
        this.DisableBreakBlockListType = ListType.BLACK_LIST;
        this.DisablePlantingSeedListType = ListType.BLACK_LIST;
        this.DisableTradingVillagerListType = ListType.BLACK_LIST;
    }

    public PlayerSettings(String name, UUID uuidPlayer) {
        this.name = name;
        this.uuidPlayer = uuidPlayer.toString();

        this.isDisablePlaceBlock = false;
        this.isDisableBreakBlock = false;
        this.isDisableDropItem = false;
        this.isDisablePickupItem = false;
        this.isDisablePlantingSeed = false;
        this.isDisableTradingVillager = false;
        this.isDisableTradingWanderingTrader = false;

        this.blockSettings = new ArrayList<>();
        this.itemSettings = new ArrayList<>();
        this.seedSettings = new ArrayList<>();
        this.villagerSetting = new ArrayList<>();

        this.DisableDropItemListType = ListType.BLACK_LIST;
        this.DisablePickupItemListType = ListType.BLACK_LIST;
        this.DisablePlaseBlockListType = ListType.BLACK_LIST;
        this.DisableBreakBlockListType = ListType.BLACK_LIST;
        this.DisablePlantingSeedListType = ListType.BLACK_LIST;
        this.DisableTradingVillagerListType = ListType.BLACK_LIST;
    }

    // ==================== Геттеры и сеттеры ====================
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUuidPlayer() { return uuidPlayer; }
    public void setUuidPlayer(String uuidPlayer) { this.uuidPlayer = uuidPlayer; }
    public void setUuidPlayer(UUID uuidPlayer) { this.uuidPlayer = uuidPlayer.toString(); }

    public List<BlockSetting> getBlockSettings() { return blockSettings; }
    public List<ItemSetting> getItemSettings() { return itemSettings; }

    // ==================== Глобальные настройки блоков ====================
    public void setDisablePlaceBlock(boolean disablePlaceBlock) { isDisablePlaceBlock = disablePlaceBlock; }
    public void setDisableBreakBlock(boolean disableBreakBlock) { isDisableBreakBlock = disableBreakBlock; }
    public void toggleDisablePlaceBlock() { isDisablePlaceBlock = !isDisablePlaceBlock; }
    public void toggleDisableBreakBlock() { isDisableBreakBlock = !isDisableBreakBlock; }
    public boolean getDisablePlaceBlock() { return isDisablePlaceBlock; }
    public boolean getDisableBreakBlock() { return isDisableBreakBlock; }

    // ==================== Глобальные настройки предметов ====================
    public void setDisableDropItem(boolean disableDropItem) { isDisableDropItem = disableDropItem; }
    public void setDisablePickupItem(boolean disablePickupItem) { isDisablePickupItem = disablePickupItem; }
    public void toggleDisableItemDrop() { isDisableDropItem = !isDisableDropItem; }
    public void toggleDisableItemPickup() { isDisablePickupItem = !isDisablePickupItem; }
    public boolean getDisableDropItem() { return isDisableDropItem; }
    public boolean getDisablePickupItem() { return isDisablePickupItem; }

    // ==================== Глобальные настройки посадки семян ====================
    public boolean getDisablePlantingSeed() { return isDisablePlantingSeed; }
    public void setDisablePlantingSeed(boolean disablePlantingSeed) { isDisablePlantingSeed = disablePlantingSeed; }
    public void toggleDisablePlantingSeed() { isDisablePlantingSeed = !isDisablePlantingSeed; }

    // ==================== Глобальные настройки торговли ====================
    public boolean getDisableTradingVillager() { return isDisableTradingVillager; }
    public void setDisableTradingVillager(boolean disableTradingVillager) { isDisableTradingVillager = disableTradingVillager; }
    public void toggleDisableTradingVillager() { isDisableTradingVillager = !isDisableTradingVillager; }

    public boolean getDisableTradingWanderingTrader() { return isDisableTradingWanderingTrader; }
    public void setDisableTradingWanderingTrader(boolean disableTradingWanderingTrader) { isDisableTradingWanderingTrader = disableTradingWanderingTrader; }
    public void toggleDisableTradingWanderingTrader() { isDisableTradingWanderingTrader = !isDisableTradingWanderingTrader; }

    // ==================== ListType для блоков ====================
    public ListType getDisablePlaceBlockListType() { return DisablePlaseBlockListType; }
    public void setDisablePlaceBlockListType(ListType listType) { this.DisablePlaseBlockListType = listType; }
    public void toggleDisablePlaceBlockListType() {
        if (DisablePlaseBlockListType == ListType.BLACK_LIST) {
            DisablePlaseBlockListType = ListType.WHITE_LIST;
        } else {
            DisablePlaseBlockListType = ListType.BLACK_LIST;
        }
    }

    public ListType getDisableBreakBlockListType() { return DisableBreakBlockListType; }
    public void setDisableBreakBlockListType(ListType listType) { this.DisableBreakBlockListType = listType; }
    public void toggleDisableBreakBlockListType() {
        if (DisableBreakBlockListType == ListType.BLACK_LIST) {
            DisableBreakBlockListType = ListType.WHITE_LIST;
        } else {
            DisableBreakBlockListType = ListType.BLACK_LIST;
        }
    }

    // ==================== ListType для предметов ====================
    public ListType getDisableDropItemListType() { return DisableDropItemListType; }
    public void setDisableDropItemListType(ListType listType) { this.DisableDropItemListType = listType; }
    public void toggleDisableDropItemListType() {
        if (DisableDropItemListType == ListType.BLACK_LIST) {
            DisableDropItemListType = ListType.WHITE_LIST;
        } else {
            DisableDropItemListType = ListType.BLACK_LIST;
        }
    }

    public ListType getDisablePickupItemListType() { return DisablePickupItemListType; }
    public void setDisablePickupItemListType(ListType listType) { this.DisablePickupItemListType = listType; }
    public void toggleDisablePickupItemListType() {
        if (DisablePickupItemListType == ListType.BLACK_LIST) {
            DisablePickupItemListType = ListType.WHITE_LIST;
        } else {
            DisablePickupItemListType = ListType.BLACK_LIST;
        }
    }

    // ==================== ListType для посадки семян ====================
    public ListType getDisablePlantingSeedListType() { return DisablePlantingSeedListType; }
    public void setDisablePlantingSeedListType(ListType listType) { this.DisablePlantingSeedListType = listType; }
    public void toggleDisablePlantingSeedListType() {
        if (DisablePlantingSeedListType == ListType.BLACK_LIST) {
            DisablePlantingSeedListType = ListType.WHITE_LIST;
        } else {
            DisablePlantingSeedListType = ListType.BLACK_LIST;
        }
    }

    // ==================== ListType для торговли ====================
    public ListType getDisableTradingVillagerListType() { return DisableTradingVillagerListType; }
    public void setDisableTradingVillagerListType(ListType listType) { this.DisableTradingVillagerListType = listType; }
    public void toggleDisableTradingVillagerListType() {
        if (DisableTradingVillagerListType == ListType.BLACK_LIST) {
            DisableTradingVillagerListType = ListType.WHITE_LIST;
        } else {
            DisableTradingVillagerListType = ListType.BLACK_LIST;
        }
    }

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

    public void removeBlockElement(String id) {
        blockSettings.removeIf(setting -> setting.getIdBlock().equals(id));
    }

    public void toggleBlockPlace(String id) {
        getOrCreateBlockSetting(id).togglePlace();
    }

    public void toggleBlockBreak(String id) {
        getOrCreateBlockSetting(id).toggleBreak();
    }

    public void setBlockPlace(String id, boolean canPlace) {
        getOrCreateBlockSetting(id).setPlace(canPlace);
    }

    public void setBlockBreak(String id, boolean canBreak) {
        getOrCreateBlockSetting(id).setBreak(canBreak);
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

    public List<String> getAllBlockID() {
        List<String> listId = new ArrayList<>();
        for (BlockSetting settingBlock : blockSettings) {
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

    public void removeItemElement(String id) {
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

    public List<String> getAllItemID() {
        List<String> listId = new ArrayList<>();
        for (ItemSetting settingItem : itemSettings) {
            listId.add(settingItem.getIdItem());
        }
        return listId;
    }

    // ==================== Работа с семенами ====================
    private SeedSetting getOrCreateSeedSetting(String id) {
        for (SeedSetting setting : seedSettings) {
            if (setting.getIdSeed().equals(id)) {
                return setting;
            }
        }
        SeedSetting newSetting = new SeedSetting(id);
        seedSettings.add(newSetting);
        return newSetting;
    }

    public void addSeedElement(Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        if (!isPlantSeedExists(id)) {
            seedSettings.add(new SeedSetting(id));
        }
    }

    public void removeSeedElement(Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        seedSettings.removeIf(setting -> setting.getIdSeed().equals(id));
    }

    public boolean isPlantSeedExists(String seedId) {
        return seedSettings.stream().anyMatch(setting -> setting.getIdSeed().equals(seedId));
    }

    public void clearPlantSeedList() {
        seedSettings.clear();
    }

    public void toggleSeedPlan(String id) {
        getOrCreateSeedSetting(id).togglePlant();
    }

    public boolean canPlanSeed(String id) {
        return seedSettings.stream()
                .filter(setting -> setting.getIdSeed().equals(id))
                .findFirst()
                .map(SeedSetting::canPlant)
                .orElse(false);
    }

    public List<String> getAllSeedID() {
        List<String> listId = new ArrayList<>();
        for (SeedSetting settingItem : seedSettings) {
            listId.add(settingItem.getIdSeed());
        }
        return listId;
    }

// ==================== Работа с жителями ====================

    private VillagerSetting getOrCreateVillagerSetting(String id) {
        for (VillagerSetting setting : villagerSetting) {
            if (setting.getId().equals(id)) {
                return setting;
            }
        }
        VillagerSetting newSetting = new VillagerSetting(id);
        villagerSetting.add(newSetting);
        return newSetting;
    }

    public void addVillagerProfession(String id) {
        if (!isVillagerProfessionExists(id)) {
            villagerSetting.add(new VillagerSetting(id));
        }
    }

    public void addVillagerProfession(VillagerProfession profession) {
        String id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
        addVillagerProfession(id);
    }


    public void removeVillagerProfession(String professionId) {
        villagerSetting.removeIf(setting -> setting.getId().equals(professionId));
    }

    public void removeVillagerProfession(VillagerProfession profession) {
        String id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
        removeVillagerProfession(id);
    }

    public boolean isVillagerProfessionExists(String professionId) {
        return villagerSetting.stream().anyMatch(setting -> setting.getId().equals(professionId));
    }

    public boolean isVillagerProfessionExists(VillagerProfession profession) {
        String id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
        return isVillagerProfessionExists(id);
    }

    public void toggleVillagerTrade(String professionId) {
        getOrCreateVillagerSetting(professionId).toggleTrade();
    }

    public void toggleVillagerTrade(VillagerProfession profession) {
        String id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
        toggleVillagerTrade(id);
    }
    public void setVillagerTrade(String professionId, boolean canTrade) {
        getOrCreateVillagerSetting(professionId).setTrade(canTrade);
    }

    /**
     * Проверить, может ли житель данной профессии торговать
     * Учитывает глобальный флаг и тип списка (черный/белый)
     */
    public boolean canTradeWithVillager(String professionId) {
        // Если глобальный запрет выключен — торговать можно всегда
        if (!isDisableTradingVillager) {
            return true;
        }

        boolean isInList = isVillagerProfessionExists(professionId);
        boolean canTradeSetting = villagerSetting.stream()
                .filter(setting -> setting.getId().equals(professionId))
                .findFirst()
                .map(VillagerSetting::canTrade)
                .orElse(false);

        // BLACK_LIST: запрещены все, кроме отмеченных canTradeVillager = true
        if (DisableTradingVillagerListType == ListType.BLACK_LIST) {
            return !isInList || canTradeSetting;
        }
        // WHITE_LIST: разрешены только отмеченные canTradeVillager = true
        else {
            return isInList && canTradeSetting;
        }
    }

    /**
     * Проверить, может ли житель данной профессии торговать (перегрузка)
     */
    public boolean canTradeWithVillager(VillagerProfession profession) {
        String id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
        return canTradeWithVillager(id);
    }

    /**
     * Очистить список всех профессий
     */
    public void clearVillagerProfessionList() {
        villagerSetting.clear();
    }

    /**
     * Получить все ID профессий в списке
     */
    public List<String> getAllVillagerProfessionIds() {
        List<String> listId = new ArrayList<>();
        for (VillagerSetting setting : villagerSetting) {
            listId.add(setting.getId());
        }
        return listId;
    }

}