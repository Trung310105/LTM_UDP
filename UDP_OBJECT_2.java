import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import UDP.Customer;

public class UDP_OBJECT_2 {
    public static String normaLizaName(String name) {
        StringBuilder sb = new StringBuilder();

        String[] words = name.trim().toLowerCase().split("\\s+");

        sb.append(words[words.length - 1].toUpperCase());
        sb.append(", ");
        for (int i = 0; i < words.length - 1; i++) {
            sb.append(Character.toUpperCase(words[i].charAt(0)));
            sb.append(words[i].substring(1));
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    public static String normaLizaDayOfBirth(String dayOfBirth) {
        StringBuilder sb = new StringBuilder();

        String[] parts = dayOfBirth.split("-");
        sb.append(parts[1]).append("/").append(parts[0]).append("/").append(parts[2]);
        return sb.toString();
    }

    public static String createUserName(String name) {
        String[] words = name.trim().toLowerCase().split("\\s+");

        StringBuilder userName = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            userName.append(words[i].charAt(0));
        }

        userName.append(words[words.length - 1]);
        return userName.toString();
    }

    public static void main(String[] args) {
        String studentCode = "B23DCCN853";
        String qCode = "eksoBA8J";

        String serverHost = "36.50.135.242";
        int serverPort = 2209;

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

            byte[] packetData = receivePacket.getData();
            int packetLength = receivePacket.getLength();

            byte[] requestId = new byte[8];
            System.arraycopy(packetData, 0, requestId, 0, 8);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(packetData, 8, packetLength - 8);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            Customer customer = (Customer) objIn.readObject();

            String name = customer.getName();
            String normalizedName = normaLizaName(name);
            String normalizedDayOfBirth = normaLizaDayOfBirth(customer.getDayOfBirth());
            customer.setName(normalizedName);
            customer.setDayOfBirth(normalizedDayOfBirth);
            customer.setUserName(createUserName(name));

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

            objOut.writeObject(customer);
            objOut.flush();

            byte[] customerData = byteOut.toByteArray();
            byte[] responseData = new byte[8 + customerData.length];

            System.arraycopy(requestId, 0, responseData, 0, 8);
            System.arraycopy(customerData, 0, responseData, 8, customerData.length);

            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                    receivePacket.getAddress(), receivePacket.getPort());
            socket.send(responsePacket);

            objIn.close();
            objOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
