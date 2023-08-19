package linkfd.fwmct.sponge;

import linkfd.fwmct.Main;
import linkfd.fwmct.misc.TeamColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class SpongeDriver implements Listener {

    private static final double CENTER_BLOCK_OFFSET = 0.5;

    public SpongeDriver(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean helpMessage(CommandSender sender) {
        if (!sender.hasPermission("fw.sponge")) {
            sender.sendMessage("§cYou do not have permission to use this command!");
            return false;
        }
        sender.sendMessage("");
        sender.sendMessage("§2Sponge Command Syntax:");
        sender.sendMessage("");
        sender.sendMessage("§a/sponge coordinates <team> <x> <y> <z> <height> <§o§7(optional) §r§areplace>");
        sender.sendMessage("§a/sponge vector <team> <vecX> <vecY> <vecZ> <§o§7(optional) §r§areplace>");
        sender.sendMessage("§a/sponge randomVector <team> <vecMinX> <vecMaxX> <vecMinY> <vecMaxY> <vecMinZ> <vecMaxZ> <§o§7(optional) §r§areplace>");
        sender.sendMessage("");
        sender.sendMessage("§2Arguments:");
        sender.sendMessage("§7 - §a<team>§7: (none/red/blue)");
        sender.sendMessage("§7 - §a<x>, <y>, <z>§7: (integer)");
        sender.sendMessage("§7 - §a<height>§7: (integer)");
        sender.sendMessage("§7 - §a<vec>§7: (float: velocity)");
        sender.sendMessage("§7 - §o§7(optional) §r§a<replace>§7: (true/false)");
        return true;
    }

    private void invalidSpongeMessage(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cInvalid Sponge Launcher!"));
    }

    private void spawnLauncherParticles(Location playerLoc, Color color) {
        World world = playerLoc.getWorld();
        Location loc = new Location(world, playerLoc.getX() + CENTER_BLOCK_OFFSET, playerLoc.getY() + 1, playerLoc.getZ() + CENTER_BLOCK_OFFSET);
        Particle.DustOptions dust = new Particle.DustOptions(color, 2.0f);
        world.spawnParticle(Particle.REDSTONE, loc, 100, 0, 0, 0, dust);
        world.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 5.0f, 1.0f);
    }

    // ==================================
    //         Event Handler Methods
    // ==================================
    @EventHandler
    public void onMoveOnSpongeLauncher(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.SPECTATOR) return;
        Location playerLoc = p.getLocation();
        World world = playerLoc.getWorld();
        Block spongeBlock = world.getBlockAt(playerLoc.getBlockX(), playerLoc.getBlockY() - 1, playerLoc.getBlockZ());
        if (spongeBlock.getType() != Material.SPONGE && spongeBlock.getType() != Material.WET_SPONGE) {
            return;
        }

        Block signBlock = world.getBlockAt(playerLoc.getBlockX(), playerLoc.getBlockY() - 2, playerLoc.getBlockZ());

        if (signBlock.getType() != Material.OAK_WALL_SIGN) {
            return;
        }

        Sign sign = (Sign) signBlock.getState();
        String[] lines = sign.getLines();


        String team = lines[0];
        Color color = Color.WHITE;
        if (team.equals("RED")) {
            color = Color.RED;
        } else if (team.equals("BLUE")) {
            color = Color.BLUE;
        }

        Location centerBlockLoc = new Location(spongeBlock.getLocation().getWorld(), spongeBlock.getLocation().getX() + 0.5, spongeBlock.getLocation().getY() + 1, spongeBlock.getLocation().getZ() + 0.5);
        FallingBlock spongeLaunchBlock = world.spawnFallingBlock(centerBlockLoc, Material.SPONGE.createBlockData());

        try {
            if (lines[1].contains(",")) {
                spongeCoordCommand(spongeLaunchBlock, p, sign, spongeBlock, color);
            } else if (lines[1].contains(">") && lines[2].contains(">") && lines[3].contains(">")) {
                spongeRandomVectorCommand(spongeLaunchBlock, p, sign, spongeBlock, color);
            } else if (!(lines[0].equals("") || lines[1].equals("") || lines[2].equals("") || lines[3].equals(""))) {
                spongeVectorCommand(spongeLaunchBlock, p, sign, spongeBlock, color);
            }
        } catch (Exception ex) {
            invalidSpongeMessage(p);
        }
    }

    private void spongeRandomVectorCommand(FallingBlock spongeLaunchBlock, Player p, Sign sign, Block spongeBlock, Color color) {
        String[] lines = sign.getLines();

        String[] line1 = lines[1].split(">");
        String[] line2 = lines[2].split(">");
        String[] line3 = lines[3].split(">");

        if (line1.length != 2) return;
        if (line2.length != 2) return;
        if (line3.length != 2) return;

        float xMin = Float.parseFloat(line1[0]);
        float yMin = Float.parseFloat(line2[0]);
        float zMin = Float.parseFloat(line3[0]);

        float xMax = Float.parseFloat(line1[1]);
        float yMax = Float.parseFloat(line2[1]);
        float zMax = Float.parseFloat(line3[1]);

        float x = xMin + (float) Math.random() * (xMax - xMin);
        float y = yMin + (float) Math.random() * (yMax - yMin);
        float z = zMin + (float) Math.random() * (zMax - zMin);

        spongeLaunchBlock.setVelocity(new Vector(x, y, z));
        spawnLauncherParticles(spongeBlock.getLocation(), color);
        spongeLaunchBlock.addPassenger(p);
        spongeLaunchBlock.setDropItem(false);
    }

    private void spongeVectorCommand(FallingBlock spongeLaunchBlock, Player p, Sign sign, Block spongeBlock, Color color) {

        String[] lines = sign.getLines();

        float x = Float.parseFloat(lines[1]);
        float y = Float.parseFloat(lines[2]);
        float z = Float.parseFloat(lines[3]);

        spongeLaunchBlock.setVelocity(new Vector(x, y, z));
        spawnLauncherParticles(spongeBlock.getLocation(), color);
        spongeLaunchBlock.addPassenger(p);
        spongeLaunchBlock.setDropItem(false);
    }

    private void spongeCoordCommand(FallingBlock spongeLaunchBlock, Player p, Sign sign, Block spongeBlock, Color color) {
        String[] lines = sign.getLines();
        String[] coords = lines[1].split(",");

        if (coords.length != 3) {
            return;
        }

        int xDest = Integer.parseInt(coords[0]);
        int yDest = Integer.parseInt(coords[1]);
        int zDest = Integer.parseInt(coords[2]);

        double xLoc = spongeLaunchBlock.getLocation().getBlockX();
        double yLoc = spongeLaunchBlock.getLocation().getBlockY();
        double zLoc = spongeLaunchBlock.getLocation().getBlockZ();

        double yMax = Integer.parseInt(lines[2]);

        double xVec = (xDest - xLoc);
        double yVec = (yDest - yLoc);
        double zVec = (zDest - zLoc);

        double lateralDist = Math.sqrt(((xVec) * (xVec) + (zVec) * (zVec)));
        double height = Math.abs(yMax + yVec);

        double y = (-11.627 + Math.sqrt(13.012 * height + 206.525419)) / 6.506;

        double heightCoef = 1 / (8.7802 * Math.log(height) + 9.4847);

        double xzDist = heightCoef * lateralDist;
        double thetaLat = Math.atan2(xVec, zVec);

        double x = Math.sin(thetaLat) * xzDist;
        double z = Math.cos(thetaLat) * xzDist;


        double length = Math.sqrt(Math.abs((xVec * xVec)) + Math.abs((yVec * yVec)) + Math.abs((zVec * zVec)));

        Vector vec = new Vector(x, y, z);

        spongeLaunchBlock.setVelocity(vec);
        spawnLauncherParticles(spongeBlock.getLocation(), color);
        spongeLaunchBlock.addPassenger(p);
        spongeLaunchBlock.setDropItem(false);

    }

    @EventHandler
    private void onSpongeLand(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getTo() == Material.SPONGE) {
            event.setCancelled(true);
        }
    }

    // ==================================
    //        Creation Methods
    // ==================================
    public boolean createSpongeLauncher(CommandSender sender, TeamColor color, int destX, int destY, int destZ, int height, boolean replaceBlocks) {
        if (!sender.hasPermission("fw.sponge")) {
            sender.sendMessage("§cYou do not have permission to use this command!");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command");
            return false;
        }
        Player player = (Player) sender;
        createSpongeBlock(player, color, destX, destY, destZ, height, replaceBlocks);
        return true;
    }

    public boolean createSpongeLauncher(CommandSender sender, TeamColor color, float vecX, float vecY, float vecZ, boolean replaceBlocks) {
        if (!sender.hasPermission("fw.sponge")) {
            sender.sendMessage("§cYou do not have permission to use this command!");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command");
            return false;
        }
        Player player = (Player) sender;
        createSpongeBlock(player, color, vecX, vecY, vecZ, replaceBlocks);
        return true;
    }

    public boolean createSpongeLauncher(CommandSender sender, TeamColor color, float vecMinX, float vecMaxX, float vecMinY, float vecMaxY, float vecMinZ, float vecMaxZ, boolean replaceBlocks) {
        if (!sender.hasPermission("fw.sponge")) {
            sender.sendMessage("§cYou do not have permission to use this command!");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command");
            return false;
        }
        Player player = (Player) sender;
        createSpongeBlock(player, color, vecMinX, vecMaxX, vecMinY, vecMaxY, vecMinZ, vecMaxZ, replaceBlocks);
        return true;
    }

    public void createSpongeBlock(Player player, TeamColor spongeColor, int destX, int destY, int destZ, int height, boolean replaceBlocks) {
        Location spongeLoc = player.getLocation().clone().subtract(0, 1, 0);
        Location signLoc = spongeLoc.clone().subtract(0, 1, 0);

        BlockFace signFace = checkSignBlock(signLoc, spongeColor);

        Block spongeBlock = spongeLoc.getBlock();
        Block signBlock = signLoc.getBlock();

        spongeBlock.setType(Material.SPONGE);
        signBlock.setType(Material.OAK_WALL_SIGN);

        WallSign wallSign = (WallSign) signBlock.getBlockData();
        wallSign.setFacing(signFace);
        signBlock.setBlockData(wallSign);
        Sign signData = (Sign) signBlock.getState();
        signData.setLine(0, spongeColor.toString());
        signData.setLine(1, destX + "," + destY + "," + destZ);
        signData.setLine(2, height + "");
        signData.update();

        if (replaceBlocks) replaceTeamBlocks(spongeLoc, spongeColor);
    }

    public void createSpongeBlock(Player player, TeamColor spongeColor, float vecX, float vecY, float vecZ, boolean replaceBlocks) {
        Location spongeLoc = player.getLocation().clone().subtract(0, 1, 0);
        Location signLoc = spongeLoc.clone().subtract(0, 1, 0);

        BlockFace signFace = checkSignBlock(signLoc, spongeColor);

        Block spongeBlock = spongeLoc.getBlock();
        Block signBlock = signLoc.getBlock();

        spongeBlock.setType(Material.SPONGE);
        signBlock.setType(Material.OAK_WALL_SIGN);
        WallSign wallSign = (WallSign) signBlock.getBlockData();
        wallSign.setFacing(signFace);
        signBlock.setBlockData(wallSign);
        Sign signData = (Sign) signBlock.getState();
        signData.setLine(0, spongeColor.toString());
        signData.setLine(1, vecX + "");
        signData.setLine(2, vecY + "");
        signData.setLine(3, vecZ + "");
        signData.update();

        if (replaceBlocks) replaceTeamBlocks(spongeLoc, spongeColor);
    }

    public void createSpongeBlock(Player player, TeamColor color, float vecMinX, float vecMaxX, float vecMinY, float vecMaxY, float vecMinZ, float vecMaxZ, boolean replaceBlocks) {
        Location spongeLoc = player.getLocation().clone().subtract(0, 1, 0);
        Location signLoc = spongeLoc.clone().subtract(0, 1, 0);

        BlockFace signFace = checkSignBlock(signLoc, color);

        Block spongeBlock = spongeLoc.getBlock();
        Block signBlock = signLoc.getBlock();

        spongeBlock.setType(Material.SPONGE);
        signBlock.setType(Material.OAK_WALL_SIGN);
        WallSign wallSign = (WallSign) signBlock.getBlockData();
        wallSign.setFacing(signFace);
        signBlock.setBlockData(wallSign);
        Sign signData = (Sign) signBlock.getState();
        signData.setLine(0, color.toString());
        signData.setLine(1, vecMinX + ">" + vecMaxX);
        signData.setLine(2, vecMinY + ">" + vecMaxY);
        signData.setLine(3, vecMinZ + ">" + vecMaxZ);
        signData.update();

        if (replaceBlocks) setEmeraldBlocks(spongeLoc);
    }

    private void setEmeraldBlocks(Location spongeLoc) {
        Location north = spongeLoc.clone().add(0, 0, -1);
        Location south = spongeLoc.clone().add(0, 0, 1);
        Location east = spongeLoc.clone().add(1, 0, 0);
        Location west = spongeLoc.clone().add(-1, 0, 0);

        north.getBlock().setType(Material.EMERALD_BLOCK);
        south.getBlock().setType(Material.EMERALD_BLOCK);
        east.getBlock().setType(Material.EMERALD_BLOCK);
        west.getBlock().setType(Material.EMERALD_BLOCK);
    }

    private Material getTeamColorBlockMaterial(TeamColor teamColor) {
        return switch (teamColor) {
            case RED -> Material.REDSTONE_BLOCK;
            case BLUE -> Material.LAPIS_BLOCK;
            default -> Material.IRON_BLOCK;
        };
    }

    private void replaceTeamBlocks(Location spongeLoc, TeamColor spongeColor) {
        Location north = spongeLoc.clone().add(0, 0, -1);
        Location south = spongeLoc.clone().add(0, 0, 1);
        Location east = spongeLoc.clone().add(1, 0, 0);
        Location west = spongeLoc.clone().add(-1, 0, 0);

        Material blockType = getTeamColorBlockMaterial(spongeColor);

        north.getBlock().setType(blockType);
        south.getBlock().setType(blockType);
        east.getBlock().setType(blockType);
        west.getBlock().setType(blockType);
    }

    private BlockFace checkSignBlock(Location signLoc, TeamColor color) {

        Block placeholder;
        Location north = signLoc.clone().add(0, 0, -1);
        Location south = signLoc.clone().add(0, 0, 1);
        Location east = signLoc.clone().add(1, 0, 0);
        Location west = signLoc.clone().add(-1, 0, 0);

        HashMap<BlockFace, Location> signFacesMap = new HashMap<>();
        signFacesMap.put(BlockFace.SOUTH, north);
        signFacesMap.put(BlockFace.NORTH, south);
        signFacesMap.put(BlockFace.WEST, east);
        signFacesMap.put(BlockFace.EAST, west);

        for (BlockFace face : signFacesMap.keySet()) {
            placeholder = signFacesMap.get(face).getBlock();
            if (!placeholder.isEmpty()) {
                return face;
            }
        }
        Block northBlock = north.getBlock();
        northBlock.setType(getTeamColorBlockMaterial(color));
        return BlockFace.SOUTH;
    }
}
