package net.fernyam.chaosmania.gui.custom;

import net.fernyam.chaosmania.data.JSONSettingCreate;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;

public class AllVillagerScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");

    private static PlayerInfoData player;
    public static MainSettingScreen parentScreen;

    private static final int BUTTON_HEIGHT = 20;
    private static final int backgroundWidth = 216;
    private static final int backgroundHeight = 230;

    private EditBox searchAllVillagers;
    private ScrollingVillagerList villagerListScroll;

    private List<ProfessionVillagerEntry> allVillagersMasterList;
    private String currentVillagersSearchFilter = "";

    // Типы поиска
    private enum SearchType {
        NAME,           // обычный поиск по имени
        ID,             // поиск по ID (префикс :)
        MOD_ID          // поиск по MOD_ID (префикс @)
    }

    private static class ParsedSearchQuery {
        SearchType type;
        String query;

        ParsedSearchQuery(SearchType type, String query) {
            this.type = type;
            this.query = query.toLowerCase().trim();
        }
    }

    protected AllVillagerScreen(PlayerInfoData player, MainSettingScreen parentScreen) {
        super(Component.literal("Добавление настройки торговли " + (player.isAllPlayers() ? player.getName() : "§9§l" + player.getName())));
        if (player == null) onClose();

        AllVillagerScreen.player = player;
        AllVillagerScreen.parentScreen = parentScreen;
        this.allVillagersMasterList = new ArrayList<>();
        this.currentVillagersSearchFilter = "";

        loadAllVillagers();
    }

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

    private void sortVillagersWithActiveFirst(List<ProfessionVillagerEntry> professions) {
        professions.sort((a, b) -> {
            boolean aAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isVillagerProfessionExists(a.getId());
            boolean bAdded = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                    .isVillagerProfessionExists(b.getId());

            if (aAdded && !bAdded) {
                return -1;
            } else if (!aAdded && bAdded) {
                return 1;
            } else {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }

    private void updateVillagerListWithFilter() {
        if (villagerListScroll == null) return;

        if (allVillagersMasterList != null) {
            List<ProfessionVillagerEntry> filtered = filterVillagersBySearch(allVillagersMasterList, currentVillagersSearchFilter);
            sortVillagersWithActiveFirst(filtered);
            villagerListScroll.updateEntries(filtered);
        }
    }

    private void loadAllVillagers() {
        Set<ProfessionVillagerEntry> allVillagersSet = new HashSet<>();

        for (VillagerProfession profession : BuiltInRegistries.VILLAGER_PROFESSION) {
            ResourceLocation id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
            String path = id.getPath();

            // Пропускаем NITWIT (не торгует) и NONE (безработный)
            if (path.equals("nitwit") || path.equals("none")) {
                continue;
            }

            // Получаем отображаемое имя через ключ перевода
            String translationKey = getProfessionDisplayName(profession);
            String displayName = Component.translatable(translationKey).getString();

            ProfessionVillagerEntry entry = new ProfessionVillagerEntry(
                    id.toString(),
                    displayName,
                    profession
            );
            allVillagersSet.add(entry);
        }

        allVillagersMasterList = new ArrayList<>(allVillagersSet);
        sortVillagersWithActiveFirst(allVillagersMasterList);
        updateVillagerListWithFilter();
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

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        // Кнопка "Назад"
        this.addRenderableWidget(Button.builder(
                Component.literal("← Назад"),
                button -> {
                    this.minecraft.setScreen(parentScreen);
                }
        ).bounds(centerX - backgroundWidth / 2, this.height - 25, backgroundWidth, BUTTON_HEIGHT).build());

        // Поле поиска
        this.searchAllVillagers = new EditBox(getFontRender(), this.width / 2 - 98, this.height / 2 - 112 + 7, backgroundWidth - 20, 17, Component.literal("Поиск... (:id | @modid)"));

        this.searchAllVillagers.setResponder(searchText -> {
            currentVillagersSearchFilter = searchText;

            if (searchText != null && !searchText.isEmpty()) {
                if (searchText.startsWith(":")) {
                    searchAllVillagers.setTextColor(0xFFFF55);
                } else if (searchText.startsWith("@")) {
                    searchAllVillagers.setTextColor(0x55FFFF);
                } else {
                    searchAllVillagers.setTextColor(0xFFFFFF);
                }
            } else {
                searchAllVillagers.setTextColor(0xFFFFFF);
            }

            updateVillagerListWithFilter();
        });

        // Список профессий
        this.villagerListScroll = new ScrollingVillagerList(
                this.width / 2 - backgroundWidth / 2 + 10,
                this.height / 2 - 117 + 25 + 7,
                backgroundWidth - 20,
                backgroundHeight - 45,
                this
        );

        updateVillagerListWithFilter();

        this.addRenderableWidget(searchAllVillagers);
        this.addRenderableWidget(villagerListScroll);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xCC000000);

        guiGraphics.blit(BACKGROUND_TEXTURE, this.width / 2 - backgroundWidth / 2, this.height / 2 - 125 + 7, 0, 0, backgroundWidth, backgroundHeight - 5, backgroundWidth, backgroundHeight);

        guiGraphics.drawString(
                this.font,
                this.title,
                this.width / 2 - this.font.width(this.title) / 2,
                5,
                0xFFFFFF,
                true
        );

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Отключаем стандартный фон
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            player = null;
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

    // ==================== Внутренние классы ====================

    static class ProfessionVillagerEntry {
        private final String id;
        private final String name;
        private final VillagerProfession profession;

        ProfessionVillagerEntry(String id, String name, VillagerProfession profession) {
            this.id = id;
            this.name = name;
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
    }

    // ==================== Скролл список ====================

    private static class ScrollingVillagerList extends ObjectSelectionList<ScrollingVillagerList.VillagerSlot> {
        private static final int SLOT_HEIGHT = 55;
        private final AllVillagerScreen parent;

        ScrollingVillagerList(int x, int y, int width, int height, AllVillagerScreen parent) {
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

        void updateEntries(List<ProfessionVillagerEntry> professions) {
            this.clearEntries();
            if (professions != null) {
                professions.forEach(profession -> this.addEntry(new VillagerSlot(parent, profession)));
            }
        }

        static class VillagerSlot extends ObjectSelectionList.Entry<VillagerSlot> {
            private final AllVillagerScreen parent;
            private final ProfessionVillagerEntry profession;
            private final Button AddVillagerButton;

            VillagerSlot(AllVillagerScreen parent, ProfessionVillagerEntry profession) {
                this.parent = parent;
                this.profession = profession;

                boolean isExists = JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                        .isVillagerProfessionExists(profession.getId());

                this.AddVillagerButton = Button.builder(
                                Component.literal(isExists ? "§c-" : "§a+"),
                                button -> {
                                    if (JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                                            .isVillagerProfessionExists(profession.getId())) {
                                        JSONSettingCreate.RemoveVillagerProfession(player.getUuid(), profession.getId());
                                    } else {
                                        JSONSettingCreate.AddVillagerProfession(player.getUuid(), profession.getId());
                                    }
                                    button.setMessage(Component.literal(JSONSettingCreate.GetPlayerSettingsOfUUID(player.getUuid())
                                            .isVillagerProfessionExists(profession.getId()) ? "§c-" : "§a+"));
                                    parent.updateVillagerListWithFilter();
                                }
                        ).bounds(0, 0, 20, 20)
                        .tooltip(Tooltip.create(Component.literal("Добавить/удалить профессию из списка")))
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

                if (font.width(name) > width - 80) {
                    name = font.plainSubstrByWidth(name, width - 80) + "...";
                }
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                ResourceLocation key = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession.getProfession());
                String idString = key.toString();
                if (font.width(idString) > width - 100) {
                    idString = font.plainSubstrByWidth(idString, width - 100) + "...";
                }
                guiGraphics.drawString(font, idString, left + 28, top + 23, 0x888888, false);

                int buttonWidth = 20;
                int buttonHeight = 20;
                int buttonX = left + width - buttonWidth - 35;
                int buttonY = top + (height - buttonHeight) / 2;

                AddVillagerButton.setX(buttonX + 20);
                AddVillagerButton.setY(buttonY);

                AddVillagerButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return AddVillagerButton.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(profession.getName());
            }
        }
    }
}