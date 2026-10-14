import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class UDP_STRING {
    public static void main(String[] args) {
        String studentCode = "B23DCCN853";
        String qCode = "xqsXKvRX";

        String serverHost = "36.50.135.242";
        int serverPort = 2208;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(serverHost);

            String message = ";" + studentCode + ";" + qCode;

            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
            socket.setSoTimeout(5000);

            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8)
                    .trim();

            String[] parts = response.split(";");

            String requestCode = parts[0];
            String data = parts[1].trim();

            Map<Character, Integer> map = new LinkedHashMap<>();

            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                map.put(c, map.getOrDefault(c, 0) + 1);
            }

            int maxCount = 0;

            for (int count : map.values()) {
                if (count > maxCount) {
                    maxCount = count;
                }
            }

            char x = 0;

            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                if (map.get(c) == maxCount) {
                    x = c;
                    break;
                }
            }

            StringBuilder s = new StringBuilder();

            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == x) {
                    if (s.length() > 0) {
                        s.append(",");
                    }
                    s.append(i + 1);
                }
            }

            String res = requestCode + ";" + x + ":" + s + ",";

            byte[] responseData = res.getBytes(StandardCharsets.UTF_8);
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                    receivePacket.getAddress(), receivePacket.getPort());
            socket.send(responsePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
