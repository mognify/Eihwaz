package blessings;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;

import org.opencv.core.Point;

import control.ControlEngine;

public class MouseController {
	// reads instructions from MouseEngine
	//protected static Queue<Thread> q;
	
	protected static void next() {
		//q.remove().start();
	}
	
	public synchronized static void click(int x1, int y1, int x2, int y2, int delayMS) throws AWTException, InterruptedException {
		Robot bot = new Robot(MouseInfo.getPointerInfo().getDevice());
		java.awt.Point startLocation = MouseInfo.getPointerInfo().getLocation();

	    bot.mouseMove(x1, y1);
	    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	    bot.mouseMove(startLocation.x, startLocation.y);
	    return;
	}
	
	public static void idleCheck(int timeLimit, int checkDelayMS, String commandIfIdle) {
		//new Thread(() -> {
			final PointerInfo pointerInfo = MouseInfo.getPointerInfo();
			
				long startTime = System.currentTimeMillis();
				java.awt.Point initialCursorPoint = pointerInfo.getLocation();

				while(true) {
					try {
							Thread.sleep(checkDelayMS);
						
							long timeDifference = System.currentTimeMillis() - startTime;
							if(pointerInfo.getLocation().equals(initialCursorPoint.getLocation()) && (timeDifference > timeLimit)) {
								String cmdargs0 = "java -DOrient -jar \"%USERPROFILE%\\Desktop\\Orient\\Orient" + ControlEngine.version + ".jar\" C \"" +  commandIfIdle + "\"";
						 		Runtime.getRuntime().exec("cmd /c start /low /min cmd.exe /K \"" + cmdargs0 + "\"");
							}else if (timeDifference <= timeLimit){
								continue;
							}else break;
				 	   } catch (IOException | InterruptedException e) {
							break;
				 	   } 
				}

				idleCheck(timeLimit, checkDelayMS, commandIfIdle);
		//}).start();
	}
	
	public synchronized static void niftyClick(int x1, int y1, int x2, int y2, int delayMS) throws AWTException, InterruptedException {
		Robot bot = new Robot(MouseInfo.getPointerInfo().getDevice());
		java.awt.Point startLocation = MouseInfo.getPointerInfo().getLocation();

	    bot.mouseMove(x1, y1);
		Thread.sleep(delayMS);
	    bot.mouseMove(x1+1, y1+1);
		Thread.sleep(delayMS);
	    bot.mouseMove(x1-2, y1-2);
		Thread.sleep(delayMS);
	    bot.mouseMove(x1, y1);
		Thread.sleep(delayMS);
	    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	    bot.mouseMove(startLocation.x, startLocation.y);
	    return;
	}
	
	public synchronized static void drag(int x1, int y1, int x2, int y2, int delayMS) throws AWTException, InterruptedException {
		Robot bot = new Robot(MouseInfo.getPointerInfo().getDevice());
		java.awt.Point startLocation = MouseInfo.getPointerInfo().getLocation();
		
	    bot.mouseMove(x1, y1);    
		Thread.sleep(delayMS);
	    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	    bot.mouseMove(x2, y2);    
	    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		Thread.sleep(delayMS);
	    bot.mouseMove(startLocation.x, startLocation.y);
	    return;
	}

	public static void rightClickAt(String substring) throws HeadlessException, AWTException {
		//q.add(new Thread() {
		//public void run() {
			Robot bot = new Robot(MouseInfo.getPointerInfo().getDevice());
			java.awt.Point startLocation = MouseInfo.getPointerInfo().getLocation();
			int x = Integer.valueOf(substring.replace("-", " ").split(" ")[0]);
			int y = Integer.valueOf(substring.replace("-", " ").split(" ")[1]);
			int delayMS = Integer.valueOf(substring.replace("-", " ").split(" ")[2]);
			
			try {
				bot = new Robot();
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    bot.mouseMove(x, y);    
		    bot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
		    try {
				Thread.sleep(delayMS);
			} catch (InterruptedException e) {
			    bot.mouseMove(x, y);    
			    bot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
			    bot.mouseMove(startLocation.x, startLocation.y);
			}
		    bot.mouseMove(x, y);    
		    bot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
		    bot.mouseMove(startLocation.x, startLocation.y);
		//}
	//});
	return;
	}
	public static void rightClick() throws HeadlessException, AWTException {
		Robot bot = new Robot(MouseInfo.getPointerInfo().getDevice());

	    bot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
	    bot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
	}

	public static void click(Point p) throws AWTException, InterruptedException {
		click((int)p.x, (int)p.y, (int)p.x, (int)p.y, 3);
	}

	public static void leftClick() throws HeadlessException, AWTException {
		Robot bot = new Robot(MouseInfo.getPointerInfo().getDevice());

	    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
}
