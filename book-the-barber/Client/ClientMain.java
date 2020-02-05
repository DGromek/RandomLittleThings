package Zadanie4.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ClientMain {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ScheduledExecutorService threads;
    private String username;
    private Scanner scanner;

    public ClientMain(String ip, int port, String username) throws IOException {
        this.socket = new Socket(ip, port);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());

        this.threads = Executors.newScheduledThreadPool(2);
        this.scanner = new Scanner(System.in);
        this.username = username;

        threads.scheduleWithFixedDelay(this::serverListener, 0, 1, TimeUnit.MILLISECONDS);
        out.writeUTF(username);

        System.out.println("Nawiązano połączenie.");

        threads.scheduleWithFixedDelay(this::reserveTerm, 0, 1, TimeUnit.MILLISECONDS);
    }


    private void reserveTerm() {
        String appointment = scanner.nextLine();
        if (appointment.matches("1\\d:00")) {
            try {
                out.writeUTF(appointment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Nieprawidłowy format danych.");
        }
    }

    private void serverListener() {
        try {
            System.out.println(in.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new ClientMain("127.0.0.1", 2137, args[0]);
    }
}
