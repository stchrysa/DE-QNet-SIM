import java.util.Iterator;
import java.util.LinkedList;
import java.lang.Math;

public class StationFCFS extends Station {

	private LinkedList<Job> queue; 	//ουρα εργασιών
	
	public StationFCFS(double meanService) {
		super(meanService);
		this.queue = new LinkedList<Job>();
	}
	
	public int getLength() {
		return queue.size();
	}
	
	public void exit(Job job, double clock){
		/**
		 * Αφαιρούνται από τη λίστα γεγονότων τα γεγονότα που αφορούν
		 * την εργασία που φεύγει από το σύστημα
		 */
		Iterator<Event> itrE = Simulation.eventsList.iterator();
		while (itrE.hasNext()){	
			Event itrEvent = itrE.next();
			if (itrEvent.getJob().getId()==job.getId()){
				itrE.remove();
			}
		}
	}
	
	public void withdraw(Job job,double clock) {
		/**
		 * Αφαιρείται από την ουρά η διεργασία που έληξε και από τη λίστα
		 * γεγονότων τα γεγονότα που την αφορούν
		 * Αν η ουρά δεν μείνει άδεια και αν η εργασία που έληξε ήταν η πρώτη
		 * δρομολογείται η εξυπηρέτηση της επόμενης
		**/
		//στατιστικά στοιχεία		
		length--;
		
		Job first=queue.peekFirst();
		
		queue.remove(job);
		Iterator<Event> itrE = Simulation.eventsList.iterator();
		while (itrE.hasNext()){	
			Event itrEvent = itrE.next();
			if (itrEvent.getJob().getId()==job.getId()){
				itrE.remove();
			}
		}
		
		if (queue.size()>=1 && first.equals(job)){
			Job nextJob=queue.peekFirst();
			nextJob.setRequest(-meanService*Math.log(Math.random()));
			Simulation.eventsList.add(new Event(clock+nextJob.getRequest(),nextJob,1));
		}
		
	}
	
	public void complete(Job job, double clock){
		/**
		 * Αφαιρείται η πρώτη εργασία από την ουρά και
		 * αν η ουρα δεν μείνει άδεια δρομολογείται η επόμενη εργασία
		 */

		
		//ενημέρωση ουράς
		queue.remove(job);
		super.length--;
		if (queue.size()>=1){
			queue.peekFirst().setRequest(-meanService*Math.log(Math.random()));
			Simulation.eventsList.add(new Event(clock+queue.peekFirst().getRequest(),queue.peekFirst(),1));
		}
		else{
			sumBusyTime+=clock-super.oldclock;
		}
	}
	
	public void arrive(Job job, double clock){
		/**
		 * Εισάγεται η εργασία στο τέλος της FIFO ουράς
		 * Δρομολογείται νέο event εξυπηρέτησης μόνο αν η εργασία βρήκε την ουρά άδεια
		 */
						
		//ενημέρωση μεταβλητών
		queue.add(job);
		super.length++;
		
		if(queue.size()==1){
			super.oldclock=clock;
			job.setRequest(-meanService*Math.log(Math.random()));
			Simulation.eventsList.add(new Event(clock+job.getRequest(),job,1));
		}
	}


}
