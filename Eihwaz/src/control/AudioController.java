package control;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import orient.Orient2;

public class AudioController {
	public static volatile ArrayList<MediaPlayer> audioClips = new ArrayList<MediaPlayer>();
	public static volatile ArrayList<String> audioClipNames = new ArrayList<String>();
	public static volatile ArrayList<Boolean> audioPlaying = new ArrayList<Boolean>();
	
	/*
	 * via native Java MediaPlayer API; plays to default audio output device
	 */
	public static synchronized void play(int audioClipIndex, int loopCount, int startTime, int endTime, String audioDevice) {
		if(audioDevice.length() > 3) { play(audioClipIndex, loopCount, startTime, endTime); return; }
		if(audioPlaying.get(audioClipIndex) == true) return;
		Orient2.println("||" + audioClipNames.get(audioClipIndex));

		audioClips.get(audioClipIndex).seek(new Duration(startTime));//.setLoopPoints(startTime*1000000, endTime*1000000);
		if(endTime != 0) audioClips.get(audioClipIndex).setStopTime(new Duration(endTime));
		else endTime = (int) audioClips.get(audioClipIndex).getTotalDuration().toMillis();
		audioClips.get(audioClipIndex).setCycleCount(loopCount);//.loop(loopCount);

        Orient2.println("Starting audioClipIndex " + audioClipIndex + " of duration " + (endTime-startTime));
		
		audioPlaying.set(audioClipIndex, true);
		audioClips.get(audioClipIndex).seek(new Duration(0));
		
		audioClips.get(audioClipIndex).play();//.start();
		try {
			Thread.sleep(endTime-startTime);
			audioPlaying.set(audioClipIndex, false);
			Thread.currentThread().interrupt();
		} catch (InterruptedException e) {
			audioPlaying.set(audioClipIndex, false);
			Thread.currentThread().interrupt();
		}
	}

	/*
	 * via VLC, to allow for selecting the audio output device
	 * */
	public static synchronized void play(int audioClipIndex, int loopCount, int startTime, int endTime) {
		
	}
}
