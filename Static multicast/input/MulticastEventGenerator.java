//Created by Nishchay on 12/02/2019

package input;

import java.util.*;

import core.Settings;
import core.SettingsError;

public class MulticastEventGenerator extends MessageEventGenerator{
    protected int[] destinationRange;

    public static final String MULTICAST_DEST_RANGE = "destinationRange";

    public static final String MULTICAST="multicast";
    public static final String MULTICAST_NR_OF_DESTINATION="nrofDestination";

    public static final String MULTICAST_DEST="multicastDest";
    //public static final String MULTICAST_DEST_NUM="multicastDestinationsNum";

    private ArrayList<Integer> toIds;
    private int destinations;

    private Settings s;

    private boolean multicast;
    private int[] multicastIDs;

    public MulticastEventGenerator(Settings s){
        super(s);
        this.destinationRange=s.getCsvInts(MULTICAST_DEST_RANGE,2);
        this.multicast=s.getBoolean(MULTICAST);
        this.destinations=s.getInt(MULTICAST_NR_OF_DESTINATION);
        this.toIds=new ArrayList<Integer>();
        this.multicastIDs=s.getCsvInts(MULTICAST_DEST);
//        if (toHostRange==null){
//            throw new SettingsError("Destination host ("+TO_HOST_RANGE_S+") must be defined");
//        }
        for(int i=destinationRange[0];i<destinationRange[1];i++){
            toIds.add(i);
        }
//        if((destinationRange[1]-destinationRange[0])!=destinations){
//            throw new SettingsError("Destination address range ("+MULTICAST_DEST_RANGE+") and number of destination ("+MULTICAST_NR_OF_DESTINATION+ ") must be identical");
//        }
       // destination_list=new int[]{destinationRange[0],destinationRange[1]};
        Collections.shuffle(toIds,rng);
    }

    /** Method picking up the destinations of the multicast in the Settings file and checking if the id lies in the destinationRange and then returning to the user.**/
    private Integer[] multicast_destination_list(int fromId){
      //  int[] multicastIDs=s.getCsvInts(MULTICAST_DEST,2);
        // int numOfMulticastDestinations=multicastIDs.length;
        List<Integer> multicastTempID= convertToList(multicastIDs);
        // System.out.println(multicastIDs[6]+" "+multicastTempID.get(6));
        for(int i=0;i<multicastTempID.size();i++){
            int tempId=multicastTempID.get(i);  
            // System.out.println(i+"  "+fromId + "  "+tempId);
            if (!(tempId > destinationRange[0] && tempId<destinationRange[1])) {
                // System.out.println("Inside");
                multicastTempID.remove(multicastTempID.get(i));
            }
            // System.out.println("Outside");
        }
        // System.out.println("List: "+multicastTempID.toString());
        // if(multicastTempID.contains(fromId)){
        //     multicastTempID.remove(fromId);
        // }
        multicastTempID.removeIf(id -> (id==fromId));
        // System.out.println("Final List: "+multicastTempID.toString());
        return multicastTempID.toArray(new Integer[multicastTempID.size()]);

    }
    /** Read from the settings file the destinations entered by the user**/
//    public Integer drawDestAddress(){
//        return s.getCsvInts(MULTICAST_DEST_NUM);
//    }

    public ExternalEvent nextEvent(){
        int responseSize = 0; /* zero stands for one way messages */
        int msgSize;
        int interval;
        int from;
        int to;
        Integer[] to_multicast;

        /* Get two *different* nodes randomly from the host ranges */
        from = drawHostAddress(this.hostRange);
        to = drawToAddress(hostRange, from);

        msgSize = drawMessageSize();
        interval = drawNextEventTimeDiff();

        to_multicast=multicast_destination_list(from);

        /* Create event and advance to next event */
        MessageCreateEvent mce = new MessageCreateEvent(from, to, this.getID(),
                msgSize, responseSize, this.nextEventsTime,"test",this.multicast,to_multicast);
        this.nextEventsTime += interval;

        if (this.msgTime != null && this.nextEventsTime > this.msgTime[1]) {
            /* next event would be later than the end time */
            this.nextEventsTime = Double.MAX_VALUE;
        }

        return mce;
    }

    private List<Integer> convertToList(int[] arr){
        List<Integer> intList = new ArrayList<Integer>();
        for (int i : arr)
        {
            intList.add(i);
        }
        return intList;
    }
}