package com.ninni.blockstar.client.sound.key;

import com.ninni.blockstar.client.config.MidiSettingsConfig;
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

import java.util.Optional;

public abstract class Key {
    public final int note;
    public final int velocity;
    public final int x, y, width, height;
    public final boolean isBlack;
    public boolean isPressed = false;

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
