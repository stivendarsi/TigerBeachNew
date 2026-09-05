package me.stivendarsi.tigerBeach.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.stivendarsi.tigerBeach.commands.economy.AddAmountCmd;
import me.stivendarsi.tigerBeach.commands.economy.GetUserInfoCmd;
import me.stivendarsi.tigerBeach.commands.economy.ReduceAmountCmd;
import me.stivendarsi.tigerBeach.commands.general.*;
import me.stivendarsi.tigerBeach.commands.items.GetItem;
import me.stivendarsi.tigerBeach.commands.mines.CreateMine;
import me.stivendarsi.tigerBeach.commands.mines.OpenMineBrowser;
import me.stivendarsi.tigerBeach.commands.mines.RequirementsBypass;
import me.stivendarsi.tigerBeach.commands.shop.OpenShopMenu;
import me.stivendarsi.tigerBeach.commands.trade.AcceptTradeRequestCMD;
import me.stivendarsi.tigerBeach.commands.trade.CancelTradeRequest;
import me.stivendarsi.tigerBeach.commands.trade.DenyTradeRequest;
import me.stivendarsi.tigerBeach.commands.trade.SendTradeRequest;
import me.stivendarsi.tigerBeach.commands.treasure.GenerateChests;
import me.stivendarsi.tigerBeach.data.BeachUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.player;
import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;
import static me.stivendarsi.tigerBeach.TigerBeach.tigerBeachInstance;

public class CommandHandler {
    public CommandHandler(LifecycleEventManager<@NotNull Plugin> manager) {
        manager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            Commands commands = event.registrar();

            commands.register(Commands.literal("shop").executes(new OpenShopMenu()).build());

            commands.register(Commands.literal("trash").executes(new Trash()).build());

            commands.register(Commands.literal("enderchest").requires(sourceStack -> sourceStack.getSender().hasPermission("beach.enderchest")).executes(new EnderChest()).build(), List.of("ec"));
            commands.register(Commands.literal("hat").requires(sourceStack -> sourceStack.getSender().hasPermission("beach.hat"))
                    .executes(new Hat()).build());


            commands.register(Commands.literal("trade")
                    .then(Commands.literal("send").then(Commands.argument("player", word()).suggests(this::onlineUsers).executes(new SendTradeRequest()))) // Sender sends a request
                    .then(Commands.literal("cancel").executes(new CancelTradeRequest())) // Sender cancels the request
                    // Receivers can have multiple senders - senders can only send one request.
                    // Therefor, the receivers must have the ability to cancel any of their pending requests by player.
                    .then(Commands.literal("accept").then(Commands.argument("player", word()).suggests(this::getUserPendingTadeRequests).executes(new AcceptTradeRequestCMD()))) // Receiver accepts the request
                    .then(Commands.literal("deny").then(Commands.argument("player", word()).suggests(this::getUserPendingTadeRequests).executes(new DenyTradeRequest()))) // Receiver Denys the request
                    .build());

            commands.register(Commands.literal("beach").requires(sourceStack -> sourceStack.getSender().hasPermission("beach.admin"))

//                    .then(Commands.literal("convertToDataBase").executes(context -> {
//                        boolean b = mainHandler().userHandler().convertYamlUsersToDataBase();
//                        if (b) context.getSource().getSender().sendRichMessage("<green>הומר !!");
//                        return 1;
//                    }))
                    .then(Commands.literal("getItem").then(Commands.argument("item definition group", word()).suggests((ctx, builder) -> {
                                mainHandler().itemDefinitionSystemHandler().itemDefinitionGroupNamed().stream()
                                        .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                        .forEach(builder::suggest);
                                return builder.buildFuture();
                            }).then(Commands.argument("item definition id", word()).suggests((ctx, builder) -> {
                                String group = ctx.getArgument("item definition group", String.class);
                                mainHandler().itemDefinitionSystemHandler().itemDefinitionIds(group).stream()
                                        .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                        .forEach(builder::suggest);
                                return builder.buildFuture();
                            }).executes(new GetItem())))
                    )

                    .then(Commands.literal("treasure").then(Commands.literal("generate").executes(new GenerateChests())).build())

                    .then(Commands.literal("bypassProgress").then(Commands.argument("player", player()).executes(new BypassProgressCmd())))
                    //    .then(Commands.literal("browse").executes(new Browse()))
                    .then(Commands.literal("mines")
                            .then(Commands.literal("browser").executes(new OpenMineBrowser()))
                            .then(Commands.literal("createMine").then(Commands.argument("id", word()).executes(new CreateMine())))
                            .then(Commands.literal("bypass").requires(source -> {
                                        if (!(source.getExecutor() instanceof Player player)) return false;
                                        BeachUser beachUser = mainHandler().userHandler().getUser(player.getUniqueId());
                                        if (beachUser == null) return false;
                                        return beachUser.bypassProgression();
                                    }).executes(new RequirementsBypass())
                            )
                    )
                    .then(Commands.literal("data")
                            .then(Commands.literal("userInfo")
                                    .then(Commands.argument("player", player()).executes(new GetUserInfoCmd())))
                            .then(Commands.literal("add")
                                    .then(Commands.argument("player", player())
                                            .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0D)).executes(new AddAmountCmd()))))
                            .then(Commands.literal("reduce").then(Commands.argument("player", player()).then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0D)).executes(new ReduceAmountCmd()))))
                    )
                    .then(Commands.literal("reload").executes(new Reload()))
                    .build());
        });
    }

    private CompletableFuture<Suggestions> getUserPendingTadeRequests(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        Entity e = ctx.getSource().getExecutor();
        if (e != null) {
            UUID playerUUID = e.getUniqueId();
            Set<UUID> uuids = mainHandler().tradeHandler().userPendingRequests(playerUUID);
            uuids.forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) return;
                String string = player.getName();
                if (string.toLowerCase().startsWith(builder.getRemainingLowerCase())) builder.suggest(string);
            });
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> onlineUsers(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        tigerBeachInstance().getServer().getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);
        return builder.buildFuture();
    }

}
