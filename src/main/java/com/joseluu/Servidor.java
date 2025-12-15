package com.joseluu;

import java.net.*;

public class Servidor {

    private static final int PUERTO = 9876;

    public static void main(String[] args) {

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {

            System.out.println("Servidor UDP iniciado en puerto " + PUERTO);
            System.out.println("Esperando mensaje del cliente...");

            byte[] buffer = new byte[1024];

            // =========================
            // 1️⃣ Recibe primer mensaje
            // =========================
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);

            String mensaje = new String(paquete.getData(), 0, paquete.getLength()).trim();
            InetAddress ipCliente = paquete.getAddress();
            int puertoCliente = paquete.getPort();

            System.out.println("Cliente dice: " + mensaje);

            // Validación mínima (robustez UDP)
            if (!mensaje.equalsIgnoreCase("que tal")) {
                System.out.println("⚠ Mensaje inesperado. Trama ignorada.");
                return;
            }

            // =========================
            // 2️⃣ Responde
            // =========================
            String respuesta = "bien, y tu?";
            enviar(socket, respuesta, ipCliente, puertoCliente);
            System.out.println("Servidor responde: " + respuesta);

            // =========================
            // 3️⃣ Recibe última respuesta
            // =========================
            paquete = new DatagramPacket(buffer, buffer.length);
            socket.receive(paquete);

            String ultimaRespuesta =
                    new String(paquete.getData(), 0, paquete.getLength()).trim();

            System.out.println("Cliente responde: " + ultimaRespuesta);

            // =========================
            // 4️⃣ Fin
            // =========================
            System.out.println("Servidor finaliza comunicación.");

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
}
