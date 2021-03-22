package blessings;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.KeyStroke;

import control.ControlEngine;
import orient.Orient2;

public class KeyboardController implements ClipboardOwner{
	private static Clipboard clipboard;
	public static boolean controlDown = false;
	public static boolean cDown = false;
	
	protected static void paste() throws AWTException {
		Orient2.print("Pasting...");
		
		 
		ControlEngine.r.keyPress(KeyEvent.VK_CONTROL);
		ControlEngine.r.keyPress(KeyEvent.VK_V);
		ControlEngine.r.keyRelease(KeyEvent.VK_V);
		ControlEngine.r.keyRelease(KeyEvent.VK_CONTROL);
		//beans rice water powdered milk peanut butter; 10 cases of water 
	}
	public static void typePaste() throws AWTException, InterruptedException {
		Orient2.print("Keypressing clipboard...");
		

		String temp = "";
		 Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		    Transferable t = c.getContents(new KeyboardController());
		    if (t == null)
		        return;
		    try {
		        temp = ((String) t.getTransferData(DataFlavor.stringFlavor));
		    } catch (Exception e){
		        Orient2.println("Trying to get clipboard information...");
		        Thread.sleep(333);
		        typePaste();
		    }//try
		    
		    paste(temp);
		//beans rice water powdered milk peanut butter; 10 cases of water 
	}
	protected static void typePasteRegex(String regex) throws AWTException, InterruptedException {
		Orient2.print("Keypressing clipboard with regex \"" + regex + "\"...");
		

		String temp = "";
		//temp = clipboardRegex(regex);
		 Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		    Transferable t = c.getContents(new KeyboardController());
		    if (t == null)
		        return;
		    try {
		        temp = ((String) t.getTransferData(DataFlavor.stringFlavor));
		    } catch (Exception e){
		        Orient2.println("Trying to get clipboard information...");
		        Thread.sleep(333);
		        typePaste();
		    }//try
		    
		    paste(temp);
		//beans rice water powdered milk peanut butter; 10 cases of water 
	}
	
	public static void paste(String text)  throws AWTException{
		Orient2.print("Pasting: " + text);
		int tl = text.length();

		 
		/*for(int i = 0; i < tl; i++) {
			type(text.charAt(i));
		}*/
		
		type(text);
		
	}
    public static void type(CharSequence characters) {
        int length = characters.length();
        for (int i = 0; i < length; i++) {
            char character = characters.charAt(i);
            type(character);
        }
    }

    public static void type(char character) {
        switch (character) {
        case 'a': doType(KeyEvent.VK_A); break;
        case 'b': doType(KeyEvent.VK_B); break;
        case 'c': doType(KeyEvent.VK_C); break;
        case 'd': doType(KeyEvent.VK_D); break;
        case 'e': doType(KeyEvent.VK_E); break;
        case 'f': doType(KeyEvent.VK_F); break;
        case 'g': doType(KeyEvent.VK_G); break;
        case 'h': doType(KeyEvent.VK_H); break;
        case 'i': doType(KeyEvent.VK_I); break;
        case 'j': doType(KeyEvent.VK_J); break;
        case 'k': doType(KeyEvent.VK_K); break;
        case 'l': doType(KeyEvent.VK_L); break;
        case 'm': doType(KeyEvent.VK_M); break;
        case 'n': doType(KeyEvent.VK_N); break;
        case 'o': doType(KeyEvent.VK_O); break;
        case 'p': doType(KeyEvent.VK_P); break;
        case 'q': doType(KeyEvent.VK_Q); break;
        case 'r': doType(KeyEvent.VK_R); break;
        case 's': doType(KeyEvent.VK_S); break;
        case 't': doType(KeyEvent.VK_T); break;
        case 'u': doType(KeyEvent.VK_U); break;
        case 'v': doType(KeyEvent.VK_V); break;
        case 'w': doType(KeyEvent.VK_W); break;
        case 'x': doType(KeyEvent.VK_X); break;
        case 'y': doType(KeyEvent.VK_Y); break;
        case 'z': doType(KeyEvent.VK_Z); break;
        case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
        case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
        case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
        case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
        case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
        case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
        case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
        case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
        case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
        case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
        case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
        case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
        case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
        case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
        case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
        case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
        case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
        case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
        case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
        case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
        case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
        case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
        case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
        case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
        case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
        case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
        case '`': doType(KeyEvent.VK_BACK_QUOTE); break;
        case '0': doType(KeyEvent.VK_0); break;
        case '1': doType(KeyEvent.VK_1); break;
        case '2': doType(KeyEvent.VK_2); break;
        case '3': doType(KeyEvent.VK_3); break;
        case '4': doType(KeyEvent.VK_4); break;
        case '5': doType(KeyEvent.VK_5); break;
        case '6': doType(KeyEvent.VK_6); break;
        case '7': doType(KeyEvent.VK_7); break;
        case '8': doType(KeyEvent.VK_8); break;
        case '9': doType(KeyEvent.VK_9); break;
        case '-': doType(KeyEvent.VK_MINUS); break;
        case '=': doType(KeyEvent.VK_EQUALS); break;
        case '~': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
        case '!': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_1); break;
        case '@': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_2); break;
        case '#': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_3); break;
        case '$': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_4); break;
        case '%': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
        case '^': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_6); break;
        case '&': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_7); break;
        case '*': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_8); break;
        case '(': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_9); break;
        case ')': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_0); break;
        case '_': doType(KeyEvent.VK_UNDERSCORE); break;
        case '+': doType(KeyEvent.VK_PLUS); break;
        case '\t': doType(KeyEvent.VK_TAB); break;
        case '\n': doType(KeyEvent.VK_ENTER); break;
        case '[': doType(KeyEvent.VK_OPEN_BRACKET); break;
        case ']': doType(KeyEvent.VK_CLOSE_BRACKET); break;
        case '\\': doType(KeyEvent.VK_BACK_SLASH); break;
        case '{': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break;
        case '}': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break;
        case '|': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break;
        case ';': doType(KeyEvent.VK_SEMICOLON); break;
        case ':': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON); break;
        case '\'': doType(KeyEvent.VK_QUOTE); break;
        case '"': doType(KeyEvent.VK_QUOTEDBL); break;
        case ',': doType(KeyEvent.VK_COMMA); break;
        case '<': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA); break;
        case '.': doType(KeyEvent.VK_PERIOD); break;
        case '>': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD); break;
        case '/': doType(KeyEvent.VK_SLASH); break;
        case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
        case ' ': doType(KeyEvent.VK_SPACE); break;
        default:
            throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    private static void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    private static void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
            return;
        }

        ControlEngine.r.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        ControlEngine.r.keyRelease(keyCodes[offset]);
    }
	/*
	protected static void type(char c){
	        int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
	        if (KeyEvent.CHAR_UNDEFINED == keyCode) {
	            throw new RuntimeException(
	                "Key code not found for character '" + c + "'");
	        }
	        ControlEngine.r.keyPress(keyCode);
	        ControlEngine.r.delay(100);
	        ControlEngine.r.keyRelease(keyCode);
	        ControlEngine.r.delay(100);
		
		/*
		ControlEngine.r.keyPress(KeyEvent.VK_ALT);
		ControlEngine.r.keyPress(KeyEvent.VK_NUMPAD0);
        ControlEngine.r.keyRelease(KeyEvent.VK_NUMPAD0);
        String altCode=Integer.toString(c);
        for(int i=0;i<altCode.length();i++){
            c=(char)(altCode.charAt(i)+'0');
            //delay(20);//may be needed for certain applications
            ControlEngine.r.keyPress(c);
            //delay(20);//uncomment if necessary
            ControlEngine.r.keyRelease(c);
        }
        ControlEngine.r.keyRelease(KeyEvent.VK_ALT);*/
    //}
	
	/*protected static void type(String s) throws AWTException {
		int sl = s.length();
		r r = new r();
		for(int i = 0; i < sl; i++) {
			type(r, s.charAt(i));
		}
	}*/
	
	public static void backspace() throws AWTException{
		 
		ControlEngine.r.keyPress(KeyEvent.VK_BACK_SPACE);
		ControlEngine.r.keyRelease(KeyEvent.VK_BACK_SPACE);
		
		return;
	}
	
	protected static void backspace(int repeat) throws AWTException{
		for(; repeat > 0; repeat--) backspace();
		
		return;
	}

	/*public static void type(String substring) throws AWTException {
		// TODO Auto-generated method stub
		String temp = "";
		 Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		    Transferable t = c.getContents(new KeyboardController());
		    if (t == null)
		        return;
		    try {
		        temp = ((String) t.getTransferData(DataFlavor.stringFlavor));
		    } catch (Exception e){
		        e.printStackTrace();
		    }//try
		    
		    StringSelection ss = new StringSelection(substring);
		    c.setContents(ss, (ClipboardOwner) new KeyboardController());
		    
		    paste();
		    
		    c.setContents(new StringSelection(temp), (ClipboardOwner) new KeyboardController());
		    
		    return;
	}*/
	
	public static String clipboardRegex(String regex, String replaceWith) throws UnsupportedFlavorException, IOException {
		Orient2.print("\nclipboardRegex(" + regex + ")\n");
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String temp = (String) clipboard.getData(DataFlavor.stringFlavor); 
		temp.replace(regex, replaceWith);
		
		return temp;
	}

/*public void paste(String substring) throws AWTException, InterruptedException, UnsupportedFlavorException, IOException {
		Orient.print("\npaste(" + substring + ")\n");
		//StringSelection stringSelection = new StringSelection(substring);

		Orient.print("\nBacking up clipboard...");
		String temp = "image deleted by Orient";
		if(!clipboardContainsImage()) {
			temp = (String) clipboard.getData(DataFlavor.stringFlavor); 
			Orient.print("Clipboard contents are: " + temp);
		}else {
			Orient.print("Clipboard contained an image. It's gone now.");
		}*/
		/*clipboard.getData(DataFlavor.stringFlavor);
		Orient.print("String temp = (String) clipboard.getData(DataFlavor.stringFlavor)");
		String temp = (String) clipboard.getData(DataFlavor.stringFlavor); */
/*setClipboard(substring);
		paste();
		setClipboard(temp);
		
		Orient.print("\nLeaving paste(String)\n");
		return;
	}*/
	
	public static String getClipboardContents() {
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String temp2 = "";
		
		try {
			clipboard.getData(DataFlavor.stringFlavor);
			temp2 = (String) clipboard.getData(DataFlavor.stringFlavor); 
			Orient2.print("Clipboard contents: " + temp2);
		} catch (UnsupportedFlavorException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Orient2.println("(Trying to grab clipboard conents again)");
			getClipboardContents();
		}
		
		return temp2;
	}
	
public static void setClipboard(String substring, ClipboardOwner co) throws UnsupportedFlavorException, IOException {
	Orient2.print("\nsetClipboard(" + substring + ")");
	clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	boolean good = false;
	StringSelection selectionString = new StringSelection(substring);
	while(!good) {
		Orient2.print("Setting clipboard to \"" + selectionString + "\"...");
		clipboard.setContents(selectionString, co);

		Orient2.print("Checking clipboard pre-paste...");
		clipboard.getData(DataFlavor.stringFlavor);
		String temp2 = (String) clipboard.getData(DataFlavor.stringFlavor); 
		Orient2.print("Clipboard contents: " + temp2);
		
		if(!temp2.equals(substring)) {
			good = false;
			Orient2.print("\tFAIL clipboard set attempt; retrying... ");
			}else{
				Orient2.print("\n\tSUCCESS clipboard set! Contents: " + (String) clipboard.getData(DataFlavor.stringFlavor) + "\n");
				good = true;
			}
	}
	
	return;
	}

public static boolean clipboardContainsImage() {
	Orient2.print("\nclipboardContainsImage()\n");
	clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	for(DataFlavor df : clipboard.getAvailableDataFlavors()) {
		if(df.isFlavorTextType())
			return true;
	}
	return false;
}

	public static void copy() throws AWTException, InterruptedException {
		ControlEngine.r.keyPress(KeyEvent.VK_CONTROL);
		Thread.sleep(200);
		ControlEngine.r.keyPress(KeyEvent.VK_C);
		Thread.sleep(200);
		ControlEngine.r.keyRelease(KeyEvent.VK_C);
		Thread.sleep(200);
		ControlEngine.r.keyRelease(KeyEvent.VK_CONTROL);
		System.out.println("ABNR");
		/*
		String temp = "";
		boolean waitingClipboardSetSuccess = true;
		while(waitingClipboardSetSuccess) {
		 //Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		    //Transferable t = c.getContents(new KeyboardController());
		    /*if (t == null) {
		    	Orient2.println("Clipboard contents empty, skipping.");
		        return;
		    }*/
		    //try {
		    	//Orient2.println("Clipboard contents: " +  t + "\nTrying to actually get the contents from clipboard to be sure");
		        //temp = ((String) t.getTransferData(DataFlavor.stringFlavor));
			    /*temp = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		    } catch (IllegalStateException | UnsupportedFlavorException | IOException e){
		        Orient2.println("Trying to get clipboard information for copy method...");
		        Thread.sleep(666);
		    }
		}
			
			Orient2.println("Clipboard now contains: " + temp);
		*/
		return;
	}

	public static void f(String substring) throws AWTException {
		 
		switch(substring.charAt(0)) {
		case '1':
			ControlEngine.r.keyPress(KeyEvent.VK_F1);
			ControlEngine.r.keyRelease(KeyEvent.VK_F1);
			break;
		case '2':
			ControlEngine.r.keyPress(KeyEvent.VK_F2);
			ControlEngine.r.keyRelease(KeyEvent.VK_F2);
			break;
		case '3':
			ControlEngine.r.keyPress(KeyEvent.VK_F3);
			ControlEngine.r.keyRelease(KeyEvent.VK_F3);
			break;
		case '4':
			ControlEngine.r.keyPress(KeyEvent.VK_F4);
			ControlEngine.r.keyRelease(KeyEvent.VK_F4);
			break;
		case '5':
			ControlEngine.r.keyPress(KeyEvent.VK_F5);
			ControlEngine.r.keyRelease(KeyEvent.VK_F5);
			break;
		case '6':
			ControlEngine.r.keyPress(KeyEvent.VK_F6);
			ControlEngine.r.keyRelease(KeyEvent.VK_F6);
			break;
		case '7':
			ControlEngine.r.keyPress(KeyEvent.VK_F7);
			ControlEngine.r.keyRelease(KeyEvent.VK_F7);
			break;
		case '8':
			ControlEngine.r.keyPress(KeyEvent.VK_F8);
			ControlEngine.r.keyRelease(KeyEvent.VK_F8);
			break;
		case '9':
			ControlEngine.r.keyPress(KeyEvent.VK_F9);
			ControlEngine.r.keyRelease(KeyEvent.VK_F9);
			break;
		case 'a':
			ControlEngine.r.keyPress(KeyEvent.VK_F10);
			ControlEngine.r.keyRelease(KeyEvent.VK_F10);
			break;
		case 'b':
			ControlEngine.r.keyPress(KeyEvent.VK_F11);
			ControlEngine.r.keyRelease(KeyEvent.VK_F11);
			break;
		case 'c':
			ControlEngine.r.keyPress(KeyEvent.VK_F12);
			ControlEngine.r.keyRelease(KeyEvent.VK_F12);
			break;
		}
		
		return;
	}

	public static void enter() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_ENTER);
		ControlEngine.r.keyRelease(KeyEvent.VK_ENTER);
		
		return;
	}

	public static void tab() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_TAB);
		ControlEngine.r.keyRelease(KeyEvent.VK_TAB);
		
		return;
	}
	public static void tab(int x) throws AWTException {
		for(; x > 0; x--) {
			ControlEngine.r.keyPress(KeyEvent.VK_TAB);
			ControlEngine.r.keyRelease(KeyEvent.VK_TAB);
		}
		return;
	}

	public static void ctrlDown() throws AWTException {
		ControlEngine.r.keyPress(KeyEvent.VK_CONTROL);
		
		return;
	}
	public static void ctrlUp() throws AWTException {
		ControlEngine.r.keyRelease(KeyEvent.VK_CONTROL);
		
		return;
	}

	public static void shiftDown() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_SHIFT);
		
		return;
	}
	public static void shiftUp() throws AWTException {
		 
		ControlEngine.r.keyRelease(KeyEvent.VK_SHIFT);
		
		return;
	}

	public static void keyStroke(String substring) throws AWTException {
		Orient2.println("Pressing " + substring);
		KeyStroke x = KeyStroke.getKeyStroke(substring/*.substring(5)*/);
		 
		int length = substring.length();
		for(int i = 0; i < length; i++) {
			Character c = substring.charAt(i);
			if(c.equals('@')) {
				ControlEngine.r.keyPress(KeyEvent.VK_SHIFT);
				ControlEngine.r.keyPress(KeyEvent.VK_2);
				ControlEngine.r.keyRelease(KeyEvent.VK_2);
				ControlEngine.r.keyRelease(KeyEvent.VK_SHIFT);
			}else if(c.equals('!')) {
				ControlEngine.r.keyPress(KeyEvent.VK_SHIFT);
				ControlEngine.r.keyPress(KeyEvent.VK_1);
				ControlEngine.r.keyRelease(KeyEvent.VK_1);
				ControlEngine.r.keyRelease(KeyEvent.VK_SHIFT);
			}else{
				if(Character.isUpperCase(c)) ControlEngine.r.keyPress(KeyEvent.VK_SHIFT);
				int keyCode = c;
				ControlEngine.r.keyPress(keyCode);
				if(Character.isUpperCase(c)) ControlEngine.r.keyRelease(KeyEvent.VK_SHIFT);
			}
		}
	}
	
	/*public static void alphabetPress(String substring) {
		KeyStroke x = KeyStroke.getKeyStroke(substring);
		 
		int length = substring.length();
		for(int i = 0; i < length; i++) {
			Character c = substring.charAt(i);
			if(Character.isUpperCase(c)) ControlEngine.r.keyPress(KeyEvent.VK_SHIFT);
			int keyCode = c;
			ControlEngine.r.keyPress(keyCode);
		}
	}*/

	public static void pressDownKey() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_DOWN);
		ControlEngine.r.keyRelease(KeyEvent.VK_DOWN);
		
		return;
	}
	public static void pressDownKey(int x) throws AWTException {
		 
		for(; x > 0; x--) {
			ControlEngine.r.keyPress(KeyEvent.VK_DOWN);
			ControlEngine.r.keyRelease(KeyEvent.VK_DOWN);
		}
		return;
	}
	public static void pressRightKey() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_RIGHT);
		ControlEngine.r.keyRelease(KeyEvent.VK_RIGHT);
		
		return;
	}
	public static void pressRightKey(int x) throws AWTException {
		 
		for(; x > 0; x--) {
			ControlEngine.r.keyPress(KeyEvent.VK_RIGHT);
			ControlEngine.r.keyRelease(KeyEvent.VK_RIGHT);
		}
		return;
	}
	public static void pressLeftKey() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_LEFT);
		ControlEngine.r.keyRelease(KeyEvent.VK_LEFT);
		
		return;
	}
	public static void pressLeftKey(int x) throws AWTException {
		 
		for(; x > 0; x--) {
			ControlEngine.r.keyPress(KeyEvent.VK_LEFT);
			ControlEngine.r.keyRelease(KeyEvent.VK_LEFT);
		}
		return;
	}
	public static void pressUpKey() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_UP);
		ControlEngine.r.keyRelease(KeyEvent.VK_UP);
		
		return;
	}
	public static void pressUpKey(int x) throws AWTException {
		 
		for(; x > 0; x--) {
			ControlEngine.r.keyPress(KeyEvent.VK_UP);
			ControlEngine.r.keyRelease(KeyEvent.VK_UP);
		}
		return;
	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		Orient2.println("lostOwnership()");
	}

	public static void delete() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_DELETE);
		ControlEngine.r.keyRelease(KeyEvent.VK_DELETE);
		
		return;
	}
	public static void delete(int x) throws AWTException {
		 
		for(; x > 0; x--) {
			ControlEngine.r.keyPress(KeyEvent.VK_DELETE);
			ControlEngine.r.keyRelease(KeyEvent.VK_DELETE);
		}
		return;
	}

	public static void home() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_HOME);
		ControlEngine.r.keyRelease(KeyEvent.VK_HOME);
		
		return;
	}

	public static void end() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_END);
		ControlEngine.r.keyRelease(KeyEvent.VK_END);
		
		return;
	}

	public static void winKey() throws AWTException {
		 
		ControlEngine.r.keyRelease(KeyEvent.VK_WINDOWS);
		ControlEngine.r.keyPress(KeyEvent.VK_WINDOWS);
		
		return;
	}
	public static void winDown() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_WINDOWS);
		
		return;
	}
	public static void winUp() throws AWTException {
		 
		ControlEngine.r.keyRelease(KeyEvent.VK_WINDOWS);
		
		return;
	}

	public static void space() throws AWTException {
		 
		ControlEngine.r.keyPress(KeyEvent.VK_SPACE);
		ControlEngine.r.keyRelease(KeyEvent.VK_SPACE);
		
		return;
	}
	public static void altDown() {
		ControlEngine.r.keyPress(KeyEvent.VK_ALT);
		
		return;
	}
	public static void altUp() {
		ControlEngine.r.keyRelease(KeyEvent.VK_ALT);
		
		return;
	}
	public static void cbMathType(String instr) {
		   // e.g. cbMath m 1.2
		   // multiply clipboard contents by 1.2
		   // Multiply, Divide, Add, Subtract
		   String s = "1";
		try {
			s = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			// TODO Auto-generated catch block
			System.out.println("cbMathType fucked up at clipboard getting");
			e.printStackTrace();
		}
		   Double cbMathVarX = Double.valueOf(s.split(" ")[0]);
		   Double cbMathVarY = Double.valueOf(instr.split(" ")[2]);
		   switch(instr.split(" ")[1]) {
			   case "M": case "m": cbMathVarX *= cbMathVarY; break;
			   case "D": case "d":  cbMathVarX /= cbMathVarY; break;
			   case "A": case "a": cbMathVarX += cbMathVarY; break;
			   case "S": case "s":  cbMathVarX -= cbMathVarY; break;
			   default: break;
		   }
		   
		   type(String.valueOf(cbMathVarX));
	}
}
