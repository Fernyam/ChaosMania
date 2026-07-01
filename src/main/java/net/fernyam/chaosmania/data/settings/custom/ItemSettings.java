package net.fernyam.chaosmania.data.settings.custom;

import net.fernyam.chaosmania.data.settings.BaseSettings;

import java.util.ArrayList;
import java.util.List;

public class ItemSettings extends BaseSettings {
    public static class ItemEntry {
        private String idItem;
        private boolean canDrop = true;
        private boolean canPickup = true;

        public ItemEntry() {}

        public ItemEntry(String idItem) {
            this.idItem = idItem;
        }

        public ItemEntry(String idItem, boolean canDrop, boolean canPickup) {
            this.idItem = idItem;
            this.canDrop = canDrop;
            this.canPickup = canPickup;
        }

        public String getIdItem() { return idItem; }
        public void setIdItem(String idItem) { this.idItem = idItem; }

        public boolean canDrop() { return canDrop; }
        public void setCanDrop(boolean canDrop) { this.canDrop = canDrop; }

        public boolean canPickup() { return canPickup; }
        public void setCanPickup(boolean canPickup) { this.canPickup = canPickup; }

        public void toggleDrop() { this.canDrop = !this.canDrop; }
        public void togglePickup() { this.canPickup = !this.canPickup; }
    }

    public ItemSettings() {}

    public ItemSettings(String uuid, String name) {
        this.uuidPlayer = uuid;
        this.namePlayer = name;
    }

    private boolean itemDropControlEnabled = false;
    private boolean itemPickupControlEnabled = false;
    private ListType listTypeDrop = ListType.BLACK_LIST;
    private ListType listTypePickup = ListType.BLACK_LIST;
    private List<ItemEntry> items = new ArrayList<>();


    // Геттеры и сеттеры
    public boolean isItemDropControlEnabled() { return itemDropControlEnabled; }
    public void setItemDropControlEnabled(boolean itemDropControlEnabled) { this.itemDropControlEnabled = itemDropControlEnabled; }
    public void toggleGlobalDropItem() { this.itemDropControlEnabled = !this.itemDropControlEnabled; }

    public boolean isItemPickupControlEnabled() { return itemPickupControlEnabled; }
    public void setItemPickupControlEnabled(boolean itemPickupControlEnabled) { this.itemPickupControlEnabled = itemPickupControlEnabled; }
    public void toggleGlobalPickupItem() { this.itemPickupControlEnabled = !this.itemPickupControlEnabled; }

    public ListType getListTypeDrop() { return listTypeDrop; }
    public void setListTypeDrop(ListType listTypeDrop) { this.listTypeDrop = listTypeDrop; }
    public void toggleListTypeDrop() {
        this.listTypeDrop = (this.listTypeDrop == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public ListType getListTypePickup() { return listTypePickup; }
    public void setListTypePickup(ListType listTypePickup) { this.listTypePickup = listTypePickup; }
    public void toggleListTypePickup() {
        this.listTypePickup = (this.listTypePickup == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public List<ItemEntry> getItems() { return items; }
    public void setItems(List<ItemEntry> items) { this.items = items; }

    // Методы работы с предметами
    public ItemEntry getOrCreateItem(String id) {
        for (ItemEntry entry : items) {
            if (entry.getIdItem().equals(id)) {
                return entry;
            }
        }
        ItemEntry newEntry = new ItemEntry(id);
        items.add(newEntry);
        return newEntry;
    }

    public void addItem(String id) {
        if (!isItemExists(id)) {
            items.add(new ItemEntry(id));
        }
    }

    public boolean isItemExists(String id) {
        return items.stream().anyMatch(e -> e.getIdItem().equals(id));
    }

    public void removeItem(String id) {
        items.removeIf(e -> e.getIdItem().equals(id));
    }

    public void toggleItemDrop(String id) {
        getOrCreateItem(id).toggleDrop();
    }

    public void toggleItemPickup(String id) {
        getOrCreateItem(id).togglePickup();
    }

    public void setItemDrop(String id, boolean canDrop) {
        getOrCreateItem(id).setCanDrop(canDrop);
    }

    public void setItemPickup(String id, boolean canPickup) {
        getOrCreateItem(id).setCanPickup(canPickup);
    }

    public boolean canDropItem(String id) {

        ItemEntry entry = getItemEntry(id);
        boolean isInList = entry != null;
        boolean canDrop = isInList && entry.canDrop();

        if (listTypeDrop == ListType.BLACK_LIST) {
            return !isInList || canDrop;
        } else {
            return isInList && canDrop;
        }
    }

    public boolean canPickupItem(String id) {

        ItemEntry entry = getItemEntry(id);
        boolean isInList = entry != null;
        boolean canPickup = isInList && entry.canPickup();

        if (listTypePickup == ListType.BLACK_LIST) {
            return !isInList || canPickup;
        } else {
            return isInList && canPickup;
        }
    }

    private ItemEntry getItemEntry(String id) {
        return items.stream()
                .filter(e -> e.getIdItem().equals(id))
                .findFirst()
                .orElse(null);
    }
}