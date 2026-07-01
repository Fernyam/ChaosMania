package net.fernyam.chaosmania.data.settings.custom;

import net.fernyam.chaosmania.data.settings.BaseSettings;

import java.util.ArrayList;
import java.util.List;

public class SeedSettings extends BaseSettings {
    public static class SeedEntry {
        private String idSeed;
        private boolean canPlant = true;

        public SeedEntry() {}

        public SeedEntry(String idSeed) {
            this.idSeed = idSeed;
        }

        public SeedEntry(String idSeed, boolean canPlant) {
            this.idSeed = idSeed;
            this.canPlant = canPlant;
        }

        public String getIdSeed() { return idSeed; }
        public void setIdSeed(String idSeed) { this.idSeed = idSeed; }

        public boolean canPlant() { return canPlant; }
        public void setCanPlant(boolean canPlant) { this.canPlant = canPlant; }

        public void togglePlant() { this.canPlant = !this.canPlant; }
    }

    public SeedSettings() {}

    public SeedSettings(String uuid, String name) {
        this.uuidPlayer = uuid;
        this.namePlayer = name;
    }

    private boolean seedPlantControlEnabled = false;
    private ListType listType = ListType.BLACK_LIST;
    private List<SeedEntry> seeds = new ArrayList<>();


    // Геттеры и сеттеры
    public boolean isSeedPlantControlEnabled() { return seedPlantControlEnabled; }
    public void setSeedPlantControlEnabled(boolean seedPlantControlEnabled) { this.seedPlantControlEnabled = seedPlantControlEnabled; }
    public void toggleGlobalPlantSeed() { this.seedPlantControlEnabled = !this.seedPlantControlEnabled; }

    public ListType getListType() { return listType; }
    public void setListType(ListType listType) { this.listType = listType; }
    public void toggleListType() {
        this.listType = (this.listType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public List<SeedEntry> getSeeds() { return seeds; }
    public void setSeeds(List<SeedEntry> seeds) { this.seeds = seeds; }

    // Методы работы с семенами
    public SeedEntry getOrCreateSeed(String id) {
        for (SeedEntry entry : seeds) {
            if (entry.getIdSeed().equals(id)) {
                return entry;
            }
        }
        SeedEntry newEntry = new SeedEntry(id);
        seeds.add(newEntry);
        return newEntry;
    }

    public void addSeed(String id) {
        if (!isSeedExists(id)) {
            seeds.add(new SeedEntry(id));
        }
    }

    public boolean isSeedExists(String id) {
        return seeds.stream().anyMatch(e -> e.getIdSeed().equals(id));
    }

    public void removeSeed(String id) {
        seeds.removeIf(e -> e.getIdSeed().equals(id));
    }

    public void toggleSeedPlant(String id) {
        getOrCreateSeed(id).togglePlant();
    }

    public void setSeedPlant(String id, boolean canPlant) {
        getOrCreateSeed(id).setCanPlant(canPlant);
    }

    public boolean canPlantSeed(String id) {

        SeedEntry entry = getSeedEntry(id);
        boolean isInList = entry != null;
        boolean canPlant = isInList && entry.canPlant();

        if (listType == ListType.BLACK_LIST) {
            return !isInList || canPlant;
        } else {
            return isInList && canPlant;
        }
    }

    private SeedEntry getSeedEntry(String id) {
        return seeds.stream()
                .filter(e -> e.getIdSeed().equals(id))
                .findFirst()
                .orElse(null);
    }
}