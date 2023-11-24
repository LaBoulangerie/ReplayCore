package net.laboulangerie.replaycore;

import org.bukkit.plugin.java.JavaPlugin;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.laboulangerie.replaycore.commands.ReplayCommand;
import net.laboulangerie.replaycore.replay.ReplaySessionManager;

public class ReplayCore extends JavaPlugin {
    public static ReplayCore PLUGIN;
    public static CoreProtectAPI COREPROTECT;
    public static ReplaySessionManager REPLAY_MANAGER;
    public static boolean IS_WORLD_EDIT_INSTALLED;
    public static int BLOCK_LIMIT = 1_000_000;

    @Override
    public void onEnable() {
        ReplayCore.PLUGIN = this;
        saveDefaultConfig();

        ReplayCore.COREPROTECT = CoreProtect.getInstance().getAPI();
        ReplayCore.IS_WORLD_EDIT_INSTALLED = getServer().getPluginManager().getPlugin("WorldEdit") != null;
        ReplayCore.REPLAY_MANAGER = new ReplaySessionManager();
        getLogger().info("Plugin started");

        getCommand("replay").setExecutor(new ReplayCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled");
    }
}