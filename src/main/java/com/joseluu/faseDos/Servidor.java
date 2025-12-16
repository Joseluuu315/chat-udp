package com.joseluu.faseDos;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Servidor {
    public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData;

        System.out.println("Servidor UDP para descubrimiento iniciado en puerto 9876...");

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            // Filtrar auto-respuesta
            if (!clientAddress.equals(InetAddress.getLocalHost())) {
                System.out.println("Solicitud de descubrimiento recibida de: " + clientAddress);

                String response = "SERVER_HERE:" + InetAddress.getLocalHost().getHostName();
                sendData = response.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }
        }
    }
}
