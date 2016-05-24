import java.util.Arrays;

public class RouterNode {
    private int myID;
    private GuiTextArea myGUI;
    private RouterSimulator sim;
    private int[] costs = new int[RouterSimulator.NUM_NODES];

    //--------------------------------------------------
    public RouterNode(int ID, RouterSimulator sim, int[] costs) {

        myID = ID;
        this.sim = sim;
        myGUI =new GuiTextArea("  Output window for Router "+ ID + "  ");

        System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
        int[] mincost = new int[RouterSimulator.NUM_NODES];    /* min cost to node 0 ... 3 */
        RouterPacket routerPacket = new RouterPacket(ID, ID+1, mincost);
        printDistanceTable();
        for (int i=0; i < costs.length; i++){
            for (int j = 0; j < routerPacket.mincost.length; j++){
                System.out.println(routerPacket.mincost[j]);
            }

        }

        sendUpdate(routerPacket);

    }

    //--------------------------------------------------
    public void recvUpdate(RouterPacket pkt) {
        System.out.println("RecvUpdate");
    }


    //--------------------------------------------------
    private void sendUpdate(RouterPacket pkt) {
        System.out.println("sendUpdate");
        sim.toLayer2(pkt);

    }


    //--------------------------------------------------
    public void printDistanceTable() {

        myGUI.println();
        myGUI.println(" Current table for router " + myID +"  at time " + sim.getClocktime());
        myGUI.println();
        myGUI.println(" Distancetable:");

        myGUI.print(" Router: |    ");
        for (int i=0; i < costs.length; i++){
            myGUI.print(i+ "    ");
        }

        myGUI.println();
        myGUI.print(" -----------");
        for (int i=0; i < costs.length; i++){
            myGUI.print("-----");
        }

        myGUI.println();
        myGUI.print(" cost       |   ");
        for (int i=0; i < costs.length; i++){
            myGUI.print(costs[i]+ "    ");
        }
        myGUI.println();
    }

    //--------------------------------------------------
    public void updateLinkCost(int dest, int newcost) {
    }

}
