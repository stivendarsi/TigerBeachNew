package me.stivendarsi.tigerBeach.data;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.stivendarsi.tigerBeach.TigerBeach;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.UUID;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class UserJoinEventHandler implements Listener {
    @EventHandler
    public void handlerPlayerWorldLoad(PlayerClientLoadedWorldEvent event) {
        if (!event.isTimeout()) {
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();
            BeachUser beachUser = mainHandler().userHandler().getUser(playerUUID);
            if (beachUser == null) throw new RuntimeException("null user");

            if (!beachUser.bypassProgression()) beachUser.loadUserInventory();
        }
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (!mainHandler().userHandler().isPlayerLoaded(playerUUID)) {

            TigerBeach.plugin().getLogger().info("Does not Contain: " + playerUUID);
            BeachUser beachUser = BeachUser.defaultUser(playerUUID);
            mainHandler().userHandler().addUser(beachUser);
        }
        Title title = Title.title(Component.text(mainHandler().utilityManager().joinScreenText()), Component.empty(), Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ofMillis(300L)));
        player.showTitle(title);
    }
}
