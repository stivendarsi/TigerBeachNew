package me.stivendarsi.tigerBeach.mine.events;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.stivendarsi.tigerBeach.mine.editor.MineBrowserMenu;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.EventListener;

public class MineBrowserMenuEvents implements EventListener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder(false) instanceof MineBrowserMenu browserMenu)) return;
        event.setCancelled(true);
        int slot = event.getSlot();
        if (((slot % 9 == 0 || slot % 9 == 8) || (slot < 9 || slot > browserMenu.getInventory().getSize() - 9)) || slot > browserMenu.getInventory().getSize()) return;
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;
        String mineId = PlainTextComponentSerializer.plainText().serialize(itemStack.getData(DataComponentTypes.ITEM_NAME));
    }
}
