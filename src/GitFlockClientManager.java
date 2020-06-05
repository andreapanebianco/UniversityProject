import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GitFlockClientManager implements Runnable {

    private Socket client_socket;
    private UsersList list;

    public GitFlockClientManager(Socket myclient, UsersList list) {
        client_socket = myclient;
        this.list = list;
    }

    @Override
    public void run() {
        String tid = Thread.currentThread().getName();
        System.out.println(tid+"-> Accepted connection from " + client_socket.getRemoteSocketAddress());
        //HashMap<String, User> userHashMap = new HashMap<>();
        //Map<String, User> userHashMap = Collections.synchronizedMap(new HashMap<>());
        ConcurrentHashMap<String, User> userHashMap = new ConcurrentHashMap<>();
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
            System.out.println("Received command:"+cmd);
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
                list.add(p);
                synchronized (userHashMap) {
                    userHashMap.put(username, p);
                    if (userHashMap.containsKey(username)) {
                        System.out.println("User put in hashmap");
                        System.out.println(username.hashCode());
                    } else System.out.println("Oh cazzo");
                }
                System.out.println("SERVER LOG:Added "+p);
                // we always return OK, but in a more complex
                // scenario, e.g., age>18, etc... an ADD_ERROR
                // could be returned
                pw.println("ADD_OK");
                pw.flush();
            }
            else if (cmd.equals("REMOVE")){
                System.out.println("Executing command REMOVE");
                String username = msg_scanner.next();
                System.out.println("Username to remove: " +username);
                userHashMap.remove(username);
                /*
                Iterator <User> iterator = list.iterator;
                while (iterator.hasNext()) {
                    System.out.println("Current username: "+iterator.next().getUsername());
                    if (iterator.next().getUsername().equals(username)) {
                        iterator.remove();
                    }
                }
                // list.remove(p);

                 */
            }
            else if (cmd.equals("LIST")) {
                pw.println("BEGIN");
                pw.flush();

                ArrayList<User> tmp;
                /* Should not do this! see PersonList comment...
                tmp = list.getList();
                for (Person p: tmp) {
                    pw.println(p);
                    pw.flush();
                }
                */

                tmp = list.getListCopy();
                for (User u: tmp) {
                    pw.println(u);
                    pw.flush();
                }

                pw.println("END");
                pw.flush();
            }

            else if (cmd.equals("CHAT")) {
                String username = msg_scanner.next();
                System.out.println(username.hashCode());
                synchronized (userHashMap) {
                    if (userHashMap.containsKey(username)) {
                        System.out.println("User exists!!");
                    } else System.out.println("User " + username + " doesn't exist.");
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
                System.out.println("Unknown command "+ message);
                pw.println("ERROR_CMD");
                pw.flush();
            }

        }
    }
}