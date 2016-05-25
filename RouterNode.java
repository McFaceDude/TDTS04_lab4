import java.util.ArrayList;
import java.util.List;

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

        printDistanceTable(); //Initial distancetable for the router

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
        System.out.println("\nRecvUpdate for router "+ myID +" from router "+ pkt.sourceid);

        // Loops through the received mincost list and if the cost is not 0(the cost to itself) and the cost
        // from us to the source router + the cost from sourcerouter to another router is less
        // than our cost to that other router, we update our linkCost to the other router
        for (int i = 0; i < pkt.mincost.length; i++){
            if (pkt.mincost[i] != 0 && pkt.mincost[i] + costs[pkt.sourceid] < costs[i]){
                costs[i] = pkt.mincost[i]+ costs[pkt.sourceid];
                System.out.println("Cost from router "+ myID +" to router "+ i +" is updated to "+ costs[i]);

                for (int neighbour: neighbours){

                    RouterPacket routerPacket = new RouterPacket(myID, neighbour, costs);
                    System.out.println("mincost to router "+ neighbour +" = "+routerPacket.mincost[neighbour]+
                            " from router "+ myID);
                    sendUpdate(routerPacket);

                }
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
        costs[dest] = newcost;

        for (int neighbour: neighbours){

            RouterPacket routerPacket = new RouterPacket(myID, neighbour, costs);
            sendUpdate(routerPacket);
        }
        printDistanceTable(); //Prints the updated distance table
    }

}
