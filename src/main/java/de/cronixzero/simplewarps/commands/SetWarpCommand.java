/* 
Coded for SimpleWarps
Made by CronixZero
Created 10.08.2021 - 19:59
 */

package de.cronixzero.simplewarps.commands;

import de.cronixzero.simplewarps.SimpleWarps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SimpleWarps.getPrefix(true) + "§cDieser Command darf nur von Spielern benutzt werden.");
            return true;
        }

        if (!sender.hasPermission("simplewarps.setwarp")) {
            sender.sendMessage(SimpleWarps.getPrefix(false) + "§cDu darfst diesen Command nicht benutzen.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(SimpleWarps.getPrefix(false) + "§cDu musst einen Namen für den Warp angeben.");
            return true;
        }

        SimpleWarps.getWarpProvider().registerWarp(args[0], player.getName(), player.getLocation());
        player.sendMessage(SimpleWarps.getPrefix(false) + "§7Der Warp §e" + args[0] + "§7 wurde§a erfolgreich§7 gesetzt!");

        return true;
    }
}
