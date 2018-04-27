package org.cacrawford.neuralnet

import breeze.linalg.{DenseVector, inv, sum}
import breeze.numerics.exp

import scala.util.Random

/**
  * Abstract definition of a multi-neuron model
  * @param size The number of neurons this "neuron" represents
  */
abstract class Neuron(val size: Int) {
  val x: DenseVector[Double] = DenseVector.zeros(size)

  def mean: Double = sum(x) / size

  /**
    * Updates a network of neurons with new values.
    * @param newX Summed input values
    */
  def :->(newX: DenseVector[Double]): Unit = {
    require(x.length == newX.length,
      s"Dimension mismatch: Expected vector of length ${x.length}, received ${newX.length}.")
    x := f(newX)
  }

  def init(count: Int, weight: Double): Unit = {
    require(count <= size, "Attempt to initialize more neurons that available.")
    x(0 until count) := weight
  }

  /**
    * Specifies how a neuron updates based on sum of inputs.
    * @param newX summed input values
    * @return new neuron activations
    */
  protected def f(newX: DenseVector[Double]): DenseVector[Double]
}


/**
  * McCullock-Pitts neuron. Neuron activates if sum total of inputs is greater than 1, otherwise deactivates.
  * This also includes a brief deactivation period similar to that from class, where each individual neuron cannot
  * active twice.
  * @param size the number of neurons represented
  */
class MCPNeuron(override val size: Int) extends Neuron(size) {
  override def f(newX: DenseVector[Double]): DenseVector[Double] = {
    val values = newX.toArray.zip(x.toArray).map {
      case (newV, oldV) => if (newV >= 1d && oldV < 1d) 1d else 0d
    }
    DenseVector(values)
  }
}


/**
  * Sigmoid neuron, for graded inputs. Includes a basic deactivation function.
  * @param size The number of neurons this "neuron" represents
  */
class SigmoidNeuron(override val size: Int) extends Neuron(size) {
  override def f(newX: DenseVector[Double]): DenseVector[Double] = ((exp(2d-newX) + 1d)^:^(-1d)) /:/ (x + 1d)
}