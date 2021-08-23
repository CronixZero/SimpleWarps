/* 
Coded for SimpleWarps
Made by CronixZero
Created 13.08.2021 - 21:45
 */

package de.cronixzero.simplewarps.commands;

import de.cronixzero.simplewarps.SimpleWarps;
import de.cronixzero.simplewarps.warps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class WarpListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean console = !(sender instanceof Player);

        if (!sender.hasPermission("simplewarps.warplist")) {
            sender.sendMessage(SimpleWarps.getPrefix(console) + "§cDu darfst diesen Command nicht benutzen.");
            return true;
        }

        Map<String, Warp> warps = SimpleWarps.getWarpProvider().getWarps();
        StringBuilder message = new StringBuilder();

        if (warps.isEmpty()) {
            sender.sendMessage(SimpleWarps.getPrefix(console) + "§cEs sind keine Warps vorhanden :(");
            return true;
        }


        for (Map.Entry<String, Warp> e : warps.entrySet()) {
            if (message.toString().equalsIgnoreCase(""))
                message.append("§e").append(e.getKey());
            else
                message.append("§8, §e").append(e.getKey());
        }

        sender.sendMessage(SimpleWarps.getPrefix(console) + "Alle verfügbaren Warps§8:§e " + message);
        return true;
    }
}
