package me.stivendarsi.tigerBeach.utility;

import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class PriceVariable {
    private String itemDefinitionGroupName;
    private String itemDefinitionId;
    private int amount;

    public PriceVariable(String itemDefinitionGroupName, String itemDefinitionId, int amount) {
        this.itemDefinitionGroupName = itemDefinitionGroupName;
        this.itemDefinitionId = itemDefinitionId;
        this.amount = amount;
    }

    public @Nullable ItemStack getItem() {
        ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(this.itemDefinitionGroupName, this.itemDefinitionId);
        if (itemDefinitionSection == null) return null;
        return itemDefinitionSection.getItem();
    }


    public int amount() {
        return this.amount;
    }

    public static void pay(List<PriceVariable> priceVariables, Player player) {
        priceVariables.forEach(priceVariable -> {
            ItemStack price = priceVariable.getItem();
            if (price != null) {
                player.getInventory().removeItemAnySlot(price.asQuantity(priceVariable.amount()));
            }
        });
    }
}
