package com.joseluu.faseCuatro;

import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {

    private static Map<PrintWriter, String> clientes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Servidor de chat iniciado...");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ClienteHandler(socket)).start();
        }
    }

    static class ClienteHandler implements Runnable {

        private Socket socket;
        private BufferedReader entrada;
        private PrintWriter salida;
        private String nick;

        public ClienteHandler(Socket socket) throws IOException {
            this.socket = socket;
            entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                // Pedir nickname
                salida.println("Introduce tu nickname:");
                nick = entrada.readLine();

                synchronized (clientes) {
                    clientes.put(salida, nick);
                }

                enviarATodos("🟢 " + nick + " se ha conectado");

                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    enviarATodos(nick + ": " + mensaje);
                }
            } catch (IOException e) {
                // cliente caído
            } finally {
                synchronized (clientes) {
                    clientes.remove(salida);
                }
                enviarATodos("🔴 " + nick + " se ha desconectado");
            }
        }

        private void enviarATodos(String mensaje) {
            synchronized (clientes) {
                for (PrintWriter cliente : clientes.keySet()) {
                    cliente.println(mensaje);
                }
            }
        }
    }
}
