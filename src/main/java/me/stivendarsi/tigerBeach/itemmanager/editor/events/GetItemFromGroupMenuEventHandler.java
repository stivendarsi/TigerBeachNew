package me.stivendarsi.tigerBeach.itemmanager.editor.events;

import me.stivendarsi.tigerBeach.TigerBeach;
import me.stivendarsi.tigerBeach.itemmanager.editor.ItemGroupBrowser;
import me.stivendarsi.tigerBeach.itemmanager.editor.ItemGroupsBrowser;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GetItemFromGroupMenuEventHandler implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ItemGroupBrowser itemGroupBrowser)) return;
        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) {
            player.updateInventory();
            int slot = event.getRawSlot();
            if (slot == 49) {
                Bukkit.getScheduler().runTaskLater(TigerBeach.tigerBeachInstance(), () -> {
                    player.openInventory((new ItemGroupsBrowser()).getInventory());
                    player.playSound(Sound.sound().type(Key.key("ui.button.click")).build());
                }, 1L);
            } else if (slot <= itemGroupBrowser.getInventory().getSize() - 10 && slot >= 9) {
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack != null) {
                    ClickType clickType = event.getClick();
                    if (clickType.isLeftClick()) {
                        player.playSound(Sound.sound().type(Key.key("entity.experience_orb.pickup")).build());
                        player.getInventory().addItem(itemStack);
                    }

                    if (clickType.isRightClick()) {
                    }
                }
            }
        }
    }
}
