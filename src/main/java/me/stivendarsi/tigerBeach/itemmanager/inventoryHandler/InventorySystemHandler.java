package me.stivendarsi.tigerBeach.itemmanager.inventoryHandler;

import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.utility.YamlConfigFile;

import java.io.File;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class InventorySystemHandler extends YamlConfigFile {
    private ItemDefinitionSection defaultPickaxe;
    private ItemDefinitionSection defaultSword;
    private ItemDefinitionSection defaultHelmet;
    private ItemDefinitionSection defaultChestplate;
    private ItemDefinitionSection defaultLeggings;
    private ItemDefinitionSection defaultBoots;

    public InventorySystemHandler(File groupFile) {
        super(groupFile);
    }

    public void load() {
        reload();
        for (String slot : get().getConfigurationSection("default").getKeys(false)) {
            InventorySlot inventorySlot = InventorySlot.of(slot);
            if (inventorySlot == null) continue;
            String itemDefinitionGroupName = get().getString("default.%s.item_definition_group".formatted(inventorySlot.name().toLowerCase()));
            String itemDefinitionId = get().getString("default.%s.item_definition_id".formatted(inventorySlot.name().toLowerCase()));
            ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(itemDefinitionGroupName, itemDefinitionId);
            switch (inventorySlot) {
                case PICKAXE -> defaultPickaxe = itemDefinitionSection;
                case SWORD -> defaultSword = itemDefinitionSection;
                case HELMET -> defaultHelmet = itemDefinitionSection;
                case CHESTPLATE -> defaultChestplate = itemDefinitionSection;
                case LEGGINGS -> defaultLeggings = itemDefinitionSection;
                case BOOTS -> defaultBoots = itemDefinitionSection;

            }
        }
    }

    public ItemDefinitionSection of(InventorySlot slot) {
        ItemDefinitionSection itemDefinitionSection = null;
        switch (slot) {
            case PICKAXE -> itemDefinitionSection = this.defaultPickaxe;
            case SWORD -> itemDefinitionSection = this.defaultSword;
            case HELMET -> itemDefinitionSection = this.defaultHelmet;
            case CHESTPLATE -> itemDefinitionSection = this.defaultChestplate;
            case LEGGINGS -> itemDefinitionSection = this.defaultLeggings;
            case BOOTS -> itemDefinitionSection = this.defaultBoots;
        }
        return itemDefinitionSection;
    }

    public enum InventorySlot {
        PICKAXE,
        SWORD,
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS;

        public static InventorySlot of(String name) {
            try {
                return InventorySlot.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
