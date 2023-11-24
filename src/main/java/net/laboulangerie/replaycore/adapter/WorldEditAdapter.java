package net.laboulangerie.replaycore.adapter;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldedit.math.BlockVector3;

public class WorldEditAdapter {
    public static Location blockVector3ToLocation(World world, BlockVector3 vector) {
        return new Location(world,
                vector.getBlockX(),
                vector.getBlockY(),
                vector.getBlockZ());
    }

    public static BlockVector3 locationToBlockVector3(Location location) {
        return BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }
}
