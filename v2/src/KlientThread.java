import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KlientThread implements Runnable {
    private final Socket socket;
    static final int ok = 2;
    static final int error = 3;
    static final int echoRequest = 4;
    static final int fileRequest = 5;

    public KlientThread(Socket socket) {
        this.socket = socket;
    }

    private void ilusWriteUTF(DataOutputStream out, String str) throws Exception {
        byte[] encoded = str.getBytes("UTF-8");
        out.writeInt(encoded.length);
        out.write(encoded);
    }
    private String ilusReadUTF(DataInputStream in) throws Exception {
        int length = in.readInt();
        byte[] encoded = in.readNBytes(length);
        return new String(encoded, "UTF-8");
    }
    private void echo(DataInputStream dataIn, DataOutputStream dataOut) throws Exception {
        dataOut.writeInt(ok);
        System.out.println("echo request - ootan sõnumit");
        String sõnum = ilusReadUTF(dataIn);
        dataOut.writeInt(ok);
        System.out.println("---");
        System.out.println(sõnum);
        System.out.println("---");
        ilusWriteUTF(dataOut, sõnum);
        System.out.println("sõnum saadetud");
    }

    private void file(DataInputStream dataIn, DataOutputStream dataOut) throws Exception {
        dataOut.writeInt(ok);
        System.out.println("file request - ootan failinime");
        String failNimi =  ilusReadUTF(dataIn);
        Path fail =  Paths.get(failNimi);
        System.out.println("Failinimi - " + failNimi);

        if (fail.isAbsolute() || Files.isDirectory(fail) || !Files.isRegularFile(fail)) {
            dataOut.writeInt(error);
            return;
        }
        dataOut.writeInt(ok);
        System.out.println("Failinimi ok");
        byte[] failiSisu = Files.readAllBytes(fail);
        dataOut.writeInt(failiSisu.length);
        dataOut.write(failiSisu);
        System.out.println("Saadetud");
    }

    @Override
    public void run() {
        try (DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataIn = new DataInputStream(socket.getInputStream())) {

            System.out.println("Uus thread kliendile loodud");
            int sõnumiteArv = dataIn.readInt();
            System.out.println("Oodatav sõnumite arv" + sõnumiteArv);
            dataOut.writeInt(ok);

            for (int i = 0; i < sõnumiteArv; i++) {

                System.out.println("********************************");
                int request = dataIn.readInt();


                if (request == echoRequest) {
                    echo(dataIn, dataOut);
                } else if (request == fileRequest) {
                    file(dataIn, dataOut);
                } else {
                    dataOut.writeInt(error);
                }

            }
        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
