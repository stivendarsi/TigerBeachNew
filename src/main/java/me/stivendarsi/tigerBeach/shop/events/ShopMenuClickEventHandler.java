package me.stivendarsi.tigerBeach.shop.events;


import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.shop.Aisle;
import me.stivendarsi.tigerBeach.shop.AisleMenuHolder;
import me.stivendarsi.tigerBeach.shop.ShopMenuHolder;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class ShopMenuClickEventHandler implements Listener {
    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ShopMenuHolder shopMenuHolder)) return;
        event.setCancelled(true);
        Sound sound = Sound.sound().type(Key.key("ui.button.click")).source(Sound.Source.UI).build();

        if (event.getSlot() == 22) {
            event.getWhoClicked().playSound(sound);
            event.getWhoClicked().closeInventory();
            return;
        }
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;
        if (!itemStack.getPersistentDataContainer().has(mainHandler().constants().itemKey())) return;
        String itemKeyString = itemStack.getPersistentDataContainer().get(mainHandler().constants().itemKey(), PersistentDataType.STRING);
        Key itemKey = Key.key(itemKeyString);
        ItemDefinitionSection section = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(itemKey.namespace(), itemKey.value());
        if (section == null || !section.containsTag(ItemDefinitionSection.ItemTag.AISLE_ICON)) return;
        Aisle aisle = mainHandler().shopHandler().getAisle(section.aisleId());
        if (aisle == null) return;
        event.getWhoClicked().playSound(sound);
        event.getWhoClicked().openInventory(new AisleMenuHolder(aisle).getInventory());
    }

}
