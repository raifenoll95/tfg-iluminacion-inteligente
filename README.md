<p align="center">
<img src="https://i.ibb.co/2q37jgD/Captura.png" width="650">
</p>


## Objetivo
El objetivo ha sido la propuesta de un diseño de sistema inteligente que aproveche las capacidades del paradigma IoT para sensorizar a peatones en la vía pública y poder controlar las luminarias.

## Funcionamiento
Para poder sensorizar se ha utilizado una cámara que realiza fotografías cada x tiempo, siendo este programable. La cámara está conectada a una raspberry pi, elemento que dota a la cámara de "IoT".
La raspberry pi le va enviando las imágenes tomadas por la cámara a un servidor, por sockets.
El servidor recibe dichas fotografías, las procesa para detectar la presencia de peatones y le avisa a la raspberry pi cuando ha detectado un peatón, momento en el cuál la raspberry ilumina el led, el cual simula a una luminaria.

## Prerrequisitos
### Prerrequisitos en la raspberry pi
- Java JDK (Viene instalado en raspbian)
- Python3 (viene instalado en raspbian)
- Pi4j -> https://pi4j.com/1.2/install.html
- picamera -> https://picamera.readthedocs.io/en/release-1.12/install.html
- Configurar la cámara -> https://projects.raspberrypi.org/en/projects/getting-started-with-picamera/4 

### Prerrequisitos en el servidor
- Java JDK -> https://docs.oracle.com/javase/8/docs/technotes/guides/install/linux_jdk.html
- Python3 -> https://www.python.org/download/releases/3.0/
- OpenCV -> https://www.learnopencv.com/install-opencv3-on-ubuntu/
- ImageAI -> Instalación de todo lo necesario para usar yolo.h5 -> https://github.com/OlafenwaMoses/ImageAI
- yolo.h5 -> https://mega.nz/#!j4kBGIJB!M3k-zDlQXEdEAXZZc3frGZxi1dkw6zuxovfra8e3dnk

## Despliegue

### Servidor (Iniciamos el servidor, estamos a la espera para detectar peatones)
1. ``` javac ServidorConcurrente.java HiloServidor.java ```
2. ``` java ServidorConcurrente 9999 ```
3. ``` python3 Detection.py ```

### Raspberry Pi (Ejecutamos servidor para recibir confirmaciones, ejecutamos cliente para empezar a enviar imagenes a servidor)
4. ``` javac Servidor.java ```
5. ``` java Servidor 8888 ```
6. ``` javac Cliente.java ```
7. ``` java Cliente ip_servidor 9999 ```

### Servidor (Ponemos el cliente preparado)
8. ``` javac Cliente.java ```
9. ``` java Cliente ip_raspberry 8888 ```

### Raspberry Pi (Empezamos a hacer fotografías!!!!)
10. ``` python3 camera.py ```

## Autor
Raimundo Fenoll Albaladejo

## Tutor
Higinio Mora Mora
