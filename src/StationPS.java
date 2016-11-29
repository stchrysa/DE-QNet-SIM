import java.util.Iterator;
import java.util.PriorityQueue;

public class StationPS extends Station {

	private PriorityQueue<Job> queue; 	//ουρα εγρασιών
	
	public StationPS(double meanService, double[] routing) {
		super(meanService);
		this.routing=routing;
		this.queue= new PriorityQueue<Job>(); 
	}
	
	public int getLength() {
		return queue.size();
	}
	
	public void extArrive(Job job, double clock){
		/**
		 * Καθορισμός επόμενης άφιξης
		 * καθορισμός timeout για την επόμενη άφιξη
		 * αφιξη τρέχουσας εργασίας στην CPU ουρά
		 */
		double T=clock-(1/1.65)*Math.log(Math.random());
		double timeout = T+30*Math.pow(-Math.log(Math.random()),2.0/3.0);
		Job newJob = new Job(0,0.0,T,timeout);
		Simulation.eventsList.add(new Event(T,newJob,0));
		Simulation.eventsList.add(new Event(newJob.getTimeout(),newJob,2));
		arrive(job,clock);		
	}
	
	public void withdraw(Job job,double clock) {
		/**
		 * Αφαιρείται από την ουρά η διεργασία που έληξε και από τη λίστα
		 * γεγονότων τα γεγονότα που την αφορούν
		 * Αν η ουρά δεν μείνει άδεια αφαιρούμε το γεγονός που αφορά
		 * την εξυπηρέτηση της πρώτης από τη λίστα γεγονότων 
		 * ενημερώνονται οι χρόνοι εξυπηρέτησής των υπολοίπων
		 * εισάγεται στην ουρά η αφιχθείσα  και δρομολογείται η εξυπηρέτηση της πρώτης
		**/
		//ενημέρωση ουράς
		length--;
		Job first=queue.peek();
		
		queue.remove(job);
		
		if(queue.size()>0){
			//remove the event associated with the first job in the CPU queue
			Event fe=null;	
			Iterator<Event> itrE = Simulation.eventsList.iterator();
			while (itrE.hasNext()&&fe==null){	
				Event itrEvent = itrE.next();
				if (itrEvent.getJob().getId()==first.getId()&&itrEvent.getType()==1){
					fe=itrEvent;
					itrE.remove();
				}
			}
			double T=fe.getTime();
			T=first.getRequest()-(T-clock)/queue.size();
			Iterator<Job> itrJ = queue.iterator();
			while(itrJ.hasNext()){	
				Job itrJob = itrJ.next();
				itrJob.setRequest(itrJob.getRequest()-T);
			}
			Job first1 = queue.peek();
			Simulation.eventsList.add(new Event(clock+first1.getRequest()*length,first1,1));
		}
		Iterator<Event> itrE = Simulation.eventsList.iterator();
		while (itrE.hasNext()){	
			Event itrEvent = itrE.next();
			if (itrEvent.getJob().getId()==job.getId()){
				itrE.remove();
			}
		}
		
	}
	
	public void complete(Job job, double clock){
		/**
		 * Αφαιρείται η πρώτη εργασία από την ουρά
		 * Aν η ουρα δεν μείνει άδεια ενημερώνονται οι χρόνοι εξυπηρέτησης
		 * και δρομολογείται η επόμενη εργασία
		 */
		
		//ενημέρωση ουράς
		super.length--;
		if(queue.size()==1){
			queue.remove(job);
			sumBusyTime+=clock-super.oldclock;
		}
		else{
			double T=job.getRequest();
			queue.remove(job);
			Iterator<Job> itr = queue.iterator();
			while(itr.hasNext()){
				Job itrJob = itr.next();
				itrJob.setRequest(itrJob.getRequest()-T);
			}
			Job nextJob=queue.peek();
			Simulation.eventsList.add(new Event(clock+nextJob.getRequest()*length,nextJob,1));
		}			
	}

	
	public void arrive(Job job,double clock){
		/**
		 * Αν η ουρά ήταν άδεια εισάγεται και δρομολογείται αμέσως αυτή που έφτασε
		 * Αν υπήρχαν κι άλλες εργασίες στην ουρά αφαιρούμε το γεγονός που αφορά
		 * την εξυπηρέτηση της πρώτης από τη λίστα γεγονότων 
		 * ενημερώνονται οι χρόνοι εξυπηρέτησής των υπολοίπων
		 * εισάγεται στην ουρά η αφιχθείσα  και δρομολογείται η εξυπηρέτηση της πρώτης
		 */
		
		//ενημέρωση ουράς
		if (queue.size()==0){
			super.oldclock=clock;
			job.setRequest(-meanService*Math.log(Math.random()));
			queue.add(job);
			super.length++;
			Simulation.eventsList.add(new Event(clock+job.getRequest(),job,1));
		}
		else{
			Job first = queue.peek();
			//remove the event associated with the first job in the CPU queue
			Event fe=null;	
			Iterator<Event> itrE = Simulation.eventsList.iterator();
			while (itrE.hasNext()&&fe==null){	
				Event itrEvent = itrE.next();
				if (itrEvent.getJob().getId()==first.getId()&&itrEvent.getType()==1){
					fe=itrEvent;
					itrE.remove();
				}
			}
			double T=fe.getTime();
			T=first.getRequest()-(T-clock)/queue.size();
			Iterator<Job> itrJ = queue.iterator();
			while(itrJ.hasNext()){	
				Job itrJob = itrJ.next();
				itrJob.setRequest(itrJob.getRequest()-T);
			}
			job.setRequest(-meanService*Math.log(Math.random()));
			queue.add(job);
			super.length++;
			Job first1 = queue.peek();
			Simulation.eventsList.add(new Event(clock+first1.getRequest()*length,first1,1));
		}
	}


}
