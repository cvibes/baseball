public class FFPlus
{
    private boolean[] marked;
    private FlowEdge[] edgeTo;
    private double value;
    private FlowNetwork g;
    private int s, t;
    private final double DELTA = 1E-11;
    public FFPlus(FlowNetwork G, int source, int target) {
        g = G;
        s = source;
        t = target;

        while(hasAugmentingPath()) {
            double bottle = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v))
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));

            for (int v = t; v != s; v = edgeTo[v].other(v))
                edgeTo[v].addResidualFlowTo(v, bottle);

            value += bottle;
        }
    }

    public double value() { return value; }
    public boolean inCut(int v) { return marked[v]; }
    public boolean allIsFull() {
        double totalFlow = 0.0;
        for (FlowEdge e : g.adj(s))
            totalFlow += e.capacity();
        return Math.abs(totalFlow - value) < DELTA;
    }

    private boolean hasAugmentingPath() {
        marked = new boolean[g.V()];
        edgeTo = new FlowEdge[g.V()];
        Queue<Integer> q = new Queue<Integer>();

        marked[s] = true;
        q.enqueue(s);
        while(!q.isEmpty()) {
            int v = q.dequeue();
            for(FlowEdge e : g.adj(v)) {
                int w = e.other(v);
                if(e.residualCapacityTo(w) > 0 && !marked[w]) {
                    edgeTo[w] = e;
                    marked[w] = true;
                    q.enqueue(w);
                }
            }
        }
        return marked[t];
    }
}
