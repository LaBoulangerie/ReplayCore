package net.laboulangerie.replaycore.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.replaycore.replay.ReplaySession;

public interface SubCommand extends CommandExecutor, TabCompleter {
    @Override
    default boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
            @NotNull String[] args) {
        return onCommand(sender, command, alias, args, null);
    }

    default boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
            @NotNull String[] args, ReplaySession replay) {
        return onCommand(sender, command, alias, args);
    }

    @Override
    default @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
