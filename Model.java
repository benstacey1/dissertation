import java.awt.Point;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")

public class Model {

    static int numberOfNodes = 1000;
    static int iterations = 1000;
    static int width = 60;
    static int height = 60;
    static int stepSize = 1;
    static long seed = 50;
    static Random rand = new Random(seed);

    // Make the world (as a grid)
    static int[][] world = new int[width][height];

    public static void runModel(int trials) {
        for (int i = 1; i < trials + 1; i++) {
            for (int j = 1; j < trials + 1; j++) {
                double probOfInfected = 0.05 * i;
                int countInfected = 0;

                // Make the nodes

                ArrayList<Node> allNodes = new ArrayList<Node>();

                for (int counter = 0; counter < numberOfNodes; counter++) {
                    Node node = new Node();
                    node.infectionChance = 0.05 * j;

                    // Position the node.
                    node.x = (int) (rand.nextDouble() * width);
                    node.y = (int) (rand.nextDouble() * height);

                    // Let the node know about the world.
                    node.world = world;
                    node.width = width;
                    node.height = height;

                    // Ensure all nodes have the list of nodes.
                    node.allNodes = allNodes;

                    // Make a node infected.

                    if (rand.nextDouble() < probOfInfected) {
                        node.infected = true;
                        node.timesInfected++;
                    }

                    // Add the node to the list of all nodes.
                    allNodes.add(node);
                }

                // Run the model for the requested number of steps.

                for (int counter = 0; counter < iterations; counter++) {
                    // Move all the nodes about.
                    for (Node node : allNodes) {
                        node.interact(stepSize);
                    }

                    // Once all the nodes have moved, let them begin the infection.
                    for (Node node : allNodes) {
                        node.infect();
                    }

                    // Iterate through the nodes, removing any that need to be removed.
                    allNodes.removeIf(node -> node.toBeRemoved);

                    countInfected = 0;

                    for (Node node : allNodes) {
                        if (node.infected) {
                            countInfected++;
                        }
                    }

                    // System.out.println(counter + " , " + countInfected);
                }

                countInfected = 0;

                for (Node node : allNodes) {
                    if (node.infected) {
                        countInfected++;
                    }
                }

                int totDays = 0;

                for (Node node : allNodes) {
                    totDays += node.daysInfected;
                }

                double avgDays = (double) totDays / numberOfNodes;

                int totTimesInfected = 0;

                for (Node node : allNodes) {
                    totTimesInfected += node.timesInfected;
                }

                double avgTimesInfected = (double) totTimesInfected / numberOfNodes;

                /*
                 * System.out.println("After " + iterations + " iterations, there are " +
                 * countInfected + " infected.");
                 * System.out.println("The average number of days infected is " + avgDays);
                 * System.out.println("The average number of times infected is " +
                 * avgTimesInfected);
                 */

                double percentInfected = (double) countInfected / numberOfNodes;
                double infectionChance = (double) 0.05 * j;

                System.out.println("Prob of initial infected: " + probOfInfected);
                System.out.println("End number of infected individuals (percentage): " + percentInfected);
                System.out.println("Infection chance:" + infectionChance);
            }

        }

    }

    public static void singleModel() {
        double probOfInfected = 0.1;
        int countInfected = 0;
        double infectionChance = 0.5;

        // Make the nodes

        ArrayList<Node> allNodes = new ArrayList<Node>();

        for (int counter = 0; counter < numberOfNodes; counter++) {
            Node node = new Node();
            node.infectionChance = infectionChance;
            node.nodeNumber = "n_" + counter;

            // Position the node.
            node.x = (int) (rand.nextDouble() * width);
            node.y = (int) (rand.nextDouble() * height);

            // Let the node know about the world.
            node.world = world;
            node.width = width;
            node.height = height;

            // Ensure all nodes have the list of nodes.
            node.allNodes = allNodes;

            // Make a node infected.

            if (rand.nextDouble() < probOfInfected) {
                node.infected = true;
                node.timesInfected++;
            }

            // Add the node to the list of all nodes.
            allNodes.add(node);
        }

        // Run the model for the requested number of steps.

        FileWriter infectOTime;

        try {
            infectOTime = new FileWriter("infectOTime" +  "m_" + seed + "n_" + Node.seed + "s_" + width*height + "t_" + numberOfNodes + "init_" + probOfInfected + "infC_" + infectionChance + ".csv");
            infectOTime.append("Time step");
            infectOTime.append(",");
            infectOTime.append("No. of infected agents");
            infectOTime.append(",");
            infectOTime.append("No. of infected (rand)");
            infectOTime.append(",");
            infectOTime.append("No. of infected (det)");
            infectOTime.append("\n");

            for (int counter = 0; counter < iterations; counter++) {
                // Move all the nodes about.
                for (Node node : allNodes) {
                    node.interact(stepSize);
                }

                // Once all the nodes have moved, let them begin the infection.
                for (Node node : allNodes) {
                    node.infect();
                }

                // Iterate through the nodes, removing any that need to be removed.
                allNodes.removeIf(node -> node.toBeRemoved);

                countInfected = 0;

                for (Node node : allNodes) {
                    if (node.infected) {
                        countInfected++;
                    }
                }

                int randInfected = randomSample(allNodes, 0.3);
                int detInfected = detSample(allNodes, 400, 700);

                infectOTime.append(Integer.toString(counter));
                infectOTime.append(",");
                infectOTime.append(Integer.toString(countInfected));
                infectOTime.append(",");
                infectOTime.append(Integer.toString(randInfected));
                infectOTime.append(",");
                infectOTime.append(Integer.toString(detInfected));
                infectOTime.append("\n");

            }

            infectOTime.flush();
            infectOTime.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        countInfected = 0;

        for (Node node : allNodes) {
            if (node.infected) {
                countInfected++;
            }
        }

        int randSampleInfected = randomSample(allNodes, 0.3);
        int detSampleInfected = detSample(allNodes, 400, 700);

        int totDays = 0;

        for (Node node : allNodes) {
            totDays += node.daysInfected;
        }

        double avgDays = (double) totDays / numberOfNodes;

        int totTimesInfected = 0;

        for (Node node : allNodes) {
            totTimesInfected += node.timesInfected;
        }

        double avgTimesInfected = (double) totTimesInfected / numberOfNodes;

        System.out.println("After " + iterations + " iterations, there are " + countInfected + " infected.");
        System.out.println("After " + iterations + " iterations, there are " + randSampleInfected + " infected, as determined by the random sampler.");
        System.out.println("After " + iterations + " iterations, there are " + detSampleInfected + " infected, as determined by the deterministic sampler.");
        System.out.println("The average number of days infected is " + avgDays);
        System.out.println("The average number of times infected is " + avgTimesInfected);

        double percentInfected = (double) countInfected / numberOfNodes;

        System.out.println("Infection chance: " + infectionChance);
        System.out.println("Prob of initial infected: " + probOfInfected);
        System.out.println("End number of infected individuals (percentage): " + percentInfected);

        double popSizeRatio = (double) numberOfNodes / (height * width);

        System.out.println("The population:size ratio is: " + popSizeRatio);

        FileWriter csvWriter;

        try {
            csvWriter = new FileWriter("m_" + seed + "n_" + Node.seed + "s_" + width*height + "t_" + numberOfNodes + "init_" + probOfInfected + "infC_" + infectionChance + ".csv");
            csvWriter.append("Source");
            csvWriter.append(",");
            csvWriter.append("Target");
            csvWriter.append(",");
            csvWriter.append("Weight");
            csvWriter.append("\n");

            for (int n = 0; n < allNodes.size(); n++) {
                List<String> interactionsNode = allNodes.get(n).allInteractions;
                HashSet<String> interactionsNodeSet = new HashSet<String>();
                ArrayList<String> interactionsNodeND = new ArrayList<String>();
                int weight[] = new int[numberOfNodes];
                for (String i : interactionsNode) {
                    if (interactionsNodeSet.add(i))
                        interactionsNodeND.add(i);
                    else {
                        int iNew = Integer.parseInt(i.replaceAll("n_", ""));
                        weight[iNew] = weight[iNew] + 1;
                    }

                }

                for (String j : interactionsNodeND) {
                    csvWriter.append("n_" + n);
                    csvWriter.append(",");
                    csvWriter.append(j);
                    csvWriter.append(",");
                    int jNew = Integer.parseInt(j.replaceAll("n_", ""));
                    csvWriter.append(Integer.toString(weight[jNew] + 1));
                    csvWriter.append("\n");
                }

            }

            csvWriter.flush();
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void timeTaken() {

        Node node = new Node();

        // Position the node.
        node.x = (int) (rand.nextDouble() * width);
        node.y = (int) (rand.nextDouble() * height);

        // Let the node know about the world.
        node.world = world;
        node.width = width;
        node.height = height;

        while (node.coordinates.size() < width * height) {
            node.step(stepSize);
            iterations++;
        }
        System.out.println("Iterations to cover whole grid: " + iterations);

        /*
         * for(Point p : node.allCoords) { System.out.println(p.x + " " + p.y); }
         */
    }

    public static int randomSample(ArrayList<Node> allNodes, double p) {
        int countInfected = 0;

        for (Node node : allNodes) {
            if (node.infected && rand.nextDouble() < p)
                countInfected++;
        }

        return countInfected;
    }

    public static int detSample(ArrayList<Node> allNodes, int lBound, int rBound) {
        int countInfected = 0;
        for (Node node : allNodes) {
            if (node.infected && (Integer.parseInt(node.nodeNumber.replaceAll("n_", "")) < rBound) && (Integer.parseInt(node.nodeNumber.replaceAll("n_", "")) > lBound))
                countInfected++;
        }

        return countInfected;
    }

    public static void main(String[] args) {
        singleModel();
        timeTaken();
    }
}
