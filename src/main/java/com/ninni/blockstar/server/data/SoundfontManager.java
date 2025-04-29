package com.ninni.blockstar.server.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.registry.BInstrumentTypeRegistry;
import com.ninni.blockstar.server.intstrument.InstrumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Rarity;

import java.util.*;

public class SoundfontManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private BiMap<ResourceLocation, SoundfontDefinition> instruments = ImmutableBiMap.of();

    public SoundfontManager() {
        super(GSON, "soundfonts");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableBiMap.Builder<ResourceLocation, SoundfontDefinition> builder = ImmutableBiMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
            try {
                SoundfontDefinition def = SoundfontDefinition.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).result().orElseThrow(() -> new JsonParseException("Invalid soundfont definition"));

                List<Integer> outOfRangeNotes = def.noteMap().stream().filter(note -> !def.instrumentType().isInRange(note)).toList();
                if (!outOfRangeNotes.isEmpty()) {
                    Blockstar.LOGGER.error("Soundfont '{}' has notes out of range for instrument '{}': {}", entry.getKey(), BInstrumentTypeRegistry.get(def.instrumentType()), outOfRangeNotes);
                }

                builder.put(entry.getKey(), def);
            } catch (Exception e) {
                Blockstar.LOGGER.error("Failed to load soundfont '{}': {}", entry.getKey(), e.getMessage());
            }
        }

        this.instruments = builder.build();
        Blockstar.LOGGER.info("Loaded {} soundfonts", instruments.size());
    }

    public SoundfontDefinition get(ResourceLocation id) {
        return instruments.get(id);
    }

    public ResourceLocation getLocation(SoundfontDefinition id) {
        return instruments.inverse().get(id);
    }

    public Collection<ResourceLocation> getAllInstrumentIds() {
        return instruments.keySet();
    }

    public Collection<SoundfontDefinition> getAll() {
        return instruments.values();
    }

    public record SoundfontDefinition(InstrumentType instrumentType, boolean instrumentExclusive, ResourceLocation name, List<Integer> noteMap, Optional<Integer> velocityLayers, boolean held, int fadeTicks, boolean creativeTab, Rarity rarity, TextColor color) {
        public static final Codec<Rarity> RARITY_CODEC = Codec.STRING.xmap(
                string -> {
                    try {
                        return Rarity.valueOf(string.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Invalid rarity: " + string);
                    }
                },
                Rarity::name
        );

        public static final Codec<SoundfontDefinition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BInstrumentTypeRegistry.CODEC.fieldOf("instrument_type").forGetter(SoundfontDefinition::instrumentType),
                Codec.BOOL.fieldOf("instrument_exclusive").orElse(false).forGetter(SoundfontDefinition::instrumentExclusive),
                ResourceLocation.CODEC.fieldOf("name").forGetter(SoundfontDefinition::name),
                Codec.list(Codec.INT).fieldOf("note_map").forGetter(SoundfontDefinition::noteMap),
                Codec.INT.optionalFieldOf("velocity_layers").forGetter(SoundfontDefinition::velocityLayers),
                Codec.BOOL.fieldOf("held").orElse(false).forGetter(SoundfontDefinition::held),
                Codec.INT.fieldOf("fade_ticks").orElse(0).forGetter(SoundfontDefinition::fadeTicks),
                Codec.BOOL.fieldOf("creative_tab").orElse(true).forGetter(SoundfontDefinition::creativeTab),
                RARITY_CODEC.fieldOf("rarity").orElse(Rarity.COMMON).forGetter(SoundfontDefinition::rarity),
                TextColor.CODEC.fieldOf("color").orElse(TextColor.fromLegacyFormat(ChatFormatting.GRAY)).forGetter(SoundfontDefinition::color)
        ).apply(inst, SoundfontDefinition::new));

        public int getClosestSampleNote(int targetNote) {
            return noteMap.stream().min(Comparator.comparingInt(n -> Math.abs(n - targetNote))).orElse(targetNote);
        }
    }

}
