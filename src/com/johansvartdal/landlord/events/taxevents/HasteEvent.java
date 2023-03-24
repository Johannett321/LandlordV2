package com.johansvartdal.landlord.events.taxevents;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HasteEvent extends LandlordEvent {

    int durationSeconds = 10;

    public HasteEvent(Main plugin) {
        super(plugin);
    }

    @Override
    public void startEvent() {
        Tools.broadcastMessage("Haste!");
        for (Player player : Bukkit.getOnlinePlayers()) {
            givePlayerHaste(player);
        }
    }

    @Override
    public String getEventType() {
        return "TaxEvent";
    }

    @Override
    public void resumeEvent() {
        Tools.broadcastMessage("The haste effect ended, as the server was restarted");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        }
    }

    private void givePlayerHaste(Player player) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.FAST_DIGGING, (int) Tools.secToTicks(durationSeconds), 1);
        player.addPotionEffect(potionEffect);
    }
}
