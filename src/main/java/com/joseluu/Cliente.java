package com.joseluu;

import java.net.*;
import java.util.Scanner;

public class Cliente {

    private static final int PUERTO = 9876;

    public static void main(String[] args) {

        try (DatagramSocket socket = new DatagramSocket()) {

            InetAddress servidor = InetAddress.getByName("localhost");
            Scanner sc = new Scanner(System.in);

            // =========================
            // 1️⃣ Cliente inicia conversación
            // =========================
            String saludo = "que tal";
            enviar(socket, saludo, servidor, PUERTO);
            System.out.println("Cliente envía: " + saludo);

            // =========================
            // 2️⃣ Recibe respuesta del servidor
            // =========================
            String respuesta = recibir(socket);
            System.out.println("Servidor responde: " + respuesta);

            // =========================
            // 3️⃣ Cliente responde por última vez
            // =========================
            System.out.print("Tu respuesta: ");
            String texto = sc.nextLine();
            enviar(socket, texto, servidor, PUERTO);

            // =========================
            // 4️⃣ Fin
            // =========================
            System.out.println("Cliente finaliza comunicación.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviar(DatagramSocket socket, String mensaje,
                               InetAddress ip, int puerto) throws Exception {

        byte[] datos = mensaje.getBytes();
        DatagramPacket paquete =
                new DatagramPacket(datos, datos.length, ip, puerto);
        socket.send(paquete);
    }

    private static String recibir(DatagramSocket socket) throws Exception {
        byte[] buffer = new byte[1024];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return new String(paquete.getData(), 0, paquete.getLength()).trim();
    }
}
