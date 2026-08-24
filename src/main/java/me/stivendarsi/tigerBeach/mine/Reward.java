package me.stivendarsi.tigerBeach.mine;

import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class Reward {
    private String itemDefinitionGroupName;
    private String itemDefinitionId;
    private int amount;
    private int chance;

    public Reward(String itemDefinitionGroupName, String itemDefinitionId, int amount, int chance) {
        this.itemDefinitionGroupName = itemDefinitionGroupName;
        this.itemDefinitionId = itemDefinitionId;
        this.amount = amount;
        this.chance = chance;
    }

    public int getAmount() {
        return amount;
    }

    public int getChance() {
        return chance;
    }

    public String itemDefinitionId() {
        return itemDefinitionId;
    }

    public @Nullable ItemStack getItem() {
        ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(this.itemDefinitionGroupName, this.itemDefinitionId);
        if (itemDefinitionSection == null) return null;
        ItemStack item  = itemDefinitionSection.getItem();
        if (item == null) return null;
        return item.asQuantity(this.amount);
    }



    public String itemDefinitionGroupName() {
        return itemDefinitionGroupName;
    }
}
