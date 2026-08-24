package me.stivendarsi.tigerBeach.commands.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Hat implements Command<CommandSourceStack> {
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Entity entity = source.getExecutor();
        if (!(entity instanceof Player player)) return 1;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemStack helmet = player.getInventory().getHelmet();
        player.getInventory().setHelmet(itemStack.clone());
        if (helmet == null){
            itemStack.setAmount(-1);
        } else {
            player.getInventory().setItemInMainHand(helmet);
        }

        return 1;
    }
}
