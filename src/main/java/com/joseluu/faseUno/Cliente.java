package com.joseluu.faseUno;


import java.net.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cliente {
    private static final int PUERTO = 9876;

    public static void main(String[] args) {

        try (DatagramSocket socket = new DatagramSocket()) {

            // ⏱️ Timeout de 5 segundos
            socket.setSoTimeout(5000);

            InetAddress servidor = InetAddress.getByName("localhost");
            Scanner sc = new Scanner(System.in);

            System.out.print("Introduce tu nombre: ");
            String nombre = sc.nextLine();

            // =========================
            // ENVÍO DE SALUDO
            // =========================
            String saludo = "@hola#" + nombre + "@";
            enviar(socket, saludo, servidor, PUERTO);
            System.out.println("Enviado: " + saludo);

            try {
                // =========================
                // RECEPCIÓN DE RESPUESTA
                // =========================
                String respuesta = recibir(socket);
                validarRespuesta(respuesta);

            } catch (SocketTimeoutException e) {
                System.out.println("⏱️ El servidor no respondió en 5 segundos.");
            }

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
        return new String(paquete.getData(), 0, paquete.getLength()).trim();
    }

    private static void validarRespuesta(String respuesta) {

        Pattern pattern = Pattern.compile("@hola#(.+?)@");
        Matcher matcher = pattern.matcher(respuesta);

        if (matcher.matches()) {
            String nombreServidor = matcher.group(1);
            System.out.println("Conectado al servidor: " + nombreServidor);
        } else {
            System.out.println("⚠ Respuesta del servidor con formato no válido.");
        }
    }
}
