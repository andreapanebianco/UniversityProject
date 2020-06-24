import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/*
Il CM è intermediario tra client e server. È responsabile dello svolgimento di tutte le funzioni eseguibili dal client,
che richiedano un'interazione con il server o con l'hashmap comune in esso locata. Comunica con il client tramite i messaggii
protocollari che precedono ciascun messaggio inviato dall'utente (ed inviandone dei propri in risposta).
 */
public class GitFlockClientManager implements Runnable {

    private Socket client_socket;
    //private Socket outsocket;
    private ConcurrentHashMap<String, User> users;
    /*
    Implementando Runnable, il CM è implementato come un thread. Data la natura della ConcurrentHashmap, locata nel server,
    essa è inserita nel costruttore in maniera tale da consentirne la consultazione e la modifica.
     */
    public GitFlockClientManager(Socket myclient, ConcurrentHashMap<String, User> users) {
        //outsocket = out_socket;
        client_socket = myclient;
        this.users = users;
    }
    /*
    La funzione getListCopy viene implementata per consentire la stampa del log di messaggi ricevuti dall'utente.
    Ciascun oggetto User dell'hashmap contiene un'arraylist di stringhe, che consente l'archiviazione dei messaggi ricevuti.
    La lista di ciascun utente è copiata in fase di stampa.
     */
    public ArrayList<String> getListCopy(ArrayList<String> list) {
        ArrayList<String> a_list = new ArrayList<>();
        a_list.addAll(list);
        return a_list;
    }

    @Override
    public void run() {
        String tid = Thread.currentThread().getName();
        System.out.println(tid+" -> Accepted connection from " + client_socket.getRemoteSocketAddress());
        // Vengono inizializzati uno scanner ed un printwriter, destinati alla comunicazione protocollare.
        Scanner client_scanner = null;
        PrintWriter pw = null;
        //PrintWriter opw = null;
        String mate = null;
        String username = null;
        // Viene inizializzata una variabile data, con successivo formato leggibile, abbinata al messaggio in fase di archiviazione.
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM HH:mm");
        /*
        Viene inizializzato un intero che consenta, tramite il confronto con l'arraylist di messaggi del singolo utente,
        di verificare l'eventuale presenza di messaggi non letti.
         */
        int listsize = 0;
        try {
            // Vengono associati getInputStream e getOutputStream per la lettura e la scrittura dei messaggi protocollari.
            client_scanner = new Scanner(client_socket.getInputStream());
            pw = new PrintWriter(client_socket.getOutputStream());
            //opw = new PrintWriter(outsocket.getOutputStream());
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
            // È inizializzato un oggetto user vuoto, che consenta l'archiviazione locale dei dati dell'utente.
            User p = new User();
            // Attraverso l'interpretazione del messaggio protocollare, vengono eseguite le rispettive funzioni.
            if (cmd.equals("ADD")) {
                /*
                Al messaggio protocollare "ADD" corrisponde l'associazione dei dati inseriti dall'utente ai campi del
                nuovo oggetto user. Il nuovo utente è dunque inserito nell'hashmap.
                 */
                String name = msg_scanner.next();
                String surname = msg_scanner.next();
                int age = msg_scanner.nextInt();
                username = msg_scanner.next();
                p.setName(name);
                p.setAge(age);
                p.setSurname(surname);
                p.setUsername(username);
                users.put(username, p);
                System.out.println("SERVER LOG: Added " + p);
                pw.println("ADD_OK");
                pw.flush();
            }
            else if (cmd.equals("REMOVE")){
                /*
                Al messaggio protocollare "REMOVE" corrisponde la rimozione dell'oggetto utente dall'hashmap comune,
                tramite l'utilizzo dello username come key.
                 */
                System.out.println("Executing command REMOVE");
                System.out.println("Username to remove: " +username);
                users.remove(username);
            }
            else if(cmd.equals("CHECK")){
                /*
                Al messaggio protocollare "CHECK" corrisponde il confronto tra l'intero listsize localmente conservato e
                la dimensione dell'arraylist dei messaggi corrispondente all'utente.
                 */
                if (listsize<this.users.get(username).msglog.size()){
                    listsize=this.users.get(username).msglog.size();
                    pw.println("NEWMSG");
                    pw.flush();
                }
                else {
                    pw.println("NOMSG");
                    pw.flush();
                }
            }
            else if (cmd.equals("LIST")) {
                /*
                Al messaggio protocollare "LIST" corrisponde la stampa degli utenti contenuti nell'hashmap, tramite una
                iterazione associata al keyset della stessa.
                 */
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
                /*
                Al messaggio protocollare "CHAT" corrisponde la stampa della copia della lista di messaggi contenuta
                nell'oggetto utente.
                 */
                pw.println("LOGBEGIN");
                pw.flush();
                ArrayList<String> tmp;
                tmp= getListCopy(this.users.get(username).msglog);
                for (String m: tmp) {
                    pw.println(m);
                    pw.flush();
                }
                pw.println("LOGEND");
                pw.flush();
            }
            else if (cmd.equals("CHATWITH")) {
                /*
                Al messaggio protocollare "CHATWITH", seguito dallo username dell'utente destinatario (localmente memorizzato
                come mate, corrisponde la verifica dell'esistenza del destinatario all'interno dell'hashmap.
                 */
                mate = msg_scanner.next();
                synchronized (users) {
                    if (this.users.containsKey(mate)) {
                        pw.println("SUCCESS");
                        pw.flush();
                    }
                    /*
                    else opw.println("CHATWITH "+mate);
                        if (cmd.equals("SUCCESS")) {
                            pw.println("SUCCESS");
                            pw.flush();
                        }
                        */
                        else {
                            pw.println("FAILURE");
                            pw.flush();
                        }
                    }
                }
            else if (cmd.equals("MSG")) {
                /*
                Al messaggio protocollare "MSG" corrisponde l'inserimento del messaggio fornito dall'utente nell'arraylist
                di messaggi del destinatario, corredata di data e ora di invio.
                 */
                synchronized (users) {
                    String msg = msg_scanner.nextLine();
                    this.users.get(mate).addmsg("["+formatter.format(date)+"] "+username+": "+msg);
                }
            }
            else if (cmd.equals("QUIT")) {
                /*
                Al messaggio protocollare "QUIT" corrisponde l'interruzione della connessione al socket con conseguente
                uscita lato utente dall'applicazione.
                 */
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