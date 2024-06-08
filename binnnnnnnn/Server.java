import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class Server {
    public static void main(String[] args) throws Exception {
        try (ServerSocket ss = new ServerSocket(9876)) {
            System.out.println("Server is ready");
            while (true) {
                Socket s = ss.accept();
                new Thread(() -> handleClient(s)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket s) {
        try {
            System.out.println("Connected to client");
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            while (true) {
                String city = din.readUTF();
                System.out.println("Client: " + city);
                String link = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=cabc9614649278ff314eb4f62e95942e&mode=xml";
                @SuppressWarnings("deprecation")
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder sb = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine);
                }
                in.close();

                dout.writeUTF(sb.toString());
                dout.flush();
                System.out.println("Response sent");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}