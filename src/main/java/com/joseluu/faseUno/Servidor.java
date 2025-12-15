package com.joseluu.faseUno;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Servidor {

    private static final int PUERTO = 9876;
    private static final String NOMBRE_SERVIDOR = "ServidorUDP";

    public static void main(String[] args) {

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {

            System.out.println("Servidor UDP escuchando en puerto " + PUERTO);

            byte[] buffer = new byte[1024];

            while (true) { // El servidor siempre escucha

                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                String mensaje = new String(
                        paquete.getData(), 0, paquete.getLength()).trim();

                InetAddress ipCliente = paquete.getAddress();
                int puertoCliente = paquete.getPort();

                System.out.println("Recibido de " + ipCliente + ": " + mensaje);

                // =========================
                // VALIDACIÓN ROBUSTA
                // =========================
                Pattern pattern = Pattern.compile("@hola#(.+?)@");
                Matcher matcher = pattern.matcher(mensaje);

                if (matcher.matches()) {

                    String nombreCliente = matcher.group(1);
                    System.out.println("Saludo válido de: " + nombreCliente);

                    String respuesta = "@hola#" + NOMBRE_SERVIDOR + "@";
                    enviar(socket, respuesta, ipCliente, puertoCliente);

                } else {
                    // 🚫 Basura UDP → se ignora
                    System.out.println("⚠ Trama incorrecta. Ignorada.");
                }
            }

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
