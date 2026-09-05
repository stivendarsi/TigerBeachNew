package me.stivendarsi.tigerBeach.itemmanager.editor;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.stivendarsi.tigerBeach.TigerBeach;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

@SuppressWarnings("UnstableApiUsage")
public class ItemGroupsBrowser implements InventoryHolder {
    private final Inventory inventory = TigerBeach.tigerBeachInstance().getServer().createInventory(this, 54);

    public ItemGroupsBrowser() {
        ItemStack border = ItemType.WHITE_STAINED_GLASS_PANE.createItemStack();
        border.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());

       for (int i = 0; i < 9; ++i) {
          this.inventory.setItem(i, border);
       }
       for (int i = this.inventory.getSize() - 9; i < this.inventory.getSize(); ++i) {
          this.inventory.setItem(i, border);
       }

        ItemStack close = ItemType.BARRIER.createItemStack();
        close.setData(DataComponentTypes.ITEM_NAME, Component.text("חזור ✘", NamedTextColor.RED));

        this.inventory.setItem(49, close);
        mainHandler().itemGroupsManager().groupMap().values().forEach(itemGroup -> {
            ItemStack groupDisplay = ItemType.NETHER_STAR.createItemStack();
            groupDisplay.setData(DataComponentTypes.ITEM_NAME, Component.text(itemGroup.getCleanName()));
            this.inventory.addItem(groupDisplay);
        });
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
