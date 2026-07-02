package net.fernyam.chaosmania.gui.custom;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;

public abstract class BaseSelectionScreen<T> extends Screen {
    protected static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");
    protected static final int BUTTON_HEIGHT = 20;
    protected static final int BACKGROUND_WIDTH = 216;
    protected static final int BACKGROUND_HEIGHT = 230;

    protected final PlayerInfoData player;
    protected final MainSettingScreen parentScreen;

    protected EditBox searchBox;
    protected SelectionListWidget selectionList;
    protected List<EntryObj> allEntriesMasterList = new ArrayList<>();
    protected String currentSearchFilter = "";

    protected enum SearchType { NAME, ID, MOD_ID }
    protected static class ParsedSearchQuery {
        SearchType type; String query;
        ParsedSearchQuery(SearchType type, String query) { this.type = type; this.query = query.toLowerCase().trim(); }
    }

    // Абстрактные методы
    public abstract Component getScreenTitle();
    protected abstract Collection<T> getAllElements();
    protected abstract String getElementId(T element);
    protected abstract String getElementName(T element);
    protected abstract void addElementToPlayer(T element);
    protected abstract void removeElementFromPlayer(T element);
    protected abstract boolean isElementInPlayerSettings(T element);
    protected abstract boolean shouldDisplayElement(T element);
    protected abstract boolean isSpecialIcon();
    protected abstract void renderExtra(GuiGraphics gui, int left, int top, int width, int height, T element);

    public BaseSelectionScreen(PlayerInfoData player, MainSettingScreen parentScreen) {
        super(Component.empty());
        this.player = player;
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;

        addRenderableWidget(Button.builder(
                Component.literal("← Назад"),
                btn -> minecraft.setScreen(parentScreen)
        ).bounds(centerX - BACKGROUND_WIDTH / 2, height - 25, BACKGROUND_WIDTH, BUTTON_HEIGHT).build());

        searchBox = new EditBox(getFontRender(), centerX - 98, height / 2 - 112 + 7, BACKGROUND_WIDTH - 20, 17, Component.literal("Поиск... (:id | @modid)"));
        searchBox.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBox);

        selectionList = new SelectionListWidget(this,
                centerX - BACKGROUND_WIDTH / 2 + 10,
                height / 2 - 117 + 25 + 7,
                BACKGROUND_WIDTH - 20,
                BACKGROUND_HEIGHT - 45
        );
        addRenderableWidget(selectionList);

        loadAndUpdateEntries();
    }

    protected void onSearchChanged(String text) {
        currentSearchFilter = text;
        updateSearchColor(text);
        updateListFilter();
    }

    protected void updateSearchColor(String text) {
        if (text != null && !text.isEmpty()) {
            if (text.startsWith(":")) searchBox.setTextColor(0xFFFF55);
            else if (text.startsWith("@")) searchBox.setTextColor(0x55FFFF);
            else searchBox.setTextColor(0xFFFFFF);
        } else {
            searchBox.setTextColor(0xFFFFFF);
        }
    }

    protected void loadAndUpdateEntries() {
        allEntriesMasterList.clear();
        for (T element : getAllElements()) {
            if (!shouldDisplayElement(element)) continue;
            allEntriesMasterList.add(new EntryObj(element, getElementName(element), getElementId(element)));
        }
        sortEntriesWithActiveFirst(allEntriesMasterList);
        updateListFilter();
    }

    protected void updateListFilter() {
        if (selectionList == null) return;
        List<EntryObj> filtered = filterEntriesBySearch(allEntriesMasterList, currentSearchFilter);
        sortEntriesWithActiveFirst(filtered);
        selectionList.updateEntries(filtered);
    }

    protected List<EntryObj> filterEntriesBySearch(List<EntryObj> entries, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return new ArrayList<>(entries);
        ParsedSearchQuery parsed = parseSearchQuery(searchText);
        if (parsed.query.isEmpty()) return new ArrayList<>(entries);

        Predicate<EntryObj> predicate = switch (parsed.type) {
            case NAME -> e -> e.name.toLowerCase().contains(parsed.query);
            case ID -> e -> e.id.toLowerCase().contains(parsed.query);
            case MOD_ID -> e -> e.id.split(":")[0].toLowerCase().contains(parsed.query);
        };
        return entries.stream().filter(predicate).collect(Collectors.toList());
    }

    protected void sortEntriesWithActiveFirst(List<EntryObj> entries) {
        entries.sort((a, b) -> {
            boolean aActive = isElementInPlayerSettings(a.element);
            boolean bActive = isElementInPlayerSettings(b.element);
            if (aActive && !bActive) return -1;
            if (!aActive && bActive) return 1;
            return a.name.compareToIgnoreCase(b.name);
        });
    }

    protected ParsedSearchQuery parseSearchQuery(String text) {
        if (text == null || text.trim().isEmpty()) return new ParsedSearchQuery(SearchType.NAME, "");
        String trimmed = text.trim();
        if (trimmed.startsWith(":")) return new ParsedSearchQuery(SearchType.ID, trimmed.substring(1).trim());
        if (trimmed.startsWith("@")) return new ParsedSearchQuery(SearchType.MOD_ID, trimmed.substring(1).trim());
        return new ParsedSearchQuery(SearchType.NAME, trimmed);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, width, height, 0xCC000000);
        guiGraphics.blit(BACKGROUND_TEXTURE, width / 2 - BACKGROUND_WIDTH / 2, height / 2 - 125 + 7, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT - 5, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        Component title = getScreenTitle();
        guiGraphics.drawString(font, title, width / 2 - font.width(title) / 2, 5, 0xFFFFFF, true);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}
    @Override public boolean isPauseScreen() { return false; }
    @Override public void onClose() { if (minecraft != null) minecraft.setScreen(null); }
    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    public Font getFontRender() { return minecraft.font; }

    // ==================== Внутренние классы ====================

    public class EntryObj {
        public final T element;
        public final String name;
        public final String id;
        public EntryObj(T element, String name, String id) {
            this.element = element;
            this.name = name;
            this.id = id;
        }
    }

    protected class SelectionListWidget extends ObjectSelectionList<SelectionListWidget.Slot> {
        private final BaseSelectionScreen<T> parent;
        private static final int SLOT_HEIGHT = 55;
        private List<EntryObj> entries = new ArrayList<>();

        public SelectionListWidget(BaseSelectionScreen<T> parent, int x, int y, int width, int height) {
            super(parent.minecraft, width, height, y, SLOT_HEIGHT);
            this.parent = parent;
            this.setX(x);
        }

        public void updateEntries(List<EntryObj> newEntries) {
            this.entries = new ArrayList<>(newEntries);
            clearEntries();
            for (EntryObj e : entries) addEntry(new Slot(e));
        }

        @Override public int getRowWidth() { return width; }
        @Override protected int getScrollbarPosition() { return getX() + width - 6; }

        protected class Slot extends ObjectSelectionList.Entry<Slot> {
            private final EntryObj entry;
            private final Button actionButton;

            public Slot(EntryObj entry) {
                this.entry = entry;

                this.actionButton = Button.builder(
                        Component.literal("?"),
                        btn -> {
                            if (parent.isElementInPlayerSettings(entry.element)) {
                                parent.removeElementFromPlayer(entry.element);
                            } else {
                                parent.addElementToPlayer(entry.element);
                            }
                            updateButtonText();
                            parent.updateListFilter();
                        }
                ).bounds(0, 0, 20, 20).tooltip(Tooltip.create(Component.literal("Добавить/удалить"))).build();

                updateButtonText();
            }

            private void updateButtonText() {
                boolean exists = parent.isElementInPlayerSettings(entry.element);
                actionButton.setMessage(Component.literal(exists ? "§c-" : "§a+"));
            }

            @Override
            public void render(@NotNull GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                if (parent.minecraft == null) return;

                updateButtonText();

                Font font = parent.minecraft.font;
                if (hovered) guiGraphics.fill(left, top, left + width, top + height, 0x44FFFFFF);

                parent.renderExtra(guiGraphics, left, top, width, height, entry.element);

                String name = entry.name;
                if (font.width(name) > width - 80) name = font.plainSubstrByWidth(name, width - 80) + "...";
                guiGraphics.drawString(font, name, left + 28, top + 11, 0xFFFFFF, false);

                String id = entry.id;
                if (font.width(id) > width - 100) id = font.plainSubstrByWidth(id, width - 100) + "...";
                guiGraphics.drawString(font, id, left + 28, top + 23, 0x888888, false);

                actionButton.setX(left + width - 20 - 35 + 20);
                actionButton.setY(top + (height - 20) / 2);
                actionButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public boolean mouseClicked(double mx, double my, int button) {
                return actionButton.mouseClicked(mx, my, button);
            }

            @Override
            public @NotNull Component getNarration() {
                return Component.literal(entry.name);
            }
        }
    }
}