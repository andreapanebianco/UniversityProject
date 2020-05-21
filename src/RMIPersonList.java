import java.util.ArrayList;

public class RMIPersonList {
    private ArrayList<RMIPerson> list = new ArrayList<>();

    public void addPerson(RMIPerson p) {
        list.add(p);
    }

    public ArrayList<RMIPerson> getList() {
        //return list;
        ArrayList<RMIPerson> anotherlist = new ArrayList<>();
        for (RMIPerson p:list) {
            RMIPerson x = new RMIPerson(p.getName(),p.getAge());
            anotherlist.add(x);
        }

        return anotherlist;
    }
}
