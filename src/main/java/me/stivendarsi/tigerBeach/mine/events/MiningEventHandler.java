package me.stivendarsi.tigerBeach.mine.events;

import io.papermc.paper.persistence.PersistentDataContainerView;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.mine.Mine;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class MiningEventHandler implements Listener {
    @EventHandler
    public void mine(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        Mine mine = mainHandler().minesHandler().getMineByPosition(location);
        if (mine == null) return;
        Sound noteDeny = Sound.sound().type(Key.key("block.note_block.bass")).source(Sound.Source.BLOCK).build();
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
            if (!mine.meetRequirements(itemDefinitionSection.itemDefinitionGroupName(), itemDefinitionSection.weight())) {
                event.setCancelled(true);
                player.sendRichMessage("<red>אין לך מכוש מתאים");
                player.playSound(noteDeny);
                return;
            }
        }

        player.incrementStatistic(Statistic.MINE_BLOCK, block.getType());
        mine.countABlockBrake();
        ItemStack reward = mine.getRewardByBlock(block.getType().asBlockType());
        if (reward != null) {
            if (!player.getInventory().addItem(reward).isEmpty()){
                player.sendRichMessage("<red>אין לך מספיק מקום.");
                player.playSound(noteDeny);
            }
        }
        mainHandler().minesHandler().activatePlayerActionBar(player.getUniqueId(), mine);
       // block.breakNaturally(ItemStack.of(Material.AIR), false);
     //   event.setCancelled(true);
    }
}
