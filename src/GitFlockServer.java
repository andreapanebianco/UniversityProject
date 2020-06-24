import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class GitFlockServer {
    /*
    Nel server sono inizializzate le variabili di socket, client-socket, porta di connessione e l'hashmap comune a tutti
    gli utenti dell'applicazione. La suddetta hashmap sarà fornita ad ognuno degli utenti tramite rispettivo CM.
     */
    ServerSocket socket;
    //ServerSocket outsocket;
    Socket client_socket;
    //Socket out_socket;
    private int port;
    // La ConcurrentHashMap è definita per avere come key una stringa (lo username dell'utente).
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
            /*
            if (port == 7500){
                outsocket = new ServerSocket(1109);
            } else outsocket = new ServerSocket(7500);
             */
            System.out.println("Started server on port "+port);
            while (true) {
                System.out.println("Listening on port " + port);
                client_socket = socket.accept();
                //out_socket = outsocket.accept();
                System.out.println("Accepted connection from " + client_socket.getRemoteSocketAddress());
                //System.out.println("Accepted remote connection from " + out_socket.getRemoteSocketAddress());
                // Viene creato il CM, fornendo al costruttore il socket e l'hashmap.
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