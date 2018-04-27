package org.cacrawford.neuralnet

import java.io.File

import edu.utulsa.cli.validators.{ValidateError, ValidateSuccess}
import edu.utulsa.cli.{Action, CLIApp, Command, Param, validators}
import org.cacrawford.net

object Driver extends CLIApp {

  // the CLI app junk is something I wrote because I like being able to specify things using a CLI

  val numNodes: Param[Int] = Param("num-nodes")
    .help("The number of nodes to run the simulation with.")
    .default(100)
    .validation(validators.INT_GEQ(1))
    .register

  val numNeuronsPerNode: Param[Int] = Param("neurons-per-node")
    .help("The number of neurons per node.")
    .default(50)
    .validation(validators.INT_GEQ(1))
    .register

  val initNeurons: Param[Int] = Param("num-init-neurons")
    .help("The number of neurons that are activated initially.")
    .default { $(numNeuronsPerNode) } // wrap in braces so it isn't evaluated immediately, which is currently a bug
    .validation(validators.INT_GEQ(1))
    .register

  val numIterations: Param[Int] = Param("iterations")
    .help("The number of iterations to run the simulation.")
    .default(100)
    .validation(validators.INT_GEQ(1))
    .register

  val sparse: Param[Double] = Param("sparse")
    .help("Parameter specifying how sparse the DC chains will be.")
    .default(1d)
    .validation(validators.DOUBLE_GEQ(1))
    .register

  val scale: Param[Double] = Param("scale")
    .help("Parameter specifying how large the weights on the DC chains will be.")
    .default(1d)
    .validation(validators.DOUBLE_GEQ(0))
    .register

  val k: Param[Int] = Param("k")
    .help("The node degree in the network.")
    .default(2)
    .validation((x: Int) => if(x % 2 == 0) ValidateSuccess() else ValidateError("k must be even."))

  val neuronType = Action[Neuron]("neuron-type")
    .add(new Command[Neuron] {
      override val name: String = "mcp"
      override val help: String = "McCulloch-Pitts neuron"

      override def exec() = new MCPNeuron($(numNeuronsPerNode))
    })
    .add(new Command[Neuron] {
      override val name = "sigmoid"
      override val help: String = "Sigmoid neuron"

      override def exec() = new SigmoidNeuron($(numNeuronsPerNode))
    })
    .register

  // Network generation type
  val networkType = Action[net.Generator[Neuron]]("network-type")
    .add(new Command[net.Generator[Neuron]] {
      override val name = "watts-strogatz"
      k.register
      val beta: Param[Double] = Param("beta")
        .help("The probability of rewiring each edge.")
        .default(0.01)
        .validation(validators.AND(validators.DOUBLE_GEQ(0), validators.DOUBLE_LEQ(1)))
        .register
      override def exec() = new net.WattsStrogatz[Neuron]($(k), $(beta))
    })
    .add(new Command[net.Generator[Neuron]] {
      override val name = "ring"
      k.register
      override def exec() = new net.Ring($(k))
    })
    .add(new Command[net.Generator[Neuron]] {
      override def name = "chain"
      k.register
      override def exec() = new net.Chain[Neuron]($(k))
    })
    .add(new Command[net.Generator[Neuron]] {
      override def name = "erdos-renyi"
      val p: Param[Double] = Param("p")
        .help("The probability of an edge between two nodes existing.")
        .default(0.01)
        .validation(validators.AND(validators.DOUBLE_GEQ(0), validators.DOUBLE_LEQ(1)))
        .register
      override def exec() = new net.ErdosRenyi[Neuron]($(p))
    })
    .register


  def genSim(useVis: Boolean = false): Simulator = {
    val nodes: Seq[Neuron] = (1 to $(numNodes)).map(_ => $(neuronType).exec())
    val net = $(networkType).exec()(nodes)
    nodes.head.init($(initNeurons), 1)
    val vis = if(useVis) {
      val vis1 = new NetworkVisualizer()
      net.nodes.foreach(vis1.addNode)
      net.edges.foreach { case (n1, n2) => vis1.addEdge(n1, n2) }
      vis1.updateNeuron(nodes.head, nodes.head.mean)
      Some(vis1)
    } else None
    val dcNet = DCNetwork.generate(net.nodes, net.edges, $(sparse), $(scale))
    new Simulator(dcNet, vis=vis)
  }

  val action: Action[Unit] = Action("action")
    .add(new Command[Unit] {
      override val name = "run"
      override val help = "Runs a single simulation of the network."

      override def exec(): Unit = {
        val sim = genSim(useVis = true)
        sim.run()
      }
    })
    .add(new Command[Unit] {
      override val name = "run-trials"
      override val help = "Runs a number of trials under the same configuration and saves the results in a data file."

      val numTrials: Param[Int] = Param("num-trials")
        .help("The number of trials to run this simulation for.")
        .default(10)
        .validation(validators.INT_GEQ(1))
        .register

      val outputFile: Param[File] = Param("output-file")
        .help("The file to write output to.")
        .default(new File("output.csv"))
        .register

      override def exec(): Unit = {
        for(trial <- 1 to $(numTrials)) {
          println(s"trial $trial")
          val sim = genSim()
          sim.run()
        }
      }
    })
    .register

  $.parse()
  $(action).exec()
}
