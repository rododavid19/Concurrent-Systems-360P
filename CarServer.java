import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class CarServer {
    static String[] car_model;
    static String[] car_color;
    static int[] car_count;
    static ArrayList<serviceRecord> customer_records = new ArrayList<>();
    static int car_ticket;
    static String logMessage;
    static int recordID = 1;

    public synchronized static void rentRequest(String data){
        data = data.replace(" rent ", "" );
        String[] info = data.split( " ");                   // TODO: consider making these static global??

        if(isInventoryAvailable(info)){
            rentToCustomer(info);
            logMessage ="Your request has been approved, " + customer_records.get(customer_records.size()-1).recordID + " " + info[0] + " \"" + info[1] + "\" \"" + info[2] + "\"" + "//" + info[3];
            System.out.println(logMessage);
        }
    }

    static synchronized void listCustomerInfo(String data){

        data = data.replace(" list ", "" );
        String info[] = data.split(" ");

        logMessage = " LIST //";
        for ( serviceRecord c: customer_records) {

            if(c.customerName.equals(info[0])){
                logMessage = logMessage + c.recordID + " \"" +c.carModel+ "\" \"" + c.carColor + "\"" + "//";
            }
        }
        logMessage = logMessage + info[1];
    }

    synchronized static boolean isInventoryAvailable(String[] info ){

        for(int i = 0; i <car_model.length; i++){
            if(info[1].equals(car_model[i]) && info[2].equals(car_color[i]) && car_count[i] > 0){
                car_ticket =  i;

                return true;
            }
        }
        logMessage ="Request Failed - We do not have this car//" + info[3];
        return false;
    }

    synchronized static void rentToCustomer(String[] info){
        customer_records.add(new serviceRecord( info[0], info[1], info[2], recordID ) );
        recordID++;
        car_count[car_ticket]--;
    }

    static synchronized void returnCar(String data){
        data = data.replace(" return ", "" );
        String info[] = data.split(" ");

        for ( serviceRecord s: customer_records) {
            if(s.recordID == Integer.parseInt(info[0])){

                logMessage = info[0] + " is returned" + "//" + info[1];
                customer_records.remove(s);

                for(int i =0; i < car_model.length; i++){
                    if(s.carModel.equals(car_model[i])){
                        car_count[i]++;
                    }
                }
                return;
            }
        }


        logMessage = info[0] +" not found, no such rental record" + "//" + info[1];
    }

    static synchronized void listInventory(String data){
        data = data.replace(" inventory ", "" );
        String info[] = data.split(" ");

        logMessage = " INVENTORY //";
        for ( int i = 0; i < car_model.length; i++){
            logMessage =  logMessage + "\"" + car_model[i] +"\" \"" + car_color[i] + "\" " + car_count[i] + "//";
        }
        logMessage = logMessage + info[1];
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

            ForkJoinPool mainPool = new ForkJoinPool();
            UDPServer udpTasks = new UDPServer();
            TCPServer tcpTasks = new TCPServer();
            mainPool.execute(udpTasks);
            mainPool.execute(tcpTasks);

            while (true){ }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public static class UDPServer extends RecursiveAction {
        private DatagramSocket socket;
        private boolean running;
        private byte[] buf = new byte[1024];

        public UDPServer() {
            try {
                socket = new DatagramSocket(4445);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void compute() {

            running = true;

            while (running) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                String received = new String(packet.getData(), 0, packet.getLength());
                if(received.contains(" rent ")){ rentRequest(received); }
                if(received.contains(" list ")){ listCustomerInfo(received);  }
                if(received.contains(" return ")){ returnCar(received);  }
                if(received.contains(" inventory ")){ listInventory(received);  }

                if (received.contains(" exit ")) {
                    try {
                        socket.disconnect();
                        socket.close();
                        socket = new DatagramSocket(4445);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                byte[] log = logMessage.getBytes();
                packet.setData(log, 0, log.length);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

        }

    }




    public static class TCPServer extends RecursiveAction {
        private ServerSocket welcomeSocket;
        private boolean running;
        private  String clientMessage;
        private  Socket reciever;

        public TCPServer() {
            try {
                welcomeSocket = new ServerSocket(6789);
                reciever  = welcomeSocket.accept();
                boolean ddd = false;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void compute() {
            running = true;

            while (running) {
                try {
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(reciever.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(reciever.getOutputStream());
                    clientMessage = inFromClient.readLine();

                    if(clientMessage.contains(" rent ")){ rentRequest(clientMessage); }
                    if(clientMessage.contains(" list ")){ listCustomerInfo(clientMessage);  }
                    if(clientMessage.contains(" return ")){ returnCar(clientMessage);  }
                    if(clientMessage.contains(" inventory ")){
                        listInventory(clientMessage);
                    }

                    if (clientMessage.contains(" exit ")) {
                        outToClient.writeBytes(" exit " + '\n');
                        reciever.close();
                        reciever = welcomeSocket.accept();
                        continue;
                    }


                    outToClient.writeBytes(logMessage + '\n');


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class serviceRecord{

        public serviceRecord(String customerName, String carModel, String carColor, int recordID) {
            this.customerName = customerName;
            this.carModel = carModel;
            this.carColor = carColor;
            this.recordID = recordID;
        }

        String customerName;
        String carModel;
        String carColor;
        int   recordID;
    }

}



