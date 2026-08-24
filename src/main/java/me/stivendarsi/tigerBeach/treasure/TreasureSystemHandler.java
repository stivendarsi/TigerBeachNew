package me.stivendarsi.tigerBeach.treasure;

import me.stivendarsi.tigerBeach.utility.YamlConfigFile;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.HeightMap;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.*;

import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class TreasureSystemHandler extends YamlConfigFile {
    private Set<BlockVector> blockLocations = new HashSet<>();

    private Map<UUID, DigProgression> playerProression = new HashMap<>();

    public TreasureSystemHandler() {
        super(new File(plugin().getDataFolder(), "treasure.yml"));
    }

    private BoundingBox treasureSpawnBoundingBox;
    private int chestAmount = 5;

    public void load() {
        int x1 = get().getInt("bounding_box.1.x");
        int x2 = get().getInt("bounding_box.2.x");

        int z1 = get().getInt("bounding_box.1.z");
        int z2 = get().getInt("bounding_box.2.z");


        treasureSpawnBoundingBox = new BoundingBox(x1, 0, z1, x2, 0, z2);
    }

    public void detectBlock(Block clickedBlock, Player player) {
        BoundingBox clickedBlockBoundingBox = clickedBlock.getBoundingBox();
               for (BlockVector block : blockLocations) {

            BoundingBox boundingBox = new BoundingBox(
                    block.getX(), block.getY(), block.getZ(),
                    block.getX() + 1, block.getY() + 1, block.getZ() + 1
            );
            for (int distance = 0; distance < 4; distance++) {
                BoundingBox clone = boundingBox.clone();
                clone.expand(distance * 2);
                if (clone.overlaps(clickedBlockBoundingBox)) {
                    player.playSound(treasureSoundByDistance(distance * 2));
                    return;
                }
            }
        }
    }

    public void dig(Block block, Player player) {
        @Nullable DigProgression digProgression = this.playerProression.getOrDefault(player.getUniqueId(), null);
        BlockVector blockVector = block.getLocation().toVector().toBlockVector();
        float prog = 0f;
        if (digProgression != null) {
            prog += digProgression.digProgression();
            if (!digProgression.block().equals(blockVector)) {
                player.sendBlockDamage(digProgression.block().toLocation(block.getWorld()), 0);
                this.playerProression.remove(player.getUniqueId());
                return;
            }

            if (prog >= 1.0f) {
                block.breakNaturally();
                player.sendBlockDamage(digProgression.block().toLocation(block.getWorld()), 0);
                this.playerProression.remove(player.getUniqueId());
                this.blockLocations.remove(blockVector);
                return;
            }
        }
        if (this.blockLocations.contains(blockVector)) {
            player.getInventory().addItem(ItemType.DIAMOND.createItemStack());
            prog += 0.2f;
            player.sendBlockDamage(block.getLocation(), prog);
            this.playerProression.put(player.getUniqueId(), new DigProgression(prog, blockVector));
        }
    }

    private Sound treasureSoundByDistance(int distance) {
        Sound.Builder builder = Sound.sound().volume(1).source(Sound.Source.PLAYER).type(Key.key("block.note_block.bit"));
        if (distance < 1) return builder.pitch(1.5f).build();
        if (distance < 4) return builder.pitch(1f).build();
        if (distance < 8) return builder.pitch(0.5f).build();
        return builder.volume(0).build();
    }

    public void generateChests(Player player, World world) {
        blockLocations.clear();
        List<BlockVector> blockVectors = getBlockVectors(this.treasureSpawnBoundingBox.getMin(), this.treasureSpawnBoundingBox.getMax());
        Random random = new Random();
        int chestCount = 0;
        while (chestCount < this.chestAmount && !blockVectors.isEmpty()) {
            int index = random.nextInt(0, blockVectors.size());
            BlockVector blockVector = blockVectors.get(index);
            Block block = world.getHighestBlockAt(blockVector.getBlockX(), blockVector.getBlockZ(), HeightMap.WORLD_SURFACE);
            blockVectors.remove(blockVector); //Remove to prevent doubles

            BlockType blockType = block.getType().asBlockType();
            if (blockType == BlockType.SAND || blockType == BlockType.SUSPICIOUS_SAND) {
                Component component = Component.text("%d %d %d ".formatted(block.getX(), block.getY(), block.getZ()))
                        .clickEvent(ClickEvent.runCommand("/tp @p %d %d %d".formatted(block.getX(), block.getY(), block.getZ()))).color(NamedTextColor.GREEN);
                player.sendMessage(component);
                blockLocations.add(new BlockVector(block.getX(), block.getY(), block.getZ()));
                chestCount++;
            }
        }
    }


    public List<BlockVector> getBlockVectors(Vector min, Vector max) {
        List<BlockVector> blockVectors = new ArrayList<>();
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                blockVectors.add(new BlockVector(x, 0, z));
            }
        }
        return blockVectors;
    }
}
