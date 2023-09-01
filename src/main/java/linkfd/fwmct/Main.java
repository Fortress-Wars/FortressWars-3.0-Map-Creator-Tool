package linkfd.fwmct;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("FortressWars") != null) {
            getLogger().warning("================================================");
            getLogger().warning("      FortressWars 3.0 is already installed!      ");
            getLogger().warning("FortressWars 3.0 (Map Creator Tool) NOT enabled!");
            getLogger().warning("================================================");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.saveDefaultConfig();
        new CommandManager(this);

        getLogger().info("FortressWars 3.0 (Map Creator Tool) Enabled");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        getLogger().info("FortressWars 3.0 (Map Creator Tool) Disabled");
    }

    public void log(String message) {
        Bukkit.getLogger().info("[FW LOG]: " + message);
    }

    public void log(Exception e) {
        Bukkit.getLogger().warning("[FW WARN]: " + e.getMessage());
        e.printStackTrace();
    }
}

