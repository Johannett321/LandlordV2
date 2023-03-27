package com.johansvartdal.landlord.events.taxevents;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HasteEvent extends LandlordEvent {

    int durationSeconds = 60*3;

    public HasteEvent(Main plugin) {
        super(plugin);
    }

    @Override
    public void startEvent() {
        God.speak(LangDict.getString("hasteInOneMinute"));
        Tools.performTaskAfterCountdown(this::applyEffect, "Haste will begin in", 60);
    }

    @Override
    public void endEvent(Boolean cancelled) {
        super.endEvent(cancelled);
        God.speak(LangDict.getString("lotOfProgress"));
    }

    private void applyEffect() {
        Tools.playSoundForEveryone(Sound.BLOCK_BEACON_ACTIVATE);
        God.speak(LangDict.getString("hasteBegin"));
        for (Player player : Bukkit.getOnlinePlayers()) {
            givePlayerHaste(player);
        }
        scheduleEndEvent();
    }

    private void scheduleEndEvent() {
        Tools.performTaskAfterCountdown(() -> {
            endEvent(false);
        }, LangDict.getString("effectWilEndIn"), durationSeconds);
    }

    @Override
    public String getEventType() {
        return "TaxEvent";
    }

    @Override
    public void resumeEvent() {
        Tools.broadcastMessage(LangDict.getString(LangDict.EVENT_CANCELLED_SERVER_RESTART));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        }
    }

    private void givePlayerHaste(Player player) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.FAST_DIGGING, (int) Tools.secToTicks(durationSeconds), 1);
        player.addPotionEffect(potionEffect);
    }
}
