# Diverging-Converging Chains Simulations on Arbitrary Networks

## Setup

To build the JAR file, run

    ./gradlew build shadowJar

on Linux/Mac or

     gradlew.bat build shadowJar

on Windows.

## Sample commands to run

To see a full description of all parameters used in the simulation,
see `src/main/scala/org/cacrawford/neuralnet/Driver.scala`. Here are
some sample commands that produce interesting inputs:

1. Runs a simulation on a Watts-Strogatz network:

        java -jar build/libs/neuralnet.jar mcp watts-strogatz run

    `watts-strogatz` can be swapped with `erdos-renyi`, `random` and `chain`.

2. Runs a simulation with a sigmoid neuron:

        java -jar build/libs/neuralnet.jar sigmoid chain run

3. Runs a simulation with higher sparsity:

        java -jar build/libs/neuralnet.jar mcp chain run --sparse=10

    There are similar parameters for the number of nodes
    (`--num-nodes`), neurons (`--num-neurons-per-node`), scale
    (`--scale`) and the degree of nodes in the
    ring/chain/Watts-Strogatz networks (`--k`).
