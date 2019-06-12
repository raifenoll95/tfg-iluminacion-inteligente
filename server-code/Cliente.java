//Raimundo Fenoll Albaladejo.
//Ahora el servidor es quien debe decir al cliente (raspberry) que se ha encontrado un peaton en la via

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.lang.Math;

public class Cliente {

	static String location_path = "/home/rfa21/Escritorio/tfg/tfg/objectDetection/processcam1/"; //RUTA DONDE ESTAN LAS IMAGENES YA PROCESADAS

	//Send confirmation to Raspberry Pi
	public static void sendResponse(Socket clientSocket, int imagecont) {

		try{
		
			DataOutputStream out= new DataOutputStream(clientSocket.getOutputStream());
			out.writeUTF("ok"+ Integer.toString(imagecont));
			System.out.println("Se ha enviado un ok" + Integer.toString(imagecont));
		}

		catch(Exception e) {

			System.out.println("Error: " + e.toString());
		}

		return;
	}

	//Send confirmation to Raspberry Pi
	public static void sendNoResponse(Socket clientSocket, int imagecont) {

		try{
		
			DataOutputStream out= new DataOutputStream(clientSocket.getOutputStream());
			out.writeUTF("fail"+ Integer.toString(imagecont));
			System.out.println("Se ha enviado un false" + Integer.toString(imagecont));
		}

		catch(Exception e) {

			System.out.println("Error: " + e.toString());
		}

		return;
	}

	public static boolean checkDection(int imagecont) {

		File f1 = new File(location_path + "screenshot" + imagecont + "yes.png");
		File f2 = new File(location_path + "screenshot" + imagecont + "no.png");

		boolean found = false;
		boolean isPeople = false;

		while(true && !found) {

			if(f1.exists()) {
				
				System.out.println("Found People in image " + imagecont);
				found = true;
				isPeople = true;	
        	}

        	if(f2.exists()) {
				
				System.out.println("Don't found People in image " + imagecont);
				found = true;
				isPeople = false;
        	}
		}

		return isPeople;
	}

	//Check if objectDetection is true
	public static void sendDetections(String ip, String puerto) {

		try {

			Socket clientSocket = new Socket(ip, Integer.parseInt(puerto));
			System.out.println("Ready to send confirmations...");
			int imagecont = 1;

			//Send confirmations all time
			while(true) {

				if(checkDection(imagecont)) {

					sendResponse(clientSocket,imagecont);
				}

				else{
					sendNoResponse(clientSocket,imagecont);
				}

				imagecont++;
			}
		}

		catch(UnknownHostException e) {

			System.out.println(e);
		}

		catch(IOException e) {

			System.out.println("Server is disconnected");
		}
	}


	public static void main(String[] args) {

		Cliente cliente = new Cliente();

		if(args.length < 1) {
			System.out.println("Debe Indicar la direccion de host y puerto correctamente");
			System.exit(-1);
		}

		String ip = args[0];
		String puerto = args[1];
		sendDetections(ip,puerto); 
	}
}