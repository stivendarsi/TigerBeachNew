package me.stivendarsi.tigerBeach.trade;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class TradeSystemHandler {
    private final Map<UUID, Long> tradeCooldownMap = new HashMap<>();
    private final Set<TradeRequest> activeTrades = new HashSet<>();
    private final Map<UUID, UUID> pendingTrades = new HashMap<>(); // Sender, Receiver

    private double cooldown;

    public void load() {
        this.tradeCooldownMap.clear();
        this.activeTrades.clear();
        this.pendingTrades.clear();
        this.cooldown = tigerBeachInstance().getConfig().getDouble("trade.cooldown");
    }

    public boolean sendTradeRequest(Player sender, Player receiver) {
        if (this.pendingTrades.getOrDefault(receiver.getUniqueId(), null) == sender.getUniqueId()) {
            acceptTradeRequest(receiver, sender);
            return true;
        }


        if (isCooldownActive(sender.getUniqueId())) {
            sender.sendRichMessage("<red>אתה צריך לחכות <time> שניות בין בקשות סחר.", Placeholder.parsed("time", String.valueOf((int) timeLeft(sender.getUniqueId()))));
            return false;
        }

        addToCooldown(sender.getUniqueId());
        if (receiver.getUniqueId() == sender.getUniqueId()) {
            sender.sendRichMessage("<red>אין אפשרות לשלוח לעצמך בקשת סחר.");
            return false;
        }
        cancelRequest(sender);

        TagResolver tagResolver = TagResolver.builder()
                .tag("sender", Tag.inserting(sender.displayName()))
                .tag("receiver", Tag.inserting(receiver.displayName()))
                .build();
        sender.sendRichMessage("<green>שלחת בקשת סחר אל <receiver>.", tagResolver);

        sender.sendRichMessage("<yellow><click:run_command:'/trade cancel'>נא ללחוץ כאן כדי לבטל.", tagResolver);

        receiver.playSound(Sound.sound(Key.key("tiger_beach:trade.incoming"), Sound.Source.UI, 1f, 1f));
        receiver.sendRichMessage("<green>קיבלת בקשת סחר מאת <sender>.", tagResolver);
        receiver.sendRichMessage("<yellow><click:run_command:'/trade accept <sender>'> נא ללחוץ כאן כדי לאשר.", tagResolver, Placeholder.parsed("sender", sender.getName()));
        pendingTrades.put(sender.getUniqueId(), receiver.getUniqueId());
        return true;
    }

    public boolean acceptTradeRequest(Player sender, Player receiver) {
        if (!this.pendingTrades.containsKey(sender.getUniqueId())) return false;
        if (this.pendingTrades.get(sender.getUniqueId()) != receiver.getUniqueId()) return false;
        if (this.activeTrades.stream().anyMatch(tradeRequest -> tradeRequest.sender() == sender.getUniqueId() || tradeRequest.receiver() == receiver.getUniqueId()))
            return false;
        TradeRequest tradeRequest = new TradeRequest(sender.getUniqueId(), receiver.getUniqueId());
        tradeRequest.acceptRequest();
        this.pendingTrades.remove(sender.getUniqueId());
        this.activeTrades.add(tradeRequest);
        return true;
    }

    public void cancelRequest(Player sender) {
        if (this.pendingTrades.containsKey(sender.getUniqueId())) sender.sendRichMessage("<red>ביטלת את בקשת הסחר.");
        this.pendingTrades.remove(sender.getUniqueId());
    }

    public void denyRequest(Player sender, Player receiver) {
        if (this.pendingTrades.containsKey(sender.getUniqueId()) && this.pendingTrades.get(sender.getUniqueId()) == receiver.getUniqueId()) {
            this.pendingTrades.remove(sender.getUniqueId());
            sender.sendRichMessage("<red>בקשת הסחר נדחתה!");
            receiver.sendRichMessage("<red>ביטלת את בקשת הסחר");
        } else {
            receiver.sendRichMessage("<red>שחקן זה לא שלח לך בקשת סחר");
        }
    }

    public void finishTrade(UUID sender) {
        activeTrades.stream()
                .filter(trade -> trade.sender().equals(sender))
                .findFirst()
                .ifPresent(this.activeTrades::remove);
    }

    public Set<UUID> userPendingRequests(UUID user) {
        return this.pendingTrades.entrySet().stream().filter(entry -> entry.getValue() == user).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public boolean userHasSentToUser(UUID sender, UUID receiver){
        return this.pendingTrades.get(sender) == receiver;
    }


    public boolean isCooldownActive(UUID sender) {
        if (timeLeft(sender) > 0) return true;
        else {
            this.tradeCooldownMap.remove(sender);
            return false;
        }
    }

    public double timeLeft(UUID sender) {
        double left = 0;
        if (this.tradeCooldownMap.containsKey(sender)) {
            double timeLeft = (this.tradeCooldownMap.get(sender) + (this.cooldown * 1000) - System.currentTimeMillis()) / 1000;
          //  System.out.println(timeLeft);
            left = Math.max(0, timeLeft);
        }
        return left;
    }

    public void addToCooldown(UUID player) {
        this.tradeCooldownMap.put(player, System.currentTimeMillis());
    }
}
