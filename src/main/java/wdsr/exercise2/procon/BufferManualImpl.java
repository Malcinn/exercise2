package wdsr.exercise2.procon;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Task: implement Exchange interface without using any *Queue classes from java.util.concurrent package.
 * Any combination of "synchronized", *Lock, *Semaphore, *Condition, *Barrier, *Latch is allowed.
 */
public class BufferManualImpl implements Buffer {
	
	private static final int ORDERS_CAPACITY = 100;
	
	private static final int ZERO = 0;
	
	private List<Order> orders = new LinkedList<>();
	
	private boolean isPermitToAdd = true;
	
	private boolean isPermitToRemove = false;
	
	/*
	 * Producent produkuje zamowienia jesli w buforze jest wolne miejsce.
	 * Producent sprawdza czy mozna dodac dane do bufoa
	 * jesli tak:
	 * to dodajemy
	 * jesli nie:
	 * to oczekujemy na zwolnienie miejsca(pobranie danych przez konsument), i dodajemy.
	 * (Skoro producent czeka, to porzebne jest powiadomienie producenta przez konsumenta 
	 * o tym ze jest wolne miejsce w buforze, po zdjeciu elementu z listy).
	 * 
	 * Konsument pobiera zamowienia z bufora jest, bufor nie jest pusty.
	 * konsument sprawdza czy mozna pobrac dane z bufora
	 * jesli tak:
	 * to pobieramy
	 * jesli nie:
	 * to oczekujemy na dodanie zamowienia przez producenta, nastepnie je pobieramy.
	 * (Taka sama sytuacja jak powyzej oczekiwanie konsumenta jest zwiazane z producentem, 
	 * wiec producent powinien powiadomic konsumenta o dostepnosci zamowien).
	 */
	
	public void submitOrder(Order order) throws InterruptedException {
		this.add(order);
	}
	
	public Order consumeNextOrder() throws InterruptedException {
		return this.remove();
	}
	
	public synchronized void add(Order order){
		// czekaj dopoki nie zwolni sie jakies miejsce w liscie;
		while(!this.isPermitToAdd){
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if( (orders.size()+1) == ORDERS_CAPACITY){
			isPermitToAdd = false;
			isPermitToRemove = true;
		} else if ( (orders.size()+1) < ORDERS_CAPACITY && (orders.size()+1) > ZERO ){
			isPermitToAdd = true;
			isPermitToRemove = true;
		}
		
		this.notifyAll();
		orders.add(order);
	}
	
	public synchronized Order remove(){
		// czekaj dopoki na liscie nie pojawi sie jakis objekt
		while (!this.isPermitToRemove){
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if( (orders.size()-1) == ZERO){
			isPermitToAdd = true;
			isPermitToRemove = false;
		} else if ( (orders.size()-1) < ORDERS_CAPACITY && (orders.size()-1) > ZERO ){
			isPermitToAdd = true;
			isPermitToRemove = true;
		}
		
		this.notifyAll();
		return orders.remove(orders.size()-1);
	}
}
