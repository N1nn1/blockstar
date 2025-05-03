package com.ninni.blockstar.client.gui;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.gui.components.CenteredEditBox;
import com.ninni.blockstar.registry.BNetwork;
import com.ninni.blockstar.server.item.MetronomeItem;
import com.ninni.blockstar.server.item.SheetMusicItem;
import com.ninni.blockstar.server.packet.SheetSettingsUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class SheetSettingsScreen extends Screen {
    public static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/widgets.png");
    private Screen parent;
    private final ItemStack sheet;
    private CenteredEditBox bpmField;
    private CenteredEditBox timeSigField;
    Font font = Minecraft.getInstance().font;
    private int selectedKeyIndex = 0;
    private int hoveredKeyIndex = 0;
    private boolean minor;
    private static final String[] MAJOR_KEYS = {
            "C", "G", "D", "A", "E", "B", "F♯", "D♭", "A♭", "E♭", "B♭", "F"
    };
    private static final String[] MINOR_KEYS = {
            "Am", "Em", "Bm", "F♯m", "C♯m", "G♯m", "D♯m", "B♭m", "Fm", "Cm", "Gm", "Dm"
    };
    private static final int[][] KEY_POSITIONS = {
            {31, 7}, {47, 7}, {63, 15}, {71, 31}, {71, 47}, {63, 63},
            {47, 71}, {31, 71}, {15, 63}, {7, 47}, {7, 31}, {15, 15}
    };


    public SheetSettingsScreen(Component component, Screen parent, ItemStack sheet) {
        super(component);
        this.parent = parent;
        this.sheet = sheet;
    }

    @Override
    protected void init() {
        super.init();

        int i = (this.width - 156) / 2;
        int j = (this.height - 94) / 2;

        this.bpmField = new CenteredEditBox(this.font, i + 109, j + 19, 28, 14, Component.empty());
        this.bpmField.setValue(String.valueOf(SheetMusicItem.getBPM(sheet)));
        this.bpmField.setFilter(s -> s.matches("\\d{0,3}"));
        this.bpmField.setTextColor(0x72a078);
        this.bpmField.setTextColor(0xffffff);
        this.bpmField.setTextColorUneditable(0xffffff);
        this.bpmField.setBordered(false);
        this.addRenderableWidget(this.bpmField);

        this.timeSigField = new CenteredEditBox(this.font, i + 109, j + 63, 28, 14, Component.empty());
        this.timeSigField.setValue(SheetMusicItem.getTimeSig(sheet));
        this.timeSigField.setFilter(s -> s.matches("^\\d{0,2}(/\\d{0,2})?$"));
        this.timeSigField.setTextColor(0xffffff);
        this.timeSigField.setTextColorUneditable(0xffffff);
        this.timeSigField.setBordered(false);
        this.addRenderableWidget(this.timeSigField);

        String currentKey = SheetMusicItem.getKey(sheet);
        for (int k = 0; k < 12; k++) {
            if (MAJOR_KEYS[k].equals(currentKey)) {
                selectedKeyIndex = k;
                break;
            }
            if (MINOR_KEYS[k].equals(currentKey)) {
                selectedKeyIndex = k;
                break;
            }
        }
        hoveredKeyIndex = -1;
        minor = SheetMusicItem.isKeyMinor(sheet);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        this.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {

        int i = (this.width - 156) / 2;
        int j = (this.height - 94) / 2;

        guiGraphics.blit(TEXTURE_WIDGETS, i, j, 0, 0, 156, 94);

        for (int k = 0; k < 12; k++) {
            int x = i + KEY_POSITIONS[k][0];
            int y = j + KEY_POSITIONS[k][1];
            int v = minor ? 170 + getStateForKey(k) * 16 : 122 + getStateForKey(k) * 16;
            int u = k * 16;

            guiGraphics.blit(TEXTURE_WIDGETS, x, y, u, v, 16, 16);
        }

        int majButtonX = i + 32;
        int majButtonY = j + 40;
        boolean hovered = mouseX >= majButtonX && mouseX < majButtonX + 30 && mouseY >= majButtonY && mouseY < majButtonY + 14;
        guiGraphics.blit(TEXTURE_WIDGETS, majButtonX, majButtonY, 192, (minor ? 170 : 122) + (hovered ? 16 : 0), 30, 14);

        if (minor) {
            guiGraphics.blit(TEXTURE_WIDGETS, i + 37,  j + 27, 224, 186, 4, 8);
            guiGraphics.blit(TEXTURE_WIDGETS, i + 52,  j + 59, 224, 170, 6, 9);
        } else {
            guiGraphics.blit(TEXTURE_WIDGETS, i + 37,  j + 26, 224, 122, 6, 9);
            guiGraphics.blit(TEXTURE_WIDGETS, i + 53,  j + 57, 224, 138, 4, 8);
        }

    }


    private int getStateForKey(int i) {
        if (i == selectedKeyIndex) return 2;
        if (i == hoveredKeyIndex) return 1;
        return 0;
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
        if (!SheetMusicItem.isTimeSigValid(timeSig)) timeSig = "4/4";
        String key = minor ? MINOR_KEYS[selectedKeyIndex] : MAJOR_KEYS[selectedKeyIndex];
        this.minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
        BNetwork.INSTANCE.sendToServer(new SheetSettingsUpdatePacket(bpm, timeSig, key, minor));

        this.minecraft.setScreen(parent);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        int i = (this.width - 156) / 2;
        int j = (this.height - 94) / 2;
        hoveredKeyIndex = -1;

        for (int k = 0; k < 12; k++) {
            int x = i + KEY_POSITIONS[k][0];
            int y = j + KEY_POSITIONS[k][1];
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                hoveredKeyIndex = k;
                break;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        int i = (this.width - 156) / 2;
        int j = (this.height - 94) / 2;

        for (int k = 0; k < 12; k++) {
            int x = i + KEY_POSITIONS[k][0];
            int y = j + KEY_POSITIONS[k][1];

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                selectedKeyIndex = k;
                this.minecraft.player.playSound(SoundEvents.NOTE_BLOCK_PLING.get(), 0.6F, 1.2F);
                return true;
            }
        }

        int majButtonX = i + 32;
        int majButtonY = j + 40;
        if (mouseX >= majButtonX && mouseX < majButtonX + 30 && mouseY >= majButtonY && mouseY < majButtonY + 14) {
            minor = !minor;
            this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.15F, 1.2F);
            return true;
        }

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
    public boolean charTyped(char codePoint, int modifiers) {
        if (bpmField != null && bpmField.charTyped(codePoint, modifiers)) return true;
        if (timeSigField != null && timeSigField.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }
}
