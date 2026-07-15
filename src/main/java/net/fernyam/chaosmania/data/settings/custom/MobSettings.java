package net.fernyam.chaosmania.data.settings.custom;

import net.fernyam.chaosmania.data.settings.BaseSettings;

import java.util.ArrayList;
import java.util.List;

public class MobSettings extends BaseSettings {
    public static class MobEntry {
        private String idMob;
        private boolean canRightClick = true;
        private boolean canLeftClick = true;

        public MobEntry() {}

        public MobEntry(String idMob) {
            this.idMob = idMob;
        }

        public MobEntry(String idMob, boolean canRightClick, boolean canLeftClick) {
            this.idMob = idMob;
            this.canRightClick = canRightClick;
            this.canLeftClick = canLeftClick;
        }

        public String getIdMob() { return idMob; }
        public void setIdMob(String idMob) { this.idMob = idMob; }

        public boolean canRightClick() { return canRightClick; }
        public void setCanRightClick(boolean canRightClick) { this.canRightClick = canRightClick; }
        public void toggleRightClick() { this.canRightClick = !this.canRightClick; }

        public boolean canLeftClick() { return canLeftClick; }
        public void setCanLeftClick(boolean canLeftClick) { this.canLeftClick = canLeftClick; }
        public void toggleLeftClick() { this.canLeftClick = !this.canLeftClick; }
    }

    public MobSettings() {}

    public MobSettings(String uuid, String name) {
        this.uuidPlayer = uuid;
        this.namePlayer = name;
    }

    private boolean mobRightClickControlEnabled = false;
    private boolean mobLeftClickControlEnabled = false;
    private ListType listTypeRightClick = ListType.BLACK_LIST;
    private ListType listTypeLeftClick = ListType.BLACK_LIST;
    private List<MobEntry> mobs = new ArrayList<>();

    // Геттеры и сеттеры для RightClick
    public boolean isMobRightClickControlEnabled() { return mobRightClickControlEnabled; }
    public void setMobRightClickControlEnabled(boolean mobRightClickControlEnabled) { this.mobRightClickControlEnabled = mobRightClickControlEnabled; }
    public void toggleGlobalRightClick() { this.mobRightClickControlEnabled = !this.mobRightClickControlEnabled; }

    public ListType getListTypeRightClick() { return listTypeRightClick; }
    public void setListTypeRightClick(ListType listTypeRightClick) { this.listTypeRightClick = listTypeRightClick; }
    public void toggleListTypeRightClick() {
        this.listTypeRightClick = (this.listTypeRightClick == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    // Геттеры и сеттеры для LeftClick
    public boolean isMobLeftClickControlEnabled() { return mobLeftClickControlEnabled; }
    public void setMobLeftClickControlEnabled(boolean mobLeftClickControlEnabled) { this.mobLeftClickControlEnabled = mobLeftClickControlEnabled; }
    public void toggleGlobalLeftClick() { this.mobLeftClickControlEnabled = !this.mobLeftClickControlEnabled; }

    public ListType getListTypeLeftClick() { return listTypeLeftClick; }
    public void setListTypeLeftClick(ListType listTypeLeftClick) { this.listTypeLeftClick = listTypeLeftClick; }
    public void toggleListTypeLeftClick() {
        this.listTypeLeftClick = (this.listTypeLeftClick == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public List<MobEntry> getMobs() { return mobs; }
    public void setMobs(List<MobEntry> mobs) { this.mobs = mobs; }

    // Методы работы с мобами
    public MobEntry getOrCreateMob(String id) {
        for (MobEntry entry : mobs) {
            if (entry.getIdMob().equals(id)) {
                return entry;
            }
        }
        MobEntry newEntry = new MobEntry(id);
        mobs.add(newEntry);
        return newEntry;
    }

    public void addMob(String id) {
        if (!isMobExists(id)) {
            mobs.add(new MobEntry(id));
        }
    }

    public boolean isMobExists(String id) {
        return mobs.stream().anyMatch(e -> e.getIdMob().equals(id));
    }

    public void removeMob(String id) {
        mobs.removeIf(e -> e.getIdMob().equals(id));
    }

    public void toggleMobRightClick(String id) {
        getOrCreateMob(id).toggleRightClick();
    }

    public void toggleMobLeftClick(String id) {
        getOrCreateMob(id).toggleLeftClick();
    }

    public void setMobRightClick(String id, boolean canRightClick) {
        getOrCreateMob(id).setCanRightClick(canRightClick);
    }

    public void setMobLeftClick(String id, boolean canLeftClick) {
        getOrCreateMob(id).setCanLeftClick(canLeftClick);
    }

    public boolean canRightClickMob(String id) {
        MobEntry entry = getMobEntry(id);
        boolean isInList = entry != null;
        boolean canRightClick = isInList && entry.canRightClick();

        if (listTypeRightClick == ListType.BLACK_LIST) {
            return !isInList || canRightClick;
        } else {
            return isInList && canRightClick;
        }
    }

    public boolean canLeftClickMob(String id) {
        MobEntry entry = getMobEntry(id);
        boolean isInList = entry != null;
        boolean canLeftClick = isInList && entry.canLeftClick();

        if (listTypeLeftClick == ListType.BLACK_LIST) {
            return !isInList || canLeftClick;
        } else {
            return isInList && canLeftClick;
        }
    }

    private MobEntry getMobEntry(String id) {
        return mobs.stream()
                .filter(e -> e.getIdMob().equals(id))
                .findFirst()
                .orElse(null);
    }
}