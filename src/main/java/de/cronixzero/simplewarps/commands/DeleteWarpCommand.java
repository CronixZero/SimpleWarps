/* 
Coded for SimpleWarps
Made by CronixZero
Created 21.08.2021 - 17:09
 */

package de.cronixzero.simplewarps.commands;

import de.cronixzero.simplewarps.SimpleWarps;
import de.cronixzero.simplewarps.warps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteWarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean console = !(sender instanceof Player);

        if (!sender.hasPermission("simplewarps.deletewarp")) {
            sender.sendMessage(SimpleWarps.getPrefix(console) + "§cDu darfst diesen Command nicht benutzen.");
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(SimpleWarps.getPrefix(console) + "§cDu musst den Namen von dem Warp angeben.");
            return true;
        }

        Warp warp;

        try {
            warp = SimpleWarps.getWarpProvider().getWarp(args[0]);
        } catch (IllegalStateException e) {
            sender.sendMessage(SimpleWarps.getPrefix(console) + "§cDer Warp existiert nicht!");
            return true;
        }

        SimpleWarps.getWarpProvider().deleteWarp(warp);
        sender.sendMessage(SimpleWarps.getPrefix(console) + "§7Der Warp wurde erfolgreich entfernt");
        return true;
    }
}
