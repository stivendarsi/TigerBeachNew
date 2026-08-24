package me.stivendarsi.tigerBeach.shop.events;

import me.stivendarsi.tigerBeach.shop.AisleMenuHolder;
import me.stivendarsi.tigerBeach.shop.Product;
import me.stivendarsi.tigerBeach.shop.ShopMenuHolder;
import me.stivendarsi.tigerBeach.utility.PriceVariable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class AisleMenuClickEvent implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AisleMenuHolder aisleMenuHolder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getSlot() == 22) {
            Sound sound = Sound.sound().type(net.kyori.adventure.key.Key.key("ui.button.click")).source(Sound.Source.UI).build();
            player.playSound(sound);
            player.openInventory(new ShopMenuHolder().getInventory());
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;
        if (!itemStack.getPersistentDataContainer().has(mainHandler().constants().itemAisleKey())) return;
        String aisleItemKeyString = itemStack.getPersistentDataContainer().get(mainHandler().constants().itemAisleKey(), PersistentDataType.STRING);
        Key aisleItemKey = Key.key(aisleItemKeyString);

        Product product = aisleMenuHolder.aisle().getProduct(aisleItemKey.value());
        if (product == null) return;

        ItemStack productItem = product.getItem();

        List<PriceVariable> priceVariables = product.price();

        boolean b = true;

        for (PriceVariable priceVariable : priceVariables) {
            ItemStack item = priceVariable.getItem();
            if (!player.getInventory().containsAtLeast(item, priceVariable.amount())) {
                b = false;
                break;
            }
        }
        if (!b) {
            player.playSound(Sound.sound().type(net.kyori.adventure.key.Key.key("entity.villager.no")).build());
            player.sendRichMessage("<red>אין לך מספיק חומרים");
            return;
        }
        if (productItem != null) {
            player.playSound(Sound.sound().type(net.kyori.adventure.key.Key.key("entity.experience_orb.pickup")).build());
            PriceVariable.pay(priceVariables, player);
            player.getInventory().addItem(productItem);
        }
    }

}
