package control;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.opencv.core.Point;

import blessings.ImageAccess;
import blessings.KeyboardController;
import blessings.MouseController;
import control.StringAlignUtils.Alignment;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.Clipboard;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;
import orient.Orient2;

public class ControlEngine implements KeyListener{
public static final String version = "3multiMatch";
// reads the text file and puts instructions into an arraylist
 public ArrayList<String> instructions;
 private ArrayList<String> varList = new ArrayList<String>();
 public ArrayList<Object> copyList = new ArrayList<Object>();
 public ArrayList<String> fileNames = new ArrayList<String>();
 
 public String workingDirectory;
private static Pattern pNumeric = Pattern.compile("-?\\d+(\\.\\d+)?");;

public static Robot r;
protected static volatile ClipboardOwner co = new KeyboardController();
public boolean[] copy = new boolean[2];
public static int threadLimit = 3;
private Scanner ui = new Scanner(System.in);

public volatile String currentInstruction;
public volatile int globalWait = 369;
public static volatile int threadCount = 0;

 public ControlEngine() throws FileNotFoundException {
  this.workingDirectory = Orient2.workingDirectory;
  instructions = new ArrayList < String > ();
 }

 public ControlEngine(char c, String s) { // this is insanely deprecated.. it runs the "c" command from Orient1, which is now for the c key
  instructions = new ArrayList < String > ();
  String[] instring = s.replace("-", " ").split(" ");
  int i = instring.length;
  for (; (((i > 0))); i--) {
   instructions.add(c + " " + instring[i]);
   Orient2.println("+> [c " + instring[i] + "] ");
  }
 }
 
 public ControlEngine(boolean command, String commandWithArgs) throws FileNotFoundException {
	 this();
	 instructions.add(commandWithArgs);
 }

 public ControlEngine(String fileName) throws FileNotFoundException {
	 this();
	 System.out.println("ControlEngine loaded with (workingDirectory " + workingDirectory + ", fileName:" + fileName + ")");
 

  Scanner fi = new Scanner(new File(workingDirectory + "\\Orient\\" + fileName + ".txt"));
  while (((fi.hasNext()))) {
   String line = fi.nextLine();
   instructions.add(line);
   Orient2.println("+" + line);
  }
  fi.close();
 }

 public void run() throws NumberFormatException, InterruptedException, AWTException, UnsupportedFlavorException, IOException { // B
		//ImageAccess.loadLibraries();
	 Orient2.println("run()");
		boolean waitMatch = true;
		GlobalMouseHook mouseHook = new GlobalMouseHook();
		GlobalKeyboardHook kbHook = new GlobalKeyboardHook();
		KeyboardController kc = new KeyboardController();
		r = new Robot();
		globalWait = 1;
		int currentInstructionIndex = 0;
		ArrayList<String> instructions = this.instructions;

		Orient2.println("Going through [audio] check");
		new JFXPanel();
		int clipIndex = 0;
		for(String instr : instructions) {
			if(instr.contains("audio")) {
			String[] instrArr = instr.replace("-", " ").replace(":", " ").split(" ");
			for(int lineSpaceIndex = 0; lineSpaceIndex < instrArr.length; lineSpaceIndex++) {
				if(instrArr[lineSpaceIndex].contains("audio")){
		    		Orient2.println("Grabbing audio clip for " + instrArr[lineSpaceIndex+1]);
		    		String fileName = String.valueOf(instrArr[lineSpaceIndex+1]);
		    		if(!(fileName.contains("."))) fileName+=".mp3";
		    		String filePath = workingDirectory + "\\Orient\\" + fileName;
		    		
		    		Media hit = new Media(new File(filePath).toURI().toString());
		    		AudioController.audioClips.add(new MediaPlayer(hit));
		    		AudioController.audioClipNames.add(fileName);
		    		AudioController.audioPlaying.add(false);
		    		/*
				    try {
				    		AudioController.audioClipNames.add(instrArr[1]);
				    		Orient2.print(filePath);
					        /*InputStream is = new java.io.FileInputStream(filePath);
					        InputStream bufferedIn = new BufferedInputStream(is);
					        AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);*
				    		AudioInputStream ais = AudioSystem.getAudioInputStream(new File(filePath));
					        AudioFormat format = ais.getFormat();
					        // this is the value of format.
					        // PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian
					        DataLine.Info info = new DataLine.Info(Clip.class, format);
					        AudioController.audioClips.add((Clip)AudioSystem.getLine(info));
					        AudioController.audioClips.get(clipIndex++).open(ais);
					        Orient2.println("\t" + fileName + " successfully gathered.");
					    } catch (Exception e) {
					        Orient2.println("\t" + fileName + " failed to gather. Oh well");
					        e.printStackTrace();
					    	continue;
					    }
					    */
			        Orient2.println("\t" + fileName + " successfully gathered.");
					}
				else {
					Orient2.println(instrArr[1] + " does not contain [audio]");
				}}
			}
		}
		
  for (String instr : instructions) {
	  currentInstruction = instr;
	  Orient2.println("Running the following command: " + instr);
	  String cmd = instr.split(" ")[0];
	  int cmdlen = Integer.valueOf((cmd.length()));
	  String cmdargs = instr.substring(cmdlen);
	  int instrlen = Integer.valueOf(instr.length());
	  int spaces = Integer.valueOf(instr.split(" ").length);
	  String[] instrArr = instr.substring(cmdlen).split(" ");
	  boolean options = (instrlen>(cmdlen+1));
	  String regex = "";
	  String repstr = "1";
	  int i = 0;
	  int i2 = 0;
	  int i3 = 0;
	  try {
		  Orient2.println("Options specified? " + options);
		  //String temp = instr.substring(beginIndex, endIndex)
          if((++cmdlen)<instrlen)
        	  if(pNumeric.matcher(instr.substring(cmdlen--)).matches())
        		  repstr = instr.substring(cmdlen).replace(" ","");
	  }catch(NumberFormatException e) {
		  Orient2.println("No repetitions: one-shot");
		  repstr = "1";
	  }
	  int repeat = 1;
	  if(repstr.length() > 0)
		  repeat = (instrlen<++cmdlen)?1:(Integer.valueOf(repstr));
	  Orient2.println("(cmdlen: " +  cmdlen + ")");
   /* w intSeconds (wait)
    * d intX intY intX1 intY1 intDelaySeconds (drag)
    * kp Hello, world!
    * kd 
    * intX intY intDelaySeconds (click)
    */
   switch (instr.split(" ")[0]) {
   default: // assume 
	   break;
   case "setThreadLimit":
	   threadLimit = Integer.valueOf(instrArr[0]);
	   break;
   case "cbMathType":
	   KeyboardController.cbMathType(instr);
	   break;
   case "menu":
	   //Orient2.alwaysAppendMessage = true;
	   menu();
	   break;
   case "dupVar":
	   i = Integer.valueOf(instr.split(" ")[1]);
	   i2 = Integer.valueOf(instr.split(" ")[2]);
	   varList.set(i2, varList.get(i));
	   break;
   case "copyVar":
	   i = Integer.valueOf(instr.split(" ")[1]);
	   KeyboardController.setClipboard(varList.get(i), co);
	   
	   break;
   case "setVar":
	   i = Integer.valueOf(instr.split(" ")[1]);
	   varList.add(i, instr.substring(cmdlen + instr.split(" ")[1].length()));
	   break;
   case "splitVar":
	   i = Integer.valueOf(instr.split(" ")[1]);
	   regex = instr.split(" ")[2];
	   i2 = Integer.valueOf(instr.split(" ")[3]);
	   varList.set(i, varList.get(i).split(regex)[i2]);
	   
	   break;
   case "ifVarContains":
	   i = Integer.valueOf(instr.split(" ")[1]);
	   regex = instr.split(" ")[2];
	   String action = instr.split(" ")[3];
	   String cmdOption = instr.substring(cmdlen + regex.length() + action.length() + 4);
	   Orient2.println("(cmdOption: " + cmdOption + ")");
	   // first check if var(i) contains regex - done
	   // then need to create a temporary instruction list - done
	   // then move the remaining instructions into that list - done
	   // then set the actual instruction list to the temp instruction list - done
	   // finally call the run() command from this current control engine instance - done
	   Orient2.println("Checking \"" + varList.get(i) + "\" for regex: " + regex);
	   if(varList.get(i).contains(regex)) {
		   Orient2.println("Regex detected in " + varList.get(i));
		   
		   List<String> temp = new ArrayList<String>();
		   temp.add(action + " " + cmdOption);
		   for(i = 1+currentInstructionIndex; i < this.instructions.size(); i++) {
			   String x = this.instructions.get(i);
			   temp.add(x);
			   Orient2.println("temp instructions: " + x + " - added");
		   }

		   //instructions = temp;
		   
		   run();
	   }
	   Orient2.println("\"" + regex + "\" not found in " + varList.get(i));
	   
	   break;
   case "orientVar":
	   i = Integer.valueOf(instr.split(" ")[1]);
	     new ControlEngine(varList.get(i)).run();
	   break;
   case "combineVarCP":
	   String combination = "";
	   Orient2.println("Running combineVarCP...");
	   for(i = 0; i < spaces; i++) {
		   combination += combination + " " + varList.get(Integer.valueOf(instr.split(" ")[i]));
	   }
	   Orient2.println("combination: " + combination);
	   KeyboardController.setClipboard(combination, co);
	   Orient2.println("Clipboard contents set to: " + combination);
	   break;
   case "cropVar":
	   //i3 = i3>=0?i3:i3*-1;
	   i = Integer.valueOf(instr.split(" ")[1]); // varList index
	   i2 = Integer.valueOf(instr.split(" ")[2]); // start index
	   Orient2.println("Cropping " + varList.get(i));
	   if(cmdlen <= 10) {
		   Orient2.println("Detected only start index: " + i2);
		   varList.set(i, varList.get(i).substring(i2));
	   }
	   else{
		   Orient2.println("Detected end index: " + i3);
		   i3 = Integer.valueOf(instr.split(" ")[3]); // end index
		   varList.set(i, varList.get(i).substring(i2, i3));
	   }
	   break;
   case "regexVar": 
	   i = Integer.valueOf(instr.split(" ")[1]);
	   regex = instr.split(" ")[2];
	   String replaceWith = instr.split(" ")[3];
	   Orient2.println("Running regexVar on " + varList.get(i));
	   varList.set(i, varList.get(i).replace(regex, replaceWith));
	   Orient2.println("regexVar completed: " + varList.get(i));
	   
	   KeyboardController.clipboardRegex(instr.split(" ")[1], instr.split(" ")[2]);
	   break;
   case "varFile":
	   String filename = instr.split(" ")[1];
	   Scanner fi = new Scanner(new File(workingDirectory + "\\Orient\\" + filename + ".txt"));
	   i = 0;
	   while(fi.hasNext()) {
		   varList.add(fi.nextLine());
		   Orient2.println("+VARIABLE " + i +  ": " + varList.get(i++));
	   }
	   break;
   /*case "typePasteRegex":
	   String regex = instr.split(" ")[1];
	   String 
	   break;*/ // just use regcp first, then typepaste or varcp->typeVar :)
   case "typeVar": // type the contents of a variable
	   i = Integer.valueOf(instr.split(" ")[1]);
	   KeyboardController.paste(varList.get(i));
	   break;
   case "varCP":
	   // var x = regcp
	   i = Integer.valueOf(instr.split(" ")[1]);
	   varList.set(i, KeyboardController.getClipboardContents());
	   break;
   case "regCP": // perform regex on clipboard contents
	   KeyboardController.setClipboard(KeyboardController.clipboardRegex(instr.split(" ")[1], instr.split(" ")[2]), co);
	   break;
   case "setGlobalWait":
	   globalWait = repeat;
	   break;
    case "wait": // wait INTtimemilliseconds OPTIONALcommandwithargstorunafterwards
     wait(Integer.valueOf(instr.replace(" ", "-").split("-")[1]));
     new ControlEngine(true, instr.substring(cmdlen + instrArr[0].length())).run();
     break;
    case "cantMatch": // cantMatch aqattack .72 33333 then orient aqrefresh else
    	String imageName = instrArr[0];
    	double threshold = Double.valueOf(instrArr[1]);
    	int delay = Integer.valueOf(instrArr[2]);
    	
    	final int elseIndex =  instr.indexOf("else");
    	String commands = instr.substring(instr.indexOf("then")+5, elseIndex);
    	String elseCommands = instr.substring(elseIndex+5);
    	
    	Orient2.print("If cant match " + imageName + " at " + threshold + " for " + delay + "s then [" + commands + "] else [" + elseCommands + "]");
    	while(true) {
    		boolean matchFound = false;
    		
    		
    		
    		if(matchFound) break;
    	}
    	break;
    case "typeTab":
    	KeyboardController.paste(instr.substring(cmdlen + 1 + instrArr[0].length()));
    	for(; repeat > 0; repeat--) {
    		Orient2.println("Repetition: " + repeat);
    		KeyboardController.tab();
    	}
    	break;
    case "shiftTab": // tab
    	KeyboardController.shiftDown();
    	for(; repeat > 0; repeat--) {  
    	KeyboardController.tab();
    	  }
  	  KeyboardController.shiftUp();
     break;
    case "shiftDown":
    	for(; repeat > 0; repeat--)  KeyboardController.shiftDown();
    	break;
    case "tab": // tab
    	for(; repeat > 0; repeat--) KeyboardController.tab();
     break;
    case "orient": // run another script
     new ControlEngine(instr.substring(7)).run();
     break;
    case "inactive":
    	int timeLimit = Integer.valueOf(instrArr[0]);
    	int cycleDelayMS = Integer.valueOf(instrArr[1]);
    	MouseController.idleCheck(timeLimit, cycleDelayMS,
    			instr.substring(cmdlen + (instrArr[0] + " " + instrArr[1] + " ").length()));
		break;
    case "audio":
    	Orient2.println(instrArr[1] + "||");
    	String audioClipName = instrArr[1];
    	int loopCount = 0;
    	int startTime = 0;
    	int endTime = 0;
    	String audioDevice = "";
    	if(instrArr.length > 2) {
    		Orient2.println("[audio] options detected\n[audio] [" + instrArr[1] + ". " + instrArr[2] + ", " + instrArr[3] + ", " + instrArr[4] + "]");
    		loopCount = Integer.valueOf(instrArr[1]);
	    	startTime = Integer.valueOf(instrArr[2]);
	    	endTime = Integer.valueOf(instrArr[3]); 
	    	audioDevice = String.valueOf(instrArr[4]);
    	}

		Orient2.println("Searching for audioClipName " + instrArr[1]);
    	for(int audioClipIndex = AudioController.audioClipNames.size()-1; audioClipIndex >= 0; ) {
    		if(AudioController.audioClipNames.get(audioClipIndex).contains(audioClipName)) {
    			Orient2.println(audioClipName + " get!");
    			AudioController.play(audioClipIndex, loopCount, startTime, endTime, audioDevice);
    			break;
    		} else {
    			Orient2.println(AudioController.audioClipNames.get(audioClipIndex) + " is not " + audioClipName);
    			audioClipIndex--;
    		}
    	}
		Orient2.println("Audio command ran.");
    	
    	break;
    case "onClick":
    	
    	break;
    case "mock": // run another script, in a separate instance (multithreading)
    	final String instrCopy = String.valueOf(instr);
    	new Thread(() -> {
			try {
				new ControlEngine(instrCopy.substring(5)).run();
			} catch (NumberFormatException | InterruptedException | AWTException | UnsupportedFlavorException
					| IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}).start();
    	break;
    case "thread":
    	final String command = instr.substring(cmdlen);
       //String cmdargs0 = "java -jar \"%USERPROFILE%\\Desktop\\Orient\\Orient" + ControlEngine.version + ".jar\" C \"" +  command + "\"";
	   //Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"" + cmdRunOrient + selection.substring(0,selection.length()-4) + "\""); 
 	   String cmdargs0 = "java -DOrient -jar \"%USERPROFILE%\\Desktop\\Orient\\Orient" + ControlEngine.version + ".jar\" C \"" +  command + "\"";
 	   Runtime.getRuntime().exec("cmd /c start /low /min cmd.exe /K \"" + cmdargs0 + "\""); 
    	/*new Thread(() -> {
			try {
				new ControlEngine(true, command).run();
			} catch (NumberFormatException | InterruptedException | AWTException | UnsupportedFlavorException
					| IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}).start();*/
    	break;
    case "rightClickAt": // right click
     MouseController.rightClickAt(instr.substring(13));
     break;
    case "rightClick":
	    for(; repeat > 0; repeat--) MouseController.rightClick();
	break;
    case "leftClick":
    	for(; repeat > 0; repeat--) MouseController.leftClick();
    	break;
    case "type": // key press (and release); presses each key of string
    	Orient2.println("Running type command...");
    	KeyboardController.paste(instr.substring(cmdlen));
     break;
    case "typeEnter":
    	KeyboardController.paste(instr.substring(cmdlen));
    	KeyboardController.enter();
    	break;
    case "press": // key press (and release); presses each key of string
    	Orient2.println("Running press command...");
    	KeyboardController.keyStroke(instr.substring(cmdlen));
     break;
    case "a":
    	r.keyPress(KeyEvent.VK_A);
    	Thread.sleep(333);
    	r.keyRelease(KeyEvent.VK_A);
    	break;
    case "c":
    	r.keyPress(KeyEvent.VK_C);
    	Thread.sleep(333);
    	r.keyRelease(KeyEvent.VK_C);
    	break;
    case "v":
    	r.keyPress(KeyEvent.VK_V);
    	Thread.sleep(333);
    	r.keyRelease(KeyEvent.VK_V);
    	break;
    case "waitTil": // wait until specific time
    	
     scheduledWait(instr.substring(8));
     break;
    case "typePaste": // paste text
     KeyboardController.typePaste();
     break;
    case "exit":
     System.exit(0);
     break;
    case "enter":
    	for(; repeat > 0; repeat--)  KeyboardController.enter();
     break;
    case "f":
     KeyboardController.f(instr.substring(2));
     break;
    case "backspace":
    	for(; repeat > 0; repeat--) { KeyboardController.backspace(); Orient2.println(repeat + "/" + instr.substring(cmdlen)); }
     break;
    case "space":
    	for(; repeat > 0; repeat--) KeyboardController.space();
	    break;
    case "delete":
	    for(; repeat > 0; repeat--)  KeyboardController.delete();
    	break;
    case "copy":
    	KeyboardController.copy();
    	break;
    case "ctrlDown":
    	for(; repeat > 0; repeat--)  KeyboardController.ctrlDown();
    	break;
    case "ctrl":
    	KeyboardController.ctrlDown();
    	KeyboardController.ctrlUp();
    	break;
    case "altDown":
    	for(; repeat > 0; repeat--)  KeyboardController.altDown();
    	break;
    case "altUp":
    	for(; repeat > 0; repeat--)  KeyboardController.altUp();
    	break;
    case "ctrlUp":
    	for(; repeat > 0; repeat--)  KeyboardController.ctrlUp();
    	break;
    case "shiftUp":
    	KeyboardController.shiftUp();
    	break;
    case "winKey":
    	KeyboardController.winKey();
    	break;
    case "winDown":
    	KeyboardController.winDown();
    	break;
    case "winUp":
    	KeyboardController.winUp();
    	break;
    /*case "matchClick":
     //WindowController.maximizeWindow(instr.substring(2));
    	ImageAccess.waitMatch(instr.substring(2));
    	waitMatch = true;*/
    case "onWindowName":
    	
    	break;
    case "untilWindowName":
    	
    	break;
    case "onProcess":
    	
    	break;
    case "whileProcess":
    	
    	break;
    case "untilMatch":
    	
    	break;
    case "isoMMC":
    	
    case "isoMatchClick":
    case "rapidMMC":
    	// WinC listener to add new bufferedimage->Mat for manyMatchClick
    	// can have this case bleed into the next
		new Thread(() -> {
			boolean winC = false;
			
			while(winC) {
				
			}
		}).start();
    	// on exit you can save it as a .txt to run with menu... you define the name of the file, and the images used will be auto named after that
    case "manyMatchClick":
    	// EXAMPLE:
    	// manyMatchClick DOUBLEthreshold INTclickcount:imagename INTclickcount:imagename INTclickcount:imagename INTclickcount:imagename
    	ImageAccess.match(instrArr, true);
    	break;
    case "matchClick": // fileName opt:i opt:offsetX opt:offsetY opt:threshold || mouse click on image match
    	Orient2.println("Splitting the string and cropping cmdname");
    	instrArr = instr.substring(cmdlen).split(" ");
    	Orient2.println("String array declared with index 0 of: " + instrArr[0] + ".\nDeclaring threshold default value.");
    	threshold = .81;
    	int[] offset = new int[2];
    	Point p = null;
    	int clickCount = 1;
    	int delayMS = 333;
    	/*if((((instrArr != null))) && (((instrArr.length > 1)))) {
    		i = Integer.valueOf(instr.substring(2).split(" ")[1]);
    		if(((instrArr.length > 2))) {
    			offset = new int[]{Integer.valueOf(instrArr[2]), Integer.valueOf(instrArr[3])};
    			p.set(new double[] {p.x + offset[0], p.y + offset[1]});
        		if(((instrArr.length > 3))) { // threshold
        			int threshold = Integer.valueOf(instrArr[4]);
        		}
    		}
    	}*/
    	Orient2.println("Default threshold value declared.\nDeclaring target image name for template matching...");
    	String target = instr.substring(cmdlen).split(" ")[0];
    	Orient2.println("Target is " + target + "\nEntering the length of array containing post-imageName values into switch as further command options...");
    	int ial = instrArr.length;
    	Orient2.println("Not loading libraries redundantly here.");
		//ImageAccess.loadLibraries();
    	Orient2.println("Entering switch case with " + ial);
    	switch(ial) {
    	case 6:
    		Orient2.println("Detected delay specification");
    		delayMS = Integer.valueOf(instrArr[5]);
	    	case 5:
	    		Orient2.println("Detected threshold specification");
				threshold = Double.valueOf(instrArr[4]);
				Orient2.println("Declaring user-specific threshold as " + threshold*100 + "%\nEntering waitMatch loop");
				/*waitMatch = true;
	    		while(waitMatch) {
	    			Orient2.println("\nDone, running template matching to get the centerpoint for clicking...");
	    			p = ImageAccess.match(target);
		        	if(p != null) {
		        		waitMatch = false;
		        		Orient2.println("Match found, centerpoint is [" + p.x + ", " + p.y + "]");
			        	p = ImageAccess.getCenterTM(p, target);
		        	}
	    		}*/
	    	case 4:
	    		Orient2.println("Offset detected, parsing...");
				offset = new int[]{Integer.valueOf(instrArr[2]), Integer.valueOf(instrArr[3])};
				Orient2.println("Offset detected to be [" + offset[0] + ", " + offset[1] + "]");
	    	case 2:
	    		Orient2.println("Click count detected to be " + instrArr[1]);
	    		clickCount = Integer.valueOf(instrArr[1]);
	    	case 1:
	    	default:
	    		Orient2.println("Running template matching to declare coordinate point value, with default threshold of 50%...");
	        	p = ImageAccess.getCenterTM(ImageAccess.match(target, threshold), target);
				p.set(new double[] {p.x + offset[0], p.y + offset[1]});
				Orient2.println("Adjusted point set to [" + p.x + ", " + p.y + "]");
	        	break;
    	}
    	for(; (((clickCount > 0))); clickCount--) MouseController.click(p); // this loop is how many times it clicks
    	break;
    case "onCopy": // onCopy int 7 orient newticketTMID
    	// first: are we looking for an int?
    	boolean integer = instrArr[1].contentEquals("int");
    	// next: what length is the value supposed to be?
    	int targetLength = Integer.valueOf(instrArr[2]);
    	// finally: what command will be run?
    	final String cmdTemp = instr.substring(instrArr[0].length() + 1 + instrArr[1].length() + 1 + instrArr[2].length() + 1);
    	
    	kbHook.addKeyListener(new GlobalKeyAdapter() {
    		
			@Override 
			public void keyPressed(GlobalKeyEvent event) {
				System.out.println(event.getVirtualKeyCode());
				if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_C) {
					KeyboardController.cDown = true;
				}
				if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_CONTROL) {
					KeyboardController.controlDown = true;
				}
				/*if(KeyboardController.cDown && KeyboardController.controlDown) {
    				if(copyList.get(copyList.size()-1) == Integer.valueOf(instrArr[2]))
						try {
							new ControlEngine(cmdTemp).run();
						} catch (NumberFormatException | InterruptedException | AWTException
								| UnsupportedFlavorException | IOException e) {
							// TODO Auto-generated catch block
							Orient2.println("onCopy messed up");
						}
				}*/
			}
			
			@Override 
			public void keyReleased(GlobalKeyEvent event) {
				System.out.println(event); 
				if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_C) {
					KeyboardController.cDown = false;
				}
				if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_CONTROL) {
					KeyboardController.controlDown = false;
				}
			}
    	});
    	//for(String s : instrArr) {
    		//for(Character c : s.toCharArray()) {
    			//if(Character.isDigit(c) && integer) { // then this is the length value, and we're looking for the copied to be an integer
    			//}
    		//}
    	//}
    	break;
    case "fac": // find at click
    	/*
    	// image name, command, command args
    	new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
		    	try {
					ImageAccess.clickSearch(instrArr[0], new int[] {arg0.getX(), arg0.getY()});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
    		
    	};
    	*/
    	break;
    case "clickMatch": // clickMatch img thresh cmd
    	instrArr = instr.substring(cmdlen).split(" "); // pulls all the shit
    	imageName = instrArr[0];
    	threshold = Double.valueOf("0." + instrArr[1].replace(".","")); // makes sure threshold comes in as a decimal value for percentage
		ImageAccess.threshold = threshold;
    	cmd = instr.substring(cmdlen + imageName.length() + instrArr[1].length());
    	final String c = cmd;
    	Orient2.println("clickMatch variables declared, moving on...");
    	
    	mouseHook.addMouseListener(new GlobalMouseAdapter() {
			public void mousePressed(GlobalMouseEvent event)  {
				System.out.println(event.getX() + " xD " + event.getY());
				if ((event.getButtons() & GlobalMouseEvent.BUTTON_LEFT) != GlobalMouseEvent.BUTTON_NO) {
					// leftclick pressed
					Point matched = null;
					while(matched==null) {
			    	try {
						matched = ImageAccess.screenSearch(ImageAccess.extensionCheck(imageName), new Point(event.getX(), event.getY()));
				    	if(matched != null) new ControlEngine(c).run();
					} catch (IOException | NumberFormatException | InterruptedException | AWTException | UnsupportedFlavorException e) {
						// TODO Auto-generated catch block
						Orient2.println("xD");
					}
					}
				}
				if((event.getButtons() & GlobalMouseEvent.BUTTON_RIGHT) != GlobalMouseEvent.BUTTON_NO){
					
    	    	}
				if (event.getButton()==GlobalMouseEvent.BUTTON_MIDDLE) {
					//run = false;
				}
			}
			
			public void mouseReleased(GlobalMouseEvent event)  {
				System.out.println(event); 
			}
			
			public void mouseMoved(GlobalMouseEvent event) {
				System.out.println(event); 
			}
			
			public void mouseWheel(GlobalMouseEvent event) {
				System.out.println(event); 
			}
		});
    	break;
    case "match": // fileNameOfImageToMatch matchThreshold command arguments
    	instrArr = instr.substring(cmdlen).split(" "); // pulls all the shit: truncates command name, and focuses on args
    	imageName = instrArr[0];
    	threshold = Double.valueOf("." + instrArr[1].replace(".","")); // makes sure threshold comes in as a decimal value for percentage
		ImageAccess.threshold = threshold;
    	String command1 = instr.substring(cmdlen + 1 + imageName.length() + 1 + instrArr[1].length());
    	ImageAccess.match(imageName);
    	if(instrArr.length > 3) new ControlEngine(true, command1).run();
    	//int[] offset2 = new int[2]; // offset only needed for matchClick
    	//Point p2 = null;
    	
    	//int clickCount2 = 1;
    	/*if((((instrArr != null))) && (((instrArr.length > 1)))) {
    		i = Integer.valueOf(instr.substring(2).split(" ")[1]);
    		if(((instrArr.length > 2))) {
    			offset = new int[]{Integer.valueOf(instrArr[2]), Integer.valueOf(instrArr[3])};
    			p.set(new double[] {p.x + offset[0], p.y + offset[1]});
        		if(((instrArr.length > 3))) { // threshold
        			int threshold = Integer.valueOf(instrArr[4]);
        		}
    		}
    	}*/
    	
    	//String target2 = instr.substring(11).split(" ")[0];
	    //p2 = ImageAccess.getCenterTM(ImageAccess.match(target2), target2);
				//threshold2 = Double.valueOf(instrArr[4]);
	    		//ImageAccess.threshold = threshold2;
	    		//while(waitMatch) {
	    			//p2 = ImageAccess.match(target2);
		        	//if(p2 != null) {
		        		//waitMatch = false;
			        	//p2 = ImageAccess.getCenterTM(p2, target2);
		        	//}
			        	
			        	/*
	    	case 3:
	    	case 2:
				offset2 = new int[]{Integer.valueOf(instrArr2[2]), Integer.valueOf(instrArr2[3])};
				p2.set(new double[] {p2.x + offset2[0], p2.y + offset2[1]});*/
    	//}
    	//for(; (((clickCount2 > 0))); clickCount2--) MouseController.click(p2); // this loop is how many times it clicks
    	break;
    case "setThreshold":
    	String thresholdTemp = "0." + instrArr[1].replace(".","");
    	Orient2.println(thresholdTemp);
    	ImageAccess.threshold = Double.valueOf(thresholdTemp);
    	break;
    case "multiMatch": // fileNameOfImageToMatch matchThreshold command arguments
    	instrArr = instr.substring(cmdlen).split(" "); // pulls all the shit
    	//imageName = instrArr[0];
    	threshold = Double.valueOf(("." + instrArr[0].replace(".",""))); // makes sure threshold comes in as a decimal value for percentage
    	Orient2.println("threshold: " + threshold);
		ImageAccess.threshold = threshold;
    	//String command = instr.substring(cmdlen + imageName.length() + instrArr[1].length());
		threadLimit = Integer.valueOf(instrArr[1]);
		Orient2.println("Thread limit: " + threadLimit);
		
    	// add the image:command items to list, then create a loop of taking a screenshot and running through each item of the list
		//int cmdcount = instr.substring(cmdlen + instrArr[1].length()).split(":").length;
    	String[] imageCommands = instr.substring(cmdlen + 1 + instrArr[0].length() + 1 + instrArr[1].length()).split(" ");
    	/*for(String mmc : commands) { // mmc = multimatch command
    		// image:command
    	}*/
    	ImageAccess.match(imageCommands);
    	
    	//ImageAccess.match(imageName);
    	
    	break;
    case "orientation":
    	
    	break;
    case "cmd":
		   /*ProcessBuilder processBuilder = new ProcessBuilder();
		   processBuilder.command("cmd.exe", "/c", selection);*/
		   //String cmdRunOrient = "java -jar \"%USERPROFILE%\\Desktop\\Orient\\Orient" + ControlEngine.version + ".jar\" c \"%USERPROFILE%\\Desktop\" ";
	         Runtime.getRuntime().exec("cmd /c start /low /min cmd.exe /K \"" + cmdargs + "\""); 
	         break;
    case "isoManyMatchClick":
    	instrArr = instr.substring(cmdlen).split(" "); // pulls all the shit, besides the command name
    	//imageName = instrArr[0];
    	threshold = Double.valueOf(("." + instrArr[0].replace(".",""))); // makes sure threshold comes in as a decimal value for percentage
    	Orient2.println("threshold: " + threshold);
		ImageAccess.threshold = threshold;
    	//String command = instr.substring(cmdlen + imageName.length() + instrArr[1].length());
		threadLimit = Integer.valueOf(instrArr[1]);	
		Orient2.println("Thread limit: " + threadLimit);
		
		ImageAccess.delay = Integer.valueOf(instrArr[2]);
		Orient2.println("Delay: " + ImageAccess.delay);
		
		//int[] iso = new int[] {Integer.valueOf(instrArr[3]), Integer.valueOf(instrArr[4]), Integer.valueOf(instrArr[5]), Integer.valueOf(instrArr[6])};

    	//String[] imageCommands3 = instr.substring(cmdlen + 1 + instrArr[0].length() + 1 + instrArr[1].length() + 1 + instrArr[2].length()).split(" ");
		instr = "LEVIWUZHERE ORWUZHE " + instr.substring(cmdlen + 1 + instrArr[0].length() + 1 + instrArr[1].length() + 1 + instrArr[2].length());
		Orient2.println("Entering manyIsoMatch(String[]) with " + instr);
    	ImageAccess.manyIsoMatch(instr.split(" "));
    	
		break;
    case "isoManyMatch":
    	instrArr = instr.substring(cmdlen).split(" "); // pulls all the shit, besides the command name
    	//imageName = instrArr[0];
    	threshold = Double.valueOf(("." + instrArr[0].replace(".",""))); // makes sure threshold comes in as a decimal value for percentage
    	Orient2.println("threshold: " + threshold);
		ImageAccess.threshold = threshold;
    	//String command = instr.substring(cmdlen + imageName.length() + instrArr[1].length());
		threadLimit = Integer.valueOf(instrArr[1]);	
		Orient2.println("Thread limit: " + threadLimit);
		
		ImageAccess.delay = Integer.valueOf(instrArr[2]);
		Orient2.println("Delay: " + ImageAccess.delay);
		
		//int[] iso = new int[] {Integer.valueOf(instrArr[3]), Integer.valueOf(instrArr[4]), Integer.valueOf(instrArr[5]), Integer.valueOf(instrArr[6])};

    	//String[] imageCommands3 = instr.substring(cmdlen + 1 + instrArr[0].length() + 1 + instrArr[1].length() + 1 + instrArr[2].length()).split(" ");
		instr = "LEVIWUZHERE " + instr.substring(cmdlen + 1 + instrArr[0].length() + 1 + instrArr[1].length() + 1 + instrArr[2].length());
		
    	ImageAccess.manyIsoMatch(instr.split(" "));
    	
		break;
    case "manyIsoMatch": // manyIsoMatchThen DOUBLEthreshold INTthreads INTcycledelay X1:Y1:X2:Y2:image:cmd:cmdargs X1:Y1:X2:Y2:image:cmd:cmdargs X1:Y1:X2:Y2:image:cmd:cmdargs 
    	instrArr = instr.substring(cmdlen).split(" "); // pulls all the shit, besides the command name
    	//imageName = instrArr[0];
    	threshold = Double.valueOf(("." + instrArr[0].replace(".",""))); // makes sure threshold comes in as a decimal value for percentage
    	Orient2.println("threshold: " + threshold);
		ImageAccess.threshold = threshold;
    	//String command = instr.substring(cmdlen + imageName.length() + instrArr[1].length());
		threadLimit = Integer.valueOf(instrArr[1]);	
		Orient2.println("Thread limit: " + threadLimit);
		
		ImageAccess.delay = Integer.valueOf(instrArr[2]);
		Orient2.println("Delay: " + ImageAccess.delay);
		
    	// add the image:command items to list, then create a loop of taking a screenshot and running through each item of the list
		//int cmdcount = instr.substring(cmdlen + instrArr[1].length()).split(":").length;

    	// manyIsoMatchThen .93 3 999
    	// cmdname = cmdlen, threshold = 0, threads = 1, cycledelay = 2
    	String[] imageCommands2 = instr.substring(cmdlen + 1 + instrArr[0].length() + 1 + instrArr[1].length() + 1 + instrArr[2].length()).split(" ");
    	/*for(String mmc : commands) { // mmc = multimatch command
    		// image:command
    	}*/
    	ImageAccess.manyIsoMatch(imageCommands2);
    	
    	//ImageAccess.match(imageName);
    	
    	break;
    case "manyMatch": // fileNameOfImageToMatch matchThreshold command arguments
    	instrArr = instr.substring(cmdlen).split(" "); // pulls all the shit
    	threshold = Double.valueOf(("." + instrArr[0].replace(".",""))); // makes sure threshold comes in as a decimal value for percentage
    	Orient2.println("threshold: " + threshold);
		ImageAccess.threshold = threshold;
    	//String command = instr.substring(cmdlen + imageName.length() + instrArr[1].length());
		threadLimit = Integer.valueOf(instrArr[1]);	
		Orient2.println("Thread limit: " + threadLimit);
		
		ImageAccess.delay = Integer.valueOf(instrArr[2]);
		Orient2.println("Delay: " + ImageAccess.delay);
		
    	// add the image:command items to list, then create a loop of taking a screenshot and running through each item of the list
		//int cmdcount = instr.substring(cmdlen + instrArr[1].length()).split(":").length;
    	String[] imageCommands3 = instr.substring(cmdlen + 1 + instrArr[0].length() + 1 + instrArr[1].length() + 1 + instrArr[2].length()).split(" ");
    	/*for(String mmc : commands) { // mmc = multimatch command
    		// image:command
    	}*/
    	ImageAccess.match(imageCommands3, false);
    	
    	//ImageAccess.match(imageName);
    	
    	break;
    case "down": // down
    	for(; repeat > 0; repeat--)   KeyboardController.pressDownKey();
    	break;
    case "up": // up
    	for(; repeat > 0; repeat--)   KeyboardController.pressUpKey();
    	break;
    case "left": // left
    	for(; repeat > 0; repeat--)   KeyboardController.pressLeftKey();
    	break;
    case "right": // right
    	for(; repeat > 0; repeat--)   KeyboardController.pressRightKey();
    	break;
    case "home":
    	KeyboardController.home();
    	break;
    case "end":
    	KeyboardController.end();
    	break;
    case "infiniloop":
    	Orient2.println("infiniloop " + currentInstruction.substring(11));
    	this.instructions = new ArrayList<String>();
    	this.instructions.add(currentInstruction.substring(11));
    	this.instructions.add("O");
    	Orient2.println("re-running ControlEngine.run()");
    	this.run();
    	break;
    case "o": // finite reruns
    	loop(instr.substring(2));
    	break;
    case "O": // infinite reruns
    	this.run();
    	break;
    case "?":
    	break;
    case "leftClickAt":
	     Integer[] clickCoord = new Integer[5];
	     String[] temp = instr.substring(cmdlen).replace(" ", "-").split("-");
	     //if(Orient.debug) System.out.println(instr);
	     for(int j = 0; j < clickCoord.length; j++) {
	         clickCoord[j] = Integer.valueOf(temp[j]); // x
	    	 Orient2.println("" + clickCoord[j]);
	     }
	     //clickCoord[1] = Integer.valueOf(temp[1]); // y
	     Orient2.println("Clicking...");
	     MouseController.click(clickCoord[0], clickCoord[1], clickCoord[2], clickCoord[3], clickCoord[4]); // {xy}1, {xy}2, release delay
     break;
    case "niftyClick":
	     clickCoord = new Integer[5];
	     temp = instr.substring(cmdlen).replace(" ", "-").split("-");
	     //if(Orient.debug) System.out.println(instr);
	     for(int j = 0; j < clickCoord.length; j++) {
	         clickCoord[j] = Integer.valueOf(temp[j]); // x
	    	 Orient2.println("" + clickCoord[j]);
	     }
	     //clickCoord[1] = Integer.valueOf(temp[1]); // y
	     Orient2.println("Clicking...");
	     MouseController.niftyClick(clickCoord[0], clickCoord[1], clickCoord[2], clickCoord[3], clickCoord[4]); // {xy}1, {xy}2, release delay
    break;
   }
   
   currentInstructionIndex++;
   Orient2.println(" | completed.\nWaiting " + globalWait + " second(s)");
   Thread.sleep(globalWait);
   /*
			switch(instr.charAt(0)) { // TODO: charAt -> str.split(" ")[0];
			case 'w': // wait
				MouseController.wait(Integer.valueOf(instr.replace(" ", "-").split("-")[1]));
				break;
			case 'd': // drag
				//useController.drag(int x1, int y2, int x2, int y2, int delaySec);
				//useController.drag(x1, y1, x2, y2, delaySec);
				Integer[] fiveTemp = new Integer[5];
				String[] instrSixTemp = instr.replace(" ", "-").split("-");
				for(int i = 0; i < fiveTemp.length; i++) {
					if(Orient.debug) System.out.println(instrSixTemp[i]); 
					fiveTemp[i] = Integer.valueOf(instrSixTemp[i+1]);
				}
				MouseController.drag(fiveTemp[0], fiveTemp[1], fiveTemp[2], fiveTemp[3], fiveTemp[4]);
				break;
				//MouseController.drag(, y1, x2, y2, delaySec);
			case 'r': // run another script
				new ControlEngine(Orient.workingDirectory, instr.substring(2)).run();
				break;
			case 'k': // key press (and release)
				break;
			case 'K': // key down (include delay for release)
				break;
			case 'W': // wait until specific time
				scheduledWait(instr.substring(2));
				break;
			case 'p': // paste
				KeyboardController.paste();
				break;
			case 'x':
				System.exit(0);
				break;
			case 'b':
				KeyboardController.backspace();
				break;
			case 'm':
				WindowController.maximizeWindow(instr.substring(2));
				break;
			default: // assume click
				Integer[] clickCoord = new Integer[2];
				String[] temp = instr.replace(" ", "-").split("-");
				//if(Orient.debug) System.out.println(instr);
				clickCoord[0] = Integer.valueOf(temp[0]); // x
				clickCoord[1] = Integer.valueOf(temp[1]); // y
				MouseController.click(clickCoord[0], clickCoord[1], 0); // x,y, release delay
		}*/
  }
 }
	
	private void menu() throws NumberFormatException, InterruptedException, AWTException, UnsupportedFlavorException, IOException {
	// TODO Auto-generated method stub
        String[] pathnames;
        File f = new File(workingDirectory + "\\Orient");
        pathnames = f.list();

        int i = 0;
    	char c = 'z';
    	int longest = 0;
    	int longest2 = 0;
        
        for (String pathname : pathnames) 
        	if(pathname.contains(".txt")) {
        		if(longest < pathname.length()) longest = pathname.length();
        		else  if(longest2 < pathname.length()) longest2 = pathname.length();
	            fileNames.add(pathname);
        	}
        i = 0;
        
        menuFormatting(i, longest, longest2);
	}
	
	public void menuFormatting(int i, int longest, int longest2) throws IOException {
        // FINAL OUTPUT FORMATTING SOLUTION
        String output = "";
        int fileNamesSize = fileNames.size();
        for(i = 0; i < fileNamesSize;){
        	int fileNameSize = fileNames.get(i).length();

        	if(i%3==0) output+="\n";
        	else output +="\t";
        	
        	if(fileNameSize==longest) output+="\n\t";
        	
        	output += i + ": " + fileNames.get(i++);
        	for(int x = longest2-fileNameSize; x > 0; x--)
        		output += " ";
        }
        Orient2.println(output);
        
        // FORMATTING OUTPUT
        /*
        int k = 0;
    	int j = 0;
    	int longest = 900;
        String output = "";
        for(String fileName : fileNames) {
        	if(!(j < 3)) {
        		j = 0;
            	output += "\n";
        	}
        	for(; j < 3;) {
                output += k++ + ":";
                output += fileName.substring(0,fileName.length()-4);
                longest = output.length()>longest?output.length():longest;
                if(j != 3) output += " ";
	            break;
        	}
        	j++;
            //*if(!(pathname.charAt(0) == c)) {
            	//Orient2.print("\t");
            	//c = pathname.charAt(0);
            //}
        }
        */
        // REFORMATTING OUTPUT
		/*
        //Scanner outputScanner = new Scanner(output.split(" "));
        String tempOutput = "";
        for(String s : output.split(" ")) {
        	if(!(j < 3)) {
        		j = 0;
            	output += "\n";
        	}
        	tempOutput += s;
        	for(i = (longest-s.length()); i > 0; i--)
        		tempOutput += " ";
        	
        	j++;
        }
        output = tempOutput;
        //System.out.println(new StringAlignUtils(longest*3, Alignment.CENTER).format(output));
        System.out.println(output);
        */
        int choice = 0;
        try {
        	choice = ui.nextInt();
        }catch(Exception e) {
	         menuFormatting(i, longest, longest2);
        }
		   //List<String> temp = new ArrayList<String>();
		   Orient2.print("[SELECTION] ");
		   String selection = fileNames.get(choice);;
		   String str = "orient " + selection;
		   str = str.substring(0,str.length()-4);
		   //temp.add(str);

		   //ControlEngine.instructions = temp;
		   //ui.close();
		 //processBuilder.command("cmd.exe", "/c", "dir C:\\Users\\mkyong");
		   //ProcessBuilder processBuilder = new ProcessBuilder();
		   //processBuilder.command("cmd.exe", "/c", selection);
		   String cmdRunOrient = "java -jar \"%USERPROFILE%\\Desktop\\Orient\\Orient" + ControlEngine.version + ".jar\" c \"%USERPROFILE%\\Desktop\" ";
	         Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"" + cmdRunOrient + selection.substring(0,selection.length()-4) + "\""); 
		   /*
			   try {
				run();
			} catch (NumberFormatException | InterruptedException | AWTException | UnsupportedFlavorException
					| IOException e) {
				// TODO Auto-generated catch block
				System.exit(1);
			}
			*/

	         menuFormatting(i, longest, longest2);
	}

	private void loop(String substring) {
	
	}

	protected static void wait(int ms) throws InterruptedException {
		Thread.sleep(ms);
		return;
	}

 private void scheduledWait(String targetTime) throws InterruptedException {
  // convert 2:59 PM 7/26/2019 to correct format
  // date format must be YYYY/MM/DD HH:MM:SS
  /*if(targetDate.substring(0, 4).contains(":")) {
  	String[] temp = targetDate.split(" ");
  	String[] temp2 = temp[2].split("/");
  	String dateTemp = temp2[2] + "/" + temp2[1] + "/" + temp2[0];
  	//String timeTemp = (temp[0].charAt(0)!='1'&&temp[0].charAt(1)==':'?"0":(temp[1].charAt(0)=='P'?))) + temp[0];
  	String[] timeTemp = temp[0].split(":");
  }
  LocalDateTime.parse(targetDate.replace(" ", "T").replace("/","-")).atZone(ZoneId.of("Central")).toInstant().toEpochMilli();
  */
  /*String pattern = "yyyy-MM-dd hh:mm:ss";
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
  String date = simpleDateFormat.format();*/
  /*
  try {
      DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("h:mm M/d/yyyy");
      LocalDate date = LocalDate.parse(targetDate, formatter);
      System.out.printf("%s%n", date);
  	long targetTime = LocalDateTime.parse(date.replace(" ", "T").replace("/","-")).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

  	formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
  }
  catch (DateTimeParseException exc) {
      System.out.printf("%s is not parsable!%n", targetDate);
      throw exc;      // Rethrow the exception.
  }
  // 'date' has been successfully parsed*/
  // convert 2:59 PM 7/26/2019 to correct format
  int hourOfDay = Integer.valueOf(targetTime.split(":")[0]);
  if(((targetTime.split(" ")[1].contains("P")))) hourOfDay+=12; // checks AM/PM and fixes hourOfDay accordingly (milTime) 
  int minute = Integer.valueOf(targetTime.split(":")[1].split(" ")[0]); // grabs the remaining minutes
  long ms = minute*60000; // converts the minutes to ms
  ms+= hourOfDay*3600000; // converts the hours to ms
  ms -= System.currentTimeMillis(); // gets the time difference between now and target time
  if(((ms < 0))) ms+=(((24*3600000))); // if the time has passed, then 24 hours are added (why?)
  wait(ms); // starts the wait. This is why it checks if time is in negatives... because thats prep before this countdown
  if(((targetTime.contains("r")))) scheduledWait(targetTime, true); // checks if "r" is in the input string. if so, then it repeats
  //new AlarmClock(hourOfDay, minute, second).start();

  return;
 }

 private void scheduledWait(String targetDate, boolean recurring) throws InterruptedException{
  instructions.add("W " + targetDate);
 }

@Override
public void keyPressed(KeyEvent arg0) {
	// TODO Auto-generated method stub
	//if(arg0.equals(obj))
}

@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}

 /*public static void waitUntil() {
     //the Date and time at which you want to execute
     DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
     Date date = dateFormatter .parse("06-07-2012 13:05:45");

     //Now create the time and schedule it
     Timer timer = new Timer();

     //Use this if you want to execute it once
     timer.schedule(new MyTimeTask(), date);
 }*/
/*
 class AlarmClock {

  private final Scheduler scheduler = new Scheduler();
  private final SimpleDateFormat dateFormat =
   new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS");
  private final int hourOfDay, minute, second;

  public AlarmClock(int hourOfDay, int minute, int second) {
   this.hourOfDay = hourOfDay;
   this.minute = minute;
   this.second = second;
  }

  public void start() {
   scheduler.schedule(new SchedulerTask() {
    public void run() {
     soundAlarm();
    }
    private void soundAlarm() {
     return;
    }
   }, new DailyIterator(hourOfDay, minute, second));
  }
 }*/
}
