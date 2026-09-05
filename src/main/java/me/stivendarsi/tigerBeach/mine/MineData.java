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

import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class MineData {
    private final String id;
    private final boolean enableActionBar;
    private String actionBarMSG = "<green> זמן שנשאר: <gold><time_left></gold> | נשארו <percent_left>";
    private int actionBarRange;
    private final Region mineBoundingBox;
    private RandomPattern randomPattern = new RandomPattern();
    private Map<BlockType, MineBlock> mineBlocks = new HashMap<>();
    private final double minimumRequiredWeight;
    private final String requiredConversionGroupName;
    private final double resetPeriod;
    private final int resetAtPercentX;
    private long blocksBroke;
    private long lastResetTime;

    private Region rangedRegion;

    public String getId() {
        return this.id;
    }

    public MineData(String id, Map<BlockType, MineBlock> mineBlocks, Region mineBoundingBox, boolean enableActionBar, int actionBarRange, String actionBarMSG, double requiredWeight, String requiredConversionGroupName, double resetDelay, int resetAtPercentX) {
        this.id = id;
        this.mineBlocks = mineBlocks;
        loadPattern();
        this.mineBoundingBox = mineBoundingBox;
        this.resetAtPercentX = resetAtPercentX;
        this.actionBarRange = actionBarRange;
        this.actionBarMSG = actionBarMSG;
        this.enableActionBar = enableActionBar;
        this.resetPeriod = resetDelay;
        this.minimumRequiredWeight = requiredWeight;
        this.requiredConversionGroupName = requiredConversionGroupName;
        this.rangedRegion = this.mineBoundingBox.clone();
        this.rangedRegion.expand(BlockVector3.ONE.multiply(this.actionBarRange));
        this.rangedRegion.expand(BlockVector3.ONE.multiply(-this.actionBarRange));
    }

    public static MineData defaultMine(String mineIdentifier, Region mineBoundingBox) {
        Map<BlockType, MineBlock> blockMap = new HashMap<>();
        MineBlock mineBlock = MineBlock.blockBuilder(BlockType.BEDROCK).setBlockPrecent(100).build();
        blockMap.put(mineBlock.getBlockType(), mineBlock);

        boolean enableActionBar = true;
        String actionBarMSG = "<green> זמן שנשאר: <gold><time_left></gold> | נשארו <percent_left>";
        int actionBarRange = 5;
        double resetDelay = 5;
        int resetAtPercentX = 10;

        double requiredWeight = 0;
        String requiredConversionGroupName = "pickaxe";

        return new MineData(mineIdentifier, blockMap, mineBoundingBox, enableActionBar, actionBarRange, actionBarMSG, requiredWeight, requiredConversionGroupName, resetDelay, resetAtPercentX);
    }


    public Region getMineBoundingBox() {
        return mineBoundingBox;
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
        tigerBeachInstance().getServer().getScheduler().runTaskTimer(tigerBeachInstance(), new MineTimerFixed(this.id), 0, (long) this.resetPeriod * 20);
    }

    public void fillMine() {
        MineFillEvent mineFillEvent = new MineFillEvent();
        if (mineFillEvent.callEvent()) {
            World world = this.mineBoundingBox.getWorld();
            if (world == null) return;

            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).fastMode(true).build()) {
                this.lastResetTime = System.currentTimeMillis();
                editSession.setBlocks(this.mineBoundingBox, this.randomPattern);
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
        return this.rangedRegion.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }


    public void countABlockBrake() {
        this.blocksBroke++;
        if (percentLeft() <= resetAtPercentX) fillMine();
    }

    public int percentLeft() {
        return (int) (((double) (this.mineBoundingBox.getVolume() - this.blocksBroke) / (double) this.mineBoundingBox.getVolume()) * 100);
    }

    public @Nullable ItemStack getRewardByBlock(BlockType blockType) {
        MineBlock mineBlock = this.mineBlocks.getOrDefault(blockType, null);
        if (mineBlock == null) return null;
        Reward reward = mineBlock.getRandomReward();
        if (reward == null) return null;

        return reward.getItem();
    }

    public boolean containPosition(Location loc) {
        return this.mineBoundingBox.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public void loadPattern() {
        this.randomPattern = new RandomPattern();
        this.mineBlocks.forEach((blockType, mineBlock) -> {
            BlockState block = BukkitAdapter.adapt(blockType.createBlockData());
            this.randomPattern.add(block, mineBlock.getBlockPrecent());
        });
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

//      public static Builder mineBuilder(String id, Region region) {
//        return new Builder(id, region);
//    }

    public MineData setActionBarRange(int actionBarRange) {
        this.actionBarRange = actionBarRange;
        this.rangedRegion = this.mineBoundingBox.clone();
        this.rangedRegion.expand(BlockVector3.ONE.multiply(this.actionBarRange));
        return this;
    }

//    public static class Builder {
//        private final String id;
//        private boolean enableActionBar = true;
//        private String actionBarMSG = "<green> זמן שנשאר: <gold><time_left></gold> | נשארו <percent_left>";
//        private int actionBarRange = 5;
//        private double resetDelay = 5;
//        private int resetAtPercentX = 10;
//
//        private Region region;
//        private Map<BlockType, MineBlock> mineBlocks = new HashMap<>();
//        private double requiredWeight = 0;
//        private String requiredConversionGroupName = "pickaxe";
//
//        private Builder(String id, Region region) {
//            this.id = id;
//            this.region = region;
//        }
//
//        public Builder requiredWeight(double requiredWeight) {
//            this.requiredWeight = requiredWeight;
//            return this;
//        }
//
//        public Builder requiredConversionGroupName(String requiredProgressionGroup) {
//            this.requiredConversionGroupName = requiredProgressionGroup;
//            return this;
//        }
//
//        public Builder resetAtPercentX(int resetAtPercentX) {
//            this.resetAtPercentX = resetAtPercentX;
//            return this;
//        }
//
//        public Builder actionBar(boolean enableActionBar) {
//            this.enableActionBar = enableActionBar;
//            return this;
//        }
//
//        public Builder actionBarMSG(String actionBarMSG) {
//            this.actionBarMSG = actionBarMSG;
//            return this;
//        }
//
//        public Builder actionBarRange(int actionBarRange) {
//            this.actionBarRange = actionBarRange;
//            return this;
//        }
//
//        public Builder resetDelay(double resetDelay) {
//            this.resetDelay = resetDelay;
//            return this;
//        }
//
//        public Builder addMineBlock(MineBlock mineBlock) {
//            this.mineBlocks.put(mineBlock.getBlockType(), mineBlock);
//            return this;
//        }
//
//
//        public MineData build() {
//            return new MineData(id, mineBlocks, region, enableActionBar, actionBarRange, actionBarMSG, requiredWeight, requiredConversionGroupName, resetDelay, resetAtPercentX);
//        }
//    }
}
