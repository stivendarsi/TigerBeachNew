package me.stivendarsi.tigerBeach.itemmanager.groups;

import me.stivendarsi.tigerBeach.utility.YamlConfigFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemGroup extends YamlConfigFile {
   private final Map<String, GroupsItemSection> itemConfigurations = new LinkedHashMap<>();
   private final List<ItemStack> itemStackList = new ArrayList<>();

   public ItemGroup(File groupFile) {
      super(groupFile);
       for (String itemId : this.get().getKeys(false)) {
           GroupsItemSection configManager = new GroupsItemSection(this);
           ConfigurationSection section = this.get().getConfigurationSection(itemId);
           configManager.load(itemId, section);
           this.itemConfigurations.put(itemId, configManager.makeItem());
       }

   }


    public void loadItems() {
      this.itemStackList.clear();
       for (GroupsItemSection sectionManager : this.itemConfigurations.values()) {
           this.itemStackList.add(sectionManager.makeItem().asItemStack());
       }
   }

   public List<ItemStack> itemStackList() {
      return this.itemStackList;
   }

   @Nullable
   public GroupsItemSection getItemSection(String id) {
      return this.itemConfigurations.getOrDefault(id, null);
   }
}
