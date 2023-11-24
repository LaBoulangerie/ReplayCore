package net.laboulangerie.replaycore.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.replaycore.common.Messages;
import net.laboulangerie.replaycore.replay.ReplaySession;

public class RollbackCommand implements SubCommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args, ReplaySession replay) {
        replay.rollback();
        sender.sendMessage(Messages.get("info.rollback"));
        return true;
    }

}
