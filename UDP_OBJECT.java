import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import UDP.Student;

public class UDP_OBJECT {

    public static String normaLizeName(String name) {
        StringBuilder sb = new StringBuilder();

        String[] words = name.trim().toLowerCase().split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1));
                sb.append(" ");
            }
        }

        return sb.toString().trim();
    }

    public static String createEmail(String name) {
        String[] words = name.trim().toLowerCase().split("\\s+");

        StringBuilder email = new StringBuilder();

        email.append(words[words.length - 1]);

        for (int i = 0; i < words.length - 1; i++) {
            email.append(words[i].charAt(0));
        }

        email.append("@ptit.edu.vn");
        return email.toString();
    }

    public static void main(String[] args) {
        String studentCode = "B23DCCN853";
        String qCode = "EDif7hy2";

        String serverHost = "36.50.135.242";
        int serverPort = 2209;

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(serverHost);

            // Gui yeu cau den server
            String message = ";" + studentCode + ";" + qCode;
            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);

            socket.setSoTimeout(5000);

            // Nhan du lieu tu server
            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(
                    receiveData, receiveData.length);

            socket.receive(receivePacket);

            byte[] packetData = receivePacket.getData();
            int packetLength = receivePacket.getLength();

            // Lay 8 byte requestId
            byte[] requestId = new byte[8];
            System.arraycopy(packetData, 0, requestId, 0, 8);

            // Doc doi tuong Student tu phan con lai
            ByteArrayInputStream byteIn = new ByteArrayInputStream(
                    packetData, 8, packetLength - 8);

            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            Student student = (Student) objIn.readObject();

            // Chuan hoa ho ten va tao email
            String normalizedName = normaLizeName(student.getName());
            student.setName(normalizedName);
            student.setEmail(createEmail(normalizedName));

            // Ma hoa lai doi tuong Student
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

            objOut.writeObject(student);
            objOut.flush();

            byte[] objData = byteOut.toByteArray();

            // Ghep requestId voi doi tuong da sua
            byte[] resData = new byte[8 + objData.length];

            System.arraycopy(requestId, 0, resData, 0, 8);
            System.arraycopy(objData, 0, resData, 8, objData.length);

            // Gui ket qua ve server
            DatagramPacket responsePacket = new DatagramPacket(
                    resData,
                    resData.length,
                    receivePacket.getAddress(),
                    receivePacket.getPort());

            socket.send(responsePacket);

            objIn.close();
            objOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}