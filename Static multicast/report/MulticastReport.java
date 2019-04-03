/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import util.Tuple;

import core.DTNHost;
import core.Message;

import core.MessageListener;

/**
 * Report for generating different kind of total statistics about message
 * relaying performance. Messages that were created during the warm up period
 * are ignored.
 * <P><strong>Note:</strong> if some statistics could not be created (e.g.
 * overhead ratio if no messages were delivered) "NaN" is reported for
 * double values and zero for integer median(s).
 */
public class MulticastReport extends Report implements MessageListener {
	private Map<String, Double> creationTimes;
	private List<Double> latencies;
	private HashMap<Integer,Integer> messagesDelivered;
	// private List<Integer> hopCounts;
	private List<Double> msgBufferTime;
	private List<Tuple<Integer,Integer>> hopCounts;
	private HashMap<Integer,Tuple<Integer,Integer>> hopCountHashMap;
	// private List<Double> time; // one way trip times
	
	private int nrofDropped;
	private int nrofRemoved;
	private int nrofStarted;
	private int nrofAborted;
	private int nrofRelayed;
	private int nrofCreated;
	private int nrofResponseReqCreated;
	private int nrofResponseDelivered;
	private int nrofDelivered;
	
	/**
	 * Constructor.
	 */
	public MulticastReport() {
		init();
	}

	@Override
	protected void init() {
		super.init();
		this.creationTimes = new HashMap<String, Double>();
		this.latencies = new ArrayList<Double>();
		this.msgBufferTime = new ArrayList<Double>();
		this.messagesDelivered=new HashMap<Integer,Integer>();
		// this.hopCounts = new ArrayList<Integer>();
		this.hopCounts=new ArrayList<Tuple<Integer,Integer>>();
		this.hopCountHashMap=new HashMap<Integer,Tuple<Integer,Integer>>();
		// this.time = new ArrayList<Double>();
		
		this.nrofDropped = 0;
		this.nrofRemoved = 0;
		// this.nrofStarted = 0;
		// this.nrofAborted = 0;
		// this.nrofRelayed = 0;
		this.nrofCreated = 0;
		// this.nrofResponseReqCreated = 0;
		// this.nrofResponseDelivered = 0;
		this.nrofDelivered = 0;
	}

	//TODO Check messageDeleted is from where
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		if (dropped) {
			this.nrofDropped++;
		}
		else {
			this.nrofRemoved++;
		}
		
		this.msgBufferTime.add(getSimTime() - m.getReceiveTime());
	}

	
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
		// if (isWarmupID(m.getId())) {
		// 	return;
		// }
		
		// this.nrofAborted++;
	}

	
	public void messageTransferred(Message m, DTNHost from, DTNHost to,
			boolean finalTarget) {
		// if (isWarmupID(m.getId())) {
		// 	return;
		// }

		// this.nrofRelayed++;
		// if (finalTarget) {
		this.latencies.add(getSimTime() - this.creationTimes.get(m.getId()));
		// 	this.nrofDelivered++;
			// this.hopCounts.add(m.getHops().size() - 1);
		// System.out.println("Hop count for "+m.getId()+ " is : "+(m.getHops().size()-1));
			
		// 	if (m.isResponse()) {
		// 		this.rtt.add(getSimTime() -	m.getRequest().getCreationTime());
		// 		this.nrofResponseDelivered++;
		// 	}
		// }
		messagesDelivered.put(Integer.parseInt(m.getId().substring(4,m.getId().length())),1);
		updateMsgDelivered();
		// this.time.add(getSimTime()-m.getCreationTime());
	}

	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
			return;
		}
		
		this.creationTimes.put(m.getId(), getSimTime());
		this.nrofCreated++;
		// if (m.getResponseSize() > 0) {
		// 	this.nrofResponseReqCreated++;
		// }
		// updateMsgGenerated();
		// System.out.println("asdf");
	}
	
	
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
		// if (isWarmupID(m.getId())) {
		// 	return;
		// }

		// this.nrofStarted++;
	}
	

	@Override
	public void done() {
		write("Message stats for scenario " + getScenarioName() + 
				"\nsim_time: " + format(getSimTime()));
		double deliveryProb = 0; // delivery probability
		// double responseProb = 0; // request-response success probability
		// double overHead = Double.NaN;	// overhead ratio
		
		if (this.nrofCreated > 0) {
			deliveryProb = (1.0 * this.nrofDelivered) / this.nrofCreated;
		}
		List<Double> hopCount=calculateHopCountsForDeliveredMessages(hopCountHashMap,messagesDelivered);
		// if (this.nrofDelivered > 0) {
		// 	overHead = (1.0 * (this.nrofRelayed - this.nrofDelivered)) /
		// 		this.nrofDelivered;
		// }
		
		// String statsText = "created: " + this.nrofCreated + 
		// 	"\nstarted: " + this.nrofStarted + 
		// 	"\nrelayed: " + this.nrofRelayed +
		// 	"\naborted: " + this.nrofAborted +
		// 	"\ndropped: " + this.nrofDropped +
		// 	"\nremoved: " + this.nrofRemoved +
		// 	"\ndelivered: " + this.nrofDelivered +
		// 	"\ndelivery_prob: " + format(deliveryProb) +
		// 	"\nresponse_prob: " + format(responseProb) + 
		// 	"\noverhead_ratio: " + format(overHead) + 
		// 	"\nlatency_avg: " + getAverage(this.latencies) +
		// 	"\nlatency_med: " + getMedian(this.latencies) + 
		// 	"\nhopcount_avg: " + getIntAverage(this.hopCounts) +
		// 	"\nhopcount_med: " + getIntMedian(this.hopCounts) + 
		// 	"\nbuffertime_avg: " + getAverage(this.msgBufferTime) +
		// 	"\nbuffertime_med: " + getMedian(this.msgBufferTime) +
		// 	"\nrtt_avg: " + getAverage(this.rtt) +
		// 	"\nrtt_med: " + getMedian(this.rtt)
		// 	;
		String statsText="created: "+this.nrofCreated+
						 "\ndelivered : "+ this.nrofDelivered+
					     // "\ndropped: " + this.nrofDropped +
					     // "\nremoved: " + this.nrofRemoved +
					     "\ndelivery_prob: " + format(deliveryProb) +
					     "\nhopcount_avg: " + getAverage(hopCount) +
						 "\nhopcount_med: " + getMedian(hopCount) + 
						 "\nlatency_avg: " + getAverage(this.latencies) +
						 "\nlatency_med: " + getMedian(this.latencies) + 
					     "\nbuffertime_avg: " + getAverage(this.msgBufferTime) +
						 "\nbuffertime_med: " + getMedian(this.msgBufferTime);
		
		write(statsText);
		super.done();
	}
	
	public void updateMsgGenerated(){
		this.nrofCreated++;
	}
	public void updateMsgDelivered(){
		this.nrofDelivered++;
	}


	public void updateHopCount(Message m, DTNHost from, DTNHost to){
		// this.hopCounts.add(m.getHops().size() - 1);
		int id=Integer.parseInt(m.getId().substring(4,m.getId().length()));
		// System.out.println("Hop count for "+id.toString()+ " is : "+m.getHops().toString());
		Tuple<Integer,Integer> tuple;
		if(!hopCountHashMap.containsKey(id)){
			tuple=new Tuple<Integer,Integer>(1,m.getHops().size()-1);
			hopCountHashMap.put(id,tuple);
		}else{
			Tuple oldTuple=hopCountHashMap.get(id);
			int receivers=(int)oldTuple.getKey()+1;
			int totalHopCounts=(int)oldTuple.getValue()+m.getHops().size()-1;
			tuple=new Tuple<Integer,Integer>(receivers,totalHopCounts);
			hopCountHashMap.replace(id,tuple);
		}
		// System.out.println("Hop count for message with id: "+id+" in tuple with number of receivers "+tuple.getKey()+ " and total hop count: "+tuple.getValue() );

	}

	public List<Double> calculateHopCountsForDeliveredMessages(HashMap<Integer,Tuple<Integer,Integer>> hopCountHashMap,HashMap<Integer,Integer> messagesDelivered){
		List<Double> hopCount=new ArrayList<Double>();
		// HashSet msgIds=hopCountHashMap.entrySet();
		for (Map.Entry<Integer,Tuple<Integer,Integer>> entry : hopCountHashMap.entrySet()){
			if(messagesDelivered.containsKey(entry.getKey())){
				Tuple t = entry.getValue();
				// System.out.println("key: "+t.getKey()+ " , value: "+t.getValue());
				hopCount.add(new Double(t.getValue().toString())/(int)t.getKey());
			}
		}
		return hopCount;
	}
}
