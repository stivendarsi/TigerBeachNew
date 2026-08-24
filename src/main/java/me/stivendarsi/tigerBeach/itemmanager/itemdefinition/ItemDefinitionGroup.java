package me.stivendarsi.tigerBeach.itemmanager.itemdefinition;

import me.stivendarsi.tigerBeach.utility.YamlConfigFile;
import org.bukkit.configuration.ConfigurationSection;

import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ItemDefinitionGroup extends YamlConfigFile {
    private ConcurrentHashMap<String, ItemDefinitionSection> itemDefinitionSectionMap = new ConcurrentHashMap<>();

    public ItemDefinitionGroup(File groupFile) {
        super(groupFile);
        for (String itemDefinitionId : get().getKeys(false)){
            ConfigurationSection itemDefinitionConfigurationSection = get().getConfigurationSection(itemDefinitionId);
            if (itemDefinitionConfigurationSection == null) continue;
            ItemDefinitionSection itemDefinitionSection = new ItemDefinitionSection(getCleanName() ,itemDefinitionId, itemDefinitionConfigurationSection);
            this.itemDefinitionSectionMap.put(itemDefinitionId, itemDefinitionSection);
        }
    }

    public @Nullable ItemDefinitionSection getItemDefinitionSection(String itemDefinitionId){
        return this.itemDefinitionSectionMap.get(itemDefinitionId);
    }

    public Set<String> getSectionNamed(){
        return this.itemDefinitionSectionMap.keySet();
    }

    public Map<String, ItemDefinitionSection> itemDefinitionSectionMap() {
        return itemDefinitionSectionMap;
    }
}
