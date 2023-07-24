package com.johansvartdal.landlord;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoNetherPortal implements Listener {

    private JavaPlugin plugin;

    public NoNetherPortal(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
            Tools.tellPlayer(player, LangDict.getString("cheatProtection.cheatProtectionEnterNether"), ChatColor.RED);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        int radius = 1;

        // Cancel the event if there are any nearby obsidian blocks (the block that creates portal frames)
        for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
            for (int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    Block nearbyBlock = loc.getWorld().getBlockAt(x, y, z);
                    if (nearbyBlock.getType().equals(Material.OBSIDIAN)) {
                        // Cancel the event to prevent fire from creating a portal
                        event.setCancelled(true);
                        Tools.tellPlayer(event.getPlayer(), LangDict.getString("cheatProtection.cheatProtectionEnterNether"), ChatColor.RED);
                        return;
                    }
                }
            }
        }
    }
}
