package net.laboulangerie.replaycore.cuboid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Cuboid {
    private Location cornerMin;
    private Location cornerMax;
    private Map<Location, Block> blockMap;
    private World world;

    public Cuboid(Pair<Location, Location> corners, World world) {
        this(corners.getLeft(), corners.getRight(), world);
    }

    public Cuboid(Location corner1, Location corner2, World world) {
        int minX = Math.min(corner1.blockX(), corner2.blockX());
        int minY = Math.min(corner1.blockY(), corner2.blockY());
        int minZ = Math.min(corner1.blockZ(), corner2.blockZ());
        int maxX = Math.max(corner1.blockX(), corner2.blockX());
        int maxY = Math.max(corner1.blockY(), corner2.blockY());
        int maxZ = Math.max(corner1.blockZ(), corner2.blockZ());

        this.cornerMin = new Location(world, minX, minY, minZ);
        this.cornerMax = new Location(world, maxX + 1, maxY + 1, maxZ + 1);
        this.world = world;
        this.blockMap = new HashMap<>();
        cacheBlocks();
    }

    public int getBlockCount() {
        return Math.max(cornerMax.blockX() - cornerMin.blockX(), 1)
                * Math.max(cornerMax.blockY() - cornerMin.blockY(), 1)
                * Math.max(cornerMax.blockZ() - cornerMin.blockZ(), 1);
    }

    private void cacheBlocks() {
        for (int x = cornerMin.blockX(); x <= cornerMax.blockX(); x++) {
            for (int y = cornerMin.blockY(); y <= cornerMax.blockY(); y++) {
                for (int z = cornerMin.blockZ(); z <= cornerMax.blockZ(); z++) {
                    Location location = new Location(this.world, x, y, z);
                    Block block = this.world.getBlockAt(location);
                    blockMap.put(location, block);
                }
            }
        }
    }

    public List<Block> getBlocks() {
        return (List<Block>) blockMap.values();
    }

    public Map<Location, Block> getBlockMap() {
        return blockMap;
    }

    public World getWorld() {
        return world;
    }

    public Pair<Location, Location> getCorners() {
        return Pair.of(cornerMin, cornerMax);
    }
}
