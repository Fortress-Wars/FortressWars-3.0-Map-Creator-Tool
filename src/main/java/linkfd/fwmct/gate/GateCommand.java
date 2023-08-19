package linkfd.fwmct.gate;

import linkfd.fwmct.Main;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GateCommand implements CommandExecutor, TabCompleter {

    private final GateDriver gateDriver;

    public GateCommand(Main plugin) {
        plugin.getCommand("gate").setExecutor(this);
        this.gateDriver = new GateDriver(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String str, String[] args) {
        if (args.length == 1 && args[0].equals("help")) {
            return gateDriver.helpMessage(sender);
        }
        if (args.length < 4) {
            sender.sendMessage("§cInvalid arguments: Use /gate help for more info");
            return false;
        }

        try {
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);
            return gateDriver.doGateAction(sender, args[0], x, y, z);
        } catch (NumberFormatException e){
            sender.sendMessage("§cInvalid arguments: Use /gate help for more info");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String str, String[] args) {
        List<String> options = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("gate")) {
            if (args.length == 1) {
                options.add("open");
                options.add("close");
                options.add("toggle");
                options.add("help");
                options.removeIf(option -> !option.toLowerCase().contains(args[0].toLowerCase()));
            } else if (args.length == 2) {
                if (!(sender instanceof Player)) return options;
                Player player = (Player) sender;
                Block b = player.getTargetBlock(null, 200);
                options.add(b.getX() + "");
            } else if (args.length == 3) {
                if (!(sender instanceof Player)) return options;
                Player player = (Player) sender;
                Block b = player.getTargetBlock(null, 200);
                options.add(b.getY() + "");
            } else if (args.length == 4) {
                if (!(sender instanceof Player)) return options;
                Player player = (Player) sender;
                Block b = player.getTargetBlock(null, 200);
                options.add(b.getZ() + "");
            }
        }
        return options;
    }
}
