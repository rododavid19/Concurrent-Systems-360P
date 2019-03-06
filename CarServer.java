import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class CarServer {
  static String[] car_model;
  static String[] car_color;
  static int[] car_count;
  static ArrayList<serviceRecord> customer_records = new ArrayList<>();
  static int car_ticket;
  static String logMessage;
  static serviceRecord c;


    public synchronized static void rentRequest(String data){
        data = data.replace("rent", "" );
        String[] info = data.split( " ");                   // TODO: consider making these static global??

        if(isInventoryAvailable(info)){
            rentToCustomer(info);
            writeToClientLog(info);
        }



    }

    synchronized static boolean isInventoryAvailable(String[] item ){

     for(int i = 0; i <car_model.length; i++){
         if(item[1].equals(car_model[i]) && item[2].equals(car_color[i]) && car_count[i] > 0){
             car_ticket =  i;

            return true;
         }
     }

        return false;
    }

    synchronized static void rentToCustomer(String[] info){
        customer_records.add(new serviceRecord( info[0], info[1], info[2], info[3] ) );

        car_count[car_ticket]--;
    }

    static void writeToClientLog(String[] item){

    }





    public static void main (String[] args) {
    int tcpPort;
    int udpPort;


    if (args.length != 1) {
      System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
      System.exit(-1);
    }
    String fileName = args[0];
    tcpPort = 7000;
    udpPort = 8000;
    StringBuilder carModel = new StringBuilder();
    StringBuilder carColor = new StringBuilder();
    StringBuilder cartCount = new StringBuilder();




    try {


      BufferedReader reader = new BufferedReader(new FileReader(fileName));

      String currLine;

      int modelIndex;

      while ((currLine = reader.readLine()) != null){

       currLine = currLine.substring(1, currLine.length());
       modelIndex = currLine.indexOf("\"");
       carModel.append(currLine.substring(0, modelIndex) + " ");

       currLine = currLine.substring(modelIndex, currLine.length());
       currLine =currLine.replace(" ", "");
       currLine = currLine.substring(2, currLine.length());
       modelIndex = currLine.indexOf("\"");
       carColor.append(currLine.substring(0, modelIndex) + " ");

       currLine = currLine.substring(modelIndex, currLine.length());
       currLine = currLine.substring(1, currLine.length());
       cartCount.append(currLine + " ");
      }

     String car_model_list = carModel.toString();
      String[] cardModelArray = car_model_list.split(" ");

      String car_color_list = carColor.toString();
      String[] cardColorArray = car_color_list.split(" ");

      String car_count_list = cartCount.toString();
      String[] cardCountArray_init = car_count_list.split(" ");
      int[] cardCountArray = new int[cardCountArray_init.length];

      for(int i =0; i < cardCountArray_init.length; i++){
        cardCountArray[i] = Integer.parseInt(cardCountArray_init[i]);
      }

      car_model = cardModelArray;
      car_color = cardColorArray;
      car_count = cardCountArray;




      boolean asd = false;

      UDPServer testSeevrer = new UDPServer();
   //   DatagramPacket data = new DatagramPacket();
      testSeevrer.run();



    } catch (Exception e) {
      e.printStackTrace();
    }





    // parse the inventory file


    // TODO: handle request from clients
  }





  public static class UDPServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public UDPServer() {
      try {
        socket = new DatagramSocket(4445);
      } catch (SocketException e) {
        e.printStackTrace();
      }
    }

    public void run() {
      running = true;

      while (running) {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length);
        try {
          socket.receive(packet);

        } catch (IOException e) {
          e.printStackTrace();
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        String received = new String(packet.getData(), 0, packet.getLength());

          rentRequest(received);         // TODO: add service detection here then call







        if (received.equals("end")) {
          running = false;
          continue;
        }
        try {
          socket.send(packet);          // SEND RESPONSE HERE
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      socket.close();
    }
  }





  static class serviceRecord{

      String customerName;
      String carModel;
      String carColor;
      String clientId;


      public serviceRecord(String customerName, String carModel, String carColor, String clientId) {
          this.customerName = customerName;
          this.carModel = carModel;
          this.carColor = carColor;
          this.clientId = clientId;
      }




    public void TCP_server(){



    }


  }




}



