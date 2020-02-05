package Zadanie4.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerMain {

    private ServerSocket serverSocket;
    private ScheduledExecutorService threads;
    private volatile Map<String, String> schedule;
    private List<DataOutputStream> outs;

    public ServerMain(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.threads = Executors.newScheduledThreadPool(10);
        this.schedule = new HashMap<>();
        this.outs = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            schedule.put("1" + i + ":00", "free");
        }

        threads.scheduleWithFixedDelay(this::initConnections, 0, 1, TimeUnit.MILLISECONDS);
    }

    private void initConnections() {
        try {
            Socket s = serverSocket.accept();
            System.out.println("Podłączono użytkownika.");
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            DataInputStream in = new DataInputStream(s.getInputStream());
            outs.add(out);

            ClientHandler clientHandler = new ClientHandler(out, in);
            threads.scheduleWithFixedDelay(clientHandler::handleClient, 0, 1, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class ClientHandler {

        private DataOutputStream out;
        private DataInputStream in;
        private String username;

        public ClientHandler(DataOutputStream out, DataInputStream in) throws IOException {
            this.out = out;
            this.in = in;
            this.username = in.readUTF(); //Get client username;
            System.out.println("Połączono " + username);
            for (Map.Entry<String, String> entry : schedule.entrySet()) {
                out.writeUTF(entry.getKey() + " - " + entry.getValue());
            }
            out.writeUTF("Podaj termin, który chcesz zarezerwować: ");
        }

        private void handleClient() {
            try {
                boolean flag = false;
                String appointment = in.readUTF();
                appointment.matches("1\\d:00");

                if (schedule.get(appointment).equals(username)) {
                    schedule.put(appointment, "free");
                    flag = true;
                } else if (schedule.get(appointment).equals("free")) {
                    schedule.put(appointment, username);
                    flag = true;
                } else {
                    out.writeUTF("Nie możesz zarezerwować tego terminu.");
                }

                if (flag) {
                    for (DataOutputStream outIter : outs) {
                        for (Map.Entry<String, String> entry : schedule.entrySet()) {
                            outIter.writeUTF(entry.getKey() + " - " + entry.getValue());
                        }
                        outIter.writeUTF("Podaj termin, który chcesz zarezerwować lub anulować: ");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws IOException {
        new ServerMain(2137);
    }
}

