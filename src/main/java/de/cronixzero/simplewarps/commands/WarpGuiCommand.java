/* 
Coded for SimpleWarps
Made by CronixZero
Created 14.08.2021 - 19:06
 */

package de.cronixzero.simplewarps.commands;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import de.cronixzero.simplewarps.SimpleWarps;
import de.cronixzero.simplewarps.utils.ItemBuilder;
import de.cronixzero.simplewarps.utils.PlayerHeadBuilder;
import de.cronixzero.simplewarps.warps.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WarpGuiCommand implements CommandExecutor {

    private static final String PAGE_ITEM_NAME = "§e%d§8/§e%d";
    private final SimpleWarps plugin;
    private final GuiItem glass;
    private final GuiItem noWarps;
    private final ItemStack back;

    public WarpGuiCommand(SimpleWarps plugin) {
        this.plugin = plugin;
        this.glass = new GuiItem(new ItemBuilder(plugin, Material.LIME_STAINED_GLASS_PANE).setName("§e").build());
        this.noWarps = new GuiItem(new ItemBuilder(plugin, Material.BARRIER).setName("§cEs sind keine Warps vorhanden").build());
        this.back = new ItemBuilder(plugin, Material.BARRIER).setName("§cZurück").build();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SimpleWarps.getPrefix(true) + "§cDieser Command darf nur von Spielern benutzt werden.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("simplewarps.warpgui")) {
            player.sendMessage(SimpleWarps.getPrefix(false) + "§cDu darfst diesen Command nicht benutzen.");
            return true;
        }

        openMainMenu(player);

        return true;
    }

    /**
     * The Main Menu with options to teleport to Warps
     *
     * @param player The Player, that uses the GUI
     *
     * @see #openSettingsMenu(Player)
     * */
    private void openMainMenu(Player player) {
        ChestGui gui = new ChestGui(6, "§2Navigator");

        gui.setOnGlobalClick(e -> e.setCancelled(true));

        /*
         + GUI Layout
         *
         * g | Glass
         * s | Settings / Glass
         * x | -
         * */
        PatternPane patternPane = new PatternPane(9, 6, new Pattern(
                "ggggggggg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "ggggggggs"));

        patternPane.bindItem('g', glass);

        if (player.hasPermission("SimpleWarps.GUI.Admin"))
            patternPane.bindItem('s', new GuiItem(new ItemBuilder(plugin, Material.ANVIL).setName("§cEinstellungen").build(),
                    e -> {
                        openSettingsMenu(player);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }));
        else
            patternPane.bindItem('s', glass);

        StaticPane pane = new StaticPane(0, 0, 9, 6);

        Material[] icons = SimpleWarps.getWarpProvider().getIcons();

        for (Warp w : SimpleWarps.getWarpProvider().getMenuWarps())
            pane.addItem(new GuiItem(new ItemBuilder(plugin, icons[w.getIcon()])
                    .setName("§e" + ChatColor.translateAlternateColorCodes('&', w.getName()))
                    .setLore(ChatColor.translateAlternateColorCodes('&', w.getDescription())).build(),
                    e -> {
                        w.warpTo(player);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    }), w.getMenuX(), w.getMenuY());

        if (SimpleWarps.getWarpProvider().getMenuWarps().isEmpty())
            pane.addItem(noWarps, 4, 2);

        gui.addPane(patternPane);
        gui.addPane(pane);

        gui.show(player);
    }

    /**
     * List of all options to set up the Warps
     * 
     * @param player The Player, that uses the GUI
     *
     * @see #openWarpSettingsMenu(Player, int)
     * @see #openCustomizationWarpListMenu(Player, int)
     * */
    private void openSettingsMenu(Player player) {
        ChestGui gui = new ChestGui(3, "§2Einstellungen");

        gui.setOnGlobalClick(e -> e.setCancelled(true));

        /*
         * GUI Layout
         *
         * g | Glass
         * s | Spawn
         * d | Design
         * w | (Customize) Warps
         * b | Back
         * x | -
         * */
        PatternPane pp = new PatternPane(9, 3, new Pattern("ggggggggg", "gggdgwggg", "bgggggggg"));

        pp.bindItem('g', glass);
        pp.bindItem('d', new GuiItem(new ItemBuilder(plugin, Material.CHEST).setName("§cHauptmenü Gestaltung").build(),
                e -> {
                    openCustomizationWarpListMenu(player, 0);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                }));
        pp.bindItem('w', new GuiItem(new ItemBuilder(plugin, Material.ACACIA_DOOR).setName("§5Warp Einstellungen").build(),
                e -> {
                    openWarpSettingsMenu(player, 0);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                }));
        pp.bindItem('b', new GuiItem(back,
                e -> {
                    openMainMenu(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, -2);
                }));

        gui.addPane(pp);

        gui.show(player);
    }

    /**
     * List all Warps with options to set them up
     *
     * @param player The Player, that uses the GUI
     * @param page The Page, the player user is on
     *
     * @see #openDescriptionSettings(Player, Warp, int)
     * */
    private void openWarpSettingsMenu(Player player, int page) {
        ChestGui gui = new ChestGui(6, "§2Warp Einstellungen");

        gui.setOnGlobalClick(e -> e.setCancelled(true));

        PaginatedPane pagePane = new PaginatedPane(1, 1, 7, 4);

        List<GuiItem> warps = new ArrayList<>();
        Material[] icons = SimpleWarps.getWarpProvider().getIcons();

        for (Map.Entry<String, Warp> we : SimpleWarps.getWarpProvider().getWarps().entrySet()) {
            Warp w = we.getValue();

            GuiItem item = new GuiItem(new ItemBuilder(plugin, icons[w.getIcon()])
                    .setName("§e" + ChatColor.translateAlternateColorCodes('&', w.getName()))
                    .setLore("§7" + ChatColor.translateAlternateColorCodes('&', w.getDescription()), "§e",
                            "§9Linksklick §8» §7Vorheriges Icon", "§9Rechtsklick §8» §7Nächstes Icon", "§9Drop §8» §7Beschreibung ändern")
                    .build());

            item.setAction(e -> {
                switch (e.getClick()) {
                    case LEFT:
                        w.setIcon(w.getIcon() - 1 < 0 ? icons.length - 1 : w.getIcon() - 1);
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                        openWarpSettingsMenu(player, pagePane.getPage());
                        break;

                    case RIGHT:
                        w.setIcon(w.getIcon() + 1 >= icons.length ? 0 : w.getIcon() + 1);
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                        openWarpSettingsMenu(player, pagePane.getPage());
                        break;

                    case DROP:
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                        openDescriptionSettings(player, w, pagePane.getPage());
                        break;

                    default:
                        break;
                }
            });

            warps.add(item);
        }

        if (SimpleWarps.getWarpProvider().getWarps().isEmpty())
            warps.add(noWarps);

        pagePane.populateWithGuiItems(warps);
        pagePane.setPage(page);

        /*
         * GUI Layout
         *
         * g | Glass
         * l | Last Page
         * c | Current Page
         * n | Next Page
         * b | Back
         * x | -
         * */
        PatternPane pp = new PatternPane(9, 6, new Pattern(
                "ggggggggg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "bgglcnggg"));

        ItemBuilder currentPage = new ItemBuilder(plugin, Material.ITEM_FRAME)
                .setName(String.format(PAGE_ITEM_NAME,
                        pagePane.getPage() + 1, pagePane.getPages()));

        GuiItem nextPage = new GuiItem(new PlayerHeadBuilder()
                .setName("§7Nächste Seite").setSkullOwner("MHF_ArrowRight").build(), e -> {
            if (pagePane.getPage() >= pagePane.getPages() - 1)
                return;

            pagePane.setPage(pagePane.getPage() + 1);

            pp.bindItem('c', new GuiItem(currentPage.setName(String.format(PAGE_ITEM_NAME,
                    pagePane.getPage() + 1, pagePane.getPages())).build()));

            gui.update();
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        });

        GuiItem lastPage = new GuiItem(new PlayerHeadBuilder()
                .setName("§7Letzte Seite").setSkullOwner("MHF_ArrowLeft").build(), e -> {
            if (pagePane.getPage() == 0)
                return;

            pagePane.setPage(pagePane.getPage() - 1);

            pp.bindItem('c', new GuiItem(currentPage.setName(String.format(PAGE_ITEM_NAME,
                    pagePane.getPage() + 1, pagePane.getPages())).build()));

            gui.update();
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        });


        pp.bindItem('g', glass);
        pp.bindItem('l', lastPage);
        pp.bindItem('c',
                new GuiItem(currentPage.setName("§e" + (pagePane.getPage() + 1) + "§8/§e" + pagePane.getPages()).build()));
        pp.bindItem('n', nextPage);
        pp.bindItem('b', new GuiItem(back, e -> {
            openSettingsMenu(player);
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, -2);
        }));

        gui.addPane(pp);
        gui.addPane(pagePane);

        gui.show(player);
    }

    /**
     * Open the Anvil with an option to add a Description
     * 
     * @param warp The warp to set the Description for
     * @param player The Player, that uses the GUI
     * @param page The page, the user was on the WarpSettingsMenu
     *             
     * @see #openWarpSettingsMenu(Player, int) 
     * */
    private void openDescriptionSettings(Player player, Warp warp, int page) {
        AnvilGui anvil = new AnvilGui("§2Beschreibung");

        anvil.setOnGlobalClick(e -> e.setCancelled(true));

        StaticPane pane = new StaticPane(1, 1);

        pane.addItem(new GuiItem(new ItemBuilder(plugin, Material.PAPER).setName(" ").build()), 0, 0);

        anvil.getFirstItemComponent().addPane(pane);

        anvil.setOnTopClick(e -> {
            warp.setDescription(anvil.getRenameText());
            openWarpSettingsMenu(player, page);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        });

        anvil.show(player);
    }

    /**
     * List all Warps with usage of #openCustomizationPicker
     * 
     * @param player The player, that uses the GUI
     * @param page The page, the user was on in the WarpSettingsMenu
     * 
     * @see #openCustomizationPicker(Player, Warp, int)
     * @see #openWarpSettingsMenu(Player, int)
     */
    private void openCustomizationWarpListMenu(Player player, int page) {
        ChestGui gui = new ChestGui(6, "§2Gestaltung");

        gui.setOnGlobalClick(e -> e.setCancelled(true));

        PaginatedPane pagePane = new PaginatedPane(1, 1, 7, 4);

        List<GuiItem> warps = new ArrayList<>();
        Material[] icons = SimpleWarps.getWarpProvider().getIcons();

        for (Map.Entry<String, Warp> we : SimpleWarps.getWarpProvider().getWarps().entrySet()) {
            Warp w = we.getValue();
            boolean onMenu = w.isOnMenu();

            GuiItem item = new GuiItem(new ItemBuilder(plugin, icons[w.getIcon()])
                    .setName("§e" + ChatColor.translateAlternateColorCodes('&', w.getName()))
                    .setLore("§7" + ChatColor.translateAlternateColorCodes('&', w.getDescription()), "§e",
                            onMenu ? "§9Linksklick §8» §7Entfernen" : "§9Linksklick §8» §7Hinzufügen").build());

            item.setAction(e -> {
                if (!e.isLeftClick())
                    return;

                if (onMenu) {
                    w.removeFromMenu();
                    w.setOnMenu(false);
                    player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, -2);
                    openCustomizationWarpListMenu(player, page);
                    return;
                }

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                openCustomizationPicker(player, w, pagePane.getPage());
            });

            warps.add(item);
        }

        if (SimpleWarps.getWarpProvider().getWarps().isEmpty())
            warps.add(noWarps);

        pagePane.populateWithGuiItems(warps);
        pagePane.setPage(page);

        /*
         * GUI Layout
         *
         * g | Glass
         * l | Last Page
         * c | Current Page
         * n | Next Page
         * b | Back
         * x | -
         * */
        PatternPane pp = new PatternPane(9, 6, new Pattern(
                "ggggggggg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "bgglcnggg"));

        ItemBuilder currentPage = new ItemBuilder(plugin, Material.ITEM_FRAME)
                .setName("§e" + (pagePane.getPage() + 1) + "§8/§e" + (pagePane.getPages() == 0 ? pagePane.getPages() + 1 : pagePane.getPages()));

        GuiItem nextPage = new GuiItem(new PlayerHeadBuilder()
                .setName("§7Nächste Seite").setSkullOwner("MHF_ArrowRight").build(), e -> {
            e.setCancelled(true);

            if (pagePane.getPage() >= pagePane.getPages() - 1)
                return;

            pagePane.setPage(pagePane.getPage() + 1);

            pp.bindItem('c', new GuiItem(
                    currentPage.setName("§e" + (pagePane.getPage() + 1)
                            + "§8/§e" + (pagePane.getPages() == 0 ? pagePane.getPages() + 1 : pagePane.getPages())).build()));

            gui.update();
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        });

        GuiItem lastPage = new GuiItem(new PlayerHeadBuilder()
                .setName("§7Letzte Seite").setSkullOwner("MHF_ArrowLeft").build(), e -> {
            e.setCancelled(true);

            if (pagePane.getPage() == 0)
                return;

            pagePane.setPage(pagePane.getPage() - 1);

            pp.bindItem('c', new GuiItem(currentPage.setName("§e" + (pagePane.getPage() + 1) + "§8/§e" + pagePane.getPages()).build(),
                    e2 -> e2.setCancelled(true)));

            gui.update();
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        });


        pp.bindItem('g', glass);
        pp.bindItem('l', lastPage);
        pp.bindItem('c',
                new GuiItem(currentPage.setName("§e" + (pagePane.getPage() + 1) + "§8/§e" + pagePane.getPages()).build()));
        pp.bindItem('n', nextPage);
        pp.bindItem('b', new GuiItem(back, e -> {
            openSettingsMenu(player);
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, -2);
        }));

        gui.addPane(pp);
        gui.addPane(pagePane);

        gui.show(player);
    }

    /**
     * Add a warp to the Main-Page
     *
     * @param player The Player, that uses the GUI
     * @param warp   The Warp, that is being set
     * @param page   The page, the user was on in the Warp List
     * @see #openCustomizationWarpListMenu(Player, int)
     */
    private void openCustomizationPicker(Player player, Warp warp, int page) {
        ChestGui gui = new ChestGui(6, "§2Warp Hinzufügen");

        gui.setOnGlobalClick(e -> e.setCancelled(true));

        gui.setOnTopClick(e -> {
            if (e.getCurrentItem() != null) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return;
            }

            int y = e.getSlot() / 9;
            int x = e.getSlot() - (9 * (e.getSlot() / 9));

            warp.setMenuX(x);
            warp.setMenuY(y);
            warp.setOnMenu(true);
            SimpleWarps.getWarpProvider().addMenuWarp(warp);
            openCustomizationWarpListMenu(player, page);
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, -2);
        });

        /*
         + GUI Layout
         *
         * g | Glass
         * s | Settings / Glass
         * x | -
         * */
        PatternPane patternPane = new PatternPane(9, 6, new Pattern(
                "ggggggggg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "gxxxxxxxg",
                "ggggggggg"));

        patternPane.bindItem('g', glass);

        StaticPane pane = new StaticPane(0, 0, 9, 6);
        Material[] icons = SimpleWarps.getWarpProvider().getIcons();

        for (Warp w : SimpleWarps.getWarpProvider().getMenuWarps())
            pane.addItem(new GuiItem(new ItemBuilder(plugin, icons[w.getIcon()])
                            .setName("§e" + ChatColor.translateAlternateColorCodes('&', w.getName()))
                            .setLore(ChatColor.translateAlternateColorCodes('&', warp.getDescription())).build()),
                    w.getMenuX(), w.getMenuY());

        gui.addPane(patternPane);
        gui.addPane(pane);

        gui.show(player);
    }
}
