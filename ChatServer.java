
import java.net.ServerSocket;
import java.net.BindException;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.UnknownHostException;

/**
 * ChatServer Class final not inhertiable. is responsible for holding clientthread list and 
 * listening for new connections. Once new connection arivesm starts a ClientThread which will listen for messages
 * 
 * @author ashabbir
 */
public final class ChatServer {

    //private members
    private int serverPort;
    private ServerSocket server;
    private ConcurrentLinkedQueue<ClientThread> clientList; //unbounded and thread safe
    private BroadcastThread broadcastThread;

    /**
     * class constructor takes 1 args which is the serverPort
     *
     * @param serverPort int which is the the port this Server will sit on
     */
    public ChatServer(int serverPort) throws IOException {
        this.serverPort = serverPort;
		//create server socket
        server = new ServerSocket(serverPort);
		//initialize client list (thread safe collection)
        clientList = new ConcurrentLinkedQueue<ClientThread>();
		
        startBroadcastThread();
        startListenerThread();

    }
	
	
    /**
     * loop through each client in list and call its broad cast method
     *
     * @param message String which is the message to be broadcasted
     */
     public void broadcast(String message) {
		 //just syncronized the list for no one can add or remove while i am working on it
		synchronized(clientList){
			for (ClientThread c : clientList) {
           	 c.broadcast(message);
        	}
		}
    }

    /**
     * add mothod on clientlist
     *
     * @param toAdd ClientThread which to be added to list
     */
    public void addToClientList(ClientThread toAdd) {
		//just syncronized the list for no one can add or remove while i am working on it
		synchronized(clientList){
        	clientList.add(toAdd);
		}
    }

    /**
     * remove mothod on clientlist
     *
     * @param toAdd ClientThread which to be remove from list
     */
    public void removeFromClientList(ClientThread toRemove) {
		//just syncronized the list for no one can add or remove while i am working on it
		synchronized(clientList){
			clientList.remove(toRemove);
    	}
	}

    /**
     * putToBroadcastThread method, adds messages to messageList which is a custome Thread safe linklist
     *
     * @throws IOException will not try to handel exception but will bubble it
     * to caller
     * @throws InterruptedException will not try to handel exception but will bubble it
     * to caller
     */
    public void putToBroadcastThread(String message) throws IOException, InterruptedException {
        broadcastThread.put(message);
    }

    /**
     * method beginCommunication implementation depends on type of1 child server
     * will have its own implementation while client will have its own
     * implementation
     */
    public void startListenerThread() {
        try {
            //call to parent to create serverSocket
            while (true) {
                ClientThread clientThread = new ClientThread(this);
                clientThread.createClient(server.accept());
                clientThread.start();
            }
        } catch (IOException ie) {
            System.out.println("Excpetion in startlistenning thread");
        }
    }

    /**
     * method creates the broadcast thread
     * then it initialize it Initialize is Syncronize method for mutual exclusion
	 * then start the thread
     */
    public void startBroadcastThread() {
        broadcastThread = new BroadcastThread();
		broadcastThread.initialize(this);
        broadcastThread.start();
    }

	//internal mesage to get localhost name
    private String getMachineName() {
        String hostName;
        try {
            hostName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ue) {
            hostName = "Unknown";
        }
        return hostName;
    }
	
    //Custom toString method returns type and state of class
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder = builder.append("Server \nHost: ")
                .append(getMachineName()).append("\nport: ")
                .append(serverPort);
        builder = builder.append(getUserList());
        return builder.toString();
    }

    /**
     * method that creates string representation of all the connected client
     */
    public String getUserList() {
        StringBuilder builder = new StringBuilder();
        builder = builder.append("Chat Server : ").append(getMachineName())
                .append("\nPort: ").append(serverPort)
                .append("\nConnected user(s): ")
                .append(clientList.size()).append("\n");
		
		//just syncronized the list for no one can add or remove while i am working on it
		synchronized(clientList){
       	 for (ClientThread client : clientList) {
         	   builder = builder.append(client.getUserName()).append("\n");
        	}
		}
		
        return builder.toString();
    }

	//main method to run the Server
    public static void main(String[] args) {
        int serverPort = 12345;
        try {

            if (args.length > 0) {
                serverPort = Integer.parseInt(args[0]);
            }

            //checking if port is between desired rage.
            //throw exception if they are not
            if (serverPort > 49151 | serverPort < 1024) {
                throw new ServerPortRangeException();
            }

            System.out.println("Starting serve: " + serverPort);
            ChatServer server = new ChatServer(serverPort);

        } catch (java.lang.NumberFormatException | BindException nfe) //parsing error for port
        {
            System.out.println("port not correct - Binding failed");
        } catch (ServerPortRangeException spe) //custom exception
        {
            System.out.println("Serverport not allowed use  (1024 - 49151)");
        } catch (Exception ex) //other exceptions
        {
            System.out.println("There was an exception kindly recheck you args");
        }


    }
}
