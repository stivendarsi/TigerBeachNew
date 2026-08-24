package me.stivendarsi.tigerBeach.shop;

import me.stivendarsi.tigerBeach.utility.PriceVariable;
import me.stivendarsi.tigerBeach.utility.YamlConfigFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Aisle extends YamlConfigFile {
    private Map<String, Product> productMap = new LinkedHashMap<>();

    public Aisle(File groupFile) {
        super(groupFile);
        for (String productId : get().getKeys(false)) {
            String itemDefinitionGroupName = get().getString("%s.item.item_definition_group".formatted(productId));
            String itemDefinitionGroupId = get().getString("%s.item.item_definition_id".formatted(productId));

            List<Map<?, ?>> maps = get().getMapList("%s.price".formatted(productId));
            List<PriceVariable> priceVariables = new ArrayList<>();
            for (Map<?, ?> map : maps) {
                YamlConfiguration aPriceSection = new YamlConfiguration();
                aPriceSection.addDefaults((Map<String, Object>) map);
                aPriceSection.options().copyDefaults(true);
                PriceVariable priceVariable = priceVariable(aPriceSection);
                priceVariables.add(priceVariable);
            }
            Product product = new Product(productId, itemDefinitionGroupId, itemDefinitionGroupName, priceVariables);
            this.productMap.put(productId, product);
        }
    }

    private PriceVariable priceVariable(ConfigurationSection section) {
        String itemDefinitionGroupName = section.getString("item_definition_group");
        String itemDefinitionGroupId = section.getString("item_definition_id");
        int amount = section.getInt("amount");
        return new PriceVariable(itemDefinitionGroupName, itemDefinitionGroupId, amount);
    }

    public Product getProduct(String productId){
        return this.productMap.getOrDefault(productId, null);
    }

    public Map<String, Product> productMap() {
        return productMap;
    }
}
