package com.ninni.blockstar.server.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
import com.ninni.blockstar.registry.BNetwork;
import com.ninni.blockstar.server.instrument.InstrumentType;
import com.ninni.blockstar.server.packet.SoundfontSyncPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.network.PacketDistributor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public class SoundfontManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private BiMap<ResourceLocation, SoundfontDefinition> soundfonts = ImmutableBiMap.of();

    public SoundfontManager() {
        super(GSON, "soundfonts");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableBiMap.Builder<ResourceLocation, SoundfontDefinition> builder = ImmutableBiMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
            try {
                SoundfontDefinition def = SoundfontDefinition.CODEC.parse(JsonOps.INSTANCE, entry.getValue())
                        .result()
                        .orElseThrow(() -> new JsonParseException("Invalid soundfont definition"));

                for (Map.Entry<InstrumentType, InstrumentSoundfontData> instrumentEntry : def.instrumentData().entrySet()) {
                    List<Integer> outOfRangeNotes = instrumentEntry.getValue().noteMap().stream()
                            .filter(note -> !instrumentEntry.getKey().isInRange(note))
                            .toList();
                    if (!outOfRangeNotes.isEmpty()) {
                        Blockstar.LOGGER.error("Soundfont '{}' has notes out of range for instrument '{}': {}",
                                entry.getKey(), BInstrumentTypeRegistry.get(instrumentEntry.getKey()), outOfRangeNotes);
                    }
                }

                builder.put(entry.getKey(), def);
            } catch (Exception e) {
                Blockstar.LOGGER.error("Failed to load soundfont '{}': {}", entry.getKey(), e.getMessage());
            }
        }

        this.soundfonts = builder.build();
        Blockstar.LOGGER.info("Loaded {} soundfonts", soundfonts.size());
    }

    public void onDatapackSync(@Nullable ServerPlayer player) {
        if (this.soundfonts.isEmpty()) return;
        BiMap<ResourceLocation, SoundfontDefinition> registryMap = HashBiMap.create(this.soundfonts);

        if (player == null) {
            BNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new SoundfontSyncPacket(registryMap));
        } else {
            BNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SoundfontSyncPacket(registryMap));
        }
    }

    public void synchronizeRegistryForClient(BiMap<ResourceLocation, SoundfontDefinition> newData) {
        this.soundfonts = ImmutableBiMap.copyOf(newData);
        Blockstar.LOGGER.info("Client applied {} soundfonts from server", this.soundfonts.size());
    }

    public SoundfontDefinition get(ResourceLocation id) {
        return soundfonts.get(id);
    }

    public ResourceLocation getLocation(SoundfontDefinition def) {
        return soundfonts.inverse().get(def);
    }

    public Collection<ResourceLocation> getAllInstrumentIds() {
        return soundfonts.keySet();
    }

    public Collection<SoundfontDefinition> getAll() {
        return soundfonts.values();
    }

    public record InstrumentSoundfontData(List<Integer> noteMap, Optional<Integer> velocityLayers, Optional<Integer> default_velocity, boolean held, int releaseTicks) {

        public static final Codec<InstrumentSoundfontData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.list(Codec.INT).fieldOf("note_map").forGetter(InstrumentSoundfontData::noteMap),
                Codec.INT.optionalFieldOf("velocity_layers").forGetter(InstrumentSoundfontData::velocityLayers),
                Codec.INT.optionalFieldOf("default_velocity").forGetter(InstrumentSoundfontData::default_velocity),
                Codec.BOOL.fieldOf("held").orElse(false).forGetter(InstrumentSoundfontData::held),
                Codec.INT.fieldOf("release_ticks").orElse(0).forGetter(InstrumentSoundfontData::releaseTicks)
        ).apply(inst, InstrumentSoundfontData::new));

        public int getClosestSampleNote(int targetNote) {
            return noteMap.stream().min(Comparator.comparingInt(n -> Math.abs(n - targetNote))).orElse(targetNote);
        }
    }

    public record SoundfontDefinition(Map<InstrumentType, InstrumentSoundfontData> instrumentData, ResourceLocation name, boolean creativeTab, Rarity rarity, TextColor color) {
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
                Codec.unboundedMap(BInstrumentTypeRegistry.CODEC, InstrumentSoundfontData.CODEC).fieldOf("instruments").forGetter(SoundfontDefinition::instrumentData),
                ResourceLocation.CODEC.fieldOf("name").forGetter(SoundfontDefinition::name),
                Codec.BOOL.fieldOf("creative_tab").orElse(true).forGetter(SoundfontDefinition::creativeTab),
                RARITY_CODEC.fieldOf("rarity").orElse(Rarity.COMMON).forGetter(SoundfontDefinition::rarity),
                TextColor.CODEC.fieldOf("color").orElse(TextColor.fromLegacyFormat(ChatFormatting.GRAY)).forGetter(SoundfontDefinition::color)
        ).apply(inst, SoundfontDefinition::new));

        public InstrumentSoundfontData getForInstrument(InstrumentType type) {
            return instrumentData.get(type);
        }
    }
}
