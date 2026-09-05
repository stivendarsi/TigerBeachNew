package me.stivendarsi.tigerBeach.mine;

import org.bukkit.block.BlockType;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MineBlock {
    private final BlockType blockType;
    private List<Reward> rewards;
    private int blockPrecent;
    private final static Random random = new Random();

    private MineBlock(BlockType blockType, List<Reward> rewards, int blockPrecent) {
        this.blockType = blockType;
        this.rewards = rewards;
        this.blockPrecent = blockPrecent;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public @Nullable Reward getRandomReward() {
        if (rewards.isEmpty()) return null;
        Reward selected = null;
        int totalChance = 0;
        for (Reward reward : rewards) {
            int chance = reward.getChance();
            totalChance += chance;
            if (random.nextInt(0, totalChance) < chance) {
                selected = reward;
            }
        }

        return selected;
    }

    public int getBlockPrecent() {
        return blockPrecent;
    }

    public static Builder blockBuilder(BlockType blockType) {
        return new Builder(blockType);
    }

    public static class Builder {
        private final BlockType blockType;
        private List<Reward> rewards = new ArrayList<>();
        private int blockPrecent;

        public Builder(BlockType blockType) {
            this.blockType = blockType;
        }

        public Builder setBlockPrecent(int blockPrecent) {
            this.blockPrecent = blockPrecent;
            return this;
        }

        public Builder addReward(Reward reward) {
            this.rewards.add(reward);
            return this;
        }

        public MineBlock build() {
            return new MineBlock(this.blockType, this.rewards, this.blockPrecent);
        }
    }
}
