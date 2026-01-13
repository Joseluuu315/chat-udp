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

        // Hilo que ESCUCHA
        Thread hiloOye = new Thread(() -> {
            try {
                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    System.out.println("Servidor: " + mensaje);
                }
            } catch (IOException e) {
                System.out.println("Servidor desconectado");
            }
        });

        // Hilo que HABLA
        Thread hiloHabla = new Thread(() -> {
            Scanner teclado = new Scanner(System.in);
            while (true) {
                String mensaje = teclado.nextLine();
                salida.println(mensaje);
            }
        });

        hiloOye.start();
        hiloHabla.start();
    }
}