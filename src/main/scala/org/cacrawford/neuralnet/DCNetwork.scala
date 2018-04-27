package org.cacrawford.neuralnet

import breeze.linalg.DenseVector

import scala.util.Random

/**
  * Represents a network of DC chains.
  */
class DCNetwork private
(
  val nodes: Seq[Neuron],
  val edges: Seq[(Neuron, Neuron, DCChainLink)]
) {
  def step(): Unit = {
    // generate the new inputs that the network generates
    val updates: Map[Neuron, DenseVector[Double]] = edges.map {
      case (input: Neuron, output: Neuron, link: DCChainLink) =>
        (output, link(input))
    }
      .groupBy(_._1).mapValues(_.map(_._2).reduce(_ + _))

    // update each neuron with its new values
    for(node <- nodes) {
      if(updates contains node)
        node :-> updates(node)
      else
        node :-> DenseVector.zeros(node.size)
    }
  }
}

object DCNetwork {
  def apply(neurons: Seq[Neuron], links: Seq[(Neuron, Neuron, DCChainLink)]): DCNetwork =
    new DCNetwork(neurons, links)

  def generate(neurons: Seq[Neuron], edges: Seq[(Neuron, Neuron)],
               sparse: Double = 2d, scale: Double = 1.04d,
               rand: Random = new Random()): DCNetwork = {
    new DCNetwork(
      neurons,
      edges.map { case (n1, n2) => (n1, n2, DCChainLink.random(n1.size, n2.size, sparse, scale, rand)) }
    )
  }
}