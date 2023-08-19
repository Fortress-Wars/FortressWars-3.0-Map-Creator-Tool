package linkfd.fwmct.sponge;

import linkfd.fwmct.Main;

import linkfd.fwmct.misc.TeamColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpongeCommand implements CommandExecutor, TabCompleter {

    private final SpongeDriver spongeDriver;

    public SpongeCommand(Main plugin) {
        plugin.getCommand("sponge").setExecutor(this);
        spongeDriver = new SpongeDriver(plugin);
    }

    private boolean commandFailed(CommandSender sender) {
        sender.sendMessage("§cCommand failed! /sponge help for syntax");
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cInvalid arguments: Use /sponge help for more info");
            return false;
        }
        if (args[0].equals("help")) {
            return spongeDriver.helpMessage(sender);
        }

        boolean REPLACE_BLOCKS_DEFAULT = false;
        switch (args[0]) {
            case "vector" -> {
                try {
                    TeamColor color = TeamColor.valueOf(args[1].toUpperCase());
                    float vecX = Float.parseFloat(args[2]);
                    float vecY = Float.parseFloat(args[3]);
                    float vecZ = Float.parseFloat(args[4]);
                    boolean replaceBlocks = args.length >= 6 ? Boolean.parseBoolean(args[5]) : REPLACE_BLOCKS_DEFAULT;
                    return spongeDriver.createSpongeLauncher(sender, color, vecX, vecY, vecZ, replaceBlocks);
                } catch (Exception e) {
                    return commandFailed(sender);
                }
            }
            case "randomVector" -> {
                try {
                    TeamColor color = TeamColor.valueOf(args[1].toUpperCase());
                    float vecMinX = Float.parseFloat(args[2]);
                    float vecMaxX = Float.parseFloat(args[3]);
                    float vecMinY = Float.parseFloat(args[4]);
                    float vecMaxY = Float.parseFloat(args[5]);
                    float vecMinZ = Float.parseFloat(args[6]);
                    float vecMaxZ = Float.parseFloat(args[7]);

                    boolean replaceBlocks = args.length >= 9 ? Boolean.parseBoolean(args[8]) : REPLACE_BLOCKS_DEFAULT;
                    return spongeDriver.createSpongeLauncher(sender, color, vecMinX, vecMaxX, vecMinY, vecMaxY, vecMinZ, vecMaxZ, replaceBlocks);
                } catch (Exception e) {
                    return commandFailed(sender);
                }
            }
            case "coordinates" -> {
                try {
                    TeamColor color = TeamColor.valueOf(args[1].toUpperCase());
                    int destX = Integer.parseInt(args[2]);
                    int destY = Integer.parseInt(args[3]);
                    int destZ = Integer.parseInt(args[4]);
                    int height = Integer.parseInt(args[5]);
                    boolean replaceBlocks = args.length >= 7 ? Boolean.parseBoolean(args[6]) : REPLACE_BLOCKS_DEFAULT;
                    return spongeDriver.createSpongeLauncher(sender, color, destX, destY, destZ, height, replaceBlocks);
                } catch (Exception e) {
                    return commandFailed(sender);
                }
            }
        }

        sender.sendMessage("§cInvalid arguments: Use /sponge help for more info");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
        List<String> options = new ArrayList<>();
        if (!sender.hasPermission("fw.sponge") || !(sender instanceof Player)) {
            return options;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("sponge")) {
            if (args.length == 1) {
                options.add("help");
                options.add("vector");
                options.add("randomVector");
                options.add("coordinates");
            } else if (args.length == 2) {
                for (TeamColor team : TeamColor.values()) {
                    options.add(team.name());
                }
            } else if (args[0].equals("vector")) {
                if (args.length == 3 || args.length == 5) {
                    options.add("0");
                } else if (args.length == 4) {
                    options.add("1");
                } else if (args.length == 6) {
                    options.add("true");
                    options.add("false");
                }
            } else if (args[0].equals("randomVector")) {
                if (args.length == 3) {
                    options.add("-0.5");
                } else if (args.length == 4) {
                    options.add("0.5");
                } else if (args.length == 5) {
                    options.add("0.5");
                } else if (args.length == 6) {
                    options.add("1");
                } else if (args.length == 7) {
                    options.add("-0.5");
                } else if (args.length == 8) {
                    options.add("0.5");
                } else if (args.length == 9) {
                    options.add("true");
                    options.add("false");
                }
            } else if (args[0].equals("coordinates")) {
                if (args.length == 3 || args.length == 5) {
                    options.add("0");
                } else if (args.length == 4) {
                    options.add("1");
                } else if (args.length == 6) {
                    options.add("10");
                } else if (args.length == 7) {
                    options.add("true");
                    options.add("false");
                }
            }
        }
        options.removeIf(option -> !option.toLowerCase().contains(args[args.length - 1].toLowerCase()));
        return options;
    }
}
