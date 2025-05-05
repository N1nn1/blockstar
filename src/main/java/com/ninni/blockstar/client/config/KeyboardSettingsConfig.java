package com.ninni.blockstar.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ninni.blockstar.Blockstar;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KeyboardSettingsConfig {
    public static Map<Integer, Integer> keyToNote = new HashMap<>();
    public static int octaveUpKey = GLFW.GLFW_KEY_UP;
    public static int octaveDownKey = GLFW.GLFW_KEY_DOWN;
    public static int pedalKey = GLFW.GLFW_KEY_SPACE;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(Minecraft.getInstance().gameDirectory, "config/blockstar_keyboard_keybinds.json");

    static {
        keyToNote.put(90, 48);
        keyToNote.put(88, 50);
        keyToNote.put(67, 52);
        keyToNote.put(86, 53);
        keyToNote.put(66, 55);
        keyToNote.put(78, 57);
        keyToNote.put(77, 59);
        keyToNote.put(44, 60);
        keyToNote.put(46, 62);
        keyToNote.put(47, 64);
        keyToNote.put(83, 49);
        keyToNote.put(68, 51);
        keyToNote.put(71, 54);
        keyToNote.put(72, 56);
        keyToNote.put(74, 58);
        keyToNote.put(76, 61);
        keyToNote.put(39, 63);
        keyToNote.put(81, 65);
        keyToNote.put(50, 66);
        keyToNote.put(87, 67);
        keyToNote.put(51, 68);
        keyToNote.put(69, 69);
        keyToNote.put(52, 70);
        keyToNote.put(82, 71);
        keyToNote.put(84, 72);
        keyToNote.put(54, 73);
        keyToNote.put(89, 74);
        keyToNote.put(55, 75);
        keyToNote.put(85, 76);
        keyToNote.put(73, 77);
        keyToNote.put(57, 78);
        keyToNote.put(79, 79);
        keyToNote.put(48, 80);
        keyToNote.put(80, 81);
        keyToNote.put(45, 82);
        keyToNote.put(91, 83);
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new ConfigData(keyToNote, octaveUpKey, octaveDownKey, pedalKey), writer);
        } catch (IOException e) {
            Blockstar.LOGGER.error("Failed to save keyboard config", e);
        }
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) return;

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                keyToNote = data.keyToNote;
                octaveUpKey = data.octaveUpKey;
                octaveDownKey = data.octaveDownKey;
                pedalKey = data.pedalKey;
            }
        } catch (IOException e) {
            Blockstar.LOGGER.error("Failed to load keyboard config", e);
        }
    }

    private static class ConfigData {
        Map<Integer, Integer> keyToNote;
        int octaveUpKey;
        int octaveDownKey;
        int pedalKey;

        ConfigData(Map<Integer, Integer> keyToNote, int octaveUpKey, int octaveDownKey, int pedalKey) {
            this.keyToNote = keyToNote;
            this.octaveUpKey = octaveUpKey;
            this.octaveDownKey = octaveDownKey;
            this.pedalKey = pedalKey;
        }
    }
}


