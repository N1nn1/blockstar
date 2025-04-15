package com.ninni.blockstar.client.sound.key;

import com.ninni.blockstar.client.sound.SoundfontSound;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.server.data.SoundfontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public abstract class Key {
    public final int note;
    public final float velocity = 2;
    public final int x, y, width, height;
    public final boolean isBlack;
    public boolean isPressed = false;
    private final Map<Integer, SoundfontSound> activeSounds = new HashMap<>();
    public static final Map<Integer, Integer> KEY_TO_NOTE = Map.ofEntries(
            Map.entry(GLFW.GLFW_KEY_Z, 48), //C3
            Map.entry(GLFW.GLFW_KEY_S, 49), //C#3
            Map.entry(GLFW.GLFW_KEY_X, 50), //D3
            Map.entry(GLFW.GLFW_KEY_D, 51), //D#3
            Map.entry(GLFW.GLFW_KEY_C, 52), //E3
            Map.entry(GLFW.GLFW_KEY_V, 53), //F3
            Map.entry(GLFW.GLFW_KEY_G, 54), //F#3
            Map.entry(GLFW.GLFW_KEY_B, 55), //G3
            Map.entry(GLFW.GLFW_KEY_H, 56), //G#3
            Map.entry(GLFW.GLFW_KEY_N, 57), //A3
            Map.entry(GLFW.GLFW_KEY_J, 58), //A#3
            Map.entry(GLFW.GLFW_KEY_M, 59), //B3
            Map.entry(GLFW.GLFW_KEY_COMMA, 60), //C2
            Map.entry(GLFW.GLFW_KEY_L, 61), //C#4
            Map.entry(GLFW.GLFW_KEY_PERIOD, 62), //D4
            Map.entry(GLFW.GLFW_KEY_SEMICOLON, 63), //D#4
            Map.entry(GLFW.GLFW_KEY_SLASH, 64), //E4

            Map.entry(GLFW.GLFW_KEY_Q, 60), //C4
            Map.entry(GLFW.GLFW_KEY_2, 61), //C#4
            Map.entry(GLFW.GLFW_KEY_W, 62), //D4
            Map.entry(GLFW.GLFW_KEY_3, 63), //D#4
            Map.entry(GLFW.GLFW_KEY_E, 64), //E4
            Map.entry(GLFW.GLFW_KEY_R, 65), //F4
            Map.entry(GLFW.GLFW_KEY_5, 66), //F#4
            Map.entry(GLFW.GLFW_KEY_T, 67), //G4
            Map.entry(GLFW.GLFW_KEY_6, 68), //G#4
            Map.entry(GLFW.GLFW_KEY_Y, 69), //A4
            Map.entry(GLFW.GLFW_KEY_7, 70), //A#4
            Map.entry(GLFW.GLFW_KEY_U, 71),  //B4
            Map.entry(GLFW.GLFW_KEY_I, 72), //C5
            Map.entry(GLFW.GLFW_KEY_9, 73), //C#5
            Map.entry(GLFW.GLFW_KEY_O, 74), //D5
            Map.entry(GLFW.GLFW_KEY_0, 75), //D#5
            Map.entry(GLFW.GLFW_KEY_P, 76)  //E5
    );


    public Key(int note, float velocity, int x, int y, boolean isBlack, int width, int height) {
        this.note = note;
        //this.velocity = velocity;
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
        this.width = width;
        this.height = height;
    }


    public void handleKeyPress(SoundfontManager.SoundfontDefinition soundfont, boolean sustained) {
        if (!isPressed) {
            SoundfontSound sound = getSoundfontSound(soundfont);
            Minecraft.getInstance().getSoundManager().play(sound);
            activeSounds.put(note, sound);
            isPressed = true;
        } else {
            if (!sustained) stopKeySound(soundfont, note);
            isPressed = false;
        }
    }

    public void stopKeySound(SoundfontManager.SoundfontDefinition soundfont, int note) {
        SoundfontSound sound = activeSounds.get(note);
        if (sound != null && soundfont.held()) {
            Minecraft.getInstance().getSoundManager().stop(sound);
            activeSounds.remove(note);
        }
    }

    private @NotNull SoundfontSound getSoundfontSound(SoundfontManager.SoundfontDefinition soundfont) {
        int sampleNote = soundfont.getClosestSampleNote(note);
        float pitch = (float) Math.pow(2, (note - sampleNote) / 12.0);

        String velocity = soundfont.velocityLayers().isPresent() ? "_" + 2 : "";
        ResourceLocation resourceLocation = new ResourceLocation(soundfont.name().getNamespace(), "soundfont." + BInstrumentTypeRegistry.get(soundfont.instrumentType()).getPath() + "." + soundfont.name().getPath() + "." + sampleNote + velocity);
        SoundfontSound sound = new SoundfontSound(resourceLocation, 1.0f, pitch, Minecraft.getInstance().player);
        return sound;
    }

    public boolean isMouseHoveringOver(int leftPos, int topPos, double mouseX, double mouseY) {
        return mouseX >= (leftPos + x) && mouseX <= (leftPos + x) + width && mouseY >= (topPos + y) && mouseY <= (topPos + y) + height;
    }
}
