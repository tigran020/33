import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {

    public static final String HOST = "localhost";
    public static final int PORT = 59898;

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket(HOST, PORT);
        Scanner sc = new Scanner(System.in);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


        new Thread(() -> {
            try {
                while (true) {
                    try {
                        List<String> buff = (List<String>) in.readObject();
                        for (String msg : buff)
                            System.out.println(msg);
                    } catch (Exception ignored) {
                    }
                }
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            String clientName;

            try {
                System.out.println("Enter your name: ");
                clientName = sc.nextLine();
                System.out.println("Hi " + clientName + "! Enter your message.");

                while (true) {
                    if (sc.hasNextLine())
                        out.println(clientName + ": " + sc.nextLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
