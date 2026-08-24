package me.stivendarsi.tigerBeach.commands.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.stivendarsi.tigerBeach.trash.TrashMenu;
import org.bukkit.entity.Player;


public class Trash implements Command<CommandSourceStack> {
   public int run(CommandContext<CommandSourceStack> context) {
      CommandSourceStack source = context.getSource();
      if (source.getExecutor() instanceof Player player) {
         player.openInventory(new TrashMenu().getInventory());
      }
      return 1;
   }
}
