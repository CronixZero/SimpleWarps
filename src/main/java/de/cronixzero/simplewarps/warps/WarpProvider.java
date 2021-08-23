/* 
Coded for SimpleWarps
Made by CronixZero
Created 10.08.2021 - 21:58
 */

package de.cronixzero.simplewarps.warps;

import de.cronixzero.simplewarps.SimpleWarps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class WarpProvider {

    private final SimpleWarps plugin;
    private final FileConfiguration config;
    private final Material[] icons = new Material[]{Material.GRASS_BLOCK, Material.ANVIL, Material.END_PORTAL_FRAME, Material.STICK,
            Material.BLAZE_ROD, Material.BOW, Material.COOKIE, Material.GOLD_BLOCK, Material.GOLD_INGOT, Material.APPLE,
            Material.JACK_O_LANTERN, Material.BARRIER, Material.GOLDEN_PICKAXE, Material.DIAMOND_BOOTS};
    private final Map<String, Warp> warps = new HashMap<>();
    private final Set<Warp> menuWarps = new HashSet<>();

    public WarpProvider(SimpleWarps plugin) {
        this.plugin = plugin;

        config = plugin.getWarpConfig();
    }

    /**
     * Get a Warp, which is properly functioning
     *
     * @param name Name of the Warp
     * @return a Warp loaded from the warps.yml
     * @throws IllegalStateException When the warp isn't existent in the warps.yml
     */
    public Warp getWarp(String name) {
        return warps.computeIfAbsent(name, k -> {
            Warp w = loadWarp(name);

            if (w == null)
                throw new IllegalStateException("Warp could not be found or is dirty");

            if (w.isOnMenu())
                menuWarps.add(w);

            return w;
        });
    }

    /**
     * Load all Warps defined in the warps.yml
     */
    public void loadAllWarps() {
        for (String name : config.getKeys(false)) {
            try {
                getWarp(name);
            } catch (IllegalStateException e) {
                Bukkit.getLogger().log(Level.SEVERE, e, () -> "Warp '" + name + "' could not be loaded (" + e.getMessage() + ")");
            }
        }
    }

    /**
     * Load warps. This will get every information out of the warps.yml and create a Warp out of it
     *
     * @param name Name of the Warp
     * @return The Warp from the warps.yml | null, when not functioning
     */
    public Warp loadWarp(String name) {
        if (!checkWarpFunction(name))
            return null;

        World world = Bukkit.getWorld(config.getString(name + ".world"));
        double x = config.getDouble(name + ".x");
        double y = config.getDouble(name + ".y");
        double z = config.getDouble(name + ".z");
        int yaw = config.getInt(name + ".yaw");
        int pitch = config.getInt(name + ".pitch");

        Warp warp = new Warp(plugin, name, config.getString(name + ".creator"), new Location(world, x, y, z, yaw, pitch),
                config.getConfigurationSection(name));

        if (config.isSet(name + ".description"))
            warp.setDescription(config.getString(name + ".description"));
        else
            warp.setDescription("&7Warp created by &e" + config.getString(name + ".creator"));

        if (config.isSet(name + ".icon"))
            warp.setIcon(config.getInt(name + ".icon"));

        if (config.isSet(name + ".menu.x"))
            warp.setMenuX(config.getInt(name + ".menu.x"));

        if (config.isSet(name + ".menu.y"))
            warp.setMenuY(config.getInt(name + ".menu.y"));

        if (config.isSet(name + ".menu.x") && config.isSet(name + ".menu.y"))
            warp.setOnMenu(true);

        return warp;
    }

    /**
     * Register a warp to the System
     *
     * @param name     Name of the new Warp (No dupes)
     * @param location The Location of the Warp to teleport the players to
     * @return The registered Warp
     */
    public Warp registerWarp(String name, String creator, Location location) {
        if (warps.containsKey(name))
            return getWarp(name);

        if (!location.isWorldLoaded() || location.getWorld() == null)
            throw new IllegalStateException("World must be loaded or available");

        config.set(name + ".creator", creator);
        config.set(name + ".world", location.getWorld().getName());
        config.set(name + ".x", location.getX());
        config.set(name + ".y", location.getY());
        config.set(name + ".z", location.getZ());
        config.set(name + ".yaw", location.getYaw());
        config.set(name + ".pitch", location.getPitch());

        try {
            plugin.saveWarpConfig();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, e, () -> "Could not remove Warp '" + name + "' to warps.yml!");
        }

        Warp warp = new Warp(plugin, name, creator, location, config.getConfigurationSection(name));

        warp.setDescription("&7Warp created by &e" + creator);

        warps.put(name, warp);

        return warp;
    }

    /**
     * Delete a Warp from the warps.yml
     *
     * @param warp The Warp to remove
     */
    public void deleteWarp(Warp warp) {
        config.set(warp.getName(), null);
        if (menuWarps.contains(warp))
            removeMenuWarp(warp);

        if (warps.containsValue(warp))
            warps.remove(warp.getName(), warp);

        try {
            plugin.saveWarpConfig();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, e, () -> "Could not remove Warp '" + warp.getName() + "' from warps.yml!");
        }
    }

    /**
     * Check, whether a Warp is properly setup in the warps.yml or not
     *
     * @param name Name of the warp
     * @return Whether the warp is properly setup or not
     */
    private boolean checkWarpFunction(String name) {
        if (!config.isSet(name + ".world"))
            return false;

        if (!config.isSet(name + ".x"))
            return false;

        if (!config.isSet(name + ".y"))
            return false;

        if (!config.isSet(name + ".z"))
            return false;

        if (!config.isSet(name + ".yaw"))
            return false;

        if (!config.isSet(name + ".creator"))
            return false;

        return config.isSet(name + ".pitch");
    }

    public void addMenuWarp(Warp warp) {
        menuWarps.add(warp);
    }

    public void removeMenuWarp(Warp warp) {
        menuWarps.remove(warp);
    }

    public Map<String, Warp> getWarps() {
        return warps;
    }

    public Set<Warp> getMenuWarps() {
        return menuWarps;
    }

    public Material[] getIcons() {
        return icons;
    }
}
