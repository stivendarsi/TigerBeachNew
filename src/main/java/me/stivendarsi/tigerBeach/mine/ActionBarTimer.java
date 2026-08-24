package me.stivendarsi.tigerBeach.mine;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActionBarTimer {
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private Runnable task;
    private UUID uuid;
    private Mine mine;

    public ActionBarTimer(Mine mine, UUID playerUUID) {
        this.uuid = playerUUID;
        this.mine = mine;
        this.task = () -> {
            Player player = Bukkit.getPlayer(this.uuid);
            if (player == null) {
                stop();
                return;
            }
            Location loc = player.getLocation();
            if (mine.playerInMineRange(loc)) {
                int secondsLeft = mine.timeSecondsLeft(System.currentTimeMillis());
                int percentLeft = mine.percentLeft();
                TagResolver tagResolver = TagResolver.builder()
                        .tag("time_left", Tag.inserting(Component.text(secondsLeft + " שניות")))
                        .tag("percent_left", Tag.inserting(Component.text(percentLeft + "%")))
                        .build();
                player.sendActionBar(MiniMessage.miniMessage().deserialize(mine.getActionBarMSG(), tagResolver));
            } else {
                stop();
            }
        };
    }

    public void start() {
        this.service.scheduleAtFixedRate(this.task, 20L, 1000, TimeUnit.MILLISECONDS);
    }

    public void restart() {
        this.stop();
        this.start();
    }

    public void stop() {
        if (!this.service.isShutdown()) {
            this.service.shutdown();
        }

    }
}
