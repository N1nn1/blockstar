package com.ninni.blockstar.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ninni.blockstar.Blockstar;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MidiSettingsConfig {
    public static String selectedDeviceName = "";
    public static float pressureSensitivity = 0.5f;
    public static String buttonX = "8";
    public static String buttonY = "6";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(Minecraft.getInstance().gameDirectory, "config/blockstar_midi_options.json");

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new ConfigData(selectedDeviceName, pressureSensitivity, buttonX, buttonY), writer);
        } catch (IOException e) {
            Blockstar.LOGGER.error("Failed to save Blockstar MIDI config");
        }
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) return;

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                selectedDeviceName = data.selectedDeviceName;
                pressureSensitivity = data.pressureSensitivity;
                buttonX = data.buttonX;
                buttonY = data.buttonY;
            }
        } catch (IOException e) {
            Blockstar.LOGGER.error("Failed to load Blockstar MIDI config");
        }
    }


    public static int parse(String expr) {
        expr = expr.replaceAll("\\s+", "");

        if (expr.contains("+")) {
            String[] parts = expr.split("\\+");
            return Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
        } else if (expr.contains("-")) {
            String[] parts = expr.split("-");
            return Integer.parseInt(parts[0]) - Integer.parseInt(parts[1]);
        } else {
            return Integer.parseInt(expr);
        }
    }

    private static class ConfigData {
        String selectedDeviceName;
        float pressureSensitivity;
        String buttonX;
        String buttonY;

        ConfigData(String selectedDeviceName, float pressureSensitivity, String buttonX, String buttonY) {
            this.selectedDeviceName = selectedDeviceName;
            this.pressureSensitivity = pressureSensitivity;
            this.buttonX = buttonX;
            this.buttonY = buttonY;
        }
    }
}
