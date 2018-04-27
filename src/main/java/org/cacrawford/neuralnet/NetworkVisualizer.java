package org.cacrawford.neuralnet;

import org.cacrawford.neuralnet.Neuron;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import scala.Tuple2;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Wrapper for the GraphStream network library, which is a huge hassle to work with in any language, but especially
 * Scala
 */
public class NetworkVisualizer {
  private Graph graph;
  private HashMap<Neuron, String> neuron2name = new HashMap<>();
  private HashMap<String, Neuron> name2neuron = new HashMap<>();
  private int idCount = 0;

  private String name(Neuron neuron) {
    return neuron2name.get(neuron);
  }

  public NetworkVisualizer() {
    graph = new MultiGraph("Neural Network Simulator");
    graph.addAttribute("ui.stylesheet", "" +
        "node {\n" +
        "  fill-mode: dyn-plain;\n" +
        "  fill-color: blue, red;\n" +
        "}\n" +
        "node.active {\n" +
        "  size: 20px;\n" +
        "}");
    graph.addAttribute("layout.stabilization-limit", 1.0);
    graph.display();
  }

//  public void addNodes(List<Neuron> neurons) {
//    for(int i = 0; i < neurons.size(); i++) {
//      Node name = graph.getNode(i);
//      Neuron neuron = neurons.get(i);
//      neuron2name.put(neuron, name.getId());
//      name2neuron.put(name(neuron), neuron);
//      updateNeuron(neuron, 0);
//    }
//  }

  public void addNode(Neuron neuron) {
    neuron2name.put(neuron, Integer.toString(idCount++));
    name2neuron.put(name(neuron), neuron);
    graph.addNode(name(neuron));
    updateNeuron(neuron, 0d);
  }

  public void addEdge(Neuron n1, Neuron n2) {
    graph.addEdge(name(n1) + "_" + name(n2), name(n1), name(n2));
  }

  public LinkedList<Tuple2<Neuron, Neuron>> getEdges() {
    LinkedList<Tuple2<Neuron, Neuron>> edges = new LinkedList<>();
    for(Edge e: graph.getEdgeSet()) {
      String id1 = e.getNode0().getId(),
          id2 = e.getNode1().getId();
      Neuron n1 = name2neuron.get(id1),
          n2 = name2neuron.get(id2);
      edges.add(new Tuple2<>(n1, n2));
    }
    return edges;
  }

  public void updateNeuron(Neuron neuron, double newValue) {
    Node n = graph.getNode(name(neuron));
    n.addAttribute("ui.color", newValue);
    if(newValue > 0)
      n.addAttribute("ui.class", "active");
    else if(n.hasAttribute("ui.class"))
      n.removeAttribute("ui.class");
  }
}
