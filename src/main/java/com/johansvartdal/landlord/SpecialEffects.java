package com.johansvartdal.landlord;

import org.bukkit.*;
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

    public static void playChunkUnlockAnim(Chunk chunk, int playerHeight) {
        int spell = 4;
        int endRod = 5;
        int smokeNormal = 10;
        int startX = chunk.getX()*16+1;
        int startZ = chunk.getZ()*16+1;

        for (int x = startX; x < startX+16; x++) {
            if (!(x % endRod == 0 || x % smokeNormal == 0 || x % spell == 0)) {
                continue;
            }
            for (int y = playerHeight-50; y < playerHeight+50; y++) {
                if (!(y % endRod == 0 || y % smokeNormal == 0 || y % spell == 0)) {
                    continue;
                }
                for (int z = startZ; z < startZ+16; z++) {
                    if (!(z % endRod == 0 || z % smokeNormal == 0 || z % spell == 0)) {
                        continue;
                    }

                    Location location = chunk.getWorld().getBlockAt(x, y,z).getLocation();

                    if (z % smokeNormal == 0 && x % smokeNormal == 0) {
                        location.getWorld().spawnParticle(Particle.SMOKE_NORMAL,location,20, 0.1F, 1F, 1F);
                    }

                    if (z % endRod == 0 && x % endRod == 0) {
                        location.getWorld().spawnParticle(Particle.END_ROD,location,10, 0.1F, 0.1F, 0.1F);
                    }

                    if (z % spell == 0 && x % spell == 0) {
                        location.getWorld().spawnParticle(Particle.SPELL_WITCH,location,20, 0.1F, 1F, 1F);
                    }


                    //location.getWorld().spawnParticle(Particle.ASH,location,20, 0.1F, 1F, 1F);
                }
            }
        }
    }

    public static void blastFireworks(int howMany) {
        Location tradeLocation = Main.tradeCenter.getLocation();

        Location initial = new Location(tradeLocation.getWorld(), tradeLocation.getX(), tradeLocation.getY(), tradeLocation.getZ());
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
            case 0:
                fireworkColor = Color.GREEN;
                break;
            case 1:
                fireworkColor = Color.RED;
                break;
            case 2:
                fireworkColor = Color.BLUE;
                break;
            case 3:
                fireworkColor = Color.PURPLE;
                break;
            default:
                fireworkColor = Color.ORANGE;
                break;
        }
        return fireworkColor;
    }
}
