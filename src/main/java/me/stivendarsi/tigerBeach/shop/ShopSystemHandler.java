package me.stivendarsi.tigerBeach.shop;

import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionGroup;
import me.stivendarsi.tigerBeach.utility.YamlConfigFile;

import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;
import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class ShopSystemHandler extends YamlConfigFile {
    private Map<String, ItemDefinitionGroup> icons = new LinkedHashMap<>();

    private Map<String, Aisle> aisles = new LinkedHashMap<>();

    public ShopSystemHandler() {
        super(new File(plugin().getDataFolder(), "shop.yml"));
    }

    public void load() {
        this.aisles.clear();
        List<String> aisleIcons = get().getStringList("icons");
        for (String iconGroup : aisleIcons) {
            ItemDefinitionGroup itemDefinitionGroup = mainHandler().itemDefinitionSystemHandler().getItemDefinitionGroup(iconGroup);
            this.icons.put(iconGroup, itemDefinitionGroup);
        }

        File file = new File(plugin().getDataFolder(), "aisles");
        if (!file.isDirectory()) return;
        File[] files = file.listFiles();
        if (files == null) return;
        for (File aisleFile : files) {
            Aisle aisle = new Aisle(aisleFile);
            this.aisles.put(aisle.getCleanName(), aisle);
        }
    }

    public Map<String, ItemDefinitionGroup> icons() {
        return icons;
    }

    public @Nullable Aisle getAisle(String aisle){
        return this.aisles.getOrDefault(aisle, null);
    }

    public @Nullable Product getProduct(String aisleId, String productId){
        Aisle aisle = this.getAisle(aisleId);
        if (aisle != null) return aisle.getProduct(productId);
        return null;
    }
}
