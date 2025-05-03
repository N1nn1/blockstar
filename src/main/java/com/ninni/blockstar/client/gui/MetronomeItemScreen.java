package com.ninni.blockstar.client.gui;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.gui.components.CenteredEditBox;
import com.ninni.blockstar.registry.BNetwork;
import com.ninni.blockstar.server.item.MetronomeItem;
import com.ninni.blockstar.server.item.MetronomeItem;
import com.ninni.blockstar.server.packet.MetronomeUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

public class MetronomeItemScreen extends Screen {
    public static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(Blockstar.MODID, "textures/gui/metronome/bg.png");
    private final ItemStack metronome;
    private CenteredEditBox bpmField;
    private CenteredEditBox timeSigField;
    Font font = Minecraft.getInstance().font;

    public MetronomeItemScreen(ItemStack metronome) {
        super(Component.empty());
        this.metronome = metronome;
    }

    @Override
    protected void init() {
        super.init();

        int i = (this.width - 134) / 2;
        int j = (this.height - 54) / 2;

        this.bpmField = new CenteredEditBox(this.font, i + 29, j + 21, 28, 14, Component.empty());
        this.bpmField.setValue(String.valueOf(MetronomeItem.getBPM(metronome)));
        this.bpmField.setFilter(s -> s.matches("\\d{0,3}"));
        this.bpmField.setTextColor(0xffffff);
        this.bpmField.setTextColorUneditable(0xffffff);
        this.bpmField.setBordered(false);
        this.addRenderableWidget(this.bpmField);

        this.timeSigField = new CenteredEditBox(this.font, i + 93, j + 21, 28, 14, Component.empty());
        this.timeSigField.setValue(MetronomeItem.getTimeSig(metronome));
        this.timeSigField.setFilter(s -> s.matches("^\\d{0,2}(/\\d{0,2})?$"));
        this.timeSigField.setTextColor(0xffffff);
        this.timeSigField.setTextColorUneditable(0xffffff);
        this.timeSigField.setBordered(false);
        this.addRenderableWidget(this.timeSigField);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x12000000, 0x68000000);
        MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(this, guiGraphics));

        int i = (this.width - 134) / 2;
        int j = (this.height - 54) / 2;

        guiGraphics.blit(TEXTURE_WIDGETS, i, j, 0, 0, 134, 54);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (bpmField != null) bpmField.tick();
        if (timeSigField != null) {
            timeSigField.tick();
            timeSigField.setTextColor(MetronomeItem.isTimeSigValid(timeSigField.getValue()) ? 0xffffff : 0xc94f4f);
        }
    }

    @Override
    public void onClose() {
        int bpm = Integer.parseInt(bpmField.getValue());
        String timeSig = timeSigField.getValue();
        if (!MetronomeItem.isTimeSigValid(timeSig)) timeSig = "4/4";
        this.minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F, 1);
        BNetwork.INSTANCE.sendToServer(new MetronomeUpdatePacket(bpm, timeSig));

        super.onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (bpmField != null && bpmField.mouseClicked(mouseX, mouseY, button)) {
            setFocused(bpmField);
            return true;
        }
        if (timeSigField != null && timeSigField.mouseClicked(mouseX, mouseY, button)) {
            setFocused(timeSigField);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bpmField != null && bpmField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (timeSigField != null && timeSigField.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (bpmField != null && bpmField.charTyped(codePoint, modifiers)) return true;
        if (timeSigField != null && timeSigField.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }
}
