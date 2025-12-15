package com.joseluu;

import java.net.*;
import java.util.regex.*;

public class Servidor {

    private static final int PUERTO = 9876;
    private static final String NOMBRE_SERVIDOR = "ServidorUDP";

    public static void main(String[] args) {

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {

            System.out.println("Servidor UDP iniciado en puerto " + PUERTO);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                String mensaje = new String(paquete.getData(), 0, paquete.getLength()).trim();
                InetAddress ipCliente = paquete.getAddress();
                int puertoCliente = paquete.getPort();

                System.out.println("Recibido de " + ipCliente + ": " + mensaje);

                // 🔹 FASE 1: Saludo
                Pattern saludoPattern = Pattern.compile("@hola#(.+?)@");
                Matcher saludoMatcher = saludoPattern.matcher(mensaje);

                // 🔹 FASE 2: Discover
                Pattern discoverPattern = Pattern.compile("@discover#(.+?)@");
                Matcher discoverMatcher = discoverPattern.matcher(mensaje);

                if (saludoMatcher.matches()) {

                    String nombreCliente = saludoMatcher.group(1);
                    System.out.println("Saludo válido de: " + nombreCliente);

                    String respuesta = "@hola#" + NOMBRE_SERVIDOR + "@";
                    enviar(socket, respuesta, ipCliente, puertoCliente);

                } else if (discoverMatcher.matches()) {

                    System.out.println("Discover recibido");
                    String respuesta = "@here#" + NOMBRE_SERVIDOR + "@";
                    enviar(socket, respuesta, ipCliente, puertoCliente);

                } else {
                    // 🔒 Robustez: basura ignorada
                    System.out.println("⚠ Trama incorrecta ignorada");
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
