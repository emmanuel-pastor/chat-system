package com.simplesmartapps.chatsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private ServerSocket serverSocket;

    public void start(int port) {
        Thread receivingStartThread = new Thread(() -> {
            try {
                serverSocket = new

                        ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    new

                            EchoClientHandler(serverSocket.accept()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receivingStartThread.start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String inputLine;
            try {
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    out.println(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}