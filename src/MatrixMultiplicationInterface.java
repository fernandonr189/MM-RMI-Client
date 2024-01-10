import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrixMultiplicationInterface extends Remote {
    public int[][] forkJoinMultiplication(int[][] _matrix, int _factor, int v_from, int v_to) throws RemoteException;

    public int[][] executorServiceMultiplication(int[][] _matrix, int _factor) throws RemoteException;

    public int[][] sequentialMultiplication(int[][] _matrix, int _factor) throws RemoteException;

    public long getDuration() throws RemoteException;
}
