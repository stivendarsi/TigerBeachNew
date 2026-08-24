package me.stivendarsi.tigerBeach.utility;

import me.stivendarsi.tigerBeach.TigerBeach;
import org.bukkit.NamespacedKey;

public class Constants {
    private final NamespacedKey progression = new NamespacedKey(TigerBeach.plugin(), "progression");
    private final NamespacedKey cosmetic = new NamespacedKey(TigerBeach.plugin(), "cosmetic");
    private final NamespacedKey conversion = new NamespacedKey(TigerBeach.plugin(), "conversion");
    private final NamespacedKey mineReward = new NamespacedKey(TigerBeach.plugin(), "reward");
    private final NamespacedKey food = new NamespacedKey(TigerBeach.plugin(), "food");
    private final NamespacedKey priceVariable = new NamespacedKey(TigerBeach.plugin(), "price_variable");
    private final NamespacedKey treasureDetector = new NamespacedKey(TigerBeach.plugin(), "treasure_detector");



    private final NamespacedKey excellentCratesKey = new NamespacedKey("excellentcrates", "crate_key.id");

    private final NamespacedKey shovel = new NamespacedKey(TigerBeach.plugin(), "shovel");

    private final NamespacedKey aisleIcon = new NamespacedKey(TigerBeach.plugin(), "aisle_icon");


    private final NamespacedKey itemKey = new NamespacedKey(TigerBeach.plugin(), "item_key");

    private final NamespacedKey itemAisleKey = new NamespacedKey(TigerBeach.plugin(), "aisle_item_key"); // aisleId:productId


    public NamespacedKey itemKey() {
        return itemKey;
    }

    public NamespacedKey excellentCratesKey() {
        return excellentCratesKey;
    }

    public NamespacedKey food() {
        return food;
    }

    public NamespacedKey treasureDetector() {
        return treasureDetector;
    }

    public NamespacedKey aisleIcon() {return aisleIcon;}

    public NamespacedKey shovel() {
        return shovel;
    }

    public NamespacedKey itemAisleKey() {return itemAisleKey;}

    public NamespacedKey cosmetic() {
        return cosmetic;
    }

    public NamespacedKey progression() {
        return this.progression;
    }


    public NamespacedKey mineReward() {
        return this.mineReward;
    }

    public NamespacedKey conversion() {
        return conversion;
    }

    public NamespacedKey priceVariable() {
        return priceVariable;
    }
}
