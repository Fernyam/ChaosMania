package net.fernyam.chaosmania.data.settings.custom;

import net.fernyam.chaosmania.data.settings.BaseSettings;

import java.util.ArrayList;
import java.util.List;

public class ModSettings extends BaseSettings {
    public static class ModEntry {
        private String idMod;
        private boolean canLoad = true;

        public ModEntry() {}

        public ModEntry(String idMod) {
            this.idMod = idMod;
        }

        public ModEntry(String idMod, boolean canLoad) {
            this.idMod = idMod;
            this.canLoad = canLoad;
        }

        public String getIdMod() { return idMod; }
        public void setIdMod(String idMod) { this.idMod = idMod; }

        public boolean canLoad() { return canLoad; }
        public void setCanLoad(boolean canLoad) { this.canLoad = canLoad; }

        public void toggleLoad() { this.canLoad = !this.canLoad; }
    }

    public ModSettings() {}

    public ModSettings(String uuid, String name) {
        this.uuidPlayer = uuid;
        this.namePlayer = name;
    }

    private boolean modLoadControlEnabled = false;
    private ListType listType = ListType.BLACK_LIST;
    private List<ModEntry> mods = new ArrayList<>();


    // Геттеры и сеттеры
    public boolean isModLoadControlEnabled() { return modLoadControlEnabled; }
    public void setModLoadControlEnabled(boolean modLoadControlEnabled) { this.modLoadControlEnabled = modLoadControlEnabled; }
    public void toggleGlobalLoadMod() { this.modLoadControlEnabled = !this.modLoadControlEnabled; }

    public ListType getListType() { return listType; }
    public void setListType(ListType listType) { this.listType = listType; }
    public void toggleListType() {
        this.listType = (this.listType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public List<ModEntry> getMods() { return mods; }
    public void setMods(List<ModEntry> mods) { this.mods = mods; }

    // Методы работы с модами
    public ModEntry getOrCreateMod(String id) {
        for (ModEntry entry : mods) {
            if (entry.getIdMod().equals(id)) {
                return entry;
            }
        }
        ModEntry newEntry = new ModEntry(id);
        mods.add(newEntry);
        return newEntry;
    }

    public void addMod(String id) {
        if (!isModExists(id)) {
            mods.add(new ModEntry(id));
        }
    }

    public boolean isModExists(String id) {
        return mods.stream().anyMatch(e -> e.getIdMod().equals(id));
    }

    public void removeMod(String id) {
        mods.removeIf(e -> e.getIdMod().equals(id));
    }

    public void toggleModLoad(String id) {
        getOrCreateMod(id).toggleLoad();
    }

    public void setModLoad(String id, boolean canLoad) {
        getOrCreateMod(id).setCanLoad(canLoad);
    }

    public boolean canLoadMod(String id) {

        ModEntry entry = getModEntry(id);
        boolean isInList = entry != null;
        boolean canLoad = isInList && entry.canLoad();

        if (listType == ListType.BLACK_LIST) {
            return !isInList || canLoad;
        } else {
            return isInList && canLoad;
        }
    }

    private ModEntry getModEntry(String id) {
        return mods.stream()
                .filter(e -> e.getIdMod().equals(id))
                .findFirst()
                .orElse(null);
    }
}