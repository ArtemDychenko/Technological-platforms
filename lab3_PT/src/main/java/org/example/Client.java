package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    public static void main(String[] args) {
       for (int i = 0; i < 4; i++) {
           Client client1 = new Client();
           Thread clientThread = new Thread(client1);
           clientThread.start();
       }
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket("localhost", 19244);
            System.out.println("Connected with server");

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // otrzymujemy "ready" od serwera
            String readyMsg = (String) in.readObject();
            System.out.println("Serwer: " + readyMsg);

            // otrzymujemy "ready for messages" od serwera
            String readyForMsgs = (String) in.readObject();
            System.out.println("Serwer: " + readyForMsgs);

            // podanie ilości powiadomień
            Scanner scanner = new Scanner(System.in);
            System.out.print("Podaj ilość zapytów do serw: ");
            int numMessages = scanner.nextInt();
            out.writeObject(numMessages);

            // nadsyłanie powiadomień so serw
            for (int i = 0; i < numMessages; i++) {
                Message message = new Message();
                message.setContent("Message " + (i + 1));
                out.writeObject(message);
            }

            // otrzymanie "finished" od serw
            String finishedMsg = (String) in.readObject();
            System.out.println("Serwer: " + finishedMsg);

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
