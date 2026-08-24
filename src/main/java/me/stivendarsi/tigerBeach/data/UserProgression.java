package me.stivendarsi.tigerBeach.data;

import io.papermc.paper.persistence.PersistentDataContainerView;
import me.stivendarsi.tigerBeach.TigerBeach;
import me.stivendarsi.tigerBeach.itemmanager.inventoryHandler.InventorySystemHandler;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.tags.ConversionTag;
import me.stivendarsi.tigerBeach.utility.DatabaseHandler;
import me.stivendarsi.tigerBeach.utility.PriceVariable;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class UserProgression {
    private UUID userUUID;
    private ItemDefinitionSection pickaxe = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.PICKAXE);
    private ItemDefinitionSection sword = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.SWORD);
    private ItemDefinitionSection helmet = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.HELMET);
    private ItemDefinitionSection chestplate = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.CHESTPLATE);
    private ItemDefinitionSection leggings = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.LEGGINGS);
    private ItemDefinitionSection boots = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.BOOTS);

    public UserProgression(UUID userUUID, ItemDefinitionSection pickaxe, ItemDefinitionSection sword, ItemDefinitionSection helmet, ItemDefinitionSection chestplate, ItemDefinitionSection leggings, ItemDefinitionSection boots) {
        this.userUUID = userUUID;
        this.pickaxe = pickaxe;
        this.sword = sword;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public static UserProgression defaultUser(UUID uuid) {
        ItemDefinitionSection pickaxe = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.PICKAXE);
        ItemDefinitionSection sword = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.SWORD);
        ItemDefinitionSection helmet = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.HELMET);
        ItemDefinitionSection chestplate = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.CHESTPLATE);
        ItemDefinitionSection leggings = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.LEGGINGS);
        ItemDefinitionSection boots = mainHandler().inventoryHandler().of(InventorySystemHandler.InventorySlot.BOOTS);
        return new UserProgression(uuid, pickaxe, sword, helmet, chestplate, leggings, boots);
    }


    @Deprecated(forRemoval = true)
    public UserProgression(UUID userUUID, ConfigurationSection progressionSection) {
        this.userUUID = userUUID;
        if (progressionSection == null) return;
        for (String slotName : progressionSection.getKeys(false)) {
            InventorySystemHandler.InventorySlot slot = InventorySystemHandler.InventorySlot.of(slotName);
            if (slot == null) continue;
            ItemDefinitionSection itemDefinitionSection;
            if (progressionSection.isConfigurationSection(slotName.toLowerCase())) {
                String itemDefinitionGroupName = progressionSection.getString("%s.item_definition_group".formatted(slotName), slotName.toLowerCase());
                String itemDefinitionId = progressionSection.getString("%s.item_definition_id".formatted(slotName));
                itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(itemDefinitionGroupName, itemDefinitionId);
            } else {
                String itemDefinitionId = progressionSection.getString(slotName);
                itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(slotName, itemDefinitionId);
            }
            switch (slot) {
                case PICKAXE -> this.pickaxe = itemDefinitionSection;
                case SWORD -> this.sword = itemDefinitionSection;
                case HELMET -> this.helmet = itemDefinitionSection;
                case CHESTPLATE -> this.chestplate = itemDefinitionSection;
                case LEGGINGS -> this.leggings = itemDefinitionSection;
                case BOOTS -> this.boots = itemDefinitionSection;
            }
        }
    }

    public boolean canUpgrade(Player player, ItemDefinitionSection itemDefinitionSection) {
        ConversionTag conversionTag = itemDefinitionSection.conversionTag();
        if (conversionTag == null) return false;
        PlayerInventory inv = player.getInventory();
        for (PriceVariable priceVariable : conversionTag.convertPrice()) {
            ItemStack itemStack = priceVariable.getItem();
            if (!inv.containsAtLeast(itemStack, priceVariable.amount())) {
                TigerBeach.plugin().getLogger().warning("Doesn't have the required items");
                return false;
            }
        }
        if (itemDefinitionSection.isProgression()) {
            TigerBeach.plugin().getLogger().warning("Its progression!");
            return true;
        }
        TigerBeach.plugin().getLogger().warning("Checking if can fit");
        return canFitNext(inv, conversionTag);
    }

    private boolean canFitNext(@NotNull PlayerInventory inventory, @NotNull ConversionTag conversionTag) {
        ItemDefinitionSection next = conversionTag.next();
        if (next == null) return false;
        Inventory inv = Bukkit.createInventory(inventory.getHolder(), 36);
        inv.setContents(inventory.getStorageContents());
        for (PriceVariable priceVariable : conversionTag.convertPrice()) {
            ItemStack price = priceVariable.getItem();
            if (price != null) {
                inv.removeItemAnySlot(price.asQuantity(priceVariable.amount()));
            }
        }
        ItemStack nextItem = next.getItem();
        if (nextItem == null) return false;
        return inv.addItem(nextItem).isEmpty();
    }


    public void payAndConvert(ItemDefinitionSection itemDefinitionSection, Player player) {
        ConversionTag conversionTag = itemDefinitionSection.conversionTag();
        if (conversionTag == null) return;
        ItemDefinitionSection next = conversionTag.next();
        if (next == null) throw new RuntimeException("Null next");
        InventorySystemHandler.InventorySlot inventorySlot = InventorySystemHandler.InventorySlot.of(itemDefinitionSection.itemDefinitionGroupName());
        if (inventorySlot != null) {
            switch (inventorySlot) {
                case PICKAXE -> {
                    this.pickaxe = next;
                    mainHandler().databaseHandler().updateUserColumn(this.userUUID, DatabaseHandler.ColumnType.PICKAXE, next.key().asString());
                }
                case SWORD -> {
                    this.sword = next;
                    mainHandler().databaseHandler().updateUserColumn(this.userUUID, DatabaseHandler.ColumnType.SWORD, next.key().asString());
                }
                case HELMET -> {
                    this.helmet = next;
                    mainHandler().databaseHandler().updateUserColumn(this.userUUID, DatabaseHandler.ColumnType.HELMET, next.key().asString());
                }
                case CHESTPLATE -> {
                    this.chestplate = next;
                    mainHandler().databaseHandler().updateUserColumn(this.userUUID, DatabaseHandler.ColumnType.CHESTPLATE, next.key().asString());
                }
                case LEGGINGS -> {
                    this.leggings = next;
                    mainHandler().databaseHandler().updateUserColumn(this.userUUID, DatabaseHandler.ColumnType.LEGGINGS, next.key().asString());
                }
                case BOOTS -> {
                    this.boots = next;
                    mainHandler().databaseHandler().updateUserColumn(this.userUUID, DatabaseHandler.ColumnType.BOOTS, next.key().asString());
                }
            }
        }

        // Pay
        PriceVariable.pay(conversionTag.convertPrice(), player);

        // Remove If Progression Item
        ItemStack itemStack = itemDefinitionSection.getItem();
        if (itemStack != null) for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            PersistentDataContainerView pdc = item.getPersistentDataContainer();
            if (pdc.has(mainHandler().constants().progression()) && pdc.has(mainHandler().constants().itemKey()) && pdc.has(mainHandler().constants().conversion())) {
                String itemKey = item.getPersistentDataContainer().get(mainHandler().constants().itemKey(), PersistentDataType.STRING);
                //   System.out.println(itemKey);
                if (Key.key(itemKey).value().equalsIgnoreCase(itemDefinitionSection.itemDefinitionId())) {
                    item.setAmount(item.getAmount() - 1);
                    break;
                }
            }
        }
        ItemStack nextItem = next.getItem();
        if (nextItem != null) player.getInventory().addItem(nextItem);
    }

    @Deprecated(forRemoval = true)
    private void setNext(ItemDefinitionSection next, InventorySystemHandler.InventorySlot inventorySlot) {
//        mainHandler().userHandler().get().set("%s.progression.%s".formatted(userUUID, inventorySlot.name().toLowerCase()), next.itemDefinitionId());
//        mainHandler().userHandler().save();
    }

    public ItemDefinitionSection pickaxe() {
        return pickaxe;
    }

    public ItemDefinitionSection sword() {
        return sword;
    }

    public ItemDefinitionSection helmet() {
        return helmet;
    }

    public ItemDefinitionSection chestplate() {
        return chestplate;
    }

    public ItemDefinitionSection leggings() {
        return leggings;
    }

    public ItemDefinitionSection boots() {
        return boots;
    }
}
