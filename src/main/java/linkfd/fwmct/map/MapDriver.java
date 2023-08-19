package linkfd.fwmct.map;

import linkfd.fwmct.Main;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class MapDriver {

    private final Main plugin;

    public MapDriver(Main plugin) {
        this.plugin = plugin;
    }

    public boolean helpMessage(CommandSender sender) {
        if (!sender.hasPermission("fw.help")) {
            sender.sendMessage("§cYou do not have permission to use this command!");
            return false;
        }
        sender.sendMessage("");
        sender.sendMessage("§2Fortress Wars Command Syntax:");
        sender.sendMessage("");
        sender.sendMessage("§a/fw broadcast <message>");
        sender.sendMessage("§a/game fakeLogin");
        sender.sendMessage("§a/game fakeLogout");
        sender.sendMessage("§a/fw logger");
        sender.sendMessage("§a/fw logger <logLevel>");
        sender.sendMessage("§a/fw load <mapName> <auto-save>");
        sender.sendMessage("§a/fw config reload");
        sender.sendMessage("§a/fw config save");
        sender.sendMessage("§a/game unload <mapName>");
        sender.sendMessage("§a/game teleport <mapName>");
        sender.sendMessage("");
        sender.sendMessage("§2Arguments:");
        sender.sendMessage("§7 - §a<message>§7: Message to broadcast");
        sender.sendMessage("§7 - §a<logLevel>§7: Log level to set");
        sender.sendMessage("§7 - §a<mapName>§7: World name of map");
        sender.sendMessage("§7 - §a<auto-save>§7: Enable auto-save on load (Default: true)");
        return true;
    }

    public ArrayList<String> getAllMapNames() {
        FileConfiguration config = plugin.getConfig();
        return new ArrayList<>(config.getStringList("maps"));
    }

    // unload a map, since setAutoSave is false. This will roll back the world
    public void unloadMap(String mapName) {
        if (Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(mapName), false)) {
            Bukkit.getLogger().info("Successfully unloaded " + mapName);
        } else {
            Bukkit.getLogger().severe("COULD NOT UNLOAD " + mapName);
        }
    }

    // Load a map right before entering a game
    public void loadMap(String mapName, boolean autosave) {
        Bukkit.getServer().createWorld(new WorldCreator(mapName));
        World world = Bukkit.getWorld(mapName);
        world.setAutoSave(autosave);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.MOB_GRIEFING, true);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.FIRE_DAMAGE, true);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setDifficulty(Difficulty.NORMAL);

        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setSize(600);
        worldBorder.setDamageAmount(69);
        worldBorder.setWarningDistance(15);
        worldBorder.setWarningDistance(30);
        worldBorder.setDamageBuffer(0);

        if (!autosave) {
            Bukkit.getLogger().info("Disabled auto save for world: " + world.getName());
        }
    }

    public static World getWorld(String mapName) {
        return Bukkit.getWorld(mapName);
    }
}
