package com.github.saleco.orders

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@SpringBootApplication
class OrdersApplication

fun main(args: Array<String>) {
	runApplication<OrdersApplication>(*args)
}

data class Order (var id: Int, var customerId: Int)

@Controller
class OrderRSocketController{

	private val db = mutableMapOf<Int, Collection <Order>>()

	init {
	    for(customerId in 0..3) {
	    	this.db[customerId] = randomOrdersFor(customerId)
		}
	}

	private fun randomOrdersFor(customerId: Int): Collection<Order> {
		val listOfOrders = mutableListOf<Order>()
		val max = (Math.random()*1000).toInt()
		for(orderId in 1..max) {
			listOfOrders.add(Order(orderId, customerId))
		}
		return listOfOrders
	}

	@MessageMapping("orders.{customerId}")
	fun getOrdersFor(@DestinationVariable customerId: Int) = Flux.fromIterable( this.db[customerId]!!.toList())
}
