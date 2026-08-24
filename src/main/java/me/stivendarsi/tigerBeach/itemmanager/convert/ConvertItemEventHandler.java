package me.stivendarsi.tigerBeach.itemmanager.convert;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ConvertItemEventHandler implements Listener {
    @EventHandler
    public void click(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConvertMenuHolder convertMenu)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        //player.updateInventory();
        if (event.getRawSlot() != 15) return;
        if (event.getInventory().getItem(15) == null) {
            player.playSound(Sound.sound().type(Key.key("entity.villager.no")).build());
            player.sendRichMessage("<red>הגעת לסוף, המשך בעתיד...");
            return;
        }
        convertMenu.beachUser().tryToUpgrade(player, convertMenu.currentItem());
    }
}
