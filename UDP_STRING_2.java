import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP_STRING_2 {
    public static void main(String[] args) {
        String studentCode = "B23DCCN853";
        String qCode = "WLGKHzAz";

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
            String[] data = parts[1].split(" ");

            StringBuilder sb = new StringBuilder();
            for (String word : data) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < word.length(); i++) {
                    if (i == 0) {
                        s.append(Character.toUpperCase(word.charAt(i)));
                    } else {
                        s.append(Character.toLowerCase(word.charAt(i)));
                    }
                }
                sb.append(s.toString()).append(" ");
            }

            String res = requestCode + ";" + sb.toString().trim();

            byte[] responseData = res.getBytes(StandardCharsets.UTF_8);
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                    receivePacket.getAddress(), receivePacket.getPort());
            socket.send(responsePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}