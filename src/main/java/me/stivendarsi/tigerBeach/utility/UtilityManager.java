package me.stivendarsi.tigerBeach.utility;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class UtilityManager {
    private String joinScreenText;
    private String reloadCommandMessage;
    private final TreeMap<Integer, String> rarityMap = new TreeMap<>();

    private final Map<String, RegistryKeySet<BlockType>> blockTags = new HashMap<>();

    private boolean debug;

    public void load() {
        this.reloadCommandMessage = plugin().getConfig().getString("messages.reload");
        this.joinScreenText = plugin().getConfig().getString("join_screen");

        this.debug = plugin().getConfig().getBoolean("debug");

        for (String rarityName : plugin().getConfig().getConfigurationSection("rarity").getKeys(false)) {
            String rarityString = plugin().getConfig().getString("rarity.%s".formatted(rarityName));
            this.rarityMap.put(Integer.valueOf(rarityName), rarityString);
        }
        for (String tag : plugin().getConfig().getConfigurationSection("tool_block_tags").getKeys(false)) {
            List<String> blockIds = plugin().getConfig().getStringList("tool_block_tags.%s".formatted(tag));

            Registry<BlockType> blockTypes = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK);

            List<BlockType> blockTypeList = blockIds.stream().map(s -> blockTypes.getOrThrow(Key.key(s))).toList();
            RegistryKeySet<BlockType> keys = RegistrySet.keySetFromValues(RegistryKey.BLOCK, blockTypeList);
            this.blockTags.put(tag, keys);
        }
    }

    public @Nullable RegistryKeySet<BlockType> getBlockSet(String tag) {
        return this.blockTags.getOrDefault(tag, RegistrySet.keySet(RegistryKey.BLOCK));
    }

    public boolean debug() {
        return debug;
    }

    public String getRarity(int average) {
        Integer key = rarityMap.floorKey(average);
        if (key == null && !rarityMap.isEmpty()) {
            key = rarityMap.firstKey();
        }

        return key != null ? rarityMap.get(key) : "אחי צלם לצוות או משהו";
    }


    public String reloadCommandMessage() {
        return this.reloadCommandMessage;
    }

    public String joinScreenText() {
        return this.joinScreenText;
    }

}
