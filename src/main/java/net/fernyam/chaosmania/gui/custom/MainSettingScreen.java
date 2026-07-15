package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.ChaosManiaMod;
import net.fernyam.chaosmania.data.settings.*;
import net.fernyam.chaosmania.data.settings.custom.*;
import net.fernyam.chaosmania.gui.PlayerInfoData;
import net.fernyam.chaosmania.gui.custom.screen.*;
import net.fernyam.chaosmania.gui.custom.scrolls.*;
import net.fernyam.chaosmania.gui.util.SearchUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;
import static net.fernyam.chaosmania.data.settings.SettingsManager.*;

enum ButtonSelect {
    BlockSetting,
    ItemSetting,
    SeedPlanting,
    VillagerTrading,
    ModLoading,
    MobSetting
}


public class MainSettingScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");

    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 5;

    private static final int DefaultObjScrollBackgroundWidth = 216;
    private static final int DefaultObjScrollBackgroundHeight = 230;

    private static final int DefaultToolBackgroundWidth = 150;
    private static final int DefaultToolBackgroundHeight = 180;

    private static int objScrollBackgroundWidth = DefaultObjScrollBackgroundWidth;
    private static int objScrollBackgroundHeight = DefaultObjScrollBackgroundHeight;

    private static int toolBackgroundWidth = DefaultToolBackgroundWidth;
    private static int toolBackgroundHeight = DefaultToolBackgroundHeight;


    public static PlayerInfoData selectedPlayer;
    private static ButtonSelect selectButton;

    // Кешированные списки
    private List<BlockEntry> allBlocksMasterList = new ArrayList<>();
    private List<ItemEntry> allItemsMasterList = new ArrayList<>();
    private List<ItemEntry> allSeedsMasterList = new ArrayList<>();
    private List<ProfessionVillagerEntry> allVillagersMasterList = new ArrayList<>();
    private List<ModEntry> allModMasterList = new ArrayList<>();
    private List<MobEntry> allMobMasterList = new ArrayList<>();

    // Фильтры поиска
    private String currentBlockSearchFilter = "";
    private String currentItemSearchFilter = "";
    private String currentSeedsSearchFilter = "";
    private String currentVillagersSearchFilter = "";
    private String currentModSearchFilter = "";
    private String currentMobSearchFilter = "";

    // UI компоненты
    private ScrollingPlayerList playerListScroll;
    private ScrollingBlockList blockListScroll;
    private ScrollingItemList itemListScroll;
    private ScrollingSeedList seedListScroll;
    private ScrollingVillagerList villagerListScroll;
    private ScrollingModList modListScroll;
    private ScrollingMobList mobListScroll;
    private EditBox addModInModList;
    private EditBox searchAllSelectObj;

    private Button BlockButton;
    private Button ItemButton;
    private Button SpecialSettingButton;
    private Button SpecialPlantingSeedSettingButton;
    private Button SpecialVillagerTradingSettingButton;
    private Button SpecialModLoadingSettingButton;
    private Button SpecialMobSettingButton;
    private Button LogButton;

    private boolean IsActiveSpecialSettingButton;

    public MainSettingScreen() {
        super(Component.empty());
    }

    // ==================== Методы для блоков ====================

    private List<BlockEntry> filterBlocksBySearch(List<BlockEntry> blocks, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(blocks);
        }

        var query = SearchUtils.parseQuery(searchText);
        if (query.isEmpty()) return new ArrayList<>(blocks);

        return blocks.stream()
                .filter(entry -> {
                    String name = entry.getName().toLowerCase();
                    String id = BuiltInRegistries.BLOCK.getKey(entry.getBlock()).toString();

                    return switch (query.type) {
                        case NAME -> name.contains(query.query);
                        case ID -> id.toLowerCase().contains(query.query);
                        case MOD_ID -> id.split(":")[0].toLowerCase().contains(query.query);
                    };
                })
                .collect(Collectors.toList());
    }

    private void sortBlocksWithActiveFirst(List<BlockEntry> blocks) {
        if (selectedPlayer == null) return;
        var settings = getBlockSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        SearchUtils.sortWithActiveFirst(
                blocks,
                entry -> settings.isBlockExists(BuiltInRegistries.BLOCK.getKey(entry.getBlock()).toString()),
                BlockEntry::getName
        );
    }

    private void loadSelectBlockList() {
        if (selectedPlayer == null) return;

        var settings = getBlockSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        Set<BlockEntry> allBlocksSet = new HashSet<>();
        for (var entry : settings.getBlocks()) {
            ResourceLocation location = ResourceLocation.tryParse(entry.getIdBlock());
            if (location != null && BuiltInRegistries.BLOCK.containsKey(location)) {
                var block = BuiltInRegistries.BLOCK.get(location);
                allBlocksSet.add(new BlockEntry(block, new ItemStack(block.asItem())));
            }
        }

        allBlocksMasterList = new ArrayList<>(allBlocksSet);
        sortBlocksWithActiveFirst(allBlocksMasterList);
        updateBlockListWithFilter();
    }

    private void updateBlockListWithFilter() {
        if (blockListScroll != null && allBlocksMasterList != null) {
            List<BlockEntry> filtered = filterBlocksBySearch(allBlocksMasterList, currentBlockSearchFilter);
            sortBlocksWithActiveFirst(filtered);
            blockListScroll.updateEntries(filtered);
        }
    }

    // ==================== Методы для предметов ====================

    private List<ItemEntry> filterItemsBySearch(List<ItemEntry> items, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(items);
        }

        var query = SearchUtils.parseQuery(searchText);
        if (query.isEmpty()) return new ArrayList<>(items);

        return items.stream()
                .filter(entry -> {
                    String name = entry.getName().toLowerCase();
                    String id = BuiltInRegistries.ITEM.getKey(entry.getItem()).toString();

                    return switch (query.type) {
                        case NAME -> name.contains(query.query);
                        case ID -> id.toLowerCase().contains(query.query);
                        case MOD_ID -> id.split(":")[0].toLowerCase().contains(query.query);
                    };
                })
                .collect(Collectors.toList());
    }

    private void sortItemsWithActiveFirst(List<ItemEntry> items) {
        if (selectedPlayer == null) return;

        var settings = getItemSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        SearchUtils.sortWithActiveFirst(
                items,
                entry -> settings.isItemExists(BuiltInRegistries.ITEM.getKey(entry.getItem()).toString()),
                ItemEntry::getName
        );
    }

    private void loadSelectItemList() {
        if (selectedPlayer == null) return;

        var settings = getItemSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        Set<ItemEntry> allItemsSet = new HashSet<>();
        for (var entry : settings.getItems()) {
            ResourceLocation location = ResourceLocation.tryParse(entry.getIdItem());
            if (location != null && BuiltInRegistries.ITEM.containsKey(location)) {
                var item = BuiltInRegistries.ITEM.get(location);
                allItemsSet.add(new ItemEntry(item, new ItemStack(item)));
            }
        }

        allItemsMasterList = new ArrayList<>(allItemsSet);
        sortItemsWithActiveFirst(allItemsMasterList);
        updateItemListWithFilter();
    }

    private void updateItemListWithFilter() {
        if (itemListScroll != null && allItemsMasterList != null) {
            List<ItemEntry> filtered = filterItemsBySearch(allItemsMasterList, currentItemSearchFilter);
            sortItemsWithActiveFirst(filtered);
            itemListScroll.updateEntries(filtered);
        }
    }

    // ==================== Методы для семян ====================

    private List<ItemEntry> filterSeedsBySearch(List<ItemEntry> items, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(items);
        }

        var query = SearchUtils.parseQuery(searchText);
        if (query.isEmpty()) return new ArrayList<>(items);

        return items.stream()
                .filter(entry -> {
                    String name = entry.getName().toLowerCase();
                    String id = BuiltInRegistries.ITEM.getKey(entry.getItem()).toString();

                    return switch (query.type) {
                        case NAME -> name.contains(query.query);
                        case ID -> id.toLowerCase().contains(query.query);
                        case MOD_ID -> id.split(":")[0].toLowerCase().contains(query.query);
                    };
                })
                .collect(Collectors.toList());
    }

    private void sortSeedsWithActiveFirst(List<ItemEntry> items) {
        if (selectedPlayer == null) return;
        var settings = getSeedSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        SearchUtils.sortWithActiveFirst(
                items,
                entry -> settings.isSeedExists(BuiltInRegistries.ITEM.getKey(entry.getItem()).toString()),
                ItemEntry::getName
        );
    }

    private void loadSelectSeedsList() {
        if (selectedPlayer == null) return;

        var settings = getSeedSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        Set<ItemEntry> allSeedsSet = new HashSet<>();
        for (var entry : settings.getSeeds()) {
            ResourceLocation location = ResourceLocation.tryParse(entry.getIdSeed());
            if (location != null && BuiltInRegistries.ITEM.containsKey(location)) {
                var item = BuiltInRegistries.ITEM.get(location);
                allSeedsSet.add(new ItemEntry(item, new ItemStack(item)));
            }
        }

        allSeedsMasterList = new ArrayList<>(allSeedsSet);
        sortSeedsWithActiveFirst(allSeedsMasterList);
        updateSeedsListWithFilter();
    }

    private void updateSeedsListWithFilter() {
        if (seedListScroll != null && allSeedsMasterList != null) {
            List<ItemEntry> filtered = filterSeedsBySearch(allSeedsMasterList, currentSeedsSearchFilter);
            sortSeedsWithActiveFirst(filtered);
            seedListScroll.updateEntries(filtered);
        }
    }

    // ==================== Методы для жителей ====================

    private List<ProfessionVillagerEntry> filterVillagersBySearch(List<ProfessionVillagerEntry> professions, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(professions);
        }

        var query = SearchUtils.parseQuery(searchText);
        if (query.isEmpty()) return new ArrayList<>(professions);

        return professions.stream()
                .filter(entry -> {
                    String name = entry.getName().toLowerCase();
                    String id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(entry.getProfession()).toString();

                    return switch (query.type) {
                        case NAME -> name.contains(query.query);
                        case ID -> id.toLowerCase().contains(query.query);
                        case MOD_ID -> id.split(":")[0].toLowerCase().contains(query.query);
                    };
                })
                .collect(Collectors.toList());
    }

    private void sortVillagersWithActiveFirst(List<ProfessionVillagerEntry> professions) {
        if (selectedPlayer == null) return;
        var settings = getVillagerSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        SearchUtils.sortWithActiveFirst(
                professions,
                entry -> settings.isProfessionExists(entry.getId()),
                ProfessionVillagerEntry::getName
        );
    }

    private void loadSelectVillagerList() {
        if (selectedPlayer == null) return;

        var settings = getVillagerSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        Set<ProfessionVillagerEntry> allVillagersSet = new HashSet<>();
        for (var entry : settings.getProfessions()) {
            ResourceLocation location = ResourceLocation.tryParse(entry.getIdProfession());
            if (location != null && BuiltInRegistries.VILLAGER_PROFESSION.containsKey(location)) {
                allVillagersSet.add(new ProfessionVillagerEntry(
                        BuiltInRegistries.VILLAGER_PROFESSION.get(location)
                ));
            }
        }

        allVillagersMasterList = new ArrayList<>(allVillagersSet);
        sortVillagersWithActiveFirst(allVillagersMasterList);
        updateVillagersListWithFilter();
    }

    private void updateVillagersListWithFilter() {
        if (villagerListScroll != null && allVillagersMasterList != null) {
            List<ProfessionVillagerEntry> filtered = filterVillagersBySearch(allVillagersMasterList, currentVillagersSearchFilter);
            sortVillagersWithActiveFirst(filtered);
            villagerListScroll.updateEntries(filtered);
        }
    }

    //===================== Методы для модов ===============

    private List<ModEntry> filterModsBySearch(List<ModEntry> mods, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(mods);
        }

        var query = SearchUtils.parseQuery(searchText);
        if (query.isEmpty()) return new ArrayList<>(mods);

        return mods.stream()
                .filter(entry -> {
                    String id = entry.getModId().toLowerCase();

                    return id.contains(query.query);
                })
                .collect(Collectors.toList());
    }

    private void sortModsWithActiveFirst(List<ModEntry> mods) {
        if (selectedPlayer == null) return;

        var settings = getModSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        SearchUtils.sortWithActiveFirst(
                mods,
                entry -> settings.isModExists(entry.getModId()),
                ModEntry::getModId
        );
    }

    private void loadSelectModList() {
        if (selectedPlayer == null) return;

        var settings = getModSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        Set<ModEntry> allModsSet = new HashSet<>();
        for (var entry : settings.getMods()) {

            var mod = entry.getIdMod();
            allModsSet.add(new ModEntry(mod));

        }

        allModMasterList = new ArrayList<>(allModsSet);
        sortModsWithActiveFirst(allModMasterList);
        updateModListWithFilter();
    }

    private void updateModListWithFilter() {
        if (modListScroll != null && allModMasterList != null) {
            List<ModEntry> filtered = filterModsBySearch(allModMasterList, currentModSearchFilter);
            sortModsWithActiveFirst(filtered);
            modListScroll.updateEntries(filtered);
        }
    }

    // ==================== Методы для мобов ================

    private List<MobEntry> filterMobsBySearch(List<MobEntry> mobs, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(mobs);
        }

        var query = SearchUtils.parseQuery(searchText);
        if (query.isEmpty()) return new ArrayList<>(mobs);

        return mobs.stream()
                .filter(entry -> {
                    String name = entry.getMobName().toLowerCase();
                    String id = entry.getMobId().toLowerCase();

                    return switch (query.type) {
                        case NAME -> name.contains(query.query);
                        case ID -> id.contains(query.query);
                        case MOD_ID -> id.split(":")[0].contains(query.query);
                    };
                })
                .collect(Collectors.toList());
    }

    private void sortMobsWithActiveFirst(List<MobEntry> mobs) {
        if (selectedPlayer == null) return;
        var settings = getMobSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        SearchUtils.sortWithActiveFirst(
                mobs,
                entry -> settings.isMobExists(entry.getMobId()),
                MobEntry::getMobName
        );
    }

    private void loadSelectMobList() {
        if (selectedPlayer == null) return;

        var settings = getMobSettings(selectedPlayer.getUuid());
        if (settings == null) return;

        Set<MobEntry> allMobsSet = new HashSet<>();
        for (var entry : settings.getMobs()) {
            ResourceLocation location = ResourceLocation.tryParse(entry.getIdMob());
            if (location != null && BuiltInRegistries.ENTITY_TYPE.containsKey(location)) {
                var entityType = BuiltInRegistries.ENTITY_TYPE.get(location);
                allMobsSet.add(new MobEntry(entityType));
            }
        }

        allMobMasterList = new ArrayList<>(allMobsSet);
        sortMobsWithActiveFirst(allMobMasterList);
        updateMobListWithFilter();
    }

    private void updateMobListWithFilter() {
        if (mobListScroll != null && allMobMasterList != null) {
            List<MobEntry> filtered = filterMobsBySearch(allMobMasterList, currentMobSearchFilter);
            sortMobsWithActiveFirst(filtered);
            mobListScroll.updateEntries(filtered);
        }
    }

    // ==================== Общие методы ====================

    private List<PlayerInfoData> getOnlinePlayers() {
        // Сначала синхронизируем игроков между всеми настройками
        synchronizePlayers();

        Set<PlayerInfoData> playersSet = new HashSet<>();

        // Получаем всех игроков из настроек
        for (String uuid : getAllPlayerUuids()) {
            if (uuid.equals(ALL_PLAYER_UUID)) {
                playersSet.add(PlayerInfoData.ALL_PLAYERS);
                continue;
            }

            String name = getPlayerName(uuid);
            playersSet.add(new PlayerInfoData(uuid, name));
        }

        // Добавляем онлайн игроков
        if (minecraft != null && minecraft.getConnection() != null) {
            for (PlayerInfo playerInfo : minecraft.getConnection().getListedOnlinePlayers()) {
                String uuid = playerInfo.getProfile().getId().toString();
                String name = playerInfo.getProfile().getName();
                playersSet.add(new PlayerInfoData(uuid, name));

                // Если онлайн игрок есть, но его нет в настройках - добавляем
                if (!getAllPlayerUuids().contains(uuid)) {
                    SettingsManager.addNewPlayer(name, uuid);
                }
            }
        }

        // Если никого нет - добавляем текущего игрока
        if (playersSet.isEmpty() && minecraft != null && minecraft.player != null) {
            String uuid = minecraft.player.getUUID().toString();
            String name = minecraft.player.getName().getString();
            playersSet.add(new PlayerInfoData(uuid, name));

            if (!getAllPlayerUuids().contains(uuid)) {
                SettingsManager.addNewPlayer(name, uuid);
            }
        }

        List<PlayerInfoData> sortedPlayers = new ArrayList<>(playersSet);
        sortedPlayers.sort((p1, p2) -> {
            if (p1.isAllPlayers()) return -1;
            if (p2.isAllPlayers()) return 1;
            return p1.getName().compareToIgnoreCase(p2.getName());
        });

        return sortedPlayers;
    }

    private void synchronizePlayers() {
        // Получаем все UUID из всех типов настроек
        Set<String> blockPlayers = new HashSet<>(SettingsManager.getAllBlockPlayers());
        Set<String> itemPlayers = new HashSet<>(SettingsManager.getAllItemPlayers());
        Set<String> seedPlayers = new HashSet<>(SettingsManager.getAllSeedPlayers());
        Set<String> villagerPlayers = new HashSet<>(SettingsManager.getAllVillagerPlayers());
        Set<String> animalPlayers = new HashSet<>(SettingsManager.getAllMobPlayers());
        Set<String> modPlayers = new HashSet<>(SettingsManager.getAllModPlayers());
        Set<String> mobPlayers = new HashSet<>(SettingsManager.getAllMobPlayers());

        // Объединяем все UUID в один набор
        Set<String> allUuids = new HashSet<>();
        allUuids.addAll(blockPlayers);
        allUuids.addAll(itemPlayers);
        allUuids.addAll(seedPlayers);
        allUuids.addAll(villagerPlayers);
        allUuids.addAll(animalPlayers);
        allUuids.addAll(modPlayers);

        // Убеждаемся, что [ВСЕМ] есть везде
        allUuids.add(ALL_PLAYER_UUID);

        // Проверяем каждый UUID во всех типах настроек
        for (String uuid : allUuids) {
            // Пропускаем [ВСЕМ] - он уже есть
            if (uuid.equals(ALL_PLAYER_UUID)) continue;

            // Получаем имя из любого существующего типа
            String name = getPlayerName(uuid);

            // Проверяем блоки
            if (!blockPlayers.contains(uuid)) {
                BlockSettings blockSettings = new BlockSettings(uuid, name);
                SettingsManager.addBlockPlayer(uuid, blockSettings);
                ChaosManiaMod.LOGGER.debug("Added missing player {} to block settings", uuid);
            }

            // Проверяем предметы
            if (!itemPlayers.contains(uuid)) {
                ItemSettings itemSettings = new ItemSettings(uuid, name);
                SettingsManager.addItemPlayer(uuid, itemSettings);
                ChaosManiaMod.LOGGER.debug("Added missing player {} to item settings", uuid);
            }

            // Проверяем семена
            if (!seedPlayers.contains(uuid)) {
                SeedSettings seedSettings = new SeedSettings(uuid, name);
                SettingsManager.addSeedPlayer(uuid, seedSettings);
                ChaosManiaMod.LOGGER.debug("Added missing player {} to seed settings", uuid);
            }

            // Проверяем жителей
            if (!villagerPlayers.contains(uuid)) {
                VillagerSettings villagerSettings = new VillagerSettings(uuid, name);
                SettingsManager.addVillagerPlayer(uuid, villagerSettings);
                ChaosManiaMod.LOGGER.debug("Added missing player {} to villager settings", uuid);
            }

            // Проверяем животных
            if (!animalPlayers.contains(uuid)) {
                MobSettings mobSettings = new MobSettings(uuid, name);
                SettingsManager.addMobPlayer(uuid, mobSettings);
                ChaosManiaMod.LOGGER.debug("Added missing player {} to animal settings", uuid);
            }

            // Проверяем моды
            if (!modPlayers.contains(uuid)) {
                ModSettings modSettings = new ModSettings(uuid, name);
                SettingsManager.addModPlayer(uuid, modSettings);
                ChaosManiaMod.LOGGER.debug("Added missing player {} to mod settings", uuid);
            }

            if(!mobPlayers.contains(uuid)) {
                MobSettings mobSettings = new MobSettings(uuid , name);
                SettingsManager.addMobPlayer(uuid , mobSettings);
                ChaosManiaMod.LOGGER.debug("Added missing player {} to mob setting" , uuid);
            }
        }
    }

    private Set<String> getAllPlayerUuids() {
        Set<String> uuids = new HashSet<>();
        uuids.addAll(SettingsManager.getAllBlockPlayers());
        uuids.addAll(SettingsManager.getAllItemPlayers());
        uuids.addAll(SettingsManager.getAllSeedPlayers());
        uuids.addAll(SettingsManager.getAllVillagerPlayers());
        uuids.addAll(SettingsManager.getAllModPlayers());
        uuids.addAll(SettingsManager.getAllMobPlayers());
        return uuids;
    }

    private String getPlayerName(String uuid) {
        // Пробуем получить имя из блоков
        var blockSettings = SettingsManager.getBlockSettings(uuid);
        if (blockSettings != null && blockSettings.getNamePlayer() != null) {
            return blockSettings.getNamePlayer();
        }

        // Пробуем получить имя из предметов
        var itemSettings = SettingsManager.getItemSettings(uuid);
        if (itemSettings != null && itemSettings.getNamePlayer() != null) {
            return itemSettings.getNamePlayer();
        }

        // Пробуем получить имя из семян
        var seedSettings = SettingsManager.getSeedSettings(uuid);
        if (seedSettings != null && seedSettings.getNamePlayer() != null) {
            return seedSettings.getNamePlayer();
        }

        // Пробуем получить имя из жителей
        var villagerSettings = SettingsManager.getVillagerSettings(uuid);
        if (villagerSettings != null && villagerSettings.getNamePlayer() != null) {
            return villagerSettings.getNamePlayer();
        }

        var modSettings = SettingsManager.getModSettings(uuid);
        if(modSettings != null && modSettings.getNamePlayer() != null) {
            return modSettings.getNamePlayer();
        }

        var mobSettings = SettingsManager.getMobSettings(uuid);
        if(mobSettings != null && mobSettings.getNamePlayer() != null) {
            return mobSettings.getNamePlayer();
        }

        // Если нигде нет - возвращаем UUID
        return uuid;
    }

    public static PlayerInfoData getSelectedPlayer() {
        return selectedPlayer;
    }

    public static void setSelectedPlayer(PlayerInfoData player) {
        selectedPlayer = player;
    }

    // ==================== Создание UI ====================

    @Override
    public void init() {
        this.clearWidgets();
        super.init();

        int buttonWidth = 150;
        int totalWidth = buttonWidth * 3 + BUTTON_SPACING * 2;
        int startX = (getWidth() - totalWidth) / 2;
        int startY = 5;

        createMainButtons(startX, startY, buttonWidth);
        createSpecialButtons(startX, startY, buttonWidth);
        createScrolls();
        createSearchBox();

        if (selectButton != null) {
            setupSelectedMode(buttonWidth);
        }

        if (IsActiveSpecialSettingButton) {
            addSpecialButtons();
        }

        updateButtonStates();
    }

    private void createMainButtons(int startX, int startY, int buttonWidth) {
        BlockButton = Button.builder(
                Component.literal("Настройка блоков"),
                button -> {
                    selectButton = ButtonSelect.BlockSetting;
                    IsActiveSpecialSettingButton = false;
                    init();
                }
        ).bounds(startX, startY, buttonWidth, BUTTON_HEIGHT).build();

        ItemButton = Button.builder(
                Component.literal("Настройка предметов"),
                button -> {
                    selectButton = ButtonSelect.ItemSetting;
                    IsActiveSpecialSettingButton = false;
                    init();
                }
        ).bounds(startX + buttonWidth + BUTTON_SPACING, startY, buttonWidth, BUTTON_HEIGHT).build();

        SpecialSettingButton = Button.builder(
                Component.literal(IsActiveSpecialSettingButton ? "Спец возможности ▼" : "Спец возможности ▶"),
                button -> {
                    IsActiveSpecialSettingButton = !IsActiveSpecialSettingButton;
                    init();
                }
        ).bounds(startX + (buttonWidth + BUTTON_SPACING) * 2, startY, buttonWidth, BUTTON_HEIGHT).build();

        LogButton = Button.builder(
                Component.literal("L"),
                button -> {}
        ).bounds(8, getHeight() - 20, 12, 12).build();

        this.addRenderableWidget(BlockButton);
        this.addRenderableWidget(ItemButton);
        this.addRenderableWidget(SpecialSettingButton);
        this.addRenderableWidget(LogButton);
    }

    private void createSpecialButtons(int startX, int startY, int buttonWidth) {
        SpecialPlantingSeedSettingButton = Button.builder(
                Component.literal("Посадка семян"),
                button -> {
                    selectButton = ButtonSelect.SeedPlanting;
                    IsActiveSpecialSettingButton = false;
                    init();
                }
        ).bounds(startX + (buttonWidth + BUTTON_SPACING) * 2, startY + BUTTON_HEIGHT + 5, buttonWidth, BUTTON_HEIGHT).build();

        SpecialVillagerTradingSettingButton = Button.builder(
                Component.literal("Торговля с жителями"),
                button -> {
                    selectButton = ButtonSelect.VillagerTrading;
                    IsActiveSpecialSettingButton = false;
                    init();
                }
        ).bounds(startX + (buttonWidth + BUTTON_SPACING) * 2, startY + (BUTTON_HEIGHT + 5) * 2, buttonWidth, BUTTON_HEIGHT).build();

        SpecialModLoadingSettingButton = Button.builder(
                Component.literal("Настройка модов"),
                button -> {
                    selectButton = ButtonSelect.ModLoading;
                    IsActiveSpecialSettingButton = false;
                    init();
                }
        ).bounds(startX + (buttonWidth + BUTTON_SPACING) * 2, startY + (BUTTON_HEIGHT + 5) * 3, buttonWidth, BUTTON_HEIGHT).build();

        SpecialMobSettingButton = Button.builder(
                Component.literal("Настройка мобов"),
                button -> {
                    selectButton = ButtonSelect.MobSetting;
                    IsActiveSpecialSettingButton = false;
                    init();
                }
        ).bounds(startX + (buttonWidth + BUTTON_SPACING) * 2, startY + (BUTTON_HEIGHT + 5) * 4, buttonWidth, BUTTON_HEIGHT).build();
    }

    private void addSpecialButtons() {
        if (SpecialPlantingSeedSettingButton != null) {
            this.addRenderableWidget(SpecialPlantingSeedSettingButton);
        }
        if (SpecialVillagerTradingSettingButton != null) {
            this.addRenderableWidget(SpecialVillagerTradingSettingButton);
        }
        if (SpecialModLoadingSettingButton != null) {
            this.addRenderableWidget(SpecialModLoadingSettingButton);
        }
        if (SpecialMobSettingButton != null) {
            this.addRenderableWidget(SpecialMobSettingButton);
        }
    }

    private void createScrolls() {
        this.playerListScroll = new ScrollingPlayerList(
                getWidth() / 2 - 108 - 98,
                getHeight() / 2 - 94,
                100,
                getHeight() / 2 + 20,
                this
        );


        int scrollX = getWidth() / 2 - 102;
        int scrollY = getHeight() / 2 - 71;
        int scrollWidth = this.objScrollBackgroundWidth - 15;
        int scrollHeight = this.objScrollBackgroundHeight - 40;

        this.blockListScroll = new ScrollingBlockList(this, scrollX, scrollY, scrollWidth, scrollHeight);
        this.itemListScroll = new ScrollingItemList(this, scrollX, scrollY, scrollWidth, scrollHeight);
        this.seedListScroll = new ScrollingSeedList(this, scrollX, scrollY, scrollWidth, scrollHeight);
        this.villagerListScroll = new ScrollingVillagerList(this, scrollX, scrollY, scrollWidth, scrollHeight);
        this.modListScroll = new ScrollingModList(this, scrollX, scrollY, scrollWidth, scrollHeight);
        this.mobListScroll = new ScrollingMobList(this, scrollX, scrollY, scrollWidth, scrollHeight);
    }

    private void createSearchBox() {
        this.searchAllSelectObj = new EditBox(
                getFontRender(),
                getWidth() / 2 - 100,
                getHeight() / 2 - 90,
                this.objScrollBackgroundWidth - 19,
                17,
                Component.literal("Поиск... (:id | @modid)")
        );
    }

    private void setupSelectedMode(int buttonWidth) {
        int buttonX = getWidth() / 2 + 105;
        int buttonY = getHeight() / 2 - 50;
        int btnWidth = buttonWidth - 37;
        int btnHeight = BUTTON_HEIGHT + 8;

        objScrollBackgroundHeight = DefaultObjScrollBackgroundHeight;
        objScrollBackgroundWidth = DefaultObjScrollBackgroundWidth;
        toolBackgroundHeight = DefaultToolBackgroundHeight;
        toolBackgroundWidth = DefaultToolBackgroundWidth;

        switch (selectButton) {
            case BlockSetting -> setupBlockMode(buttonX, buttonY, btnWidth, BUTTON_HEIGHT + 3);
            case ItemSetting -> setupItemMode(buttonX, buttonY, btnWidth, btnHeight);
            case SeedPlanting -> setupSeedMode(buttonX, buttonY, btnWidth, btnHeight);
            case VillagerTrading -> setupVillagerMode(buttonX, buttonY, btnWidth, btnHeight);
            case ModLoading -> setupModMode(buttonX , buttonY , btnWidth, btnHeight);
            case MobSetting -> setupMobMode(buttonX , buttonY , btnWidth, btnHeight);
        }

        ChaosManiaMod.LOGGER.info(selectButton.toString());

        playerListScroll.updateEntries(getOnlinePlayers());
        this.addRenderableWidget(playerListScroll);
    }

    private void setupBlockMode(int buttonX, int buttonY, int btnWidth, int btnHeight) {
        loadSelectBlockList();

        toolBackgroundHeight += 10;

        searchAllSelectObj.setResponder(searchText -> {
            currentBlockSearchFilter = searchText;
            searchAllSelectObj.setTextColor(SearchUtils.getSearchTextColor(searchText));
            updateBlockListWithFilter();
        });

        if(selectedPlayer != null)
        {
            addRenderableWidget(Button.builder(
                    Component.literal("Add Block"),
                    button -> {
                        if (minecraft != null) {
                            minecraft.setScreen(new AllBlockScreen(selectedPlayer, this));
                        }
                    }).bounds(buttonX, buttonY, btnWidth, btnHeight).build());
        }

        // Добавляем toggle кнопки через Enum
        addToggleButton(ToggleButtonType.BLOCK_PLACE, buttonX, buttonY + btnHeight + 2, btnWidth, BUTTON_HEIGHT + 3);
        addToggleButton(ToggleButtonType.BLOCK_BREAK, buttonX, buttonY + (btnHeight + 2) * 2, btnWidth, BUTTON_HEIGHT + 3);
        addToggleButton(ToggleButtonType.BLOCK_RIGHT_CLICK, buttonX, buttonY + (btnHeight + 2) * 3, btnWidth, BUTTON_HEIGHT + 3);
        addToggleButton(ToggleButtonType.BLOCK_LEFT_CLICK, buttonX, buttonY + (btnHeight + 2) * 4, btnWidth, BUTTON_HEIGHT + 3);

        addCloseButton(buttonX, buttonY + (btnHeight + 5) * 4 + 20, btnWidth, BUTTON_HEIGHT);

        this.addRenderableWidget(searchAllSelectObj);
        this.addRenderableWidget(blockListScroll);
    }

    private void setupItemMode(int buttonX, int buttonY, int btnWidth, int btnHeight) {
        loadSelectItemList();

        searchAllSelectObj.setResponder(searchText -> {
            currentItemSearchFilter = searchText;
            searchAllSelectObj.setTextColor(SearchUtils.getSearchTextColor(searchText));
            updateItemListWithFilter();
        });

        if(selectedPlayer != null)
        {
            addRenderableWidget(Button.builder(
                    Component.literal("Add Item"),
                    button -> {
                        if (minecraft != null) {
                            minecraft.setScreen(new AllItemScreen(selectedPlayer, this));
                        }
                    }).bounds(buttonX, buttonY, btnWidth, btnHeight).build());
        }

        addToggleButton(ToggleButtonType.ITEM_DROP, buttonX, buttonY + btnHeight + 5, btnWidth, BUTTON_HEIGHT + 3);
        addToggleButton(ToggleButtonType.ITEM_PICKUP, buttonX, buttonY + (btnHeight + 5) * 2, btnWidth, BUTTON_HEIGHT + 3);


        addCloseButton(buttonX, buttonY + (btnHeight + 5) * 3 + 10, btnWidth, BUTTON_HEIGHT);

        this.addRenderableWidget(searchAllSelectObj);
        this.addRenderableWidget(itemListScroll);
    }

    private void setupSeedMode(int buttonX, int buttonY, int btnWidth, int btnHeight) {
        loadSelectSeedsList();

        searchAllSelectObj.setResponder(searchText -> {
            currentSeedsSearchFilter = searchText;
            searchAllSelectObj.setTextColor(SearchUtils.getSearchTextColor(searchText));
            updateSeedsListWithFilter();
        });

        if(selectedPlayer != null)
        {
            addRenderableWidget(Button.builder(
                    Component.literal("Add Seed"),
                    button -> {
                        if (minecraft != null) {
                            minecraft.setScreen(new AllSeedsScreen(selectedPlayer, this));
                        }
                    }).bounds(buttonX, buttonY, btnWidth, btnHeight).build());
        }

        addToggleButton(ToggleButtonType.SEED_PLANT, buttonX, buttonY + btnHeight + 5, btnWidth, BUTTON_HEIGHT + 3);

        addCloseButton(buttonX, buttonY + (btnHeight + 5) * 2 + 10, btnWidth, BUTTON_HEIGHT);

        this.addRenderableWidget(searchAllSelectObj);
        this.addRenderableWidget(seedListScroll);
    }

    private void setupVillagerMode(int buttonX, int buttonY, int btnWidth, int btnHeight) {
        loadSelectVillagerList();

        searchAllSelectObj.setResponder(searchText -> {
            currentVillagersSearchFilter = searchText;
            searchAllSelectObj.setTextColor(SearchUtils.getSearchTextColor(searchText));
            updateVillagersListWithFilter();
        });

        if(selectedPlayer != null)
        {
            addRenderableWidget(Button.builder(
                    Component.literal("Add Villager"),
                    button -> {
                        if (minecraft != null) {
                            minecraft.setScreen(new AllVillagerScreen(selectedPlayer, this));
                        }
                    }).bounds(buttonX, buttonY, btnWidth, btnHeight).build());
        }

        addToggleButton(ToggleButtonType.VILLAGER_TRADE, buttonX, buttonY + btnHeight + 5, btnWidth, BUTTON_HEIGHT + 3);
        addToggleButton(ToggleButtonType.WANDERING_TRADER, buttonX, buttonY + (btnHeight + 5) * 2, btnWidth, BUTTON_HEIGHT + 3);

        addCloseButton(buttonX, buttonY + (btnHeight + 5) * 3 + 10, btnWidth, BUTTON_HEIGHT);

        this.addRenderableWidget(searchAllSelectObj);
        this.addRenderableWidget(villagerListScroll);
    }

    private void setupModMode(int buttonX, int buttonY, int btnWidth, int btnHeight) {
        loadSelectModList();

        searchAllSelectObj.setResponder(searchText -> {
            currentModSearchFilter = searchText;
            updateModListWithFilter();
        });

        addModInModList = new EditBox(
                getFontRender(),
                getWidth() / 2 + 105,
                getHeight() / 2 - 50,
                90,
                16,
                Component.literal("Add Mod"));

        if(selectedPlayer != null)
        {
            addRenderableWidget(Button.builder(
                    Component.literal("+"),
                    button -> {
                        var modText = addModInModList.getValue();
                        if(!modText.isEmpty())
                        {
                            var settings = SettingsManager.getModSettings(selectedPlayer.getUuid());
                            if (settings != null) {
                                if (!settings.isModExists(modText)) {
                                    settings.addMod(modText);
                                    SettingsManager.saveModSettings(selectedPlayer.getUuid());
                                }
                            }
                            init();
                        }
                        addModInModList.setValue("");

                    }).bounds(buttonX + 92, buttonY, 16, 16).build());

            this.addRenderableWidget(addModInModList);
        }

        addToggleButton(ToggleButtonType.MOD_LOAD, buttonX, buttonY + btnHeight + 5, btnWidth, BUTTON_HEIGHT + 3);

        addCloseButton(buttonX, buttonY + (btnHeight + 5) * 3 + 10, btnWidth, BUTTON_HEIGHT);

        this.addRenderableWidget(searchAllSelectObj);
        this.addRenderableWidget(modListScroll);
    }

    private void setupMobMode(int buttonX, int buttonY, int btnWidth, int btnHeight) {
        loadSelectMobList();

        searchAllSelectObj.setResponder(searchText -> {
            currentMobSearchFilter = searchText;
            searchAllSelectObj.setTextColor(SearchUtils.getSearchTextColor(searchText));
            updateMobListWithFilter();
        });

        if(selectedPlayer != null)
        {
            addRenderableWidget(Button.builder(
                    Component.literal("Add Mob"),
                    button -> {
                        if (minecraft != null) {
                            minecraft.setScreen(new AllMobScreen(selectedPlayer, this));
                        }
                    }).bounds(buttonX, buttonY, btnWidth, btnHeight).build());
        }

        addToggleButton(ToggleButtonType.MOB_RIGHT_CLICK, buttonX, buttonY + btnHeight + 5, btnWidth, BUTTON_HEIGHT + 3);
        addToggleButton(ToggleButtonType.MOB_LEFT_CLICK, buttonX, buttonY + (btnHeight + 5) * 2, btnWidth, BUTTON_HEIGHT + 3);


        addCloseButton(buttonX, buttonY + (btnHeight + 5) * 3 + 10, btnWidth, BUTTON_HEIGHT);

        this.addRenderableWidget(searchAllSelectObj);
        this.addRenderableWidget(mobListScroll);
    }

    // ==================== Вспомогательные методы для кнопок ====================

    private void addToggleButton(ToggleButtonType type, int x, int y, int width, int height) {
        if (selectedPlayer != null) {
            this.addRenderableWidget(type.createButton(selectedPlayer.getUuid(), x, y, width, height));
        }
    }

    private void addCloseButton(int x, int y, int width, int height) {
        this.addRenderableWidget(Button.builder(
                Component.literal("Close"),
                button -> onClose()
        ).bounds(x, y, width, height).build());
    }

    private void updateButtonStates() {
        if (BlockButton != null) {
            BlockButton.active = selectButton != ButtonSelect.BlockSetting;
        }
        if (ItemButton != null) {
            ItemButton.active = selectButton != ButtonSelect.ItemSetting;
        }
        if (SpecialPlantingSeedSettingButton != null) {
            SpecialPlantingSeedSettingButton.active = selectButton != ButtonSelect.SeedPlanting;
        }
        if (SpecialVillagerTradingSettingButton != null) {
            SpecialVillagerTradingSettingButton.active = selectButton != ButtonSelect.VillagerTrading;
        }
        if(SpecialModLoadingSettingButton != null) {
            SpecialModLoadingSettingButton.active = selectButton != ButtonSelect.ModLoading;
        }
        if(SpecialMobSettingButton != null) {
            SpecialMobSettingButton.active = selectButton != ButtonSelect.MobSetting;
        }
    }

    // ==================== Render методы ====================

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, getWidth(), getHeight(), 0xCC000000);

        if (selectButton == null) {
            renderTitle(guiGraphics, "§lВыберите категорию возможностей §r", 0xE1A12D);
        } else {
            renderBackground(guiGraphics);
            if (selectedPlayer == null) {
                renderTitle(guiGraphics, "§lВыберите игрока §r", 0x7F7D7A);
            } else {
                renderToolsLabel(guiGraphics);
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderTitle(GuiGraphics guiGraphics, String text, int color) {
        guiGraphics.drawString(
                this.font,
                Component.literal(text),
                getWidth() / 2 - this.font.width(text) / 2,
                getHeight() / 2,
                color,
                false
        );
    }

    private void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.blit(BACKGROUND_TEXTURE, getWidth() / 2 + 75, getHeight() / 2 - 155 / 2, 0, 0, toolBackgroundWidth, toolBackgroundHeight, toolBackgroundWidth, toolBackgroundHeight);
        guiGraphics.blit(BACKGROUND_TEXTURE, getWidth() / 2 - 108, getHeight() / 2 - 100, 0, 0, this.objScrollBackgroundWidth, this.objScrollBackgroundHeight, this.objScrollBackgroundWidth, this.objScrollBackgroundHeight);
    }

    private void renderToolsLabel(GuiGraphics guiGraphics) {
        String text = "§5§lTools §r";
        guiGraphics.drawString(
                this.font,
                Component.literal(text),
                getWidth() / 2 - this.font.width(text) / 2 + 123,
                getHeight() / 2 - 65,
                0xE1A12D,
                false
        );
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Отключаем стандартный фон
    }

    // ==================== Остальные методы ====================

    public void selectPlayer(PlayerInfoData player) {
        selectedPlayer = player;
        currentBlockSearchFilter = "";
        currentItemSearchFilter = "";
        currentSeedsSearchFilter = "";
        currentVillagersSearchFilter = "";
        currentModSearchFilter = "";
        currentMobSearchFilter = "";
        if (searchAllSelectObj != null) {
            searchAllSelectObj.setValue("");
        }
        this.init();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            selectedPlayer = null;
            selectButton = null;
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public Font getFontRender() {
        return getMinecraft().font;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    // Внутренние Entry

    public static class BlockEntry {
        private final Block block;
        private final ItemStack itemStack;
        private final String name;

        BlockEntry(Block block, ItemStack itemStack) {
            this.block = block;
            this.itemStack = itemStack;
            this.name = itemStack.getHoverName().getString();
        }

        public Block getBlock() { return block; }
        public ItemStack getItemStack() { return itemStack; }
        public String getName() { return name; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BlockEntry that = (BlockEntry) obj;
            return Objects.equals(block, that.block);
        }

        @Override
        public int hashCode() {
            return Objects.hash(block);
        }
    }

    public static class ItemEntry {
        private final Item item;
        private final ItemStack itemStack;
        private final String name;

        ItemEntry(Item item, ItemStack itemStack) {
            this.item = item;
            this.itemStack = itemStack;
            this.name = itemStack.getHoverName().getString();
        }

        public Item getItem() { return item; }
        public ItemStack getItemStack() { return itemStack; }
        public String getName() { return name; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ItemEntry that = (ItemEntry) obj;
            return Objects.equals(item, that.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item);
        }
    }

    public static class ProfessionVillagerEntry {
        private final String id;
        private final String name;
        private final VillagerProfession profession;

        ProfessionVillagerEntry(VillagerProfession profession) {
            this.id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
            this.name = getProfessionDisplayName(profession);
            this.profession = profession;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public VillagerProfession getProfession() { return profession; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ProfessionVillagerEntry that = (ProfessionVillagerEntry) obj;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        public static String getProfessionDisplayName(VillagerProfession profession) {
            if (profession == null) return "Неизвестно";

            ResourceLocation id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
            String translationKey = "entity." + id.getNamespace() + ".villager." + id.getPath();

            Component translated = Component.translatable(translationKey);
            String result = translated.getString();

            if (result.equals(translationKey)) {
                String path = id.getPath();
                result = path.substring(0, 1).toUpperCase() + path.substring(1).replace("_", " ");
            }

            return result;
        }
    }

    public static class ModEntry {
        private final String modId;

        ModEntry(String id) {
            modId = id;
        }

        public String getModId() { return modId; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ModEntry that = (ModEntry) obj;
            return Objects.equals(modId, that.modId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(modId);
        }
    }

    public static class MobEntry {
        private final EntityType<?> entityType;
        private final String mobId;
        private final String mobName;

        MobEntry(EntityType<?> entityType) {
            this.entityType = entityType;
            this.mobId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();
            this.mobName = entityType.getDescription().getString();
        }

        public EntityType<?> getEntityType() { return entityType; }
        public String getMobId() { return mobId; }
        public String getMobName() { return mobName; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            MobEntry that = (MobEntry) obj;
            return Objects.equals(mobId, that.mobId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mobId);
        }
    }
}