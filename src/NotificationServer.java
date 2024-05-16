import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class NotificationServer {
    private static final int SERVER_PORT = 12345;
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Serwer nasłuchuje na porcie " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + clientSocket.getRemoteSocketAddress());
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Błąd serwera: " + e.getMessage());
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

                // Odczytanie notyfikacji od klienta
                String notification = in.readLine();
                String timeStr = in.readLine();
                int time = Integer.parseInt(timeStr);

                System.out.println("Otrzymano notyfikację: " + notification + ", czas: " + time);

                // Kolejkowanie notyfikacji
                ScheduledFuture<?> scheduledNotification = scheduleNotification(out, notification, time);
                scheduledNotification.get(); // Poczekaj na zakończenie zadania
            } catch (IOException | NumberFormatException | InterruptedException | ExecutionException e) {
                System.err.println("Błąd obsługi klienta: " + e.getMessage());
            }
        }

        private ScheduledFuture<?> scheduleNotification(PrintWriter out, String notification, int delayInSeconds) {
            return scheduler.schedule(() -> {
                System.out.println("Wysyłanie notyfikacji: " + notification);
                out.println(notification);
                out.flush(); // Ensure the message is sent
                System.out.println("Wysłano notyfikację: " + notification);
                return null;
            }, delayInSeconds, TimeUnit.SECONDS);
        }
    }
}
