package com.johansvartdal.landlord;

import java.io.File;

public class Configurator {

    public Main plugin;

    public Configurator(Main plugin) {
        this.plugin = plugin;
    }

    public boolean alreadyConfigured() {
        return false;
    }

    public void configure() {
        if (alreadyConfigured()) {
            return;
        }

        System.out.println("Setting up Landlord files...");
        File pluginDir = new File(plugin.getDataFolder() + "/");
        if(!pluginDir.exists())
            pluginDir.mkdir();

        File playersDir = new File(plugin.getDataFolder() + "/" + "players");
        if(!playersDir.exists())
            playersDir.mkdir();

    }
}
