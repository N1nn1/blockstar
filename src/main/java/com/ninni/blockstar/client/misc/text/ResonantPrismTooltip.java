package com.ninni.blockstar.client.misc.text;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.item.ResonantPrismItem;
import net.minecraft.client.Minecraft;
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

        if (icon.getPath().contains("-")) {
            resourceLocation = new ResourceLocation(icon.getNamespace(), "textures/soundfont/" + icon.getPath().split("-")[1] + ".png");
            ResourceLocation noteBlockResourceLocation = new ResourceLocation(Blockstar.MODID, "textures/soundfont/base/" + icon.getPath().split("-")[0] + ".png");
            graphics.blit(noteBlockResourceLocation, x, y - 2, 0, 0, this.getWidth(font), this.getHeight(), this.getWidth(font), this.getHeight());
            x += 24;
        }

        if (Minecraft.getInstance().getResourceManager().getResource(resourceLocation).isEmpty()) {
            resourceLocation = new ResourceLocation(Blockstar.MODID, "textures/soundfont/base/empty.png");
        }

        graphics.blit(resourceLocation, x, y - 2, 0, 0, this.getWidth(font), this.getHeight(), this.getWidth(font), this.getHeight());
    }
}