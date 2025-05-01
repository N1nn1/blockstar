package com.ninni.blockstar.client.gui;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ComposingTableScreen extends AbstractContainerScreen<ComposingTableMenu> {
    public static final ResourceLocation TEXTURE_BG = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/bg.png");
    public static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/widgets.png");
    public static final ResourceLocation TEXTURE_PAPER = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/paper.png");

    public ComposingTableScreen(ComposingTableMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 176;
        this.imageHeight = 254;
        this.inventoryLabelY = 161;
        this.inventoryLabelX = 12;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        int i = this.leftPos;
        int j = this.topPos;

        guiGraphics.blit(TEXTURE_BG, i, j, 0, 0, this.imageWidth, this.imageHeight);

        if (!this.menu.getInkSlot().hasItem()) guiGraphics.blit(TEXTURE_WIDGETS, i + 152, j + 29, 208, 0, 16, 16);

        if (!this.menu.getSheetMusicSlot().hasItem()) guiGraphics.blit(TEXTURE_WIDGETS, i + 8, j + 29, 224, 0, 16, 16);
        else {
            guiGraphics.blit(TEXTURE_PAPER, i + 8, j + 49, 0, 0, 139, 116);
        }

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
}
