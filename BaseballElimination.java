public class BaseballElimination
{
    private int num;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] g;
    private ST<String, Integer> idOf;
    private String[] nameOf;
    private boolean[] tested;
    private boolean[] eliminated;
    private int maxWin;

    public BaseballElimination(String filename) {
        In in     = new In(filename);
        num       = in.readInt();

        wins      = new int[num];
        losses    = new int[num];
        remaining = new int[num];
        idOf      = new ST<String, Integer>();
        g         = new int[num][num];
        nameOf    = new String[num];
        tested    = new boolean[num];
        eliminated = new boolean[num];
        maxWin     = 0;

        for (int i = 0; i < num; i++) {
            String name = in.readString();
            idOf.put(name, i);
            nameOf[i]    = name;
            wins[i]      = in.readInt();
            losses[i]    = in.readInt();
            remaining[i] = in.readInt();
            if (wins[i] > maxWin) maxWin = wins[i];
            
            for (int j = 0; j < num; j++)
                g[i][j] = in.readInt();
        }
    }

    public int numberOfTeams() {
        return num;
    }

    public Iterable<String> teams() {
        Bag<String> t = new Bag<String>();
        return t;
    }

    public int wins(String team) {
        int id = idOf.get(team);
        return wins[id];
    }

    public int losses(String team) {
        int id = idOf.get(team);
        return losses[id];
    }

    public int remaining(String team) {
        int id = idOf.get(team);
        return remaining[id];
    }

    public int against(String team1, String team2) {
        int id1 = idOf.get(team1);
        int id2 = idOf.get(team2);
        return g[id1][id2];
    }

    public boolean isEliminated(String team) {
        int id = idOf.get(team);
        if (tested[id]) return eliminated[id];
        if (wins[id] + remaining[id] < maxWin) {
            tested[id] = true;
            eliminated[id] = false;
            return false;
        }

        if (num < 2) return false;
        if (num == 2) return wins[id] + remaining[id] < maxWin;
        FlowNetwork n = buildFN(id);
        StdOut.printf("%s\n", n.toString());
        return false;
    }

    private FlowNetwork buildFN(int id) {
        int total = num + (num - 1) * (num - 2) / 2 + 2;
        int s = total - 2;
        int t = total - 1;
        FlowNetwork n = new FlowNetwork(total);
        int cnt = num;
        OUTER:
        for (int i = 0; i < num; i++) {
            if (i == id) continue OUTER;
            FlowEdge e = new FlowEdge(i, t, wins[id] + remaining[id] - wins[i]);
            n.addEdge(e);

            INNER:
            for (int j = i + 1; j < num; j++) {
                if (j == id) continue INNER;
                FlowEdge sToG = new FlowEdge(s, cnt, g[i][j]);
                FlowEdge gToT1 = new FlowEdge(cnt, i, Double.POSITIVE_INFINITY);
                FlowEdge gToT2 = new FlowEdge(cnt, j, Double.POSITIVE_INFINITY);
                n.addEdge(sToG);
                n.addEdge(gToT1);
                n.addEdge(gToT2);
                cnt++;
            }
        }
        return n;
    }

    public Iterable<String> certificateOfElimination(String team) {
        Bag<String> s = new Bag<String>();
        return s;
    }

    public static void main(String[] args) {
        BaseballElimination be = new BaseballElimination(args[0]);
        StdOut.printf("%d\n", be.numberOfTeams());
        StdOut.printf("\n\n");
        be.isEliminated("Atlanta");
    }
}
