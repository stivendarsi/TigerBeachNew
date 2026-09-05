package me.stivendarsi.tigerBeach.trade.event;

import me.stivendarsi.tigerBeach.trade.TradeMenuHolder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;
import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class TradeMenuEventHandler implements Listener {
    @EventHandler
    public void onclick(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof TradeMenuHolder tradeMenu)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (tradeMenu.isSender(player.getUniqueId())) {
            if (event.getRawSlots().stream().anyMatch(integer -> (integer < tradeMenu.getInventory().getSize()) && (integer % 9 > 4))){
                event.setCancelled(true);
                return;
            }
        } else {
            if (event.getRawSlots().stream().anyMatch(integer -> (integer < tradeMenu.getInventory().getSize()) && (integer % 9 < 4))){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onclick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof TradeMenuHolder tradeMenu)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getAction() == InventoryAction.HOTBAR_SWAP) {
            event.setCancelled(true);
            return;
        }

        if (event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack != null) {
            if (itemStack.getPersistentDataContainer().has(mainHandler().constants().progression())) {
                event.setCancelled(true);
                player.sendRichMessage("<red>אתה לא יכול לסחור עם פריטי התקדמות");
                player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.UI, 1F, 1F));
                return;
            }
        }


        int clickedSlot = event.getRawSlot();
        if (clickedSlot > event.getInventory().getSize()) return;

        if (clickedSlot < 9 || clickedSlot % 9 == 4) {
            event.setCancelled(true);
            if (clickedSlot != 1 && clickedSlot != 7) return;
        }

        if (tradeMenu.isSender(player.getUniqueId())) {
            if (clickedSlot % 9 > 4) {
                event.setCancelled(true);
                return;
            }
            if (clickedSlot == 1) {
                tradeMenu.changeTradeStatus(tradeMenu.sender());
            } else if (tradeMenu.isSenderReady()) {
                tradeMenu.changeTradeStatus(tradeMenu.sender());
            }
        } else {
            if (clickedSlot % 9 < 4) {
                event.setCancelled(true);
                return;
            }
            if (clickedSlot == 7) {
                tradeMenu.changeTradeStatus(tradeMenu.receiver());
            } else if (tradeMenu.isReceiverReady()) {
                tradeMenu.changeTradeStatus(tradeMenu.receiver());
            }
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof TradeMenuHolder tradeMenu)) return;
        if (event.getReason() == InventoryCloseEvent.Reason.PLAYER)
            event.getPlayer().playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.UI, 1F, 1F));
        Player sender = Bukkit.getPlayer(tradeMenu.sender());
        Player receiver = Bukkit.getPlayer(tradeMenu.receiver());

        if (!tradeMenu.isTradeActive()) return;

        returnUserItemstacks(tradeMenu, sender);
        returnUserItemstacks(tradeMenu, receiver);

        mainHandler().tradeHandler().finishTrade(tradeMenu.sender());
        if (tradeMenu.isSender(event.getPlayer().getUniqueId())) {
            if (receiver != null && receiver.getOpenInventory().getType() != InventoryType.CRAFTING) {
                tigerBeachInstance().getServer().getScheduler().runTaskLater(tigerBeachInstance(), bukkitTask -> receiver.closeInventory(), 1);
            }
        } else {
            if (sender != null && sender.getOpenInventory().getType() != InventoryType.CRAFTING) {
                tigerBeachInstance().getServer().getScheduler().runTaskLater(tigerBeachInstance(), bukkitTask -> sender.closeInventory(), 1);
            }
        }

        tradeMenu.setTradeActive(false);
    }

    private void returnUserItemstacks(TradeMenuHolder tradeMenu, @Nullable Player user) {
        if (user == null) return;
        if (tradeMenu.isSender(user.getUniqueId())) {
            for (int i = 9; i < tradeMenu.getInventory().getSize(); i++) {
                if (i % 9 < 4) {
                    ItemStack itemStack = tradeMenu.getInventory().getItem(i);
                    if (itemStack != null) user.getInventory().addItem(itemStack);
                }
            }
        } else {
            for (int i = 9; i < tradeMenu.getInventory().getSize(); i++) {
                if (i % 9 > 4) {
                    ItemStack itemStack = tradeMenu.getInventory().getItem(i);
                    if (itemStack != null) user.getInventory().addItem(itemStack);
                }
            }
        }
    }
}
