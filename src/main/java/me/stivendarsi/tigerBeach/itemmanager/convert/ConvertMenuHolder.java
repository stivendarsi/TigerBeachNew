package me.stivendarsi.tigerBeach.itemmanager.convert;

import me.stivendarsi.tigerBeach.TigerBeach;
import me.stivendarsi.tigerBeach.data.BeachUser;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.tags.ConversionTag;
import me.stivendarsi.tigerBeach.utility.PriceVariable;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ConvertMenuHolder implements InventoryHolder {
    private final Inventory inventory;
    private final BeachUser beachUser;
    private final @NotNull ItemDefinitionSection currentItem;
    private @Nullable ItemDefinitionSection nextItem;


    // itemKey -> itemDefinitionGroup:itemDefinitionId
    public ConvertMenuHolder(@NotNull BeachUser beachUser, @NotNull ItemDefinitionSection itemDefinitionSection) {
        this.beachUser = beachUser;
        this.inventory = TigerBeach.plugin().getServer().createInventory(this, 54, MiniMessage.miniMessage().deserialize("<font:tiger_beach:beach><!shadow><white>\ue002\ue001"));

        this.currentItem = itemDefinitionSection;

        ConversionTag conversionTag = currentItem.conversionTag();
        if (conversionTag == null) throw new RuntimeException("No conversion tag");
        this.nextItem = conversionTag.next();


        inventory.setItem(11, this.currentItem.getItem());

        if (nextItem != null &&
                !this.currentItem.itemDefinitionId().equalsIgnoreCase(nextItem.itemDefinitionId())) {
            ItemStack nextItem = this.nextItem.getItem();
            inventory.setItem(15, nextItem);
        } else return;

        List<PriceVariable> priceVariables = new ArrayList<>(conversionTag.convertPrice());

        int slot = 28;
        while (!priceVariables.isEmpty() && slot < this.inventory.getSize()) {
            int a = slot % 9;
            if (0 < a && a < 8) {
                PriceVariable priceVariable = priceVariables.getFirst();
                ItemStack priceStack = priceVariable.getItem();
                if (priceStack == null) continue;
                this.inventory.setItem(slot, priceStack.asQuantity(priceVariable.amount()));
                priceVariables.removeFirst();
            }
            slot++;
        }
    }


    public BeachUser beachUser() {
        return this.beachUser;
    }

    public ItemDefinitionSection currentItem() {
        return currentItem;
    }

    public ItemDefinitionSection nextItem() {
        return nextItem;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
