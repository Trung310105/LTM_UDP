import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class UDP_DATA {
    public static void main(String[] args) {
        String StudentCode = "B23DCCN853";
        String qCode = "fMBzhcpr";

        String serverHost = "36.50.135.242";
        int serverPort = 2207;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(serverHost); // Lấy địa chỉ IP của server từ tên miền

            String message = ";" + StudentCode + ";" + qCode;

            byte[] sendData = message.getBytes(StandardCharsets.UTF_8); // Chuyển chuỗi sang mảng byte sử dụng UTF-8
            // Tao gói dữ liệu UDP để gửi đến server
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

            socket.send(sendPacket); // Gửi gói dữ liệu UDP đến server

            socket.setSoTimeout(5000); // Set timeout

            byte[] receiveData = new byte[4096];
            // Tao gói dữ liệu UDP để nhận phản hồi từ server
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            socket.receive(receivePacket); // Nhận phản hồi từ server

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8)
                    .trim(); // Chuyển mảng byte nhận được từ server sang chuỗi sử dụng UTF-8 và loại bỏ
                             // khoảng trắng thừa

            String[] parts = response.split(";", -1);
            String requestCode = parts[0];
            Integer n = Integer.parseInt(parts[1].trim());

            Set<Integer> ls = new HashSet<>();
            String[] values = parts[2].split(",");

            String res = requestCode + ";";

            for (String value : values) {
                if (!value.isEmpty()) {
                    ls.add(Integer.parseInt(value.trim()));
                }
            }

            for (int i = 1; i <= n; i++) {
                if (!ls.contains(i)) {
                    res += String.valueOf(i) + ",";
                }
            }

            res = res.substring(0, res.length() - 1);

            byte[] responseData = res.getBytes(StandardCharsets.UTF_8); // Chuyển chuỗi phản hồi sang mảng byte sử dụng
                                                                        // UTF-8
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                    receivePacket.getAddress(),
                    receivePacket.getPort()); // Tao gói dữ liệu UDP để gửi phản hồi đến server
            socket.send(responsePacket); // Gửi phản hồi đến server
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}