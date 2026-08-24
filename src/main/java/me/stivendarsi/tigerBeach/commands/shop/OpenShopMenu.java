package me.stivendarsi.tigerBeach.commands.shop;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.stivendarsi.tigerBeach.shop.ShopMenuHolder;
import org.bukkit.entity.Player;


public class OpenShopMenu implements Command<CommandSourceStack> {
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        if (!(source.getExecutor() instanceof Player player)) return 1;

        player.openInventory(new ShopMenuHolder().getInventory());

        return 1;
    }
}
