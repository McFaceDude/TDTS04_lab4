import java.util.ArrayList;
import java.util.List;

/*
Works for install5 with no linkUpdate
Works for install5 with linkUpdate
Works for install4 with no linkUpdate
Works for install4 with linkUpdate
Works for install3 with linkUpdate
All test cases working
TODO implement a poision reverse boolean so tha the count to infinity porblem can occur
*/
public class RouterNode {
    private int myID;
    private GuiTextArea myGUI;
    private RouterSimulator sim;
    private int[] costs = new int[RouterSimulator.NUM_NODES];
    private List<Integer> neighbours = new ArrayList<Integer>();
    private int[] routes = new int[RouterSimulator.NUM_NODES];
    private boolean poisonReverse = true;
    private int[] neighbourCosts = new int[RouterSimulator.NUM_NODES];
    private int[][] neighbourTable = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];

    //--------------------------------------------------
    public RouterNode(int ID, RouterSimulator sim, int[] costs) {

        myID = ID;
        this.sim = sim;
        myGUI =new GuiTextArea("  Output window for Router "+ ID + "  ");
        System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);

        printDistanceTable(); //Initial distancetable for the routes

        // Loops through the number of routers, if the distance to a router is less than INFINTY and the ID
        // is not the same as this router, then it is neighbour and we send a update with our cost list to it


        for (int i = 0; i < routes.length; i++){

            routes[i] = myID;
        }

        for (int i = 0; i < costs.length; i++){
           if (costs[i] < RouterSimulator.INFINITY && i != myID){
               RouterPacket routerPacket = new RouterPacket(ID, i, costs);
               System.out.println("mincost to router "+i+ " = "+routerPacket.mincost[i]+" from router "+ myID);
               neighbours.add(i);
               neighbourCosts[i] = costs[i];

               sendUpdate(routerPacket);
           }
        }
    }

    //--------------------------------------------------
    public void recvUpdate(RouterPacket pkt) {
        System.out.println("\nRecrived Update for router "+ myID +" from router "+ pkt.sourceid);
        boolean updated = false;
        neighbourTable[pkt.sourceid] = pkt.mincost;

        for (int i = 0; i < pkt.mincost.length; i++) { //loops through all the costs

            int newCost = pkt.mincost[i] + costs[pkt.sourceid];

            if (pkt.sourceid == routes[i]){

                if (costs[i] != newCost)
                    updated = true;

                costs[i] = newCost;
                System.out.println("Case 1, Cost from router "+ myID +" to router "+ i +" is updated to "+ costs[i]);
            }

            else if (newCost < costs[i]){

                costs[i] = newCost;
                routes[i] = pkt.sourceid;
                updated = true;
                System.out.println("Case 2, Cost from router "+ myID +" to router "+ i +" is updated to "+ costs[i]);
            }

            if (neighbours.contains(i)){
                if (neighbourCosts[i] < costs[i]){
                    costs[i] = neighbourCosts[i];
                    routes[i] = i;
                    updated = true;
                    System.out.println("Case 3, Cost from router "+ myID +" to router "+ i +" is updated to "+ costs[i]);
                }
            }
        }

        if (updated)
            updateNeighbours();

    }



    private void updateNeighbours(){

        int[] tempCost = new int[costs.length];
        System.arraycopy(costs, 0, tempCost, 0, costs.length);
        RouterPacket routerPacket;

        if (poisonReverse){
            for (int i = 0; i < tempCost.length; i++) {
                if (!neighbours.contains(i)) {
                    tempCost[i] = RouterSimulator.INFINITY;
                }
                else{
                    tempCost[i] = costs[i];
                }
            }
            for (int i = 0; i< neighbours.size(); i++){
                routerPacket = new RouterPacket(myID, neighbours.get(i), tempCost);
                System.out.println("POISION!!!!!!!!!!!");
                sendUpdate(routerPacket);
            }
        }

        else {
            for (int i = 0; i < neighbours.size(); i++) {
                routerPacket = new RouterPacket(myID, neighbours.get(i), costs);
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

        for (int neighbour: neighbours) {


            if ((neighbourCosts[neighbour] + neighbourTable[neighbour][dest]) <= newcost) {
                costs[dest] = neighbourCosts[neighbour] + neighbourTable[neighbour][dest];
                routes[dest] = neighbour;
            }
            else{
                costs[dest] = newcost;

            }
        }

        updateNeighbours();


    }
}
