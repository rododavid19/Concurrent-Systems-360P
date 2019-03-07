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
    UDPClient client = null;

    try {
        Scanner sc = new Scanner(new FileReader(commandFile));
        boolean flagUDP = false ;
        boolean flagTCP = false ;

        while(sc.hasNextLine()) {
          String cmd = sc.nextLine();
          String[] tokens = cmd.split(" ");

          if (tokens[0].equals("setmode")) {


             if(tokens[1].equals("U")){
                flagUDP = true;
                flagTCP = false;
             }
              if(tokens[1].equals("T")){
                  flagTCP = true;
                  flagUDP = false;
              }
          }
          else if (tokens[0].equals("rent")) {

              String customer = tokens[1].replace("\"", "") ;
              String carModel = tokens[2].replace("\"", "");
              String carColor = tokens[3].replace("\"" , "") ;

              if(flagUDP){
                  client = new UDPClient();
                  client.sendRequest(" rent " + customer + " " + carModel +  " " +carColor + " " + clientId);
              }

              if(flagTCP){
                //  client = new ();
                  client.sendRequest(" rent " + customer + " " + carModel +  " " +carColor + " " + clientId);
              }

          } else if (tokens[0].equals("return")) {

              String customer = tokens[1].replace("\"", "") ;
              if(flagUDP){
                  client = new UDPClient();
                  client.sendRequest(" return " + customer + " " + clientId);
              }

          } else if (tokens[0].equals("inventory")) {

              if(flagUDP){
                  client = new UDPClient();
                  client.sendRequest(" inventory " +  " " + clientId);
              }

          } else if (tokens[0].equals("list")) {

              String customer = tokens[1].replace("\"", "") ;


              if(flagUDP){
                  client = new UDPClient();
                  client.sendRequest(" list " + customer + " " + clientId);

              }


          } else if (tokens[0].equals("exit")) {

              if(flagUDP){
                  client.sendRequest(" exit ");
              }

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

        private byte[] buf = new byte[1024];


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


        public String sendRequest(String msg) {
            buf = msg.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            try {
                socket.send(packet);            // SENDS CUSTOMER DESIRE
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                buf = "                                                                                                                                                                                                                                                                                        ".getBytes();   // TODO: how to assign large empty buf
                packet = new DatagramPacket(buf, buf.length, address, 4445);
               socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = new String(packet.getData(), 0, packet.getLength());

            try {
                String[] responseThenID = received.split("//");
                PrintWriter writer = new PrintWriter(new FileWriter("outTEST_" +  responseThenID[1] + ".txt", true));
                responseThenID[0] = responseThenID[0].trim();
                writer.println(responseThenID[0]);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return received;
        }



        public void close() {
            socket.close();
        }
    }



    public static class TCPCLient {
        private DatagramSocket socket;
        private InetAddress address;

        private byte[] buf = new byte[1024];


        public TCPCLient() {
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


        public String sendRequest(String msg) {
            buf = msg.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            try {
                socket.send(packet);            // SENDS CUSTOMER DESIRE
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                buf = "                                                                                                                                                                                                                                                                                        ".getBytes();   // TODO: how to assign large empty buf
                packet = new DatagramPacket(buf, buf.length, address, 4445);
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = new String(packet.getData(), 0, packet.getLength());

            try {
                String[] responseThenID = received.split("//");
                PrintWriter writer = new PrintWriter(new FileWriter("outTEST_" + responseThenID[1] + ".txt", true));
                responseThenID[0] = responseThenID[0].trim();
                writer.println(responseThenID[0]);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return received;
        }

    }




}
