import java.io.*;
import java.net.*;
import java.util.Scanner;

public class clientnew {
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Połączono z serwerem.");

            // Pobranie notyfikacji od użytkownika
            System.out.print("Podaj treść notyfikacji: ");
            String notification = scanner.nextLine();
            System.out.print("Podaj czas odesłania notyfikacji (w sekundach): ");
            String timeStr = scanner.nextLine();

            try {
                validateInput(notification, timeStr);
                out.println(notification);
                out.println(timeStr);
                out.flush();

                // Oczekiwanie na odpowiedź serwera
                System.out.println("Oczekiwanie na odpowiedź serwera...");

                // Dodanie opóźnienia, aby upewnić się, że klient czeka na odpowiedź
                String receivedNotification = null;
                for (int i = 0; i < 5; i++) { // Ponów próbę 5 razy z 1-sekundowym odstępem
                    Thread.sleep(1000);
                    if (in.ready()) {
                        receivedNotification = in.readLine();
                        break;
                    }
                }

                System.out.println("Otrzymana notyfikacja: " + receivedNotification);

                // Debugowanie
                if (receivedNotification == null) {
                    System.out.println("Błąd: otrzymana notyfikacja to null.");
                }
            } catch (InvalidInputException | InterruptedException e) {
                System.err.println("Błąd: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Błąd połączenia z serwerem: " + e.getMessage());
        }
    }

    private static void validateInput(String notification, String timeStr) throws InvalidInputException {
        if (notification == null || notification.trim().isEmpty()) {
            throw new InvalidInputException("Treść notyfikacji nie może być pusta.");
        }
        try {
            int time = Integer.parseInt(timeStr);
            if (time <= 0) {
                throw new InvalidInputException("Czas musi być większy od zera.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Nieprawidłowy format czasu.");
        }
    }
}

class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
