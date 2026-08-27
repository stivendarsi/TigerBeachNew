package me.stivendarsi.tigerBeach.commands.items;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class GetItem implements Command<CommandSourceStack> {
   public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = context.getSource();

      if (!(source.getExecutor() instanceof Player sender)) return 1;
      String conversionGroupString = context.getArgument("item definition group", String.class);
      String conversionNodeString = (context.getArgument("item definition id", String.class));

      final PlayerSelectorArgumentResolver targetResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
      final Player target = targetResolver.resolve(context.getSource()).getFirst();

      int amount = context.getArgument("amount", Integer.class);

      ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(conversionGroupString, conversionNodeString);
      if (itemDefinitionSection != null) {
         ItemStack itemStack = itemDefinitionSection.getItem();
         if (itemStack != null) {
            target.getInventory().addItem(itemStack.asQuantity(amount));
            return 1;
         }
      }
      sender.sendRichMessage("<red>נקודת ההתקדמות שצוינה אינה קיימת, בדוק שכתבת נכון את הפרטים או פנה לסטיבן");
      return 1;
   }
}
