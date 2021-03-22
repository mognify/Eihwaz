package orient;

import java.awt.AWTException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.Session;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT.HRESULT;

import access.EmailAccess;
import blessings.ImageAccess;
import control.ControlEngine;
import control.WindowController;
import html.WebController;

public class Orient2 {
	// MCubeEngine ->MCubeScript
	// MCubeEngine: read file for coordinates to use with MCube
	// MCubeScript: create and run the script for MCube
	
	// GraphBuilder->GraphSaver
	// GraphBuilder: houses methods for building the graphs
	// GraphSaver: houses methods for saving and inputting the graphs
	public final static boolean DEBUG = true;
	private static volatile String appendMessage = "";
	public static volatile boolean alwaysAppendMessage = false;
	public static String workingDirectory = "";
	public static String[] args;
	private static ControlEngine ce = null;
	
	public Orient2(String[] args) {
		main(args);
	}
	
	public static void main(String[] args) {
		Orient2.println("oy");
		Orient2.args = args;
		workingDirectory = getWorkingDirectory();
		Orient2.println("Loading OpenCV libraries....");
		ImageAccess.loadLibraries();
		/*if(args.length<1 || args[0].length() < 2)*/ 
		//else workingDirectory = args[0];
		/*
		new EmailAccess().xs();
		System.exit(0);
		WindowController.maximizeWindow("Flight Deck");
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*try {
			ce = new ControlEngine();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Orient2.print("Fucked up from the start");
		}*/

		Orient2.println("Starting Control Engine with args: ");
		if(args.length>0) {
			switch(args[0].charAt(0)) {
				case 'C':
					Orient2.println("Detected option \"C\"");
					activateControl(args);
					break;
				case 'c':
					Orient2.println("Detected option \"c\"");
					activateControl(args[1]/*.charAt(0)*/, args[2]);
					break;
				case 'h':
					Orient2.println("Detected option \"h\"");
					activateHTTP();
					break;
				default: 
					Orient2.println("Detected no options");
					activateControl();
			}
		}else{
			Orient2.println("Detected no options; defaulting...");
			activateControl();
		}
		//activateHTTP();
	}
	
	private static void activateHTTP() {
		// TODO Auto-generated method stub
		try {
			new WebController().sendCURLGet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void activateControl() {
		Orient2.print("activateControl()");
		/* I want to test accomplishing a task with just Click/Drag/Wait/Time
		 * (1/3: Just test Mouse* (Robot), not Graph* (REST, GRAL, JavaFX)
		 */
		try { 
			Orient2.print("Running Control EngineLEVI WUZ HERE");
			ce = new ControlEngine(workingDirectory);
			ce.run();
			System.in.read();
		} catch (NumberFormatException | InterruptedException | AWTException | UnsupportedFlavorException | IOException e) {
			// TODO Auto-generated catch block
			print(e.getMessage());
			//e.printStackTrace();
			try {
				System.in.read();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private static void activateControl(/*char*/ String c, String arg) {
		Orient2.println("activateControl(c: " + c + ", args: " + arg + ")");
		/* I want to test accomplishing a task with just Click/Drag/Wait/Time
		 * (1/3: Just test Mouse* (Robot), not Graph* (REST, GRAL, JavaFX)
		 */
		try { 
			Orient2.println("Loading control engine with c and args...");
			ControlEngine ce = new ControlEngine(arg);
			Orient2.println("Running Control Engine with the following file: " + arg + ".txt");
			ce.run();
		} catch (NumberFormatException | InterruptedException | AWTException | UnsupportedFlavorException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void activateControl(/*char*/ String[] commandWithArgs) {
		Orient2.println("activateControl(commandWithArgs)");
		/* I want to test accomplishing a task with just Click/Drag/Wait/Time
		 * (1/3: Just test Mouse* (Robot), not Graph* (REST, GRAL, JavaFX)
		 */
		try { 
			Orient2.println("Loading control engine with C and args...");
			String temp = "";
			for(int i = 1; i < commandWithArgs.length; i++) {
				temp += commandWithArgs[i] + " ";
			}
			temp = temp.substring(0, temp.length()-1);
			ControlEngine ce = new ControlEngine(true, temp);
			Orient2.println("Running Control Engine with the following command: " + temp);
			ce.run();
		} catch (NumberFormatException | InterruptedException | AWTException | UnsupportedFlavorException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// successfully retrieves user's desktop
	// no filechooser
	public static String getWorkingDirectory() {
		Orient2.println("Getting working directory...");
		String workDir = "";
		char[] pszPath = new char[WinDef.MAX_PATH];
		Shell32.INSTANCE.SHGetFolderPath(null,
		      ShlObj.CSIDL_DESKTOPDIRECTORY, null, ShlObj.SHGFP_TYPE_CURRENT,
		      pszPath);
		workDir = Native.toString(pszPath);

		Orient2.println("Working directory is: "  + workDir);
		return workDir;
	}

	public static synchronized void print(String output){
		if(Orient2.DEBUG) System.out.print(output);
	}
	
	public static synchronized void println(String output){
		if(Orient2.DEBUG) {
			System.out.println(output);
			if(alwaysAppendMessage) System.out.println(appendMessage);
		}
	}
}
