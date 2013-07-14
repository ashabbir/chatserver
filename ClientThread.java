
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.util.UUID;


/**
 * client thread class. is responsible for creating client Connection, holding the client streams and
 * handeling communication with.
 * Once Connected, stores the CHatServer instance, bings streams and gets UserName and UserId. Then adds it 
 * to server Client List
 *
 * @author ashabbir
 */
class ClientThread extends Thread {

    private String userName;
	private UUID userId;
    private PrintWriter output;
    private Scanner input;
    private Socket client;
    private ChatServer chatServer;


    /**
     * class constructor takes 1 args which is the ChatServer
     *
     * @param chatServer is the ChatServer which this client thread blongs to
     */
    public ClientThread(ChatServer chatServer) {
        super();
        this.chatServer = chatServer;

    }
	
    /**
     * Binds the clientsocket input output. 
     * receives username and uuid. then adds thread to Server list
     * @param client is the socket accepted by the server
	 * @throws IOException bubbles IOException to Caller
     */
    synchronized public void createClient(Socket client) throws IOException {
        output = new PrintWriter(client.getOutputStream()); //bind output
        input = new Scanner(client.getInputStream()); //bind input

        userName = input.nextLine();		//User Name the First time
		userId = UUID.fromString(input.nextLine());			//UserID the First time
        chatServer.addToClientList(this); //add the Thread to Server's thread safe Client List
    }

    /**
     * close all the connections
     */
    synchronized public void close() {
        try {
            chatServer.removeFromClientList(this);
            input.close();
            output.close();
            if (client != null) {
                client.close();
            }

        } catch (Exception ioe) {
        }

    }

    /**
     * Binds the clientsocket input output. 
     * receives username and uuid. then adds thread to Server list
     * @param message String message that is to be broadcasted
     */
    synchronized public void broadcast(String message) {
        if (output.checkError()) //Check for the Input Stream of Client
        {
            System.out.println(userName + " can not be reached");
        } else {	
            /*shabbir: 31fa6a41-e3a1-4200-a1d5-4faa91793694: Give Me User List 
				expected messege
				*/
            if (message.equals(userName+ ": " + userId.toString() + ": Give Me User List")) {
                message = chatServer.getUserList();
                output.println(message);
                output.flush();
            } else if (!message.contains(": Give Me User List")) {
                output.println(message);
                output.flush();
            }
        }
    }
	
	/**
	* getter for userName
	*/
    synchronized public String getUserName() {
        return userName;
    }
	
	

    @Override
    public void run() {
        String message;
        try {
            try {

                chatServer.putToBroadcastThread(userName + " has joined the chat room ");
                while (input.hasNext()) {
                    message = input.nextLine();		//get the messsage
                    chatServer.putToBroadcastThread(userName + ": " + message);
                }
            } finally {
                chatServer.putToBroadcastThread(userName + " has left the chat room");
				close();
            }
        } catch (IOException | InterruptedException ie) {
        }

    }
}
