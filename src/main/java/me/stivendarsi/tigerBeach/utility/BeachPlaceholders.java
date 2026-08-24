package me.stivendarsi.tigerBeach.utility;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.stivendarsi.tigerBeach.data.BeachUser;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class BeachPlaceholders extends PlaceholderExpansion {
   @NotNull
   public String getAuthor() {
      return "TigerDev";
   }

   @NotNull
   public String getIdentifier() {
      return "beach";
   }

   @NotNull
   public String getVersion() {
      return "1.0.0";
   }

   public String onRequest(OfflinePlayer player, @NotNull String params) {
      if (params.equalsIgnoreCase("beach_money")) {
         BeachUser beachUser = mainHandler().userHandler().getUser(player.getUniqueId());
         if (beachUser != null) {
            return String.valueOf(beachUser.moneyAmount());
         }
      }
      return null;
   }
}
