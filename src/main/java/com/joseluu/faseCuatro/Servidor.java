package com.joseluu.faseCuatro;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Servidor {

    private static final Map<PrintWriter, String> clientes = new HashMap<>();
    private static final DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm");

    static class Colores {
        public static final String RESET = "\u001B[0m";
        public static final String VERDE = "\u001B[32m";
        public static final String ROJO = "\u001B[31m";
        public static final String AZUL = "\u001B[34m";
        public static final String AMARILLO = "\u001B[33m";
    }


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("💬 Servidor de chat iniciado...");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ClienteHandler(socket)).start();
        }
    }

    static class ClienteHandler implements Runnable {

        private final BufferedReader entrada;
        private final PrintWriter salida;
        private String nick;

        public ClienteHandler(Socket socket) throws IOException {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                salida.println("Introduce tu nickname:");
                nick = entrada.readLine();

                synchronized (clientes) {
                    clientes.put(salida, nick);
                }

                enviarSistema("🟢 " + nick + " se ha conectado");

                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {

                    if (mensaje.equalsIgnoreCase("/salir")) {
                        break;
                    }

                    enviarATodos(formatearMensaje(nick, mensaje));
                }
            } catch (IOException ignored) {
            } finally {
                synchronized (clientes) {
                    clientes.remove(salida);
                }
                enviarSistema("🔴 " + nick + " se ha desconectado");
            }
        }

        private String formatearMensaje(String nick, String mensaje) {
            return Colores.AZUL + "[" + LocalTime.now().format(hora) + "] "
                    + Colores.VERDE + nick + Colores.RESET + ": " + mensaje;
        }

        private void enviarSistema(String mensaje) {
            synchronized (clientes) {
                for (PrintWriter cliente : clientes.keySet()) {
                    cliente.println(Colores.AMARILLO + mensaje + Colores.RESET);
                }
            }
        }

        private void enviarATodos(String mensaje) {
            synchronized (clientes) {
                for (Map.Entry<PrintWriter, String> entry : clientes.entrySet()) {
                    if (!entry.getValue().equals(nick)) {
                        entry.getKey().println(mensaje);
                    }
                }
            }
        }
    }
}
