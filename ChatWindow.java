//step 1 make a chat window and test it
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.text.*;
import java.io.IOException;

/**
 * ChatWindow class, basic chat window  implementation 
 *
 * @author ashabbir
 */
final class ChatWindow extends JFrame {
    //ui private members

    private JButton btnSend;
    private JButton btnList;
    private JTextArea txtAreaChatWorld;
    private JTextField txtMessage;
    private JLabel lblStatus;
    private ClientCore core;

    /**
     * class constructor takes no args, calls super
     * renders window and sets title
     */
    public ChatWindow() {
        super();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center it 
        setTitle("Client");
        createUI();
    }

    /**
     * class constructor takes 1 args ClientCore, calls super
     * renders window, attaches core and sets title
     * @param core is the CLientCore used for connectivity with server
     */
    public ChatWindow(ClientCore core) {
        this();
        setClientCore(core);
    }

    /**
     * utility fuction to bind core
     */
    private void setClientCore(ClientCore core) {
        this.core = core;
		core.setChatUI(this);
        setTitle(core.getUserName());
    }

    /**
     * utility fuction to toggle UI on and off for sending messages
     */
    public void toggleUI(boolean isEnabled) {
        btnSend.setEnabled(isEnabled);
        btnList.setEnabled(isEnabled);
        txtMessage.setEnabled(isEnabled);
        updateStatus(isEnabled ? "Enabled" : "Disabled");
    }


    /*
     * creates UI and add ActionListeners.
     * UI has textField for input, TextArea in ScrollPane as Chat world
     * send button , status button and status label 
     */
    private void createUI() {
        //initialize UI components
        txtAreaChatWorld = new JTextArea();
        txtMessage = new JTextField();
        btnSend = new JButton("Send");
        
        btnList = new JButton("Server Stats");
        lblStatus = new JLabel("status");


        //set component default
        txtAreaChatWorld.setEditable(false);
        txtAreaChatWorld.setColumns(20);
        txtAreaChatWorld.setFont(new java.awt.Font("Tahoma", 1, 11));
        txtAreaChatWorld.setRows(5);
        JScrollPane scrollpane = new JScrollPane(txtAreaChatWorld);

        // jScrollPane1.setViewportView(txtAreaChatWorld);
        txtMessage.setFont(new java.awt.Font("Tahoma", 1, 11));
        txtMessage.setHorizontalAlignment(JTextField.LEFT);

        //disable buttons
        toggleUI(false);

        //add listners to send button client and pressing enter on textbox 
        btnSend.addActionListener(new actionSendMessage());
        txtMessage.addActionListener(new actionSendMessage());
        btnList.addActionListener(new actionSendUserListRequest());


        JPanel chatPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        chatPanel.setBorder(BorderFactory.createTitledBorder("World Chat"));
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.ipady = 150;      //make this component tall
        c1.ipadx = 300;		//make it wide
        c1.weightx = 1.0;
        c1.gridwidth = 5;
        c1.gridx = 0;
        c1.gridy = 0;
        chatPanel.add(scrollpane, c1);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.weightx = 0.5;
        c2.ipadx = 180;  //make it wide
        c2.gridwidth = 3; //take 3 cells under width
        c2.gridx = 0;
        c2.gridy = 1;
        chatPanel.add(txtMessage, c2);

        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.gridwidth = 1; //take 1 cell under width
        c3.weightx = 0.5;
        c3.gridy = 1;
        chatPanel.add(btnSend, c3);
        chatPanel.add(btnList, c3);

        GridBagConstraints c4 = new GridBagConstraints();
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.gridwidth = 5; //take 1 cell under width
        c4.weightx = 0.5;
        c4.gridy = 2;
        chatPanel.add(lblStatus, c4);



        add(chatPanel, BorderLayout.NORTH);
        pack();
    }

    /**
     * utility fuction this is to send message to just your self
     */
    public void printInWindow(String message) {
        txtAreaChatWorld.append("\n" + message);
        txtAreaChatWorld.setCaretPosition(txtAreaChatWorld.getText().length());
    }

    /**
     * utility fuction this is to send message to server
     * @throws IOException bubbles IOException to Caller
     */
    public void sendMessage(String message) throws IOException {
        //if i have core then broadcast and update stats
		if (core != null) {
            core.broadcast(message);
            updateStatus("Sent");
		} else {
			//dont have core it must be a test
            printInWindow("test : "+message);
        }
    }

    /**
     * utility fuction this is to update status label
     */
    private void updateStatus(String type) {
        DateFormat df = new SimpleDateFormat("MMM dd HH:mm:ss");
        lblStatus.setText(type + ": " + df.format(new Date()));
    }

    /*
     * ActionListener for btnSend client and textMessage (enter press)
     * it checks if txtmessage is not empty then broadcast message
     */
    private class actionSendMessage implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                if (!txtMessage.getText().equals("")) {
                    sendMessage(txtMessage.getText());
                    txtMessage.setText("");
                }
            } catch (Exception e) {
                printInWindow("could not broadcast due an exception");
            }
        }
    }
	
    /*
     * ActionListener for statusBtn client 
     * ask for userlist and server status
     */
    private class actionSendUserListRequest implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                sendMessage(core.getUserId() + ": Give Me User List");
            } catch (Exception e) {
                printInWindow("could not broadcast due an exception");
            }
        }
    }

    /**
     * utility fuction to close the window with message
     */
    public void exitWindowGracefully() {
		toggleUI(false);
        printInWindow("closing in 3 seconds ");
        waitUtili(3);
        System.exit(1);
    }

    /**
     * utility fuction implicit exception swollowing on wait
	 * other wise would have to try catch on wait  
     */
    public void waitUtili(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (Exception ex) {
		}
        
    }

    // just to test the window and make adjustments 
    public static void main(String[] args) {
        final ChatWindow chatUI = new ChatWindow();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    chatUI.setVisible(true);
                    chatUI.toggleUI(true);
                } catch (Exception ex) {
                }
            }
        });


        //test in  window ca receive messages
        for (int i = 0; i < 10; ++i) {
            chatUI.printInWindow("" + (i + 1) + " Ping");
            chatUI.waitUtili(1);
        }
        chatUI.exitWindowGracefully();
    }
}
