package net.laboulangerie.replaycore.replay;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class ReplaySessionManager {
    private Map<UUID, ReplaySession> replaySessions;

    public ReplaySessionManager() {
        replaySessions = new HashMap<>();
    }

    public ReplaySession getReplay(Player player) {
        if (!replaySessions.keySet().contains(player.getUniqueId())) {
            return null;
        }
        return replaySessions.get(player.getUniqueId());
    }

    public void addReplay(Player player, ReplaySession replay) {
        replaySessions.put(player.getUniqueId(), replay);
    }

}
