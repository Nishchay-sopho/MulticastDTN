/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import core.DTNHost;
import core.Message;
import core.World;
// import report.MulticastCounter;

import java.util.ArrayList;

/**
 * External event for creating a message.
 */
public class MessageCreateEvent extends MessageEvent {
	private int size;
	private int responseSize;
	private String msgContent;

	/** Added for multicast and broadcast implementation on 12-02-2019 **/
	private boolean multicast;
	private Integer[] destination_list;
	
	/**
	 * Creates a message creation event with a optional response request
	 * @param from The creator of the message
	 * @param to Where the message is destined to
	 * @param id ID of the message
	 * @param size Size of the message
	 * @param responseSize Size of the requested response message or 0 if
	 * no response is requested
	 * @param time Time, when the message is created
	 */
	public MessageCreateEvent(int from, int to, String id, int size,
			int responseSize, double time,String msgContent,boolean multicast,Integer[] destination_list) {
		super(from,to, id, time);
		//System.out.println("Initiating constructor for messageCreateEvent");
		this.size = size;
		this.responseSize = responseSize;
		this.msgContent=msgContent;
		this.multicast=multicast;
		this.destination_list=destination_list;
		//System.out.println("Finished constructor for messageCreateEvent");
	}

	public MessageCreateEvent(int from, int to, String id, int size,
							  int responseSize, double time,String msgContent) {
		super(from,to, id, time);
		this.size = size;
		this.responseSize = responseSize;
		this.msgContent=msgContent;
	}

	public MessageCreateEvent(int from, int to, String id, int size,
							  int responseSize, double time) {
		super(from,to, id, time);
		this.size = size;
		this.responseSize = responseSize;
	}
	
	/**
	 * Creates the message this event represents. 
	 */
	@Override
	public void processEvent(World world) {
		DTNHost to = world.getNodeByAddress(this.toAddr);
		DTNHost from = world.getNodeByAddress(this.fromAddr);

		if(multicast){
			ArrayList<DTNHost> multicastDestinations=new ArrayList<DTNHost>();
			for(int i=0;i<destination_list.length;i++){
				multicastDestinations.add(world.getNodeByAddress(this.destination_list[i]));
			}
			Message m= new Message(from,to,this.id,this.size,multicastDestinations);
			// System.out.println("Message with destinations: "+multicastDestinations.toString()+" created.");
			m.setResponseSize(this.responseSize);
			m.setContent(this.msgContent);
			from.createNewMessage(m);
			// MulticastCounter.msgGenerated();
		}
		else {
			Message m = new Message(from, to, this.id, this.size,null);
			m.setResponseSize(this.responseSize);
			m.setContent(this.msgContent);
			from.createNewMessage(m);
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + fromAddr + "->" + toAddr + "] " +
		"size:" + size + " CREATE";
	}
}
