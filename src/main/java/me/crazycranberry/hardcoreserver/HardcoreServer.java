package me.crazycranberry.hardcoreserver;

import org.bukkit.GameMode;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class HardcoreServer extends JavaPlugin implements Listener {
    public final static Logger logger = Logger.getLogger("Minecraft");
    YamlConfiguration someoneDiedConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            someoneDiedConfig().save(getDataFolder() + "" + File.separatorChar + "on_god_you_better_not_edit_this_file_i_will_mess_you_up.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (someoneDiedConfig().getBoolean("someone_died", true)) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        someoneDiedConfig().set("someone_died", true);
        for (Player player : getServer().getOnlinePlayers()) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerGameModeChangeEvent event) {
        if (someoneDiedConfig().getBoolean("someone_died", true) && event.getNewGameMode() != GameMode.SPECTATOR) {
            getServer().broadcastMessage("Someone tried to sneakily change gamemode to " + event.getNewGameMode().name());
            getServer().broadcastMessage("If you didn't want to lose the server, you shouldn't have let someone die :)");
            event.setCancelled(true);
        }
    }

    private YamlConfiguration loadConfig(String configName) {
        File configFile = new File(getDataFolder() + "" + File.separatorChar + configName);
        if(!configFile.exists()){
            saveResource(configName, true);
            logger.info(String.format("%s not found! copied %s to %s", configName, configName, getDataFolder()));
        }
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info("[ ERROR ] An error occured while trying to load " + configName);
            e.printStackTrace();
        }
        return config;
    }

    private YamlConfiguration someoneDiedConfig() {
        if (someoneDiedConfig != null) {
            return someoneDiedConfig;
        }
        someoneDiedConfig = loadConfig("on_god_you_better_not_edit_this_file_i_will_mess_you_up.yml");
        return someoneDiedConfig;
    }
}
