/* 
Coded for SimpleWarps
Made by CronixZero
Created 10.08.2021 - 17:30
 */

package de.cronixzero.simplewarps;

import de.cronixzero.simplewarps.commands.*;
import de.cronixzero.simplewarps.warps.WarpProvider;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class SimpleWarps extends JavaPlugin {

    private FileConfiguration warpConfig;
    private File warpConfigFile;

    private static WarpProvider warpProvider;
    private static final String PREFIX = "§8» §a§lSimple§3§lWarps §8▏§7 ";
    private static final String PREFIX_CONSOLE = "§8>> §a§lSimple§3§lWarps §8|§7 ";

    @Override
    public void onEnable() {
        warpConfigFile = new File(getDataFolder(), "warps.yml");
        warpConfig = YamlConfiguration.loadConfiguration(warpConfigFile);
        setWarpProvider(new WarpProvider(this));

        if (!warpConfigFile.exists())
            try {
                if ((!getDataFolder().exists() && !getDataFolder().mkdirs()) || !warpConfigFile.createNewFile()) {
                    Bukkit.getLogger().log(Level.SEVERE, "Could not create Warps.yml!");
                    Bukkit.getPluginManager().disablePlugin(this);
                }
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, e, () -> "Could not create Warps.yml!");
                Bukkit.getPluginManager().disablePlugin(this);
            }

        warpProvider.loadAllWarps();

        getCommand("warpGui").setExecutor(new WarpGuiCommand(this));
        getCommand("setWarp").setExecutor(new SetWarpCommand());
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("warpList").setExecutor(new WarpListCommand());
        getCommand("deleteWarp").setExecutor(new DeleteWarpCommand());
    }

    public void saveWarpConfig() throws IOException {
        warpConfig.save(warpConfigFile);
    }

    private static void setWarpProvider(WarpProvider warpProvider) {
        SimpleWarps.warpProvider = warpProvider;
    }

    public FileConfiguration getWarpConfig() {
        return warpConfig;
    }

    public static WarpProvider getWarpProvider() {
        return warpProvider;
    }

    public static String getPrefix(boolean console) {
        return console ? PREFIX_CONSOLE : PREFIX;
    }
}
