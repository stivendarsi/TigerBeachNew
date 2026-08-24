package me.stivendarsi.tigerBeach.commands.trade;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;


public class SendTradeRequest implements Command<CommandSourceStack> {
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (!(source.getExecutor() instanceof Player sender)) return 1;

        final String receiverString = context.getArgument("player", String.class);
        Player receiver = Bukkit.getPlayer(receiverString);
        if (receiver == null) return 1;

        mainHandler().tradeHandler().sendTradeRequest(sender, receiver);
        return 1;
    }
}
