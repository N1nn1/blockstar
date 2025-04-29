package com.ninni.blockstar.client.midi;

import com.ninni.blockstar.server.midi.MidiInputHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MidiSettingsScreen extends OptionsSubScreen {
    private float pressureSensitivity = 0.5f;

    public MidiSettingsScreen(Screen screen, Options options) {
        super(screen, options, Component.translatable("blockstar.options.midi.title"));
    }

    @Override
    protected void init() {
        List<MidiDevice.Info> midiDevices = new ArrayList<>(List.of(MidiSystem.getMidiDeviceInfo()));
        MidiDevice.Info initialValue = midiDevices.get(0);

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(lastScreen)).bounds(this.width / 2 - 100, this.height - 30, 200, 20).build());

        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            if (Objects.equals(MidiSettingsConfig.selectedDeviceName, info.getName())) initialValue = info;
        }

        addRenderableWidget(CycleButton.builder((MidiDevice.Info info) -> Component.literal(info.getName()).withStyle(ChatFormatting.YELLOW))
                .withValues(midiDevices).withInitialValue(initialValue)
                .create(this.width / 2 - 100, 50, 200, 20, Component.translatable("blockstar.options.midi.select_midi_device"),
                        (button, info) -> {
                            MidiSettingsConfig.selectedDeviceName = info.getName();
                            MidiSettingsConfig.save();
                            MidiInputHandler.startListening();
                        })
        );

        pressureSensitivity = MidiSettingsConfig.pressureSensitivity;
        addRenderableWidget(new AbstractSliderButton(this.width / 2 - 100, 100, 200, 20,
                Component.translatable("blockstar.options.midi.pressure_sensitivity", (int)(pressureSensitivity * 100) + "%"), pressureSensitivity) {

            @Override
            protected void updateMessage() {
                setMessage(Component.translatable("blockstar.options.midi.pressure_sensitivity", (int)(pressureSensitivity * 100) + "%"));
            }

            @Override
            protected void applyValue() {
                pressureSensitivity = (float) value;
                MidiSettingsConfig.pressureSensitivity = pressureSensitivity;
                MidiSettingsConfig.save();
            }
        });
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
    }
}
