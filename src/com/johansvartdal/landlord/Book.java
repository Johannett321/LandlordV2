package com.johansvartdal.landlord;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class Book {

    private ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
    private BookMeta meta;

    public Book(String title) {
        meta = (BookMeta) book.getItemMeta();

        meta.setTitle(ChatColor.translateAlternateColorCodes('&', title));
        meta.setAuthor(ChatColor.translateAlternateColorCodes('&', "The Landlord"));
    }

    public void addPage(String pageContent) {
        meta.addPage(ChatColor.translateAlternateColorCodes('&', pageContent));
    }

    public ItemStack produceAndGetBook() {
        book.setItemMeta(meta);
        return book;
    }
}
