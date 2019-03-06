import java.net.*;
import java.util.Scanner;
import java.io.*;
import java.util.*;
public class CarClient {


  public static void main (String[] args) {
    String hostAddress;
    int tcpPort;
    int udpPort;
    int clientId;

    if (args.length != 2) {
      System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
      System.out.println("\t(1) <command-file>: file with commands to the server");
      System.out.println("\t(2) client id: an integer between 1..9");
      System.exit(-1);
    }

    String commandFile = args[0];
    clientId = Integer.parseInt(args[1]);
    hostAddress = "localhost";
    tcpPort = 7000;// hardcoded -- must match the server's tcp port
    udpPort = 8000;// hardcoded -- must match the server's udp port

    try {
        Scanner sc = new Scanner(new FileReader(commandFile));
        boolean flagUDP = false ;
        boolean flagTCP = false ;

        while(sc.hasNextLine()) {
          String cmd = sc.nextLine();
          String[] tokens = cmd.split(" ");

          if (tokens[0].equals("setmode")) {
            // TODO: set the mode of communication for sending commands to the server

             if(tokens[1].equals("T")){         // TODO: CHANGE LETTER TO U
                flagUDP = true;
             }
          }
          else if (tokens[0].equals("rent")) {
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server


              String customer = tokens[1].replace("\"", "") ;
              String carModel = tokens[2].replace("\"", "");
              String carColor = tokens[3].replace("\"" , "") ;

              if(flagUDP){
                  UDPClient client = new UDPClient();
                  client.sendRentRequest("rent" + customer + " " + carModel +  " " +carColor + " " + clientId);
              }







          } else if (tokens[0].equals("return")) {
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
          } else if (tokens[0].equals("inventory")) {
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
          } else if (tokens[0].equals("list")) {
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
          } else if (tokens[0].equals("exit")) {
            // TODO: send appropriate command to the server 
          } else {
            System.out.println("ERROR: No such command");
          }
        }
    } catch (FileNotFoundException e) {
	e.printStackTrace();
    }
  }


    public static class UDPClient {
        private DatagramSocket socket;
        private InetAddress address;

        private byte[] buf;

        public UDPClient() {
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                address = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        public String sendEcho(String msg) {
            buf = msg.getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, 4445);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            return received;
        }

        public String sendRentRequest(String msg) {
            buf = msg.getBytes();

            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, 4445);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            return received;
        }



        public void close() {
            socket.close();
        }
    }
}
