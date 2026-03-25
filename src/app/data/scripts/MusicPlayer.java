package app.data.scripts;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.util.Duration;

public class MusicPlayer {
    public static MediaPlayer mediaPlayer;

    public static void play() {
        if (mediaPlayer != null) return;
        Media media = new Media(MusicPlayer.class.getResource("/app/data/music/HollowKnightOST-Hornet.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setStartTime(Duration.seconds(3.5));
        mediaPlayer.play();
    }
}
