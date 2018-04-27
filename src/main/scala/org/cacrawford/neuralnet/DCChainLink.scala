package org.cacrawford.neuralnet

import breeze.linalg.{Axis, DenseMatrix, DenseVector, normalize, sum}
import breeze.numerics.pow

import scala.util.Random


/**
  * Implementation of a single layer of a diverging-converging chain.
  */
class DCChainLink(val w: DenseMatrix[Double]) {
  val width: Int = w.cols // number of neurons at the end of the link
  val height: Int = w.rows // number of neurons at the start of the link

  def apply(input: Neuron): DenseVector[Double] = w.t * input.x
  def inv(output: Neuron): DenseVector[Double] = w * output.x

  lazy val rate: Double = sum(sum(w ^:^ 2d, Axis._0) / pow(sum(w, Axis._0), 2d)) / width
}


object DCChainLink {
  def random
  (
    height: Int, width: Int,
    sparse: Double = 10d, scale: Double = 1.04d,
    rand: Random = new Random()
  ): DCChainLink = {
    val w: DenseMatrix[Double] = DenseMatrix.rand(height, width)
    for(r <- 0 until height) {
      w(r, ::) := normalize(w(r, ::).t ^:^ sparse, scale).t
    }
    new DCChainLink(w)
  }
}