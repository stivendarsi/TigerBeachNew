package me.stivendarsi.tigerBeach.commands.mines;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.stivendarsi.tigerBeach.mine.MineData;
import org.bukkit.entity.Player;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;


public class CreateMine implements Command<CommandSourceStack> {
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getExecutor() instanceof Player player)) return 1;

        String mineIdentifier = context.getArgument("id", String.class);

        if (mainHandler().minesHandler().getMine(mineIdentifier) != null) {
            player.sendRichMessage("<red>מחצבה עם שם דומה כבר קיימת");
            return 1;
        }

        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(actor);

        World selectionWorld = localSession.getSelectionWorld();

        if (selectionWorld == null || !localSession.isSelectionDefined(selectionWorld)) {
            player.sendRichMessage("<red>לא ניתן ליצור מחצבה");
            return 1;
        }
        Region region = localSession.getSelection(selectionWorld).clone();

        MineData mineData = MineData.defaultMine(mineIdentifier, region);
        mainHandler().minesHandler().registerMine(mineData);
        player.sendRichMessage("<green>יצרת מחצבה");

        return 1;
    }
}
