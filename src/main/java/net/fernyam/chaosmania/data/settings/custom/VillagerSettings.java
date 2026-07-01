package net.fernyam.chaosmania.data.settings.custom;

import net.fernyam.chaosmania.data.settings.BaseSettings;

import java.util.ArrayList;
import java.util.List;

public class VillagerSettings extends BaseSettings {
    public static class VillagerEntry {
        private String idProfession;
        private boolean canTrade = true;

        public VillagerEntry() {}

        public VillagerEntry(String idProfession) {
            this.idProfession = idProfession;
        }

        public VillagerEntry(String idProfession, boolean canTrade) {
            this.idProfession = idProfession;
            this.canTrade = canTrade;
        }

        public String getIdProfession() { return idProfession; }
        public void setIdProfession(String idProfession) { this.idProfession = idProfession; }

        public boolean canTrade() { return canTrade; }
        public void setCanTrade(boolean canTrade) { this.canTrade = canTrade; }

        public void toggleTrade() { this.canTrade = !this.canTrade; }
    }

    public VillagerSettings() {}

    public VillagerSettings(String uuid, String name) {
        this.uuidPlayer = uuid;
        this.namePlayer = name;
    }


    private boolean villagerTradeControlEnabled = false;
    private boolean wanderingTraderControlEnabled = false;
    private ListType listType = ListType.BLACK_LIST;
    private List<VillagerEntry> professions = new ArrayList<>();


    // Геттеры и сеттеры
    public boolean isVillagerTradeControlEnabled() { return villagerTradeControlEnabled; }
    public void setVillagerTradeControlEnabled(boolean villagerTradeControlEnabled) { this.villagerTradeControlEnabled = villagerTradeControlEnabled; }
    public void toggleGlobalTradeVillager() { this.villagerTradeControlEnabled = !this.villagerTradeControlEnabled; }

    public boolean isWanderingTraderControlEnabled() { return wanderingTraderControlEnabled; }
    public void setWanderingTraderControlEnabled(boolean wanderingTraderControlEnabled) { this.wanderingTraderControlEnabled = wanderingTraderControlEnabled; }
    public void toggleGlobalTradeWanderingTrader() { this.wanderingTraderControlEnabled = !this.wanderingTraderControlEnabled; }

    public ListType getListType() { return listType; }
    public void setListType(ListType listType) { this.listType = listType; }
    public void toggleListType() {
        this.listType = (this.listType == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public List<VillagerEntry> getProfessions() { return professions; }
    public void setProfessions(List<VillagerEntry> professions) { this.professions = professions; }

    // Методы работы с жителями
    public VillagerEntry getOrCreateProfession(String id) {
        for (VillagerEntry entry : professions) {
            if (entry.getIdProfession().equals(id)) {
                return entry;
            }
        }
        VillagerEntry newEntry = new VillagerEntry(id);
        professions.add(newEntry);
        return newEntry;
    }

    public void addProfession(String id) {
        if (!isProfessionExists(id)) {
            professions.add(new VillagerEntry(id));
        }
    }

    public boolean isProfessionExists(String id) {
        return professions.stream().anyMatch(e -> e.getIdProfession().equals(id));
    }

    public void removeProfession(String id) {
        professions.removeIf(e -> e.getIdProfession().equals(id));
    }

    public void toggleProfessionTrade(String id) {
        getOrCreateProfession(id).toggleTrade();
    }

    public void setProfessionTrade(String id, boolean canTrade) {
        getOrCreateProfession(id).setCanTrade(canTrade);
    }

    public boolean canTradeWithVillager(String id) {

        VillagerEntry entry = getProfessionEntry(id);
        boolean isInList = entry != null;
        boolean canTrade = isInList && entry.canTrade();

        if (listType == ListType.BLACK_LIST) {
            return !isInList || canTrade;
        } else {
            return isInList && canTrade;
        }
    }

    public boolean canTradeWithWanderingTrader() {
        return !wanderingTraderControlEnabled;
    }

    private VillagerEntry getProfessionEntry(String id) {
        return professions.stream()
                .filter(e -> e.getIdProfession().equals(id))
                .findFirst()
                .orElse(null);
    }
}