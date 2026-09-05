package me.stivendarsi.tigerBeach.data;

import me.stivendarsi.tigerBeach.itemmanager.inventoryHandler.InventorySystemHandler;
import net.kyori.adventure.key.Key;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class UserSystemHandler {
    private final ConcurrentHashMap<UUID, BeachUser> beachUsers = new ConcurrentHashMap<>();


//    public UserSystemHandler() {
//        super(new File(plugin().getDataFolder(), "users.yml"));
//    }

    public void loadUsers() {
        this.beachUsers.clear();
        List<BeachUser> users = mainHandler().databaseHandler().getAllUsersAsync();
        if (users.isEmpty()) return;
        users.forEach(beachUser -> beachUsers.put(beachUser.userUUID(), beachUser));

//        for (String playerStringUUID : get().getKeys(false)) {
//            UUID playerUUID = UUID.fromString(playerStringUUID);
//            BeachUser beachUser = this.buildUser(playerUUID);
//            if (beachUser != null) {
//                this.beachUsers.put(playerUUID, beachUser);
//            }
//        }
    }

//    public boolean convertYamlUsersToDataBase() {
//        List<BeachUser> beachUsers = new ArrayList<>();
//        for (String playerStringUUID : get().getKeys(false)) {
//            UUID playerUUID = UUID.fromString(playerStringUUID);
//
//            ItemDefinitionSection pickaxe = definitionSection("pickaxe", playerStringUUID);
//            ItemDefinitionSection sword = definitionSection("sword", playerStringUUID);
//            ItemDefinitionSection helmet = definitionSection("helmet", playerStringUUID);
//            ItemDefinitionSection chestplate = definitionSection("chestplate", playerStringUUID);
//            ItemDefinitionSection leggings = definitionSection("leggings", playerStringUUID);
//            ItemDefinitionSection boots = definitionSection("boots", playerStringUUID);
//
//            double money = get().getDouble("%s.money".formatted(playerStringUUID));
//            boolean bypass = get().getBoolean("%s.bypass_progression".formatted(playerStringUUID));
//
//
//            UserProgression userProgression = new UserProgression(playerUUID, pickaxe, sword, helmet, chestplate, leggings, boots);
//
//
//            BeachUser beachUser = new BeachUser(playerUUID, money, userProgression, bypass);
//            beachUsers.add(beachUser);
//
//        }
//        mainHandler().databaseHandler().saveUsersAsync(beachUsers);
//        return true;
//    }
//
//    private ItemDefinitionSection definitionSection(String group, String playerStringUUID) {
//
//        String id;
//        if (get().isConfigurationSection("%s.progression.%s".formatted(playerStringUUID, group))) {
//            id = get().getString("%s.progression.%s.item_definition_id".formatted(playerStringUUID, group));
//        } else {
//            id = get().getString("%s.progression.%s".formatted(playerStringUUID, group));
//        }
//        return mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(group, id);
//
//    }

    public void addUser(BeachUser beachUser) {
        mainHandler().databaseHandler().saveUserAsync(beachUser);
        this.beachUsers.put(beachUser.userUUID(), beachUser);
    }


    public String getProgressionItemRarity(Key itemDefinitionKey) {
        if (itemDefinitionKey == null) return mainHandler().utilityManager().getRarity(100);
        InventorySystemHandler.InventorySlot inventorySlot = InventorySystemHandler.InventorySlot.of(itemDefinitionKey.namespace());
        int sum = 0;
        if (inventorySlot != null) {
            switch (inventorySlot) {
                case PICKAXE ->
                        sum += (int) this.beachUsers.values().stream().filter(beachUser -> beachUser.userProgression().pickaxe().itemDefinitionId().equalsIgnoreCase(itemDefinitionKey.value())).count();
                case SWORD ->
                        sum += (int) this.beachUsers.values().stream().filter(beachUser -> beachUser.userProgression().sword().itemDefinitionId().equalsIgnoreCase(itemDefinitionKey.value())).count();
                case HELMET ->
                        sum += (int) this.beachUsers.values().stream().filter(beachUser -> beachUser.userProgression().helmet().itemDefinitionId().equalsIgnoreCase(itemDefinitionKey.value())).count();
                case CHESTPLATE ->
                        sum += (int) this.beachUsers.values().stream().filter(beachUser -> beachUser.userProgression().chestplate().itemDefinitionId().equalsIgnoreCase(itemDefinitionKey.value())).count();
                case LEGGINGS ->
                        sum += (int) this.beachUsers.values().stream().filter(beachUser -> beachUser.userProgression().leggings().itemDefinitionId().equalsIgnoreCase(itemDefinitionKey.value())).count();
                case BOOTS ->
                        sum += (int) this.beachUsers.values().stream().filter(beachUser -> beachUser.userProgression().boots().itemDefinitionId().equalsIgnoreCase(itemDefinitionKey.value())).count();
            }
        }
        int avg = (int) Math.round((double) sum / this.beachUsers.size() * 100);
        return mainHandler().utilityManager().getRarity(avg);
    }


    public void refreshInventories() {
        this.beachUsers.forEach((uuid, beachUser) -> beachUser.loadUserInventory());
    }

//    public BeachUser buildUser(UUID playerUUID) {
//        BeachUser.Builder builder = BeachUser.beachUser(playerUUID);
//       // double money = get().getDouble("%s.money".formatted(playerUUID), 0.0D);
//        builder.setMoneyAmount(0);
//
//
//        boolean bypass = get().getBoolean("%s.bypass_progression".formatted(playerUUID));
//        builder.setBypassProgression(bypass);
//
//        ConfigurationSection progressionSection = get().getConfigurationSection("%s.progression".formatted(playerUUID));
//        UserProgression userProgression = new UserProgression(playerUUID, progressionSection);
//
//        builder.setProgression(userProgression);
//        return builder.build();
//    }

//    public void registerUser(BeachUser beachUser) {
//        UUID uuid = beachUser.userUUID();
//        get().set("%s.money".formatted(uuid), beachUser.moneyAmount());
//
//        get().set("%s.bypass_progression".formatted(uuid), beachUser.bypassProgression());
//
//        registerSlot(uuid, InventorySystemHandler.InventorySlot.PICKAXE, beachUser.userProgression().pickaxe());
//        registerSlot(uuid, InventorySystemHandler.InventorySlot.SWORD, beachUser.userProgression().sword());
//        registerSlot(uuid, InventorySystemHandler.InventorySlot.HELMET, beachUser.userProgression().helmet());
//        registerSlot(uuid, InventorySystemHandler.InventorySlot.CHESTPLATE, beachUser.userProgression().chestplate());
//        registerSlot(uuid, InventorySystemHandler.InventorySlot.LEGGINGS, beachUser.userProgression().leggings());
//        registerSlot(uuid, InventorySystemHandler.InventorySlot.BOOTS, beachUser.userProgression().boots());
//        save();
//        this.beachUsers.put(uuid, beachUser);
//    }
//    private void registerSlot(UUID uuid, InventorySystemHandler.InventorySlot inventorySlot, ItemDefinitionSection section){
//        get().set("%s.progression.%s.item_definition_group".formatted(uuid, inventorySlot.name().toLowerCase()), section.itemDefinitionGroupName());
//        get().set("%s.progression.%s.item_definition_id".formatted(uuid, inventorySlot.name().toLowerCase()), section.itemDefinitionId());
//    }

    public boolean isPlayerLoaded(UUID uuid) {
        return this.beachUsers.containsKey(uuid);
    }

    @Nullable
    public BeachUser getUser(UUID userUUID) {
        return this.beachUsers.getOrDefault(userUUID, null);
    }
}
