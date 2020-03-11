import java.awt.Point;
import java.util.*;

public class Node {
    // Variables storing node properties.

    // Spatial coordinates.
    int x = 0;
    int y = 0;
    int daysInfected = 0;
    int timesInfected = 0;
    int daysImmune = 0;

    int maxDaysInfect = 100;
    int maxDaysImmune = 20;

    String nodeNumber;
    static long seed = 51;
    private Random rand = new Random(seed);

    boolean infected;
    boolean mask;
    boolean immune;
    public double infectionChance;
    List<Point> coordinates = new ArrayList<Point>();
    List<Point> allCoords = new ArrayList<Point>();
    List<String> allInteractions = new ArrayList<String>();

    // List of the whole population.
    ArrayList<Node> allNodes = null;

    // Environment so nodes can see next to them.
    int[][] world = null;
    int width = 0;
    int height = 0;

    // Check if dead or otherwise removed from the model.
    boolean toBeRemoved = false;

    void interact(int stepSize) {
        // Move the node randomly on the grid.
        if (rand.nextDouble() > 0.5) {
            x = x + stepSize;
        } else {
            x = x - stepSize;
        }

        if (rand.nextDouble() > 0.5) {
            y = y + stepSize;
        } else {
            y = y - stepSize;
        }

        // Ensure nodes stay on the grid.
        if (x < 0)
            x = 0;
        if (x >= width)
            x = width - stepSize;
        if (y < 0)
            y = 0;
        if (y >= height)
            y = height - stepSize;

    }

    void step(int stepSize) {
        allCoords.add(new Point(x, y));
        if (!coordinates.contains(new Point(x, y)))
            coordinates.add(new Point(x, y));
        interact(stepSize);
    }

    void infect() {
        // If the node is dead or otherwise removed, ignore it this round.
        if (toBeRemoved)
            return;

        // Count how many days the node has been infected for.
        if (infected) {
            daysInfected++;
        }

        // Count how many days the node has been immune for.
        if (immune) {
            daysImmune++;
        }

        // Check whether the nodes need to be made immune or susceptible again.
        if (this.daysInfected > maxDaysInfect) {
            this.infected = false;
            this.daysInfected = 0;
            this.immune = true;
        }

        if (this.daysImmune > maxDaysImmune) {
            this.immune = false;
            this.daysImmune = 0;
        }

        // Actions that the node will take.
        for (Node otherNode : allNodes) {

            // Get the next node in the list.
            // If the node is the node selected, skip.
            if (otherNode == this)
                continue;

            // If two nodes are overlapping, check the infection chance.
            if ((otherNode.x == x) && (otherNode.y == y)) {
                // If the node is infected, have a chance to infect the other node.
                if (otherNode.infected && !infected && !immune) {
                    if (rand.nextDouble() < infectionChance) {
                        infected = true;
                        timesInfected++;
                    }
                } else if (infected && !otherNode.infected && !otherNode.immune) {
                    if (rand.nextDouble() < infectionChance) {
                        otherNode.infected = true;
                        otherNode.timesInfected++;
                    }
                }

                allInteractions.add(otherNode.nodeNumber);
            }

        }
    }
}

