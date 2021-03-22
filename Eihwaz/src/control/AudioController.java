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
	
	public static synchronized void play(int audioClipIndex, int loopCount, int startTime, int endTime, String audioDevice) {
		if(audioPlaying.get(audioClipIndex) == true) return;
		// TODO Auto-generated method stub
		//audioClips.get(audioClipIndex).setMicrosecondPosition((long)startTime*1000000);
		//loopCount++;
		Orient2.println("||" + audioClipNames.get(audioClipIndex));
		//if(audioClipNames.get(audioClipIndex).contains("ALREADYRUNNING")) return;
		//else audioClips.get(audioClipIndex).setStatus(MediaPlayer.Status.PLAYING); 
		//audioClips.get(audioClipIndex).getBufferProgressTime();
		audioClips.get(audioClipIndex).seek(new Duration(startTime));//.setLoopPoints(startTime*1000000, endTime*1000000);
		if(endTime != 0) audioClips.get(audioClipIndex).setStopTime(new Duration(endTime));
		else endTime = (int) audioClips.get(audioClipIndex).getTotalDuration().toMillis();
		audioClips.get(audioClipIndex).setCycleCount(loopCount);//.loop(loopCount);

        Orient2.println("Starting audioClipIndex " + audioClipIndex + " of duration " + (endTime-startTime));
		
        //audioClipNames.set(audioClipIndex, audioClipNames.get(audioClipIndex) + "ALREADYRUNNING");
		//Orient2.println("||" + audioClipNames.get(audioClipIndex));
		audioPlaying.set(audioClipIndex, true);
		audioClips.get(audioClipIndex).seek(new Duration(0));
		audioClips.get(audioClipIndex).play();//.start();
		try {
			Thread.sleep(endTime-startTime);
	        //audioClipNames.set(audioClipIndex, audioClipNames.get(audioClipIndex).substring(0, "ALREADYRUNNING".length()));
			//Orient2.println("||" + audioClipNames.get(audioClipIndex));
			audioPlaying.set(audioClipIndex, false);
			Thread.currentThread().interrupt();
		} catch (InterruptedException e) {
	        //audioClipNames.set(audioClipIndex, audioClipNames.get(audioClipIndex).substring(0, "ALREADYRUNNING".length()));
			//audioClips.get(audioClipIndex).seek(new Duration(0));
			audioPlaying.set(audioClipIndex, false);
			Thread.currentThread().interrupt();
		}
	}

}
