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

        System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);

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
        System.out.println("NODE TABLE");
        myGUI.println("Current table for " + myID +
                "  at time " + sim.getClocktime());
    }

    //--------------------------------------------------
    public void updateLinkCost(int dest, int newcost) {
    }

}
