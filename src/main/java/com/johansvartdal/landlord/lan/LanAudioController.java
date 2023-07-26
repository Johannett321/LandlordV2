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

    public LanAudioController() {
        try {
            backgroundMusic = AudioSystem.getClip();
            voice = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public void playAudioFile(String audioFilePath, AudioLayer layer) {
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
