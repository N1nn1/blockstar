package com.ninni.minestrel.server.soundfont;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ninni.minestrel.Minestrel;
import com.ninni.minestrel.registry.MSoundEventRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SoundfontManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    private final Map<ResourceLocation, SoundfontDefinition> instruments = new HashMap<>();

    public SoundfontManager() {
        super(GSON, "soundfonts");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        instruments.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
            try {
                SoundfontDefinition def = SoundfontDefinition.CODEC.parse(JsonOps.INSTANCE, entry.getValue())
                        .result()
                        .orElseThrow(() -> new JsonParseException("Invalid soundfont definition"));

                instruments.put(entry.getKey(), def);

            } catch (Exception e) {
                Minestrel.LOGGER.error("Failed to load soundfont '{}': {}", entry.getKey(), e.getMessage());
            }
        }

        Minestrel.LOGGER.info("Loaded {} soundfonts", instruments.size());
    }

    public SoundfontDefinition get(ResourceLocation id) {
        return instruments.get(id);
    }

    public Collection<ResourceLocation> getAllInstrumentIds() {
        return instruments.keySet();
    }

    public Collection<SoundfontDefinition> getAll() {
        return instruments.values();
    }


    public record SoundfontDefinition(ResourceLocation soundPrefix, Map<String, String> noteMapRaw, int velocityLayers, boolean sustain) {

        public static final Codec<SoundfontDefinition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("sound_prefix").forGetter(SoundfontDefinition::soundPrefix),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("note_map").forGetter(SoundfontDefinition::noteMapRaw),
                Codec.INT.fieldOf("velocity_layers").forGetter(SoundfontDefinition::velocityLayers),
                Codec.BOOL.fieldOf("sustain").forGetter(SoundfontDefinition::sustain)
        ).apply(inst, SoundfontDefinition::new));

        public Map<Integer, String> noteMap() {
            return noteMapRaw.entrySet().stream().collect(Collectors.toMap(e -> Integer.parseInt(e.getKey()), Map.Entry::getValue));
        }

        public int getClosestSampleNote(int targetNote) {
            return noteMap().keySet().stream().min(Comparator.comparingInt(n -> Math.abs(n - targetNote))).orElse(targetNote);
        }
    }

}