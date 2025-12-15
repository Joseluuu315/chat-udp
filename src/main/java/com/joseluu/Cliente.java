package com.joseluu;

import java.net.*;
import java.util.Scanner;

public class Cliente {

    private static final int PUERTO = 9876;

    public static void main(String[] args) {

        try (DatagramSocket socket = new DatagramSocket()) {

            InetAddress servidor = InetAddress.getByName("localhost");
            Scanner sc = new Scanner(System.in);

            System.out.println("Conectado al servidor " + servidor + ":" + PUERTO);
            System.out.println("Escribe 'bye' para salir");

            // Hilo para recibir mensajes
            Thread hiloRecepcion = new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (true) {
                    try {
                        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                        socket.receive(paquete);

                        String mensaje = new String(paquete.getData(), 0, paquete.getLength()).trim();
                        System.out.println("\nServidor dice: " + mensaje);
                        System.out.print("Tú: ");

                        if (mensaje.equalsIgnoreCase("bye")) {
                            System.out.println("Servidor finalizó la comunicación.");
                            System.exit(0);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            });

            hiloRecepcion.start();

            // Hilo principal para enviar mensajes
            while (true) {
                System.out.print("Tú: ");
                String texto = sc.nextLine();
                socket.send(new DatagramPacket(texto.getBytes(), texto.getBytes().length, servidor, PUERTO));

                if (texto.equalsIgnoreCase("bye")) {
                    System.out.println("Cliente finaliza comunicación.");
                    break;
                }
            }

            sc.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
