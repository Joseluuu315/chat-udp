package com.joseluu;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;

public class Cliente {

    private static final int PUERTO = 9876;
    private static final String BROADCAST = "172.16.8.255";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try (DatagramSocket socket = new DatagramSocket()) {

            // Timeout SOLO para fases 1 y 2
            socket.setSoTimeout(5000);

            System.out.print("Introduce tu nombre: ");
            String nombre = sc.nextLine();

            // =========================
            // FASE 1 – SALUDO
            // =========================
            InetAddress servidor = InetAddress.getByName("localhost");
            enviar(socket, "@hola#" + nombre + "@", servidor, PUERTO);

            try {
                String respuesta = recibir(socket);
                validarSaludo(respuesta);
            } catch (SocketTimeoutException e) {
                System.out.println("⏱️ Servidor no responde");
                return;
            }

            // =========================
            // FASE 2 – DISCOVERY
            // =========================
            List<InetSocketAddress> amigos = new ArrayList<>();

            socket.setBroadcast(true);
            InetAddress broadcastAddr = InetAddress.getByName(BROADCAST);
            enviar(socket, "@discover#" + nombre + "@", broadcastAddr, PUERTO);

            System.out.println("Buscando servidores...");

            // Dirección real del socket (IP + puerto)
            InetSocketAddress miDireccion =
                    new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());

            while (true) {
                try {
                    DatagramPacket p = recibirPaquete(socket);

                    InetSocketAddress origen =
                            new InetSocketAddress(p.getAddress(), p.getPort());

                    // Filtrar SOLO mi propio paquete
                    if (origen.equals(miDireccion)) {
                        continue;
                    }

                    amigos.add(origen);
                    System.out.println("Servidor encontrado: " + origen.getAddress());

                } catch (SocketTimeoutException e) {
                    break;
                }
            }

            if (amigos.isEmpty()) {
                System.out.println("No se encontraron servidores");
                return;
            }

            // =========================
            // FASE 3 – CHAT BÁSICO
            // =========================

            // 🔓 IMPORTANTE: en chat NO usamos timeout
            socket.setSoTimeout(0);

            InetSocketAddress amigo = amigos.get(0);
            System.out.println("Conectado a " + amigo.getAddress());
            System.out.println("Escribe 'bye' para salir");

            while (true) {

                System.out.print("Tú: ");
                String texto = sc.nextLine();

                enviar(socket, texto, amigo.getAddress(), amigo.getPort());

                if (texto.equalsIgnoreCase("bye")) {
                    break;
                }

                String respuesta = recibir(socket);
                System.out.println("Amigo: " + respuesta);

                if (respuesta.equalsIgnoreCase("bye")) {
                    break;
                }
            }

            System.out.println("Chat finalizado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // MÉTODOS AUXILIARES
    // =========================
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
        return new String(paquete.getData(), 0, paquete.getLength());
    }

    private static DatagramPacket recibirPaquete(DatagramSocket socket) throws Exception {
        byte[] buffer = new byte[1024];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete;
    }

    private static void validarSaludo(String respuesta) {
        Pattern pattern = Pattern.compile("@hola#(.+?)@");
        Matcher matcher = pattern.matcher(respuesta);

        if (matcher.matches()) {
            System.out.println("Conectado al servidor: " + matcher.group(1));
        } else {
            System.out.println("Respuesta con formato no válido");
        }
    }
}
