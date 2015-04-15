public class BaseballElimination
{
    private int num;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] g;
    private ST<String, Integer> idOf;
    private String[] nameOf;
    private boolean[] eliminated;
    private int maxWin;
    private Bag<String> cert;
    private String maxWinner;
    private boolean[] trivial;

    public BaseballElimination(String filename) {
        In in     = new In(filename);
        num       = in.readInt();

        wins      = new int[num];
        losses    = new int[num];
        remaining = new int[num];
        idOf      = new ST<String, Integer>();
        g         = new int[num][num];
        nameOf    = new String[num];
        eliminated = new boolean[num];
        maxWin     = 0;
        cert       = new Bag<String>();
        trivial    = new boolean[num];

        for (int i = 0; i < num; i++) {
            String name = in.readString();
            idOf.put(name, i);
            nameOf[i]    = name;
            wins[i]      = in.readInt();
            losses[i]    = in.readInt();
            remaining[i] = in.readInt();

            if (wins[i] > maxWin) {
                maxWin = wins[i];
                maxWinner = name;
            }
            
            for (int j = 0; j < num; j++)
                g[i][j] = in.readInt();
        }

        for (int i = 0; i < num; i++)
            tryEliminate(i);
    }

    private void tryEliminate(int i) {
        if (wins[i] + remaining[i] < maxWin) {
            eliminated[i] = true;
            trivial[i] = true;
        }
        else if (num < 2)
            eliminated[i] = true;
        else if (num == 2)
            eliminated[i] = wins[i] + remaining[i] < maxWin;
        else {
            int total = num + (num - 1) * (num - 2) / 2 + 2;
            FlowNetwork n = buildFN(i, total);
            FFPlus ff = new FFPlus(n, total - 2, total - 1);
            eliminated[i] = !ff.allIsFull();
            if (eliminated[i])
                for (int k = 0; k < num; k++)
                    if (k != i && ff.inCut(k))
                        cert.add(nameOf[k]);
        }
    }

    private int getId(String team) {
        if (!idOf.contains(team))
            throw new IllegalArgumentException("Invalid team name");
        return idOf.get(team);
    }

    public int numberOfTeams() { return num; }

    public Iterable<String> teams() { return idOf.keys(); }

    public int wins(String team) { return wins[getId(team)]; }

    public int losses(String team) { return losses[getId(team)]; }

    public int remaining(String team) { return remaining[getId(team)]; }

    public int against(String team1, String team2) {
        return g[getId(team1)][getId(team2)];
    }

    public boolean isEliminated(String team) { return eliminated[getId(team)]; }

    private FlowNetwork buildFN(int id, int total) {
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
        if (isEliminated(team)) {
            int id = getId(team);
            if (trivial[id]) {
                Bag<String> w = new Bag<String>();
                w.add(maxWinner);
                return w;
            }
            else
                return cert;
        }
        else return null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
