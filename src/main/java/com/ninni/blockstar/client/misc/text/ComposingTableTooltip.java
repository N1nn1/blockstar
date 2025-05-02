package com.ninni.blockstar.client.misc.text;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.item.ComposingTableItem;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ComposingTableTooltip implements ClientTooltipComponent {
    public static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/widgets.png");
    private final int inkAmount;

    public ComposingTableTooltip(ComposingTableItem.ComposingTableTooltip tooltip) {
        this.inkAmount = tooltip.amount();
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(Font p_169952_) {
        return 20;
    }

    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int inkAmount = (int) (this.inkAmount/12.5);
        guiGraphics.blit(TEXTURE_WIDGETS, x + 1, y - 1, 208, 64, 18, 18);
        guiGraphics.blit(TEXTURE_WIDGETS, x + 2, y + (16 - inkAmount), 192, 64 + (16 - inkAmount), 16, inkAmount);
    }
}