package com.ninni.blockstar.client.gui.components;

import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

//...... I dont wanna talk about it

@OnlyIn(Dist.CLIENT)
public class CenteredEditBox extends AbstractWidget implements Renderable {

    private final Font font;
    private String value;
    private int maxLength;
    private int frame;
    private boolean bordered;
    private boolean canLoseFocus;
    private boolean isEditable;
    private boolean shiftPressed;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private int textColor;
    private int textColorUneditable;
    @Nullable
    private String suggestion;
    @Nullable
    private Consumer<String> responder;
    private Predicate<String> filter;
    private BiFunction<String, Integer, FormattedCharSequence> formatter;
    @Nullable
    private Component hint;

    public CenteredEditBox(Font p_94114_, int p_94115_, int p_94116_, int p_94117_, int p_94118_, Component p_94119_) {
        this(p_94114_, p_94115_, p_94116_, p_94117_, p_94118_, null, p_94119_);
    }

    public CenteredEditBox(Font p_94106_, int p_94107_, int p_94108_, int p_94109_, int p_94110_, @Nullable EditBox p_94111_, Component p_94112_) {
        super(p_94107_, p_94108_, p_94109_, p_94110_, p_94112_);
        this.value = "";
        this.maxLength = 32;
        this.bordered = true;
        this.canLoseFocus = true;
        this.isEditable = true;
        this.textColor = 14737632;
        this.textColorUneditable = 7368816;
        this.filter = Objects::nonNull;
        this.formatter = (p_94147_, p_94148_) -> FormattedCharSequence.forward(p_94147_, Style.EMPTY);
        this.font = p_94106_;
        if (p_94111_ != null) {
            this.setValue(p_94111_.getValue());
        }

    }

    public void setResponder(Consumer<String> p_94152_) {
        this.responder = p_94152_;
    }

    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> p_94150_) {
        this.formatter = p_94150_;
    }

    public void tick() {
        ++this.frame;
    }

    protected MutableComponent createNarrationMessage() {
        Component $$0 = this.getMessage();
        return Component.translatable("gui.narrate.editBox", new Object[]{$$0, this.value});
    }

    public void setValue(String p_94145_) {
        if (this.filter.test(p_94145_)) {
            if (p_94145_.length() > this.maxLength) {
                this.value = p_94145_.substring(0, this.maxLength);
            } else {
                this.value = p_94145_;
            }

            this.moveCursorToEnd();
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(p_94145_);
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int $$0 = Math.min(this.cursorPos, this.highlightPos);
        int $$1 = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring($$0, $$1);
    }

    public void setFilter(Predicate<String> p_94154_) {
        this.filter = p_94154_;
    }

    public void insertText(String p_94165_) {
        int $$1 = Math.min(this.cursorPos, this.highlightPos);
        int $$2 = Math.max(this.cursorPos, this.highlightPos);
        int $$3 = this.maxLength - this.value.length() - ($$1 - $$2);
        String $$4 = SharedConstants.filterText(p_94165_);
        int $$5 = $$4.length();
        if ($$3 < $$5) {
            $$4 = $$4.substring(0, $$3);
            $$5 = $$3;
        }

        String $$6 = (new StringBuilder(this.value)).replace($$1, $$2, $$4).toString();
        if (this.filter.test($$6)) {
            this.value = $$6;
            this.setCursorPosition($$1 + $$5);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(this.value);
        }
    }

    private void onValueChange(String p_94175_) {
        if (this.responder != null) {
            this.responder.accept(p_94175_);
        }

    }

    private void deleteText(int p_94218_) {
        if (Screen.hasControlDown()) {
            this.deleteWords(p_94218_);
        } else {
            this.deleteChars(p_94218_);
        }

    }

    public void deleteWords(int p_94177_) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteChars(this.getWordPosition(p_94177_) - this.cursorPos);
            }
        }
    }

    public void deleteChars(int p_94181_) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int $$1 = this.getCursorPos(p_94181_);
                int $$2 = Math.min($$1, this.cursorPos);
                int $$3 = Math.max($$1, this.cursorPos);
                if ($$2 != $$3) {
                    String $$4 = (new StringBuilder(this.value)).delete($$2, $$3).toString();
                    if (this.filter.test($$4)) {
                        this.value = $$4;
                        this.moveCursorTo($$2);
                    }
                }
            }
        }
    }

    public int getWordPosition(int p_94185_) {
        return this.getWordPosition(p_94185_, this.getCursorPosition());
    }

    private int getWordPosition(int p_94129_, int p_94130_) {
        return this.getWordPosition(p_94129_, p_94130_, true);
    }

    private int getWordPosition(int p_94141_, int p_94142_, boolean p_94143_) {
        int $$3 = p_94142_;
        boolean $$4 = p_94141_ < 0;
        int $$5 = Math.abs(p_94141_);

        for(int $$6 = 0; $$6 < $$5; ++$$6) {
            if (!$$4) {
                int $$7 = this.value.length();
                $$3 = this.value.indexOf(32, $$3);
                if ($$3 == -1) {
                    $$3 = $$7;
                } else {
                    while(p_94143_ && $$3 < $$7 && this.value.charAt($$3) == ' ') {
                        ++$$3;
                    }
                }
            } else {
                while(p_94143_ && $$3 > 0 && this.value.charAt($$3 - 1) == ' ') {
                    --$$3;
                }

                while($$3 > 0 && this.value.charAt($$3 - 1) != ' ') {
                    --$$3;
                }
            }
        }

        return $$3;
    }

    public void moveCursor(int p_94189_) {
        this.moveCursorTo(this.getCursorPos(p_94189_));
    }

    private int getCursorPos(int p_94221_) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, p_94221_);
    }

    public void moveCursorTo(int p_94193_) {
        this.setCursorPosition(p_94193_);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    public void setCursorPosition(int p_94197_) {
        this.cursorPos = Mth.clamp(p_94197_, 0, this.value.length());
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    public boolean keyPressed(int p_94132_, int p_94133_, int p_94134_) {
        if (!this.canConsumeInput()) {
            return false;
        } else {
            this.shiftPressed = Screen.hasShiftDown();
            if (Screen.isSelectAll(p_94132_)) {
                this.moveCursorToEnd();
                this.setHighlightPos(0);
                return true;
            } else if (Screen.isCopy(p_94132_)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                return true;
            } else if (Screen.isPaste(p_94132_)) {
                if (this.isEditable) {
                    this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                }

                return true;
            } else if (Screen.isCut(p_94132_)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                if (this.isEditable) {
                    this.insertText("");
                }

                return true;
            } else {
                switch (p_94132_) {
                    case 259:
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(-1);
                            this.shiftPressed = Screen.hasShiftDown();
                        }

                        return true;
                    case 260:
                    case 264:
                    case 265:
                    case 266:
                    case 267:
                    default:
                        return false;
                    case 261:
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(1);
                            this.shiftPressed = Screen.hasShiftDown();
                        }

                        return true;
                    case 262:
                        if (Screen.hasControlDown()) {
                            this.moveCursorTo(this.getWordPosition(1));
                        } else {
                            this.moveCursor(1);
                        }

                        return true;
                    case 263:
                        if (Screen.hasControlDown()) {
                            this.moveCursorTo(this.getWordPosition(-1));
                        } else {
                            this.moveCursor(-1);
                        }

                        return true;
                    case 268:
                        this.moveCursorToStart();
                        return true;
                    case 269:
                        this.moveCursorToEnd();
                        return true;
                }
            }
        }
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    public boolean charTyped(char p_94122_, int p_94123_) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (SharedConstants.isAllowedChatCharacter(p_94122_)) {
            if (this.isEditable) {
                this.insertText(Character.toString(p_94122_));
            }

            return true;
        } else {
            return false;
        }
    }

    public void onClick(double p_279417_, double p_279437_) {
        String visible = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        int textWidth = this.font.width(visible);
        int boxCenter = this.getX() + this.width / 2;
        int clickOffset = (int)p_279417_ - (boxCenter - textWidth / 2);
        this.moveCursorTo(this.font.plainSubstrByWidth(visible, clickOffset).length() + this.displayPos);
    }

    public void playDownSound(SoundManager p_279245_) {
    }

    public void renderWidget(GuiGraphics p_283252_, int p_281594_, int p_282100_, float p_283101_) {
        if (this.isVisible()) {
            if (this.isBordered()) {
                int $$4 = this.isFocused() ? -1 : -6250336;
                p_283252_.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, $$4);
                p_283252_.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
            }

            int $$5 = this.isEditable ? this.textColor : this.textColorUneditable;
            int $$6 = this.cursorPos - this.displayPos;
            int $$7 = this.highlightPos - this.displayPos;
            String $$8 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean $$9 = $$6 >= 0 && $$6 <= $$8.length();
            boolean $$10 = this.isFocused() && this.frame / 6 % 2 == 0 && $$9;
            int $$11 = this.getX() + (this.width - this.font.width($$8)) / 2;
            int $$12 = this.getY() + (this.height - 9) / 2;
            int $$13 = $$11;
            if ($$7 > $$8.length()) {
                $$7 = $$8.length();
            }

            if (!$$8.isEmpty()) {
                String $$14 = $$9 ? $$8.substring(0, $$6) : $$8;
                $$13 = p_283252_.drawString(this.font, this.formatter.apply($$14, this.displayPos), $$11, $$12, $$5);
            }

            boolean $$15 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
            //TODO
            int $$16 = $$11 + this.font.width($$8.substring(0, $$6));
            if (!$$9) {
                $$16 = $$6 > 0 ? $$11 + this.width : $$11;
            } else if ($$15) {
                $$16 = $$13 - 1;
                --$$13;
            }

            if (!$$8.isEmpty() && $$9 && $$6 < $$8.length()) {
                p_283252_.drawString(this.font, this.formatter.apply($$8.substring($$6), this.cursorPos), $$13, $$12, $$5);
            }

            if (this.hint != null && $$8.isEmpty() && !this.isFocused()) {
                int hintX = this.getX() + (this.width - this.font.width(this.hint)) / 2;
                p_283252_.drawString(this.font, this.hint, hintX, $$12, $$5);
            }

            if (!$$15 && this.suggestion != null) {
                p_283252_.drawString(this.font, this.suggestion, $$16 - 1, $$12, -8355712);
            }

            if ($$10) {
                if ($$15) {
                    RenderType var10001 = RenderType.guiOverlay();
                    int var10003 = $$12 - 1;
                    int var10004 = $$16 + 1;
                    int var10005 = $$12 + 1;
                    Objects.requireNonNull(this.font);
                    p_283252_.fill(var10001, $$16, var10003, var10004, var10005 + 9, -3092272);
                } else {
                    p_283252_.drawString(this.font, "_", $$16, $$12, $$5);
                }
            }

            if ($$7 != $$6) {
                int $$17 = $$11 + this.font.width($$8.substring(0, $$7));
                int var19 = $$12 - 1;
                int var20 = $$17 - 1;
                int var21 = $$12 + 1;
                Objects.requireNonNull(this.font);
                this.renderHighlight(p_283252_, $$16, var19, var20, var21 + 9);
            }

        }
    }

    private void renderHighlight(GuiGraphics p_281400_, int p_265338_, int p_265693_, int p_265618_, int p_265584_) {
        if (p_265338_ < p_265618_) {
            int $$5 = p_265338_;
            p_265338_ = p_265618_;
            p_265618_ = $$5;
        }

        if (p_265693_ < p_265584_) {
            int $$6 = p_265693_;
            p_265693_ = p_265584_;
            p_265584_ = $$6;
        }

        if (p_265618_ > this.getX() + this.width) {
            p_265618_ = this.getX() + this.width;
        }

        if (p_265338_ > this.getX() + this.width) {
            p_265338_ = this.getX() + this.width;
        }

        p_281400_.fill(RenderType.guiTextHighlight(), p_265338_, p_265693_, p_265618_, p_265584_, -16776961);
    }

    public void setMaxLength(int p_94200_) {
        this.maxLength = p_94200_;
        if (this.value.length() > p_94200_) {
            this.value = this.value.substring(0, p_94200_);
            this.onValueChange(this.value);
        }

    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    private boolean isBordered() {
        return this.bordered;
    }

    public void setBordered(boolean p_94183_) {
        this.bordered = p_94183_;
    }

    public void setTextColor(int p_94203_) {
        this.textColor = p_94203_;
    }

    public void setTextColorUneditable(int p_94206_) {
        this.textColorUneditable = p_94206_;
    }

    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent p_265216_) {
        return this.visible && this.isEditable ? super.nextFocusPath(p_265216_) : null;
    }

    public boolean isMouseOver(double p_94157_, double p_94158_) {
        return this.visible && p_94157_ >= (double)this.getX() && p_94157_ < (double)(this.getX() + this.width) && p_94158_ >= (double)this.getY() && p_94158_ < (double)(this.getY() + this.height);
    }

    public void setFocused(boolean p_265520_) {
        if (this.canLoseFocus || p_265520_) {
            super.setFocused(p_265520_);
            if (p_265520_) {
                this.frame = 0;
            }

        }
    }

    private boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean p_94187_) {
        this.isEditable = p_94187_;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int p_94209_) {
        int $$1 = this.value.length();
        this.highlightPos = Mth.clamp(p_94209_, 0, $$1);
        if (this.font != null) {
            if (this.displayPos > $$1) {
                this.displayPos = $$1;
            }

            int $$2 = this.getInnerWidth();
            String $$3 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), $$2);
            int $$4 = $$3.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, $$2, true).length();
            }

            if (this.highlightPos > $$4) {
                this.displayPos += this.highlightPos - $$4;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }

            this.displayPos = Mth.clamp(this.displayPos, 0, $$1);
        }

    }

    public void setCanLoseFocus(boolean p_94191_) {
        this.canLoseFocus = p_94191_;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean p_94195_) {
        this.visible = p_94195_;
    }

    public void setSuggestion(@Nullable String p_94168_) {
        this.suggestion = p_94168_;
    }

    public int getScreenX(int p_94212_) {
        String visible = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        int textStartX = this.getX() + (this.width - this.font.width(visible)) / 2;
        int relative = Mth.clamp(p_94212_, this.displayPos, this.displayPos + visible.length());
        return textStartX + this.font.width(this.value.substring(this.displayPos, relative));
    }

    public void updateWidgetNarration(NarrationElementOutput p_259237_) {
        p_259237_.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }

    public void setHint(Component p_259584_) {
        this.hint = p_259584_;
    }
}
