package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class SpecialEffects {
    public static Plugin plugin;

    public static void setPlugin(Plugin plugin) {
        SpecialEffects.plugin = plugin;
    }

    public static void blastFireworks(int howMany) {
        Location initial = Main.tradeCenter.getLocation();
        initial.setX((initial.getChunk().getX()*16)+8);
        initial.setZ((initial.getChunk().getZ()*16)+8);
        initial.setY(Tools.highestStandingPoint(initial).getY() + 10);

        blastFireworks(initial, howMany);
    }

    public static void blastFireworks(Location where, int howMany) {
        blastNextFireworks(where, howMany);
    }

    private static void blastNextFireworks(Location where, int howManyLeft) {
        blastOneFireWork(where, getRandomColor());

        if (howManyLeft != 0) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    blastNextFireworks(where, howManyLeft-1);
                }
            }, 20*1);
        }
    }

    public static void blastOneFireWork(Location where, Color fireworkColor) {
        Firework fw = (Firework) where.getWorld().spawnEntity(where, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();



        FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(fireworkColor).with(FireworkEffect.Type.BALL_LARGE).trail(true).build();
        fwm.addEffect(effect);

        fwm.setPower(2);
        fw.setFireworkMeta(fwm);
    }

    private static Color getRandomColor() {
        Color fireworkColor;

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        switch (randomNumber) {
            case 0: {
                fireworkColor = Color.GREEN;
                break;
            }
            case 1: {
                fireworkColor = Color.RED;
                break;
            }
            case 2: {
                fireworkColor = Color.BLUE;
                break;
            }
            case 3: {
                fireworkColor = Color.PURPLE;
                break;
            } default:
                fireworkColor = Color.ORANGE;
                break;
        }
        return fireworkColor;
    }
}
