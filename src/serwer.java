import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class serwer {
    private static final int SERVER_PORT = 12345;
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Serwer nasluchuje na porcie " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowy IP polaczony: " + clientSocket.getRemoteSocketAddress());
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Blad serwera: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String mess = in.readLine();
                String timeStr = in.readLine();
                int time = Integer.parseInt(timeStr);

                System.out.println("Otrzymano wiadmosc: " + mess + ", po " + time + " sekundach");

                ScheduledFuture<?> scheduledNotification = scheduleNotification(out, mess, time);
                scheduledNotification.get();
            } catch (IOException | NumberFormatException | InterruptedException | ExecutionException e) {
                System.err.println("Blad: " + e.getMessage());
            }
        }

        private ScheduledFuture<?> scheduleNotification(PrintWriter out, String mess, int delay) {
            return scheduler.schedule(() -> {
                System.out.println("Wysylanie wiadmosci: " + mess);
                out.println(mess);
                System.out.println("Wyslano wiadmosc: " + mess);
                return null;
            }, delay, TimeUnit.SECONDS);
        }
    }
}
