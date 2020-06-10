import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    // Vengono definite le variabili relative ai campi dell'utente, inserite nell'oggetto User.
    private String name;
    private String surname;
    private int age;
    private String username;
    /*
    Viene inizializzata un'arraylist pubblica, che consenta a tutti gli altri utenti di inserire i messaggi destinati
    allo user.
     */
    public ArrayList<String> msglog = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    // Viene implementata una funzione che consenta l'aggiunta remota di elementi all'arraylist contenuta nell'oggetto User.
    public void addmsg(String msg){
        msglog.add(msg);
    }
    // Viene eseguito un override della funzione ToString per la stampa degli utenti.
    @Override
    public String toString() {
        return username+"("+name+" "+surname+", "+age+")";
    }
}