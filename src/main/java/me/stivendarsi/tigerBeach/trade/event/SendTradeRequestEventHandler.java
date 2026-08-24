package me.stivendarsi.tigerBeach.trade.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class SendTradeRequestEventHandler implements Listener {
    @EventHandler
    public void sendTradeRequest(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Player receiver)) return;
        Player sender = event.getPlayer();
        if (!sender.isSneaking()) return;
        if (sender.getOpenInventory().getType() != InventoryType.CRAFTING && sender.getOpenInventory().getType() != InventoryType.CREATIVE)
            return; // If a player has open GUI, don't send a request
        mainHandler().tradeHandler().sendTradeRequest(sender, receiver);

    }
}
