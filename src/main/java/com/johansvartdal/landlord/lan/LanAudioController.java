package com.johansvartdal.landlord.lan;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class LanAudioController {

    private final Clip backgroundMusic;
    private final Clip voice;
    private final boolean audioAvailable;

    public LanAudioController() {
        Clip background = null;
        Clip voiceClip = null;
        boolean available = false;

        try {
            background = AudioSystem.getClip();
            voiceClip = AudioSystem.getClip();
            available = true;
        } catch (LineUnavailableException | IllegalArgumentException e) {
            // Headless machines — Docker containers, most VPSes — have no audio device.
            // LAN audio is an optional party feature, so degrade to silence rather than
            // taking the whole plugin down with us.
            System.out.println("Landlord: no audio output available, LAN audio disabled (" + e.getMessage() + ")");
        }

        this.backgroundMusic = background;
        this.voice = voiceClip;
        this.audioAvailable = available;
    }

    public void playAudioFile(String audioFilePath, AudioLayer layer) {
        if (!audioAvailable) {
            return;
        }

        try {
            //read audio data file
            InputStream audioSrc = getClass().getResourceAsStream("/rawaudio/" + audioFilePath);

            //add buffer for mark/reset support
            InputStream bufferedIn = new BufferedInputStream(audioSrc);

            //create stream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = null;

            switch (layer) {
                case BACKGROUND -> clip = backgroundMusic;
                case VOICE -> clip = voice;
            }

            clip.close();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing audio: " + e.getMessage());
        }
    }

    public void stopAudio() {

    }
}
