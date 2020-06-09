import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GitFlockClient {
    Socket socket;
    private String address;
    private int port;
    private String username;

    public static void main(String args[]) {

        if (args.length!=2)  {
            System.out.println("Usage: java GitFlockClient <address> <port>");
            return;
        }

        GitFlockClient client = new GitFlockClient(args[0], Integer.parseInt(args[1]));
        client.start();
    }

    public GitFlockClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void start() {
        System.out.println("Starting Client connection to "+address+": "+port);

        try {
            socket = new Socket(address,port);
            System.out.println("Started Client connection to " +address+ ": " +port);

            // to server
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            // from server
            Scanner server_scanner = new Scanner(socket.getInputStream());
            // from user
            Scanner user_scanner = new Scanner(System.in);

            String msg_to_send;
            String msg_received;

            boolean go = true;
            int choice1,choice2;

            while (go) {

                System.out.println("********** Welcome to GitFlock, the java-based social network **********");
                System.out.println("1 - Register user");
                System.out.println("2 - About GitFlock");
                System.out.println("3 - Quit");
                System.out.println("************************************************************************");
                System.out.print("Enter choice -> ");
                choice1 = user_scanner.nextInt();

                switch (choice1) {
                    case 1:
                        System.out.print("Insert name:\n");
                        String name = user_scanner.next();
                        System.out.print("Insert surname:\n");
                        String surname = user_scanner.next();
                        System.out.println("Insert age:");
                        int age = user_scanner.nextInt();
                        System.out.print("Insert username:\n");
                        String username = user_scanner.next();
                        if(age < 14) {
                            System.out.println("We're sorry, you're too young to use this application.");
                            break;
                        }
                        else if (age > 65) {
                            System.out.println("We're sorry, it's too late for you to use this application.");
                            break;
                        }

                        msg_to_send = "ADD "+name+" "+surname+" "+age+ " " +username;
                        pw.println(msg_to_send);
                        pw.flush();

                        msg_received = server_scanner.nextLine();
                        if (msg_received.equals("ADD_OK")) {
                            System.out.println("Person added correctly!");
                        }
                        else if (msg_received.equals("ADD_ERROR")) {
                            System.out.println("Error adding person!");
                        }
                        else {
                            System.out.println("ERROR: unknown message -> "+msg_received);
                        }

                        boolean connect = true;
                        while (connect) {
                            System.out.println("1 - See who's online");
                            System.out.println("2 - Start a chat with someone");
                            System.out.println("3 - Back to previous menu");
                            System.out.print("Enter choice -> ");
                            choice2 = user_scanner.nextInt();
                            switch (choice2) {
                                case 1:
                                    msg_to_send = "LIST";
                                    pw.println(msg_to_send);
                                    pw.flush();
                                    msg_received = server_scanner.nextLine();
                                    boolean listing = true;
                                    if (msg_received.equals("BEGIN")) {
                                        System.out.println("Receiving list...");
                                        while (listing) {
                                            msg_received = server_scanner.nextLine();
                                            if (msg_received.equals("END")) {
                                                listing = false;
                                                System.out.println("************************************************************************");
                                            } else {
                                                System.out.println(msg_received);
                                            }
                                        }
                                    }
                                    else {
                                        System.out.println("Unknown Response: "+msg_received);
                                    }
                                    break;
                                case 2:
                                    msg_to_send = "CHAT";
                                    pw.println(msg_to_send);
                                    pw.flush();
                                    msg_received = server_scanner.nextLine();
                                    boolean loglisting = true;
                                    if (msg_received.equals("LOGBEGIN")) {
                                        System.out.println("Receiving chat log...");
                                        while (loglisting) {
                                            msg_received = server_scanner.nextLine();
                                            if (msg_received.equals("LOGEND")) {
                                                loglisting = false;
                                                System.out.println("************************************************************************");
                                            } else {
                                                System.out.println(msg_received);
                                            }
                                        }
                                    }
                                    else {
                                        System.out.println("Unknown Response: "+msg_received);
                                    }
                                    System.out.println("Enter a username to chat with: ");
                                    String mate = user_scanner.next();
                                    if (mate.equals("QUIT")) break;
                                    user_scanner.nextLine();
                                    msg_to_send = "CHATWITH "+mate;
                                    pw.println(msg_to_send);
                                    pw.flush();
                                    msg_received = server_scanner.nextLine();
                                    if (msg_received.equals("SUCCESS")) {
                                        System.out.println("Contact established with user "+mate);
                                        boolean talk = true;
                                        String msg = null;
                                        while(talk) {
                                            System.out.println("Type your message:");
                                            msg = user_scanner.nextLine();
                                            if (msg.equals("QUIT")) {
                                                talk = false;
                                                break;
                                            }
                                            msg_to_send = "MSG "+msg;
                                            pw.println(msg_to_send);
                                            pw.flush();
                                        }
                                        break;
                                    } else if (msg_received.equals("FAILURE")) {
                                        System.out.println("User not available");
                                    }
                                    break;
                                case 3:
                                    msg_to_send = "REMOVE";
                                    pw.println(msg_to_send);
                                    pw.flush();
                                    System.out.println("Going offline...");
                                    connect = false;
                                    System.out.println("Quitting chatroom...");
                                    break;
                            }
                        }
                        break;
                    case 2:
                        System.out.println("");
                        break;
                    case 3:
                        go = false;
                        System.out.println("Quitting GitFlock...");
                        msg_to_send = "QUIT";
                        pw.println(msg_to_send);
                        pw.flush();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
