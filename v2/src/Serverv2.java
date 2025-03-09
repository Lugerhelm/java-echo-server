import java.net.ServerSocket;
import java.net.Socket;

public class Serverv2 {

    public static void main(String[] args) throws Exception {
        try (ServerSocket ss = new ServerSocket(1337)) {
            System.out.println("now listening on :1337");
            while (true) {
                Socket uusSocket = ss.accept();
                KlientThread uusKlient = new KlientThread(uusSocket);
                Thread uusKlientThread = new Thread(uusKlient);
                uusKlientThread.start();

            }
        }
    }
}