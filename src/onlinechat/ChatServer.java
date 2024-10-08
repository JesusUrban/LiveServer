
package onlinechat;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    // A set to keep track of all connected clients
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
       
        try (ServerSocket serverSocket = new ServerSocket(5500)) { // Server listens on port 12345
            System.out.println("Server started....... Waiting for clients to connect...");
            

            while (true) {
                // Accept a new client connection
                //This line also serve as instruction
                Socket clientSocket = serverSocket.accept();
                
                // Create a new handler for this client and start its thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                clientHandler.start(); // Start the client's handler thread
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }

    // Method to broadcast a message to all clients except the sender
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != sender) { // Send every message to the clients excepts to the sender
                clientHandler.sendMessage(message);
            }
        }
    }

    // Inner class to handle communication with a specific client
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String userName;

        // Constructor: initialize with the client's socket
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        // Run method executed when the thread starts
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                // Ask the client for their name
                out.println("Welcome to the chat! Please enter your name:");
                userName = in.readLine(); // Read the user's name from input

                // Notify all clients that a new user has joined
                System.out.println(userName + " has joined the chat.");
                broadcastMessage(userName + " has joined the chat!", this);

                String message;
                // Continuously listen for messages from the client
                while ((message = in.readLine()) != null) {
                    // Display and broadcast the message with the user's name
                    System.out.println(userName + ": " + message);
                    broadcastMessage(userName + ": " + message, this);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle exceptions related to IO
            } finally {
                try {
                // Close the connection when done
                    socket.close(); 
                } catch (IOException e) {
                    e.printStackTrace(); 
                }
                clientHandlers.remove(this); // Remove this client from the set

                // Notify all clients that this user has left
                broadcastMessage(userName + " has left the chat.", this);
                System.out.println(userName + " has left the chat.");
            }
        }

        // Method to send a message to this client
        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
