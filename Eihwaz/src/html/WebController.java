package html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Scanner;

import orient.Orient2;
//import org.apache.commons.codec.binary.Base64;

public class WebController {
	private final String USER_AGENT = "Mozilla/5.0";
	private static Scanner ui;
	
	/*public static void prep(Object obj) {
		ui = new Scanner(obj);
	}*/
	
	public void sendPost() {
		String stringUrl = "https://dccc8f5928404270a2f1ef8187e9ce11.us-central1.gcp.cloud.es.io:9243/goto/f71b88b9637a2c7295ba231d5cb753aa";
		try {
            URL url = new URL(stringUrl);
            String userpass = "";
            System.out.print("Username: ");
            userpass = getUserInput();
            System.out.print("Password: ");
            userpass += ":" + getUserInput();
            String encoding = Base64.getEncoder().encodeToString((userpass).getBytes("UTF-8"));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoding);
            InputStream content = (InputStream)connection.getInputStream();
            BufferedReader in   = 
                new BufferedReader (new InputStreamReader (content));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	public void sendCURLGet() throws IOException {
        //prep(System.in);
		ui = new Scanner(System.in);
		String stringUrl = "https://dccc8f5928404270a2f1ef8187e9ce11.us-central1.gcp.cloud.es.io:9243/goto/f71b88b9637a2c7295ba231d5cb753aa";
		URL url = new URL(stringUrl);
        URLConnection uc = url.openConnection();

        uc.setRequestProperty("X-Requested-With", "Curl");
        String userpass = "";
        
        System.out.print("Username: ");
        userpass = getUserInput();
        System.out.print("Password: ");
        userpass += ":" + getUserInput();
        
        //String userpass = "gstevens:QCz9n4tTgWS";
        if(Orient2.DEBUG) System.out.println(userpass); 
        String basicAuth = "Basic " + new String((Base64.getEncoder().encodeToString(userpass.getBytes("utf-8"))));
        uc.setRequestProperty("Authorization", basicAuth);

        //InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(uc.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
        // read this input
	}
	
	private String getUserInput() throws IOException {
		String containerString = "";
		containerString = ui.nextLine();
		
		String fileName = "ReadMe";
		PrintWriter pw = new PrintWriter(new FileWriter(Orient2.workingDirectory + "\\Orient\\" + fileName + ".txt", true));
		
		System.out.println(Orient2.workingDirectory + " and " + containerString);
		pw.println(containerString);
		
		pw.close();
		
		return containerString;
	}

	// HTTP GET request
	public void sendGet() throws Exception {

		String url = "https://dccc8f5928404270a2f1ef8187e9ce11.us-central1.gcp.cloud.es.io:9243/goto/f71b88b9637a2c7295ba231d5cb753aa";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());

	}
}
