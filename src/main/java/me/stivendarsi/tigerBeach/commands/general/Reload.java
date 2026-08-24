package me.stivendarsi.tigerBeach.commands.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;

import java.awt.*;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;
import static me.stivendarsi.tigerBeach.TigerBeach.plugin;


public class Reload implements Command<CommandSourceStack> {
   public int run(CommandContext<CommandSourceStack> context) {
      CommandSourceStack source = context.getSource();
      plugin().getServer().getOnlinePlayers().forEach(HumanEntity::closeInventory);

      mainHandler().inventoryHandler().reload();
      plugin().reloadConfig();
      mainHandler().itemGroupsManager().loadGroups();
      mainHandler().itemGroupsManager().loadItems();
      mainHandler().itemDefinitionSystemHandler().load();
      mainHandler().minesHandler().loadMines();
      mainHandler().inventoryHandler().load();
      mainHandler().utilityManager().load();
      mainHandler().shopHandler().load();
      mainHandler().tradeHandler().load();
      mainHandler().treasureHandler().load();
      mainHandler().userHandler().loadUsers();
      source.getSender().sendRichMessage(mainHandler().utilityManager().reloadCommandMessage());

      mainHandler().userHandler().refreshInventories();
      return 1;
   }
}
