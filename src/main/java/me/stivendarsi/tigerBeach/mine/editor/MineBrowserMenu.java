package me.stivendarsi.tigerBeach.mine.editor;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.stivendarsi.tigerBeach.mine.MineBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;
import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class MineBrowserMenu implements InventoryHolder {

    private final Inventory inventory;

    public MineBrowserMenu() {
        this.inventory = tigerBeachInstance().getServer().createInventory(this, 54);
        ItemStack border = ItemType.GRAY_STAINED_GLASS_PANE.createItemStack();
        border.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());

        for (int i = 0; i < this.inventory.getSize(); i++) {
            if ((i % 9 == 0 || i % 9 == 8) || (i < 9 || i > this.inventory.getSize() - 9)) {
                this.inventory.setItem(i, border);
            }
        }

        mainHandler().minesHandler().getMineMap().forEach((id, mine) -> {
            MineBlock mineBlock = mine.getFirstEntry();
            if (mineBlock == null) return;
            BlockType blockType = mineBlock.getBlockType();
            ItemStack mineIcon = blockType.getItemType().createItemStack();
            mineIcon.setData(DataComponentTypes.ITEM_NAME, Component.text(id, NamedTextColor.AQUA));
            this.inventory.addItem(mineIcon);
        });

        ItemStack delete = ItemType.REDSTONE_BLOCK.createItemStack();
        delete.setData(DataComponentTypes.ITEM_NAME, Component.text("מחק", NamedTextColor.RED));
        this.inventory.setItem(this.inventory.getSize() - 5, delete);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

}