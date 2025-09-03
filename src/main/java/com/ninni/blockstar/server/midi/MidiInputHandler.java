package com.ninni.blockstar.server.midi;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.gui.KeyboardScreen;
import com.ninni.blockstar.client.config.MidiSettingsConfig;

import javax.sound.midi.*;

public class MidiInputHandler {
    private static MidiDevice activeDevice;
    private static Transmitter activeTransmitter;

    public static void startListening() {
        stopListening();

        try {
            for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
                if (info.getName().equals(MidiSettingsConfig.selectedDeviceName)) {
                    MidiDevice device = MidiSystem.getMidiDevice(info);

                    if (device.getMaxTransmitters() == 0) continue;

                    device.open();
                    activeDevice = device;
                    activeTransmitter = device.getTransmitter();
                    activeTransmitter.setReceiver(new MidiReceiver());
                    Blockstar.LOGGER.info("Listening to MIDI device: {}", info.getName());
                    return;
                }
            }
            Blockstar.LOGGER.warn("No matching MIDI input device found, not listening.");
        } catch (MidiUnavailableException e) {
            Blockstar.LOGGER.warn("No MIDI input devices found");
        }
    }


    public static void stopListening() {
        try {
            if (activeTransmitter != null) {
                activeTransmitter.close();
                activeTransmitter = null;
            }
            if (activeDevice != null) {
                activeDevice.close();
                activeDevice = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class MidiReceiver implements Receiver {
        @Override
        public void send(MidiMessage message, long timeStamp) {

            if (message instanceof ShortMessage shortMessage) {
                int command = shortMessage.getCommand();
                int data1 = shortMessage.getData1();
                int data2 = shortMessage.getData2();

                if (KeyboardScreen.getInstance() != null) {
                    if (command == ShortMessage.NOTE_ON && data2 > 0) {
                        KeyboardScreen.getInstance().playNoteFromMidi(data1, data2);
                    } else if (command == ShortMessage.NOTE_OFF || (command == ShortMessage.NOTE_ON && data2 == 0)) {
                        KeyboardScreen.getInstance().playNoteFromMidi(data1, 0);
                    } else if (command == ShortMessage.CONTROL_CHANGE && data1 == 64) {
                        KeyboardScreen.getInstance().handleSustainPedalFromMidi(data2 >= 64);
                    }
                }
            }
        }

        @Override
        public void close() {
        }
    }
}
