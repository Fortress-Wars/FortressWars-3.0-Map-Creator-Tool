package linkfd.fwmct.map;

import linkfd.fwmct.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MapCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private final MapDriver mapDriver;
    public MapCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("map").setExecutor(this);
        this.mapDriver = new MapDriver(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("fw.admin")
                && !sender.hasPermission("fw.broadcast")
                && !sender.hasPermission("fw.secretLogin")
                && !sender.hasPermission("fw.help")) {
            sender.sendMessage("§cYou do not have permission to do that!");
            return false;
        }

        if (args.length == 0)
            return true;
        if (args[0] == null || args[0].equals(""))
            return true;
        switch (args[0]) {
            case "unload" -> {
                if (!sender.hasPermission("fw.admin")) {
                    sender.sendMessage("§cYou do not have permission to do that!");
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cInvalid Arguments! Try /fw help for help!");
                    return false;
                }
                if (mapDriver.getAllMapNames().contains(args[1])) {
                    sender.sendMessage("§3Unloading §r" + args[1]);
                    try {
                        mapDriver.unloadMap(args[1]);
                    } catch (Exception e) {
                        plugin.log("Failed to unload " + args[1]);
                        plugin.log(e.getMessage());
                        sender.sendMessage("§cFailed to unload " + args[1]);
                        return false;
                    }
                    sender.sendMessage("§aComplete");
                    break;
                }
                sender.sendMessage("§cWorld doesn't exist!");
            }
            case "load" -> {
                if (!sender.hasPermission("fw.admin")) {
                    sender.sendMessage("§cYou do not have permission to do that!");
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cInvalid Arguments! Try /fw help for help!");
                    return false;
                }
                boolean autosave = args.length != 3 || !args[2].equals("false");
                if (mapDriver.getAllMapNames().contains(args[1])) {
                    sender.sendMessage("§3Loading §r" + args[1]);
                    try {
                        mapDriver.loadMap(args[1], autosave);
                    } catch (Exception e) {
                        plugin.log("Failed to load " + args[1]);
                        plugin.log(e.getMessage());
                        sender.sendMessage("§cFailed to load " + args[1]);
                        return false;
                    }
                    sender.sendMessage("§aComplete");
                    break;
                }
                sender.sendMessage("§cWorld doesn't exist!");
            }
            case "tp" -> {
                if (!sender.hasPermission("fw.admin")) {
                    sender.sendMessage("§cYou do not have permission to do that!");
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cInvalid Arguments! Try /fw help for help!");
                    return false;
                }
                Player p = (Player) sender;
                World world = Bukkit.getWorld(args[1]);
                if (world != null) {
                    sender.sendMessage("§aTeleporting...§r");
                    p.teleport(world.getSpawnLocation().clone().add(0.5, 0, 0.5));
                    return true;
                }
                sender.sendMessage("§cWorld isn't loaded!");
                return false;
            }
            case "help" -> {
                return mapDriver.helpMessage(sender);
            }
            default -> {
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
        List<String> options = new ArrayList<>();
        if (!sender.hasPermission("fw.admin")) return options;
        if (cmd.getName().equalsIgnoreCase("map")) {
            if (args.length == 1 && sender.hasPermission("fw.admin")) {
                options.add("load");
                options.add("unload");
                options.add("tp");
                options.removeIf(option -> !option.toLowerCase().contains(args[0].toLowerCase()));
            } else if (args.length == 2 && sender.hasPermission("fw.admin")) {
                options.addAll(mapDriver.getAllMapNames());
                options.removeIf(option -> !option.toLowerCase().contains(args[1].toLowerCase()));
            } else if (args.length == 3 && args[0].equals("load") && sender.hasPermission("fw.admin")) {
                options.add("true");
                options.add("false");
                options.removeIf(option -> !option.toLowerCase().contains(args[2].toLowerCase()));
            }
        }
        return options;
    }
}
