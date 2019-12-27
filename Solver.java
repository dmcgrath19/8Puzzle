import java.util.Comparator;
import java.util.LinkedList;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private boolean solvable = false;
    private LinkedList<Board> solution = new LinkedList<>();
    private Comparator<Node> hamming = new Hamming();
    private Comparator<Node> manhattan = new Manhattan();

    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();
        MinPQ<Node> pq = new MinPQ<Node>(manhattan);
        MinPQ<Node> twin = new MinPQ<Node>(manhattan);
        pq.insert(new Node(null, initial));
        twin.insert(new Node(null, initial.twin()));
        Node current;
        Node currentTwin;
        do {
            current = delete(pq);
            currentTwin = delete(twin);
        } while (!current.val.isGoal() && !currentTwin.val.isGoal());
        if (current.val.isGoal()) {
            solvable = true;
            while (current != null) {
                solution.addFirst(current.val);
                current = current.prev;
            }
        }
    }

    public boolean isSolvable() {
        return solvable;
    }

    public int moves() {
        return solvable ? solution.size() - 1 : -1;
    }
    
    public Iterable<Board> solution() {
        return isSolvable()? solution : null;
    }

    private class Node implements Comparable<Node> {
        public Board val;
        private Node prev;
        private int moves;
        private int hamming;
        private int manhattan;

        public Node(Node prev, Board val) {

            this.prev = prev;
            this.val = val;
            moves = (prev == null) ? 0 : prev.moves + 1;
            hamming = val.hamming() + moves;
            manhattan = val.manhattan() + moves;
        }

        public int compareTo(Node n) {
            return hamming - n.hamming;
        }

    }

    private class Hamming implements Comparator<Node> {
        public int compare(Node n1, Node n2) {
            return n1.hamming - n2.hamming;
        }
    }

    private class Manhattan implements Comparator<Node> {
        public int compare(Node n1, Node n2) {
            return n1.manhattan - n2.manhattan;
        }
    }

    private Node delete(MinPQ<Node> q) {
        Node low = q.delMin();
        Iterable<Board> i = low.val.neighbors();
        for (Board b : i) {
            if (low.prev == null || !b.equals(low.prev.val))
                q.insert(new Node(low, b));
        }
        return low;
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In("test/puzzle4x4-unsolvable.txt");
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        System.out.println(initial);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}