//Raimundo Fenoll Albaladejo. 
//Hilo del servidor. Se ejecuta 1 hilo por camara.

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

public class HiloServidor extends Thread {

	private Socket cliente;
	private String ruta_destino;
    private int num_thread;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;

    //Constructor con parametros
	public HiloServidor(Socket cliente, String ruta_destino, int num_thread) {

		this.cliente = cliente;
		this.ruta_destino = ruta_destino;
        this.num_thread = num_thread;
	}

    //Recibe imagenes de la raspberry
	public void recieveImages(Socket cliente, String ruta_destino) {

        int imagecont = 1;

        while(true) {
		
    		try {
     
                InputStream inputStream = cliente.getInputStream(); //Lo que se recibe del cliente (inpust stream)
                DataOutputStream outputStream;

                if(inputStream != null) {

                    byte[] sizeAr = new byte[4];
                    inputStream.read(sizeAr);
                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                    Thread.sleep(150);
                    /*
                    Sometimes the server receive the images bad cause there is a
                    low error rate, and the image is not written well
                    */
                    if(size > 1000000) {

                        byte[] imageAr = new byte[size];
                        inputStream.read(imageAr);
             
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

                        //image must be not null
                        if (image!=null) {

                            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

                            for (int x = 0; x < image.getWidth(); x++) {
                                for (int y = 0; y < image.getHeight(); y++) {
                                    newImage.setRGB(x, y, image.getRGB(x, y));
                                }
                            }

                            ImageIO.write(newImage, "jpg", new File(ruta_destino + "/screenshot" + imagecont + ".png"));
                            //Writting Image at
                            LocalDateTime currentDataTime = LocalDateTime.now();
                            String date = currentDataTime.format(dtf);

                            System.out.println("Writing screenshot" + imagecont + ".png from camera" + num_thread + " | " + date + " | " + "Info: OK " + size);
                            
                            //Enviamos confirmacion al cliente
                            //this.checkImageResult(imagecont);

                            imagecont++;
                        }
                    }
                }

                inputStream = null;
            }

            catch (UnknownHostException e){
                System.out.println(e);
            } 
            catch (IOException e) {
                System.out.println(e);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }    
	}

	public void run() {

        File folder = new File(ruta_destino);
        folder.mkdir();
		recieveImages(cliente,ruta_destino);
	}
}
