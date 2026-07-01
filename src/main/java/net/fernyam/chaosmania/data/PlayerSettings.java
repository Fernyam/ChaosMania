package net.fernyam.chaosmania.data;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
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

    // ==================== Внутренние классы для хранения настроек ====================

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

        public SeedSetting(String idItem, boolean canPlant) {
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

    private static class VillagerSetting {
        private final String id;
        private String name;
        private boolean canTradeVillager;

        public VillagerSetting(String id, String name, boolean canTradeVillager) {
            this.id = id;
            this.name = name;
            this.canTradeVillager = canTradeVillager;
        }

        public VillagerSetting(String id, String name) {
            this(id, name, false);
        }

        public VillagerSetting(String id) {
            this(id, getProfessionDisplayName(id), false);
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public boolean canTrade() { return canTradeVillager; }
        public void setTrade(boolean canTrade) { this.canTradeVillager = canTrade; }
        public void toggleTrade() { this.canTradeVillager = !this.canTradeVillager; }

        private static String getProfessionDisplayName(String professionId) {
            try {
                ResourceLocation id = ResourceLocation.parse(professionId);
                VillagerProfession profession = BuiltInRegistries.VILLAGER_PROFESSION.get(id);
                if (profession != null) {
                    String translationKey = "entity." + id.getNamespace() + ".villager." + id.getPath();
                    Component translated = Component.translatable(translationKey);
                    String result = translated.getString();
                    if (!result.equals(translationKey)) {
                        return result;
                    }
                    String path = id.getPath();
                    return path.substring(0, 1).toUpperCase() + path.substring(1).replace("_", " ");
                }
            } catch (Exception e) {
                ChaosManiaMod.LOGGER.error("Failed to get profession display name: " + professionId, e);
            }
            return professionId;
        }
    }

    private static class ModSetting {
        private final String idMod;
        private boolean canLoad;

        public ModSetting(String idMod, boolean canLoad) {
            this.idMod = idMod;
            this.canLoad = canLoad;
        }

        public ModSetting(String idMod) {
            this(idMod, false);
        }

        public String getIdMod() { return idMod; }
        public boolean canLoad() {return canLoad; }
        public void setLoad(boolean canLoad) { this.canLoad = canLoad; }
        public void toggleLoad() { this.canLoad = !this.canLoad; }
    }

    private static class AnimalSetting {
        private final String id;        // ResourceLocation животного, например "minecraft:cow"
        private String name;            // Отображаемое имя
        private boolean canBreed;       // Может ли размножаться
        private boolean canSpawn;       // Может ли появляться (опционально)

        public AnimalSetting(String id, String name, boolean canBreed, boolean canSpawn) {
            this.id = id;
            this.name = name;
            this.canBreed = canBreed;
            this.canSpawn = canSpawn;
        }

        public AnimalSetting(String id, String name) {
            this(id, name, false, false);
        }

        public AnimalSetting(String id) {
            this(id, getAnimalDisplayName(id), false, false);
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public boolean canBreed() { return canBreed; }
        public boolean canSpawn() { return canSpawn; }
        public void setBreed(boolean canBreed) { this.canBreed = canBreed; }
        public void setSpawn(boolean canSpawn) { this.canSpawn = canSpawn; }
        public void toggleBreed() { this.canBreed = !this.canBreed; }
        public void toggleSpawn() { this.canSpawn = !this.canSpawn; }
    }



    // ==================== Поля класса ====================

    private String name;
    private String uuidPlayer;      // Для JSON сериализации
    private transient String uuid;    // Для рантайма (не сохраняется в JSON)

    // Настройки блоков
    private ListType disablePlaceBlockListType;
    private ListType disableBreakBlockListType;
    private boolean isDisablePlaceBlock;
    private boolean isDisableBreakBlock;
    private List<BlockSetting> blockSettings;

    // Настройки предметов
    private ListType disableDropItemListType;
    private ListType disablePickupItemListType;
    private boolean isDisableDropItem;
    private boolean isDisablePickupItem;
    private List<ItemSetting> itemSettings;

    // Настройки семян
    private ListType disablePlantingSeedListType;
    private boolean isDisablePlantingSeed;
    private List<SeedSetting> seedSettings;

    // Настройки торговли
    private ListType disableTradingVillagerListType;
    private boolean isDisableTradingVillager;
    private boolean isDisableTradingWanderingTrader;
    private List<VillagerSetting> villagerSettings;

    //Настройка размножения
    private ListType disableBreedAnimalListType;
    private ListType disableSpawnAnimalListType;
    private boolean isDisableBreedAnimal;
    private boolean isDisableSpawnAnimal;
    private List<AnimalSetting> animalSettings;

    //Настройки использования модов

    private ListType disableModListType;
    private boolean isDisableLoadingMod;
    private List<ModSetting> modSettings;

    // ==================== Конструкторы ====================

    // Конструктор по умолчанию (нужен для Gson)
    public PlayerSettings() {}

    public PlayerSettings(String name, String uuidPlayer) {
        this.name = name;
        this.uuidPlayer = uuidPlayer;
        this.uuid = uuidPlayer;
        initDefaults();
    }

//    public PlayerSettings(String name, UUID uuid) {
//        this.name = name;
//        this.uuidPlayer = uuid.toString();
//        this.uuid = uuid;
//        initDefaults();
//    }

    private void initDefaults() {
        this.isDisablePlaceBlock = false;
        this.isDisableBreakBlock = false;
        this.isDisableDropItem = false;
        this.isDisablePickupItem = false;
        this.isDisablePlantingSeed = false;
        this.isDisableTradingVillager = false;
        this.isDisableTradingWanderingTrader = false;
        this.isDisableLoadingMod = false;

        this.blockSettings = new ArrayList<>();
        this.itemSettings = new ArrayList<>();
        this.seedSettings = new ArrayList<>();
        this.villagerSettings = new ArrayList<>();
        this.modSettings = new ArrayList<>();

        this.disableDropItemListType = ListType.BLACK_LIST;
        this.disablePickupItemListType = ListType.BLACK_LIST;
        this.disablePlaceBlockListType = ListType.BLACK_LIST;
        this.disableBreakBlockListType = ListType.BLACK_LIST;
        this.disablePlantingSeedListType = ListType.BLACK_LIST;
        this.disableTradingVillagerListType = ListType.BLACK_LIST;
        this.disableModListType = ListType.BLACK_LIST;




        this.isDisableBreedAnimal = false;
        this.isDisableSpawnAnimal = false;
        this.animalSettings = new ArrayList<>();
        this.disableBreedAnimalListType = ListType.BLACK_LIST;
        this.disableSpawnAnimalListType = ListType.BLACK_LIST;

    }

    // ==================== Геттеры и сеттеры ====================

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUuidPlayer() { return uuidPlayer; }
    public void setUuidPlayer(String uuidPlayer) {
        this.uuidPlayer = uuidPlayer;
        this.uuid = uuidPlayer;
    }



    public List<BlockSetting> getBlockSettings() { return blockSettings; }
    public List<ItemSetting> getItemSettings() { return itemSettings; }
    public List<SeedSetting> getSeedSettings() { return seedSettings; }
    public List<VillagerSetting> getVillagerSettings() { return villagerSettings; }

    // ==================== Глобальные настройки блоков ====================

    public boolean isDisablePlaceBlock() { return isDisablePlaceBlock; }
    public void toggleDisablePlaceBlock() { isDisablePlaceBlock = !isDisablePlaceBlock; }

    public boolean isDisableBreakBlock() { return isDisableBreakBlock; }
    public void toggleDisableBreakBlock() { isDisableBreakBlock = !isDisableBreakBlock; }

    // ==================== Глобальные настройки предметов ====================

    public boolean isDisableDropItem() { return isDisableDropItem; }
    public void toggleDisableItemDrop() { isDisableDropItem = !isDisableDropItem; }

    public boolean isDisablePickupItem() { return isDisablePickupItem; }
    public void toggleDisableItemPickup() { isDisablePickupItem = !isDisablePickupItem; }

    // ==================== Глобальные настройки посадки семян ====================

    public boolean isDisablePlantingSeed() { return isDisablePlantingSeed; }
    public void toggleDisablePlantingSeed() { isDisablePlantingSeed = !isDisablePlantingSeed; }

    // ==================== Глобальные настройки торговли ====================

    public boolean isDisableTradingVillager() { return isDisableTradingVillager; }
    public void toggleDisableTradingVillager() { isDisableTradingVillager = !isDisableTradingVillager; }

    public boolean isDisableTradingWanderingTrader() { return isDisableTradingWanderingTrader; }
    public void toggleDisableTradingWanderingTrader() { isDisableTradingWanderingTrader = !isDisableTradingWanderingTrader; }

    // ==================== ListType для блоков ====================

    public ListType getDisablePlaceBlockListType() { return disablePlaceBlockListType; }
    public void toggleDisablePlaceBlockListType() {
        disablePlaceBlockListType = (disablePlaceBlockListType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public ListType getDisableBreakBlockListType() { return disableBreakBlockListType; }
    public void toggleDisableBreakBlockListType() {
        disableBreakBlockListType = (disableBreakBlockListType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    // ==================== ListType для предметов ====================

    public ListType getDisableDropItemListType() { return disableDropItemListType; }
    public void toggleDisableDropItemListType() {
        disableDropItemListType = (disableDropItemListType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public ListType getDisablePickupItemListType() { return disablePickupItemListType; }
    public void toggleDisablePickupItemListType() {
        disablePickupItemListType = (disablePickupItemListType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    // ==================== ListType для посадки семян ====================

    public ListType getDisablePlantingSeedListType() { return disablePlantingSeedListType; }
    public void toggleDisablePlantingSeedListType() {
        disablePlantingSeedListType = (disablePlantingSeedListType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    // ==================== ListType для торговли ====================

    public ListType getDisableTradingVillagerListType() { return disableTradingVillagerListType; }
    public void toggleDisableTradingVillagerListType() {
        disableTradingVillagerListType = (disableTradingVillagerListType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
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

    public void addBlockElement(String id) {
        if (!isBlockExists(id)) {
            blockSettings.add(new BlockSetting(id));
        }
    }

    public void addBlockElement(Block block) {
        addBlockElement(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public boolean isBlockExists(String id) {
        return blockSettings.stream().anyMatch(setting -> setting.getIdBlock().equals(id));
    }

    public void removeBlockElement(String id) {
        blockSettings.removeIf(setting -> setting.getIdBlock().equals(id));
    }

    public void removeBlockElement(Block block) {
        removeBlockElement(BuiltInRegistries.BLOCK.getKey(block).toString());
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

    public List<String> getAllBlockIds() {
        return blockSettings.stream()
                .map(BlockSetting::getIdBlock)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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

    public void addItemElement(String id) {
        if (!isItemExists(id)) {
            itemSettings.add(new ItemSetting(id));
        }
    }

    public void addItemElement(Item item) {
        addItemElement(BuiltInRegistries.ITEM.getKey(item).toString());
    }

    public boolean isItemExists(String id) {
        return itemSettings.stream().anyMatch(setting -> setting.getIdItem().equals(id));
    }

    public void removeItemElement(String id) {
        itemSettings.removeIf(setting -> setting.getIdItem().equals(id));
    }

    public void removeItemElement(Item item) {
        removeItemElement(BuiltInRegistries.ITEM.getKey(item).toString());
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

    public List<String> getAllItemIds() {
        return itemSettings.stream()
                .map(ItemSetting::getIdItem)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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

    public void addSeedElement(String id) {
        if (!isSeedExists(id)) {
            seedSettings.add(new SeedSetting(id));
        }
    }

    public void addSeedElement(Item seed) {
        addSeedElement(BuiltInRegistries.ITEM.getKey(seed).toString());
    }

    public boolean isSeedExists(String seedId) {
        return seedSettings.stream().anyMatch(setting -> setting.getIdSeed().equals(seedId));
    }

    public void removeSeedElement(String id) {
        seedSettings.removeIf(setting -> setting.getIdSeed().equals(id));
    }

    public void removeSeedElement(Item seed) {
        removeSeedElement(BuiltInRegistries.ITEM.getKey(seed).toString());
    }

    public void clearSeedList() {
        seedSettings.clear();
    }

    public void toggleSeedPlant(String id) {
        getOrCreateSeedSetting(id).togglePlant();
    }

    public boolean canPlantSeed(String id) {
        return seedSettings.stream()
                .filter(setting -> setting.getIdSeed().equals(id))
                .findFirst()
                .map(SeedSetting::canPlant)
                .orElse(false);
    }

    public List<String> getAllSeedIds() {
        return seedSettings.stream()
                .map(SeedSetting::getIdSeed)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // ==================== Работа с жителями ====================

    private VillagerSetting getOrCreateVillagerSetting(String id) {
        for (VillagerSetting setting : villagerSettings) {
            if (setting.getId().equals(id)) {
                return setting;
            }
        }
        VillagerSetting newSetting = new VillagerSetting(id);
        villagerSettings.add(newSetting);
        return newSetting;
    }

    public void addVillagerProfession(String id) {
        if (!isVillagerProfessionExists(id)) {
            villagerSettings.add(new VillagerSetting(id));
        }
    }

    public void addVillagerProfession(VillagerProfession profession) {
        addVillagerProfession(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString());
    }

    public void removeVillagerProfession(String professionId) {
        villagerSettings.removeIf(setting -> setting.getId().equals(professionId));
    }

    public void removeVillagerProfession(VillagerProfession profession) {
        removeVillagerProfession(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString());
    }

    public boolean isVillagerProfessionExists(String professionId) {
        return villagerSettings.stream().anyMatch(setting -> setting.getId().equals(professionId));
    }

    public boolean isVillagerProfessionExists(VillagerProfession profession) {
        return isVillagerProfessionExists(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString());
    }

    public void toggleVillagerTrade(String professionId) {
        getOrCreateVillagerSetting(professionId).toggleTrade();
    }

    public void toggleVillagerTrade(VillagerProfession profession) {
        toggleVillagerTrade(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString());
    }

    public void setVillagerTrade(String professionId, boolean canTrade) {
        getOrCreateVillagerSetting(professionId).setTrade(canTrade);
    }

    public boolean canTradeWithVillager(String professionId) {

        boolean isInList = isVillagerProfessionExists(professionId);
        boolean canTradeSetting = villagerSettings.stream()
                .filter(setting -> setting.getId().equals(professionId))
                .findFirst()
                .map(VillagerSetting::canTrade)
                .orElse(false);

        if (disableTradingVillagerListType == ListType.BLACK_LIST) {
            return !isInList || canTradeSetting;
        } else {
            return isInList && canTradeSetting;
        }
    }

    public boolean canTradeWithVillager(VillagerProfession profession) {
        return canTradeWithVillager(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString());
    }

    public void clearVillagerProfessionList() {
        villagerSettings.clear();
    }

    public List<String> getAllVillagerProfessionIds() {
        return villagerSettings.stream()
                .map(VillagerSetting::getId)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }


    // ==================== Глобальные настройки животных ====================

    public boolean isDisableBreedAnimal() { return isDisableBreedAnimal; }
    public void setDisableBreedAnimal(boolean disable) { isDisableBreedAnimal = disable; }
    public void toggleDisableBreedAnimal() { isDisableBreedAnimal = !isDisableBreedAnimal; }

    public boolean isDisableSpawnAnimal() { return isDisableSpawnAnimal; }
    public void setDisableSpawnAnimal(boolean disable) { isDisableSpawnAnimal = disable; }
    public void toggleDisableSpawnAnimal() { isDisableSpawnAnimal = !isDisableSpawnAnimal; }

    public ListType getDisableBreedAnimalListType() { return disableBreedAnimalListType; }
    public void setDisableBreedAnimalListType(ListType type) { this.disableBreedAnimalListType = type; }
    public void toggleDisableBreedAnimalListType() {
        disableBreedAnimalListType = (disableBreedAnimalListType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public ListType getDisableSpawnAnimalListType() { return disableSpawnAnimalListType; }
    public void setDisableSpawnAnimalListType(ListType type) { this.disableSpawnAnimalListType = type; }
    public void toggleDisableSpawnAnimalListType() {
        disableSpawnAnimalListType = (disableSpawnAnimalListType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public List<AnimalSetting> getAnimalSettings() {
        if (animalSettings == null) animalSettings = new ArrayList<>();
        return animalSettings;
    }

// ==================== Работа с животными ====================

    private AnimalSetting getOrCreateAnimalSetting(String id) {
        for (AnimalSetting setting : getAnimalSettings()) {
            if (setting.getId().equals(id)) {
                return setting;
            }
        }
        AnimalSetting newSetting = new AnimalSetting(id);
        getAnimalSettings().add(newSetting);
        return newSetting;
    }

    public void addAnimal(String id) {
        if (!isAnimalExists(id)) {
            getAnimalSettings().add(new AnimalSetting(id));
        }
    }

    public void addAnimal(EntityType<?> entityType) {
        addAnimal(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
    }

    public boolean isAnimalExists(String id) {
        return getAnimalSettings().stream().anyMatch(setting -> setting.getId().equals(id));
    }

    public void removeAnimal(String id) {
        getAnimalSettings().removeIf(setting -> setting.getId().equals(id));
    }

    public void toggleAnimalBreed(String id) {
        getOrCreateAnimalSetting(id).toggleBreed();
    }

    public void toggleAnimalSpawn(String id) {
        getOrCreateAnimalSetting(id).toggleSpawn();
    }

    public boolean canBreedAnimal(String id) {
        return getAnimalSettings().stream()
                .filter(setting -> setting.getId().equals(id))
                .findFirst()
                .map(AnimalSetting::canBreed)
                .orElse(false);
    }

    public boolean canSpawnAnimal(String id) {
        return getAnimalSettings().stream()
                .filter(setting -> setting.getId().equals(id))
                .findFirst()
                .map(AnimalSetting::canSpawn)
                .orElse(false);
    }

    public List<String> getAllAnimalIds() {
        return getAnimalSettings().stream()
                .map(AnimalSetting::getId)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private static String getAnimalDisplayName(String animalId) {
        try {
            ResourceLocation id = ResourceLocation.parse(animalId);
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (entityType != null) {
                Component translated = Component.translatable(entityType.getDescriptionId());
                return translated.getString();
            }
        } catch (Exception e) {
            // fallback
        }
        String path = animalId.split(":")[1];
        return path.substring(0, 1).toUpperCase() + path.substring(1).replace("_", " ");
    }

    // ==================== Проверка наличия конкретных настроек ====================

    public boolean hasBlockSetting(String blockId) {
        return blockSettings.stream().anyMatch(setting -> setting.getIdBlock().equals(blockId));
    }

    public boolean hasItemSetting(String itemId) {
        return itemSettings.stream().anyMatch(setting -> setting.getIdItem().equals(itemId));
    }

    public boolean hasSeedSetting(String seedId) {
        return seedSettings.stream().anyMatch(setting -> setting.getIdSeed().equals(seedId));
    }

    public boolean hasVillagerSetting(String professionId) {
        return villagerSettings.stream().anyMatch(setting -> setting.getId().equals(professionId));
    }

    public boolean hasAnimalSetting(String animalAd) {
        return animalSettings.stream().anyMatch(setting -> setting.getId().equals(animalAd));
    }
}