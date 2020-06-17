import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GitFlockClient {
    Socket socket;
    private String address;
    private int port;

    /*
    Il programma sfrutta l'inserimento di due parametri (indirizzo e porta) per aprire il socket destinato al client
    che ne fa richiesta. Lato server questa azione comporta anche la creazione e l'assegnazione di un client manager (CM).
     */

    public static void main(String args[]) {

        if (args.length!=2)  {
            System.out.println("Usage: java GitFlockClient <address> <port>");
            return;
        }

        GitFlockClient client = new GitFlockClient(args[0], Integer.parseInt(args[1]));
        client.start();
    }

    //I valori appena inseriti vengono associati al client da servire

    public GitFlockClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void start() {
        System.out.println("Starting Client connection to "+address+": "+port);

        /*
        Ha dunque inizio la creazione del socket. Vengono inoltre creati degli scanner e dei printwriter destinati
        alla raccolta degli input dell'utente e alla comunicazione protocollare tra client e rispettivo CM.
         */

        try {
            socket = new Socket(address,port);
            System.out.println("Started Client connection to " +address+ ": " +port);

            // Il seguente printwriter consentirà la comunicazione protocollare indirizzata al server e inoltrata al CM.
            PrintWriter pw = new PrintWriter(socket.getOutputStream());

            // Il seguente scanner consentirà la comunicazione protocollare, leggendo i messaggi inviati dal CM.
            Scanner server_scanner = new Scanner(socket.getInputStream());

            // Il seguente scanner consentirà di leggere gli input inseriti dall'utente.
            Scanner user_scanner = new Scanner(System.in);

            // Vengono inizializzate le stringhe adibite alla comunicazione protocollare.
            String msg_to_send;
            String msg_received;

            // Viene introdotto un booleano che consenta l'iterazione della stampa del menu.
            boolean go = true;
            // Vengono introdotti due interi utilizzati per interpretare la scelta dell'utente all'interno del menu.
            int choice1,choice2;

            while (go) {
                System.out.println("************************************************************************");
                System.out.println("   ______   __   ______  ______  __       ______   ______   __  __    ");
                System.out.println("  /\\  ___\\ /\\ \\ /\\__  _\\/\\  ___\\/\\ \\     /\\  __ \\ /\\  ___\\ /\\ \\/ /    ");
                System.out.println("  \\ \\ \\__ \\\\ \\ \\\\/_/\\ \\/\\ \\  __\\\\ \\ \\____\\ \\ \\/\\ \\\\ \\ \\____\\ \\  _\"-.  ");
                System.out.println("   \\ \\_____\\\\ \\_\\  \\ \\_\\ \\ \\_\\   \\ \\_____\\\\ \\_____\\\\ \\_____\\\\ \\_\\ \\_\\ ");
                System.out.println("    \\/_____/ \\/_/   \\/_/  \\/_/    \\/_____/ \\/_____/ \\/_____/ \\/_/\\/_/ ");
                System.out.println("");
                System.out.println("********** Welcome to GitFlock, the java-based social network **********");
                System.out.println("");
                System.out.println("1 - Register user");
                System.out.println("2 - About GitFlock");
                System.out.println("3 - Quit");
                System.out.println("");
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
                        /*
                        In seguito alla raccolta dei campi componenti l'oggetto utente, viene inviato al server
                        un messaggio protocollare adibito all'aggiunta del nuovo utente nell'hashmap.
                        Verrà interpretato il comando "ADD", seguito dai campi precedentemente inseriti.
                        */
                        msg_to_send = "ADD "+name+" "+surname+" "+age+ " " +username;
                        pw.println(msg_to_send);
                        // Verrà sempre eseguito il flush dopo aver adoperato un printwriter, per svuotare il buffer.
                        pw.flush();
                        msg_received = server_scanner.nextLine();
                        if (msg_received.equals("ADD_OK")) {
                            System.out.println("Person added correctly!");
                        }
                        else {
                            System.out.println("ERROR: unknown message -> "+msg_received);
                        }
                        /*
                        Tramite la scelta numero 2, viene introdotto un sotto-menu, fornendo nuove funzioni all'utente
                        appena registratosi.
                         */
                        boolean connect = true;
                        while (connect) {
                            System.out.println("1 - See who's online");
                            System.out.println("2 - Start a chat with someone");
                            System.out.println("3 - Back to previous menu");
                            /*
                            Ogni volta che l'utente tornerà al suddetto sotto-menu verrà svolto un controllo della
                            lista di messaggi ricevuti conservata nell'hashmap e specifica di ciascun utente. Questo
                            controllo sarà richiamato tramite comando protocollare "CHECK", che causerà infine la
                            stampa di un messaggio di notifica.
                             */
                            msg_to_send="CHECK";
                            pw.println(msg_to_send);
                            pw.flush();
                            msg_received = server_scanner.nextLine();
                            if (msg_received.equals("NEWMSG")) {
                                System.out.println("**You have new messages!**");
                            } else if(msg_received.equals("NOMSG")){
                                System.out.println("No new messages.");
                            }
                            System.out.print("Enter choice -> ");
                            choice2 = user_scanner.nextInt();
                            switch (choice2) {
                                case 1:
                                    /*
                                    Il caso 1 comporta l'invio del comando protocollare "LIST". Tramite l'iterazione di
                                    un ciclo while, verrà eseguita la stampa di tutti gli elementi dell'hashmap.
                                    */
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
                                    /*
                                    Il caso 2 comporta l'invio del comando protocollare "CHAT". Tramite l'iterazione di
                                    un ciclo while stampa la lista dei messaggi ricevuti dall'utente ed in seguito
                                    consente la scelta di un destinatario per l'invio di un messaggio.
                                     */
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
                                    /*
                                    Inserendo lo username del destinatario viene inviato il comando protocollare "CHATWITH",
                                    che comporta la ricerca del nome utente riportato nell'hashmap, utilizzandolo come key.
                                     */
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
                                        //Rintracciando il destinatario nell'hashmap, è consentita la scrittura di un messaggio.
                                        boolean talk = true;
                                        String msg = null;
                                        while(talk) {
                                            System.out.println("Type your message:");
                                            msg = user_scanner.nextLine();
                                            if (msg.equals("QUIT")) {
                                                talk = false;
                                                break;
                                            }
                                            //Tramite il comando protocollare "MSG", il messaggio viene inoltrato.
                                            msg_to_send = "MSG "+msg;
                                            pw.println(msg_to_send);
                                            pw.flush();
                                        }
                                        break;
                                    }
                                    else if (msg_received.equals("FAILURE")) {
                                        System.out.println("User not available");
                                    }
                                    break;
                                case 3:
                                    /*
                                    Il caso 3 comporta l'invio del comando protocollare "REMOVE", che causa la rimozione
                                    dell'utente dall'hashmap al fine di non essere più stampato alla richiesta della lista
                                    degli utenti online ad opera di altri.
                                     */
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
                        System.out.println("   ______   __   ______  ______  __       ______   ______   __  __    ");
                        System.out.println("  /\\  ___\\ /\\ \\ /\\__  _\\/\\  ___\\/\\ \\     /\\  __ \\ /\\  ___\\ /\\ \\/ /    ");
                        System.out.println("  \\ \\ \\__ \\\\ \\ \\\\/_/\\ \\/\\ \\  __\\\\ \\ \\____\\ \\ \\/\\ \\\\ \\ \\____\\ \\  _\"-.  ");
                        System.out.println("   \\ \\_____\\\\ \\_\\  \\ \\_\\ \\ \\_\\   \\ \\_____\\\\ \\_____\\\\ \\_____\\\\ \\_\\ \\_\\ ");
                        System.out.println("    \\/_____/ \\/_/   \\/_/  \\/_/    \\/_____/ \\/_____/ \\/_____/ \\/_/\\/_/ ");
                        System.out.println("");
                        System.out.println("GitFlock - the java-based social network");
                        System.out.println("Chat with old and new friends using the complete list of online users; check who's messaged you and answer with as many messages as you want!");
                        System.out.println("You'll always be able to tell when a message was sent to you, and who sent it!");
                        System.out.println("- Input the numbers corresponding to the desired function to navigate the menus.");
                        System.out.println("- Input \"QUIT\" if you wish to go back to the precedent menu (and ultimately exit GitFlock).");
                        break;
                    case 3:
                        /*
                        Il caso 3 comporta l'invio del comando protocollare "QUIT", che in questo menu causa la chiusura
                        dell'applicazione e la conseguente terminazione della connessione tra client e server.
                         */
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
