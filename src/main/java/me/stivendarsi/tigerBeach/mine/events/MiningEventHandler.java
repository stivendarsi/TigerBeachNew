package me.stivendarsi.tigerBeach.mine.events;

import io.papermc.paper.persistence.PersistentDataContainerView;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.stivendarsi.tigerBeach.TigerBeach;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.mine.MineData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class MiningEventHandler implements Listener {

    private static final Sound noteDeny = Sound.sound().type(Key.key("block.note_block.bass")).source(Sound.Source.UI).build();

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private static final Map<UUID, ScheduledTask> activeActionBarTasks = new ConcurrentHashMap<>();
    private static final Map<UUID, String> activePlayerMineMap = new ConcurrentHashMap<>();

    @EventHandler
    public void playerMineEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        MineData mineData = mainHandler().minesHandler().getMineByPosition(location);
        if (mineData == null) return;
        event.setDropItems(false);
        Player player = event.getPlayer();

        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!mainHandler().minesHandler().hasPlayerRequirementsBypass(player.getUniqueId())) {

            PersistentDataContainerView pdc = tool.getPersistentDataContainer();

            Key itemKey = Key.key(pdc.getOrDefault(mainHandler().constants().itemKey(), PersistentDataType.STRING, ""));
            ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(itemKey.namespace(), itemKey.value());
            if (itemDefinitionSection == null) {
                event.setCancelled(true);
                player.sendRichMessage("<red>אין לך כלי מורשה!");
                player.playSound(noteDeny);
                return;
            }
            if (!mineData.meetRequirements(itemDefinitionSection.itemDefinitionGroupName(), itemDefinitionSection.weight())) {
                event.setCancelled(true);
                player.sendRichMessage("<red>אין לך מכוש מתאים");
                player.playSound(noteDeny);
                return;
            }
        }

        player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
        mineData.countABlockBrake();
        ItemStack reward = mineData.getRewardByBlock(block.getType().asBlockType());
        if (reward != null) {
            reward.setAmount(getAmountAfterFortune(reward.getAmount(), tool.getEnchantmentLevel(Enchantment.FORTUNE)));
            if (!player.getInventory().addItem(reward).isEmpty()) {
                player.sendRichMessage("<red>אין לך מספיק מקום!");
                player.playSound(noteDeny);
            }
        }


        UUID uuid = player.getUniqueId();

        //Check if player is already tracking this exact mine
        String currentMineId = activePlayerMineMap.get(uuid);
        if (mineData.getId().equalsIgnoreCase(currentMineId) && activeActionBarTasks.containsKey(uuid)) return;

        // Cancel existing task if switching mines or refreshing state
        ScheduledTask existingTask = activeActionBarTasks.remove(uuid);
        if (existingTask != null) existingTask.cancel();


        activePlayerMineMap.put(uuid, mineData.getId());

        ScheduledTask newTask = player.getScheduler().runAtFixedRate(
                TigerBeach.tigerBeachInstance(), task -> {
                    if (!player.isOnline() || !mineData.playerInMineRange(player.getLocation())) {
                        task.cancel();
                        activeActionBarTasks.remove(uuid);
                        activePlayerMineMap.remove(uuid, mineData.getId());
                        return;
                    }

                    int secondsLeft = mineData.timeSecondsLeft(System.currentTimeMillis());
                    int percentLeft = mineData.percentLeft();

                    TagResolver tagResolver = TagResolver.builder()
                            .tag("time_left", Tag.inserting(Component.text(secondsLeft + " שניות")))
                            .tag("percent_left", Tag.inserting(Component.text(percentLeft + "%")))
                            .build();

                    player.sendActionBar(miniMessage.deserialize(mineData.getActionBarMSG(), tagResolver));
                }, () -> {
                    activeActionBarTasks.remove(uuid);
                    activePlayerMineMap.remove(uuid);
                },
                1, 20
        );

        if (newTask != null) activeActionBarTasks.put(uuid, newTask);

    }
    public int getAmountAfterFortune(int amount, int fortuneLevel) {
        if (fortuneLevel <= 0) return amount;

        int multiplier = Math.max(1, ThreadLocalRandom.current().nextInt(fortuneLevel + 2));

        return amount * multiplier;
    }
}
