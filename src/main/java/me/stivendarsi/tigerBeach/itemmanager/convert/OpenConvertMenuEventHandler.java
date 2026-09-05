package me.stivendarsi.tigerBeach.itemmanager.convert;

import io.papermc.paper.persistence.PersistentDataContainerView;
import me.stivendarsi.tigerBeach.data.BeachUser;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.tags.ConversionTag;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class OpenConvertMenuEventHandler implements Listener {
    @EventHandler
    public void beachItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getHand() == EquipmentSlot.HAND && player.isSneaking())) return;
        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        PersistentDataContainerView pdc = itemStack.getPersistentDataContainer();
        if (!pdc.has(mainHandler().constants().conversion())) return;

        player.updateInventory();
        BeachUser beachUser = mainHandler().userHandler().getUser(player.getUniqueId());
        if (beachUser == null) {
            player.sendRichMessage("<red>אופס! נראה שאתה לא קיים במערכת. נא לפתוח טיקט מיד!!");
            return;
        }

        String itemKeyString = pdc.get(mainHandler().constants().itemKey(), PersistentDataType.STRING);
        Key itemKey = Key.key(itemKeyString);

        ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(itemKey.namespace(), itemKey.value());
        if (itemDefinitionSection == null) throw new RuntimeException("Null Current Node");

        ConversionTag conversionTag = itemDefinitionSection.conversionTag();
        // Filter if equal or no
        if (conversionTag == null || conversionTag.next() == null || itemDefinitionSection.itemDefinitionId().equalsIgnoreCase(conversionTag.next().itemDefinitionId()))
            return;


        if (event.getAction() == Action.PHYSICAL) return;
        if (event.getAction().isRightClick()) {
            player.openInventory(new ConvertMenuHolder(beachUser, itemDefinitionSection).getInventory());
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK && pdc.has(mainHandler().constants().mineReward())) {
            // No menu convert
            if (player.hasPermission("beach.fastconvert")) beachUser.tryToUpgrade(player, itemDefinitionSection);
        } else if (event.getAction() == Action.LEFT_CLICK_AIR && pdc.has(mainHandler().constants().mineReward())) {
            if (player.hasPermission("beach.fastconvert")) beachUser.tryToUpgrade(player, itemDefinitionSection);
        }
    }
}
