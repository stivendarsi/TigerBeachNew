package me.stivendarsi.tigerBeach.data;

import io.papermc.paper.persistence.PersistentDataContainerView;
import me.stivendarsi.tigerBeach.TigerBeach;
import me.stivendarsi.tigerBeach.itemmanager.convert.ConvertMenuHolder;
import me.stivendarsi.tigerBeach.itemmanager.inventoryHandler.InventorySystemHandler;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.tags.ConversionTag;
import me.stivendarsi.tigerBeach.utility.DatabaseHandler;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class BeachUser {
    private final UUID userUUID;
    private double moneyAmount;
    private final @NotNull UserProgression userProgression;
    private boolean bypassProgression;
    private int page;

    private boolean[] inventoryCheck;

    public BeachUser(UUID userUUID, double moneyAmount, @NotNull UserProgression userProgression, boolean bypassProgression) {
        this.userUUID = userUUID;
        this.moneyAmount = moneyAmount;
        this.bypassProgression = bypassProgression;

        this.userProgression = userProgression;
        this.inventoryCheck = new boolean[6];
    }

    public static BeachUser defaultUser(UUID uuid) {
        return new BeachUser(uuid, 0, UserProgression.defaultUser(uuid), false);
    }

    public UUID userUUID() {
        return this.userUUID;
    }

    public double moneyAmount() {
        return this.moneyAmount;
    }

    public boolean bypassProgression() {
        return bypassProgression;
    }

    public BeachUser setBypassProgression(boolean bypassProgression) {
        this.bypassProgression = bypassProgression;
        mainHandler().databaseHandler().updateUserColumn(this.userUUID, DatabaseHandler.ColumnType.BYPASS_PROGRESSION, bypassProgression);
        return this;
    }

    public void saveAsync() {
        mainHandler().databaseHandler().saveUserAsync(this);
    }

    public void tryToUpgrade(Player player, ItemDefinitionSection itemDefinitionSection) {
        if (this.userProgression.canUpgrade(player, itemDefinitionSection)) {
            player.playSound(Sound.sound().type(Key.key("entity.experience_orb.pickup")).build());
            // Convert
            this.userProgression.payAndConvert(itemDefinitionSection, player);
            if (!(player.getOpenInventory().getType() == InventoryType.CRAFTING || player.getOpenInventory().getType() == InventoryType.CREATIVE)) {
                if (itemDefinitionSection.containsTag(ItemDefinitionSection.ItemTag.PROGRESSION)) {
                    ConversionTag conversionTag = itemDefinitionSection.conversionTag();
                    if (conversionTag != null) {
                        ItemDefinitionSection next = conversionTag.next();
                        if (next != null) {
                            ConvertMenuHolder menuHolder = new ConvertMenuHolder(this, next);
                            if (menuHolder.nextItem() != null && !menuHolder.nextItem().itemDefinitionId().equals(next.itemDefinitionId())) {
                                player.openInventory(menuHolder.getInventory());
                                return;
                            }
                        }
                    }
                    player.closeInventory();
                } else player.openInventory(new ConvertMenuHolder(this, itemDefinitionSection).getInventory());
            }
        } else {
            player.playSound(Sound.sound().type(Key.key("entity.villager.no")).build());
            player.sendRichMessage("<red>אין אפשרות לשדרוג, יכול להיות שחסר לך מקום או שאין לך מספיק חומרים!");
        }
    }

    public UserProgression userProgression() {
        return this.userProgression;
    }

    public BeachUser addMoneyAmount(double moneyAmount) {
        this.setMoneyAmount(this.moneyAmount + moneyAmount);
        return this;
    }

    public BeachUser setMoneyAmount(double moneyAmount) {
        this.moneyAmount = moneyAmount;
        mainHandler().databaseHandler().updateUserColumn(this.userUUID, DatabaseHandler.ColumnType.BALANCE, moneyAmount);
        return this;
    }

    public BeachUser reduceMoneyAmount(double moneyAmount) {
        this.setMoneyAmount(this.moneyAmount - moneyAmount);
        return this;
    }

    public Component getInfo() {
        TagResolver tagResolver = TagResolver.builder()
                .tag("user", Tag.selfClosingInserting(Component.text(String.valueOf(this.userUUID))))
                .tag("money", Tag.selfClosingInserting(Component.text(this.moneyAmount)))
                .tag("pickaxe", Tag.selfClosingInserting(Component.text(this.userProgression.pickaxe().itemDefinitionId())))
                .tag("sword", Tag.selfClosingInserting(Component.text(this.userProgression.sword().itemDefinitionId())))
                .tag("helmet", Tag.selfClosingInserting(Component.text(this.userProgression.helmet().itemDefinitionId())))
                .tag("chestplate", Tag.selfClosingInserting(Component.text(this.userProgression.chestplate().itemDefinitionId())))
                .tag("leggings", Tag.selfClosingInserting(Component.text(this.userProgression.leggings().itemDefinitionId())))
                .tag("boots", Tag.selfClosingInserting(Component.text(this.userProgression.boots().itemDefinitionId())))
                .build();
        return MiniMessage.miniMessage().deserialize("<green>UUID: <yellow><user><newline>\n<green>Money: <yellow><money><newline>\n<green>Pickaxe: <yellow><pickaxe><newline>\n<green>Sword: <yellow><sword><newline>\n<green>Helmet: <yellow><helmet><newline>\n<green>Chestplate: <yellow><chestplate><newline>\n<green>Leggings: <yellow><leggings><newline>\n<green>Boots: <yellow><boots>\n", tagResolver);
    }

    public void loadUserInventory() {
        this.inventoryCheck = new boolean[6];
        Player player = Bukkit.getPlayer(this.userUUID);
        if (player == null || !player.isOnline()) return;
        inventoryCheck(player.getInventory());
        inventoryCheck(player.getEnderChest());

        for (int i = 0; i < inventoryCheck.length; i++) {
            if (!inventoryCheck[i]) {
                switch (i) {
                    case 0 -> add(player, userProgression.pickaxe().getItem());
                    case 1 -> add(player, userProgression.sword().getItem());
                    case 2 -> add(player, userProgression.helmet().getItem());
                    case 3 -> add(player, userProgression.chestplate().getItem());
                    case 4 -> add(player, userProgression.leggings().getItem());
                    case 5 -> add(player, userProgression.boots().getItem());
                }
            }
        }
    }

    private void add(Player player, ItemStack itemStack) {
        if (itemStack == null) throw new RuntimeException("null item");
        Map<Integer, ItemStack> map = player.getInventory().addItem(itemStack);
        if (!map.isEmpty()) {
            map.forEach((integer, itemStack1) -> player.getEnderChest().addItem(itemStack1.asQuantity(integer)));
        }
    }


    private Inventory inventoryCheck(Inventory inventory) {// pick, sword, helmet, chest, legs,feet
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;
            PersistentDataContainerView pdc = item.getPersistentDataContainer();
            if (pdc.has(mainHandler().constants().excellentCratesKey())) continue;
            if (!(pdc.has(mainHandler().constants().itemKey()))) { // progression items are set after checks
                item.setAmount(-1);
                continue;
            }
            int amount = item.getAmount();

            String id = pdc.get(mainHandler().constants().itemKey(), PersistentDataType.STRING);

            if (id == null) continue;
            Key itemDefinitionKey = Key.key(id);
            ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(itemDefinitionKey.namespace(), itemDefinitionKey.value());
            if (itemDefinitionSection == null) continue;
            ItemStack itemStack = itemDefinitionSection.getItem();
            if (itemStack == null) continue;

            InventorySystemHandler.InventorySlot slot = InventorySystemHandler.InventorySlot.of(itemDefinitionKey.namespace());
            if (slot == null) {
                TigerBeach.plugin().getLogger().warning("Replacing item!");
                itemStack.setAmount(amount);
                inventory.setItem(i, itemStack);
            } else {
                switch (slot) {
                    case PICKAXE -> {
                        if (inventoryCheck[0]){
                            item.setAmount(-1);
                        }
                        else {
                            inventory.setItem(i, this.userProgression.pickaxe().getItem());
                            inventoryCheck[0] = true;
                        }
                    }
                    case SWORD -> {
                        if (inventoryCheck[1]) item.setAmount(-1);
                        else {
                            inventory.setItem(i, this.userProgression.sword().getItem());
                            inventoryCheck[1] = true;
                        }
                    }
                    case HELMET -> {
                        if (inventoryCheck[2]) item.setAmount(-1);
                        else {
                            inventory.setItem(i, this.userProgression.helmet().getItem());
                            inventoryCheck[2] = true;
                        }
                    }
                    case CHESTPLATE -> {
                        if (inventoryCheck[3]) item.setAmount(-1);
                        else {
                            inventory.setItem(i, this.userProgression.chestplate().getItem());
                            inventoryCheck[3] = true;
                        }
                    }
                    case LEGGINGS -> {
                        if (inventoryCheck[4]) item.setAmount(-1);
                        else {
                            inventory.setItem(i, this.userProgression.leggings().getItem());
                            inventoryCheck[4] = true;
                        }
                    }

                    case BOOTS -> {
                        if (inventoryCheck[5]) item.setAmount(-1);
                        else {
                            inventory.setItem(i, this.userProgression.boots().getItem());
                            inventoryCheck[5] = true;
                        }
                    }
                    default -> item.setAmount(-1);
                }
            }
        }
        return inventory;
    }

    public static Builder beachUser(UUID uuid) {
        return new Builder(uuid);
    }

    public static class Builder {
        private final UUID userUUID;
        private double moneyAmount;
        private UserProgression userProgression;
        private boolean bypassProgression;

        private Builder(UUID uuid) {
            this.userUUID = uuid;
            this.moneyAmount = 100.0D;
        }

        public Builder setMoneyAmount(double moneyAmount) {
            this.moneyAmount = moneyAmount;
            return this;
        }

        public Builder setBypassProgression(boolean bypassProgression) {
            this.bypassProgression = bypassProgression;
            return this;
        }

        public Builder setProgression(UserProgression userProgression) {
            this.userProgression = userProgression;
            return this;
        }

        public BeachUser build() {
            return new BeachUser(this.userUUID, this.moneyAmount, this.userProgression, bypassProgression);
        }
    }
}
