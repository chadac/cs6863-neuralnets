#!/bin/bash

./gradlew shadowJar

cmd="java -jar build/libs/neuralnet.jar"

mkdir results

# mkdir results/nodes
# ## Test how the number of nodes affects transmission
# for num_nodes in $(seq -w 005 005 100); do
#     echo "nodes: " $num_nodes
#     ${cmd} mcp chain run-trials --num-nodes=$num_nodes --output-file="results/nodes/n$num_nodes"
#     sleep 1
# done

# mkdir results/neurons
# for num_neurons in $(seq -w 005 005 100); do
#     echo "neurons: " $num_neurons
#     ${cmd} mcp chain run-trials --neurons-per-node=$num_neurons --output-file="results/neurons/n$num_neurons"
#     sleep 0.1
# done

# mkdir results/neurons-sp1.5
# for num_neurons in $(seq -w 005 005 100); do
#     echo "neurons: " $num_neurons
#     ${cmd} mcp chain run-trials --sparse 1.5 --neurons-per-node=$num_nodes --output-file="results/neurons-sp1.5/n$num_neurons"
#     sleep 0.1
# done

# mkdir results/sparse
# for sparse in $(seq -w 1 0.2 10); do
#     echo "sparse: " $sparse
#     ${cmd} mcp chain run-trials --sparse $sparse --output-file="results/sparse/s$sparse"
#     sleep 0.1
# done

mkdir results/sigmoid-sparse
for sparse in $(seq -w 1 0.2 10); do
    echo "sparse: " $sparse
    ${cmd} sigmoid chain run-trials --sparse $sparse --output-file="results/sigmoid-sparse/s$sparse"
    sleep 0.1
done

# mkdir results/scale
# for scale in $(seq -w 1 0.1 2); do
#     echo "scale: " $scale
#     ${cmd} mcp chain run-trials --scale $scale --sparse 10.0 --output-file="results/scale/s$scale"
#     sleep 0.1
# done
