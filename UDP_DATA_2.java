import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP_DATA_2 {
    public static void main(String[] args) {
        String studentCode = "B23DCCN853";
        String qCode = "OTbVR19Z";

        String serverHost = "36.50.135.242";
        int serverPort = 2207;

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
            String[] values = parts[1].split(",");

            int maxValue = -1, minValue = Integer.MAX_VALUE;
            for (String value : values) {
                maxValue = Math.max(maxValue, Integer.parseInt(value.trim()));
                minValue = Math.min(minValue, Integer.parseInt(value.trim()));
            }

            String res = requestCode + ";" + maxValue + "," + minValue;

            byte[] responseData = res.getBytes(StandardCharsets.UTF_8);
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                    receivePacket.getAddress(), receivePacket.getPort());
            socket.send(responsePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
