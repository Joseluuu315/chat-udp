package com.joseluu.faseCuatro;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000);
        System.out.println("Conectado al servidor");

        BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

        Scanner teclado = new Scanner(System.in);

        // El servidor pide el nick
        System.out.println(entrada.readLine());
        String nick = teclado.nextLine();
        salida.println(nick);

        // Hilo que ESCUCHA
        Thread hiloOye = new Thread(() -> {
            try {
                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    System.out.println(mensaje);
                }
            } catch (IOException e) {
                System.out.println("Servidor desconectado");
            }
        });

        // Hilo que HABLA
        Thread hiloHabla = new Thread(() -> {
            while (true) {
                String mensaje = teclado.nextLine();
                salida.println(mensaje);
            }
        });

        hiloOye.start();
        hiloHabla.start();
    }
}
