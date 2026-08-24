package me.stivendarsi.tigerBeach.shop;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.LinkedList;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;
import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class ShopMenuHolder implements InventoryHolder {

    private final Inventory inventory;

    public ShopMenuHolder() {
        this.inventory = plugin().getServer().createInventory(this, 27, MiniMessage.miniMessage().deserialize("<font:tiger_beach:beach><!shadow><white>\ue002\ue004"));

        LinkedList<ItemDefinitionSection> icons = new LinkedList<>();
        mainHandler().shopHandler().icons().forEach((s, iconGroup) -> {
            iconGroup.itemDefinitionSectionMap().forEach((s1, icon) -> {
                icons.add(icon);
            });
        });
        ItemStack close = ItemType.BARRIER.createItemStack();
        close.setData(DataComponentTypes.ITEM_NAME, MiniMessage.miniMessage().deserialize("<red>חזרה"));
        close.setData(DataComponentTypes.ITEM_MODEL, net.kyori.adventure.key.Key.key("tiger_beach:gui/close"));
        this.inventory.setItem(22, close);

        for (int i = 10; i < 18; i++) {
            if (icons.isEmpty()) break;
            ItemStack itemStack = icons.getFirst().getItem();
            icons.removeFirst();
            if (itemStack == null) return;
            this.inventory.setItem(i, itemStack);
        }
//
//        for (int i = 10; i < 17; i++) {
//           ItemStack item = this.inventory.getItem(i);
//           if (item == null){
//               ItemStack barrier = ItemType.BEDROCK.createItemStack();
//               barrier.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
//               this.inventory.setItem(i, barrier);
//           }
//        }

    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}