package me.stivendarsi.tigerBeach.itemmanager.itemdefinition;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.stivendarsi.tigerBeach.itemmanager.groups.GroupsItemSection;
import me.stivendarsi.tigerBeach.itemmanager.inventoryHandler.InventorySystemHandler;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.tags.ConversionTag;
import me.stivendarsi.tigerBeach.utility.PriceVariable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class ItemDefinitionSection {
    private final String itemDefinitionId;
    private final String itemDefinitionGroupName;
    private String itemId;
    private String itemGroupName;
    private List<ItemTag> tags = new ArrayList<>();


    private ConversionTag conversionTag;

    private String aisleId;

    private double weight;

    public Key key() {
        return Key.key(this.itemDefinitionGroupName, this.itemDefinitionId);
    }

    private ItemStack item;

    public boolean isProgression(){
        InventorySystemHandler.InventorySlot inventorySlot = InventorySystemHandler.InventorySlot.of(this.itemGroupName);
        return inventorySlot != null;
    }

    public ItemDefinitionSection(String itemDefinitionGroupName, String itemDefinitionId, ConfigurationSection section) {
        this.itemDefinitionId = itemDefinitionId;
        this.itemDefinitionGroupName = itemDefinitionGroupName;
        List<String> tagList = section.getStringList("tags");
        for (String tagName : tagList) {
            ItemTag tag = ItemTag.of(tagName);
            if (tag == null) continue;
            this.tags.add(tag);
        }
        this.itemId = section.getString("item.id");
        this.itemGroupName = section.getString("item.group");

        this.weight = section.getInt("weight", 0);

        if (tags.contains(ItemTag.CONVERSION)) {
            ConversionTag.Builder builder = ConversionTag.conversionTag();
            String nextItemDefinitionGroupName = section.getString("next.item_definition_group");
            String nextItemDefinitionId = section.getString("next.item_definition_id");
            List<Map<?, ?>> map = section.getMapList("price");
            List<PriceVariable> priceVariables = loadPriceVariable(map);

            builder.setNextItemDefinitionId(nextItemDefinitionId);
            builder.setNextItemDefinitionGroupName(nextItemDefinitionGroupName);
            builder.setConvertPrice(priceVariables);
            this.conversionTag = builder.build();
        }
        if (tags.contains(ItemTag.AISLE_ICON)) {
            this.aisleId = section.getString("aisle_id");
        }
        loadItem();
    }

    private void loadItem() {
        GroupsItemSection section = mainHandler().itemGroupsManager().getItem(this.itemGroupName, this.itemId);
        if (section == null) {
            return;
        }
        ItemStack itemStack = section.asItemStack();
        itemStack.editPersistentDataContainer(pdc -> {
            for (ItemTag tag : this.tags) {
                switch (tag) {
                    case SHOVEL -> pdc.set(mainHandler().constants().shovel(), PersistentDataType.BOOLEAN, true);
                    case TREASURE_DETECTOR ->
                            pdc.set(mainHandler().constants().treasureDetector(), PersistentDataType.BOOLEAN, true);
                    case AISLE_ICON -> pdc.set(mainHandler().constants().aisleIcon(), PersistentDataType.BOOLEAN, true);
                    case PRICE_VARIABLE ->
                            pdc.set(mainHandler().constants().priceVariable(), PersistentDataType.BOOLEAN, true);
                    case FOOD -> pdc.set(mainHandler().constants().food(), PersistentDataType.BOOLEAN, true);
                    case COSMETIC -> pdc.set(mainHandler().constants().cosmetic(), PersistentDataType.BOOLEAN, true);
                    case MINE_REWARD ->
                            pdc.set(mainHandler().constants().mineReward(), PersistentDataType.BOOLEAN, true);
                    case PROGRESSION ->
                            pdc.set(mainHandler().constants().progression(), PersistentDataType.BOOLEAN, true);
                    case CONVERSION ->
                            pdc.set(mainHandler().constants().conversion(), PersistentDataType.BOOLEAN, true);

                }
            }
            pdc.set(mainHandler().constants().itemKey(), PersistentDataType.STRING, this.key().asString());
        });
        this.item = itemStack;
    }

    public @Nullable ItemStack getItem() {
        if (this.tags.contains(ItemTag.PROGRESSION)) {
            if (this.item.hasData(DataComponentTypes.LORE)) {
                ItemLore lore = this.item.getData(DataComponentTypes.LORE);
                TextReplacementConfig rarityReplacement = TextReplacementConfig.builder()
                        .match("<rarity>")
                        .replacement(mainHandler().userHandler().getProgressionItemRarity(this.key()))
                        .build();
                List<Component> newLore = lore.lines().stream()
                        .map(component -> component.replaceText(rarityReplacement))
                        .toList();

                this.item.setData(DataComponentTypes.LORE, ItemLore.lore(newLore));
            }
        }
        return this.item.clone();
    }

    private List<PriceVariable> loadPriceVariable(List<Map<?, ?>> map) {
        List<PriceVariable> priceVariables = new ArrayList<>();
        for (Map<?, ?> aPrice : map) {
            YamlConfiguration aPriceVariable = new YamlConfiguration();
            aPriceVariable.addDefaults((Map<String, Object>) aPrice);
            aPriceVariable.options().copyDefaults(true);
            int amount = aPriceVariable.getInt("amount");
            String item_group = aPriceVariable.getString("item_definition_group");
            String itemId = aPriceVariable.getString("item_definition_id");
            PriceVariable priceVariable = new PriceVariable(item_group, itemId, amount);
            priceVariables.add(priceVariable);
        }
        return priceVariables;
    }

    public String itemDefinitionGroupName() {
        return itemDefinitionGroupName;
    }

    public @Nullable ConversionTag conversionTag() {
        return conversionTag;
    }

    public double weight() {
        return weight;
    }

    public String itemDefinitionId() {
        return itemDefinitionId;
    }

    public boolean containsTag(ItemTag itemTag) {
        return this.tags.contains(itemTag);
    }

    public String aisleId() {
        return aisleId;
    }

    public enum ItemTag {
        CONVERSION, // NameSpaced Key -> "<itemDefinitionGroupName>:<itemDefinitionId>"
        PROGRESSION, // Boolean
        MINE_REWARD, // Boolean
        FOOD, // Boolean
        PRICE_VARIABLE,
        AISLE_ICON,
        TREASURE_DETECTOR,
        SHOVEL,
        COSMETIC; // Boolean

        public static @Nullable ItemTag of(String name) {
            try {
                return ItemTag.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
