package net.fernyam.chaosmania.gui.custom.base;

import net.fernyam.chaosmania.gui.custom.MainSettingScreen;
import net.minecraft.client.gui.components.ObjectSelectionList;

import java.util.List;
import java.util.Objects;

public abstract class BaseScrollingList<T, E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
    protected final MainSettingScreen parent;
    protected static final int SLOT_HEIGHT = 55;

    public BaseScrollingList(MainSettingScreen parent, int x, int y, int width, int height) {
        super(Objects.requireNonNull(parent.getMinecraft()), width, height, y, SLOT_HEIGHT);
        this.parent = parent;
        this.setX(x);
    }

    public BaseScrollingList(MainSettingScreen parent, int x, int y, int width, int height, int slotHeight) {
        super(Objects.requireNonNull(parent.getMinecraft()), width, height, y, slotHeight);
        this.parent = parent;
        this.setX(x);
    }


    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getX() + this.width - 6;
    }

    public abstract void updateEntries(List<T> entries);
}