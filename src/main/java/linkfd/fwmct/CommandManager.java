package linkfd.fwmct;

import linkfd.fwmct.gate.GateCommand;
import linkfd.fwmct.map.MapCommand;
import linkfd.fwmct.sponge.SpongeCommand;

public class CommandManager {

    public CommandManager(Main plugin) {
        new GateCommand(plugin);
        new MapCommand(plugin);
        new SpongeCommand(plugin);
    }
}
