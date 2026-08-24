package me.stivendarsi.tigerBeach.mine.editor;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.stivendarsi.tigerBeach.mine.Mine;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class EditMineMenu implements InventoryHolder {

    private final Inventory inventory;
    private Mine mine;

    public EditMineMenu(Mine mine) {
        this.mine = mine;
        this.inventory = plugin().getServer().createInventory(this, 54);
        ItemStack border = ItemType.GRAY_STAINED_GLASS_PANE.createItemStack();
        border.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}