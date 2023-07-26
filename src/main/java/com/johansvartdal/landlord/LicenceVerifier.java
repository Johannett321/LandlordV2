package com.johansvartdal.landlord;

import org.bukkit.Bukkit;

public class LicenceVerifier {
    public static String licenceKey;

    public static void verifyLicence() {
        System.out.println("Landlord: Licence verified");

        // if licence is not valid
        if (false) {
            System.out.println("##########################################################");
            System.out.println("SEERVER SHUTDOWN BECAUSE OF INVALID LANDLORD LICENCE.");
            System.out.println("You can purchase a licence at https://www.landlord.eu");
            System.out.println("##########################################################");
            Bukkit.getServer().shutdown();
        }
    }
}
