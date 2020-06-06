import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GitFlockClientManager implements Runnable {

    private Socket client_socket;
    private ConcurrentHashMap<String, User> users;

    public GitFlockClientManager(Socket myclient, ConcurrentHashMap<String, User> users) {
        client_socket = myclient;
        this.users = users;
    }

    @Override
    public void run() {
        String tid = Thread.currentThread().getName();
        System.out.println(tid+" -> Accepted connection from " + client_socket.getRemoteSocketAddress());
        Scanner client_scanner = null;
        PrintWriter pw = null;
        try {
            client_scanner = new Scanner(client_socket.getInputStream());
            pw = new PrintWriter(client_socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean go = true;
        while (go) {
            String message = client_scanner.nextLine();
            System.out.println("Server Received: "+message);
            Scanner msg_scanner = new Scanner(message);
            String cmd = msg_scanner.next();
            System.out.println("Received command: "+cmd);
            User p = new User();
            if (cmd.equals("ADD")) {
                String name = msg_scanner.next();
                String surname = msg_scanner.next();
                int age = msg_scanner.nextInt();
                String username = msg_scanner.next();
                p.setName(name);
                p.setAge(age);
                p.setSurname(surname);
                p.setUsername(username);
                users.put(username, p);
                System.out.println("SERVER LOG: Added "+p);
                pw.println("ADD_OK");
                pw.flush();
            }
            else if (cmd.equals("REMOVE")){
                System.out.println("Executing command REMOVE");
                String username = msg_scanner.next();
                System.out.println("Username to remove: " +username);
                users.remove(username);
            }
            else if (cmd.equals("LIST")) {
                pw.println("BEGIN");
                pw.flush();
                for(String key: users.keySet()){
                    pw.println(users.get(key));
                    pw.flush();
                }
                pw.println("END");
                pw.flush();
            }

            else if (cmd.equals("CHAT")) {
                String username = msg_scanner.next();
                synchronized (users) {
                    if (this.users.containsKey(username)) {
                        pw.println("SUCCESS");
                        pw.flush();
                    } else {
                        pw.println("FAILURE");
                        pw.flush();
                    }
                }
            }

            else if (cmd.equals("QUIT")) {
                System.out.println("Server: Closing connection to "+client_socket.getRemoteSocketAddress());
                try {
                    client_socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                go = false;
            }
            else {
                System.out.println("Unknown command "+message);
                pw.println("ERROR_CMD");
                pw.flush();
            }

        }
    }
}