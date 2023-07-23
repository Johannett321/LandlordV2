package com.johansvartdal.landlord;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EmissionTax implements Listener {

    private final Main plugin;

    public EmissionTax(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (!Main.properties.gameStateIsNormal()) {
            return;
        }
        if (event.getBlock().getType() == Material.COAL_ORE) {
            Player player = event.getPlayer();

            if (!Bank.playerCanAffordTaxFree(player, StaticValues.EMISSION_TAX)) {
                event.setCancelled(true);
                JailManager.sendToJail(plugin, player, LangDict.getString("jailReasonEmissionTax"), LangDict.getString("jailOutReasonEmissionTax"), 60*2);
                return;
            }

            // actually withdraw
            Bank.withdrawPlayerWithoutTax(player, StaticValues.EMISSION_TAX);
            Tools.tellPlayer(player, LangDict.getString("youJustPaid") + StaticValues.EMISSION_TAX + LangDict.getString(LangDict.CURRENCY) + LangDict.getString("inEmissionTax"), ChatColor.GRAY);
        }
    }
}
