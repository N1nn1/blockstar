package com.ninni.blockstar.server.instrument;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class InstrumentType {
    private int lowestNote;
    private int highestNote;
    public int[] major               = {0, 2, 4, 5, 7, 9, 11, 12};
    public int[] minor               = {0, 2, 3, 5, 7, 8, 10, 12};
    public int[] pentatonic          = {0, 3, 5, 7, 10, 12};
    public int[] dorian              = {0, 2, 3, 5, 7, 9, 10, 12};
    public int[] phrygian            = {0, 1, 3, 5, 7, 8, 10, 12};
    public int[] lydian              = {0, 2, 4, 6, 7, 9, 11, 12};
    public int[] mixolydian          = {0, 2, 4, 5, 7, 9, 10, 12};
    public int[] blues               = {0, 3, 5, 6, 7, 10, 12};
    public int[] harmonicMinor       = {0, 2, 3, 5, 7, 8, 11, 12};
    public int[] chromatic           = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    public int[] doubleHarmonic      = {0, 1, 4, 5, 7, 8, 11, 12};
    public int[] doubleHarmonicMinor = {0, 1, 3, 5, 7, 8, 11, 12};
    public int[] locrian             = {0, 1, 3, 5, 6, 8, 10, 12};
    public int[] superLocrian        = {0, 1, 3, 4, 6, 8, 10, 12};

    public InstrumentType(int lowestNote, int highestNote) {
        this.lowestNote = lowestNote;
        this.highestNote = highestNote;
    }

    public int getRange() {
        return this.highestNote - this.lowestNote;
    }

    public boolean isInRange(int note) {
        return note <= this.highestNote && note >= this.lowestNote;
    }

    public int getLowestNote() {
        return lowestNote;
    }

    public int getHighestNote() {
        return highestNote;
    }

    public abstract void playNoteSoundFromBlock(BlockPos blockpos, Level level, Entity entity);

    public SoundfontManager.SoundfontDefinition getSoundfont(ItemStack stack) {
        SoundfontManager.SoundfontDefinition def = resolveSoundfont(stack);
        if (def == null) {
            def = getBaseSoundFont();
        }
        if (def == null) {
            Blockstar.LOGGER.error("[Soundfonts] Missing 'blockstar:base' definition! Falling back to first available.");
            var all = Blockstar.PROXY.getSoundfontManager().getAll();
            def = all.isEmpty() ? null : all.iterator().next();
        }
        return def;
    }

    public boolean isValidSoundfontForInstrumentType(ItemStack stack) {
        ResourceLocation id = readSoundfontId(stack);
        if (id == null) return false;

        SoundfontManager.SoundfontDefinition data = Blockstar.PROXY.getSoundfontManager().get(id);
        if (data == null) {
            Blockstar.LOGGER.debug("[Soundfonts] Unknown soundfont id on stack: {}", id);
            return false;
        }
        return data.instrumentData().containsKey(this);
    }

    public SoundfontManager.SoundfontDefinition getBaseSoundFont() {
        return Blockstar.PROXY.getSoundfontManager().get(new ResourceLocation(Blockstar.MODID, "base"));
    }

    @Nullable
    private ResourceLocation readSoundfontId(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) return null;
        if (!stack.getTag().contains("Soundfont")) return null;

        String raw = stack.getTag().getString("Soundfont");
        if (raw == null || raw.isEmpty()) return null;

        ResourceLocation id = ResourceLocation.tryParse(raw);
        if (id == null) {
            Blockstar.LOGGER.debug("[Soundfonts] Invalid ResourceLocation in NBT: '{}'", raw);
        }
        return id;
    }

    private SoundfontManager.SoundfontDefinition resolveSoundfont(ItemStack stack) {
        ResourceLocation id = readSoundfontId(stack);
        if (id == null) return null;

        SoundfontManager.SoundfontDefinition data = Blockstar.PROXY.getSoundfontManager().get(id);
        if (data == null) return null;
        return data.instrumentData().containsKey(this) ? data : null;
    }

    int[] getScaleForPlayer(LivingEntity entity, Level level) {
        if (entity.getHealth() < 2) {
            return harmonicMinor;
        } else if (level.isRaining()) {
            return level.getBiome(entity.blockPosition()).get().getPrecipitationAt(entity.blockPosition()).name().equals("NONE") ? phrygian : dorian;
        } else if (entity.hasEffect(MobEffects.SLOW_FALLING)) {
            return pentatonic;
        } else if (entity.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            return mixolydian;
        } else if (entity.hasEffect(MobEffects.REGENERATION)) {
            return lydian;
        } else if (level.isThundering()) {
            return blues;
        } else if (level.dimension() != Level.OVERWORLD) {
            return chromatic;
        } else {
            float temperature = level.getBiome(entity.blockPosition()).value().getBaseTemperature();
            boolean nightTime = level.getDayTime() % 24000 >= 13000;

            if (temperature >= 1.0F) {
                return nightTime ? doubleHarmonicMinor : doubleHarmonic;
            } else if (temperature <= 0.2F) {
                return nightTime ? superLocrian : locrian;
            } else {
                return nightTime ? minor : major;
            }
        }
    }

}
