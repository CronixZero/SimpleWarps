/* 
Coded for SimpleWarps
Made by CronixZero
Created 10.08.2021 - 21:58
 */

package de.cronixzero.simplewarps.warps;

import de.cronixzero.simplewarps.SimpleWarps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

public class Warp {

    private final String name;
    private final String creator;
    private final Location location;
    private boolean onMenu = false;

    private final SimpleWarps plugin;
    private final ConfigurationSection config;

    private int icon = 0;
    private String description = "";
    private int menuX;
    private int menuY;

    public Warp(SimpleWarps plugin, String name, String creator, Location location, ConfigurationSection config) {
        this.plugin = plugin;
        this.name = name;
        this.creator = creator;
        this.location = location;
        this.config = config;
    }

    /**
     * Teleport a specific Player to this warp
     */
    public void warpTo(Player player) {
        if (!location.isWorldLoaded() || location.getWorld() == null)
            throw new IllegalStateException("Could not teleport Player '" + player.getName() + "' because of dirty World");

        player.teleport(location);
    }

    /**
     * Teleport all Players on Server to this Warp
     */
    public void warpAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            warpTo(p);
        }
    }

    private void save() {
        try {
            plugin.saveWarpConfig();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, e, () -> "Could not save warps.yml");
        }
    }

    public void removeFromMenu() {
        config.set("menu.x", null);
        config.set("menu.y", null);
        SimpleWarps.getWarpProvider().removeMenuWarp(this);
        save();
    }

    public void setIcon(int icon) {
        this.icon = icon;
        config.set("icon", icon);
        save();
    }

    public void setMenuX(int menuX) {
        this.menuX = menuX;
        config.set("menu.x", menuX);
        save();
    }

    public void setMenuY(int menuY) {
        this.menuY = menuY;
        config.set("menu.y", menuY);
        save();
    }

    public void setDescription(String description) {
        this.description = description;
        config.set("description", description);
        save();
    }

    public void setOnMenu(boolean onMenu) {
        this.onMenu = onMenu;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public int getMenuX() {
        return menuX;
    }

    public int getMenuY() {
        return menuY;
    }

    public String getDescription() {
        return description;
    }

    public String getCreator() {
        return creator;
    }

    public boolean isOnMenu() {
        return onMenu;
    }
}
