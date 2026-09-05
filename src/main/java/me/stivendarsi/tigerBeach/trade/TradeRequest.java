package me.stivendarsi.tigerBeach.trade;

import org.bukkit.entity.Player;

import java.util.UUID;

import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class TradeRequest {
    private UUID sender;
    private UUID receiver;
    private TradeMenuHolder tradeMenu;


    public TradeRequest(UUID sender, UUID receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.tradeMenu = new TradeMenuHolder(sender, receiver);
    }

    public void acceptRequest() {
        Player sender = tigerBeachInstance().getServer().getPlayer(this.sender);
        Player receiver = tigerBeachInstance().getServer().getPlayer(this.receiver);
        if (sender == null || receiver == null) return;
        sender.openInventory(this.tradeMenu.getInventory());
        receiver.openInventory(this.tradeMenu.getInventory());
    }

    public UUID sender() {
        return sender;
    }

    public UUID receiver() {
        return receiver;
    }
}
