
import	java.util.LinkedList;


/**
 * BroadcastThread Class is responsible for holding LinkedList of messages
 * could have used a concurrent collection but wanted to have more control 
 * clasic example of producer consumer solution
 * @author ashabbir
 */
class BroadcastThread extends Thread {

    private ChatServer server;
	//not using array List.  or ConcurrentLinkedQueue
	//(pop on linked list is easier - gets the last )
	private LinkedList<String> messeges;
   

    /**
     * initizalises the list and set the server reference 
	 * Seperate method is used for Syncing, didnt wana sync the Run method. By syncing this method ensure intrinsic locks
     */
    synchronized public void initialize(ChatServer server) {
        this.server = server;
        messeges = new LinkedList<>();
    }
	
	//run method for the thread to call
    @Override
    synchronized public void run() {
        try {
            while (true) {
			    server.broadcast(take());
            }
        } catch (Exception e) {
        }
    }

    /**
     * puts message at bottom and then notify all who are waiting
     * @param message String message to be put in queue
     * @throws InterruptedException bubbles InterruptedException to Caller
     */
    synchronized public void put(String message)
            throws InterruptedException {
				messeges.add(message);
				notifyAll();
    }

    /**
     * takes the message from the head. if there is no message it waits 
     * @return message String message which is poped from queue
     * @throws InterruptedException bubbles InterruptedException to Caller
     */
    synchronized public String take()
            throws InterruptedException {
        while (messeges.isEmpty()) {
            wait();
        }
        return messeges.pop();
	
    }
}
