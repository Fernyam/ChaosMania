package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.fernyam.chaosmania.data.PlayerSettings;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;
import static net.fernyam.chaosmania.data.JSONSettingCreate.*;

enum ButtonSelect {
    BlockSetting,
    ItemSetting,
    PlantingSeed,
    TradingVillager
}

// Класс для хранения информации об игроке
class PlayerInfoData {
    private final UUID uuid;
    private final String name;
    private final boolean isAllPlayers;

    public static final PlayerInfoData ALL_PLAYERS = new PlayerInfoData(UUID.fromString(ALL_UUID_PLAYER), "§6§l[ВСЕМ]", true);

    public PlayerInfoData(UUID uuid, String name) {
        this(uuid, name, false);
    }

    public PlayerInfoData(String uuid, String name) {
        this(UUID.fromString(uuid), name, false);
    }

    private PlayerInfoData(UUID uuid, String name, boolean isAllPlayers) {
        this.uuid = uuid;
        this.name = name;
        this.isAllPlayers = isAllPlayers;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public boolean isAllPlayers() { return isAllPlayers; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerInfoData that = (PlayerInfoData) obj;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}

public class MainSettingScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");

    private static PlayerInfoData selectedPlayer;
    private static ButtonSelect selectButton;

    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 5;

    private static final int backgroundWidth = 216;
    private static final int backgroundHeight = 230;

    private ScrollingPlayerList playerListScroll;
    private ScrollingBlockList blockListScroll;
    private ScrollingItemList itemListScroll;
    private ScrollingSeedList seedListScroll;
    private ScrollingVillagerList villagerScroll;

    private EditBox searchAllSelectObj;

    private Button BlockButton;
    private Button ItemButton;
    private Button SpecialSettingButton;

    private boolean IsActiveSpecialSettingButton;
    private Button SpecialPlantingSeedSettingButton;
    private Button SpecialVillagerTradingSettingButton;

    private Button LogButton;

    // Блоки
    private List<BlockEntry> allBlocksMasterList;
    private String currentBlockSearchFilter = "";

    // Предметы
    private List<ItemEntry> allItemsMasterList;
    private String currentItemSearchFilter = "";

    // Семена
    private List<ItemEntry> allSeedsMasterList;
    private String currentSeedsSearchFilter = "";

    // Жители
    private List<ProfessionVillagerEntry> allVillagersMasterList;
    private String currentVillagersSearchFilter = "";

    // Типы поиска
    private enum SearchType {
        NAME,
        ID,
        MOD_ID
    }

    private static class ParsedSearchQuery {
        SearchType type;
        String query;

        ParsedSearchQuery(SearchType type, String query) {
            this.type = type;
            this.query = query.toLowerCase().trim();
        }
    }

    public MainSettingScreen() {
        super(Component.empty());
        selectedPlayer = null;
        selectButton = null;
        allBlocksMasterList = new ArrayList<>();
        allItemsMasterList = new ArrayList<>();
        allSeedsMasterList = new ArrayList<>();
        allVillagersMasterList = new ArrayList<>();
        currentBlockSearchFilter = "";
        currentItemSearchFilter = "";
        currentSeedsSearchFilter = "";
        currentVillagersSearchFilter = "";
    }

    // ==================== Методы для поиска ====================

    private ParsedSearchQuery parseSearchQuery(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ParsedSearchQuery(SearchType.NAME, "");
        }

        String trimmed = searchText.trim();

        if (trimmed.startsWith(":")) {
            String query = trimmed.substring(1).trim();
            return new ParsedSearchQuery(SearchType.ID, query);
        } else if (trimmed.startsWith("@")) {
            String query = trimmed.substring(1).trim();
            return new ParsedSearchQuery(SearchType.MOD_ID, query);
        } else {
            return new ParsedSearchQuery(SearchType.NAME, trimmed);
        }
    }

    // ==================== Методы для блоков ====================

    private List<BlockEntry> filterBlocksBySearch(List<BlockEntry> blocks, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(blocks);
        }

        ParsedSearchQuery parsed = parseSearchQuery(searchText);

        if (parsed.query.isEmpty()) {
            return new ArrayList<>(blocks);
        }

        List<BlockEntry> filtered = new ArrayList<>();

        switch (parsed.type) {
            case NAME:
                for (BlockEntry entry : blocks) {
                    if (entry.getName().toLowerCase().contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case ID:
                for (BlockEntry entry : blocks) {
                    ResourceLocation key = BuiltInRegistries.BLOCK.getKey(entry.getBlock());
                    String path = key.getPath().toLowerCase();
                    if (path.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case MOD_ID:
                for (BlockEntry entry : blocks) {
                    ResourceLocation key = BuiltInRegistries.BLOCK.getKey(entry.getBlock());
                    String modId = key.getNamespace().toLowerCase();
                    if (modId.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;
        }

        return filtered;
    }

    private void loadSelectBlockList() {
        Set<BlockEntry> allBlocksSet = new HashSet<>();

        if (selectedPlayer == null) return;

        var allID = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getAllBlockID();

        for (String id : allID) {
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null && BuiltInRegistries.BLOCK.containsKey(location)) {
                allBlocksSet.add(new BlockEntry(
                        BuiltInRegistries.BLOCK.get(location),
                        new ItemStack(BuiltInRegistries.BLOCK.get(location).asItem())
                ));
            }
        }

        allBlocksMasterList = new ArrayList<>(allBlocksSet);
        updateBlockListWithFilter();
    }

    private void updateBlockListWithFilter() {
        if (blockListScroll != null && allBlocksMasterList != null) {
            List<BlockEntry> filtered = filterBlocksBySearch(allBlocksMasterList, currentBlockSearchFilter);
            blockListScroll.updateEntries(filtered);
        }
    }

    // ==================== Методы для предметов ====================

    private List<ItemEntry> filterItemsBySearch(List<ItemEntry> items, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(items);
        }

        ParsedSearchQuery parsed = parseSearchQuery(searchText);

        if (parsed.query.isEmpty()) {
            return new ArrayList<>(items);
        }

        List<ItemEntry> filtered = new ArrayList<>();

        switch (parsed.type) {
            case NAME:
                for (ItemEntry entry : items) {
                    if (entry.getName().toLowerCase().contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case ID:
                for (ItemEntry entry : items) {
                    ResourceLocation key = BuiltInRegistries.ITEM.getKey(entry.getItem());
                    String path = key.getPath().toLowerCase();
                    if (path.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case MOD_ID:
                for (ItemEntry entry : items) {
                    ResourceLocation key = BuiltInRegistries.ITEM.getKey(entry.getItem());
                    String modId = key.getNamespace().toLowerCase();
                    if (modId.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;
        }

        return filtered;
    }

    private void loadSelectItemList() {
        Set<ItemEntry> allItemsSet = new HashSet<>();

        if (selectedPlayer == null) return;

        var allID = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getAllItemID();

        for (String id : allID) {
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null && BuiltInRegistries.ITEM.containsKey(location)) {
                allItemsSet.add(new ItemEntry(
                        BuiltInRegistries.ITEM.get(location),
                        new ItemStack(BuiltInRegistries.ITEM.get(location))
                ));
            }
        }

        allItemsMasterList = new ArrayList<>(allItemsSet);
        updateItemListWithFilter();
    }

    private void updateItemListWithFilter() {
        if (itemListScroll != null && allItemsMasterList != null) {
            List<ItemEntry> filtered = filterItemsBySearch(allItemsMasterList, currentItemSearchFilter);
            itemListScroll.updateEntries(filtered);
        }
    }

    // ==================== Методы для семян ====================

    private List<ItemEntry> filterSeedsBySearch(List<ItemEntry> items, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(items);
        }

        ParsedSearchQuery parsed = parseSearchQuery(searchText);

        if (parsed.query.isEmpty()) {
            return new ArrayList<>(items);
        }

        List<ItemEntry> filtered = new ArrayList<>();

        switch (parsed.type) {
            case NAME:
                for (ItemEntry entry : items) {
                    if (entry.getName().toLowerCase().contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case ID:
                for (ItemEntry entry : items) {
                    ResourceLocation key = BuiltInRegistries.ITEM.getKey(entry.getItem());
                    String path = key.getPath().toLowerCase();
                    if (path.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case MOD_ID:
                for (ItemEntry entry : items) {
                    ResourceLocation key = BuiltInRegistries.ITEM.getKey(entry.getItem());
                    String modId = key.getNamespace().toLowerCase();
                    if (modId.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;
        }

        return filtered;
    }

    private void loadSelectSeedsList() {
        Set<ItemEntry> allSeedsSet = new HashSet<>();

        if (selectedPlayer == null) return;

        var allID = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getAllSeedID();

        for (String id : allID) {
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null && BuiltInRegistries.ITEM.containsKey(location)) {
                allSeedsSet.add(new ItemEntry(
                        BuiltInRegistries.ITEM.get(location),
                        new ItemStack(BuiltInRegistries.ITEM.get(location))
                ));
            }
        }

        allSeedsMasterList = new ArrayList<>(allSeedsSet);
        updateSeedsListWithFilter();
    }

    private void updateSeedsListWithFilter() {
        if (seedListScroll != null && allSeedsMasterList != null) {
            List<ItemEntry> filtered = filterSeedsBySearch(allSeedsMasterList, currentSeedsSearchFilter);
            seedListScroll.updateEntries(filtered);
        }
    }

    // ==================== Методы для жителей ====================

    private List<ProfessionVillagerEntry> filterVillagersBySearch(List<ProfessionVillagerEntry> professions, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(professions);
        }

        ParsedSearchQuery parsed = parseSearchQuery(searchText);

        if (parsed.query.isEmpty()) {
            return new ArrayList<>(professions);
        }

        List<ProfessionVillagerEntry> filtered = new ArrayList<>();

        switch (parsed.type) {
            case NAME:
                for (ProfessionVillagerEntry entry : professions) {
                    if (entry.getName().toLowerCase().contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case ID:
                for (ProfessionVillagerEntry entry : professions) {
                    ResourceLocation key = BuiltInRegistries.VILLAGER_PROFESSION.getKey(entry.getProfession());
                    String path = key.getPath().toLowerCase();
                    if (path.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;

            case MOD_ID:
                for (ProfessionVillagerEntry entry : professions) {
                    ResourceLocation key = BuiltInRegistries.VILLAGER_PROFESSION.getKey(entry.getProfession());
                    String modId = key.getNamespace().toLowerCase();
                    if (modId.contains(parsed.query)) {
                        filtered.add(entry);
                    }
                }
                break;
        }

        return filtered;
    }

    private void loadSelectVillagerList() {
        Set<ProfessionVillagerEntry> allVillagersSet = new HashSet<>();

        if (selectedPlayer == null) return;

        var allID = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getAllVillagerProfessionIds();

        for (String id : allID) {
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null && BuiltInRegistries.VILLAGER_PROFESSION.containsKey(location)) {
                allVillagersSet.add(new ProfessionVillagerEntry(
                        BuiltInRegistries.VILLAGER_PROFESSION.get(location)
                ));
            }
        }

        allVillagersMasterList = new ArrayList<>(allVillagersSet);
        updateVillagersListWithFilter();
    }

    private void updateVillagersListWithFilter() {
        if (villagerScroll != null && allVillagersMasterList != null) {
            List<ProfessionVillagerEntry> filtered = filterVillagersBySearch(allVillagersMasterList, currentVillagersSearchFilter);
            villagerScroll.updateEntries(filtered);
        }
    }

    // ==================== Общие методы ====================

    private List<PlayerInfoData> getOnlinePlayers() {
        Set<PlayerInfoData> playersSet = new HashSet<>();

        for (PlayerSettings playerSett : JSONSettingCreate.loadSettings()) {
            if (Objects.equals(playerSett.getUuidPlayer(), ALL_UUID_PLAYER)) {
                playersSet.add(PlayerInfoData.ALL_PLAYERS);
                continue;
            }
            playersSet.add(new PlayerInfoData(playerSett.getUuidPlayer(), playerSett.getName()));
        }

        if (minecraft != null && minecraft.getConnection() != null) {
            for (PlayerInfo playerInfo : minecraft.getConnection().getListedOnlinePlayers()) {
                playersSet.add(new PlayerInfoData(playerInfo.getProfile().getId(), playerInfo.getProfile().getName()));
            }
        }

        if (playersSet.isEmpty() && minecraft != null && minecraft.player != null) {
            playersSet.add(new PlayerInfoData(minecraft.player.getUUID(), minecraft.player.getName().getString()));
        }

        List<PlayerInfoData> sortedPlayers = new ArrayList<>(playersSet);
        sortedPlayers.sort((p1, p2) -> {
            if (p1.isAllPlayers()) return -1;
            if (p2.isAllPlayers()) return 1;
            return p1.getName().compareToIgnoreCase(p2.getName());
        });

        return sortedPlayers;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();

        int buttonWidth = 150;
        int totalWidth = buttonWidth * 3 + BUTTON_SPACING * 2;
        int startX = (getWidth() - totalWidth) / 2;
        int startY = 5;

        BlockButton = Button.builder(
                Component.literal("Настройка блоков"),
                button -> {
                    selectButton = ButtonSelect.BlockSetting;
                    updateButtonStates();
                    IsActiveSpecialSettingButton = false;
                    init();
                }
        ).bounds(startX, startY, buttonWidth, BUTTON_HEIGHT).build();

        ItemButton = Button.builder(
                Component.literal("Настройка предметов"),
                button -> {
                    selectButton = ButtonSelect.ItemSetting;
                    updateButtonStates();
                    IsActiveSpecialSettingButton = false;
                    init();
                }
        ).bounds(startX + buttonWidth + BUTTON_SPACING, startY, buttonWidth, BUTTON_HEIGHT).build();

        SpecialSettingButton = Button.builder(
                Component.literal(IsActiveSpecialSettingButton ? "Спец возможности ▼" : "Спец возможности ▶"),
                button -> {
                    IsActiveSpecialSettingButton = !IsActiveSpecialSettingButton;
                    updateButtonStates();
                    init();
                }
        ).bounds(startX + (buttonWidth + BUTTON_SPACING) * 2, startY, buttonWidth, BUTTON_HEIGHT).build();

        if (IsActiveSpecialSettingButton) {
            SpecialPlantingSeedSettingButton = Button.builder(
                    Component.literal("Посадка семян"),
                    button -> {
                        selectButton = ButtonSelect.PlantingSeed;
                        updateButtonStates();
                        IsActiveSpecialSettingButton = false;
                        init();
                    }
            ).bounds(startX + (buttonWidth + BUTTON_SPACING) * 2, startY + BUTTON_HEIGHT + 5, buttonWidth, BUTTON_HEIGHT).build();

            SpecialVillagerTradingSettingButton = Button.builder(
                    Component.literal("Торговля с жителями"),
                    button -> {
                        selectButton = ButtonSelect.TradingVillager;
                        updateButtonStates();
                        IsActiveSpecialSettingButton = false;
                        init();
                    }
            ).bounds(startX + (buttonWidth + BUTTON_SPACING) * 2, startY + (BUTTON_HEIGHT + 5) * 2, buttonWidth, BUTTON_HEIGHT).build();
        }

        LogButton = Button.builder(
                Component.literal("L"),
                button -> {}
        ).bounds(8, getHeight() - 20, 12, 12).build();

        this.addRenderableWidget(BlockButton);
        this.addRenderableWidget(ItemButton);
        this.addRenderableWidget(SpecialSettingButton);
        this.addRenderableWidget(LogButton);

        this.playerListScroll = new ScrollingPlayerList(getWidth() / 2 - 108 - 98, getHeight() / 2 - 94, 100, getHeight() / 2 + 20, this);

        this.blockListScroll = new ScrollingBlockList(getWidth() / 2 - 102, getHeight() / 2 - 71, this.backgroundWidth - 15, this.backgroundHeight - 40, this);
        this.itemListScroll = new ScrollingItemList(getWidth() / 2 - 102, getHeight() / 2 - 71, this.backgroundWidth - 15, this.backgroundHeight - 40, this);
        this.seedListScroll = new ScrollingSeedList(getWidth() / 2 - 102, getHeight() / 2 - 71, this.backgroundWidth - 15, this.backgroundHeight - 40, this);
        this.villagerScroll = new ScrollingVillagerList(getWidth() / 2 - 102, getHeight() / 2 - 71, this.backgroundWidth - 15, this.backgroundHeight - 40, this);

        this.searchAllSelectObj = new EditBox(getFontRender(), getWidth() / 2 - 100, getHeight() / 2 - 90, this.backgroundWidth - 19, 17, Component.literal("Поиск... (:id | @modid)"));

        if (selectButton != null) {
            if (selectedPlayer != null) {
                if (selectButton == ButtonSelect.BlockSetting) {
                    loadSelectBlockList();

                    this.searchAllSelectObj.setResponder(searchText -> {
                        currentBlockSearchFilter = searchText;

                        if (searchText != null && !searchText.isEmpty()) {
                            if (searchText.startsWith(":")) {
                                searchAllSelectObj.setTextColor(0xFFFF55);
                            } else if (searchText.startsWith("@")) {
                                searchAllSelectObj.setTextColor(0x55FFFF);
                            } else {
                                searchAllSelectObj.setTextColor(0xFFFFFF);
                            }
                        } else {
                            searchAllSelectObj.setTextColor(0xFFFFFF);
                        }

                        updateBlockListWithFilter();
                    });

                    addRenderableWidget(Button.builder(
                            Component.literal("Add Block"),
                            button -> {
                                this.minecraft.setScreen(new AllBlockScreen(selectedPlayer, this));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 - 50, buttonWidth - 37, BUTTON_HEIGHT + 8).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Запрет на установку: " + (JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisablePlaceBlock() ? "§aВКЛ" : "§cВЫКЛ")),
                            button -> {
                                boolean current = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisablePlaceBlock();
                                JSONSettingCreate.SwitchGlobalDisablePlaceBlock(selectedPlayer.getUuid());
                                button.setMessage(Component.literal("Запрет на установку: " + (!current ? "§aВКЛ" : "§cВЫКЛ")));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 - 18, buttonWidth - 37, BUTTON_HEIGHT + 3).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Запрет на ломание: " + (JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisableBreakBlock() ? "§aВКЛ" : "§cВЫКЛ")),
                            button -> {
                                boolean current = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisableBreakBlock();
                                JSONSettingCreate.SwitchGlobalDisableBreakBlock(selectedPlayer.getUuid());
                                button.setMessage(Component.literal("Запрет на ломание: " + (!current ? "§aВКЛ" : "§cВЫКЛ")));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 + 12, buttonWidth - 37, BUTTON_HEIGHT + 3).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Close"),
                            button -> onClose()
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 + 73, buttonWidth - 37, BUTTON_HEIGHT).build());

                    this.addRenderableWidget(searchAllSelectObj);
                    this.addRenderableWidget(blockListScroll);
                } else if (selectButton == ButtonSelect.ItemSetting) {
                    loadSelectItemList();

                    this.searchAllSelectObj.setResponder(searchText -> {
                        currentItemSearchFilter = searchText;

                        if (searchText != null && !searchText.isEmpty()) {
                            if (searchText.startsWith(":")) {
                                searchAllSelectObj.setTextColor(0xFFFF55);
                            } else if (searchText.startsWith("@")) {
                                searchAllSelectObj.setTextColor(0x55FFFF);
                            } else {
                                searchAllSelectObj.setTextColor(0xFFFFFF);
                            }
                        } else {
                            searchAllSelectObj.setTextColor(0xFFFFFF);
                        }

                        updateItemListWithFilter();
                    });

                    addRenderableWidget(Button.builder(
                            Component.literal("Add Item"),
                            button -> {
                                this.minecraft.setScreen(new AllItemScreen(selectedPlayer, this));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 - 50, buttonWidth - 37, BUTTON_HEIGHT + 8).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Запрет на выбрасывание: " + (JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisableDropItem() ? "§aВКЛ" : "§cВЫКЛ")),
                            button -> {
                                boolean current = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisableDropItem();
                                JSONSettingCreate.SwitchGlobalDisableItemDrop(selectedPlayer.getUuid());
                                button.setMessage(Component.literal("Запрет на выбрасывание: " + (!current ? "§aВКЛ" : "§cВЫКЛ")));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 - 18, buttonWidth - 37, BUTTON_HEIGHT + 3).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Запрет на подбор: " + (JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisablePickupItem() ? "§aВКЛ" : "§cВЫКЛ")),
                            button -> {
                                boolean current = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisablePickupItem();
                                JSONSettingCreate.SwitchGlobalDisableItemPickup(selectedPlayer.getUuid());
                                button.setMessage(Component.literal("Запрет на подбор: " + (!current ? "§aВКЛ" : "§cВЫКЛ")));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 + 12, buttonWidth - 37, BUTTON_HEIGHT + 3).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Close"),
                            button -> onClose()
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 + 73, buttonWidth - 37, BUTTON_HEIGHT).build());

                    this.addRenderableWidget(searchAllSelectObj);
                    this.addRenderableWidget(itemListScroll);
                } else if (selectButton == ButtonSelect.PlantingSeed) {
                    loadSelectSeedsList();

                    this.searchAllSelectObj.setResponder(searchText -> {
                        currentSeedsSearchFilter = searchText;

                        if (searchText != null && !searchText.isEmpty()) {
                            if (searchText.startsWith(":")) {
                                searchAllSelectObj.setTextColor(0xFFFF55);
                            } else if (searchText.startsWith("@")) {
                                searchAllSelectObj.setTextColor(0x55FFFF);
                            } else {
                                searchAllSelectObj.setTextColor(0xFFFFFF);
                            }
                        } else {
                            searchAllSelectObj.setTextColor(0xFFFFFF);
                        }

                        updateSeedsListWithFilter();
                    });

                    addRenderableWidget(Button.builder(
                            Component.literal("Add Seed"),
                            button -> {
                                this.minecraft.setScreen(new AllSeedsScreen(selectedPlayer, this));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 - 50, buttonWidth - 37, BUTTON_HEIGHT + 8).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Запрет на посадку: " + (JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisablePlantingSeed() ? "§aВКЛ" : "§cВЫКЛ")),
                            button -> {
                                boolean current = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisablePlantingSeed();
                                JSONSettingCreate.SwitchGlobalDisablePlanSeed(selectedPlayer.getUuid());
                                button.setMessage(Component.literal("Запрет на посадку: " + (!current ? "§aВКЛ" : "§cВЫКЛ")));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 - 18, buttonWidth - 37, BUTTON_HEIGHT + 3).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Close"),
                            button -> onClose()
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 + 73, buttonWidth - 37, BUTTON_HEIGHT).build());

                    this.addRenderableWidget(searchAllSelectObj);
                    this.addRenderableWidget(seedListScroll);
                } else if (selectButton == ButtonSelect.TradingVillager) {
                    loadSelectVillagerList();

                    this.searchAllSelectObj.setResponder(searchText -> {
                        currentVillagersSearchFilter = searchText;

                        if (searchText != null && !searchText.isEmpty()) {
                            if (searchText.startsWith(":")) {
                                searchAllSelectObj.setTextColor(0xFFFF55);
                            } else if (searchText.startsWith("@")) {
                                searchAllSelectObj.setTextColor(0x55FFFF);
                            } else {
                                searchAllSelectObj.setTextColor(0xFFFFFF);
                            }
                        } else {
                            searchAllSelectObj.setTextColor(0xFFFFFF);
                        }

                        updateVillagersListWithFilter();
                    });

                    addRenderableWidget(Button.builder(
                            Component.literal("Add Villager"),
                            button -> {
                                this.minecraft.setScreen(new AllVillagerScreen(selectedPlayer , this));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 - 50, buttonWidth - 37, BUTTON_HEIGHT + 8).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Запрет на торговлю: " + (JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisableTradingVillager() ? "§aВКЛ" : "§cВЫКЛ")),
                            button -> {
                                boolean current = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisableTradingVillager();
                                JSONSettingCreate.SwitchGlobalDisableVillagerTrade(selectedPlayer.getUuid());
                                button.setMessage(Component.literal("Запрет на торговлю: " + (!current ? "§aВКЛ" : "§cВЫКЛ")));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 - 18, buttonWidth - 37, BUTTON_HEIGHT + 3).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Запрет на торговлю со странствующими торговцами: " + (JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisableTradingWanderingTrader() ? "§aВКЛ" : "§cВЫКЛ")),
                            button -> {
                                boolean current = JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).getDisableTradingWanderingTrader();
                                JSONSettingCreate.SwitchGlobalDisableWanderingTraderTrade(selectedPlayer.getUuid());
                                button.setMessage(Component.literal("Запрет на торговлю со странствующими торговцами: " + (!current ? "§aВКЛ" : "§cВЫКЛ")));
                            }
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 - 18 + BUTTON_HEIGHT + 5, buttonWidth - 37, BUTTON_HEIGHT + 3).build());

                    addRenderableWidget(Button.builder(
                            Component.literal("Close"),
                            button -> onClose()
                    ).bounds(getWidth() / 2 + 105, getHeight() / 2 + 73, buttonWidth - 37, BUTTON_HEIGHT).build());

                    this.addRenderableWidget(searchAllSelectObj);
                    this.addRenderableWidget(villagerScroll);
                }
            }

            playerListScroll.updateEntries(getOnlinePlayers());
            this.addRenderableWidget(playerListScroll);
        }

        if (IsActiveSpecialSettingButton) {
            if (SpecialPlantingSeedSettingButton != null) this.addRenderableWidget(SpecialPlantingSeedSettingButton);
            if (SpecialVillagerTradingSettingButton != null) this.addRenderableWidget(SpecialVillagerTradingSettingButton);
        }

        updateButtonStates();
    }

    private void updateButtonStates() {
        if (BlockButton != null) {
            BlockButton.active = selectButton != ButtonSelect.BlockSetting;
        }
        if (ItemButton != null) {
            ItemButton.active = selectButton != ButtonSelect.ItemSetting;
        }
        if (SpecialPlantingSeedSettingButton != null) {
            SpecialPlantingSeedSettingButton.active = selectButton != ButtonSelect.PlantingSeed;
        }
        if (SpecialVillagerTradingSettingButton != null) {
            SpecialVillagerTradingSettingButton.active = selectButton != ButtonSelect.TradingVillager;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, getWidth(), getHeight(), 0xCC000000);

        if (selectButton == null) {
            String Title = "§lВыберите категорию возможностей §r";
            guiGraphics.drawString(
                    this.font,
                    Component.literal(Title),
                    getWidth() / 2 - this.font.width(Title) / 2,
                    getHeight() / 2,
                    0xE1A12D,
                    false
            );
        } else {
            guiGraphics.blit(BACKGROUND_TEXTURE, getWidth() / 2 + 75, getHeight() / 2 - 155 / 2, 0, 0, 150, 180, 150, 180);
            guiGraphics.blit(BACKGROUND_TEXTURE, getWidth() / 2 - 108, getHeight() / 2 - 100, 0, 0, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);

            if (selectedPlayer == null) {
                String Title = "§lВыберите игрока §r";
                guiGraphics.drawString(
                        this.font,
                        Component.literal(Title),
                        getWidth() / 2 - this.font.width(Title) / 2,
                        getHeight() / 2,
                        0x7F7D7A,
                        false
                );
            } else {
                String Title = "§5§lTools §r";
                guiGraphics.drawString(
                        this.font,
                        Component.literal(Title),
                        getWidth() / 2 - this.font.width(Title) / 2 + 123,
                        getHeight() / 2 - 65,
                        0xE1A12D,
                        false
                );
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Отключаем стандартный фон
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

    private void selectPlayer(PlayerInfoData player) {
        selectedPlayer = player;
        currentBlockSearchFilter = "";
        currentItemSearchFilter = "";
        currentSeedsSearchFilter = "";
        currentVillagersSearchFilter = "";
        if (searchAllSelectObj != null) {
            searchAllSelectObj.setValue("");
        }
        this.init();
    }

    // ==================== Внутренние классы ====================

    public static class BlockEntry {
        private final Block block;
        private final ItemStack itemStack;
        private final String name;

        BlockEntry(Block block, ItemStack itemStack) {
            this.block = block;
            this.itemStack = itemStack;
            this.name = itemStack.getHoverName().getString();
        }

        Block getBlock() { return block; }
        ItemStack getItemStack() { return itemStack; }
        String getName() { return name; }

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

        Item getItem() { return item; }
        ItemStack getItemStack() { return itemStack; }
        String getName() { return name; }

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

    static class ProfessionVillagerEntry {
        private final String id;
        private final String name;
        private final VillagerProfession profession;

        ProfessionVillagerEntry(String id, String name, VillagerProfession profession) {
            this.id = id;
            this.name = name;
            this.profession = profession;
        }

        ProfessionVillagerEntry(VillagerProfession profession) {
            this.id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
            this.name = getProfessionDisplayName(profession);
            this.profession = profession;
        }

        String getId() { return id; }
        String getName() { return name; }
        VillagerProfession getProfession() { return profession; }

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

            // Если перевод не найден, используем форматированный path
            if (result.equals(translationKey)) {
                String path = id.getPath();
                result = path.substring(0, 1).toUpperCase() + path.substring(1).replace("_", " ");
            }

            return result;
        }
    }

    // ==================== Скроллы ====================

    private static class ScrollingPlayerList extends ObjectSelectionList<ScrollingPlayerList.PlayerSlot> {
        private static final int SLOT_HEIGHT = 30;
        private final MainSettingScreen parent;

        ScrollingPlayerList(int x, int y, int width, int height, MainSettingScreen parent) {
            super(Objects.requireNonNull(parent.minecraft), width, height, y, SLOT_HEIGHT);
            this.parent = parent;
            this.setX(x);
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getX() + this.width - 6;
        }

        void updateEntries(List<PlayerInfoData> players) {
            this.clearEntries();
            players.forEach(player -> this.addEntry(new PlayerSlot(parent, player)));
        }

        class PlayerSlot extends ObjectSelectionList.Entry<PlayerSlot> {
            private final MainSettingScreen parent;
            private final PlayerInfoData player;
            private boolean isSelected;

            PlayerSlot(MainSettingScreen parent, PlayerInfoData player) {
                this.parent = parent;
                this.player = player;
                this.isSelected = false;
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                this.isSelected = selectedPlayer != null && selectedPlayer.equals(player);

                if (isSelected) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFAA00);
                } else if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                if (player.isAllPlayers()) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x2266FF66);
                }

                if (player.isAllPlayers()) {
                    guiGraphics.fill(left + 4, top + 4, left + 24, top + 24, 0xFFAA44AA);
                    int centerX = left + 14;
                    int centerY = top + 14;
                    guiGraphics.fill(centerX - 1, centerY - 6, centerX + 1, centerY + 6, 0xFFFFFF);
                    guiGraphics.fill(centerX - 6, centerY - 1, centerX + 6, centerY + 1, 0xFFFFFF);
                } else {
                    guiGraphics.fill(left + 4, top + 4, left + 24, top + 24, 0xFF44AA44);
                }

                Font font = parent.minecraft.font;
                int color = isSelected ? 0xFFFFAA : 0xFFFFFF;

                Component displayName;
                if (player.isAllPlayers()) {
                    displayName = Component.literal("§6§l[ВСЕМ]");
                } else {
                    displayName = Component.literal(player.getName());
                }

                guiGraphics.drawString(font, displayName, left + 30, top + (height - 8) / 2, color, false);

                if (isSelected) {
                    guiGraphics.drawString(font, "✓", left + width - 15, top + (height - 8) / 2, 0x00FF00, false);
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                parent.selectPlayer(player);
                return true;
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(player.isAllPlayers() ? "[ВСЕМ]" : player.getName());
            }
        }
    }

    private static class ScrollingBlockList extends ObjectSelectionList<ScrollingBlockList.BlockSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final MainSettingScreen parent;

        ScrollingBlockList(int x, int y, int width, int height, MainSettingScreen parent) {
            super(Objects.requireNonNull(parent.minecraft), width, height, y, SLOT_HEIGHT);
            this.parent = parent;
            this.setX(x);
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getX() + this.width - 6;
        }

        void updateEntries(List<BlockEntry> blocks) {
            this.clearEntries();
            if (blocks != null) {
                blocks.forEach(block -> this.addEntry(new BlockSlot(parent, block)));
            }
        }

        static class BlockSlot extends ObjectSelectionList.Entry<BlockSlot> {
            private final MainSettingScreen parent;
            private final BlockEntry block;
            private final Button DisableplaceItemButton;
            private final Button DisableBreakItemButton;

            BlockSlot(MainSettingScreen parent, BlockEntry block) {
                this.parent = parent;
                this.block = block;

                this.DisableplaceItemButton = Button.builder(
                                Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPlaceBlock(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "✖" : "✔"),
                                button -> {
                                    JSONSettingCreate.SwitchDisablePlaceBlock(selectedPlayer.getUuid(), BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString());
                                    button.setMessage(Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPlaceBlock(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "✖" : "✔"));
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить устанавливать блоки")))
                        .build();

                this.DisableBreakItemButton = Button.builder(
                                Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canBreakBlock(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "✖" : "✔"),
                                button -> {
                                    JSONSettingCreate.SwitchDisableBreakBlock(selectedPlayer.getUuid(), BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString());
                                    button.setMessage(Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canBreakBlock(BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString()) ? "✖" : "✔"));
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить ломать блоки")))
                        .build();
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                Font font = parent.minecraft.font;
                ItemStack stack = block.getItemStack();
                String name = block.getName();

                if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                guiGraphics.renderItem(stack, left + 4, top + 14);
                guiGraphics.renderItemDecorations(font, stack, left + 4, top + 14);

                if (font.width(name) > width - 90) {
                    name = font.plainSubstrByWidth(name, width - 90) + "...";
                }
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block.getBlock());
                String idString = key.toString();
                if (font.width(idString) > width - 120) {
                    idString = font.plainSubstrByWidth(idString, width - 120) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                int buttonWidth = 20;
                int buttonHeight = 20;
                int buttonX = left + width - buttonWidth - 35;
                int buttonY = top + (height - buttonHeight) / 2;

                DisableplaceItemButton.setX(buttonX);
                DisableplaceItemButton.setY(buttonY);

                DisableBreakItemButton.setX(buttonX + 25);
                DisableBreakItemButton.setY(buttonY);

                DisableplaceItemButton.render(guiGraphics, mouseX, mouseY, partialTick);
                DisableBreakItemButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (DisableplaceItemButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                } else if (DisableBreakItemButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                return false;
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(block.getName());
            }
        }
    }

    private static class ScrollingItemList extends ObjectSelectionList<ScrollingItemList.ItemSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final MainSettingScreen parent;

        ScrollingItemList(int x, int y, int width, int height, MainSettingScreen parent) {
            super(Objects.requireNonNull(parent.minecraft), width, height, y, SLOT_HEIGHT);
            this.parent = parent;
            this.setX(x);
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getX() + this.width - 6;
        }

        void updateEntries(List<ItemEntry> items) {
            this.clearEntries();
            if (items != null) {
                items.forEach(item -> this.addEntry(new ItemSlot(parent, item)));
            }
        }

        static class ItemSlot extends ObjectSelectionList.Entry<ItemSlot> {
            private final MainSettingScreen parent;
            private final ItemEntry item;
            private final Button DisableDropItemButton;
            private final Button DisablePickupItemButton;

            ItemSlot(MainSettingScreen parent, ItemEntry item) {
                this.parent = parent;
                this.item = item;

                this.DisableDropItemButton = Button.builder(
                                Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canDropItem(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"),
                                button -> {
                                    JSONSettingCreate.SwitchDisableItemDrop(selectedPlayer.getUuid(), BuiltInRegistries.ITEM.getKey(item.getItem()).toString());
                                    button.setMessage(Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canDropItem(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"));
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить выбрасывать предмет")))
                        .build();

                this.DisablePickupItemButton = Button.builder(
                                Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPickupItem(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"),
                                button -> {
                                    JSONSettingCreate.SwitchDisableItemPickup(selectedPlayer.getUuid(), BuiltInRegistries.ITEM.getKey(item.getItem()).toString());
                                    button.setMessage(Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPickupItem(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"));
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить подбирать предмет")))
                        .build();
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                Font font = parent.minecraft.font;
                ItemStack stack = item.getItemStack();
                String name = item.getName();

                if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                guiGraphics.renderItem(stack, left + 4, top + 14);
                guiGraphics.renderItemDecorations(font, stack, left + 4, top + 14);

                if (font.width(name) > width - 90) {
                    name = font.plainSubstrByWidth(name, width - 90) + "...";
                }
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.ITEM.getKey(item.getItem());
                String idString = key.toString();
                if (font.width(idString) > width - 120) {
                    idString = font.plainSubstrByWidth(idString, width - 120) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                int buttonWidth = 20;
                int buttonHeight = 20;
                int buttonX = left + width - buttonWidth - 35;
                int buttonY = top + (height - buttonHeight) / 2;

                DisableDropItemButton.setX(buttonX);
                DisableDropItemButton.setY(buttonY);

                DisablePickupItemButton.setX(buttonX + 25);
                DisablePickupItemButton.setY(buttonY);

                DisableDropItemButton.render(guiGraphics, mouseX, mouseY, partialTick);
                DisablePickupItemButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (DisableDropItemButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                } else if (DisablePickupItemButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                return false;
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(item.getName());
            }
        }
    }

    private static class ScrollingSeedList extends ObjectSelectionList<ScrollingSeedList.SeedSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final MainSettingScreen parent;

        ScrollingSeedList(int x, int y, int width, int height, MainSettingScreen parent) {
            super(Objects.requireNonNull(parent.minecraft), width, height, y, SLOT_HEIGHT);
            this.parent = parent;
            this.setX(x);
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getX() + this.width - 6;
        }

        void updateEntries(List<ItemEntry> items) {
            this.clearEntries();
            if (items != null) {
                items.forEach(item -> this.addEntry(new SeedSlot(parent, item)));
            }
        }

        static class SeedSlot extends ObjectSelectionList.Entry<SeedSlot> {
            private final MainSettingScreen parent;
            private final ItemEntry item;
            private final Button DisablePlanSeedButton;

            SeedSlot(MainSettingScreen parent, ItemEntry item) {
                this.parent = parent;
                this.item = item;

                this.DisablePlanSeedButton = Button.builder(
                                Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPlanSeed(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"),
                                button -> {
                                    JSONSettingCreate.SwitchDisableSeedPlan(selectedPlayer.getUuid(), BuiltInRegistries.ITEM.getKey(item.getItem()).toString());
                                    button.setMessage(Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canPlanSeed(BuiltInRegistries.ITEM.getKey(item.getItem()).toString()) ? "✖" : "✔"));
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить сажать культуру")))
                        .build();
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                Font font = parent.minecraft.font;
                ItemStack stack = item.getItemStack();
                String name = item.getName();

                if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                guiGraphics.renderItem(stack, left + 4, top + 14);
                guiGraphics.renderItemDecorations(font, stack, left + 4, top + 14);

                if (font.width(name) > width - 90) {
                    name = font.plainSubstrByWidth(name, width - 90) + "...";
                }
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.ITEM.getKey(item.getItem());
                String idString = key.toString();
                if (font.width(idString) > width - 120) {
                    idString = font.plainSubstrByWidth(idString, width - 120) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                int buttonWidth = 20;
                int buttonHeight = 20;
                int buttonX = left + width - buttonWidth - 35;
                int buttonY = top + (height - buttonHeight) / 2;

                DisablePlanSeedButton.setX(buttonX + 25);
                DisablePlanSeedButton.setY(buttonY);

                DisablePlanSeedButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return DisablePlanSeedButton.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(item.getName());
            }
        }
    }

    private static class ScrollingVillagerList extends ObjectSelectionList<ScrollingVillagerList.VillagerSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final MainSettingScreen parent;

        ScrollingVillagerList(int x, int y, int width, int height, MainSettingScreen parent) {
            super(Objects.requireNonNull(parent.minecraft), width, height, y, SLOT_HEIGHT);
            this.parent = parent;
            this.setX(x);
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getX() + this.width - 6;
        }

        void updateEntries(List<ProfessionVillagerEntry> items) {
            this.clearEntries();
            if (items != null) {
                items.forEach(item -> this.addEntry(new VillagerSlot(parent, item)));
            }
        }

        static class VillagerSlot extends ObjectSelectionList.Entry<VillagerSlot> {
            private final MainSettingScreen parent;
            private final ProfessionVillagerEntry profession;
            private final Button DisableTradVillagerButton;

            VillagerSlot(MainSettingScreen parent, ProfessionVillagerEntry profession) {
                this.parent = parent;
                this.profession = profession;

                this.DisableTradVillagerButton = Button.builder(
                                Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canTradeWithVillager(profession.getId()) ? "✖" : "✔"),
                                button -> {
                                    JSONSettingCreate.SwitchDisableVillagerTrade(selectedPlayer.getUuid(), profession.getId());
                                    button.setMessage(Component.literal(!JSONSettingCreate.GetPlayerSettingsOfUUID(selectedPlayer.getUuid()).canTradeWithVillager(profession.getId()) ? "✖" : "✔"));
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Запретить торговлю")))
                        .build();
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                Font font = parent.minecraft.font;
                String name = profession.getName();

                if (hovered) {
                    guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);
                }

                if (font.width(name) > width - 90) {
                    name = font.plainSubstrByWidth(name, width - 90) + "...";
                }
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession.getProfession());
                String idString = key.toString();
                if (font.width(idString) > width - 120) {
                    idString = font.plainSubstrByWidth(idString, width - 120) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                int buttonWidth = 20;
                int buttonHeight = 20;
                int buttonX = left + width - buttonWidth - 35;
                int buttonY = top + (height - buttonHeight) / 2;

                DisableTradVillagerButton.setX(buttonX + 25);
                DisableTradVillagerButton.setY(buttonY);

                DisableTradVillagerButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return DisableTradVillagerButton.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(profession.getName());
            }
        }
    }
}