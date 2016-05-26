import java.util.ArrayList;
import java.util.List;

/*
Works for install5 with no linkUpdate
Works for install5 with linkUpdate
Works for install4 with no linkUpdate
Works for install4 with linkUpdate
Works for install3 with linkUpdate
All test cases working
TODO implement a poision reverse boolean so tha tthe count to infinity porblem can occur
*/
public class RouterNode {
    private int myID;
    private GuiTextArea myGUI;
    private RouterSimulator sim;
    private int[] costs = new int[RouterSimulator.NUM_NODES];
    private int[] route = new int[RouterSimulator.NUM_NODES];
    private int[][] neighbourTable = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
    private List<Integer> neighbourList = new ArrayList<Integer>();
    private int[] neighbourCosts = new int[RouterSimulator.NUM_NODES];

    //--------------------------------------------------
    public RouterNode(int ID, RouterSimulator sim, int[] costs) {

        myID = ID;
        this.sim = sim;
        myGUI =new GuiTextArea("  Output window for Router "+ ID + "  ");
        System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);

        printDistanceTable(); //Initial distancetable for the route

        for(int i = 0; i < RouterSimulator.NUM_NODES; i++) //sets default route to myID
            route[i] = ID;

        // Loops through the number of routers, if the distance to a router is less than INFINTY and the ID
        // is not the same as this router, then it is neighbour and we send a update with our cost list to it
        for (int i = 0; i < costs.length; i++){
           if (costs[i] < RouterSimulator.INFINITY && i != myID){
               RouterPacket routerPacket = new RouterPacket(ID, i, costs);
               System.out.println("mincost to router "+i+ " = "+routerPacket.mincost[i]+" from router "+ myID);
               neighbourList.add(i);
               neighbourCosts[i] = costs[i]; //sets initial cost to neighbour
               sendUpdate(routerPacket);
           }
        }
    }

    //--------------------------------------------------
    public void recvUpdate(RouterPacket pkt) {
        System.out.println("\nRecvUpdate for router "+ myID +" from router "+ pkt.sourceid);

        neighbourTable[pkt.sourceid] = pkt.mincost; //updates the neigbourTable
        boolean updated = false; //net yet updated any costs

        for (int routerID = 0; routerID < pkt.mincost.length; routerID++){ //loops through all the costs

            //sourceRouters cost to a router + our cost to sourcerouter
            int newCost = pkt.mincost[routerID] + costs[pkt.sourceid];

            //Standard case
            //if newCost < than our old cost to that router
            if (newCost < costs[routerID]){
                route[routerID] = pkt.sourceid;
                costs[routerID] = newCost;
                System.out.println("Cost from router "+ myID +" to router "+ routerID +" is updated to "+ costs[routerID]);
                updated = true;

             //if we are using sourceRouter for any current routs
            }else if(pkt.sourceid == route[routerID]){

                //The used router must be a neigbour, we only get updates from neighbours
                for (int neighbour: neighbourList){

                    //If the cost to a neighbour and then to a route < newCost
                    if((neighbourCosts[neighbour] + neighbourTable[neighbour][routerID]) <= newCost){

                        //If the cost is not the same, we update
                        if((neighbourCosts[neighbour] + neighbourTable[neighbour][routerID]) != newCost){
                            updated = true;
                        }

                        newCost = neighbourCosts[neighbour] + neighbourTable[neighbour][routerID];

                        System.out.println("Old path to "+ routerID +" through " + route[routerID] +
                                " updated to path through " + neighbour +
                                " with cost: " + newCost);

                        route[routerID] = neighbour == routerID ? myID : neighbour;
                        costs[routerID] = newCost;
                    }
                }
            }
        }

        if (updated){
            for (int neighbour: neighbourList){

                RouterPacket routerPacket = new RouterPacket(myID, neighbour, costs);
                sendUpdate(routerPacket);
            }
        }
    }


    //--------------------------------------------------
    private void sendUpdate(RouterPacket pkt) {
        System.out.println("Sending update from router "+ myID+ " to router "+ pkt.destid +"\n");
        sim.toLayer2(pkt);
    }


    //--------------------------------------------------
    public void printDistanceTable() {

        myGUI.println("\n\n\n Current table for router " + myID +"  at time " + sim.getClocktime());

        myGUI.println(" Distancetable:");
        myGUI.print(" Router: |    ");
        for (int i=0; i < costs.length; i++){
            myGUI.print(i+ "    ");
        }

        myGUI.print("\n -----------");
        for (int cost : costs) {
            myGUI.print("-----");
        }

        myGUI.print("\n cost       |   ");
        for (int cost : costs) {
            myGUI.print(cost + "    ");
        }
    }

    //--------------------------------------------------
    public void updateLinkCost(int dest, int newcost) {
        System.out.println("updateLinkCost for router "+ myID +" to router "+ dest + " with cost "+ newcost);

        neighbourCosts[dest] = newcost;
        if (route[dest] == myID){
            costs[dest] = newcost;
        }

        for (int neighbour: neighbourList){

            RouterPacket routerPacket = new RouterPacket(myID, neighbour, costs);
            sendUpdate(routerPacket);
        }
        printDistanceTable(); //Prints the updated distance table
    }

}
