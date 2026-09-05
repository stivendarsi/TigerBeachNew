package me.stivendarsi.tigerBeach.utility;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.stivendarsi.tigerBeach.data.BeachUser;
import me.stivendarsi.tigerBeach.data.UserProgression;
import me.stivendarsi.tigerBeach.itemmanager.inventoryHandler.InventorySystemHandler;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import net.kyori.adventure.key.Key;

import org.jetbrains.annotations.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;
import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class DatabaseHandler {
    private HikariDataSource dataSource;
    private String username;
    private String password;
    private String JdbcUrl;


    public void load() {
        if (this.dataSource != null && !this.dataSource.isClosed()) this.dataSource.close();
        this.username = tigerBeachInstance().getConfig().getString("database.username");
        this.password = tigerBeachInstance().getConfig().getString("database.password");
        this.JdbcUrl = tigerBeachInstance().getConfig().getString("database.jdbc-url");
        try {
            connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void connect() throws SQLException {
        if (this.username == null || this.password == null || this.JdbcUrl == null) {
            tigerBeachInstance().getLogger().warning("Some database info are null.");
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(this.JdbcUrl); // Address of your running MySQL database
        config.setUsername(this.username); // Username
        config.setPassword(this.password); // Password
        config.setMaximumPoolSize(20);

        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(10000);

        config.addDataSourceProperty("", ""); // MISC settings to add
        dataSource = new HikariDataSource(config);

        Connection connection = dataSource.getConnection();
        String createTable = """
                    CREATE TABLE IF NOT EXISTS beach_users_data (
                        uuid VARCHAR(36) PRIMARY KEY,
                        balance FLOAT(5),
                        bypass_progression BOOL,
                        current_pickaxe_key VARCHAR(255),
                        current_sword_key VARCHAR(255),
                        current_helmet_key VARCHAR(255),
                        current_chestplate_key VARCHAR(255),
                        current_leggings_key VARCHAR(255),
                        current_boots_key VARCHAR(255)
                    )
                """;
        try (PreparedStatement createStmt = connection.prepareStatement(createTable)) {
            createStmt.executeUpdate();
            createStmt.close();
            connection.close();
        }
    }


    private BeachUser loadUser(ResultSet rs) throws SQLException {
        UUID uuid = UUID.fromString(rs.getString("uuid"));
        BeachUser.Builder beachUser = BeachUser.beachUser(uuid);
        boolean bypassProgression = rs.getBoolean("bypass_progression");
        double balance = rs.getDouble("balance");
        ItemDefinitionSection pickaxeKey = loadItemKey(InventorySystemHandler.InventorySlot.PICKAXE, "current_pickaxe_key", rs);
        ItemDefinitionSection swordKey = loadItemKey(InventorySystemHandler.InventorySlot.SWORD, "current_sword_key", rs);
        ItemDefinitionSection helmetKey = loadItemKey(InventorySystemHandler.InventorySlot.HELMET, "current_helmet_key", rs);
        ItemDefinitionSection chestplateKey = loadItemKey(InventorySystemHandler.InventorySlot.CHESTPLATE, "current_chestplate_key", rs);
        ItemDefinitionSection leggingsKey = loadItemKey(InventorySystemHandler.InventorySlot.LEGGINGS, "current_leggings_key", rs);
        ItemDefinitionSection bootsKey = loadItemKey(InventorySystemHandler.InventorySlot.BOOTS, "current_boots_key", rs);

        UserProgression userProgression = new UserProgression(uuid, pickaxeKey, swordKey, helmetKey, chestplateKey, leggingsKey, bootsKey);
        beachUser.setProgression(userProgression);
        beachUser.setBypassProgression(bypassProgression);
        beachUser.setMoneyAmount(balance);
        return beachUser.build();
    }

    public List<BeachUser> getAllUsersAsync() {
        String sqlQuery = "SELECT * FROM beach_users_data";
        List<BeachUser> users = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                BeachUser beachUser = loadUser(rs);
                users.add(beachUser);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }


    public @Nullable BeachUser getUser(UUID uuid) {
        String sqlQuery = "SELECT * FROM beach_users_data WHERE uuid = ?";

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sqlQuery)
        ) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return loadUser(rs);
                }
                connection.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveUserAsync(BeachUser beachUser) {
        tigerBeachInstance().getServer().getAsyncScheduler().runNow(tigerBeachInstance(), task -> {
            try {
                Connection connection = dataSource.getConnection();
                String sqlQuery = """
                            REPLACE INTO beach_users_data (
                                uuid, balance, bypass_progression, 
                                current_pickaxe_key, current_sword_key, current_helmet_key, 
                                current_chestplate_key, current_leggings_key, current_boots_key
                            ) 
                            VALUES (?,?,?,?,?,?,?,?,?);
                        """;
                try (PreparedStatement stmt = connection.prepareStatement(sqlQuery)) {
                    stmt.setString(1, beachUser.userUUID().toString());
                    stmt.setDouble(2, beachUser.moneyAmount());
                    stmt.setBoolean(3, beachUser.bypassProgression());
                    stmt.setString(4, beachUser.userProgression().pickaxe().key().asString());
                    stmt.setString(5, beachUser.userProgression().sword().key().asString());
                    stmt.setString(6, beachUser.userProgression().helmet().key().asString());
                    stmt.setString(7, beachUser.userProgression().chestplate().key().asString());
                    stmt.setString(8, beachUser.userProgression().leggings().key().asString());
                    stmt.setString(9, beachUser.userProgression().boots().key().asString());
                    stmt.execute();
                    stmt.close();
                }
                connection.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void saveUsersAsync(List<BeachUser> beachUsers) {
        tigerBeachInstance().getServer().getAsyncScheduler().runNow(tigerBeachInstance(), task -> {
            String sqlQuery = """
            INSERT INTO beach_users_data (
                uuid, balance, bypass_progression, current_pickaxe_key,
                current_sword_key, current_helmet_key, current_chestplate_key,
                current_leggings_key, current_boots_key
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                balance = VALUES(balance),
                bypass_progression = VALUES(bypass_progression),
                current_pickaxe_key = VALUES(current_pickaxe_key),
                current_sword_key = VALUES(current_sword_key),
                current_helmet_key = VALUES(current_helmet_key),
                current_chestplate_key = VALUES(current_chestplate_key),
                current_leggings_key = VALUES(current_leggings_key),
                current_boots_key = VALUES(current_boots_key)
            """;

            try (
                    Connection connection = dataSource.getConnection();
                    PreparedStatement stmt = connection.prepareStatement(sqlQuery)
            ) {
                for (BeachUser beachUser : beachUsers) {
                    stmt.setString(1, beachUser.userUUID().toString());
                    stmt.setDouble(2, beachUser.moneyAmount());
                    stmt.setBoolean(3, beachUser.bypassProgression());
                    stmt.setString(4, beachUser.userProgression().pickaxe().key().asString());
                    stmt.setString(5, beachUser.userProgression().sword().key().asString());
                    stmt.setString(6, beachUser.userProgression().helmet().key().asString());
                    stmt.setString(7, beachUser.userProgression().chestplate().key().asString());
                    stmt.setString(8, beachUser.userProgression().leggings().key().asString());
                    stmt.setString(9, beachUser.userProgression().boots().key().asString());

                    stmt.addBatch();
                }

                stmt.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private ItemDefinitionSection loadItemKey(InventorySystemHandler.InventorySlot inventorySlot, String section, ResultSet resultSet) throws SQLException {
        String itemKey = resultSet.getString(section);
        if (itemKey == null) itemKey = mainHandler().inventoryHandler().of(inventorySlot).key().asString();
        Key key = Key.key(itemKey);
        return mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(key.namespace(), key.value());
    }

    public enum ColumnType {
        BALANCE("balance"),
        BYPASS_PROGRESSION("bypass_progression"),
        UUID("uuid"),
        PICKAXE("current_pickaxe_key"),
        SWORD("current_sword_key"),
        HELMET("current_helmet_key"),
        CHESTPLATE("current_chestplate_key"),
        LEGGINGS("current_leggings_key"),
        BOOTS("current_boots_key");
        private final String columnId;

        ColumnType(String columnId) {
            this.columnId = columnId;
        }

        public String columnId() {
            return columnId;
        }
    }

    public void updateUserColumn(UUID uuid, ColumnType column, Object value) {
        String sql = "UPDATE beach_users_data SET " + column.columnId() + " = ? WHERE uuid = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setObject(1, value);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}