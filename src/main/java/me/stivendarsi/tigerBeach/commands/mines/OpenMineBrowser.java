package me.stivendarsi.tigerBeach.commands.mines;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.stivendarsi.tigerBeach.mine.editor.MineBrowserMenu;
import org.bukkit.entity.Player;


public class OpenMineBrowser implements Command<CommandSourceStack> {
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getExecutor() instanceof Player player)) return 1;
        player.openInventory(new MineBrowserMenu().getInventory());
        return 1;
    }
}
