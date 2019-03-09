import java.net.*;
import java.util.Scanner;
import java.io.*;
import java.util.*;
public class CarClient2 {

    //TODO: Compare BOTH MODES BETWEEN BOTH CLients and determine that both modes work on at least one of them.
    // TODO: also make sure that the port Numbers are hardcoded at the end. Also make a couple of test cases and CONGRATS!!


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
        UDPClient clientUDP = new UDPClient();
        TCPCLient clientTCP = new TCPCLient();

        try {
            Scanner sc = new Scanner(new FileReader(commandFile));
            boolean flagUDP = true ;
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
                        clientUDP.sendRequest(" rent " + customer + " " + carModel +  " " +carColor + " " + clientId);
                    }
                    if(flagTCP){
                        clientTCP.sendRequest(" rent " + customer + " " + carModel +  " " +carColor + " " + clientId);
                    }

                } else if (tokens[0].equals("return")) {

                    String customer = tokens[1].replace("\"", "") ;

                    if(flagUDP){
                        clientUDP.sendRequest(" return " + customer + " " + clientId);
                    }

                    if(flagTCP){
                        clientTCP.sendRequest(" return " + customer + " " + clientId);
                    }

                } else if (tokens[0].equals("inventory")) {

                    if(flagUDP){
                        clientUDP.sendRequest(" inventory " +  " " + clientId);
                    }

                    if(flagTCP){
                        clientTCP.sendRequest(" inventory " +  " " + clientId);
                    }

                } else if (tokens[0].equals("list")) {

                    String customer = tokens[1].replace("\"", "") ;


                    if(flagUDP){
                        clientUDP.sendRequest(" list " + customer + " " + clientId);
                    }

                    if(flagTCP){
                        clientTCP.sendRequest(" list " + customer + " " + clientId);
                    }


                } else if (tokens[0].equals("exit")) {

                        clientUDP.sendRequest(" exit ");
                        clientTCP.sendRequest(" exit ");
                        clientUDP.socket.close();
                        try {
                            clientTCP.clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
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
        //  private DatagramPacket packet;

        private byte[] buf = new byte[1024];


        public UDPClient() {
            try {
                socket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                address = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }


        public void sendRequest(String msg) {
            buf = msg.getBytes();

            if(msg.equals( " exit ")){
                buf = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);

            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                buf = "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ".getBytes();   // TODO: how to assign large empty buf
                packet = new DatagramPacket(buf, buf.length, address, 4445);
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = new String(packet.getData(), 0, packet.getLength());
            String[] responseThenID = received.split("//");
            if(received.equals(" exit ")){
                return;
            }

            try {
                for (String s: responseThenID) {
                    if(s.equals(" LIST ")){ continue;}
                    if(s.equals(" INVENTORY ")){ continue;}
                    if(s.equals(responseThenID[responseThenID.length-1])){
                        break;
                    }

                    PrintWriter writer = new PrintWriter(new FileWriter("outTEST_" +  responseThenID[responseThenID.length-1] + ".txt", true));
                    s = s.trim();
                    writer.println(s);
                    writer.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

    }



    public static class TCPCLient {
        Socket clientSocket = null;

        private byte[] buf = new byte[1024];


        public TCPCLient() {
            try {
                clientSocket = new Socket("localhost", 6789);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void sendRequest(String msg) {
            BufferedReader inFromServer = null;
            DataOutputStream outToServer = null;

            PrintWriter writer = null;
            try {
                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.writeBytes(msg + '\n');
                String recievedMessage = inFromServer.readLine();
                if(recievedMessage.equals(" exit ")){
                    return;
                }
                String[] responseThenID = recievedMessage.split("//");

                for (String s: responseThenID) {
                    if(s.equals(" LIST ")){ continue;}
                    if(s.equals(" INVENTORY ")){ continue;}
                    if(s.equals(responseThenID[responseThenID.length-1])){
                        break;
                    }
                    writer = new PrintWriter(new FileWriter("outTEST_" +  responseThenID[responseThenID.length-1] + ".txt", true));
                    s = s.trim();
                    writer.println(s);
                    writer.close();
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }


    }




}
