package me.stivendarsi.tigerBeach.treasure.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class DigTreasureEventHandler implements Listener {
    @EventHandler
    public void detect(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Block block = event.getClickedBlock();
        if (block == null) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        if (!itemStack.getPersistentDataContainer().has(mainHandler().constants().treasureDetector())) return;

        mainHandler().treasureHandler().detectBlock(block, event.getPlayer());
    }


    @EventHandler
    public void dig(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.getItem().getPersistentDataContainer().has(mainHandler().constants().shovel())) return;
        Player player = event.getPlayer();
        player.sendRichMessage("click");

    }
}
