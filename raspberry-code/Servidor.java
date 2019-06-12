//Raimundo Fenoll Albaladejo.
//Raspberry actuando como Servidor (recibiendo si se ha detectado peatones o no)

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

public class Servidor {
	
	//Recibe confirmaciones de la raspberry
	public static void recieveConfirmations(Socket cliente) throws InterruptedException{

        int imagecont = 0;     
        BufferedWriter bw;
        
        while(true) {
			
			DataInputStream input = null;
		
    		try {
     
                input = new DataInputStream(cliente.getInputStream());
                String message = input.readUTF();
                System.out.println(message);
                
                final GpioController gpio = GpioFactory.getInstance();
				final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLed",PinState.HIGH);
				pin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
				
				if(message!=null && message.contains("fail")) {
					
					pin.low();
					Thread.sleep(300);
					gpio.shutdown();
					gpio.unprovisionPin(pin);
				}
				
				else {
					
					pin.high();
					Thread.sleep(300);
					pin.low();
					gpio.shutdown();
					gpio.unprovisionPin(pin);
				}
            }

            catch (UnknownHostException e){
                System.out.println(e);
            } 
            catch (IOException e) {
                System.out.println(e);
            }
        }    
	}

	public static void main(String[] args) {

		String port = ""; //Puerto de escucha del servidor

		try {

			port = "2000";
			ServerSocket servidor = new ServerSocket(Integer.parseInt(port)); //Creamos un socket con el servidor
			System.out.println("Escuchando el puerto " + port);
			int num_calle = 1;
			int num_thread = 0;

			//Ahora somos capaces de atender muchas peticiones
			for(;;) {

				Socket cliente = servidor.accept(); //Aceptamos peticion del cliente
				
				try {
					recieveConfirmations(cliente);
				}
		
				catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		catch(Exception e) {

			System.out.println("Error: " + e.toString());
		}
	}
}
