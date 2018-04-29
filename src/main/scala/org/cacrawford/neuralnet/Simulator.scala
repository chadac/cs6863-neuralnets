package org.cacrawford.neuralnet

import java.io.{File, PrintWriter}

import breeze.linalg.sum

import scala.collection.mutable

/** Stores statistics for a single moment of the simulation **/
case class TimeRecord
(
  time: Int,
  nodesActivated: Int,
  neuronsActivated: Int,
  avgIntensity: Double
)

/** Stores summary statistics for the simulation **/
case class SimRecord
(
  totalTime: Int,
  signalComplete: Boolean,
  totalIntensity: Double,
  timeRecords: Seq[TimeRecord]
)

object Simulator {
  def run
  (
    dcNet: DCNetwork,
    vis: Option[NetworkVisualizer] = None,
    numIterations: Int = 1000,
    verbose: Boolean = false
  ): SimRecord = {
    if(vis.isDefined)
      Thread.sleep(5000) // wait 5 seconds so that the network looks like something\
    var iter = 1
    var signalComplete = false
    val timeStats: mutable.ListBuffer[TimeRecord] = mutable.ListBuffer()
    while(iter < numIterations && dcNet.nodes.map(_.mean).sum > 0) {
      dcNet.step()
      if (vis.isDefined) {
        for (n <- dcNet.nodes) vis.get.updateNeuron(n, n.mean)
        Thread.sleep(100)
      }
      if(dcNet.nodes.last.mean > 0) signalComplete = true
      timeStats += createRecord(iter, dcNet)
      iter += 1
    }
    if(verbose)
      println(s" finished in $iter iterations.")
    SimRecord(
      iter,
      signalComplete,
      timeStats.map(t => t.avgIntensity * t.neuronsActivated).sum,
      timeStats
    )
  }

  def save(sims: Seq[SimRecord], output: String): Unit = {
    val simFile = new File(output + "_sim.csv")
    val timeFile = new File(output + "_time.csv")

    val pw1 = new PrintWriter(simFile)
    val pw2 = new PrintWriter(timeFile)
    pw1.write("total.time,signal.complete,total.intensity\n")
    pw2.write("sim,time,nodes.activated,neurons.activated,avg.intensity\n")
    for((sim, i) <- sims.zipWithIndex) {
      pw1.write(s"${sim.totalTime},${sim.signalComplete},${sim.totalIntensity}\n")
      for(tr <- sim.timeRecords) {
        pw2.write(s"$i,${tr.nodesActivated},${tr.neuronsActivated},${tr.avgIntensity}\n")
      }
    }
    pw1.close()
    pw2.close()
  }

  private def createRecord(time: Int, dcNet: DCNetwork): TimeRecord = {
    val activeNodes: Seq[Neuron] = dcNet.nodes.filter(n => sum(n.x) > 0)
    val activeNeurons: Seq[Double] = activeNodes.flatMap(n => n.x.toArray.filter(_ > 1e-2))
    TimeRecord(
      time,
      activeNodes.size,
      activeNeurons.size,
      if(activeNeurons.nonEmpty) activeNeurons.sum / activeNeurons.size else 0d
    )
  }
}
