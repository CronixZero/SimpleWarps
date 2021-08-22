package de.cronixzero.simplewarps.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The ItemBuilder is made, to make building Items easier.
 *
 * @author CronixZero
 * @version 2.0
 */
public class ItemBuilder {

    private final ItemStack is;
    private final ItemMeta meta;
    private final Plugin plugin;

    public ItemBuilder(Plugin plugin, ItemStack is) {
        if (is == null)
            throw new IllegalArgumentException("ItemStack cannot be null");

        this.plugin = plugin;
        this.is = is;
        this.meta = is.getItemMeta();
    }

    public ItemBuilder(Plugin plugin, Material material) {
        this.plugin = plugin;
        this.is = new ItemStack(material);
        this.meta = is.getItemMeta();
    }

    public ItemBuilder(Plugin plugin, Material material, int dmg) {
        this.plugin = plugin;
        this.is = new ItemStack(material, 1);
        this.meta = is.getItemMeta();

        setDurability(dmg);
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment) {
        is.addEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag... flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        meta.setLore(Collections.singletonList(lore));
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder setLore(String lore) {
        addLore(lore);
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        ((Damageable) meta).setDamage(durability);
        return this;
    }


    public ItemBuilder removeLore(int line) {
        List<String> newLore = meta.getLore();

        if (newLore == null)
            return this;

        newLore.remove(line);
        meta.setLore(newLore);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder addNBTTag(String tag, PersistentDataType<Object, Object> type, Object value) {
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, tag), type, value);
        return this;
    }

    public ItemBuilder removeNBTTag(String tag) {
        meta.getPersistentDataContainer().remove(new NamespacedKey(plugin, tag));
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemStack build() {
        is.setItemMeta(meta);
        return is;
    }
}
