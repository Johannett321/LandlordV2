package com.johansvartdal.landlord;

import org.bukkit.Bukkit;

public class CustomConfig {

    public static void load() {
        if (!Tools.fileExists("landlord.config")) {
            createConfigFile();
        }
        String configContent = Tools.read("landlord.config");
        String[] props = configContent.split(System.lineSeparator());
        for (String prop : props) {
            String[] propInfo = prop.split("=");

            // shutdown if wrong config
            if (propInfo.length != 2) {
                System.out.println("Length: " + propInfo.length);
                shutdownServer();
            }

            // run prop
            runProp(propInfo[0], propInfo[1]);
        }
    }

    private static void runProp(String key, String value) {
        switch (key) {
            case "LICENCE_KEY":
                // Legacy key from when Landlord was a paid plugin. Landlord is now free and
                // open source, so the value is ignored. Accepted so existing landlord.config
                // files keep working instead of tripping the unknown-key shutdown below.
                break;
            case "LANGUAGE":
                LangDict.languageCode = value;
                break;
            default:
                shutdownServer();
                break;
        }
    }

    private static void createConfigFile() {
        StringBuilder builder = new StringBuilder();
        builder.append("LANGUAGE=en");
        Tools.write("landlord.config", builder.toString());
    }

    private static void shutdownServer() {
        System.out.println("##########################################################");
        System.out.println("SERVER SHUTDOWN BECAUSE OF INVALID LANDLORD CONFIG FILE!!");
        System.out.println("##########################################################");
        Bukkit.getServer().shutdown();
    }
}
