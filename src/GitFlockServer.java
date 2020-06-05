import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GitFlockServer {

    ServerSocket socket;
    Socket client_socket;
    private int port;

    int client_id = 0;

    UsersList list = new UsersList();

    public static void main(String args[]) {

        if (args.length!=1) {
            System.out.println("Usage java MyServer <port>");
            return;
        }

        GitFlockServer server = new GitFlockServer(Integer.parseInt(args[0]));
        server.start();
    }

    public GitFlockServer(int port) {
        // we could also put port value checks here...
        System.out.println("Initializing server with port "+port);
        this.port = port;
    }

    public void start() {
        try {
            System.out.println("Starting server on port "+port);
            socket = new ServerSocket(port);
            System.out.println("Started server on port "+port);
            while (true) {
                System.out.println("Listening on port " + port);
                client_socket = socket.accept();
                System.out.println("Accepted connection from " + client_socket.getRemoteSocketAddress());

                GitFlockClientManager cm = new GitFlockClientManager(client_socket,list);
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
