package me.stivendarsi.tigerBeach.commands.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;


public class EnderChest implements Command<CommandSourceStack> {
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Entity entity = source.getExecutor();
        if (!(entity instanceof Player player)) return 1;
        player.openInventory(player.getEnderChest());
        return 1;
    }
}
