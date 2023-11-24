package net.laboulangerie.replaycore.commands.subcommands;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.laboulangerie.replaycore.common.Messages;
import net.laboulangerie.replaycore.replay.ReplaySession;

public class DelayCommand implements SubCommand {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args, ReplaySession replay) {
        if (args.length == 1) {
            sender.sendMessage(
                    Messages.get("err.invalid-arguments")
                            .appendSpace()
                            .append(Messages.get("syntax.replay-delay")));
            return true;
        }

        int tickDelay;
        try {
            tickDelay = Integer.parseInt(args[1]);
            if (tickDelay < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(Messages.get("err.invalid-arguments"));
            return true;
        }

        replay.setTickDelay(tickDelay);
        sender.sendMessage(
                Messages.get("info.delay",
                        Formatter.number("delay", tickDelay)));
        return true;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        return Arrays.asList("1", "2", "5", "10", "20");
    }
}
