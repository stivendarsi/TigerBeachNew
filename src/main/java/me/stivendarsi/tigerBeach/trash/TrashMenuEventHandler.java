package me.stivendarsi.tigerBeach.trash;

import net.kyori.adventure.sound.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class TrashMenuEventHandler implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event){
        if (!(event.getInventory().getHolder() instanceof TrashMenu trashMenu)) return;
        if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
            event.setCancelled(true);
            return;
        }
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;
        if (itemStack.getPersistentDataContainer().has(mainHandler().constants().progression())){
            event.setCancelled(true);
            event.getWhoClicked().playSound(Sound.sound().type(net.kyori.adventure.key.Key.key("entity.villager.no")).build());
            event.getWhoClicked().sendRichMessage("<red>אין אפשרות לזרוק פריטי התקדמות.");
        }
    }
}
