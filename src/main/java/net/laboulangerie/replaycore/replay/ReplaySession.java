package net.laboulangerie.replaycore.replay;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.coreprotect.CoreProtectAPI.ParseResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.laboulangerie.replaycore.ReplayCore;
import net.laboulangerie.replaycore.common.Messages;
import net.laboulangerie.replaycore.cuboid.Cuboid;

public class ReplaySession {
    private final Player player;
    private final World world;
    private final Map<Location, Block> blockMap;
    private Stack<ParseResult> coreProtectStack;
    private BukkitRunnable loop;
    private int cursor;

    private ReplayDirection direction = ReplayDirection.BACKWARD;
    private int tickDelay = 1;
    private boolean logging = false;

    public ReplaySession(Cuboid cuboid, Player player, World world) {
        this.player = player;
        this.world = world;
        this.blockMap = cuboid.getBlockMap();
        this.coreProtectStack = new Stack<>();
        this.loop = getLoop(direction);
        fetchCoreProtect((int) (System.currentTimeMillis() / 1000L));
    }

    private void fetchCoreProtect(int time) {
        for (Block block : this.blockMap.values()) {
            List<String[]> stringResults = ReplayCore.COREPROTECT.blockLookup(block, time);

            for (int i = 0; i < stringResults.size(); i++) {
                ParseResult result = ReplayCore.COREPROTECT.parseResult(stringResults.get(i));
                // Fitler out only remove and place
                if (result.getActionId() == 0 || result.getActionId() == 1) {
                    coreProtectStack.add(result);
                }
            }
        }

        // Sort by time from oldest to most recent
        coreProtectStack.sort(new Comparator<ParseResult>() {
            @Override
            public int compare(ParseResult p1, ParseResult p2) {
                long t1 = p1.getTimestamp();
                long t2 = p2.getTimestamp();

                // Special case where two actions are in the same tick
                // Want to place the break (0) before anything
                if (t1 == t2) {
                    return p1.getActionId() == 0 ? -1 : (p2.getActionId() == 0 ? 1 : 0);
                }

                return Long.compare(t1, t2);
            }
        });

        cursor = coreProtectStack.size() - 1;
    }

    public List<ParseResult> getCoreProtectResults() {
        return coreProtectStack;
    }

    public int getTickDelay() {
        return tickDelay;
    }

    public void setTickDelay(int tickDelay) {
        this.tickDelay = tickDelay;
        this.loop.cancel();
        runLoop();
    }

    public boolean isLogging() {
        return this.logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public boolean step(ReplayDirection direction) {
        ParseResult result = coreProtectStack.get(cursor);
        Location resultLocation = getResultLocation(result);
        BlockData blockData = result.getBlockData();

        if (this.logging) {
            player.sendMessage(getLogComponent(result, resultLocation));
        }

        switch (direction) {
            case BACKWARD:
                return stepBackward(result, blockData, resultLocation);
            case FORWARD:
                return stepForward(result, blockData, resultLocation);
            default:
                break;
        }

        return false;
    }

    private boolean stepBackward(ParseResult result, BlockData blockData, Location location) {
        if (result.getActionId() == 1) {
            // place - so remove it
            blockData = Material.AIR.createBlockData();
        } else if (blockData.getMaterial() == this.blockMap.get(location).getType()) {
            blockData = this.blockMap.get(location).getBlockData();
        }

        player.sendBlockChange(location, blockData);

        cursor -= (cursor != 0) ? 1 : 0;
        return cursor != 0;
    }

    private boolean stepForward(ParseResult result, BlockData blockData, Location location) {
        if (result.getActionId() == 0) {
            // remove - so remove it
            blockData = Material.AIR.createBlockData();
        } else if (blockData.getMaterial() == this.blockMap.get(location).getType()) {
            blockData = this.blockMap.get(location).getBlockData();
        }

        player.sendBlockChange(location, blockData);

        cursor += (cursor != coreProtectStack.size() - 1) ? 1 : 0;
        return cursor != coreProtectStack.size() - 1;
    }

    public void pause() {
        this.loop.cancel();
    }

    public void play() {
        runLoop();
    }

    public void rollback() {
        this.direction = ReplayDirection.BACKWARD;
        runLoop();
    }

    public void restore() {
        this.direction = ReplayDirection.FORWARD;
        runLoop();
    }

    public void runLoop() {
        this.loop = getLoop(direction);
        this.loop.runTaskTimerAsynchronously(ReplayCore.PLUGIN, 0, tickDelay);
    }

    private BukkitRunnable getLoop(ReplayDirection direction) {
        return new BukkitRunnable() {
            public void run() {
                if (!step(direction))
                    this.cancel();
            }
        };
    }

    private Location getResultLocation(ParseResult result) {
        return new Location(this.world, result.getX(), result.getY(), result.getZ());
    }

    private Component getLogComponent(ParseResult result, Location location) {
        Component logComponent = Messages.get("info.log",
                Placeholder.unparsed("action", result.getActionString()),
                Placeholder.unparsed("coordinates",
                        String.format("%d %d %d", location.blockX(), location.blockY(), location.blockZ())),
                Placeholder.unparsed("player", player.getName()),
                Formatter.date("date", LocalDateTime.ofInstant(Instant.ofEpochMilli(result.getTimestamp()),
                        TimeZone.getDefault().toZoneId())));

        return logComponent;
    }
}
