package com.ninni.blockstar.client.sound.key;

import com.ninni.blockstar.client.midi.MidiSettingsConfig;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.registry.BNetwork;
import com.ninni.blockstar.server.data.SoundfontManager;
import com.ninni.blockstar.server.instrument.InstrumentType;
import com.ninni.blockstar.server.inventory.KeyboardMenu;
import com.ninni.blockstar.server.packet.PlaySoundPacket;
import com.ninni.blockstar.server.packet.StopSoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.Optional;

public abstract class Key {
    public final int note;
    public final int velocity;
    public final int x, y, width, height;
    public final boolean isBlack;
    public boolean isPressed = false;
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


    public Key(int note, int velocity, int x, int y, boolean isBlack, int width, int height) {
        this.note = note;
        this.velocity = velocity;
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
        this.width = width;
        this.height = height;
    }

    public void press(KeyboardMenu menu, Optional<Integer> velocity) {
        SoundfontManager.SoundfontDefinition soundfont = menu.getInstrumentType().getSoundfont(menu.getSoundfontSlot().getItem());
        SoundfontManager.InstrumentSoundfontData forInstrument = soundfont.getForInstrument(menu.getInstrumentType());
        int sampleNote = forInstrument.getClosestSampleNote(note);
        float pitch = (float) Math.pow(2, (note - sampleNote) / 12.0);

        String velocitySuffix = "";

        if (velocity.isPresent()) {
            velocitySuffix = getVelocity(menu.getInstrumentType(), soundfont, velocity.get());
        } else {
            if (forInstrument.velocityLayers().isPresent()) velocitySuffix = "_" + forInstrument.default_velocity().orElse(forInstrument.velocityLayers().get());
        }

        ResourceLocation resourceLocation = new ResourceLocation(
                soundfont.name().getNamespace(),
                "soundfont." + BInstrumentTypeRegistry.get(menu.getInstrumentType()).getPath()
                        + "." + soundfont.name().getPath()
                        + "." + sampleNote
                        + velocitySuffix
        );

        LocalPlayer player = Minecraft.getInstance().player;

        BNetwork.INSTANCE.send(
                PacketDistributor.NEAR.with(() -> PacketDistributor.TargetPoint.p(player.getX(), player.getY(), player.getZ(), 32, player.level().dimension()).get()),
                new PlaySoundPacket(resourceLocation, pitch, player.getId(), note, Optional.empty())
        );

        isPressed = true;
    }

    public void release(KeyboardMenu menu, boolean sustained) {
        isPressed = false;
        SoundfontManager.SoundfontDefinition soundfont = menu.getInstrumentType().getSoundfont(menu.getSoundfontSlot().getItem());
        if (!sustained && soundfont.getForInstrument(menu.getInstrumentType()).held()) {
            stopKeySound(menu, soundfont);
        }
    }

    public void stopKeySound(KeyboardMenu menu, SoundfontManager.SoundfontDefinition soundfont) {
        LocalPlayer player = Minecraft.getInstance().player;
        BNetwork.INSTANCE.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), 32, player.level().dimension())),
                new StopSoundPacket(note, soundfont.getForInstrument(menu.getInstrumentType()).releaseTicks(), player.getId())
        );
    }

    public static String getVelocity(InstrumentType type, SoundfontManager.SoundfontDefinition soundfont, int inputVelocity) {
        if (soundfont.getForInstrument(type).velocityLayers().isPresent()) {
            int layers = soundfont.getForInstrument(type).velocityLayers().get();

            float sensitivity = MidiSettingsConfig.pressureSensitivity * 2.0f;
            sensitivity = Math.max(0.1f, Math.min(sensitivity, 2.0f));
            float adjustedVelocity = Math.min(inputVelocity * sensitivity, 127);

            // Soft: ~20%, Mid: ~50%, Hard: ~30%
            float softZoneEnd = 127.0f * 0.2f;
            float midZoneEnd = softZoneEnd + 127.0f * 0.5f;


            int softLayers = Math.max(1, (int)(layers * 0.2f));
            int midLayers = Math.max(1, (int)(layers * 0.5f));
            int hardLayers = Math.max(1, layers - softLayers - midLayers);


            int totalAssignedLayers = softLayers + midLayers + hardLayers;
            if (totalAssignedLayers < layers) {
                hardLayers += (layers - totalAssignedLayers);
            } else if (totalAssignedLayers > layers) {
                hardLayers -= (totalAssignedLayers - layers);
            }

            int layer;

            if (adjustedVelocity <= softZoneEnd) {
                float percent = adjustedVelocity / softZoneEnd;
                layer = (int)(percent * softLayers) + 1;
            } else if (adjustedVelocity <= midZoneEnd) {
                float percent = (adjustedVelocity - softZoneEnd) / (midZoneEnd - softZoneEnd);
                layer = softLayers + (int)(percent * midLayers) + 1;
            } else {
                float percent = (adjustedVelocity - midZoneEnd) / (127.0f - midZoneEnd);
                layer = softLayers + midLayers + (int)(percent * hardLayers) + 1;
            }

            layer = Math.min(layer, layers);
            if (inputVelocity == 0) layer = (int)Math.ceil((float)layers/2);

            return "_" + layer;
        }
        return "";
    }




    public boolean isMouseHoveringOver(int leftPos, int topPos, double mouseX, double mouseY) {
        return mouseX >= (leftPos + x) && mouseX <= (leftPos + x) + width && mouseY >= (topPos + y) && mouseY <= (topPos + y) + height;
    }
}
