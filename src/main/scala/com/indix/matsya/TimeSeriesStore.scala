package com.indix.matsya

case class Metric(machineType: String,
                  az: String,
                  price: Double,
                  timestamp: Long)

trait TimeSeriesStore extends AutoCloseable {
  def get(machineType: String, az: String): List[Metric]
  def exists(machineType: String, az: String): Boolean
  def batchPut(instanceType: String, az: String, metrics: List[Metric])
}
