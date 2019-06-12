//Raimundo Fenoll Albaladejo. Diseño de Sistemas de Iluminación Inteligente
//CODIGO QUE LA RASPBERRY USA PARA ENVIAR IMAGENES AL SERVIDOR
//Compilacion -> javac ClienteTFG.java
//Ejecucion -> java ClienteTFG ip_servidor puerto_servidor carpeta_donde_se_almacenan_las_fotografias_de_la_camara 

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

public class ClienteTFG {
	
	private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME; 

	public static void sendImages(String ip_servidor, String puerto_servidor, String folder) {

		try {

			Socket clientSocket = new Socket(ip_servidor, Integer.parseInt(puerto_servidor)); //Nos creamos un socket en el cliente
			int imagecont = 1;
			
			System.out.println("Cliente preparado para enviar imágenes...");

			while(true) {

				boolean image_found = false;
				String date ="";
				boolean success = false;

				OutputStream outputStream = clientSocket.getOutputStream();
	            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); //ALMACENAREMOS AQUI LA CAPTURA EN BYTES

	            //scrypt.py is slowly, so wait if the image dont exists yet
	            File f = new File(folder + "/screenshot" + imagecont + ".jpg");

	            if(f.exists()) {

	            	image_found = true;
	            	
	            	//Necesitamos saber en que momento el sistema ha detecado una imagen
	            	LocalDateTime now = LocalDateTime.now();
	            	date = now.format(dtf);
	            	
	            }
	            
	            while(!f.exists()) {
					
					if(f.exists()) {

						image_found = true;
	            	
						//Necesitamos saber en que momento el sistema ha detecado una imagen
						LocalDateTime now = LocalDateTime.now();
						date = now.format(dtf);
					}
				}
	
				
				Thread.sleep(150);
				BufferedImage image = ImageIO.read(new File(folder + "/screenshot" + imagecont + ".jpg")); //Leemos de la ruta

	            if(image!=null) {

		            ImageIO.write(image, "jpg", byteArrayOutputStream); //LEEMOS DEL BUFFER Y ALMACENAMOS EN BYTEARRAYOUTPUTSTREAM LA CAPTURA
					
		            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
		            outputStream.write(size); //ASIGNAMOS EL ESPACIO QUE DEBE DE TENER 
		            outputStream.write(byteArrayOutputStream.toByteArray()); //AQUI ESCRIBIMOS EN EL SOCKET LA IMAGEN 

		            outputStream.flush(); //FLUSHED PARA ESTAR SEGUROS DE QUE SE HA ESCRITO TODO Y NO SE HA ALMACENADO NADA

		            System.out.println("Sending " + "screenshot" + imagecont + ".png | " + date);
		            imagecont++;
		        }
	        }
		}

		catch (UnknownHostException e){
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println("Server is disconnected");
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	
	//Lee lo que le envia el servidor
	public static String leeSocket(Socket clientSocket, String confirma) {
		
		try {
			
			InputStream aux = clientSocket.getInputStream();
			DataInputStream flujo = new DataInputStream(aux);
			confirma = flujo.readUTF();
		}
		
		catch(Exception e) {
			
			System.out.println("Error: " + e.toString());
		}
		
		return confirma;
	}


	public static void main(String[] args) {

		ClienteTFG cl = new ClienteTFG();

		//Se le debe pasar la ip del cliente y el puerto del servidor
		if (args.length < 2) {
			System.out.println ("Debe indicar la direccion del servidor y el puerto");
			System.out.println ("$./Cliente nombre_servidor puerto_servidor");
			System.exit(-1);
		}

		String ip_servidor = args[0]; //Ip del servidor
		String puerto_servidor = args[1]; //Puerto del servidor
		String folder = args[2]; //Carpeta de donde va a enviar las imagenes

		sendImages(ip_servidor, puerto_servidor, folder);
	}
}
