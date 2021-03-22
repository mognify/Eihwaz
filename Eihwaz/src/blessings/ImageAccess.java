package blessings;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import com.sun.jna.Native;
//import com.sun.jna.platform.win32.W32API;
import com.sun.jna.win32.*;
import com.sun.jna.win32.W32APIOptions;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
//import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFOHEADER;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIOptions;

import control.AudioController;
import control.ControlEngine;
import orient.Orient2;
 
public class ImageAccess implements ClipboardOwner{
	
	public static String resultImgName = "TemplateMatchingOutput.jpg";
	public static volatile double threshold = .81;
	public static volatile BufferedImage lastScreenshot = null;
	public static int numThreadsDone = 0;
	public static boolean matchFound = false;
	public static volatile Mat ss;
	public static volatile Integer delay;
	public static volatile boolean manyMatchWithClick = false;
	public static volatile boolean[] alreadyRunning;
	
	/*public static void preloadTemplates() {
		for(String instruction : ControlEngine.instructions) {
			if(instruction.matches("(?s).*\\M\\b.*\\m\\b.*"))
				
		}
	}*/
 
	public static void loadLibraries() {
		Orient2.println("loadLibraries()");

	    try {
	        InputStream in = null;
	        File fileOut = null;
	        String osName = System.getProperty("os.name");
	        //String opencvpath = System.getProperty("user.dir");
	        String opencvpath = Orient2.workingDirectory + "\\Orient";
	        if(osName.startsWith("Windows")) {
	            int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
	            if(bitness == 32) {
	                opencvpath+="\\opencv\\x86\\";
	            }
	            else if (bitness == 64) { 
	                opencvpath+="\\opencv\\x64\\";
	            } else { 
	                opencvpath+="\\opencv\\x86\\"; 
	            }           
	        } 
	        else if(osName.equals("Mac OS X")){
	            opencvpath +="mac users should get spanked";
	        }
	        opencvpath=opencvpath.replace("opencv","opencv\\build\\java");
	        System.out.println("OpenCV library path is: " + opencvpath);
	        System.load(opencvpath + "opencv_java411.dll");
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to load opencv native library", e);
	    }
	}
	
	public static Point screenSearch(String targetName, Point clickCoords) throws IOException {
    	Orient2.println("screenSearch(" + targetName + ")");
    	//System.setProperty("java.library.path", "C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java");
    	//System.loadLibrary("opencv_java411");
        //System.load("C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java\\x64\\opencv_java411");
        
        Mat source=null;
        Mat template=null;
        String filePath=Orient2.workingDirectory;
        //Load image file
        try {
            screenshot();
        }catch(CvException cve) {
        	System.out.println("\n\tCouldn't take screenshot");
        }
        String targetPath = filePath+"\\Orient\\"+targetName;
        Orient2.println("targetPath: " + targetPath);
        template=Imgcodecs.imread(targetPath);	
        // left off here. 2:31 PM 6/3/2020 CDT
        // putting the target load first, then passing it to screenshot(Point, Mat) for focused search
        // then decided i want to finish onCopy first
        
        String screenshotPath = filePath+"\\Orient\\screenshotFor-"+targetName;
        Orient2.println("screenshotPath: " + screenshotPath);
        source=Imgcodecs.imread(screenshotPath);
        //source=Imgcodecs.imread
    
        Orient2.println("Click registered at: " + (int)clickCoords.x + ", " + (int)clickCoords.y);
        Orient2.println("Reducing search region to 3x target image dimensions: " + (template.width()*3) + ", " + (template.height()*3));
        Rect tempRect = new Rect(template.width()*3, template.height()*3, (int)clickCoords.x, (int)clickCoords.y);
        //Mat temp = new Mat(source, tempRect);
        Mat temp = source.submat(tempRect);
        //source = temp;
        //source = source.submat((int)clickCoords.x, (int)clickCoords.y, template.width(), template.height());
        
        Mat outputImage=new Mat();    
        int machMethod =  Imgproc.TM_CCOEFF_NORMED;
        //Template matching method
        Imgproc.matchTemplate(temp, template, outputImage, machMethod);
 
        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
        Orient2.print("" + mmr.maxVal);
        if(mmr.maxVal < threshold) return null;
        Point matchLoc=mmr.maxLoc;
        //Draw rectangle on result image
        Imgproc.rectangle(temp, matchLoc, new Point(matchLoc.x + template.cols(),
                matchLoc.y + template.rows()), new Scalar(0, 255, 0));
        
        Imgcodecs.imwrite(filePath+"\\Orient\\"+resultImgName, temp);

        return matchLoc;
    }
	
    public static Point screenSearch(String targetName) throws IOException {
    	Orient2.println("screenSearch(" + targetName + ")");
    	//System.setProperty("java.library.path", "C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java");
    	//System.loadLibrary("opencv_java411");
        //System.load("C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java\\x64\\opencv_java411");
        
        Mat source=null;
        Mat template=null;
        String filePath=Orient2.workingDirectory;
        String targetPath = filePath+"\\Orient\\"+targetName;
        Orient2.println("targetPath: " + targetPath);
        template=Imgcodecs.imread(targetPath);	
        //Load image file
        //String screenshotPath = filePath+"\\Orient\\s\\screenshot.jpg";
        //Orient2.println("screenshotPath: " + screenshotPath);
        //source=Imgcodecs.imread(screenshotPath);
    
        
        Mat outputImage=new Mat();    
        int machMethod =  Imgproc.TM_CCOEFF_NORMED;
        //Template matching method
        while(true) {
        	Orient2.println("taking screenshot");
	        try {
	            source = screenshot();
	        }catch(CvException cve) {
	        	System.out.println("\n\tCouldn't take screenshot");
	        }
	        // anon thread start
        	Orient2.print("matching... ");
	        Imgproc.matchTemplate(source, template, outputImage, machMethod);
	 
	        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
	        Orient2.println("match ratio: " + mmr.maxVal + " vs. " + threshold);
	        Point matchLoc=mmr.maxLoc;
	        if(mmr.maxVal >= threshold) return matchLoc;
	        // anon thread done
        }
        //Draw rectangle on result image
        /*Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(),
                matchLoc.y + template.rows()), new Scalar(0, 255, 0));
        
        Imgcodecs.imwrite(filePath+"\\Orient\\"+resultImgName, source);*/
    }
	
    public static Point screenSearch(String targetName, double threshold) throws IOException {
    	Orient2.println("screenSearch(" + targetName + ")");
    	//System.setProperty("java.library.path", "C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java");
    	//System.loadLibrary("opencv_java411");
        //System.load("C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java\\x64\\opencv_java411");
        
        Mat source=null;
        Mat template=null;
        String filePath=Orient2.workingDirectory;
        String targetPath = filePath+"\\Orient\\"+targetName;
        Orient2.println("targetPath: " + targetPath);
        template=Imgcodecs.imread(targetPath);	
        //Load image file
        //String screenshotPath = filePath+"\\Orient\\s\\screenshot.jpg";
        //Orient2.println("screenshotPath: " + screenshotPath);
        //source=Imgcodecs.imread(screenshotPath);
    
        
        Mat outputImage=new Mat();    
        int machMethod =  Imgproc.TM_CCOEFF_NORMED;
        //Template matching method
        while(true) {
        	Orient2.println("taking screenshot");
	        try {
	            source = screenshot();
	        }catch(CvException | NullPointerException cve) {
	        	System.out.println("\n\tCouldn't take screenshot");
	        	continue;
	        }
	        // anon thread start
        	Orient2.print("matching... ");
	        Imgproc.matchTemplate(source, template, outputImage, machMethod);
	 
	        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
	        Orient2.println("match ratio: " + mmr.maxVal + " vs. " + threshold);
	        Point matchLoc=mmr.maxLoc;
	        if(mmr.maxVal >= threshold) return matchLoc;
	        // anon thread done
        }
        //Draw rectangle on result image
        /*Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(),
                matchLoc.y + template.rows()), new Scalar(0, 255, 0));
        
        Imgcodecs.imwrite(filePath+"\\Orient\\"+resultImgName, source);*/
    }
    
    public static Point screenSearch(String targetName, int threshold) throws IOException {
    	Orient2.println("screenSearch(" + targetName + ")");
    	//System.setProperty("java.library.path", "C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java");
    	//System.loadLibrary("opencv_java411");
        //System.load("C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java\\x64\\opencv_java411");
        
        Mat source=null;
        Mat template=null;
        String filePath=Orient2.workingDirectory;
        String targetPath = filePath+"\\Orient\\"+targetName;
        Orient2.println("targetPath: " + targetPath);
        template=Imgcodecs.imread(targetPath);	
        //Load image file
        //String screenshotPath = filePath+"\\Orient\\s\\screenshot.jpg";
        //Orient2.println("screenshotPath: " + screenshotPath);
        //source=Imgcodecs.imread(screenshotPath);
    
        
        Mat outputImage=new Mat();    
        int machMethod =  Imgproc.TM_CCOEFF_NORMED;
        //Template matching method
        while(true) {
        	Orient2.println("taking screenshot");
	        try {
	            source = screenshot();
				/*DataBufferByte dbbtemp = (DataBufferByte) lastScreenshot.getRaster().getDataBuffer();
				byte[] pixels = dbbtemp.getData();
				 Mat image = new Mat(lastScreenshot.getHeight(), lastScreenshot.getWidth(), CvType.CV_8UC3);
				 image.put(0, 0, pixels);

	            lastScreenshot = new BufferedImage(capture.width, capture.height, BufferedImage.TYPE_3BYTE_BGR);
	            lastScreenshot.getGraphics().drawImage(r.createScreenCapture(capture), 0, 0, null);*/ 
	        }catch(CvException cve) {
	        	System.out.println("\n\tCouldn't take screenshot");
	        }
	        // anon thread start
        	Orient2.print("matching... ");
	        Imgproc.matchTemplate(source, template, outputImage, machMethod);
	 
	        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
	        Orient2.println("match ratio: " + mmr.maxVal);
	        Point matchLoc=mmr.maxLoc;
	        Orient2.println("(" + matchLoc.x + "," + matchLoc.y + ")");
	        if(mmr.maxVal >= threshold) return matchLoc;
	        // anon thread done
        }
        //Draw rectangle on result image
        /*Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(),
                matchLoc.y + template.rows()), new Scalar(0, 255, 0));
        
        Imgcodecs.imwrite(filePath+"\\Orient\\"+resultImgName, source);*/
    }

    public static void screenSearch(String[] imageCommands){
		// add image to perScreenshotSearchQueue
    	// load all the images up ONCE.
		// run the command if image is matched
    	final int numImg = imageCommands.length;
        final String filePath=Orient2.workingDirectory;
        Mat[] templates = new Mat[numImg];
        Mat source=null;
        final int machMethod =  Imgproc.TM_CCOEFF_NORMED;
        final double threshold = ImageAccess.threshold;
        String screenshotPath = filePath+"\\Orient\\s\\screenshot.jpg";
	        
	        // loading up the images
	    	for(int i = 0; i < numImg; i++) {
	    		Orient2.println("working with " + imageCommands[i]);
	    		String targetName = imageCommands[i].split(":")[0];
	        	Orient2.println("Loading up " + targetName);
	            String targetPath = filePath+"\\Orient\\"+targetName;
	            Orient2.println("targetPath: " + targetPath);
	            Orient2.println("\t\t" + imageCommands[i]);
	        	templates[i] = Imgcodecs.imread(targetPath);	
	    	}
	    	
	    	//System.setProperty("java.library.path", "C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java");
	    	//System.loadLibrary("opencv_java411");
	        //System.load("C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java\\x64\\opencv_java411");

	    while(true) {
	        for(int i = 0; i < numImg; i++) {
	        	ImageAccess.threshold = threshold;
	        	while(ControlEngine.threadCount >= ControlEngine.threadLimit) {
	        		if(ControlEngine.threadCount < ControlEngine.threadLimit)
	        			break;
	        	}
	            final int j = i;
	            ControlEngine.threadCount++;
	        	new Thread(() -> {
		        //Load image file
		    	/*boolean waitingOnScreenshot = true;
		    	while(waitingOnScreenshot)
		        try {
		            source = screenshot();
		            waitingOnScreenshot = false;
		        }catch(CvException | IOException cve) {
		        	System.out.println("\n\tCouldn't take screenshot");
		        } */
		        //source=Imgcodecs.imread(screenshotPath);
		        //Orient2.println("screenshotPath: " + screenshotPath);
		        
	        	Orient2.println("\ttrying to find " + imageCommands[j].split(":")[0]);
	        	//Mat template = templates[i];
	        	Mat outputImage=new Mat();  
	            try {
	            	ss = screenshot();
					Imgproc.matchTemplate(ss, templates[j], outputImage, machMethod);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Orient2.println("match failed, could be because screenshot failed, or img4match failed. skipping");
					ControlEngine.threadCount--;
				}
	            MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
	            Orient2.println("\t\tClosest match rate: " + mmr.maxVal);
	            if(mmr.maxVal >= threshold) 
					new Thread(() -> {
			            ControlEngine.threadCount++;
						try {
							new ControlEngine(imageCommands[j].split(":")[1]).run();
				            ControlEngine.threadCount--;
						} catch (NumberFormatException | InterruptedException | AWTException
								| UnsupportedFlavorException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
				            ControlEngine.threadCount--;
						}
					}).start();
	            ControlEngine.threadCount--;
	        	}).start();
	            /*while(!((numThreadsDone != numImg) || (!matchFound))) {
	            	if(numThreadsDone == numImg || matchFound) break;
	            }*/
	        }
    	}
        //Template matching method
 
        //Point matchLoc=mmr.maxLoc;
        //Draw rectangle on result image
        //Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(),
                //matchLoc.y + template.rows()), new Scalar(0, 255, 0));
        
        //Imgcodecs.imwrite(filePath+"\\Orient\\"+resultImgName, source);

        //return matchLoc;
    }
    
    public synchronized static void screenSearch(String[] imageCommandsArg, boolean manyMatchWithClick){
		// add image to perScreenshotSearchQueue
    	// load all the images up ONCE.
		// run the command if image is matched
        final String filePath=Orient2.workingDirectory;
        final int machMethod =  Imgproc.TM_CCOEFF_NORMED;
        //Mat source=null;
    	
        // if it is manyMatchClick, then...
        // manyMatchClick INTdelay DOUBLEthreshold imagename imagename imagename
        /*
         *  this method removes the first element of imagecommands with manyMatchClick
         *  which is the delay
         */
    	ImageAccess.manyMatchWithClick = manyMatchWithClick;
    	if(manyMatchWithClick) {
    		Orient2.println("manyMatchWithClick: " + manyMatchWithClick + " | " + imageCommandsArg[1] + "\t" + imageCommandsArg[2]);
	    	ImageAccess.delay = Integer.valueOf(imageCommandsArg[1]);
	    	ControlEngine.threadLimit = Integer.valueOf(imageCommandsArg[2]);
	    	// just removing the first 2 elements
	    	int tempNumImg = imageCommandsArg.length-3;
	    	String[] temp = new String[tempNumImg];
	    	for(int i = 0;  i < tempNumImg;) {
	    		temp[i] = imageCommandsArg[2 + ++i];
	    	}
	    	imageCommandsArg = new String[tempNumImg];
	    	imageCommandsArg = temp;
    	}
    	final String[] imageCommands = imageCommandsArg;
    	final int numImg = imageCommands.length;
    	
        Mat[] templates = new Mat[numImg];
        
    	alreadyRunning = new boolean[numImg];
    	for(int i = 0; i < numImg; i++) {
    		alreadyRunning[i] = false;
    	}
    	
        //String screenshotPath = filePath+"\\Orient\\s\\screenshot.jpg";
	        
	        // loading up the images
	    	for(int i = 0; i < numImg; i++) {
	    		// it's possible to change the threshold for each following match attempt
	    		// manyMatchClick .72 imagename imagename imagename .81 imagename imagename imagename
	    		//Orient2.println(imageCommands[i]);
	    		if(imageCommands[i].contains(".png") || imageCommands[i].contains(".jpg")) {
		    		Orient2.println("working with " + imageCommands[i]);
		    		String targetName = imageCommands[i].split(":")[0];
		        	Orient2.println("Loading up " + targetName);
		            String targetPath = filePath+"\\Orient\\"+targetName;
		            Orient2.println("targetPath: " + targetPath);
		            Orient2.println("\t\t" + imageCommands[i]);
		        	templates[i] = Imgcodecs.imread(targetPath);	
		        	Orient2.println(templates[i].toString());
	    		}
	    	}
	    	
	    	
	    	
	    	//System.setProperty("java.library.path", "C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java");
	    	//System.loadLibrary("opencv_java411");
	        //System.load("C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java\\x64\\opencv_java411");
            double initialThreshold = ImageAccess.threshold;
	    while(true) {
	        for(int i = 0; i < numImg; i++) {
	        	//Orient2.println("(" + imageCommands[i] + ")");
	        	
	        	while(ControlEngine.threadCount >= ControlEngine.threadLimit) {
	        		if(ControlEngine.threadCount < ControlEngine.threadLimit) {
	        			i--; continue;
	        		}
	        	}
	        	try {
		    		if(imageCommands[i].charAt(0) == '.') {
		    			Orient2.println("\tSetting threshold to " + imageCommands[i]);
			        	initialThreshold = Double.valueOf(imageCommands[i]);
			        	continue;
		    		} else if(!manyMatchWithClick && !imageCommands[i].contains(":"))
		    			continue;
		        } catch(Exception e) {
	    			continue;
	    		}
	        	Mat outputImage=new Mat();  
	            final int j = i;
		        //Load image file
		    	/*boolean waitingOnScreenshot = true;
		    	while(waitingOnScreenshot)
		        try {
		            source = screenshot();
		            waitingOnScreenshot = false;
		        }catch(CvException | IOException cve) {
		        	System.out.println("\n\tCouldn't take screenshot");
		        } */
		        //source=Imgcodecs.imread(screenshotPath);
		        //Orient2.println("screenshotPath: " + screenshotPath);
		        
	        	//Mat template = templates[i];
	            try {
					Thread.sleep(ImageAccess.delay);
				} catch (InterruptedException e1) {
					continue;
				}
	            String temp[] = new String[3];
	            temp[0]= ""; // then case command; NOT USING
	            temp[1] = "1";  // else case command
	            temp[2] = imageCommands[i].replace(":", " ");
	            //if(temp[2].contains("then")) temp[0] = temp[2].substring(temp[2].indexOf("then") + 5);
	            if(temp[2].contains("else")) temp[1] = temp[2].substring(temp[2].indexOf("else") + 5);
	            
	            final String[] thenElse = new String[] {temp[0], temp[1]};
	            final double entryThreshold = initialThreshold;
    			final int attemptAllowance = Integer.valueOf(thenElse[1].split(" ")[0]); // else:1:orient:zz
	            ControlEngine.threadCount++;
					new Thread(() -> {
						int attemptCount = 1;
						
						do {
					    	BufferedImage tempScreenshot = null;
							try {
								//long startTime = System.currentTimeMillis();
								tempScreenshot = screenshot(true);
								if (tempScreenshot == null) Thread.currentThread().interrupt();
								//System.out.println(System.currentTimeMillis() - startTime);
							} catch (IOException e2) {
								// TODO Auto-generated catch block
								Thread.currentThread().interrupt();
							}
				            final BufferedImage lastScreenshot = tempScreenshot;
				            Orient2.println("\t\t\tScreenshot resolution: " + lastScreenshot.getWidth() + "w x " + lastScreenshot.getHeight() + "h");
							//long startTime = System.currentTimeMillis();
				        	Orient2.println("\ttrying to find " + imageCommands[j].split(":")[0]);
							try {
								/*DataBufferByte dbbtemp = (DataBufferByte) lastScreenshot.getRaster().getDataBuffer();
								byte[] pixels = dbbtemp.getData();*/
								 Mat image = img2Mat(lastScreenshot);
								 //image.put(0, 0, pixels);
								 // rowa, rowb, cola, colb
								 //String[] iso = imageCommands[j].split(":");
								 //image = image.submat(Integer.valueOf(iso[1]), Integer.valueOf(iso[3]), Integer.valueOf(iso[0]), Integer.valueOf(iso[2]));
								 
								 
						    	//System.out.println("\t" + (System.currentTimeMillis() - startTime));
								 try {
									 Imgproc.matchTemplate(image, templates[j], outputImage, machMethod);
								 }catch(Exception e){
							         ControlEngine.threadCount--;
									 Thread.currentThread().interrupt();
								 }
						        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
					    		//System.out.println("\t" + (System.currentTimeMillis() - startTime));
					            // -=-=-=
					            /*
					             
	        	Orient2.print("matching... ");
		        Imgproc.matchTemplate(source, template, outputImage, machMethod);
		 
		        if(mmr.maxVal >= threshold) return matchLoc;
					             */
					    		// -=-=-=
						        Point matchLoc=mmr.maxLoc;
						        Orient2.println("\t" + imageCommands[j] + "\n\t\tmatch ratio: " + mmr.maxVal + " vs. " + entryThreshold);
						        Orient2.println("\t\tBest match found at (" + matchLoc.x +  ", " + matchLoc.y + ")");
					            //Orient2.print("\t\tClosest match rate: " + mmr.maxVal);
					            if(mmr.maxVal >= entryThreshold && !alreadyRunning[j]) {
					            	alreadyRunning[j] = true;
					            	if(manyMatchWithClick) {
					            		matchLoc.x = matchLoc.x + (templates[j].width()/2);
					            		matchLoc.y = matchLoc.y + (templates[j].height()/2);
					            		MouseController.niftyClick((int)matchLoc.x, (int)matchLoc.y, (int)matchLoc.x, (int)matchLoc.y, 3); // {xy}1, {xy}2, release delay
						            	alreadyRunning[j] = false;
										 Thread.currentThread().interrupt();
					            	} else {
						            	//String command = "leftClickAt " + (int)matchLoc.x + " " + (int)matchLoc.y + " " + (int)matchLoc.x + " " + (int)matchLoc.y + " 1";
					            		String command = "";
					            		if(!thenElse[1].equals("")) {
					            			
					            		}else {
					            			//for(int repetition = 0; repetition < 4; repetition++)
					            			command = imageCommands[j].replace(":",  " ").substring(imageCommands[j].indexOf(" "));
					            		}
						            	Orient2.println(command);
						            	new ControlEngine(true, command).run();
						            	alreadyRunning[j] = false;
										 Thread.currentThread().interrupt();
					            	}
					            	alreadyRunning[j] = false;
					            }else if(!thenElse[1].equals("Zx 1")) {
					            	
					            }
							} catch (NumberFormatException | InterruptedException | AWTException
									| UnsupportedFlavorException | IOException | ClassCastException | NullPointerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				    		//System.out.println("\t" + (System.currentTimeMillis() - startTime));
				            ControlEngine.threadCount--;
						}while(attemptCount < attemptAllowance);
					}).start();
	            /*while(!((numThreadsDone != numImg) || (!matchFound))) {
	            	if(numThreadsDone == numImg || matchFound) break;
	            }*/
	        }
    	}
        //Template matching method
 
        //Point matchLoc=mmr.maxLoc;
        //Draw rectangle on result image
        //Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(),
                //matchLoc.y + template.rows()), new Scalar(0, 255, 0));
        
        //Imgcodecs.imwrite(filePath+"\\Orient\\"+resultImgName, source);

        //return matchLoc;
    }

    public static void isoScreenSearch(String[] imageCommandsArg){
		// add image to perScreenshotSearchQueue
    	// load all the images up ONCE.
		// run the command if image is matched
        final String filePath=Orient2.workingDirectory;
        final int machMethod =  Imgproc.TM_CCOEFF_NORMED;
        //Mat source=null;
    	
    	final String[] imageCommands = imageCommandsArg;
    	final int numImg = imageCommands.length;
    	
        Mat[] templates = new Mat[numImg];
        
    	alreadyRunning = new boolean[numImg];
    	for(int i = 0; i < numImg; i++) {
    		alreadyRunning[i] = false;
    	}
    	
        //String screenshotPath = filePath+"\\Orient\\s\\screenshot.jpg";
	        
	        // loading up the images
	    	for(int i = 0; i < numImg; i++) {
	    		// it's possible to change the threshold for each following match attempt
	    		// manyMatchClick .72 imagename imagename imagename .81 imagename imagename imagename
	    		if((imageCommands[i].charAt(0) == '.') || !imageCommands[i].contains(":")) {
		        	ImageAccess.threshold = Double.valueOf(imageCommands[i]);
		        	continue;
	    		}
	    		
	    		Orient2.println("working with " + imageCommands[i]);
	    		String targetName = imageCommands[i].split(":")[4];
	        	Orient2.println("Loading up " + targetName);
	            String targetPath = filePath+"\\Orient\\"+targetName;
	            Orient2.println("targetPath: " + targetPath);
	            Orient2.println("\t\t" + imageCommands[i]);
	        	templates[i] = Imgcodecs.imread(targetPath);	
	    	}
	    	
	    	//System.setProperty("java.library.path", "C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java");
	    	//System.loadLibrary("opencv_java411");
	        //System.load("C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java\\x64\\opencv_java411");
            final double initialThreshold = ImageAccess.threshold;
            
            int smallestX = Integer.MAX_VALUE;
            int smallestY = Integer.MAX_VALUE;
            int largestX = 0;
            int largestY = 0;
            
            
            for(int i = 0; i < numImg; i++) {
                if(!imageCommands[i].contains(":")) continue;
            	Orient2.println(imageCommands[i]);
            	String[] ica = imageCommands[i].split(":");
            	int x1 = Integer.valueOf(ica[0]);
            	int y1 = Integer.valueOf(ica[1]);
            	int x2 = Integer.valueOf(ica[2]);
            	int y2 = Integer.valueOf(ica[3]);
            	if(x1 < smallestX) smallestX = x1;
            	if(y1 < smallestY) smallestY = y1;
            	if(x2 > largestX) largestX = x2;
            	if(y2 > largestY) largestY = y2;
            }
            
            final int[] lsxy = new int[] {smallestX, smallestY, largestX, largestY};
            
	    while(true) {
			try {
				//long startTime = System.currentTimeMillis();
				lastScreenshot = screenshot(true, new int[] {smallestX, smallestY, largestX, largestY});
				//System.out.println(System.currentTimeMillis() - startTime);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
	        for(int i = 0; i < numImg; i++) {
        		
	        	double tempThreshold = initialThreshold;
        		if(!(imageCommands[i].contains(":"))) {
        			Orient2.println("Threshold change detected: " + imageCommands[i]);
        			tempThreshold = Double.valueOf("0." + imageCommands[i].replace(".", ""));
        			continue;
        		}
        		
	        	while(ControlEngine.threadCount >= ControlEngine.threadLimit) {
	        		if(ControlEngine.threadCount < ControlEngine.threadLimit)
	        			break;
	        	}
	            final int j = i;
		        //Load image file
		    	/*boolean waitingOnScreenshot = true;
		    	while(waitingOnScreenshot)
		        try {
		            source = screenshot();
		            waitingOnScreenshot = false;
		        }catch(CvException | IOException cve) {
		        	System.out.println("\n\tCouldn't take screenshot");
		        } */
		        //source=Imgcodecs.imread(screenshotPath);
		        //Orient2.println("screenshotPath: " + screenshotPath);
		        
	        	//Mat template = templates[i];
		    	try {
					Thread.sleep(delay);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		if(alreadyRunning[i] == true) continue; 
            	alreadyRunning[j] = true;
	            ControlEngine.threadCount++;
	            final double entryThreshold = tempThreshold;
					new Thread(() -> {
						//long startTime = System.currentTimeMillis();
			        	//Orient2.println("\ttrying to find " + imageCommands[j].split(":")[4]);
						try {
							/*DataBufferByte dbbtemp = (DataBufferByte) lastScreenshot.getRaster().getDataBuffer();
							byte[] pixels = dbbtemp.getData();*/
							 Mat image =  img2Mat(lastScreenshot); //new Mat(lastScreenshot.getHeight(), lastScreenshot.getWidth(), CvType.CV_8UC3);
							 //image.put(0, 0, pixels);
							 // rowa, rowb, cola, colb
							 String[] iso = imageCommands[j].split(":");
							 int coordStringLength = iso[0].length() + iso[1].length() + iso[2].length() + iso[3].length() + iso[4].length() + 5;
							 for(String s : iso) Orient2.print(" iso " + s);
							 Orient2.println("Height/width: " + image.height() + "/" + image.width());
							 if((image.height() != (lsxy[3] - lsxy[1])) && (image.width() != (lsxy[2]-lsxy[0]))) {
								 Orient2.println("\nCropping screenshot");
									 image = image.submat(Integer.valueOf(iso[1]), Integer.valueOf(iso[3]), Integer.valueOf(iso[0]), Integer.valueOf(iso[2]));
									 Orient2.println("Cropping done");
							 }
							 
					    	//System.out.println("\t" + (System.currentTimeMillis() - startTime));
					        Mat outputImage=new Mat();  
							 Orient2.println(Thread.currentThread().getName() + ": looking for " + imageCommands[j]);
				            Imgproc.matchTemplate(image, templates[j], outputImage, machMethod);
				    		//System.out.println("\t" + (System.currentTimeMillis() - startTime));
				    		
				            MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
				            Point matchLoc = mmr.maxLoc;
				            Orient2.println("\t" + imageCommands[j].split(":")[4] + "\n\t\tmatch rate: " + mmr.maxVal + " vs. " + entryThreshold);
				            if(mmr.maxVal >=  entryThreshold) {
				            	if(iso[5].contains("audio")) {
				            		String[] instrArr = imageCommands[j].substring(iso[0].length() + iso[1].length() + iso[2].length() + iso[3].length() + iso[4].length() + 5).split(":");
				                	Orient2.println(instrArr[1] + "||");
				                	String audioClipName = instrArr[1];
				                	int loopCount = 0;
				                	int startTime = 0;
				                	int endTime = 0;
				                	if(instrArr.length > 2) {
				                		Orient2.println("[audio] options detected\n[audio] [" + instrArr[1] + ". " + instrArr[2] + ", " + instrArr[3] + "]");
				                		loopCount = Integer.valueOf(instrArr[1]);
				            	    	startTime = Integer.valueOf(instrArr[2]);
				            	    	endTime = Integer.valueOf(instrArr[3]); 
				                	}

				            		Orient2.println("Searching for audioClipName " + instrArr[1]);
				                	for(int audioClipIndex = AudioController.audioClipNames.size()-1; audioClipIndex >= 0; ) {
				                		if(AudioController.audioClipNames.get(audioClipIndex).contains(audioClipName)) {
				                			Orient2.println(audioClipName + " get!");
				                			AudioController.play(audioClipIndex, loopCount, startTime, endTime);
				                			break;
				                		} else {
				                			Orient2.println(AudioController.audioClipNames.get(audioClipIndex) + " is not " + audioClipName);
				                			audioClipIndex--;
				                		}
				                	}
				            		Orient2.println("Audio command ran.");
				            	}else if(imageCommands[j].contains("clickit")){
				            		matchLoc.x = Integer.valueOf(imageCommands[j].split(":")[0]) + matchLoc.x;
				            		matchLoc.y = Integer.valueOf(imageCommands[j].split(":")[1]) + matchLoc.y;
				                	Orient2.println(imageCommands[j] + "|||" + matchLoc.x + ", " + matchLoc.y);
				            		new ControlEngine(true, "niftyClick " + (int)(matchLoc.x + templates[j].width()/2) + " " + (int)(matchLoc.y + templates[j].height()/2) + " " + (int)(matchLoc.x + templates[j].width()/2) + " " + (int)(matchLoc.y + templates[j].height()/2) + " 3").run();;
				            	}else {
				                	Orient2.println(imageCommands[j] + "||||");
				            		new ControlEngine(true, imageCommands[j].substring(coordStringLength).replace(":", " ")).run();
				            	}
				            	alreadyRunning[j] = false;
					            ControlEngine.threadCount--;
				            	Thread.currentThread().interrupt();
				            }
						} catch (NumberFormatException | InterruptedException | AWTException
								| UnsupportedFlavorException | IOException | ClassCastException | NullPointerException | OutOfMemoryError e) {
							// TODO Auto-generated catch blo
							e.printStackTrace();
			            	alreadyRunning[j] = false;

				            ControlEngine.threadCount--;
			            	Thread.currentThread().interrupt();
						}
			    		//System.out.println("\t" + (System.currentTimeMillis() - startTime));
		            	alreadyRunning[j] = false;

			            ControlEngine.threadCount--;
		            	Thread.currentThread().interrupt();
					}).start();
	            /*while(!((numThreadsDone != numImg) || (!matchFound))) {
	            	if(numThreadsDone == numImg || matchFound) break;
	            }*/
	        }
    	}
        //Template matching method
 
        //Point matchLoc=mmr.maxLoc;
        //Draw rectangle on result image
        //Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(),
                //matchLoc.y + template.rows()), new Scalar(0, 255, 0));
        
        //Imgcodecs.imwrite(filePath+"\\Orient\\"+resultImgName, source);

        //return matchLoc;
    }
    
    // this one is used for multiMatch, target names is perScreenshotSearchQueue
    /*public static Point screenSearch(String[] targetNames) throws IOException {
    	Orient2.println("screenSearch(" + targetNames + ")");
    	//System.setProperty("java.library.path", "C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java");
    	//System.loadLibrary("opencv_java411");
        //System.load("C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java\\x64\\opencv_java411");
    	final int numTargets = targetNames.length;
        Mat source=null;
        Mat[] templates=new Mat[numTargets];
        String filePath=Orient2.workingDirectory;
        String targetPath = filePath+"\\Orient\\"+targetNames;
        Orient2.println("targetPath: " + targetPath);
        //template=Imgcodecs.imread(targetPath);	
        // template array equal to size of targets array... because its gonna load the targets
        
        for(int i = 0; (i < numTargets); i++) {
        	templates[i] = Imgcodecs.imread(filePath + "\\Orient\\" + targets[i])
        }
        
        //Load image file
        try {
            screenshot();
        }catch(CvException cve) {
        	System.out.println("\n\tCouldn't take screenshot");
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			screenSearch(targetNames);
		}
        String screenshotPath = filePath+"\\Orient\\s\\screenshot.jpg";
        Orient2.println("screenshotPath: " + screenshotPath);
        source=Imgcodecs.imread(screenshotPath);
    
        
        Mat outputImage=new Mat();    
        int machMethod =  Imgproc.TM_CCOEFF_NORMED;
        //Template matching method
        Imgproc.matchTemplate(source, templates, outputImage, machMethod);
 
        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
        Orient2.print("" + mmr.maxVal);
        if(mmr.maxVal < threshold) return null;
        Point matchLoc=mmr.maxLoc;
        //Draw rectangle on result image
        Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + templates.cols(),
                matchLoc.y + templates.rows()), new Scalar(0, 255, 0));
        
        Imgcodecs.imwrite(filePath+"\\Orient\\"+resultImgName, source);

        return matchLoc;
    }*/
    

    private static BufferedImage screenshot(boolean b) throws IOException {
			 Orient2.println("\nmanyMatch screenshot(true)");
				long startTime = System.currentTimeMillis();

				// gets the resolution of all combined screens
	            Rectangle capture = new Rectangle(0, 0, 0, 0);
	            for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
	                capture = capture.union(gd.getDefaultConfiguration().getBounds());
	            }
	            lastScreenshot = getScreenshot(capture);
	            return lastScreenshot;
				//final int WIDTH = capture.width;
				//final int HEIGHT = capture.height;
	            // place actual capturing mechanism here
	            
	            // and ofc...
				//BufferedImage thisScreenshot = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
				
				//return thisScreenshot;
				/*
		        try { 
		            Robot r = new Robot(); 
		  
		            // It saves screenshot to desired path 
		            String path = Orient2.workingDirectory + "\\Orient\\s\\screenshot.jpg"; 
		            // Used to get ScreenSize and capture image 
		            /*Rectangle capture =  
		            new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		            lastScreenshot = new BufferedImage(capture.width, capture.height, BufferedImage.TYPE_3BYTE_BGR);
		            lastScreenshot.getGraphics().drawImage(r.createScreenCapture(capture), 0, 0, null); 
		            //ImageIO.write(image, "jpg", new File(path)); 
		            //System.out.println("\nScreenshot saved to: " + path); 
		            //return img2Mat(lastScreenshot);
		        } 
		        catch (AWTException ex) { 
		            System.out.println(ex);
						screenshot(true);
		            //return screenshot();
		    		System.out.println(System.currentTimeMillis() - startTime);
		        }
		        
		        
		        return lastScreenshot;*/
	}

    private static BufferedImage screenshot(boolean b, int[] iso) throws IOException {
			 Orient2.println("\nisoMatch screenshot(true, int[] isolated)");

				// gets the resolution of all combined screens
	            Rectangle capture = new Rectangle(iso[0], iso[1], iso[2]-iso[0], iso[3]-iso[1]);
	            lastScreenshot = getScreenshot(capture);
	            return lastScreenshot;
				//final int WIDTH = capture.width;
				//final int HEIGHT = capture.height;
	            // place actual capturing mechanism here
	            
	            // and ofc...
				//BufferedImage thisScreenshot = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
				
				//return thisScreenshot;
				/*
		        try { 
		            Robot r = new Robot(); 
		  
		            // It saves screenshot to desired path 
		            String path = Orient2.workingDirectory + "\\Orient\\s\\screenshot.jpg"; 
		            // Used to get ScreenSize and capture image 
		            /*Rectangle capture =  
		            new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		            lastScreenshot = new BufferedImage(capture.width, capture.height, BufferedImage.TYPE_3BYTE_BGR);
		            lastScreenshot.getGraphics().drawImage(r.createScreenCapture(capture), 0, 0, null); 
		            //ImageIO.write(image, "jpg", new File(path)); 
		            //System.out.println("\nScreenshot saved to: " + path); 
		            //return img2Mat(lastScreenshot);
		        } 
		        catch (AWTException ex) { 
		            System.out.println(ex);
						screenshot(true);
		            //return screenshot();
		    		System.out.println(System.currentTimeMillis() - startTime);
		        }
		        
		        
		        return lastScreenshot;*/
	}

	public static Point clickSearch(String targetName, int[] offset) throws IOException {
    	Orient2.println("screenSearch(" + targetName + ")");
    	//System.setProperty("java.library.path", "C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java");
    	//System.loadLibrary("opencv_java411");
        //System.load("C:\\Users\\Levi\\Desktop\\Orient\\opencv\\build\\java\\x64\\opencv_java411");
        
        Mat source=null;
        Mat template=null;
        String filePath=Orient2.workingDirectory;
        //Load image file
        try {
            screenshot();
        }catch(CvException cve) {
        	System.out.println("\n\tCouldn't take screenshot");
        }
        String screenshotPath = filePath+"\\Orient\\s\\screenshot.jpg";
        Orient2.println("screenshotPath: " + screenshotPath);
        source=Imgcodecs.imread(screenshotPath);
        String targetPath = filePath+"\\Orient\\"+targetName;
        Orient2.println("targetPath: " + targetPath);
        template=Imgcodecs.imread(targetPath);	
    
        
        Mat outputImage=new Mat();    
        int machMethod =  Imgproc.TM_CCOEFF_NORMED;
        //Template matching method
        Imgproc.matchTemplate(source, template, outputImage, machMethod);
 
        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
        Orient2.print("" + mmr.maxVal);
        if(mmr.maxVal < threshold) return null;
        Point matchLoc=mmr.maxLoc;
        //Draw rectangle on result image
        Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(),
                matchLoc.y + template.rows()), new Scalar(0, 255, 0));
        
        Imgcodecs.imwrite(filePath+"\\Orient\\"+resultImgName, source);

        return matchLoc;
    }
    
    
	public static void screenshot(String saveTo) throws InterruptedException, IOException {
			 Orient2.println("screenshot()");
		        try { 
		            Robot r = new Robot(); 
		  
		            // It saves screenshot to desired path 
		            String path = Orient2.workingDirectory + "\\Orient\\" + saveTo; 
		            Rectangle capture = new Rectangle(0, 0, 0, 0);
		            for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
		                capture = capture.union(gd.getDefaultConfiguration().getBounds());
		            }
		            // Used to get ScreenSize and capture image 
		            /*Rectangle capture =  
		            new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());*/ 
		            BufferedImage image = r.createScreenCapture(capture); 
		            ImageIO.write(image, "jpg", new File(path)); 
		            System.out.println("\nScreenshot saved to: " + path); 
		        } 
		        catch (AWTException | IOException ex) { 
		            System.out.println(ex);
		            Thread.sleep(333);
		            screenshot();
		        }
		    } 

    /*
	public static BufferedImage screenshot() throws IOException {
		 { 
			 Orient2.println("screenshot()");
		        try { 
		            Robot r = new Robot(); 
		  
		            // It saves screenshot to desired path 
		            String path = Orient2.workingDirectory + "\\Orient\\s\\screenshot.jpg"; 
		            Rectangle capture = new Rectangle(0, 0, 0, 0);
		            for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
		                capture = capture.union(gd.getDefaultConfiguration().getBounds());
		            }
		            // Used to get ScreenSize and capture image 
		            /*Rectangle capture =  
		            new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());*
		            lastScreenshot = r.createScreenCapture(capture); 
		            //ImageIO.write(image, "jpg", new File(path)); 
		            //System.out.println("\nScreenshot saved to: " + path); 
		            return img2Mat(lastScreenshot);
		        } 
		        catch (AWTException ex) { 
		            System.out.println(ex);
		            try {
						Thread.sleep(333);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
		            return screenshot();
		        }
		    } 
	}*/
    
	public static Mat screenshot() throws IOException {
		 { 
			 Orient2.println("screenshot()");
		        try { 
		            Robot r = new Robot(); 
		  
		            // It saves screenshot to desired path 
		            String path = Orient2.workingDirectory + "\\Orient\\s\\screenshot.jpg"; 
		            Rectangle capture = new Rectangle(0, 0, 0, 0);
		            for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
		                capture = capture.union(gd.getDefaultConfiguration().getBounds());
		            }
		            // Used to get ScreenSize and capture image 
		            /*Rectangle capture =  
		            new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());*/ 
		            lastScreenshot = r.createScreenCapture(capture); 
		            //ImageIO.write(image, "jpg", new File(path)); 
		            //System.out.println("\nScreenshot saved to: " + path); 
		            return img2Mat(lastScreenshot);
		        } 
		        catch (AWTException ex) { 
		            System.out.println(ex);
		            try {
						Thread.sleep(333);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
		            return screenshot();
		        }
		    } 
	}
	
	public static Mat img2Mat(BufferedImage in) {
		long startTime = System.currentTimeMillis();
		///*
		Mat out = new Mat(in.getHeight(), in.getWidth(), org.opencv.core.CvType.CV_8UC3);
		byte[] data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
		int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
		for (int i = 0; i < dataBuff.length; i++) {
			data[i * 3] = (byte) ((dataBuff[i]));
			data[i * 3 + 1] = (byte) ((dataBuff[i]));
			data[i * 3 + 2] = (byte) ((dataBuff[i]));
		}
		out.put(0, 0, data);
		//*/
		/*
		  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		try {
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		}catch(ClassCastException e) {
			  img2Mat(bi);
		  }*/

			System.out.println(System.currentTimeMillis() - startTime);
		  return out;//mat;
	}

	public static void screenshot(Point mouseCoords, Mat img) throws InterruptedException, IOException {
		 { 
			 Orient2.println("screenshot()");
		        try { 
		            Thread.sleep(120); 
		            Robot r = new Robot(); 
		  
		            // It saves screenshot to desired path 
		            String path = Orient2.workingDirectory + "\\Orient\\s\\screenshot.jpg"; 
		            Rectangle capture = new Rectangle(0, 0, 0, 0);
		            for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
		                capture = capture.union(gd.getDefaultConfiguration().getBounds());
		            }
		            // Used to get ScreenSize and capture image 
		            /*Rectangle capture =  
		            new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());*/ 
		            BufferedImage screenshot = r.createScreenCapture(capture); 
		            ImageIO.write(screenshot, "jpg", new File(path)); 
		            System.out.println("\nScreenshot saved to: " + path); 
		        } 
		        catch (AWTException | IOException | InterruptedException ex) { 
		            System.out.println(ex);
		            Thread.sleep(3000);
		            screenshot();
		        }
		    } 
	}
    
    public static Image getImageFromClipboard()
    {
        Orient2.println("getImageFromClipboard()");
      Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
      if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor))
      {
        try
        {
          return (Image) transferable.getTransferData(DataFlavor.imageFlavor);
        }
        catch (UnsupportedFlavorException | IOException e)
        {
          // handle this as desired
          e.printStackTrace();
        }
      }
      else
      {
        Orient2.println("getImageFromClipboard: That wasn't an image!");
      }
      return null;
    }
    // unused
    /*public static Mat convertBufferedImageToMat(BufferedImage bi) {
    	Orient.println("bufferedImageToMat(BufferedImage)");
    	  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
    	  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    	  mat.put(0, 0, data);
    	  return mat;
    	}
    // unused
    public static BufferedImage convertToBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }*/

    /*
     * assumes jpg if unspecified
     */
	public static Point match(String substring) throws IOException {
		// image to find should come in as the name of the file including extension(?) - optional
		String fileName = extensionCheck(substring);
		// the name of the file must be there,
		// File targetFile;
		// if extension is not provided then the program should search for any file by that name

		Point resultPoint = screenSearch(fileName);
		
		return resultPoint;
	}

	public static Point match(String substring, double threshold) throws IOException {
		// image to find should come in as the name of the file including extension(?) - optional
		String fileName = extensionCheck(substring);
		// the name of the file must be there,
		// File targetFile;
		// if extension is not provided then the program should search for any file by that name

		Point resultPoint = screenSearch(fileName, threshold);
		
		return resultPoint;
	}

	public static void match(String[] imageCommands) throws IOException {
		// image to find should come in as the name of the file including extension(?) - optional
		//String fileName = extensionCheck(substring);
		int iclen = imageCommands.length;
		for(int i = 0; i < iclen; i++) {
			Orient2.println(imageCommands[i]);
			imageCommands[i] = extensionCheck(imageCommands[i].split(":")[0]) + ":" + imageCommands[i].split(":")[imageCommands[i].split(":").length-1];
		}
		// the name of the file must be there,
		// File targetFile;
		// if extension is not provided then the program should search for any file by that name

		screenSearch(imageCommands);
		
		//return resultPoint;
	}
	
	public static void match(String[] imageCommands, boolean manyMatch) throws IOException {
		// image to find should come in as the name of the file including extension(?) - optional
		//String fileName = extensionCheck(substring);
		int iclen = imageCommands.length;
		int i = 0;
		if(manyMatch) i = 3;
		for(; i < iclen; i++) {
			Orient2.println(imageCommands[i]);
			if(imageCommands[i].contains(":"))
				imageCommands[i] = extensionCheck(imageCommands[i].split(":")[0]) + ":" + imageCommands[i].split(":")[imageCommands[i].split(":").length-1];
			else if(manyMatch && !imageCommands[i].contains(".") && !imageCommands[i].contains(".png"))
				imageCommands[i] = extensionCheck(imageCommands[i]);
		}
		// the name of the file must be there,
		// File targetFile;
		// if extension is not provided then the program should search for any file by that name

		screenSearch(imageCommands, manyMatch);
		
		//return resultPoint;
	}
	

	public static void manyIsoMatch(String[] imageCommands) throws IOException {
		// image to find should come in as the name of the file including extension(?) - optional
		//String fileName = extensionCheck(substring);
		int iclen = imageCommands.length;
		boolean isoManyMatch = imageCommands[0].contains("LEVIWUZHERE");
		boolean withClick = imageCommands[1].contains("ORWUZHE");
		final int difference = isoManyMatch?withClick?6:5:4;
		
		int i = isoManyMatch?withClick?6:5:0;
		for(; i < iclen; i++) {
			Orient2.println(imageCommands[i]);
			if((!(imageCommands[i].contains(":"))) && (!isoManyMatch)) {
				Orient2.println("zz Threshold detected");
				continue;
			}
			
			String[] tempArr = imageCommands[i].split(":");
			String tempStr = "";
			int tempImgNameIndex = isoManyMatch?0:4;
			
			tempArr[tempImgNameIndex] = extensionCheck(tempArr[tempImgNameIndex]);
			for(String temp : tempArr) {
				tempStr += ":" + temp;
			}
			
			for(String str : imageCommands)
				Orient2.println(i + i +" ... "+ str);
			
			String coordinates =  imageCommands[2] + ":" + imageCommands[3] + ":" + imageCommands[4] + ":" + imageCommands[4] + ":" + tempStr.substring(1);
			Orient2.println("Adding coordinates: " + coordinates);
			if(isoManyMatch == true) imageCommands[i] = coordinates;
			else imageCommands[i] = tempStr.substring(1);
			if(withClick == true) imageCommands[i] += ":clickit";
			Orient2.println("lol " + tempStr.substring(1));

			for(String str : imageCommands)
				Orient2.println(i + " ... "+ str);
		}

		String[] tempcmds = new String[imageCommands.length-difference];
		if(isoManyMatch) {
			for(i = difference; i < imageCommands.length; i++) {
				String temp = imageCommands[i];
				tempcmds[i-difference] = temp;
			}
		}
		if(isoManyMatch)
			imageCommands = tempcmds;

		for(String str : imageCommands)
			Orient2.println(str);
		
		isoScreenSearch(imageCommands);
		
		//return resultPoint;
	}

	public static Point waitMatch(String substring) throws IOException {
		Point matchFound = null;
		while(matchFound == null) {
			matchFound = match(substring);
		}
		
		return matchFound;
	}
	
	public static Point waitMatch(String substring, boolean x) throws IOException {
		Point matchFound = null;
		while(matchFound == null) {
			matchFound = screenSearch(extensionCheck(substring));
		}
		
		return matchFound;
	}
	
	public static String extensionCheck(String substring) {
		Orient2.println("extensionCheck(" + substring + ")");
		String fileName = "";
		
		if(substring.contains(".")) {
			//targetFile = new File(Orient.workingDirectory + substring); 
			fileName = substring;
		}else {
			fileName = substring + ".jpg";
		}
		
		return fileName;
	}
	
	public static Point getCenterTM(Point matchLoc, String targetName) throws IOException {
		Orient2.println("getCenterTM(Point{" + matchLoc.x + "," + matchLoc.y + "}, " + targetName + ")");
		//matchLoc = waitMatch(targetName);

        String targetPath = Orient2.workingDirectory+"\\Orient\\"+extensionCheck(targetName);
        Orient2.println("targetPath from extensionCheck: " + targetPath);
        File targetFile = new File(targetPath);
        Orient2.println("targetFile absolute path: " + targetFile.getAbsolutePath());
		BufferedImage bimg = ImageIO.read(targetFile);
		int w = bimg.getWidth()/2;
		int h = bimg.getHeight()/2;
		
		Point targetCenter = new Point(matchLoc.x+w, matchLoc.y+h);
		Orient2.println("Target center: " +  targetCenter.toString());
		
		return targetCenter;
	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		Orient2.println("lostOwnership");
	}
	

    public static BufferedImage getScreenshot(Rectangle bounds)
    {
    	
        HDC windowDC = GDI.GetDC(USER.GetDesktopWindow());
        HBITMAP outputBitmap = GDI.CreateCompatibleBitmap(windowDC, bounds.width, bounds.height);
        try
        {
            HDC blitDC = GDI.CreateCompatibleDC(windowDC);
            try
            {
                HANDLE oldBitmap = GDI.SelectObject(blitDC, outputBitmap);
                try
                {
                    GDI.BitBlt(blitDC, 0, 0, bounds.width, bounds.height, windowDC, bounds.x, bounds.y, GDI32.SRCCOPY);
                }
                finally
                {
                    GDI.SelectObject(blitDC, oldBitmap);
                }
                BITMAPINFO bi = new BITMAPINFO(40);
                bi.bmiHeader.biSize = 40;
                boolean ok = GDI.GetDIBits(blitDC, outputBitmap, 0, bounds.height, (byte[]) null, bi, 0x00);
                if (ok)
                {
                    BITMAPINFOHEADER bih = bi.bmiHeader;
                    bih.biHeight = -Math.abs(bih.biHeight);
                    bi.bmiHeader.biCompression = 0;
                    return bufferedImageFromBitmap(blitDC, outputBitmap, bi);
                }
                else
                {
                    return null;
                }
            }
            finally
            {
                GDI.DeleteObject(blitDC);
            }
        }
        finally
        {
            GDI.DeleteObject(outputBitmap);
        }
    }

    private static BufferedImage bufferedImageFromBitmap(HDC blitDC, HBITMAP outputBitmap, BITMAPINFO bi)
    {
        BITMAPINFOHEADER bih = bi.bmiHeader;
        int height = Math.abs(bih.biHeight);
        final ColorModel cm;
        final DataBuffer buffer;
        final WritableRaster raster;
        int strideBits = (bih.biWidth * bih.biBitCount);
        int strideBytesAligned = (((strideBits - 1) | 0x1F) + 1) >> 3;
        final int strideElementsAligned;
        switch (bih.biBitCount)
        {
            case 16:
                strideElementsAligned = strideBytesAligned / 2;
                cm = new DirectColorModel(16, 0x7C00, 0x3E0, 0x1F);
                buffer = new DataBufferUShort(strideElementsAligned * height);
                raster = Raster.createPackedRaster(buffer, bih.biWidth, height, strideElementsAligned, ((DirectColorModel) cm).getMasks(), null);
                break;
            case 32:
                strideElementsAligned = strideBytesAligned / 4;
                cm = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);
                buffer = new DataBufferInt(strideElementsAligned * height);
                raster = Raster.createPackedRaster(buffer, bih.biWidth, height, strideElementsAligned, ((DirectColorModel) cm).getMasks(), null);
                break;
            default:
                throw new IllegalArgumentException("Unsupported bit count: " + bih.biBitCount);
        }
        final boolean ok;
        switch (buffer.getDataType())
        {
            case DataBuffer.TYPE_INT:
            {
                int[] pixels = ((DataBufferInt) buffer).getData();
                ok = GDI.GetDIBits(blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0);
            }
                break;
            case DataBuffer.TYPE_USHORT:
            {
                short[] pixels = ((DataBufferUShort) buffer).getData();
                ok = GDI.GetDIBits(blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0);
            }
                break;
            default:
                throw new AssertionError("Unexpected buffer element type: " + buffer.getDataType());
        }
        if (ok)
        {
            return new BufferedImage(cm, raster, false, null);
        }
        else
        {
            return null;
        }
    }

    private static final User32 USER = User32.INSTANCE;

    private static final GDI32 GDI = GDI32.INSTANCE;

}

interface GDI32 extends com.sun.jna.platform.win32.GDI32
{
    GDI32 INSTANCE = (GDI32) Native.loadLibrary(GDI32.class);

    boolean BitBlt(HDC hdcDest, int nXDest, int nYDest, int nWidth, int nHeight, HDC hdcSrc, int nXSrc, int nYSrc, int dwRop);

    HDC GetDC(HWND hWnd);

    boolean GetDIBits(HDC dc, HBITMAP bmp, int startScan, int scanLines, byte[] pixels, BITMAPINFO bi, int usage);

    boolean GetDIBits(HDC dc, HBITMAP bmp, int startScan, int scanLines, short[] pixels, BITMAPINFO bi, int usage);

    boolean GetDIBits(HDC dc, HBITMAP bmp, int startScan, int scanLines, int[] pixels, BITMAPINFO bi, int usage);

    int SRCCOPY = 0xCC0020;
}

interface User32 extends com.sun.jna.platform.win32.User32
{
    User32 INSTANCE = (User32) Native.loadLibrary(User32.class, W32APIOptions.UNICODE_OPTIONS);

    HWND GetDesktopWindow();
}

/*
 * Direction
 * Reactivity
 * Vicinity
 * Mimicry
 * */
