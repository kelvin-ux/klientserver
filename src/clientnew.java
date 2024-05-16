import java.io.*;
import java.net.*;
import java.util.Scanner;




public class clientnew {
    private static final int SERVER_PORT = 12345;
    private static final int MAX_WAIT_TIME = 15;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Połączono z serwerem.");

            System.out.print("Podaj wiadomosc: ");
            String notification = scanner.nextLine();
            System.out.print("Podaj czas : ");
            String timeStr = scanner.nextLine();

            try {
                validate(notification, timeStr);
                out.println(notification);
                out.println(timeStr);
                out.flush();

                System.out.println("Oczekiwanie na odpowiedz ...");

                String receivedNotification = null;
                for (int i = 0; i < MAX_WAIT_TIME; i++) {
                    Thread.sleep(1000);
                    if (in.ready()) {
                        receivedNotification = in.readLine();
                        break;
                    }
                }

                System.out.println("Otrzymana wiadmosc: " + receivedNotification);

            } catch (InvalidInputException | InterruptedException e) {
                System.err.println("Blad: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Bład polaczenia z serwerem: " + e.getMessage());
        }
    }

    private static void validate(String notification, String timeStr) throws InvalidInputException {
        if (notification == null || notification.trim().isEmpty()) {
            throw new InvalidInputException("Wiadmosc nie moze być pusta.");
        }
        try {
            int time = Integer.parseInt(timeStr);
            if (time <= 0) {
                throw new InvalidInputException("Czas musi być wiekszy od zera.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Nieprawidlowy format musisz podac czas w sekundach.");
        }
    }
}



class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
