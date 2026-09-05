package me.stivendarsi.tigerBeach;

import me.stivendarsi.tigerBeach.commands.CommandHandler;
import me.stivendarsi.tigerBeach.data.UserJoinEventHandler;
import me.stivendarsi.tigerBeach.itemmanager.convert.ConvertItemEventHandler;
import me.stivendarsi.tigerBeach.itemmanager.convert.OpenConvertMenuEventHandler;
import me.stivendarsi.tigerBeach.itemmanager.editor.events.ClickGroupMenuEventHandler;
import me.stivendarsi.tigerBeach.itemmanager.editor.events.GetItemFromGroupMenuEventHandler;
import me.stivendarsi.tigerBeach.mine.events.MiningEventHandler;
import me.stivendarsi.tigerBeach.shop.events.AisleMenuClickEvent;
import me.stivendarsi.tigerBeach.shop.events.ShopMenuClickEventHandler;
import me.stivendarsi.tigerBeach.trade.event.SendTradeRequestEventHandler;
import me.stivendarsi.tigerBeach.trade.event.TradeMenuEventHandler;
import me.stivendarsi.tigerBeach.trash.TrashMenuEventHandler;
import me.stivendarsi.tigerBeach.utility.BeachPlaceholders;
import me.stivendarsi.tigerBeach.utility.HandlersManager;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TigerBeach extends JavaPlugin {
    private static TigerBeach plugin;
    private static HandlersManager mainHandler;

    public static TigerBeach tigerBeachInstance() {
        return plugin;
    }

    public static HandlersManager mainHandler() {
        return mainHandler;
    }

    public void onEnable() {

        getServer().clearRecipes();
        getServer().updateRecipes();


        this.loadDefaultResourcesFromFolder("items", new String[]{"pickaxe", "sword", "helmet", "chestplate", "leggings", "boots", "mineral", "shovel", "cosmetic", "shop_icon", "food"});
        this.loadDefaultResourcesFromFolder("groups", new String[]{"pickaxe", "sword", "armor", "mineral", "cosmetic", "food", "shop_icon", "shovel"});
        this.loadDefaultResourcesFromFolder("aisles", new String[]{"food", "treasure_tools"});
        this.loadDefaultYamlResources(new String[]{"inventory", "mines", "shop", "treasure"});


        plugin = this;
        mainHandler = new HandlersManager();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            (new BeachPlaceholders()).register();
        }

        saveDefaultConfig();
        reloadConfig();


        mainHandler().utilityManager().load();

        mainHandler().itemGroupsManager().loadGroups();
        mainHandler().itemDefinitionSystemHandler().load();
        mainHandler().inventoryHandler().load();
        mainHandler().tradeHandler().load();
        mainHandler().shopHandler().load();
        mainHandler().treasureHandler().load();

        this.getLogger().info("Loading Beach Users...");
        mainHandler().databaseHandler().load();
        mainHandler().userHandler().loadUsers();
        this.getLogger().info("Loaded Beach Users.");

        Permission permission = new Permission("beach.fastconvert", PermissionDefault.FALSE);
        getServer().getPluginManager().addPermission(permission);

        new CommandHandler(this.getLifecycleManager());

        mainHandler().itemGroupsManager().loadItems();
        mainHandler().minesHandler().loadMines();

        this.getServer().getPluginManager().registerEvents(new UserJoinEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new OpenConvertMenuEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new GetItemFromGroupMenuEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new ClickGroupMenuEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new ConvertItemEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new MiningEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new SendTradeRequestEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new TradeMenuEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new ShopMenuClickEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new AisleMenuClickEvent(), this);
        this.getServer().getPluginManager().registerEvents(new TrashMenuEventHandler(), this);
    }

    private void loadDefaultYamlResources(String[] fileNameArray) {
        for (String fileName : fileNameArray) {
            String name = fileName + ".yml";
            File file = new File(this.getDataFolder() + "/" + name);
            if (!file.exists()) {
                this.saveResource(name, false);
            }
        }
    }


    private void loadDefaultResourcesFromFolder(String path, String[] fileNameArray) {
        for (String fileName : fileNameArray) {
            String name = fileName + ".yml";
            File file = new File(this.getDataFolder() + "/" + path, name);
            if (!file.exists()) {
                this.saveResource(path + "/" + name, false);
            }
        }
    }

    public void onDisable() {
        //
    }
}
