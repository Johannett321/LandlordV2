package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlyingEvent extends PlayerEvent {

    public FlyingEvent(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void start() {
        Tools.tellPlayer(player, LangDict.getString("whoahFlying"), ChatColor.GREEN);
        player.setAllowFlight(true);
        player.setFlying(true);

        // tell how to stop
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Tools.tellPlayer(player, LangDict.getString("turnOffFlightInstructions"), ChatColor.YELLOW);
        }, Tools.secToTicks(5));

        scheduleAutoEnd();
    }

    @Override
    public void endEvent() {
        if (player.isFlying()) {
            addSlowFalling();
        }
        player.setFlying(false);
        player.setAllowFlight(false);
        super.endEvent();
    }

    public void addSlowFalling() {
        // add slow falling, so player doesn't hurt
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, (int) Tools.secToTicks(20), 6);
        player.addPotionEffect(potionEffect);
    }

    @Override
    public int getLengthOfEventInSeconds() {
        return 60;
    }

    @Override
    public boolean playerTPAwayAllowed() {
        return false;
    }

    @Override
    public void onWarningEventShouldCancel() {
        attemptReloadEvent();
    }

    private void attemptReloadEvent() {
        if (!Bank.playerCanAfford(player, getExtensionPrice())) {
            Tools.tellPlayer(player, LangDict.getString("cannotAffordExtendFlight"), ChatColor.RED);
            endEvent();
            return;
        }
        Bank.withdrawPlayer(LangDict.getString("extendingFlight"), player, getExtensionPrice());
        scheduleAutoEnd();
    }

    @Override
    public int getExtensionPrice() {
        return StaticValues.FLYING_PRICE_PER_MINUTE;
    }

    @Override
    public String getTitle() {
        return LangDict.getString("flying");
    }
}
