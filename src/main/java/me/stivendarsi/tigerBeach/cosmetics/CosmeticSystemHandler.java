package me.stivendarsi.tigerBeach.cosmetics;

import me.stivendarsi.tigerBeach.utility.YamlConfigFile;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class CosmeticSystemHandler extends YamlConfigFile {
    public CosmeticSystemHandler() {
        super(new File(tigerBeachInstance().getDataFolder(), "cosmetic.yml"));
    }

    private Map<String, Cosmetic> cosmeticMap = new HashMap<>();

    public void load(){
        for (String id : get().getKeys(false)){
            ConfigurationSection section = get().getConfigurationSection(id);
            if (section == null) continue;
            String groupName = section.getString("item.group");
            String itemId = section.getString("item.id");
            Cosmetic cos =  new Cosmetic(groupName, itemId);
            this.cosmeticMap.put(id, cos);
        }
    }
}
