package com.joseluu.faseCuatro;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000);

        BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

        Scanner teclado = new Scanner(System.in);

        System.out.println(entrada.readLine());
        salida.println(teclado.nextLine());

        new Thread(() -> {
            try {
                String msg;
                while ((msg = entrada.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException ignored) {
            }
        }).start();

        while (true) {
            String mensaje = teclado.nextLine();
            salida.println(mensaje);
            if (mensaje.equalsIgnoreCase("/salir")) break;
        }

        socket.close();
    }
}
