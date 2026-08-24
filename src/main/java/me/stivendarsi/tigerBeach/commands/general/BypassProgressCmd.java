package me.stivendarsi.tigerBeach.commands.general;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.stivendarsi.tigerBeach.TigerBeach;
import me.stivendarsi.tigerBeach.data.BeachUser;
import org.bukkit.entity.Player;



public class BypassProgressCmd implements Command<CommandSourceStack> {
   public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = context.getSource();

      final PlayerSelectorArgumentResolver targetResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
      final Player target = targetResolver.resolve(source).getFirst();

      BeachUser beachUser = TigerBeach.mainHandler().userHandler().getUser(target.getUniqueId());
      if (beachUser == null) return 0;
      beachUser.setBypassProgression(!beachUser.bypassProgression());
      if (beachUser.bypassProgression()){
         source.getSender().sendRichMessage("<green>השחקן " + target.getName() + " עוקף את ההתקדמות");
      } else {
         source.getSender().sendRichMessage("<red>השחקן " + target.getName() + " אינו עוקף את ההתקדמות");
      }
      return 1;
   }
}
