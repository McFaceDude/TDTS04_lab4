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
    private List<Integer> neighbours = new ArrayList<Integer>();

    //--------------------------------------------------
    public RouterNode(int ID, RouterSimulator sim, int[] costs) {

        myID = ID;
        this.sim = sim;
        myGUI =new GuiTextArea("  Output window for Router "+ ID + "  ");
        System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);

        printDistanceTable(); //Initial distancetable for the route

        // Loops through the number of routers, if the distance to a router is less than INFINTY and the ID
        // is not the same as this router, then it is neighbour and we send a update with our cost list to it
        for (int i = 0; i < costs.length; i++){
           if (costs[i] < RouterSimulator.INFINITY && i != myID){
               RouterPacket routerPacket = new RouterPacket(ID, i, costs);
               System.out.println("mincost to router "+i+ " = "+routerPacket.mincost[i]+" from router "+ myID);
               neighbours.add(i);
               sendUpdate(routerPacket);
           }
        }
    }

    //--------------------------------------------------
    public void recvUpdate(RouterPacket pkt) {
        System.out.println("\nRecrived Update for router "+ myID +" from router "+ pkt.sourceid);

        for (int routerID = 0; routerID < pkt.mincost.length; routerID++) { //loops through all the costs

            if (pkt.mincost[routerID] != 0 && pkt.mincost[routerID] + costs[pkt.sourceid] < costs[routerID]){

                costs[routerID] = pkt.mincost[routerID]+ costs[pkt.sourceid];
                System.out.println("Cost from router "+ myID +" to router "+ routerID +" is updated to "+ costs[routerID]);
                updateNeighbours();

            }
        }
    }

    private void updateNeighbours(){
        int[] tempCost = new int[costs.length];
        System.arraycopy(costs, 0, tempCost, 0, costs.length);
        RouterPacket routerPacket = null;

        for (int routerID = 0; routerID < costs.length; routerID++){

            if (!neighbours.contains(routerID)){
                tempCost[routerID] = RouterSimulator.INFINITY;
                routerPacket = new RouterPacket(myID, routerID, tempCost);
            }
            else {
                routerPacket = new RouterPacket(myID, routerID, costs);
            }
        }

        for (int neighbour: neighbours){
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
        /*
        neighbourCosts[dest] = newcost;
        if (route[dest] == myID){
            costs[dest] = newcost;
        }

        for (int neighbour: neighbourList){

            RouterPacket routerPacket = new RouterPacket(myID, neighbour, costs);
            sendUpdate(routerPacket);
        }
        printDistanceTable(); //Prints the updated distance table
        */
    }

}
