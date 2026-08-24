package me.stivendarsi.tigerBeach.mine;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.stivendarsi.tigerBeach.TigerBeach;
import me.stivendarsi.tigerBeach.utility.YamlConfigFile;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.*;

import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class MinesSystemHandler extends YamlConfigFile {
    private final Map<String, Mine> mineMap = new HashMap<>();
    private final Map<UUID, ActionBarTimer> activeActionbars = new HashMap<>();
    private final Set<UUID> requirementsBypass = new HashSet<>();

    public MinesSystemHandler() {
        super(new File(TigerBeach.plugin().getDataFolder(), "mines.yml"));
    }

    @Nullable
    public Mine getMine(String mineId) {
        return this.mineMap.getOrDefault(mineId, null);
    }

    public void stopTimers() {
       // plugin().getLogger().info("cancelling timers");
        plugin().getServer().getScheduler().cancelTasks(plugin());
        this.activeActionbars.forEach((uuid, actionBarTimer) -> actionBarTimer.stop());
    }


    public Map<String, Mine> getMineMap() {
        return mineMap;
    }

    public void activatePlayerActionBar(UUID uuid, Mine mine) {
        if (mine.isActionBarEnabled()) {
            ActionBarTimer barTimer = this.activeActionbars.getOrDefault(uuid, null);
            if (barTimer != null) barTimer.stop();
            ActionBarTimer actionBarTimer = new ActionBarTimer(mine, uuid);
            actionBarTimer.start();
            this.activeActionbars.put(uuid, actionBarTimer);
        }
    }

    public void addPlayerRequirementsBypass(UUID player) {
        this.requirementsBypass.add(player);
    }

    public boolean hasPlayerRequirementsBypass(UUID player) {
        return this.requirementsBypass.contains(player);
    }

    public void removePlayerRequirementsBypass(UUID player) {
        this.requirementsBypass.remove(player);
    }

    public void loadMines() {
        stopTimers();
        this.mineMap.clear();
        reload();

        for (String mineId : this.get().getKeys(false)) {
            ConfigurationSection mineSection = this.get().getConfigurationSection(mineId);
            if (mineSection == null) continue;

            CuboidRegion region = loadRegion(mineSection);
            Mine.Builder mineBuilder = Mine.mineBuilder(mineId, region);

            if (mineSection.contains("actionbar")) {
                boolean enableActionBar = mineSection.getBoolean("actionbar.enabled");
                int range = mineSection.getInt("actionbar.range");
                if (mineSection.contains("actionbar.message")) {
                    String msg = mineSection.getString("actionbar.message");
                    mineBuilder.actionBarMSG(msg);
                }
                mineBuilder.actionBarRange(range);
                mineBuilder.actionBar(enableActionBar);
            }

            if (mineSection.contains("requirements.weight"))
                mineBuilder.requiredWeight(mineSection.getDouble("requirements.weight"));

            if (mineSection.contains("requirements.item_definition_group"))
                mineBuilder.requiredConversionGroupName(mineSection.getString("requirements.item_definition_group"));

            if (mineSection.contains("reset")) {
                double seconds = mineSection.getDouble("reset.seconds", 5);
                int percent = mineSection.getInt("reset.percent", 10);
                mineBuilder.resetDelay(seconds);
                mineBuilder.resetAtPercentX(percent);
            }


            ConfigurationSection blocksSection = mineSection.getConfigurationSection("blocks");
            if (blocksSection != null) {
                for (String blockTypeName : blocksSection.getKeys(false)) {
                    String key = blockTypeName.toLowerCase(Locale.ROOT);
                    BlockType blockType = Registry.BLOCK.get(Key.key(key));
                    MineBlock.Builder blockBuilder = MineBlock.blockBuilder(blockType);
                    int blockPercent = blocksSection.getInt("%s.percent".formatted(blockTypeName));
                    blockBuilder.setBlockPrecent(blockPercent);

                    List<Map<?, ?>> rewardsList = blocksSection.getMapList("%s.rewards".formatted(blockTypeName));
                    for (Map<?, ?> rawReward : rewardsList) {
                        YamlConfiguration rewardSection = new YamlConfiguration();
                        rewardSection.addDefaults((Map<String, Object>) rawReward);
                        rewardSection.options().copyDefaults(true);
                        Reward reward = loadReward(rewardSection);
                        blockBuilder.addReward(reward);
                    }

                    mineBuilder.addMineBlock(blockBuilder.build());
                }
            }

            this.mineMap.put(mineId, mineBuilder.build());
        }

        startMines();
    }


    public void startMines() {
        for (Mine value : this.mineMap.values()) {
            value.startTimer();
        }
    }


    private Reward loadReward(ConfigurationSection section) {
        String itemDefinitionId = section.getString("item_definition_id");
        int amount = section.getInt("amount");
        int chance = section.getInt("chance");
        String itemDefinitionGroupName = section.getString("item_definition_group", "mineral");
        return new Reward(itemDefinitionGroupName, itemDefinitionId, amount, chance);
    }

    private CuboidRegion loadRegion(ConfigurationSection section) {
        int x1 = section.getInt("region.1.x");
        int y1 = section.getInt("region.1.y");
        int z1 = section.getInt("region.1.z");

        int x2 = section.getInt("region.2.x");
        int y2 = section.getInt("region.2.y");
        int z2 = section.getInt("region.2.z");

        BlockVector3 pos1 = BlockVector3.at(x1, y1, z1);
        BlockVector3 pos2 = BlockVector3.at(x2, y2, z2);

        String worldName = section.getString("region.world", "");
        World world = Bukkit.getWorld(worldName);
        if (world == null) throw new RuntimeException("Invalid world: " + worldName);
        return new CuboidRegion(BukkitAdapter.adapt(world), pos1, pos2);
    }


    public void registerMine(Mine mine) {
        String id = mine.getId();
        BlockVector3 max = mine.getRegion().getMaximumPoint();
        get().set("%s.region.1.x".formatted(id), max.x());
        get().set("%s.region.1.y".formatted(id), max.y());
        get().set("%s.region.1.z".formatted(id), max.z());

        BlockVector3 min = mine.getRegion().getMinimumPoint();
        get().set("%s.region.2.x".formatted(id), min.x());
        get().set("%s.region.2.y".formatted(id), min.y());
        get().set("%s.region.2.z".formatted(id), min.z());

        com.sk89q.worldedit.world.World world = mine.getRegion().getWorld();
        if (world != null) get().set("%s.region.world".formatted(id), world.getName());

// requirements
        get().set("%s.requirements.weight".formatted(id), mine.getMinimumRequiredWeight());
        get().set("%s.requirements.conversion_group".formatted(id), mine.getRequiredConversionGroupName());

// ActionBar
        get().set("%s.actionbar.enabled".formatted(id), mine.isActionBarEnabled());
        get().set("%s.actionbar.range".formatted(id), mine.getActionBarRange());
        get().set("%s.actionbar.message".formatted(id), mine.getActionBarMSG());

        get().set("%s.reset.seconds".formatted(id), mine.resetDelay());
        get().set("%s.reset.percent".formatted(id), mine.resetAtPercentX());


        mine.getMineBlocks().forEach((blockType, mineBlock) -> {
            String name = blockType.key().asString();
            get().set("%s.blocks.%s.percent".formatted(id, name), mineBlock.getBlockPrecent());

            List<Map<?, ?>> rewardsList = new ArrayList<>();
            for (Reward reward : mineBlock.getRewards()) {
                Map<String, Object> rewardMap = new HashMap<>();
                rewardMap.put("item_definition_group", reward.itemDefinitionGroupName());
                rewardMap.put("chance", reward.getChance());
                rewardMap.put("item_definition_id", reward.itemDefinitionId());
                rewardMap.put("amount", reward.getAmount());
                rewardsList.add(rewardMap);
            }

            get().set("%s.blocks.%s.rewards".formatted(id, name), rewardsList);
        });


        save();

        this.mineMap.put(mine.getId(), mine);
        mine.startTimer();
    }

    @Nullable
    public Mine getMineByPosition(Location location) {
        for (Mine mine : this.mineMap.values()) {
            if (mine.containPosition(location)) return mine;
        }
        return null;

    }
}
