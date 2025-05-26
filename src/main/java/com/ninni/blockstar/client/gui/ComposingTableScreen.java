package com.ninni.blockstar.client.gui;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.gui.components.CenteredEditBox;
import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BItemRegistry;
import com.ninni.blockstar.registry.BNetwork;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.inventory.ComposingTableMenu;
import com.ninni.blockstar.server.item.SheetMusicItem;
import com.ninni.blockstar.server.packet.SheetNoteEditPacket;
import com.ninni.blockstar.server.packet.SheetRenamePacket;
import com.ninni.blockstar.server.sheetmusic.SheetNote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
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
import java.util.function.Function;

import static com.ninni.blockstar.client.sound.key.Key.getVelocity;

public class ComposingTableScreen extends AbstractContainerScreen<ComposingTableMenu> {
    public static final ResourceLocation TEXTURE_BG = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/bg.png");
    public static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/widgets.png");
    public static final ResourceLocation TEXTURE_PAPER = new ResourceLocation(Blockstar.MODID, "textures/gui/composing_table/paper.png");
    private static final Function<String, ResourceLocation> FUNCTION = s -> new ResourceLocation(Blockstar.MODID, "item/empty_slot_" + s);
    private static final List<ResourceLocation> INPUT_LIST = List.of(FUNCTION.apply("paper"), FUNCTION.apply("sheet_music"));
    private final CyclingSlotBackground inputIcon =  new CyclingSlotBackground(0);

    private int scrollOffsetY = 0;
    private boolean isDraggingScrollbar = false;
    private int scrollbarStartY = 0;
    private int scrollOffsetStart = 0;
    private SheetNote draggedNote = null;
    private SheetNote lastModifiedNote = new SheetNote(1, 60, 1, 100);
    private CenteredEditBox nameField;
    private boolean hasNameField = false;

    private static final int TOTAL_STAFFS = 12;

    public int[] majorSteps = {23, 21, 19, 17, 16, 14, 12, 11, 9, 7, 5, 4, 2, 0};
    public int[] minorSteps = {22, 20, 19, 17, 15, 14, 12, 10, 8, 7, 5, 3, 2, 0};

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
                this.nameField.setMaxLength(20);
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

        this.inputIcon.tick(INPUT_LIST);
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
        this.inputIcon.render(this.menu, guiGraphics, partialTicks, this.leftPos, this.topPos);

        int inkAmount = (int) (this.menu.getInkAmount()/3.5);
        if (!this.menu.getInkSlot().hasItem()) guiGraphics.blit(TEXTURE_WIDGETS, i + 152, j + 29, 0, 224, 16, 16);
        guiGraphics.blit(TEXTURE_WIDGETS, i + 152, j + 49 + (57 - inkAmount), 240, 64 + (57 - inkAmount), 16, inkAmount);
        if (!this.menu.getInstrumentSlot().hasItem()) guiGraphics.blit(TEXTURE_WIDGETS, i + 152, j + 110, 16, 224, 16, 16);
        if (!this.menu.getSoundfontSlot().hasItem()) guiGraphics.blit(TEXTURE_WIDGETS, i + 152, j + 130, 0, 240, 16, 16);

        if (!getSheetMusic().isEmpty()) {

            int paperX = i + 8;
            int paperY = j + 49;
            int paperWidth = 139;
            int paperHeight = 116;

            int staffX = paperX + 4;
            int staffy = paperY + 12;

            int totalTicks = 256;

            guiGraphics.blit(TEXTURE_PAPER, paperX, paperY, 0, 0, paperWidth, paperHeight);

            //quarter notes
            for (int t = 0; t < totalTicks; t++) {
                int x = staffX + (t * 9);

                if (x >= staffX && x < staffX + 131) {
                    for (int s = 0; s < 3; s++) {
                        guiGraphics.fill(x, paperY + 6, x + 1, staffy + paperHeight - 15, 0xFFCCCCCC);
                    }
                }
            }

            guiGraphics.enableScissor(paperX ,paperY + 4,paperX + paperWidth,paperY + paperHeight - 4);

            // pentagram
            for (int s = 0; s <= 11; s++) {
                int staffY = paperY + 6 + s * 33 - scrollOffsetY;

                if (s > 0) guiGraphics.blit(TEXTURE_PAPER, paperX + 4, staffY - 2, 0, 162, 130, 1);

                for (int r = 0; r <= 5; r++) {
                    if (s != 11) guiGraphics.blit(TEXTURE_PAPER, paperX + 4, staffY - 2 + (r * 4) + 9, 0, 137 + (r * 4), 130, 1);
                }
            }

            //measures
            int ticksPerMeasure = SheetMusicItem.getTimeSigValues(SheetMusicItem.getTimeSig(getSheetMusic()), true);
            int ticksPerStaff = 14;
            int tickWidth = 9;
            int tickOffset = 0;

            for (int s = 0; s < 11; s++) {
                int measureY = paperY + 6 + s * 33 - scrollOffsetY;

                for (int t = 0; t < ticksPerStaff; t++) {
                    int currentTick = tickOffset + t;

                    if (currentTick % ticksPerMeasure == 0 && t != 0) {
                        int x = paperX + 4 + (t * tickWidth);
                        guiGraphics.fill(x, measureY + 6, x + 1, measureY + 25, 0xFF888888);
                    }
                }
                tickOffset += ticksPerStaff;
            }

            List<SheetNote> notes = SheetMusicItem.getNotes(getSheetMusic());

            for (SheetNote note : notes) {
                int noteTick = note.tick;
                int staffIndex = noteTick / ticksPerStaff;
                int tickInStaff = noteTick % ticksPerStaff;

                int x = paperX + 7 + tickInStaff * tickWidth;
                int y = paperY + 6 + staffIndex * 33 + pitchToY(note.pitch) - scrollOffsetY;
                int width = Math.max(4, (note.duration - 1) * tickWidth);

                drawNote(guiGraphics, x, y, width);
            }
            guiGraphics.disableScissor();
        }

        int gearX = this.leftPos + 151;
        int gearY = this.topPos + 149;
        boolean hovered = mouseX >= gearX && mouseX < gearX + 18 && mouseY >= gearY && mouseY < gearY + 18;
        guiGraphics.blit(TEXTURE_WIDGETS, gearX, gearY, 160, getSheetMusic().isEmpty() ? 36 : hovered ? 18 : 0, 18, 18);
    }

    private int getStaffNumber(double mouseX, double mouseY, int left, int top) {
        int paperX0 = left + 12;
        int paperX1 = left + 143;
        int paperY0 = top + 55;
        int paperY1 = top + 154;

        if (mouseX >= paperX0 && mouseX < paperX1 && mouseY >= paperY0 && mouseY < paperY1) {
            int localY = (int) (mouseY - paperY0 + scrollOffsetY);

            int staffNum = localY / 33;

            return staffNum < 11 ? staffNum : -1;
        }

        return -1;
    }


    private int pitchToY(int pitch) {
        int[] diatonicSteps = SheetMusicItem.isKeyMinor(getSheetMusic()) ? minorSteps : majorSteps;

        for (int i = 0; i < diatonicSteps.length; i++) {
            if (diatonicSteps[i] == (pitch - 60)) return i * 2;
        }
        return 2;
    }

    private void drawNote(GuiGraphics guiGraphics, int x, int y, int width) {

        if (width <= 4) {
            guiGraphics.blit(TEXTURE_PAPER, x, y, 144, 0, width, 3);
            return;
        }

        // Head
        guiGraphics.blit(TEXTURE_PAPER, x, y, 144, 0, 1, 3);

        // Middle
        for (int i = 0; i < width + 2; i++) {
            guiGraphics.blit(TEXTURE_PAPER, x + 1 + i, y, 145, 0, 1, 3);
        }

        // Tail
        guiGraphics.blit(TEXTURE_PAPER, x + width + 3, y, 147, 0, 1, 3);
    }

    private SheetNote findNoteAtTickAndPitch(ItemStack sheet, int tick, int pitch) {
        List<SheetNote> notes = SheetMusicItem.getNotes(sheet);
        for (SheetNote note : notes) {
            if (tick >= 0 && note.pitch == pitch && tick >= note.tick && tick <= note.tick + (note.duration - 1)) {
                return note;
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.nameField != null) {
            if (this.nameField.mouseClicked(mouseX, mouseY, button)) {
                this.nameField.setFocused(true);
                return true;
            }
            this.nameField.setFocused(false);
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
                    int paperX = this.leftPos + 12;
                    int localX = (int)(mouseX - paperX);

                    int ticksPerStaff = 14;
                    int tickWidth = 9;

                    int tickInStaff = localX / tickWidth;
                    int staffNum = getStaffNumber((int)mouseX, (int)mouseY, this.leftPos, this.topPos);
                    if (staffNum == -1) return false;

                    int tick = staffNum * ticksPerStaff + tickInStaff;
                    int note = getDiatonicPitchFromY(mouseY);

                    SheetNote toDrag = findNoteAtTickAndPitch(sheet, tick, note);

                    if (toDrag != null) {
                        draggedNote = toDrag;
                        return true;
                    }

                    draggedNote = new SheetNote(tick, note, 1, 100);

                    int duration = lastModifiedNote.duration > 0 ? lastModifiedNote.duration : 1;

                    if (getSheetMusic().is(Items.PAPER)) this.minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);

                    if (this.menu.getInkAmount() > 0) {
                        SheetNote note1 = new SheetNote(tick, note, 1, lastModifiedNote.velocity);
                        this.playSoundFromNote(note1, Optional.of(10));
                    }
                    BNetwork.INSTANCE.sendToServer(new SheetNoteEditPacket(
                            SheetNoteEditPacket.Action.ADD, tick, note, duration, lastModifiedNote.velocity
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
                int paperX = this.leftPos + 12;
                int localX = (int)(mouseX - paperX);

                int ticksPerStaff = 14;
                int tickWidth = 9;

                int tickInStaff = localX / tickWidth;
                int staffNum = getStaffNumber((int)mouseX, (int)mouseY, this.leftPos, this.topPos);
                if (staffNum == -1) return false;

                int tick = staffNum * ticksPerStaff + tickInStaff;

                int newDuration = 1;
                if (isInPaperBounds(mouseX, mouseY)) newDuration = Math.max(1, tick - draggedNote.tick + 1);

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

            playSoundFromNote(note, Optional.of(10));
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
        return Math.max(0, TOTAL_STAFFS * (33) - 116);
    }

    private int getDiatonicPitchFromY(double mouseY) {
        int topStaffY = this.topPos + 53;
        int localY = (int)(mouseY - topStaffY + scrollOffsetY);
        int yWithinStaff = localY % 33;
        int rowInStaff = (yWithinStaff - 2) / 2;
        int[] diatonicSteps = SheetMusicItem.isKeyMinor(getSheetMusic()) ? minorSteps : majorSteps;

        rowInStaff = Mth.clamp(rowInStaff, 0, diatonicSteps.length - 1);

        return diatonicSteps[rowInStaff] + 60;
    }

    private boolean isInPaperBounds(double mouseX, double mouseY) {
        int paperX = this.leftPos + 8 + 4;
        int paperY = this.topPos + 49 + 1;
        return mouseX >= paperX && mouseX < paperX + 139 - 6 && mouseY >= paperY + 3 && mouseY < paperY + 116 - 4;
    }

    private SheetNote findNoteAtLocation(double mouseX, double mouseY) {
        if (isInPaperBounds(mouseX, mouseY)) {
            ItemStack sheet = this.getSheetMusic();
            if (!sheet.isEmpty()) {
                int paperX = this.leftPos + 12;
                int localX = (int)(mouseX - paperX);
                int staffNum = getStaffNumber(mouseX, mouseY, this.leftPos, this.topPos);

                int ticksPerStaff = 14;
                int tickWidth = 9;
                int tickInStaff = localX / tickWidth;

                int tick = staffNum * ticksPerStaff + tickInStaff;
                int pitch = getDiatonicPitchFromY(mouseY);

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

    private void playSoundFromNote(SheetNote note, Optional<Integer> duration) {
        SoundfontManager.SoundfontDefinition soundfont = menu.getSoundfont();
        SoundfontManager.InstrumentSoundfontData forInstrument = soundfont.getForInstrument(menu.getInstrumentType());
        int sampleNote = forInstrument.getClosestSampleNote(note.pitch);
        float notePitch = (float) Math.pow(2, (note.pitch - sampleNote) / 12.0);

        String velocitySuffix = getVelocity(menu.getInstrumentType(), soundfont, note.velocity);
        ResourceLocation resourceLocation = new ResourceLocation(
                soundfont.name().getNamespace(),
                "soundfont." + BInstrumentTypeRegistry.get(menu.getInstrumentType()).getPath()
                        + "." + soundfont.name().getPath()
                        + "." + sampleNote
                        + velocitySuffix
        );

        this.minecraft.getSoundManager().play(new SoundfontSound(note.pitch, resourceLocation, note.velocity, notePitch, this.minecraft.player, duration));
    }
}