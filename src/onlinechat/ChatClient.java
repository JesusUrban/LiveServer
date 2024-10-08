
package onlinechat;


import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        
        
        
        
        
        
        try (Socket socket = new Socket("localhost", 5500); 
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // For reading server messages
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // For sending messages to the server
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) { // For reading user input from the console

            System.out.println("Connected to the chat server...");

            // Read the welcome message from the server and prompt the user to enter their name
            System.out.println(in.readLine());
            String userName = consoleInput.readLine();
            out.println(userName); // Send the user name to the server

            // Start a new thread to listen for incoming messages from the server
            new Thread(() -> {
                try {
                    String serverMessage;
                    // Continuously read messages from the server and display them
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace(); 
                }
            }).start();

            // Continuously read user input and send it to the server
            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}
