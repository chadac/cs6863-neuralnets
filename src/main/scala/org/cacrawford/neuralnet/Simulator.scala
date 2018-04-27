package org.cacrawford.neuralnet

import breeze.linalg.sum

class Simulator
(
  val dcNet: DCNetwork,
  val vis: Option[NetworkVisualizer] = None,
  val numIterations: Int = 10000
) {
  def run(): Unit = {
    if(vis.isDefined)
      Thread.sleep(5000) // wait 5 seconds so that the network looks like something\
    var iter = 1
    while(iter < numIterations && dcNet.nodes.map(_.mean).sum > 0) {
      dcNet.step()
      if (vis.isDefined) {
        for (n <- dcNet.nodes) vis.get.updateNeuron(n, n.mean)
        Thread.sleep(100)
      }
      iter += 1
    }
    println(s" finished in $iter iterations.")
  }
}
