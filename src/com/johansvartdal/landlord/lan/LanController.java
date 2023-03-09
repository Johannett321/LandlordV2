package com.johansvartdal.landlord.lan;

public class LanController {

    private static LanAudioController lanMusicController;

    public static void initiate() {
        lanMusicController = new LanAudioController();
    }

    public static LanAudioController getLanMusicController() {
        return lanMusicController;
    }
}
