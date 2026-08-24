package me.stivendarsi.tigerBeach.commands.items;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.stivendarsi.tigerBeach.itemmanager.editor.ItemGroupsBrowser;
import org.bukkit.entity.Player;

public class Browse implements Command<CommandSourceStack> {
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getExecutor() instanceof Player player)) return 1;
        player.openInventory((new ItemGroupsBrowser()).getInventory());
        return 1;
    }
}
