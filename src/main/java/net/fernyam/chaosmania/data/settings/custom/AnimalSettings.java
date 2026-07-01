package net.fernyam.chaosmania.data.settings.custom;

import net.fernyam.chaosmania.data.settings.BaseSettings;

import java.util.ArrayList;
import java.util.List;

public class AnimalSettings extends BaseSettings {
    public static class AnimalEntry {
        private String idAnimal;
        private boolean canBreed = true;
        private boolean canSpawn = true;

        public AnimalEntry() {}

        public AnimalEntry(String idAnimal) {
            this.idAnimal = idAnimal;
        }

        public AnimalEntry(String idAnimal, boolean canBreed, boolean canSpawn) {
            this.idAnimal = idAnimal;
            this.canBreed = canBreed;
            this.canSpawn = canSpawn;
        }

        public String getIdAnimal() { return idAnimal; }
        public void setIdAnimal(String idAnimal) { this.idAnimal = idAnimal; }

        public boolean canBreed() { return canBreed; }
        public void setCanBreed(boolean canBreed) { this.canBreed = canBreed; }

        public boolean canSpawn() { return canSpawn; }
        public void setCanSpawn(boolean canSpawn) { this.canSpawn = canSpawn; }

        public void toggleBreed() { this.canBreed = !this.canBreed; }
        public void toggleSpawn() { this.canSpawn = !this.canSpawn; }
    }

    public AnimalSettings() {}

    public AnimalSettings(String uuid, String name) {
        this.uuidPlayer = uuid;
        this.namePlayer = name;
    }

    private boolean animalBreedControlEnabled = false;
    private boolean animalSpawnControlEnabled = false;
    private ListType listTypeBreed = ListType.BLACK_LIST;
    private ListType listTypeSpawn = ListType.BLACK_LIST;
    private List<AnimalEntry> animals = new ArrayList<>();

    // Геттеры и сеттеры
    public boolean isAnimalBreedControlEnabled() { return animalBreedControlEnabled; }
    public void setAnimalBreedControlEnabled(boolean animalBreedControlEnabled) { this.animalBreedControlEnabled = animalBreedControlEnabled; }
    public void toggleGlobalBreedAnimal() { this.animalBreedControlEnabled = !this.animalBreedControlEnabled; }

    public boolean isAnimalSpawnControlEnabled() { return animalSpawnControlEnabled; }
    public void setAnimalSpawnControlEnabled(boolean animalSpawnControlEnabled) { this.animalSpawnControlEnabled = animalSpawnControlEnabled; }
    public void toggleGlobalSpawnAnimal() { this.animalSpawnControlEnabled = !this.animalSpawnControlEnabled; }

    public ListType getListTypeBreed() { return listTypeBreed; }
    public void setListTypeBreed(ListType listTypeBreed) { this.listTypeBreed = listTypeBreed; }
    public void toggleListTypeBreed() {
        this.listTypeBreed = (this.listTypeBreed == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public ListType getListTypeSpawn() { return listTypeSpawn; }
    public void setListTypeSpawn(ListType listTypeSpawn) { this.listTypeSpawn = listTypeSpawn; }
    public void toggleListTypeSpawn() {
        this.listTypeSpawn = (this.listTypeSpawn == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public List<AnimalEntry> getAnimals() { return animals; }
    public void setAnimals(List<AnimalEntry> animals) { this.animals = animals; }

    // Методы работы с животными
    public AnimalEntry getOrCreateAnimal(String id) {
        for (AnimalEntry entry : animals) {
            if (entry.getIdAnimal().equals(id)) {
                return entry;
            }
        }
        AnimalEntry newEntry = new AnimalEntry(id);
        animals.add(newEntry);
        return newEntry;
    }

    public void addAnimal(String id) {
        if (!isAnimalExists(id)) {
            animals.add(new AnimalEntry(id));
        }
    }

    public boolean isAnimalExists(String id) {
        return animals.stream().anyMatch(e -> e.getIdAnimal().equals(id));
    }

    public void removeAnimal(String id) {
        animals.removeIf(e -> e.getIdAnimal().equals(id));
    }

    public void toggleAnimalBreed(String id) {
        getOrCreateAnimal(id).toggleBreed();
    }

    public void toggleAnimalSpawn(String id) {
        getOrCreateAnimal(id).toggleSpawn();
    }

    public void setAnimalBreed(String id, boolean canBreed) {
        getOrCreateAnimal(id).setCanBreed(canBreed);
    }

    public void setAnimalSpawn(String id, boolean canSpawn) {
        getOrCreateAnimal(id).setCanSpawn(canSpawn);
    }

    public boolean canBreedAnimal(String id) {

        AnimalEntry entry = getAnimalEntry(id);
        boolean isInList = entry != null;
        boolean canBreed = isInList && entry.canBreed();

        if (listTypeBreed == ListType.BLACK_LIST) {
            return !isInList || canBreed;
        } else {
            return isInList && canBreed;
        }
    }

    public boolean canSpawnAnimal(String id) {

        AnimalEntry entry = getAnimalEntry(id);
        boolean isInList = entry != null;
        boolean canSpawn = isInList && entry.canSpawn();

        if (listTypeSpawn == ListType.BLACK_LIST) {
            return !isInList || canSpawn;
        } else {
            return isInList && canSpawn;
        }
    }

    private AnimalEntry getAnimalEntry(String id) {
        return animals.stream()
                .filter(e -> e.getIdAnimal().equals(id))
                .findFirst()
                .orElse(null);
    }
}