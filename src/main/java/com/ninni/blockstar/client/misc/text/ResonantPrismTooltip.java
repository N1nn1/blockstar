package com.ninni.blockstar.client.misc.text;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResonantPrismTooltip implements ClientTooltipComponent {
    private final ResourceLocation icon;

    public ResonantPrismTooltip(ResonantPrismItem.SoundfontTooltip tooltip) {
        this.icon = tooltip.icon();
    }

    @Override
    public int getHeight() {
        return 24;
    }

    @Override
    public int getWidth(Font p_169952_) {
        return 24;
    }

    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        ResourceLocation resourceLocation = new ResourceLocation(icon.getNamespace(), "textures/soundfont/" + icon.getPath() + ".png");

        if (icon.getPath().startsWith("note_block_")) {
            ResourceLocation noteBlockResourceLocation = new ResourceLocation(Blockstar.MODID, "textures/soundfont/note_block.png");
            graphics.blit(noteBlockResourceLocation, x, y - 2, 0, 0, this.getWidth(font), this.getHeight(), this.getWidth(font), this.getHeight());
            graphics.blit(resourceLocation, x + 28, y - 2, 0, 0, this.getWidth(font), this.getHeight(), this.getWidth(font), this.getHeight());
        } else {
            graphics.blit(resourceLocation, x, y - 2, 0, 0, this.getWidth(font), this.getHeight(), this.getWidth(font), this.getHeight());
        }
    }
}