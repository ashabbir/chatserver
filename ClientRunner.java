//step 3 combine Core and  UI 
import java.io.IOException;
import java.awt.EventQueue;


/**
 * ClientRunner class, create core which is connected to server 
 * then creates UI in Dispatcher and bind them. Marked final to prevent inheritence
 * this class is a container for static methods.
 *
 * @author ashabbir
 */
public final class ClientRunner {

	//private members
    private static ChatWindow chatUI;
    private static ClientCore clientCore;
    private static int serverPort;
    private static String userName;
    private static String serverHost;

    //marked private so other classes can not create instance
    private ClientRunner() {
    }

	//parse args and set variables
    public static void parseArgs(String[] args) {
        //try parse Args
        serverPort = 12345;
        userName = "shabbir";
        serverHost = "LocalHost";
        try {
            //check if args are present and then try assigning them
            if (args.length == 3) {
                //if we have three args try parse and assign
                serverHost = args[0];
                serverPort = Integer.parseInt(args[1]);
                userName = args[2];
            } else if (args.length == 2) {
                //two args present host and port 
                serverHost = args[0];
                serverPort = Integer.parseInt(args[1]);
            } else if (args.length == 1) {
                //only the host arg is present
                serverHost = args[0];
            }

            //make sure args have the desired port between 1024 to 49151
        //    if (serverPort > 49151 | serverPort < 1024) {
        //        throw new ServerPortRangeException();
        //    }
        } catch (java.lang.NumberFormatException nfe) //parsing error for port
        {
            System.out.println("port not correct");
        } catch (Exception ex) //other excetions
        {
            System.out.println("There was an exception kindly recheck you args");
        }

    }

	//create core and bind to UI
    public static void main(String[] args) {
        //try parse args or set default values
        parseArgs(args);
        try {
            clientCore = new ClientCore(serverHost, serverPort, userName);
            clientCore.bindToStream();


            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    chatUI = new ChatWindow(clientCore);
                    chatUI.setVisible(true);
                    chatUI.toggleUI(true);
                    clientCore.sendStatus();
                }
            });

            clientCore.listenForMessages();
        } catch (java.net.UnknownHostException uh) {
            System.out.println("host name not valid");
        } catch (IOException ioe) {
            System.out.println("Could not connect  - server might be down");
        }
    }
}