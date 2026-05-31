package net.fernyam.chaosmania.gui.custom.custom2;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static net.fernyam.chaosmania.ChaosManiaMod.MOD_ID;

public class ModScreen extends Screen {

    protected static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/bg.png");


    protected ModScreen(Component title) {
        super(title);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Отключаем стандартный фон
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public Font getFontRender() {
        return getMinecraft().font;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
