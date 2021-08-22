/*
Coded for SimpleWarps
Made by CronixZero
Created 13.08.2021 - 16:45
 */

package de.cronixzero.simplewarps.commands;

import de.cronixzero.simplewarps.SimpleWarps;
import de.cronixzero.simplewarps.warps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SimpleWarps.getPrefix(true) + "§cDieser Command darf nur von Spielern benutzt werden.");
            return true;
        }

        if (!sender.hasPermission("simplewarps.warp")) {
            sender.sendMessage(SimpleWarps.getPrefix(false) + "§cDu darfst diesen Command nicht benutzen.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(SimpleWarps.getPrefix(false) + "§cDu musst einen Warp angeben. Liste alle mit §6'/warplist'§c auf.");
            return true;
        }

        Warp warp;

        try {
            warp = SimpleWarps.getWarpProvider().getWarp(args[0]);
        } catch (IllegalStateException e) {
            player.sendMessage(SimpleWarps.getPrefix(false) + "§cDer Warp konnte nicht gefunden werden.");
            return true;
        }

        warp.warpTo(player);
        player.sendMessage(SimpleWarps.getPrefix(false) + "§7Du wurdest§a erfolgreich§7 teleportiert!");

        return true;
    }
}
