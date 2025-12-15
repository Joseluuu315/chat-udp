package com.joseluu;

import java.net.*;
import java.util.*;
import java.util.regex.*;

public class Servidor {

    private static final int PUERTO = 9876;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {

            System.out.println("Servidor UDP iniciado en puerto " + PUERTO);

            Scanner sc = new Scanner(System.in);

            // Lista de clientes conectados
            List<InetSocketAddress> clientes = Collections.synchronizedList(new ArrayList<>());

            // Hilo para recibir mensajes
            Thread hiloRecepcion = new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (true) {
                    try {
                        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                        socket.receive(paquete);

                        String mensaje = new String(paquete.getData(), 0, paquete.getLength()).trim();
                        InetAddress origen = paquete.getAddress();
                        int puerto = paquete.getPort();
                        InetSocketAddress cliente = new InetSocketAddress(origen, puerto);

                        // Añadir el cliente a la lista si es nuevo
                        if (!clientes.contains(cliente)) {
                            clientes.add(cliente);
                        }

                        // Validación handshake
                        if (mensaje.startsWith("@hola#") && mensaje.endsWith("@")) {
                            String nombre = mensaje.substring(6, mensaje.length() - 1);
                            System.out.println("🔹 Saludo de: " + nombre + " desde " + origen);
                            String respuesta = "@hola#Servidor@";
                            enviar(socket, respuesta, origen, puerto);
                        } else if (mensaje.startsWith("@discover#") && mensaje.endsWith("@")) {
                            String nombre = mensaje.substring(10, mensaje.length() - 1);
                            System.out.println("🔹 Discover recibido de: " + nombre + " desde " + origen);
                            String respuesta = "@discover#Servidor@";
                            enviar(socket, respuesta, origen, puerto);
                        } else {
                            // Chat libre
                            System.out.println("Cliente " + origen + ":" + puerto + " dice: " + mensaje);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            hiloRecepcion.start();

            // Hilo principal para enviar mensajes a todos los clientes
            while (true) {
                if (!clientes.isEmpty()) {
                    System.out.print("Tú: ");
                    String texto = sc.nextLine();

                    for (InetSocketAddress c : clientes) {
                        enviar(socket, texto, c.getAddress(), c.getPort());
                    }

                    if (texto.equalsIgnoreCase("bye")) {
                        System.out.println("Servidor finaliza comunicación.");
                        break;
                    }
                }
            }

            sc.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviar(DatagramSocket socket, String mensaje,
                               InetAddress ip, int puerto) throws Exception {

        byte[] datos = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, puerto);
        socket.send(paquete);
    }
}