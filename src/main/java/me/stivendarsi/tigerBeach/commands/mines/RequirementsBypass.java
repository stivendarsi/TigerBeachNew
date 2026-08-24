package me.stivendarsi.tigerBeach.commands.mines;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;


public class RequirementsBypass implements Command<CommandSourceStack> {
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getExecutor() instanceof Player player)) return 1;
        UUID uuid = player.getUniqueId();
        if (mainHandler().minesHandler().hasPlayerRequirementsBypass(uuid)) {
            mainHandler().minesHandler().removePlayerRequirementsBypass(uuid);
            player.sendRichMessage("<red>אינך מסוגל לעקוף את הדרישות");
        } else {
            mainHandler().minesHandler().addPlayerRequirementsBypass(uuid);
            player.sendRichMessage("<green>אתה מסוגל לעקוף את הדרישות");
        }
        return 1;
    }
}
