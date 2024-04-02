package org.example;
import javax.annotation.processing.Messager;
import java.io.*;
import java.net.*;
import java.util.Scanner;






class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            // wysyłamy powiadomienie o gotowości do klientu
            out.writeObject("ready");

            // wysyłamy powiadomienie o gotowości na powiadomienie do klientu
            out.writeObject("ready for messages");

            // otrzymujemy jakąś ilość powiadomień od klienta
            int numMessages = (int) in.readObject();
            System.out.println("Client wants to send" + numMessages + " messages. ");

            // otrzymujemy i logujemy powiadomienia od klienta
            for (int i = 0; i< numMessages; i++) {
                Message message = (Message) in.readObject();
                System.out.println("Received message: " + message.getContent());
            }

            // wysyłamy "finished" powiadomienie do klienta
            out.writeObject("finished");

            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}



class Message implements Serializable {
   // private static final long serialVersionUID = 1L;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
