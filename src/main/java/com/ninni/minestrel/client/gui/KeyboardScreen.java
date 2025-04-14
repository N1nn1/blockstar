package com.ninni.minestrel.client.gui;

import com.ninni.minestrel.Minestrel;
import com.ninni.minestrel.server.inventory.KeyboardMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KeyboardScreen extends AbstractContainerScreen<KeyboardMenu> {
    public static final ResourceLocation TEXTURE_BG = new ResourceLocation(Minestrel.MODID, "textures/gui/keyboard/bg.png");
    public static final ResourceLocation TEXTURE_BG_SHEET_MUSIC = new ResourceLocation(Minestrel.MODID, "textures/gui/keyboard/bg_sheet_music.png");
    public static final ResourceLocation TEXTURE_PAUSE = new ResourceLocation(Minestrel.MODID, "textures/gui/keyboard/pause.png");
    public static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(Minestrel.MODID, "textures/gui/keyboard/widgets.png");

    public KeyboardScreen(KeyboardMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        menu.registerUpdateListener(this::containerChanged);
        this.imageWidth = 183;
        this.imageHeight = 254;
        this.inventoryLabelY = 161;
        this.inventoryLabelX = 12;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        int i = this.leftPos;
        int j = this.topPos;

        if (this.menu.getSheetMusicSlot().hasItem()) guiGraphics.blit(TEXTURE_BG_SHEET_MUSIC, i, j, 0, 0, this.imageWidth, this.imageHeight);
        else guiGraphics.blit(TEXTURE_BG, i, j, 0, 0, this.imageWidth, this.imageHeight);

        if (!this.menu.getInstrumentSlot().hasItem()) guiGraphics.blit(TEXTURE_WIDGETS, i + 7, j + 43, 32, 32, 16, 16);
        if (!this.menu.getSheetMusicSlot().hasItem()) guiGraphics.blit(TEXTURE_WIDGETS, i + 160, j + 43, 48, 32, 16, 16);
    }

    private void containerChanged() {}
}
