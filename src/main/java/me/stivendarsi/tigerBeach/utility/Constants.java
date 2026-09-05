package me.stivendarsi.tigerBeach.utility;

import me.stivendarsi.tigerBeach.TigerBeach;
import org.bukkit.NamespacedKey;

public class Constants {
    private final NamespacedKey progression = new NamespacedKey(TigerBeach.tigerBeachInstance(), "progression");
    private final NamespacedKey cosmetic = new NamespacedKey(TigerBeach.tigerBeachInstance(), "cosmetic");
    private final NamespacedKey conversion = new NamespacedKey(TigerBeach.tigerBeachInstance(), "conversion");
    private final NamespacedKey mineReward = new NamespacedKey(TigerBeach.tigerBeachInstance(), "reward");
    private final NamespacedKey food = new NamespacedKey(TigerBeach.tigerBeachInstance(), "food");
    private final NamespacedKey priceVariable = new NamespacedKey(TigerBeach.tigerBeachInstance(), "price_variable");
    private final NamespacedKey treasureDetector = new NamespacedKey(TigerBeach.tigerBeachInstance(), "treasure_detector");



    private final NamespacedKey excellentCratesKey = new NamespacedKey("excellentcrates", "crate_key.id");

    private final NamespacedKey shovel = new NamespacedKey(TigerBeach.tigerBeachInstance(), "shovel");

    private final NamespacedKey aisleIcon = new NamespacedKey(TigerBeach.tigerBeachInstance(), "aisle_icon");


    private final NamespacedKey itemKey = new NamespacedKey(TigerBeach.tigerBeachInstance(), "item_key");

    private final NamespacedKey itemAisleKey = new NamespacedKey(TigerBeach.tigerBeachInstance(), "aisle_item_key"); // aisleId:productId


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
