package net.laboulangerie.replaycore.cuboid;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

import net.laboulangerie.replaycore.adapter.WorldEditAdapter;

public class WorldEditCuboid extends Cuboid {

    public WorldEditCuboid(Player player) throws IncompleteRegionException {
        super(getCornersFromPlayerSelection(player), player.getWorld());
    }

    private static Pair<Location, Location> getCornersFromPlayerSelection(Player player)
            throws IncompleteRegionException {
        BukkitPlayer bPlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.world.World bWorld = bPlayer.getWorld();

        Region region = WorldEdit.getInstance().getSessionManager().get(bPlayer).getSelection(bWorld);
        BlockVector3 pos1 = region.getBoundingBox().getPos1();
        BlockVector3 pos2 = region.getBoundingBox().getPos2();

        Location corner1 = WorldEditAdapter.blockVector3ToLocation(player.getWorld(), pos1);
        Location corner2 = WorldEditAdapter.blockVector3ToLocation(player.getWorld(), pos2);

        return Pair.of(corner1, corner2);
    }

}
