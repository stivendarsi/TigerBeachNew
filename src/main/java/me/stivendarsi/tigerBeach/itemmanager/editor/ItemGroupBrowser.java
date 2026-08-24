package me.stivendarsi.tigerBeach.itemmanager.editor;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.stivendarsi.tigerBeach.TigerBeach;
import me.stivendarsi.tigerBeach.itemmanager.groups.ItemGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;

public class ItemGroupBrowser implements InventoryHolder {

    private final ItemGroup group;
    private final Inventory inventory;

    public ItemGroupBrowser(ItemGroup group) {
        this.group = group;
        this.inventory = TigerBeach.plugin().getServer().createInventory(this, 54);
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

        List<ItemStack> itemStacks = new ArrayList<>(group.itemStackList());
        if (itemStacks.isEmpty()) return;
        for (int column = 0; column < 9; column++) {
            for (int row = 1; row < ((inventory.getSize() - 9) / 9); row++) {
                int slot = row * 9;
                if (itemStacks.isEmpty()) break;
                this.inventory.setItem(slot + column, itemStacks.getFirst());
                itemStacks.removeFirst();
            }
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
