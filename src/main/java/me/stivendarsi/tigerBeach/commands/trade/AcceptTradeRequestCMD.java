package me.stivendarsi.tigerBeach.commands.trade;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;


public class AcceptTradeRequestCMD implements Command<CommandSourceStack> {
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (!(source.getExecutor() instanceof Player receiver)) return 1;

        String playerName = context.getArgument("player", String.class);
        Player sender = Bukkit.getPlayer(playerName);
        if (sender == null) {
            receiver.sendRichMessage("<red>השחקן אינו מחובר.");
            return 1;
        }

       if ( mainHandler().tradeHandler().acceptTradeRequest(sender,receiver)){
           receiver.sendRichMessage("<red>לא ניתן לקבל בקשת סחר. אולי היא כבר לא תקפה.");
       }

        return 1;
    }
}
