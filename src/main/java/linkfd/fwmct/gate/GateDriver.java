package linkfd.fwmct.gate;

import linkfd.fwmct.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GateDriver implements Listener {

    public GateDriver(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void gateAnimation(Location l) {
        World world = l.getWorld();
        world.playSound(l, Sound.BLOCK_WOODEN_DOOR_OPEN, 3, 0);
    }

    @EventHandler
    public void rightClickOpenGateEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("fw.gate")) return;

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block b = e.getClickedBlock();

        if (b.getType() != Material.OAK_WALL_SIGN) return;
        Location l = b.getLocation();
        toggle(l);
    }

    public boolean doGateAction(CommandSender sender, String action, int x, float y, float z) {
        if (!sender.hasPermission("fw.gate")) {
            sender.sendMessage("§cYou do not have permission to use this command!");
            return false;
        }

        Location gateSignLoc;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            gateSignLoc = new Location(player.getWorld(), x, y, z);
        } else if (sender instanceof BlockCommandSender) {
            BlockCommandSender blockSender = (BlockCommandSender) sender;
            Block block = blockSender.getBlock();
            gateSignLoc = new Location(block.getWorld(), x, y, z);
        } else {
            sender.sendMessage("§cOnly players and blocks can use this command");
            return false;
        }

        switch (action.toUpperCase()) {
            case "OPEN" -> open(gateSignLoc);
            case "CLOSE" -> close(gateSignLoc);
            case "TOGGLE" -> toggle(gateSignLoc);
            default -> sender.sendMessage("§cGate action is invalid. Try /gate help for help!");
        }
        return true;
    }

    public boolean helpMessage(CommandSender sender) {
        if (!sender.hasPermission("fw.gate")) {
            sender.sendMessage("§cYou do not have permission to use this command!");
            return false;
        }
        sender.sendMessage("");
        sender.sendMessage("§2Gate Command Syntax:");
        sender.sendMessage("");
        sender.sendMessage("§a/gate <action> <x> <y> <z>");
        sender.sendMessage("");
        sender.sendMessage("§2Arguments:");
        sender.sendMessage("§7 - §aaction§7: (string: open, close, or toggle)");
        sender.sendMessage("§7 - §ax, y, z§7: (integer: location of gate sign)");
        return true;
    }

    private void toggle(Location l) {
        try {
            l = l.clone();
            Block signBlock = l.getBlock();
            BlockState blockState = signBlock.getState();
            if (!(blockState instanceof Sign)) return;
            Sign sign = (Sign) blockState;
            String[] lines = sign.getLines();

            String fwStrucutreType = lines[0];
            String gateStatus = lines[2];

            if (!fwStrucutreType.equals("GATE")) return;
            if (gateStatus.equalsIgnoreCase("CLOSED")) {
                open(l);
                sign.setLine(2, "OPEN");
            } else {
                close(l);
                sign.setLine(2, "CLOSED");
            }
            sign.update();
        } catch (Exception e) {
            // Squash
        }
    }

    private void open(Location l) {
        try {
            l = l.clone();
            Block signBlock = l.getBlock();
            BlockState blockState = signBlock.getState();
            if (!(blockState instanceof Sign)) return;
            Sign sign = (Sign) blockState;
            String[] lines = sign.getLines();

            String fwStrucutreType = lines[0];
            String[] info = lines[1].split(" ");

            String direction = info[0];
            int height = Integer.parseInt(info[1]);
            int length = Integer.parseInt(info[2]);

            if (!fwStrucutreType.equals("GATE")) return;
            if (height < 2 || length < 1) return;
            if (!direction.equals("NORTH") && !direction.equals("EAST")) return;

            setGateBlocks(l, Material.AIR, height, length, direction);
            // Gate Animation
            gateAnimation(l);
        } catch (Exception e) {
            // Squash
        }
    }

    private void close(Location l) {
        try {
            l = l.clone();
            Block signBlock = l.getBlock();
            BlockState blockState = signBlock.getState();
            if (!(blockState instanceof Sign)) return;
            Sign sign = (Sign) blockState;
            String[] lines = sign.getLines();

            String fwStrucutreType = lines[0];
            String[] info = lines[1].split(" ");
            String matStr = lines[3];

            Material material = Material.valueOf(matStr.toUpperCase());

            String direction = info[0];
            int height = Integer.parseInt(info[1]);
            int length = Integer.parseInt(info[2]);

            // Check Gate
            if (!fwStrucutreType.equals("GATE")) return;
            if (height < 2 || length < 1) return;
            if (!direction.equals("NORTH") && !direction.equals("EAST")) return;

            // Change the blocks
            setGateBlocks(l, material, height, length, direction);

            // Gate Animation
            gateAnimation(l);
        } catch (Exception e) {
            // Squash
        }
    }

    private void setGateBlocks(Location l, Material material, int height, int length, String direction) {
        Location baseL = l.add(0, 2, 0);

        if (direction.equals("NORTH")) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < height; j++) {
                    Location newLoc = baseL.clone().add(0, j, -i);
                    Block newBlock = newLoc.getBlock();
                    newBlock.setType(material);
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < height; j++) {
                    Location newLoc = baseL.clone().add(-i, j, 0);
                    Block newBlock = newLoc.getBlock();
                    newBlock.setType(material);
                }
            }
        }
    }
}
