package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] agrs) {
        try {
            ServerSocket serverSocket = new ServerSocket(19244);
            System.out.println("Serwer oczekuje na porcie 19244");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie zaakceptowane.");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clienThread = new Thread(clientHandler);
                clienThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}