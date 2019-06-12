#Raimundo Fenoll Albaladejo. Trabajo Fin Grado.
#Codigo que realiza fotografías con la cámara cada segundo y las va guardando en la ruta especificada
#NOTA: La ruta folder es en donde se irán almacenando dichas fotografías realizadas

import io
import time
from picamera import PiCamera
from datetime import datetime

#Camera parameters
camera = PiCamera()
camera.resolution = (640, 480) #640x180 Píxeles
camera.framerate = 80

screenshot = 1 #Inicialice camera parameters
folder = '/home/pi/Desktop/tfg/tfg/codigo-tfg-cliente-servidor/enviar/street1/'

while True:
    
    if screenshot==1:
        print("Taking images...")
    
    camera.capture(folder + 'screenshot' + str(screenshot) + '.jpg')
    
    time.sleep(0.7)
    print("Capturing screenshot" + str(screenshot) + ".jpg at " + time.strftime("%c"))
    screenshot += 1