package wdsr.exercise2.procon;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Task: implement Exchange interface using one of *Queue classes from java.util.concurrent package.
 */
public class BufferQueueImpl implements Buffer {
	
	private final int ORDER_QUEUE_CAPACITY = 100;
	
	private BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(ORDER_QUEUE_CAPACITY);
	
	public void submitOrder(Order order) throws InterruptedException {
		this.orderQueue.put(order);
	}
	
	public Order consumeNextOrder() throws InterruptedException {
		return this.orderQueue.take();
	}
}
