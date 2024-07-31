package nl.teamdiopside.separatedleaves;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.architectury.platform.Platform;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class Reload {

    public record LeavesRule(Set<Block> leaves, Set<Block> logs) {}
    public record JsonFile(ResourceLocation key, JsonElement json) {}

    public static final List<LeavesRule> LEAVES_RULES = new ArrayList<>();
    public static final Set<String> BIOMES = new HashSet<>();
    public static final Set<String> BIOME_NAMESPACES = new HashSet<>();

    public static void reload(ResourceManager resourceManager) {
        apply(getJsons(resourceManager));
    }

    public static void apply(Map<ResourceLocation, JsonElement> jsons) {
        LEAVES_RULES.clear();
        List<LeavesRule> rules = new ArrayList<>();

        List<JsonFile> files = new ArrayList<>();
        jsons.forEach((key, json) -> files.add(new JsonFile(key, json)));
        files.sort(Comparator.comparing(jsonFile -> jsonFile.key().toString()));

        for (JsonFile file : files) {
            ResourceLocation key = file.key();
            JsonElement json = file.json();

            // Skip unloaded mods
            if (!Platform.getModIds().contains(key.getNamespace())) {
                continue;
            }

            // biomes.json
            if (key.getPath().equals("biomes")) {
                try {
                    if (json.getAsJsonObject().get("all").getAsBoolean()) {
                        BIOME_NAMESPACES.add(key.getNamespace());
                    } else {
                        json.getAsJsonObject().get("biomes").getAsJsonArray().forEach(jsonElement -> BIOMES.add(jsonElement.getAsString()));
                    }
                } catch (Exception e) {
                    SeparatedLeaves.LOGGER.error("Failed to parse {}'s biomes.json for Separated Leaves, Error: {}", key.getNamespace(), e);
                }

                continue;
            }

            // Leaves rule
            try {
                Set<Block> leaves = getBlocks(key, json, "leaves");
                Set<Block> logs = getBlocks(key, json, "logs");

                if (!leaves.isEmpty() && !logs.isEmpty()) {
                    rules.add(new LeavesRule(leaves, logs));
                    SeparatedLeaves.LOGGER.info("Loaded Separated Leaves file {}", key);
                }
            } catch (Exception e) {
                SeparatedLeaves.LOGGER.error("Failed to parse JSON object for leaves rule {}.json, Error: {}", key, e);
            }
        }

        LEAVES_RULES.addAll(rules);
    }

    public static Set<Block> getBlocks(ResourceLocation key, JsonElement json, String string) {
        Set<Block> blocks = new HashSet<>();
        for (JsonElement jsonElement : json.getAsJsonObject().get(string).getAsJsonArray()) {
            if (jsonElement.getAsString().startsWith("#")) {
                TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, ResourceLocation.parse(jsonElement.getAsString().replace("#", "")));
                for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getOrCreateTag(blockTagKey)) {
                    blocks.add(blockHolder.value());
                }
            } else {
                Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(jsonElement.getAsString()));
                if (block == Blocks.AIR && !jsonElement.getAsString().replace("minecraft:", "").equals("air")) {
                    SeparatedLeaves.LOGGER.error("Block \"{}\" from {} does not exist!", jsonElement.getAsString(), key);
                } else {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static Map<ResourceLocation, JsonElement> getJsons(ResourceManager resourceManager) {
        String directory = "separated_leaves";
        Gson gson = new Gson();
        HashMap<ResourceLocation, JsonElement> map = Maps.newHashMap();
        SimpleJsonResourceReloadListener.scanDirectory(resourceManager, directory, gson, map);
        return map;
    }
}
