package com.ninni.blockstar.client.gui;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.gui.components.CenteredEditBox;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.registry.BNetwork;
import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import com.ninni.blockstar.server.item.SheetMusicItem;
import com.ninni.blockstar.server.packet.SheetNoteEditPacket;
import com.ninni.blockstar.server.packet.SheetRenamePacket;
import com.ninni.blockstar.server.sheetmusic.SheetNote;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class ComposingTableScreen extends AbstractContainerScreen<ComposingTableMenu> {
    public static final ResourceLocation TEXTURE_BG = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/bg.png");
    public static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/widgets.png");
    public static final ResourceLocation TEXTURE_PAPER = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/paper.png");

    private int scrollOffsetY = 0;
    private boolean isDraggingScrollbar = false;
    private int scrollbarStartY = 0;
    private int scrollOffsetStart = 0;
    private SheetNote draggedNote = null;
    private SheetNote lastModifiedNote = new SheetNote(1, 60, 1, 100);
    private CenteredEditBox nameField;
    private boolean hasNameField = false;

    private static final int PENTAGRAM_HEIGHT = 17;
    private static final int PENTAGRAM_SPACING = 16;
    private static final int PAPER_WIDTH = 139;
    private static final int PAPER_HEIGHT = 116;
    private static final int PAPER_OFFSET_X = 8;
    private static final int PAPER_OFFSET_Y = 49;
    private static final int STAFF_INNER_X = 4;
    private static final int TICK_WIDTH = 9;
    private static final int TOTAL_STAFFS = 12;
    private static final int SEMITONE_HEIGHT = 3;

    private static final int[] DIATONIC_STEPS = {
            79, // G5 (top line)
            77, // F5
            76, // E5
            74, // D5
            72, // C5
            71, // B4
            69, // A4
            67, // G4
            65, // F4
            64, // E4
            62, // D4
            60, // C4
            59, // B3
            57, // A3
            55, // G3
            53, // F3
            52  // E3 (bottom line)
    };


    public ComposingTableScreen(ComposingTableMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 176;
        this.imageHeight = 254;
        this.inventoryLabelY = 161;
        this.inventoryLabelX = 12;
    }

    @Override
    protected void init() {
        super.init();
        this.nameField = null;
        this.hasNameField = false;
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        ItemStack sheet = getSheetMusic();

        if (!sheet.isEmpty() && sheet.getItem() == BItemRegistry.SHEET_MUSIC.get()) {
            if (!hasNameField) {
                this.nameField = new CenteredEditBox(this.font, this.leftPos + 28, this.topPos + 29, 120, 16, Component.literal("Sheet Name"));
                this.nameField.setValue(sheet.getHoverName().getString());
                this.nameField.setMaxLength(32);
                this.nameField.setTextColor(0xffffff);
                this.nameField.setTextColorUneditable(0xffffff);
                this.nameField.setBordered(false);
                this.addRenderableWidget(this.nameField);
                hasNameField = true;
            } else {
                nameField.tick();

                if (this.nameField != null && !this.nameField.getValue().isEmpty()) {
                    if (!sheet.isEmpty() && sheet.getItem() == BItemRegistry.SHEET_MUSIC.get() && !this.nameField.getValue().equals(sheet.getHoverName().getString())) {
                        BNetwork.INSTANCE.sendToServer(new SheetRenamePacket(this.nameField.getValue()));
                    }
                }
            }
        } else {
            if (hasNameField) {
                this.removeWidget(this.nameField);
                this.nameField = null;
                hasNameField = false;
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        SheetNote note = findNoteAtLocation(mouseX, mouseY);
        if (note != null) {
            guiGraphics.renderTooltip(this.font, note.getTooltip(), Optional.empty(), Items.PAPER.getDefaultInstance(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        int i = this.leftPos;
        int j = this.topPos;

        guiGraphics.blit(TEXTURE_BG, i, j, 0, 0, this.imageWidth, this.imageHeight);

        if (!getSheetMusic().isEmpty()) {

            int paperX = i + 8;
            int paperY = j + 49;
            int paperWidth = 139;
            int paperHeight = 116;

            int staffX = paperX + 4;
            int staffY = paperY + 12;

            int totalTicks = 256;

            guiGraphics.blit(TEXTURE_PAPER, paperX, paperY, 0, 0, paperWidth, paperHeight);

            //quarter notes
            for (int t = 0; t < totalTicks; t++) {
                int x = staffX + (t * TICK_WIDTH);

                if (x >= staffX && x < staffX + 131) {
                    for (int s = 0; s < 3; s++) {
                        guiGraphics.fill(x, paperY + 6, x + 1, staffY + paperHeight - 15, 0xFFCCCCCC);
                    }
                }
            }

            // pentagram
            for (int s = 0; s < TOTAL_STAFFS; s++) {
                int y = staffY + 3 + s * (PENTAGRAM_HEIGHT + PENTAGRAM_SPACING) - scrollOffsetY;

                if (isInPaperBounds(paperX + 6, y))  {
                    guiGraphics.blit(TEXTURE_PAPER, staffX, y, 0, PAPER_HEIGHT, 131, PENTAGRAM_HEIGHT);
                }
            }

            //measures
            for (int t = 0; t < totalTicks; t++) {
                int x = staffX + (t * TICK_WIDTH);

                if (x >= staffX && x < staffX + 131) {
                    for (int s = 0; s < 3; s++) {
                        if (t % STAFF_INNER_X == 0 && t != 0) {
                            int y = staffY + 4 + s * (PENTAGRAM_HEIGHT + PENTAGRAM_SPACING) - scrollOffsetY;
                            if (isInPaperBounds(paperX + 6, y)) guiGraphics.fill(x, y - 2, x + 1, y + PENTAGRAM_HEIGHT, 0xFF888888);
                        }
                    }
                }
            }

            //notes
            List<SheetNote> notes = SheetMusicItem.getNotes(getSheetMusic());

            for (SheetNote note : notes) {
                int x = staffX + 3 + (note.tick * TICK_WIDTH);
                int y = paperY + 3 + pitchToY(note.pitch) - scrollOffsetY;
                int width = Math.max(4, (note.duration - 1) * TICK_WIDTH);

                if (isInPaperBounds(x, y)) drawNote(guiGraphics, x, y, width);
            }
        }

        int k = (int)((116 - 34) * (scrollOffsetY / (float) maxScroll()));
        guiGraphics.blit(TEXTURE_WIDGETS, i + 152, j + 49 + k, 32, !this.getSheetMusic().isEmpty() ? 224 : 224 + 15, 16, 15);

        int gearX = this.leftPos + 151;
        int gearY = this.topPos + 149;
        boolean hovered = mouseX >= gearX && mouseX < gearX + 18 && mouseY >= gearY && mouseY < gearY + 18;
        guiGraphics.blit(TEXTURE_WIDGETS, gearX, gearY, 160, getSheetMusic().isEmpty() ? 36 : hovered ? 18 : 0, 18, 18);
    }


    private int pitchToY(int pitch) {
        int stepIndex = 0;

        for (int i = 0; i < DIATONIC_STEPS.length; i++) {
            if (DIATONIC_STEPS[i] == pitch) {
                stepIndex = i;
                break;
            }
        }

        return stepIndex * SEMITONE_HEIGHT;
    }

    private void drawNote(GuiGraphics guiGraphics, int x, int y, int width) {

        if (width <= 4) {
            guiGraphics.blit(TEXTURE_PAPER, x, y, 0, 144, width, 4);
            return;
        }

        // Head
        guiGraphics.blit(TEXTURE_PAPER, x, y, 0, 144, 1, 4);

        // Middle
        for (int i = 0; i < width + 2; i++) {
            guiGraphics.blit(TEXTURE_PAPER, x + 1 + i, y, 1, 144, 1, 4);
        }

        // Tail
        guiGraphics.blit(TEXTURE_PAPER, x + width + 3, y, 3, 144, 1, 4);
    }

    private SheetNote findNoteAtTickAndPitch(ItemStack sheet, int tick, int pitch) {
        List<SheetNote> notes = SheetMusicItem.getNotes(sheet);
        for (SheetNote note : notes) {
            if (note.pitch == pitch && tick >= note.tick && tick <= note.tick + (note.duration - 1)) {
                return note;
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int scrollBarX = this.leftPos + 152;
        int scrollBarY = this.topPos + 49;
        int thumbHeight = 15;
        int thumbY = scrollBarY + (int)((116 - thumbHeight) * (scrollOffsetY / (float) maxScroll()));

        if (this.nameField != null) {
            if (this.nameField.mouseClicked(mouseX, mouseY, button)) {
                this.nameField.setFocused(true);
                return true;
            }
            this.nameField.setFocused(false);
        }

        if (mouseX >= scrollBarX && mouseX < scrollBarX + 16 && mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
            isDraggingScrollbar = true;
            scrollbarStartY = (int) mouseY;
            scrollOffsetStart = scrollOffsetY;
            return true;
        }


        int gearX = this.leftPos + 151;
        int gearY = this.topPos + 149;

        if (mouseX >= gearX && mouseX < gearX + 18 && mouseY >= gearY && mouseY < gearY + 18 && !getSheetMusic().isEmpty()) {
            this.minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F,1);
            this.minecraft.setScreen(new SheetSettingsScreen(Component.empty(),this, getSheetMusic()));
            return true;
        }

        if (isInPaperBounds(mouseX, mouseY)) {

            ItemStack sheet = this.getSheetMusic();
            if (!sheet.isEmpty()) {

                if (button == 1) {
                    SheetNote toRemove = findNoteAtLocation(mouseX, mouseY);
                    if (toRemove != null) {
                        BNetwork.INSTANCE.sendToServer(new SheetNoteEditPacket(
                                SheetNoteEditPacket.Action.REMOVE, toRemove.tick, toRemove.pitch, 0, 0
                        ));
                        return true;
                    }
                } else {
                    int paperX = this.leftPos + PAPER_OFFSET_X + STAFF_INNER_X;
                    int paperY = this.topPos + PAPER_OFFSET_Y;
                    int localX = (int) (mouseX - paperX);
                    int localY = (int) (mouseY - paperY + scrollOffsetY);
                    int tick = (localX / TICK_WIDTH);
                    int pitch = getDiatonicPitchFromY(localY);

                    SheetNote toDrag = findNoteAtTickAndPitch(sheet, tick, pitch);

                    if (toDrag != null) {
                        draggedNote = toDrag;
                        return true;
                    }

                    draggedNote = new SheetNote(tick, pitch, 1, 100);

                    int duration = lastModifiedNote.duration + tick > 14 ? lastModifiedNote.duration - (lastModifiedNote.duration + tick - 14) : lastModifiedNote.duration;

                    this.minecraft.player.playSound(SoundEvents.NOTE_BLOCK_HARP.get(), 1.0F, (float) Math.pow(2.0D, (pitch - 69) / 12.0D));
                    if (getSheetMusic().is(Items.PAPER)) this.minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
                    BNetwork.INSTANCE.sendToServer(new SheetNoteEditPacket(
                            SheetNoteEditPacket.Action.ADD, tick, pitch, duration, lastModifiedNote.velocity
                    ));

                    return true;
                }

            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        SheetNote note = findNoteAtLocation(mouseX, mouseY);

        if (isDraggingScrollbar) {
            int delta = (int) mouseY - scrollbarStartY;

            float ratio = delta / (float) (116 - 15);
            scrollOffsetY = Mth.clamp(scrollOffsetStart + (int) (ratio * maxScroll()), 0, maxScroll());
            return true;
        }

        if (button == 1 && note != null) {
            BNetwork.INSTANCE.sendToServer(new SheetNoteEditPacket(
                    SheetNoteEditPacket.Action.REMOVE, note.tick, note.pitch, note.duration, note.velocity
            ));
        } else {
            if (draggedNote != null) {
                int paperX = this.leftPos + PAPER_OFFSET_X + STAFF_INNER_X;
                int localX = (int) (mouseX - paperX);
                int currentTick = (localX / TICK_WIDTH);

                int newDuration = lastModifiedNote.duration + draggedNote.tick > 14 ? lastModifiedNote.duration - (lastModifiedNote.duration + draggedNote.tick - 14) : lastModifiedNote.duration;
                if (isInPaperBounds(mouseX, mouseY)) newDuration = Math.max(1, currentTick - draggedNote.tick + 1);

                lastModifiedNote = new SheetNote(draggedNote.tick, draggedNote.pitch, newDuration, draggedNote.velocity);

                BNetwork.INSTANCE.sendToServer(new SheetNoteEditPacket(
                        SheetNoteEditPacket.Action.UPDATE, draggedNote.tick, draggedNote.pitch, newDuration, draggedNote.velocity
                ));
                return true;
            }
        }


        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggedNote = null;
        isDraggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {

        SheetNote note = findNoteAtLocation(mouseX, mouseY);
        if (note != null) {
            int amount = hasShiftDown() ? 1 : hasControlDown() ? 10 : 5;
            int newVelocity = Math.max(note.getMinVelocity(), Math.min(note.getMaxVelocity(), note.velocity + (int) delta * amount));

            lastModifiedNote = new SheetNote(note.tick, note.pitch, note.duration, newVelocity);

            BNetwork.INSTANCE.sendToServer(new SheetNoteEditPacket(
                    SheetNoteEditPacket.Action.UPDATE, note.tick, note.pitch, note.duration, newVelocity
            ));
            return true;
        } else {
            if (isInPaperBounds(mouseX, mouseY)) {
                scrollOffsetY = Mth.clamp(scrollOffsetY - (int) delta * 12, 0, maxScroll());
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private int maxScroll() {
        int totalHeight = TOTAL_STAFFS * (PENTAGRAM_HEIGHT + PENTAGRAM_SPACING);
        return Math.max(0, totalHeight - PAPER_HEIGHT);
    }

    private int getDiatonicPitchFromY(int localY) {
        int stepIndex = Math.round((float) (localY - 4) / SEMITONE_HEIGHT);
        stepIndex = Math.min(Math.max(0, stepIndex), DIATONIC_STEPS.length - 1);
        return DIATONIC_STEPS[stepIndex];
    }

    private boolean isInPaperBounds(double mouseX, double mouseY) {
        int paperX = this.leftPos + PAPER_OFFSET_X + STAFF_INNER_X;
        int paperY = this.topPos + PAPER_OFFSET_Y;
        return mouseX >= paperX && mouseX < paperX + PAPER_WIDTH - 15 && mouseY >= paperY + 3 && mouseY < paperY + PAPER_HEIGHT - 17;
    }

    private SheetNote findNoteAtLocation(double mouseX, double mouseY) {
        if (isInPaperBounds(mouseX, mouseY)) {
            ItemStack sheet = this.getSheetMusic();
            if (!sheet.isEmpty()) {
                int paperX = this.leftPos + PAPER_OFFSET_X + STAFF_INNER_X;
                int paperY = this.topPos + PAPER_OFFSET_Y;

                int localX = (int) (mouseX - paperX);
                int localY = (int) (mouseY - paperY + scrollOffsetY);

                int tick = (localX / TICK_WIDTH);
                int pitch = getDiatonicPitchFromY(localY);

                return findNoteAtTickAndPitch(sheet, tick, pitch);
            }
        }
        return null;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.nameField != null && this.nameField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.nameField != null && this.nameField.isFocused()) {
            if (keyCode == 256) this.minecraft.player.closeContainer();
            return this.nameField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public ItemStack getSheetMusic() {
        return this.menu.getSheetMusicSlot().getItem();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
}