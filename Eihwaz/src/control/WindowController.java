package control;

import java.util.Map;
import java.util.Map.Entry;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

import me.coley.simplejna.Windows;

public class WindowController {
	public static void maximizeWindow(String windowName) {
		//Map<HWND, String> windows = Windows.getWindows();
		
        /*User32 user32 = Native.loadLibrary("User32.dll", User32.class);
        //user32.FindWindow(windowName, windowName);
        HWND window = user32.FindWindow(windowName, windowName);
        //HWND FindWindow(String winClass, String title); 
        boolean success = user32.ShowWindow(window, Windows.SW_MAXIMIZE);
        if (success) {
            System.out.println("Success");
        } else {
            System.out.println("Fail: " + Native.getLastError());
        })/
        
        //Pointer.nativeValue(handle.getPointer());
        
        /*for (Entry<HWND, String> window : windows.entrySet()) {
        	
        }*/
		
		final WinDef.HWND[] windowHandle = new WinDef.HWND[1];
		User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
		    @Override
		    public boolean callback(WinDef.HWND hwnd, Pointer pointer) {
		        if (matches(hwnd)) {
		            windowHandle[0] = hwnd;
		            return false;
		        }
		        return true;
		    }

			private boolean matches(HWND hwnd) {
				System.out.println(hwnd.toString());
				
				return false;
			}
		}, Pointer.NULL);
		
		// Find and minimize a window:
		WinDef.HWND hWnd = User32.INSTANCE.FindWindow("className", "windowName");
		User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_MINIMIZE);
	}
}
