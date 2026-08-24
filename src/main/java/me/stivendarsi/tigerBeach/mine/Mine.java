package me.stivendarsi.tigerBeach.mine;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import me.stivendarsi.tigerBeach.mine.events.custom.MineFillEvent;
import org.bukkit.Location;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class Mine {
    private final String id;
    private boolean enableActionBar;
    private String actionBarMSG;
    private int actionBarRange;
    private Region region;
    private RandomPattern randomPattern = new RandomPattern();
    private Map<BlockType, MineBlock> mineBlocks = new HashMap<>();
    private double minimumRequiredWeight;
    private String requiredConversionGroupName;
    private double resetPeriod;
    private long blocksBroke;
    private int resetAtPercentX;
    private long lastResetTime;

    private Region rangedRegion;

    public String getId() {
        return this.id;
    }

    private Mine(String id, Map<BlockType, MineBlock> mineBlocks, Region mineSpace, boolean enableActionBar, int actionBarRange, String actionBarMSG, double requiredWeight, String requiredConversionGroupName, double resetDelay, int resetAtPercentX) {
        this.id = id;
        this.mineBlocks = mineBlocks;
        loadPattern();
        this.region = mineSpace;
        this.resetAtPercentX = resetAtPercentX;
        this.actionBarRange = actionBarRange;
        this.actionBarMSG = actionBarMSG;
        this.enableActionBar = enableActionBar;
        this.resetPeriod = resetDelay;
        this.minimumRequiredWeight = requiredWeight;
        this.requiredConversionGroupName = requiredConversionGroupName;
        this.rangedRegion = this.region.clone();
        this.rangedRegion.expand(BlockVector3.ONE.multiply(this.actionBarRange));
        this.rangedRegion.expand(BlockVector3.ONE.multiply(-this.actionBarRange));

        //  plugin().getLogger().info(String.valueOf(this.resetPeriod));
    }

    public Region getRegion() {
        return region;
    }

    public Map<BlockType, MineBlock> getMineBlocks() {
        return mineBlocks;
    }

    @Nullable
    public MineBlock getFirstEntry() {
        Optional<MineBlock> a = this.mineBlocks.values().stream().findFirst();
        return a.orElse(null);
    }

    public void startTimer() {
        plugin().getServer().getScheduler().runTaskTimer(plugin(), new MineTimerFixed(this.id), 0, (long) this.resetPeriod * 20);
    }

    public void fillMine() {
        MineFillEvent mineFillEvent = new MineFillEvent();
        if (mineFillEvent.callEvent()) {
            World world = this.region.getWorld();
            if (world == null) {
                plugin().getLogger().info("null world, returning..");
                return;
            }
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).fastMode(true).build()) {
                this.lastResetTime = System.currentTimeMillis();
                editSession.setBlocks(this.region, this.randomPattern);
                this.blocksBroke = 0;
            } catch (MaxChangedBlocksException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public int timeSecondsLeft(long currentTime) {
        long left = (this.lastResetTime + (long) (this.resetPeriod * 1000)) - currentTime;
        if (left <= 0) return 0;
        return Math.toIntExact(left / 1000);
    }

    public boolean playerInMineRange(Location loc) {
        BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(loc);
        return this.rangedRegion.contains(blockVector3);
    }


    public void countABlockBrake() {
        this.blocksBroke++;
        if (percentLeft() <= resetAtPercentX) fillMine();
    }

    public int percentLeft() {
        return (int) (((double) (this.region.getVolume() - this.blocksBroke) / (double) this.region.getVolume()) * 100);
    }

    public @Nullable ItemStack getRewardByBlock(BlockType blockType) {
        MineBlock mineBlock = this.mineBlocks.getOrDefault(blockType, null);
        if (mineBlock != null) {
            Reward reward = mineBlock.getRandomReward();
            if (reward != null) {
                return reward.getItem();
            }
        }
        return null;
    }

    public boolean containPosition(Location loc) {
        return this.region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public RandomPattern loadPattern() {
        this.randomPattern = new RandomPattern();
        this.mineBlocks.forEach((blockType, mineBlock) -> {
            BlockState block = BukkitAdapter.adapt(blockType.createBlockData());
            this.randomPattern.add(block, mineBlock.getBlockPrecent());
        });
        return this.randomPattern;
    }

    public double getMinimumRequiredWeight() {
        return minimumRequiredWeight;
    }


    public double resetDelay() {
        return resetPeriod;
    }

    public int resetAtPercentX() {
        return resetAtPercentX;
    }

    public String getRequiredConversionGroupName() {
        return requiredConversionGroupName;
    }

    public boolean meetRequirements(String itemDefinitionGroupName, double itemWeight) {
        return this.requiredConversionGroupName.equalsIgnoreCase(itemDefinitionGroupName) && itemWeight >= this.minimumRequiredWeight;
    }

    public boolean isActionBarEnabled() {
        return enableActionBar;
    }

    public String getActionBarMSG() {
        return actionBarMSG;
    }

    public int getActionBarRange() {
        return actionBarRange;
    }

    public static Builder mineBuilder(String id, Region region) {
        return new Builder(id, region);
    }

    public Mine setActionBarRange(int actionBarRange) {
        this.actionBarRange = actionBarRange;
        this.rangedRegion = this.region.clone();
        this.rangedRegion.expand(BlockVector3.ONE.multiply(this.actionBarRange));
        return this;
    }

    public static class Builder {
        private final String id;
        private boolean enableActionBar = true;
        private String actionBarMSG = "<green> זמן שנשאר: <gold><time_left></gold> | נשארו <percent_left>";
        private int actionBarRange = 5;
        private double resetDelay = 5;
        private int resetAtPercentX = 10;

        private Region region;
        private Map<BlockType, MineBlock> mineBlocks = new HashMap<>();
        private double requiredWeight = 0;
        private String requiredConversionGroupName = "pickaxe";

        private Builder(String id, Region region) {
            this.id = id;
            this.region = region;
        }

        public Builder requiredWeight(double requiredWeight) {
            this.requiredWeight = requiredWeight;
            return this;
        }

        public Builder requiredConversionGroupName(String requiredProgressionGroup) {
            this.requiredConversionGroupName = requiredProgressionGroup;
            return this;
        }

        public Builder resetAtPercentX(int resetAtPercentX) {
            this.resetAtPercentX = resetAtPercentX;
            return this;
        }

        public Builder actionBar(boolean enableActionBar) {
            this.enableActionBar = enableActionBar;
            return this;
        }

        public Builder actionBarMSG(String actionBarMSG) {
            this.actionBarMSG = actionBarMSG;
            return this;
        }

        public Builder actionBarRange(int actionBarRange) {
            this.actionBarRange = actionBarRange;
            return this;
        }

        public Builder resetDelay(double resetDelay) {
            this.resetDelay = resetDelay;
            return this;
        }

        public Builder addMineBlock(MineBlock mineBlock) {
            this.mineBlocks.put(mineBlock.getBlockType(), mineBlock);
            return this;
        }


        public Mine build() {
            return new Mine(id, mineBlocks, region, enableActionBar, actionBarRange, actionBarMSG, requiredWeight, requiredConversionGroupName, resetDelay, resetAtPercentX);
        }
    }
}
