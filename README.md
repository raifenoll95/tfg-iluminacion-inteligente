![alt text](https://i.ibb.co/MNF93VD/hub.png)
# Diseño de sistemas de iluminación inteligente para el desarrollo de ciudades sostenibles
Trabajo de fin de grado realizado durante el curso 2018/2019

## Objetivo
El objetivo ha sido la propuesta de un diseño de sistema inteligente que aproveche las capacidades del paradigma IoT para sensorizar a peatones en la vía pública.

## Funcionamiento
Para poder sensorizar se ha utilizado una cámara que realiza fotografías cada x tiempo, siento este programable. La cámara está conectada a una raspberry pi, siendo este el elemento que dota a la cámara de "IoT".
La raspberry pi le va enviando las imágenes tomadas por la cámara a un servidor, por sockets.
El servidor recibe dichas fotografías, las procesa para detectar la presencia de peatones y le avisa a la raspberry pi cuando ha detectado un peatón, momento en el cuál la raspberry ilumina el led.

## Prerrequisitos

## Despliegue
