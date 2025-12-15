package com.joseluu.faseDos;
import java.net.*;
import java.util.HashSet;
public class Cliente {
    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setBroadcast(true);
        clientSocket.setSoTimeout(3000); // Timeout 3s para esperar respuestas

        byte[] sendData = "DISCOVER_SERVER".getBytes();
        byte[] receiveData = new byte[1024];

        // Broadcast a la red local (cambia según tu subnet si es necesario)
        InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcastAddress, 9876);
        clientSocket.send(sendPacket);

        System.out.println("Mensaje de descubrimiento enviado. Esperando respuestas...");

        HashSet<String> serversFound = new HashSet<>();

        try {
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress serverAddress = receivePacket.getAddress();

                // Filtrar respuestas duplicadas
                if (!serversFound.contains(serverAddress.getHostAddress())) {
                    serversFound.add(serverAddress.getHostAddress());
                    System.out.println("Servidor encontrado: " + response + " - IP: " + serverAddress.getHostAddress());
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Tiempo de espera terminado. Servidores encontrados:");
            for (String server : serversFound) {
                System.out.println(" - " + server);
            }
        }

        clientSocket.close();
    }
}
