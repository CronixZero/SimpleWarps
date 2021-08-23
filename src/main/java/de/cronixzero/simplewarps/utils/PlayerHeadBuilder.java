package de.cronixzero.simplewarps.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;

public class PlayerHeadBuilder {

    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) skull.getItemMeta();

    public PlayerHeadBuilder setSkullOwner(String name) {
        meta.setOwner(name);
        return this;
    }

    public PlayerHeadBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public PlayerHeadBuilder setAmount(int amount) {
        skull.setAmount(amount);
        return this;
    }

    public PlayerHeadBuilder addEnchantment(Enchantment enchantment) {
        skull.addEnchantment(enchantment, 1);
        return this;
    }

    public PlayerHeadBuilder addEnchantment(Enchantment enchantment, int level) {
        skull.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public PlayerHeadBuilder addItemFlag(ItemFlag... flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public PlayerHeadBuilder addLore(String lore) {
        meta.setLore(Collections.singletonList(lore));
        return this;
    }

    public PlayerHeadBuilder setLore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public PlayerHeadBuilder setLore(String lore) {
        addLore(lore);
        return this;
    }

    public PlayerHeadBuilder removeLore(int line) {
        List<String> newLore = meta.getLore();

        if (newLore == null)
            return this;

        newLore.remove(line);
        meta.setLore(newLore);
        return this;
    }

    public ItemStack build() {
        skull.setItemMeta(meta);
        return skull;
    }
}
