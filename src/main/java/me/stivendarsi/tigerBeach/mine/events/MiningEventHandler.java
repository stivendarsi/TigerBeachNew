package me.stivendarsi.tigerBeach.mine.events;

import io.papermc.paper.persistence.PersistentDataContainerView;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class MiningEventHandler implements Listener {

    private static final Sound noteDeny = Sound.sound().type(Key.key("block.note_block.bass")).source(Sound.Source.UI).build();

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final Map<UUID, String> userMineMap = new HashMap<>();

    @EventHandler
    public void mine(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        MineData mineData = mainHandler().minesHandler().getMineByPosition(location);
        if (mineData == null) return;
        event.setDropItems(false);
        Player player = event.getPlayer();

        if (!mainHandler().minesHandler().hasPlayerRequirementsBypass(player.getUniqueId())) {
            ItemStack tool = player.getInventory().getItemInMainHand();
            PersistentDataContainerView pdc = tool.getPersistentDataContainer();
            if (!(pdc.has(mainHandler().constants().progression())) || !pdc.has(mainHandler().constants().itemKey())) { // Convertible key used later
                event.setCancelled(true);
                player.sendRichMessage("<red>אין לך כלי מורשה!");
                player.playSound(noteDeny);
                return;
            }
            Key itemKey = Key.key(pdc.get(mainHandler().constants().itemKey(), PersistentDataType.STRING));
            ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(itemKey.namespace(), itemKey.value());
            if (itemDefinitionSection == null) return;
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
            if (!player.getInventory().addItem(reward).isEmpty()) {
                player.sendRichMessage("<red>אין לך מספיק מקום.");
                player.playSound(noteDeny);
            }
        }

        if (userMineMap.getOrDefault(player.getUniqueId(), "").equalsIgnoreCase(mineData.getId())) return; // Already running a task

        player.getScheduler().runAtFixedRate(TigerBeach.tigerBeachInstance(), task -> {
            Location loc = player.getLocation();
            if (!mineData.playerInMineRange(loc)) {
                task.cancel();
                return;
            }
            int secondsLeft = mineData.timeSecondsLeft(System.currentTimeMillis());
            int percentLeft = mineData.percentLeft();
            TagResolver tagResolver = TagResolver.builder()
                    .tag("time_left", Tag.inserting(Component.text(secondsLeft + " שניות")))
                    .tag("percent_left", Tag.inserting(Component.text(percentLeft + "%")))
                    .build();
            player.sendActionBar(miniMessage.deserialize(mineData.getActionBarMSG(), tagResolver));
        }, () -> userMineMap.remove(player.getUniqueId()), 1, 20);
        userMineMap.putIfAbsent(player.getUniqueId(), mineData.getId());
    }
}
