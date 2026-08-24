package me.stivendarsi.tigerBeach.shop;

import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.utility.PriceVariable;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class Product {
    private final String productId;
    private String itemDefinitionId;
    private String itemDefinitionGroup;
    private List<PriceVariable> price = new ArrayList<>();

    public Product(String productId, String itemDefinitionId, String itemDefinitionGroup, List<PriceVariable> price) {
        this.productId = productId;
        this.itemDefinitionId = itemDefinitionId;
        this.itemDefinitionGroup = itemDefinitionGroup;
        this.price = price;
    }

    public String productId() {
        return productId;
    }

    public @Nullable ItemStack getItem(){
        ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(this.itemDefinitionGroup, this.itemDefinitionId);
        if (itemDefinitionSection != null)  return itemDefinitionSection.getItem();
        return null;
    }

    public List<PriceVariable> price() {
        return price;
    }
}
