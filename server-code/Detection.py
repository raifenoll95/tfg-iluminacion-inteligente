#Raimundo Fenoll Albaladejo. Detection Humans.
#Código que detecta la presencia de peatones en imágenes.
#Compilacion -> python3 detection.py

"""
This code analize frames and detect people. It can detects others parameters like cars,bus,umbrellas,bags,etc with a high percent of probality.
Detector catch all the frames that server receive from raspberrypi. This frames are save in /home/rfa21/Documentos/gitgubtfg/objectDetection.
Then detector detects humans (custom_objects = detector.CustomObjects(person=True)) from images
and finally save them in an another directory /home/rfa21/Documentos/gitgubtfg/objectDetection/processcam1
"""

from imageai.Detection import ObjectDetection
from tkinter import *

import os
import sys
import cv2
import time

resnet50_path = "/home/rfa21/Escritorio/tfg/tfg/objectDetection/yolo.h5" #RUTA DEL FICHERO YOLO.h5
images_path = "/home/rfa21/Escritorio/tfg/tfg/objectDetection/street1/" #RUTA DE DONDE COGE LAS IMAGENES A PROCESAR
images_save_path = "/home/rfa21/Escritorio/tfg/tfg/objectDetection/processcam1/" #RUTA DONDE GUARDA LAS IMAGENES PROCESADAS
image_wait_path = "/home/rfa21/Escritorio/tfg/tfg/objectDetection/waitImage/wait.png" #AQUI TENGO LA IMAGEN QUE SE MUESTRA MIENTRAS LE VAN LLEGANDO IMAGENES

NUM_MAX_IMAGES = 26

#Queue (No se usa al final)
class Cola:

	def __init__(self):

		self.items = []

	def encolar(self, x):

		self.items.append(x)

	def desencolar(self):

		try:
			return self.items.pop(0)

		except:
			raise ValueError("La cola está vacia")

	def es_vacia(self):

		return self.items == []

	def longitud(self):

		return len(self.items)


#Inicializar el detector de humanos en screenshots
def start_detector():

	starting_point = time.time() #Save actual time

	detector = ObjectDetection()
	detector.setModelTypeAsYOLOv3()
	detector.setModelPath(resnet50_path)
	detector.loadModel()

	elapsed_time = time.time() - starting_point #Time detector has taken
	elapsed_time_seconds = elapsed_time % 60
	print(elapsed_time_seconds,"seconds detector has taken")

	return detector


#Deteccion de humanos en screenshots
def detect_humans(detector,custom_objects):

	#create .txt for history of humans detects
	file = open(images_save_path + "history.txt","w")

	#creamos una cola
	cola1 = Cola()

	cont = 1 #num_images

	while True:

		name_file = "screenshot" + str(cont) + ".png" #Archivo que va a buscar ahora

		if cont%5==0:
			cv2.destroyAllWindows();

		#Cuando se haya encontrado el fichero
		if findImage(name_file):

			num_persons = 0 #Numero de personas en cada imagen
			time.sleep(0.10)

			starting_point_image = time.time()
			detections = detector.detectCustomObjectsFromImage(custom_objects=custom_objects, input_image=images_path + name_file, output_image_path=images_save_path + name_file)
			
			for person in detections:
					num_persons += 1

			#Rename file if person > 1 ->yes, else no
			if num_persons >= 1:
				old_file = os.path.join(images_save_path, name_file)
				new_file = os.path.join(images_save_path, "screenshot" + str(cont) + "yes.png")
				os.rename(old_file,new_file)

				final_point_image = time.time() - starting_point_image
				print("Detecting screenshot" + str(cont),".png from camera1 ",final_point_image, "seconds in process. " + str(num_persons) + " humans detects.")

				history(file,cont,num_persons,final_point_image) #Dejamos constancia en el historial

				#Vamos mostrando por pantalla las imagenes con la deteccion
				frame = cv2.imread(images_save_path + "screenshot" + str(cont) + "yes.png",1) #leemos la imagen
				cv2.namedWindow('monitor: screenshot' + str(cont) + '.png') #Renombramos la ventana
				cv2.moveWindow('monitor: screenshot' + str(cont) + '.png',350,150) #Movemos la ventana
				frameS = cv2.resize(frame,(700,450)) #Redimensionamos la imagen
				cv2.imshow('monitor: screenshot' + str(cont) + '.png', frameS) #Mostramos imagen

			else:

				old_file = os.path.join(images_save_path, name_file)
				new_file = os.path.join(images_save_path, "screenshot" + str(cont) + "no.png")
				os.rename(old_file,new_file)

				final_point_image = time.time() - starting_point_image
				#print("Image " + str(cont) , ":" ,final_point_image,"seconds in process. " + str(num_persons) + " humans detects.")
				print("Detecting screenshot" + str(cont),".png from camera1 ",final_point_image, "seconds in process. " + str(num_persons) + " humans detects.")

				history(file,cont,num_persons,final_point_image) #Dejamos constancia en el historial

				#Vamos mostrando por pantalla las imagenes con la deteccion
				frame = cv2.imread(images_save_path + "screenshot" + str(cont) + "no.png",1) #leemos la imagen
				cv2.namedWindow('monitor: screenshot' + str(cont) + '.png') #Renombramos la ventana
				cv2.moveWindow('monitor: screenshot' + str(cont) + '.png',350,150) #Movemos la ventana
				frameS = cv2.resize(frame,(700,450)) #Redimensionamos la imagen
				cv2.imshow('monitor: screenshot' + str(cont) + '.png', frameS) #Mostramos imagen

			#En este punto puede que ya haya otra imagen, por lo que debemos darnos prisa en mostrarla, si no se acumula el tiempo
			if findNextImage("screenshot" + str(cont+1) + ".png"):

				key = cv2.waitKey(50)

				if key == 27:
					cv2.destroyAllWindows() #Cancelamos monitoreo si pulsamos escape

			#Vamos cumpliendo con el tiempo establecido
			else:

				key = cv2.waitKey(400)

				if key == 27:
					cv2.destroyAllWindows() #Cancelamos monitoreo si pulsamos escape

			cont += 1

		else:

			frame = cv2.imread(image_wait_path,1) #leemos la imagen de espera
			cv2.namedWindow('monitor') #Renombramos la ventana
			cv2.moveWindow('monitor', 350,150) #Movemos la ventana
			frameS = cv2.resize(frame,(700,450)) #Redimensionamos la imagen
			cv2.imshow('monitor', frameS) #Mostramos imagen
			key = cv2.waitKey(1000) #Muestra la imagen de carga

			if key == 27:

				cv2.destroyAllWindows() #Cancelamos monitoreo si pulsamos escape
				break		

	#close file
	file.close()


#Guarda en un fichero de texto el historial de cuantas personas ha detectado en una screenshot
def history(file,cont,num_persons,final_point_image):

	if num_persons > 0:
		file.write("Image " + str(cont) + ": LIGHTS ON. " + str(num_persons) + " humans detects. " + str(final_point_image) + " seconds in process" + os.linesep)
	else:
		file.write("Image " + str(cont) + ": LIGHTS OFF. " + str(num_persons) + " humans detects. " + str(final_point_image) + " seconds in process" + os.linesep)


#Se encarga siempre que solo hayan un numero de imagenes para no sobrecargar el servidor
def manejar_cola(cola,file):

	#Mete elementos hasta que se llene la cola
	if cola.longitud() <= NUM_MAX_IMAGES:
		cola.encolar(file)

	else:
		image_to_delete = cola.desencolar()
		delete_image(images_save_path + image_to_delete)
		cola.encolar(file)


#Enuentra una imagen en el directorio
def findImage(nameFile):

	found = False

	#Esta en bucle hasta que encuentra el fichero
	while found==False:

		for root, dirs, files in os.walk(images_path):

			if nameFile in files:
				found = True
		return found

#Enuentra la siguiente imagen en el directorio
def findNextImage(nameFile):

	found = False

	#Esta en bucle hasta que encuentra el fichero
	while found==False:

		for root, dirs, files in os.walk(images_path):

			if nameFile in files:
				found = True
		return found


#Delete a image
def delete_image(path):

    if os.path.isdir(path):
        print("Imposible borrar {0}!. Es una carpeta.".format(path))

    elif os.path.isfile(path):  
        try:
            os.remove(path)

        except e:
            print ("Error: %s - %s." % (e.filename,e.strerror))

    else:  
        print("Error. No se ha encontrado {0}.".format(path))


#Method principal
def main():

	#start detector
	detector = start_detector()
	#We want detect only humans
	custom_objects = detector.CustomObjects(person=True)
	#detect humans
	detect_humans(detector,custom_objects)


#Program starts
if __name__ == "__main__":
	main()


