package me.stivendarsi.tigerBeach.commands.economy;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.stivendarsi.tigerBeach.data.BeachUser;
import org.bukkit.entity.Player;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;


public class GetUserInfoCmd implements Command<CommandSourceStack> {
   public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = context.getSource();
      PlayerSelectorArgumentResolver targetResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
      Player target = targetResolver.resolve(source).getFirst();
       BeachUser beachUser = mainHandler().userHandler().getUser(target.getUniqueId());
       if (beachUser != null) {
          source.getSender().sendMessage(beachUser.getInfo());
       }

       return 1;
   }
}
