import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class GitFlockServer {

    ServerSocket socket;
    Socket client_socket;
    private int port;
    ConcurrentHashMap<String, User> Users = new ConcurrentHashMap<String, User>();

    int client_id = 0;

    public static void main(String args[]) {

        if (args.length!=1) {
            System.out.println("Usage java MyServer <port>");
            return;
        }

        GitFlockServer server = new GitFlockServer(Integer.parseInt(args[0]));
        server.start();
    }

    public GitFlockServer(int port) {
        System.out.println("Initializing server with port "+port);
        this.port = port;
    }

    public void start() {
        try {
            System.out.println("Starting server on port "+port);
            socket = new ServerSocket(port);
            System.out.println("Started server on port "+port);
            while (true) {
                System.out.println("Listening on port "+port);
                client_socket = socket.accept();
                System.out.println("Accepted connection from "+client_socket.getRemoteSocketAddress());

                GitFlockClientManager cm = new GitFlockClientManager(client_socket, Users);
                Thread t = new Thread(cm,"client_"+client_id);
                client_id++;
                t.start();
            }

        } catch (IOException e) {
            System.out.println("Could not start server on port "+port);
            e.printStackTrace();
        }

    }
}