//step 2 make chat core and test it 
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * client Core second component for the chat client. CLient core is responsible for
 * handeling communication with the client. IT can work without the Chat window. void main is provided to test
 * basic connectivity. Generated UUID which is unique per user.
 * UUID is further used by the server to to identify differnt connect users even with the same nick
 *
 * @author ashabbir
 */
public class ClientCore {

	//private members
    private int serverPort;
    private String userName;
	private UUID userId; //client can have same username but unique Ids would keep them seperate at server
    private String serverHost;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private ChatWindow chatUI;

	
    /**
     * class constructor takes 3 args host serverPort userName, create a client
     * server, generate UUID.
     *
     * @param serverHost is the hostmachine name where server is running:
     * (localHost or 0.0.0.0)
     * @param serverPort is the port number server is running on :use (49152 -
     * 65535)
     * @param userName String which is the nick used in chat( ahmed)
     * @throws IOException will not try to handel exception but will bubble it
     * to caller
     * @throws UnknownHostException will not try to handel exception but will bubble it
     * to caller
     */
    public ClientCore(String serverHost, int serverPort, String userName) throws UnknownHostException, IOException {
        socket = new Socket(serverHost, serverPort);
        this.userName = userName;
		userId =  UUID.randomUUID();
    }


    /**
     * setter for chatUI 
     *
     * @param chatUI is the ChatUI used with this core
     */
    public void setChatUI(ChatWindow chatUI) {
        this.chatUI = chatUI;
    }


	//getter for userName 
    public String getUserName() {
        return userName;
    }
	
	//getter for UserId in string
    public String getUserId() {
        return userId.toString();
    }
	
	
    /**
     * bindtostream method, binds client incomming and outgoing stream with
     * print writer and scanner
     *
     * @throws IOException will not try to handel exception but will bubble it
     * to caller
     */
    public void bindToStream() throws IOException {
        output = new PrintWriter(socket.getOutputStream());
        input = new Scanner(socket.getInputStream());

    }

    /**
     * sendStatus method, Sends Username Userid and ask for Connect client to the server
     * is used on initial connection
     */
    public void sendStatus() {
        sendMessageToServer(userName);
		sendMessageToServer(userId.toString());
        sendMessageToServer(getUserId() + ": Give Me User List");
    }

    /**
     * close all connections, If ChatUI is present close as well
     *
     * @throws IOException bubbles IOException to Caller
     */
    private void closeAll() throws IOException {
        input.close();
        output.close();
        socket.close();
		if(chatUI != null) {
			chatUI.exitWindowGracefully();
		}

    }

    /**
     * utility fuction this send message to self and to the client
     */
    private void sendMessageToServer(String message) {
        if (!output.checkError()) //Check for the Input Stream of Client
        {
            output.println(message);
            output.flush();
        }

    }

    /**
     * utility fuction this send message to self and to the client
     */
    private void sendMessageToSelf(String message) {
        if (chatUI == null) {
            //test mode
            System.out.println(message);
        } else {
            chatUI.printInWindow(message);
        }

    }

    /**
     * utility fuction this send message to self and to the client
	 * @throws IOException bubbles IOException to Caller	
     */
    public void broadcast(String message) throws IOException {
        sendMessageToServer(message);
    }

    /**
     * listenForMessages method, busy waiting listening for message if is server
     * will notify if a client connects will keep looping and listen for
     * messages print to UI when received
     */
    public void listenForMessages() {
        try {
            try {
                String message;
				//get the messsage if is present
                while (input.hasNext()) {
                    message = input.nextLine();		
                    sendMessageToSelf(message);
                }


            } finally {
                closeAll();
            }
        } catch (Exception ex) {
        }


    }

    //Custom toString method returns type and state of class
    @Override
    public String toString() {
        return "UserName: " + userName;
    }

    //to Test run a server from old home work this client core will connect and send messages
    public static void main(String[] args) {

        try {
			//test 1 - create core, bind and send status 
            ClientCore core = new ClientCore("localHost", 12345, "test");
			core.bindToStream();
            core.sendStatus();
			//test 2 - send messages to test
            for (int i = 1; i < 6; i++) {
                core.broadcast("" + i + " ping");
            }
			
			//test 3 - now listen for messages
            core.listenForMessages();


        } catch (UnknownHostException ex) {
            System.out.println("host not found");
        } catch (IOException io) {
            System.out.println("cant connect to server");
        }
    }
}