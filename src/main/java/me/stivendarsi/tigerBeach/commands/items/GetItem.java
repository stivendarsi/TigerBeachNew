package me.stivendarsi.tigerBeach.commands.items;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class GetItem implements Command<CommandSourceStack> {
   public int run(CommandContext<CommandSourceStack> context) {
      CommandSourceStack source = context.getSource();

      if (!(source.getExecutor() instanceof Player player)) return 1;
      String conversionGroupString = context.getArgument("item definition group", String.class);
      String conversionNodeString = (context.getArgument("item definition id", String.class));
      ItemDefinitionSection itemDefinitionSection = mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(conversionGroupString, conversionNodeString);
      if (itemDefinitionSection != null) {
         ItemStack itemStack = itemDefinitionSection.getItem();
         if (itemStack != null) {
            player.getInventory().addItem(itemStack);
            return 1;
         }
      }
      player.sendRichMessage("<red>נקודת ההתקדמות שצוינה אינה קיימת, בדוק שכתבת נכון את הפרטים או פנה לסטיבן");
      return 1;
   }
}
