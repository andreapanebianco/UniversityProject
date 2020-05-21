import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIServices extends Remote {

    public String getDate() throws RemoteException;
    public String toUP(String s) throws RemoteException;
    public ArrayList<RMIPerson> getList() throws RemoteException;
    public void addPerson(RMIPerson p) throws RemoteException;
    public void doIntensiveTask() throws RemoteException;
}
