package net.laboulangerie.replaycore.commands.subcommands;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.replaycore.common.Messages;
import net.laboulangerie.replaycore.replay.ReplayDirection;
import net.laboulangerie.replaycore.replay.ReplaySession;

public class StepCommand implements SubCommand {

    private static List<String> directions = Arrays.asList("backward", "forward");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args, ReplaySession replay) {
        if (args.length == 1) {
            sender.sendMessage(
                    Messages.get("err.missing-arguments")
                            .appendSpace()
                            .append(Messages.get("syntax.replay-step")));
            return true;
        }

        String direction = args[1].toLowerCase();
        switch (direction) {
            case "backward":
                replay.step(ReplayDirection.BACKWARD);
                break;
            case "forward":
                replay.step(ReplayDirection.FORWARD);
                break;
            default:
                sender.sendMessage(Messages.get("err.invalid-arguments"));
                return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return null;

        return directions;
    }

}
