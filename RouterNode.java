public class RouterNode {
    private int myID;
    private GuiTextArea myGUI;
    private RouterSimulator sim;
    private int[] costs = new int[RouterSimulator.NUM_NODES];

    //--------------------------------------------------
    public RouterNode(int ID, RouterSimulator sim, int[] costs) {
        System.out.println("Node nr " + ID);

        for (int i=0; i<costs.length; i ++){
            System.out.println(costs[i]);
        }

        System.out.println("");

        myID = ID;
        this.sim = sim;
        myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");
        //System.out.println(" mygiu " + (myGUI == null));
        System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);

        printDistanceTable();

    }

    //--------------------------------------------------
    public void recvUpdate(RouterPacket pkt) {

    }


    //--------------------------------------------------
    private void sendUpdate(RouterPacket pkt) {
        sim.toLayer2(pkt);

    }


    //--------------------------------------------------
    public void printDistanceTable() {

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
    }

    //--------------------------------------------------
    public void updateLinkCost(int dest, int newcost) {
    }

}
