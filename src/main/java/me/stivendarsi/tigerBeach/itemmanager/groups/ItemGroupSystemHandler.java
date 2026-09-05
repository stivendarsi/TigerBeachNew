package me.stivendarsi.tigerBeach.itemmanager.groups;

import me.stivendarsi.tigerBeach.TigerBeach;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemGroupSystemHandler {
   private final Map<String, ItemGroup> groupMap = new HashMap();

   public void loadGroups() {
      this.groupMap.clear();
      File groupsFolder = new File(TigerBeach.tigerBeachInstance().getDataFolder(), "groups");
      if (!groupsFolder.isDirectory()) {
         throw new RuntimeException("No groups file: " + groupsFolder.getPath());
      } else {
         File[] files = groupsFolder.listFiles();
         if (files == null) {
            TigerBeach.tigerBeachInstance().getLogger().warning("Null Files");
         } else {
            File[] var3 = files;
            int var4 = files.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               File groupFile = var3[var5];
               ItemGroup itemGroup = new ItemGroup(groupFile);
               this.groupMap.put(itemGroup.getCleanName(), itemGroup);
            }

         }
      }
   }

   public Map<String, ItemGroup> groupMap() {
      return this.groupMap;
   }

   @Nullable
   public ItemGroup getGroup(String groupName) {
      return this.groupMap.getOrDefault(groupName, null);
   }

   @Nullable
   public GroupsItemSection getItem(String groupName, String id) {
      ItemGroup itemGroup = this.getGroup(groupName);
      if (itemGroup == null) return null;
      return itemGroup.getItemSection(id);
   }

   public void loadItems() {
       for (ItemGroup group : this.groupMap.values()) {
           group.loadItems();
       }
   }
}
