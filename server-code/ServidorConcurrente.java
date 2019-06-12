//Raimundo Fenoll Albaladejo.
//Codigo Servidor Concurrente
//Crea un hilo por cada conexion
//Recibe imágenes enviadas por la raspberry pi
//Compilacion -> javac ServidorConcurrente.java HiloServidor.java
//Ejecucion -> java ServidorConcurrete puerto_servidor

//NOTA: En ruta_destino especificar la ruta donde se van a guardar las imagenes recibidas

import java.net.*;
import java.io.*;

public class ServidorConcurrente {

	public static void main(String[] args) {

		String port = ""; //Puerto de escucha del servidor

		try {

			//Se le debe pasar obligatoriamente el puerto de escucha del servidor
			if(args.length < 1) {

				System.out.println("Debe indicar el puerto de escucha del servidor");
				System.out.println("$./Servidor puerto_servidor");
				System.exit(1);
			}

			port = args[0];
			ServerSocket servidor = new ServerSocket(Integer.parseInt(port)); //Creamos un socket con el servidor
			System.out.println("Escuchando el puerto " + port);
			int num_calle = 1;
			int num_thread = 0;

			//Ahora somos capaces de atender muchas peticiones
			for(;;) {

				Socket cliente = servidor.accept(); //Aceptamos peticion del cliente
				num_thread++;
				String ruta_destino = "/home/rfa21/Escritorio/tfg/tfg/objectDetection/street" + num_calle;
				System.out.println("Preparing to receive screenshots from camera: " + num_calle);
				Thread hilo = new HiloServidor(cliente,ruta_destino,num_thread);
				hilo.start(); //Comenzamos el hilo
				num_calle++;
			}
		}

		catch(Exception e) {

			System.out.println("Error: " + e.toString());
		}
	}
}
