import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class UsersList {
    public Iterator <User> iterator;
    // wrapping an arraylist into a custom class
    // (1) manage and syncronize access to list
    // (2) add other meta-data (creation, modification date)

    //String archive_id;
    //String last_modification;
    private ArrayList<User> list;
    public UsersList() {
        list = new ArrayList<User>();
    }

    // could also return a boolean to check the validity...
    public synchronized void add(User p) {
        //last_modification = new Date().toString();
        list.add(p);
    }

    public synchronized void remove(User p) {
       // last_modification = new Date().toString();
        list.remove(p);
    }

    /* this would cancel any benefic of sync and private accesss to list
    public ArrayList<Person> getList() {
        return list;
    }
    */

    public ArrayList<User> getListCopy() {
        ArrayList<User> a_list = new ArrayList<>();
        a_list.addAll(list);
        return a_list;
    }


    // other solution, use this in ClientManager:
    // pw.println(list);
    @Override
    public String toString() {
        String s;
        s = "BEGIN_LIST";
        //s = s+" MOD_DATE: "+last_modification;
        for (User p : list) {
            s = s+" NAME: "+p.getName();
            s = s+" SURNAME: "+p.getSurname();
            s = s+" AGE: "+p.getAge();
            s = s+" USERNAME: "+p.getUsername();
        }
        s = s + "END_LIST";

        return s;
    }
}
