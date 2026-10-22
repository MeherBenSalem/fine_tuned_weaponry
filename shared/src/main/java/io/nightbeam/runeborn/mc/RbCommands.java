package io.nightbeam.runeborn.mc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.compat.Platform;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.config.ConfigIO;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.mob.Runes;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.stat.Attr;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/** {@code /runeborn} admin commands. */
public final class RbCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        List<String> attrs = new ArrayList<>();
        for (Attr a : Attr.ALL) {
            attrs.add(a.name().toLowerCase());
        }
        List<String> bosses = new ArrayList<>();
        for (BossSpec b : Bosses.all()) {
            if (b.summonable()) {
                bosses.add(b.id());
            }
        }
        dispatcher.register(Commands.literal("runeborn")
                .requires(Compat::isOperator)
                .then(Commands.literal("runes")
                        .then(Commands.literal("add").then(Commands.argument("amount", LongArgumentType.longArg(0))
                                .executes(ctx -> runes(ctx, true))))
                        .then(Commands.literal("set").then(Commands.argument("amount", LongArgumentType.longArg(0))
                                .executes(ctx -> runes(ctx, false)))))
                .then(Commands.literal("attribute")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(attrs, b))
                                .then(Commands.argument("value", IntegerArgumentType.integer(Attr.MIN, Attr.MAX))
                                        .executes(RbCommands::attribute))))
                .then(Commands.literal("origin").then(Commands.literal("reset").executes(RbCommands::resetOrigin)))
                .then(Commands.literal("summon")
                        .then(Commands.argument("boss", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(bosses, b))
                                .executes(RbCommands::summon)))
                .then(Commands.literal("reload").executes(RbCommands::reload))
                .then(Commands.literal("info").executes(RbCommands::info)));
    }

    private static ServerPlayer self(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return ctx.getSource().getPlayerOrException();
    }

    private static int runes(CommandContext<CommandSourceStack> ctx, boolean add) throws CommandSyntaxException {
        ServerPlayer p = self(ctx);
        long amount = LongArgumentType.getLong(ctx, "amount");
        PlayerData d = RbServer.data(p);
        if (add) {
            Players.giveRunes(p, amount);
        } else {
            d.runes = amount;
        }
        Players.sync(p);
        Compat.feedback(ctx.getSource(), Component.literal("Runes: " + Runes.format(d.runes)));
        return 1;
    }

    private static int attribute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = self(ctx);
        Attr attr = Attr.byCode(StringArgumentType.getString(ctx, "name"));
        if (attr == null) {
            Compat.feedback(ctx.getSource(), Component.literal("Unknown attribute"));
            return 0;
        }
        PlayerData d = RbServer.data(p);
        d.attrs[attr.ordinal()] = IntegerArgumentType.getInteger(ctx, "value");
        Players.applyAttributes(p);
        Players.sync(p);
        Compat.feedback(ctx.getSource(), Component.literal(attr.name() + " = " + d.attrs[attr.ordinal()] + " (level " + d.level() + ")"));
        return 1;
    }

    private static int resetOrigin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = self(ctx);
        PlayerData d = RbServer.data(p);
        d.origin = "";
        d.attrs = PlayerData.defaultAttrs();
        Players.applyAttributes(p);
        Players.sync(p);
        Players.send(p, io.nightbeam.runeborn.core.net.Packets.open(io.nightbeam.runeborn.core.net.Packets.Screen.ORIGIN,
                "", "", 0, 0, 0));
        return 1;
    }

    private static int summon(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BossSpec spec = Bosses.get(StringArgumentType.getString(ctx, "boss"));
        if (spec == null || !spec.summonable()) {
            Compat.feedback(ctx.getSource(), Component.literal("Unknown boss"));
            return 0;
        }
        return BossManager.summon(self(ctx), spec) ? 1 : 0;
    }

    private static int reload(CommandContext<CommandSourceStack> ctx) {
        ServerConfig.INSTANCE = ConfigIO.load(Platform.configDir().resolve("runeborn-server.json"), ServerConfig.class,
                ServerConfig::new).sanitize();
        for (ServerPlayer p : ctx.getSource().getServer().getPlayerList().getPlayers()) {
            Players.sync(p);
        }
        Compat.feedback(ctx.getSource(), Component.literal("Runeborn config reloaded"));
        return 1;
    }

    private static int info(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer p = self(ctx);
        PlayerData d = RbServer.data(p);
        StringBuilder sb = new StringBuilder("Level " + d.level() + " · " + Runes.format(d.runes) + " runes ·");
        for (Attr a : Attr.ALL) {
            sb.append(' ').append(a.code.toUpperCase()).append(' ').append(d.attrs[a.ordinal()]);
        }
        Compat.feedback(ctx.getSource(), Component.literal(sb.toString()));
        return 1;
    }

    private RbCommands() {
    }
}
