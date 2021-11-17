import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 59898;
    public static final String HOST = "localhost";

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static List<String> buff = new ArrayList<>();
    private static ServerSocket serverSocket;


    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORT);
        Socket clientSocket = null;

        new Thread(() -> {
            while (true) {
                try {
                    for (ClientHandler client : clients) {
                        client.printMessages();
                    }
                    buff.clear();
                    Thread.sleep(5000);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            System.out.println("Server is running...");

            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket);
                clients.add(client);
                pool.execute(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Server is stopped");
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static class ClientHandler implements Runnable {

        private Socket clientSocket;
        private ObjectOutputStream out;
        private Scanner in;

        ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new Scanner(socket.getInputStream());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (in.hasNext()) {
                        String msg = in.nextLine();
                        buff.add(msg);
                        System.out.println("Buffer size is: " + buff.size());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void printMessages() throws IOException {
            if (buff.size() != 0) {
                out.writeObject(buff);
                out.reset();
            }
        }
    }
}
