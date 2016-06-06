import java.util.ArrayList;
import java.util.List;

/*

*/
public class RouterNode {
    private int myID;
    private GuiTextArea myGUI;
    private RouterSimulator sim;
    private int[] costs = new int[RouterSimulator.NUM_NODES]; //The least cost to all the routers in the network
    private List<Integer> neighbours = new ArrayList<Integer>(); //List of neighbours
    private int[] nextHop = new int[RouterSimulator.NUM_NODES]; //Next hop to get to a router
    private int[] neighbourCosts = new int[RouterSimulator.NUM_NODES]; //The cost to the neighbours
    //The costs table for all the neighbours
    private int[][] neighbourTable = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];


    boolean poisonReverse = false; //For the poison reverse solution

    //--------------------------------------------------
    public RouterNode(int ID, RouterSimulator sim, int[] costs) {

        myID = ID;
        this.sim = sim;
        myGUI =new GuiTextArea("  Output window for Router "+ ID + "  ");
        System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);

        printDistanceTable(); //Initial distancetable for the router

        //Initiates the nextHop to -1 sense we don't have any next hop otions yet
        for (int i = 0; i < nextHop.length; i++){
            nextHop[i] = -1;
        }

        // Loops through the number of routers, if the distance to a router is less than INFINTY and the ID
        // is not the same as this router, then it is neighbour and we set the neigbourCost and add it to neighbours
        // and nextHop
        for (int i = 0; i < costs.length; i++){
           if (costs[i] < RouterSimulator.INFINITY && i != myID){
               RouterPacket routerPacket = new RouterPacket(ID, i, costs);
               System.out.println("mincost to router "+i+ " = "+routerPacket.mincost[i]+" from router "+ myID);
               neighbours.add(i);
               neighbourCosts[i] = costs[i];
               nextHop[i] = i;
           }
            if(i == myID){ //We can go to ourself my hopping to ourself
                nextHop[i] = myID;
            }
        }

        //Initiate the neigbourTable
        for (int neighbour = 0; neighbour < neighbours.size(); neighbour++){
            for (int cost = 0; cost < neighbourTable.length; cost++){
                neighbourTable[neighbours.get(neighbour)][cost] = RouterSimulator.INFINITY;
            }
        }

        updateNeighbours(); //Update the neighbours with the current cost table for the router
    }

    //--------------------------------------------------
    public void recvUpdate(RouterPacket pkt) {
        System.out.println("\nRecrived Update for router "+ myID +" from router "+ pkt.sourceid);
        boolean updated = false;

        //Update the neighbourTable with the costs table from the sourceRouter
        neighbourTable[pkt.sourceid] = pkt.mincost;

        /*
        for (int neighbour = 0; neighbour < neighbourTable.length; neighbour++){
            for (int cost = 0; cost < neighbourTable.length; cost++){
                System.out.print(" Nbr: "+ neighbour + " cost: "+ neighbourTable[neighbour][cost]);
            }
            System.out.println("");
        }*/


        for (int i = 0; i < pkt.mincost.length; i++) { //Loops through all the routers

            //The newCost is our cost to the sourceRouter + the sourceRouters cost to the destinationRouter
            int newCost = pkt.mincost[i] + costs[pkt.sourceid];

            //If we use the sourceRouter to get to a router, we override our old cost to that router
            //with the newCost from the sourceRouter. We only update if the newCost is different.
            if (pkt.sourceid == nextHop[i]){
                if (costs[i] != newCost)
                    updated = true;

                costs[i] = newCost;
                System.out.println("Case 1, Cost from router "+ myID +" to router "+ i +" is updated to "+ costs[i]);
            }

            //If the newCost is less that our current cost to a router, we change our current
            //cost to the newcost and sets the nexHop accordingly.
            else if (newCost < costs[i]){

                costs[i] = newCost;
                nextHop[i] = pkt.sourceid;
                updated = true;
                System.out.println("Case 2, Cost from router "+ myID +" to router "+ i +" is updated to "+ costs[i]);
            }

            //If we have the router as a neighbour and we can go directly to it for less
            //than our current cost, we do that.
            if (neighbours.contains(i)){
                if (neighbourCosts[i] < costs[i]){
                    costs[i] = neighbourCosts[i];
                    nextHop[i] = i;
                    updated = true;
                    System.out.println("Case 3, Cost from router "+ myID +" to router "+ i +" is updated to "+ costs[i]);
                }
            }
        }

        if (updated)
            updateNeighbours();
    }

    private void updateNeighbours(){

        int[] tempCost = new int[costs.length]; //Copy the costs table fo the poison reverse solution
        System.arraycopy(costs, 0, tempCost, 0, costs.length);

        for (int neighbourID = 0; neighbourID < neighbours.size(); neighbourID++) {
            RouterPacket routerPacket;

            //If we are using a neighbour to get to a router, we tell that neighbour that our distance to the router
            //is infinity so that our neighbour does not route through us.
            if (poisonReverse) {
                for (int hop = 0; hop < nextHop.length; hop++) {
                    if (nextHop[hop] == neighbours.get(neighbourID)) {
                        tempCost[hop] = RouterSimulator.INFINITY;
                    }
                    else {
                        tempCost[hop] = costs[hop];
                    }
                }
                //Sends the costs table with the infinit values instead of the actuall costs table for the router
                routerPacket = new RouterPacket(myID, neighbours.get(neighbourID), tempCost);
            }
            // If no poison revers, we send the cotst table as it is.
            else{
                routerPacket = new RouterPacket(myID, neighbours.get(neighbourID), costs);
            }
            sendUpdate(routerPacket);
        }
    }


    //--------------------------------------------------
    private void sendUpdate(RouterPacket pkt) {
        System.out.println("Sending update from router "+ myID+ " to router "+ pkt.destid +"\n");
        sim.toLayer2(pkt);
    }

    //--------------------------------------------------
    public void printDistanceTable() {

        myGUI.println("\n\n Current state for router " + myID + "  at time " + sim.getClocktime());

        //Prints the neighbourTable for the router
        myGUI.println(" Neighbours cost tables");
        myGUI.print(" Router    |");
        for (int i = 0; i < neighbourTable.length; i++) {
            myGUI.print(F.format(i, 9));
        }

        myGUI.print("\n --------------");
        for (int neighbour : neighbours) {
            myGUI.print("--------");
        }

        for (int neighbour = 0; neighbour < neighbours.size(); neighbour++){
            myGUI.print("\n nbr "+ neighbours.get(neighbour) +"       |");
            for (int cost = 0; cost < neighbourTable.length; cost++) {
                myGUI.print("|"+F.format(neighbourTable[neighbours.get(neighbour)][cost], 9));
            }
        }


        //Prints the cost table and nexHop table for the router
        myGUI.println("\n\n Costs table:");
        myGUI.print(" Router    |");
        for (int i=0; i < costs.length; i++){
            myGUI.print(F.format(i,9));
        }

        myGUI.print("\n --------------");
        for (int cost : costs) {
            myGUI.print("--------");
        }

        myGUI.print("\n cost         |");
        for (int cost : costs) {
            myGUI.print(F.format(cost,9));
        }
        myGUI.print("\n next hop |");
        for (int hop : nextHop) {
            if (hop == -1) {
                myGUI.print(F.format("-",9));
            }
            else{
                myGUI.print(F.format(hop,9));
            }

        }
        myGUI.println();
    }

    //--------------------------------------------------
    public void updateLinkCost(int dest, int newCost) {
        System.out.println("updateLinkCost for router "+ myID +" to router "+ dest + " with cost "+ newCost);

        neighbourCosts[dest] = newCost; //Change the neigbourcost to the newCost

        for (int neighbour: neighbours) {

            //If a neighbour has a cost to the destination router which is less than the newCost, we use the neighbours
            //cost and update our nextHop
            if ((neighbourCosts[neighbour] + neighbourTable[neighbour][dest]) <= newCost) {
                costs[dest] = neighbourCosts[neighbour] + neighbourTable[neighbour][dest];
                nextHop[dest] = neighbour;
            }
            else{
                costs[dest] = newCost;
            }
        }
        updateNeighbours();
    }
}
