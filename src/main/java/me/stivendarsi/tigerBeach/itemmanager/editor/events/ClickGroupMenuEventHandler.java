package me.stivendarsi.tigerBeach.itemmanager.editor.events;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.stivendarsi.tigerBeach.TigerBeach;
import me.stivendarsi.tigerBeach.itemmanager.editor.ItemGroupBrowser;
import me.stivendarsi.tigerBeach.itemmanager.editor.ItemGroupsBrowser;
import me.stivendarsi.tigerBeach.itemmanager.groups.ItemGroup;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class ClickGroupMenuEventHandler implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ItemGroupsBrowser itemGroupsBrowser)) return;

        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) {
            player.updateInventory();
            int slot = event.getRawSlot();
            if (slot == 49) {
                player.playSound(Sound.sound().type(Key.key("ui.button.click")).build());
                player.closeInventory();
            } else if (slot <= itemGroupsBrowser.getInventory().getSize() - 10 && slot >= 9) {
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack != null) {
                    ItemGroup itemGroup = mainHandler().itemGroupsManager().getGroup(PlainTextComponentSerializer.plainText().serialize(itemStack.getDataOrDefault(DataComponentTypes.ITEM_NAME, Component.empty())).toLowerCase());
                    Bukkit.getScheduler().runTaskLater(TigerBeach.tigerBeachInstance(), () -> {
                        player.openInventory((new ItemGroupBrowser(itemGroup)).getInventory());
                        player.playSound(Sound.sound().type(Key.key("ui.button.click")).build());
                    }, 1L);
                }
            }
        }
    }
}
