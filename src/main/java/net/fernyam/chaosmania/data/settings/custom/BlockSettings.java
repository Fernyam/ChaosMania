package net.fernyam.chaosmania.data.settings.custom;

import net.fernyam.chaosmania.data.settings.BaseSettings;

import java.util.ArrayList;
import java.util.List;

public class BlockSettings extends BaseSettings {
    public static class BlockEntry {
        private String idBlock;
        private boolean canPlace = true;
        private boolean canBreak = true;

        public BlockEntry() {}

        public BlockEntry(String idBlock) {
            this.idBlock = idBlock;
        }

        public BlockEntry(String idBlock, boolean canPlace, boolean canBreak) {
            this.idBlock = idBlock;
            this.canPlace = canPlace;
            this.canBreak = canBreak;
        }

        public String getIdBlock() { return idBlock; }
        public void setIdBlock(String idBlock) { this.idBlock = idBlock; }

        public boolean canPlace() { return canPlace; }
        public void setCanPlace(boolean canPlace) { this.canPlace = canPlace; }

        public boolean canBreak() { return canBreak; }
        public void setCanBreak(boolean canBreak) { this.canBreak = canBreak; }

        public void togglePlace() { this.canPlace = !this.canPlace; }
        public void toggleBreak() { this.canBreak = !this.canBreak; }
    }

    public BlockSettings() {}

    public BlockSettings(String uuid , String name)
    {
        this.uuidPlayer = uuid;
        this.namePlayer = name;
    }

    private boolean blockPlaceControlEnabled = false;
    private boolean blockBreakControlEnabled = false;
    private ListType listTypePlace = ListType.BLACK_LIST;
    private ListType listTypeBreak = ListType.BLACK_LIST;
    private List<BlockEntry> blocks = new ArrayList<>();


    // Геттеры и сеттеры
    public boolean isBlockPlaceControlEnabled() { return blockPlaceControlEnabled; }
    public void setBlockPlaceControlEnabled(boolean blockPlaceControlEnabled) { this.blockPlaceControlEnabled = blockPlaceControlEnabled; }
    public void toggleGlobalPlaceBlock() { this.blockPlaceControlEnabled = !this.blockPlaceControlEnabled; }

    public boolean isBlockBreakControlEnabled() { return blockBreakControlEnabled; }
    public void setBlockBreakControlEnabled(boolean blockBreakControlEnabled) { this.blockBreakControlEnabled = blockBreakControlEnabled; }
    public void toggleGlobalBreakBlock() { this.blockBreakControlEnabled = !this.blockBreakControlEnabled; }

    public ListType getListTypePlace() { return listTypePlace; }
    public void setListTypePlace(ListType listTypePlace) { this.listTypePlace = listTypePlace; }
    public void toggleListTypePlace() {
        this.listTypePlace = (this.listTypePlace == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public ListType getListTypeBreak() { return listTypeBreak; }
    public void setListTypeBreak(ListType listTypeBreak) { this.listTypeBreak = listTypeBreak; }
    public void toggleListTypeBreak() {
        this.listTypeBreak = (this.listTypeBreak == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public List<BlockEntry> getBlocks() { return blocks; }
    public void setBlocks(List<BlockEntry> blocks) { this.blocks = blocks; }

    // Методы работы с блоками
    public BlockEntry getOrCreateBlock(String id) {
        for (BlockEntry entry : blocks) {
            if (entry.getIdBlock().equals(id)) {
                return entry;
            }
        }
        BlockEntry newEntry = new BlockEntry(id);
        blocks.add(newEntry);
        return newEntry;
    }

    public void addBlock(String id) {
        if (!isBlockExists(id)) {
            blocks.add(new BlockEntry(id));
        }
    }

    public void removeBlock(String id) {
        blocks.removeIf(e -> e.getIdBlock().equals(id));
    }

    public boolean isBlockExists(String id) {
        return blocks.stream().anyMatch(e -> e.getIdBlock().equals(id));
    }

    public void setBlockPlace(String id, boolean canPlace) {
        getOrCreateBlock(id).setCanPlace(canPlace);
    }

    public void setBlockBreak(String id, boolean canBreak) {
        getOrCreateBlock(id).setCanBreak(canBreak);
    }

    public void toggleBlockPlace(String id) {
        getOrCreateBlock(id).togglePlace();
    }

    public void toggleBlockBreak(String id) {
        getOrCreateBlock(id).toggleBreak();
    }


    public boolean canPlaceBlock(String id) {

        BlockEntry entry = getBlockEntry(id);
        boolean isInList = entry != null;
        boolean canPlace = isInList && entry.canPlace();

        if (listTypePlace == ListType.BLACK_LIST) {
            return !isInList || canPlace;
        } else {
            return isInList && canPlace;
        }
    }

    public boolean canBreakBlock(String id) {

        BlockEntry entry = getBlockEntry(id);
        boolean isInList = entry != null;
        boolean canBreak = isInList && entry.canBreak();

        if (listTypeBreak == ListType.BLACK_LIST) {
            return !isInList || canBreak;
        } else {
            return isInList && canBreak;
        }
    }

    private BlockEntry getBlockEntry(String id) {
        return blocks.stream()
                .filter(e -> e.getIdBlock().equals(id))
                .findFirst()
                .orElse(null);
    }
}