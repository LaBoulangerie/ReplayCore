package net.laboulangerie.replaycore.commands.subcommands;

import java.util.List;

import javax.annotation.Nullable;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldedit.IncompleteRegionException;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.laboulangerie.replaycore.ReplayCore;
import net.laboulangerie.replaycore.common.Messages;
import net.laboulangerie.replaycore.cuboid.Cuboid;
import net.laboulangerie.replaycore.cuboid.WorldEditCuboid;
import net.laboulangerie.replaycore.replay.ReplaySession;

public class LoadCommand implements SubCommand {
    private static List<String> modes = Arrays.asList("selection", "corners");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.get("err.not-a-player"));
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(Messages.get("err.missing-mode"));
            return true;
        }

        Player player = (Player) sender;
        World world = player.getWorld();
        String mode = args[1];

        Cuboid cuboid;

        switch (mode.toLowerCase()) {
            case "selection":
                if (!ReplayCore.IS_WORLD_EDIT_INSTALLED) {
                    sender.sendMessage(Messages.get("err.worldedit-not-installed"));
                    return true;
                }

                try {
                    cuboid = new WorldEditCuboid(player);
                } catch (IncompleteRegionException e) {
                    sender.sendMessage(Messages.get("err.incomplete-selection"));
                    return true;
                }
                break;

            case "corners":
                if (args.length != 7) {
                    sender.sendMessage(Messages.get("err.missing-corners").appendSpace()
                            .append(Messages.get("syntax.replay-corners")));
                    return true;
                }

                List<String> argsList = Arrays.asList(args);

                try {
                    Location corner1 = stringListToLocation(world, argsList.subList(1, 3));
                    Location corner2 = stringListToLocation(world, argsList.subList(4, 6));

                    cuboid = new Cuboid(corner1, corner2, world);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Messages.get("err.invalid-corners").appendSpace()
                            .append(Messages.get("syntax.replay-corners")));
                    return true;
                }
                break;

            default:
                sender.sendMessage(Messages.get("err.invalid-mode"));
                return true;
        }

        // Fetch all CoreProtect data
        int blockCount = cuboid.getBlockCount();
        sender.sendMessage(
                Messages.get("info.fetching-coreprotect",
                        Placeholder.unparsed("count", Integer.toString(blockCount))));

        ReplaySession replay = new ReplaySession(cuboid, player, world);
        ReplayCore.REPLAY_MANAGER.addReplay(player, replay);

        sender.sendMessage(Messages.get("info.successfully-loaded"));
        return true;
    }

    private Location stringListToLocation(World world, List<String> coos) throws NumberFormatException {
        return new Location(world,
                Integer.parseInt(coos.get(0)),
                Integer.parseInt(coos.get(1)),
                Integer.parseInt(coos.get(2)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return null;

        if (args.length >= 2 && args[1].toUpperCase() == "corners" && args.length < 3) {
            // XYZ of the block the player is looking at
            Player player = (Player) sender;
            Location targetLocation = player.getTargetBlockExact(10).getLocation();
            String targetXYZ = String.format("%d %d %d",
                    Integer.toString(targetLocation.getBlockX()),
                    Integer.toString(targetLocation.getBlockY()),
                    Integer.toString(targetLocation.getBlockZ()));

            return Arrays.asList(targetXYZ);
        }

        return modes;
    }

}