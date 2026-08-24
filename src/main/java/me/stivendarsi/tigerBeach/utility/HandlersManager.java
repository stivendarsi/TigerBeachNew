package me.stivendarsi.tigerBeach.utility;

import me.stivendarsi.tigerBeach.data.UserSystemHandler;
import me.stivendarsi.tigerBeach.itemmanager.groups.ItemGroupSystemHandler;
import me.stivendarsi.tigerBeach.itemmanager.inventoryHandler.InventorySystemHandler;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSystemHandler;
import me.stivendarsi.tigerBeach.mine.MinesSystemHandler;
import me.stivendarsi.tigerBeach.shop.ShopSystemHandler;
import me.stivendarsi.tigerBeach.trade.TradeSystemHandler;
import me.stivendarsi.tigerBeach.treasure.TreasureSystemHandler;

import java.io.File;

import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class HandlersManager {
    private final ItemGroupSystemHandler groupsManager;
    private final InventorySystemHandler inventorySystemHandler;
    private final UserSystemHandler userSystemHandler;
    private final Constants constants;
    private final ItemDefinitionSystemHandler itemDefinitionSystemHandler;
    private final UtilityManager utilityManager;
    private final MinesSystemHandler minesSystemHandler;
    private final TradeSystemHandler tradeSystemHandler;
    private final ShopSystemHandler shopSystemHandler;
    private final TreasureSystemHandler treasureSystemHandler;

    private final DatabaseHandler databaseHandler;

    public DatabaseHandler databaseHandler() {
        return databaseHandler;
    }

    public ItemGroupSystemHandler itemGroupsManager() {
        return groupsManager;
    }

    public InventorySystemHandler inventoryHandler() {
        return inventorySystemHandler;
    }

    public UserSystemHandler userHandler() {
        return userSystemHandler;
    }

    public Constants constants() {
        return constants;
    }

    public ItemDefinitionSystemHandler itemDefinitionSystemHandler() {
        return itemDefinitionSystemHandler;
    }

    public UtilityManager utilityManager() {
        return utilityManager;
    }

    public ShopSystemHandler shopHandler() {
        return shopSystemHandler;
    }

    public MinesSystemHandler minesHandler() {
        return minesSystemHandler;
    }

    public TradeSystemHandler tradeHandler() {
        return tradeSystemHandler;
    }

    public TreasureSystemHandler treasureHandler() {
        return treasureSystemHandler;
    }


    public HandlersManager() {
        groupsManager = new ItemGroupSystemHandler();
        constants = new Constants();
        inventorySystemHandler = new InventorySystemHandler(new File(plugin().getDataFolder(), "inventory.yml"));
        itemDefinitionSystemHandler = new ItemDefinitionSystemHandler();
        userSystemHandler = new UserSystemHandler();
        utilityManager = new UtilityManager();
        minesSystemHandler = new MinesSystemHandler();
        tradeSystemHandler = new TradeSystemHandler();
        shopSystemHandler = new ShopSystemHandler();
        treasureSystemHandler = new TreasureSystemHandler();
        databaseHandler = new DatabaseHandler();
    }
}
