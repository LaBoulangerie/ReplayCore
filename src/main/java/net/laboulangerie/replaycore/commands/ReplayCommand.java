package net.laboulangerie.replaycore.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.laboulangerie.replaycore.ReplayCore;
import net.laboulangerie.replaycore.commands.subcommands.DelayCommand;
import net.laboulangerie.replaycore.commands.subcommands.LoadCommand;
import net.laboulangerie.replaycore.commands.subcommands.LogCommand;
import net.laboulangerie.replaycore.commands.subcommands.PauseCommand;
import net.laboulangerie.replaycore.commands.subcommands.PlayCommand;
import net.laboulangerie.replaycore.commands.subcommands.RestoreCommand;
import net.laboulangerie.replaycore.commands.subcommands.RollbackCommand;
import net.laboulangerie.replaycore.commands.subcommands.StepCommand;
import net.laboulangerie.replaycore.commands.subcommands.SubCommand;
import net.laboulangerie.replaycore.common.Messages;
import net.laboulangerie.replaycore.replay.ReplaySession;

public class ReplayCommand implements CommandExecutor, TabCompleter {

    private Map<String, SubCommand> subcommands;

    public ReplayCommand() {
        this.subcommands = new HashMap<>();
        this.subcommands.put("delay", new DelayCommand());
        this.subcommands.put("load", new LoadCommand());
        this.subcommands.put("log", new LogCommand());
        this.subcommands.put("pause", new PauseCommand());
        this.subcommands.put("play", new PlayCommand());
        this.subcommands.put("restore", new RestoreCommand());
        this.subcommands.put("rollback", new RollbackCommand());
        this.subcommands.put("step", new StepCommand()); // are u stuck
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.get("err.not-a-player"));
            return true;
        }

        if (args.length == 0) {
            PluginMeta pluginMeta = ReplayCore.PLUGIN.getPluginMeta();
            String version = pluginMeta.getVersion();
            String authors = String.join(", ", pluginMeta.getAuthors());
            String website = pluginMeta.getWebsite();

            Component versionComp = Messages.get("info.version",
                    Placeholder.unparsed("version", version));
            Component authorsWebsiteComp = Messages.get("info.authors-website",
                    Placeholder.unparsed("authors", authors),
                    Placeholder.unparsed("website", website));

            sender.sendMessage(versionComp.appendNewline().append(authorsWebsiteComp));
            return false;
        }

        String subcommand = args[0].toLowerCase();

        if (subcommands.containsKey(subcommand)) {
            if (subcommand.equals("load")) {
                return subcommands.get(subcommand).onCommand(sender, command, alias, args);
            }

            ReplaySession replay = ReplayCore.REPLAY_MANAGER.getReplay((Player) sender);
            if (replay == null) {
                sender.sendMessage(Messages.get("err.no-replay-found"));
                return true;
            }

            return subcommands.get(subcommand).onCommand(sender, command, alias, args, replay);
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return null;

        if (args.length >= 1 && subcommands.containsKey(args[0])) {
            return subcommands.get(args[0]).onTabComplete(sender, command, alias, args);
        }

        return new ArrayList<>(subcommands.keySet());
    }

}