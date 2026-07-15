package net.fernyam.chaosmania.data.settings.custom;

import net.fernyam.chaosmania.data.settings.BaseSettings;

import java.util.ArrayList;
import java.util.List;

public class BlockSettings extends BaseSettings {
    public static class BlockEntry {
        private String idBlock;
        private boolean canPlace = true;
        private boolean canBreak = true;

        private boolean canRightClick = true;
        private boolean canLeftClick = true;

        public BlockEntry() {}

        public BlockEntry(String idBlock) {
            this.idBlock = idBlock;
        }

        public BlockEntry(String idBlock, boolean canPlace, boolean canBreak , boolean canRightClick , boolean canLeftClick) {
            this.idBlock = idBlock;
            this.canPlace = canPlace;
            this.canBreak = canBreak;
            this.canRightClick = canRightClick;
            this.canLeftClick = canLeftClick;
        }

        public String getIdBlock() { return idBlock; }
        public void setIdBlock(String idBlock) { this.idBlock = idBlock; }

        public boolean canPlace() { return canPlace; }
        public void setCanPlace(boolean canPlace) { this.canPlace = canPlace; }

        public boolean canRightClick() { return canRightClick; }
        public void setCanRightClick(boolean canRightClick) { this.canRightClick = canRightClick; }

        public boolean canLeftClick() { return canLeftClick; }
        public void setCanLeftClick(boolean canLeftClick) { this.canLeftClick = canLeftClick; }

        public boolean canBreak() { return canBreak; }
        public void setCanBreak(boolean canBreak) { this.canBreak = canBreak; }

        public void togglePlace() { this.canPlace = !this.canPlace; }
        public void toggleBreak() { this.canBreak = !this.canBreak; }
        public void toggleRightClick() { this.canRightClick = !this.canRightClick; }
        public void toggleLeftClick() { this.canLeftClick = !this.canLeftClick; }

    }

    public BlockSettings() {}

    public BlockSettings(String uuid , String name)
    {
        this.uuidPlayer = uuid;
        this.namePlayer = name;
    }

    private boolean blockPlaceControlEnabled = false;
    private boolean blockBreakControlEnabled = false;
    private boolean blockRightClickControlEnabled = false;
    private boolean blockLeftClickControlEnabled = false;
    private ListType listTypePlace = ListType.BLACK_LIST;
    private ListType listTypeBreak = ListType.BLACK_LIST;
    private ListType listTypeRightClick = ListType.BLACK_LIST;
    private ListType listTypeLeftClick = ListType.BLACK_LIST;
    private List<BlockEntry> blocks = new ArrayList<>();


    // Геттеры и сеттеры
    public boolean isBlockPlaceControlEnabled() { return blockPlaceControlEnabled; }
    public void setBlockPlaceControlEnabled(boolean blockPlaceControlEnabled) { this.blockPlaceControlEnabled = blockPlaceControlEnabled; }
    public void toggleGlobalPlaceBlock() { this.blockPlaceControlEnabled = !this.blockPlaceControlEnabled; }

    public boolean isBlockBreakControlEnabled() { return blockBreakControlEnabled; }
    public void setBlockBreakControlEnabled(boolean blockBreakControlEnabled) { this.blockBreakControlEnabled = blockBreakControlEnabled; }
    public void toggleGlobalBreakBlock() { this.blockBreakControlEnabled = !this.blockBreakControlEnabled; }

    public boolean isBlockRightClickControlEnabled() { return blockRightClickControlEnabled; }
    public void setBlockRightClickControlEnabled(boolean blockRightClickControlEnabled) { this.blockRightClickControlEnabled = blockRightClickControlEnabled; }
    public void toggleGlobalRightClickBlock() { this.blockRightClickControlEnabled = !this.blockRightClickControlEnabled; }

    public boolean isBlockLeftClickControlEnabled() { return blockLeftClickControlEnabled; }
    public void setBlockLeftClickControlEnabled(boolean blockLeftClickControlEnabled) { this.blockLeftClickControlEnabled = blockLeftClickControlEnabled; }
    public void toggleGlobalLeftClickBlock() { this.blockLeftClickControlEnabled = !this.blockLeftClickControlEnabled; }

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

    public ListType getListTypeRightClick() { return listTypeRightClick; }
    public void setListTypeRightClick(ListType listTypeRightClick) { this.listTypeRightClick = listTypeRightClick; }
    public void toggleListTypeRightClick() {
        this.listTypeRightClick = (this.listTypeRightClick == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
    }

    public ListType getListTypeLeftClick() { return listTypeLeftClick; }
    public void setListTypeLeftClick(ListType listTypeLeftClick) { this.listTypeLeftClick = listTypeLeftClick; }
    public void toggleListTypeLeftClick() {
        this.listTypeLeftClick = (this.listTypeLeftClick == ListType.BLACK_LIST) ? ListType.WHITE_LIST : ListType.BLACK_LIST;
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

    // Сеттеры для отдельных действий
    public void setBlockPlace(String id, boolean canPlace) {
        getOrCreateBlock(id).setCanPlace(canPlace);
    }

    public void setBlockBreak(String id, boolean canBreak) {
        getOrCreateBlock(id).setCanBreak(canBreak);
    }

    public void setBlockRightClick(String id, boolean canRightClick) {
        getOrCreateBlock(id).setCanRightClick(canRightClick);
    }

    public void setBlockLeftClick(String id, boolean canLeftClick) {
        getOrCreateBlock(id).setCanLeftClick(canLeftClick);
    }

    // Тогглы для отдельных действий
    public void toggleBlockPlace(String id) {
        getOrCreateBlock(id).togglePlace();
    }

    public void toggleBlockBreak(String id) {
        getOrCreateBlock(id).toggleBreak();
    }

    public void toggleBlockRightClick(String id) {
        getOrCreateBlock(id).toggleRightClick();
    }

    public void toggleBlockLeftClick(String id) {
        getOrCreateBlock(id).toggleLeftClick();
    }

    // Проверки для каждого действия
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

    public boolean canRightClickBlock(String id) {

        BlockEntry entry = getBlockEntry(id);
        boolean isInList = entry != null;
        boolean canRightClick = isInList && entry.canRightClick();

        if (listTypeRightClick == ListType.BLACK_LIST) {
            return !isInList || canRightClick;
        } else {
            return isInList && canRightClick;
        }
    }

    public boolean canLeftClickBlock(String id) {

        BlockEntry entry = getBlockEntry(id);
        boolean isInList = entry != null;
        boolean canLeftClick = isInList && entry.canLeftClick();

        if (listTypeLeftClick == ListType.BLACK_LIST) {
            return !isInList || canLeftClick;
        } else {
            return isInList && canLeftClick;
        }
    }

    private BlockEntry getBlockEntry(String id) {
        return blocks.stream()
                .filter(e -> e.getIdBlock().equals(id))
                .findFirst()
                .orElse(null);
    }
}