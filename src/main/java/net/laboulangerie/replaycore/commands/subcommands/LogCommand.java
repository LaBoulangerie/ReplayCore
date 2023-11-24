package net.laboulangerie.replaycore.commands.subcommands;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.replaycore.common.Messages;
import net.laboulangerie.replaycore.replay.ReplaySession;

public class LogCommand implements SubCommand {

    private static List<String> options = Arrays.asList("on", "off", "toggle");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args, ReplaySession replay) {
        if (args.length == 1) {
            sender.sendMessage(
                    Messages.get("err.missing-arguments")
                            .appendSpace()
                            .append(Messages.get("syntax.replay-log")));
            return true;
        }

        String option = args[1].toLowerCase();
        switch (option) {
            case "on":
                replay.setLogging(true);
                break;
            case "off":
                replay.setLogging(false);
                break;
            case "toggle":
                replay.setLogging(replay.isLogging());
                break;
            default:
                sender.sendMessage(Messages.get("err.invalid-arguments"));
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return null;

        return options;
    }
}
