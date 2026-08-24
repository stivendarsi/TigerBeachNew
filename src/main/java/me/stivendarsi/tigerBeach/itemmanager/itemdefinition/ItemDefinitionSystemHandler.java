package me.stivendarsi.tigerBeach.itemmanager.itemdefinition;


import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class ItemDefinitionSystemHandler {
    Map<String, ItemDefinitionGroup> groupMap = new HashMap<>();

    public void load(){
        this.groupMap.clear();
        File itemsFolder = new File(plugin().getDataFolder(), "items");
        if (!itemsFolder.isDirectory()) return;
        File[] files = itemsFolder.listFiles();
        if (files == null) return;
        for (File file : files) {
            ItemDefinitionGroup itemDefinitionGroup = new ItemDefinitionGroup(file);
            this.groupMap.put(itemDefinitionGroup.getCleanName(), itemDefinitionGroup);
        }
    }


    public @Nullable ItemDefinitionSection getItemDefinitionSection(String itemDefinitionGroupName, String itemDefinitionId) {
        ItemDefinitionGroup itemDefinitionGroup = this.groupMap.get(itemDefinitionGroupName);
        if (itemDefinitionGroup != null) return itemDefinitionGroup.getItemDefinitionSection(itemDefinitionId);
        return null;
    }

    public @Nullable ItemDefinitionGroup getItemDefinitionGroup(String itemDefinitionGroupName) {
        return this.groupMap.get(itemDefinitionGroupName);

    }

    public Set<String> itemDefinitionGroupNamed() {
        return this.groupMap.keySet();
    }

    public Set<String> itemDefinitionIds(String itemDefinitionGroupName) {
        ItemDefinitionGroup itemDefinitionGroup = getItemDefinitionGroup(itemDefinitionGroupName);
        if (itemDefinitionGroup != null) return itemDefinitionGroup.getSectionNamed();
        return new HashSet<>();
    }
}
