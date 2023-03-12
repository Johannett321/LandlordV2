package com.johansvartdal.landlord.lan;

public class LanController {

    private static LanAudioController lanMusicController;
    private static LanLightsController lanLightsController;

    public static void initiate() {
        lanMusicController = new LanAudioController();
        lanLightsController = new LanLightsController();
    }

    public static LanAudioController getLanMusicController() {
        return lanMusicController;
    }

    public static LanLightsController getLightsController() {
        return lanLightsController;
    }
}
