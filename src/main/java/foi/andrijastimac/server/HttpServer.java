package foi.andrijastimac.server;

import foi.andrijastimac.services.DatabaseInitializer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() {
        new DatabaseInitializer().initialize();

        try (ServerSocket serverSocket = new ServerSocket(port)){

            System.out.println("Server started on port " + port);
            while (true) {
                Socket client = serverSocket.accept();

                ClientHandler handler = new ClientHandler(client);
                new Thread(() -> handler.handle()).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
