package org.cacrawford

import scala.util.Random

/**
  * I'm using this instead of GraphStream for generating my networks because I really don't like GraphStream.
  */
package object net {

  trait Generator[T] {
    def apply(nodes: Seq[T]): Network[T]
  }

  class Ring[T](val k: Int) extends Generator[T] {
    override def apply(nodes: Seq[T]): Network[T] = {
      val edges: Seq[(T, T)] = nodes.indices.flatMap { i =>
        (i+1 to i+k+1).map(j =>
          (nodes(i), nodes(j % nodes.size))
        )
      }
      new Network[T](nodes, edges)
    }
  }

  class WattsStrogatz[T](val k: Int, val beta: Double, val rand: Random = new Random()) extends Generator[T] {
    override def apply(nodes: Seq[T]): Network[T] = {
      val ringNet = new Ring(k)(nodes)
      val newEdges: Set[(T, T)] = ringNet.edges.foldLeft(Set[(T,T)]()) { case (e: Set[(T,T)], next: (T,T)) =>
        if(rand.nextDouble() < beta) {
          val (n1, n2): (T, T) = next
          val i = nodes.indexOf(n1)
          val n3: T = {
            val k = rand.nextInt(nodes.size-1)
            if(k >= i) nodes(k+1) else nodes(k)
          }
          if(e contains (n1, n3))
            e + Tuple2(n1, n2)
          else
            e + Tuple2(n1, n3)
        } else {
          e + next
        }
      }
      new Network[T](nodes, newEdges.toSeq)
    }
  }

  class Chain[T](val k: Int) extends Generator[T] {
    override def apply(nodes: Seq[T]): Network[T] = {
      val edges: Seq[(T, T)] = nodes.indices.flatMap { i =>
        (i+1 to Math.min(i+k+1, nodes.size-1)).map(j =>
          (nodes(i), nodes(j % nodes.size))
        )
      }
      new Network[T](nodes, edges)
    }
  }

  class ErdosRenyi[T](val p: Double, rand: Random = new Random()) extends Generator[T] {
    override def apply(nodes: Seq[T]): Network[T] = {
      val edges: Seq[(T, T)] = nodes
        .flatMap(x => nodes.filter(_ != x).map((x, _))) // generate pairs; remove self-loops
        .filter(_ => rand.nextDouble() < p)
      new Network[T](nodes, edges)
    }
  }
}
