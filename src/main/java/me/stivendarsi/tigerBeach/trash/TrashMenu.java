package me.stivendarsi.tigerBeach.trash;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class TrashMenu implements InventoryHolder {

    private final Inventory inventory;

    public TrashMenu() {
        this.inventory = tigerBeachInstance().getServer().createInventory(this, 27,  MiniMessage.miniMessage().deserialize("<font:tiger_beach:beach><!shadow><white>\ue002\ue005"));
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}