package me.stivendarsi.tigerBeach.trade;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;
import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class TradeMenuHolder implements InventoryHolder {
    private final Inventory inventory;
    private final UUID sender;
    private final UUID receiver;
    private boolean isTradeActive;

    private final ItemStack readyItemstack = ItemType.LIME_CONCRETE.createItemStack(itemMeta -> {
        itemMeta.itemName(MiniMessage.miniMessage().deserialize("<gray>מצב<dark_gray>: </dark_gray><green>אושר"));
    });

    private final ItemStack notReadyItemstack = ItemType.RED_CONCRETE.createItemStack(itemMeta -> {
        itemMeta.itemName(MiniMessage.miniMessage().deserialize("<gray>מצב<dark_gray>: </dark_gray><red>לא אושר"));
    });

    private boolean isSenderReady;
    private boolean isReceiverReady;

    public TradeMenuHolder(UUID sender, UUID receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.inventory = tigerBeachInstance().getServer().createInventory(this, 54);
        this.isTradeActive = true;

        ItemStack senderHead = playerHead(sender);
        ItemStack receiverHead = playerHead(receiver);

        ItemStack glass = ItemType.GRAY_STAINED_GLASS_PANE.createItemStack();
        glass.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
        for (int i = 0; i < 9; i++) this.inventory.setItem(i, glass);
        for (int i = 9; i < this.inventory.getSize(); i++) if (i % 9 == 4) this.inventory.setItem(i, glass);

        this.inventory.setItem(1, this.notReadyItemstack);
        this.inventory.setItem(7, this.notReadyItemstack);

        this.inventory.setItem(2, senderHead);
        this.inventory.setItem(6, receiverHead);
    }

    public void changeTradeStatus(UUID user) {
        if (user == this.sender) {
            this.isSenderReady = !this.isSenderReady;
            if (isSenderReady) this.inventory.setItem(1, readyItemstack);
            else this.inventory.setItem(1, notReadyItemstack);
        } else if (user == this.receiver) {
            this.isReceiverReady = !this.isReceiverReady;
            if (isReceiverReady) this.inventory.setItem(7, readyItemstack);
            else this.inventory.setItem(7, notReadyItemstack);
        }

        Audience audience = Audience.audience(this.inventory.getViewers());
        if (trade(audience)) {
            mainHandler().tradeHandler().finishTrade(sender);
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public UUID sender() {
        return sender;
    }

    public UUID receiver() {
        return receiver;
    }

    public boolean trade(Audience audience) {
        if (!(this.isSenderReady && this.isReceiverReady)) return false;
        AtomicInteger count = new AtomicInteger();
        Sound sound = Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.UI, 1f, 1f);

        MiniMessage miniMessage = MiniMessage.miniMessage();

        tigerBeachInstance().getServer().getScheduler().runTaskTimer(tigerBeachInstance(), /* Lambda: */task -> {
            if (count.get() < 3 && (this.isSenderReady && this.isReceiverReady)) {
                audience.playSound(sound);
                audience.sendMessage(miniMessage.deserialize("<yellow>בקשת הסחר תסגר בעוד <left>", Placeholder.parsed("left", String.valueOf(3 - count.get()))));
                count.getAndIncrement();
            } else if (count.get() == 3 && (this.isSenderReady && this.isReceiverReady)) {
                task.cancel();
                if (!this.isTradeActive) return;
                this.isTradeActive = false;
                Player sender = Bukkit.getPlayer(this.sender);
                Player receiver = Bukkit.getPlayer(this.receiver);

                List<ItemStack> senderItems = new ArrayList<>();
                List<ItemStack> receiverItems = new ArrayList<>();

                for (int i = 9; i < this.inventory.getSize(); i++) {
                    int side = i % 9;
                    if (side == 4) continue;

                    ItemStack itemStack = this.inventory.getItem(i);
                    if (itemStack == null) continue;

                    if (side < 4) senderItems.add(itemStack);
                    else receiverItems.add(itemStack);

                }

                if (sender != null) {
                    sender.closeInventory();
                    receiverItems.forEach(itemStack -> sender.getInventory().addItem(itemStack));
                }
                if (receiver != null) {
                    receiver.closeInventory();
                    senderItems.forEach(itemStack -> receiver.getInventory().addItem(itemStack));
                }

            } else task.cancel();
        }, 0, 20);


        return true;
    }

    public boolean isSenderReady() {
        return isSenderReady;
    }

    public boolean isReceiverReady() {
        return isReceiverReady;
    }

    public boolean isSender(UUID user) {
        return this.sender == user;
    }

    public TradeMenuHolder setTradeActive(boolean tradeActive) {
        isTradeActive = tradeActive;
        return this;
    }

    public boolean isTradeActive() {
        return isTradeActive;
    }

    private ItemStack playerHead(UUID user) {
        ItemStack head = ItemType.PLAYER_HEAD.createItemStack();
        Player player = Bukkit.getPlayer(user);
        if (player == null) return null;
        PlayerProfile playerProfile = player.getPlayerProfile();
        head.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(playerProfile));
        head.setData(DataComponentTypes.CUSTOM_NAME, MiniMessage.miniMessage().deserialize("<!i><yellow><player>", Placeholder.parsed("player", player.getName())));
        return head;
    }
}
