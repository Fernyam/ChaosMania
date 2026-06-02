package net.fernyam.chaosmania.gui.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchUtils {

    // ==================== Типы поиска ====================

    public enum SearchType {
        NAME,
        ID,
        MOD_ID
    }

    public static class ParsedSearchQuery {
        public final SearchType type;
        public final String query;

        public ParsedSearchQuery(SearchType type, String query) {
            this.type = type;
            this.query = query.toLowerCase().trim();
        }

        public boolean isEmpty() {
            return query.isEmpty();
        }
    }

    // ==================== Парсинг строки поиска ====================

    public static ParsedSearchQuery parseQuery(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ParsedSearchQuery(SearchType.NAME, "");
        }

        String trimmed = searchText.trim();

        if (trimmed.startsWith(":")) {
            return new ParsedSearchQuery(SearchType.ID, trimmed.substring(1).trim());
        } else if (trimmed.startsWith("@")) {
            return new ParsedSearchQuery(SearchType.MOD_ID, trimmed.substring(1).trim());
        } else {
            return new ParsedSearchQuery(SearchType.NAME, trimmed);
        }
    }

    public static int getSearchTextColor(String text) {
        if (text == null || text.isEmpty()) return 0xFFFFFF;
        if (text.startsWith(":")) return 0xFFFF55;
        if (text.startsWith("@")) return 0x55FFFF;
        return 0xFFFFFF;
    }

    // ==================== Проверка соответствия ====================

    // Переименовал параметры: nameGetter, idGetter вместо getName, getId
    public static <T> boolean matches(T element, ParsedSearchQuery query,
                                      Function<T, String> nameGetter,
                                      Function<T, String> idGetter) {
        if (query.isEmpty()) return true;

        switch (query.type) {
            case NAME:
                String name = nameGetter.apply(element);
                return name != null && name.toLowerCase().contains(query.query);
            case ID:
                String id = idGetter.apply(element);
                return id != null && id.toLowerCase().contains(query.query);
            case MOD_ID:
                String fullId = idGetter.apply(element);
                if (fullId == null) return false;
                String modId = fullId.split(":")[0];
                return modId.toLowerCase().contains(query.query);
            default:
                return false;
        }
    }

    // Переименовал параметры
    public static <T> List<T> filter(List<T> items, String searchText,
                                     Function<T, String> nameGetter,
                                     Function<T, String> idGetter) {
        if (items == null || items.isEmpty()) return List.of();

        ParsedSearchQuery query = parseQuery(searchText);
        if (query.isEmpty()) return List.copyOf(items);

        return items.stream()
                .filter(item -> matches(item, query, nameGetter, idGetter))
                .collect(Collectors.toList());
    }

    // ==================== Сортировка ====================

    public static <T> void sortWithActiveFirst(List<T> items,
                                               Function<T, Boolean> isActive,
                                               Function<T, String> nameGetter) {
        items.sort((a, b) -> {
            boolean aActive = isActive.apply(a);
            boolean bActive = isActive.apply(b);
            if (aActive && !bActive) return -1;
            if (!aActive && bActive) return 1;
            return nameGetter.apply(a).compareToIgnoreCase(nameGetter.apply(b));
        });
    }

    // ==================== Удобные методы для Block ====================

    public static String getBlockName(Block block) {
        return block.getName().getString();
    }

    public static String getBlockId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    public static List<Block> filterBlocks(List<Block> blocks, String searchText) {
        return filter(blocks, searchText, SearchUtils::getBlockName, SearchUtils::getBlockId);
    }

    public static void sortBlocksWithActiveFirst(List<Block> blocks, Function<Block, Boolean> isActive) {
        sortWithActiveFirst(blocks, isActive, SearchUtils::getBlockName);
    }

    // ==================== Удобные методы для Item ====================

    public static String getItemName(Item item) {
        return new ItemStack(item).getHoverName().getString();
    }

    public static String getItemId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    public static List<Item> filterItems(List<Item> items, String searchText) {
        return filter(items, searchText, SearchUtils::getItemName, SearchUtils::getItemId);
    }

    public static void sortItemsWithActiveFirst(List<Item> items, Function<Item, Boolean> isActive) {
        sortWithActiveFirst(items, isActive, SearchUtils::getItemName);
    }

    // ==================== Удобные методы для VillagerProfession ====================

    public static String getVillagerProfessionName(VillagerProfession profession) {
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

    public static String getVillagerProfessionId(VillagerProfession profession) {
        return BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString();
    }

    public static List<VillagerProfession> filterVillagerProfessions(List<VillagerProfession> professions, String searchText) {
        return filter(professions, searchText, SearchUtils::getVillagerProfessionName, SearchUtils::getVillagerProfessionId);
    }

    public static void sortVillagerProfessionsWithActiveFirst(List<VillagerProfession> professions, Function<VillagerProfession, Boolean> isActive) {
        sortWithActiveFirst(professions, isActive, SearchUtils::getVillagerProfessionName);
    }
}