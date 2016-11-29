public class Station {
	static int count=0;
	protected double meanService; 				//μέσος χρόνος εξυπηρέτησης - εκθετική κατανομή
	protected double routing[];					//πιθανότητες δρομολόγησης
	protected int length=0;						//αριθμός εργασιών στο σταθμό
	protected double oldclock=0.0;				//χρονική στιγμή τελευταίου γεγονότος στο σταθμό
	protected double sumBusyTime;				//άθροισμα διαστημάτων απασχόλησης
	protected double bt=0.0;
	
	public Station(double meanService){
		this.meanService=meanService;
	}
	 public void complete(Job job, double clock){}
	
	public void arrive(Job job, double clock){}
	
	public void extArrive(Job job, double clock){}
	
	public void exit(Job job, double clock){}
	
	protected int min(int a, int b){
		if (a<=b) return a;
		else return b;
	}
	
	public void withdraw(Job job, double clock) {}
	
	public double[] getRouting() {
		return routing;
	}
	public int getLength() {
		return length;
	}
	public double getSumBusyTime() {
		//sumBusyTime+=(Simulation.clock-oldclock);
		//oldclock=Simulation.clock;
		bt+=sumBusyTime;
		sumBusyTime=0.0;
		return bt;
	}
	
	
}
