import java.util.PriorityQueue;

public class Simulation {
	
	static PriorityQueue<Event> eventsList;
	private int nq = 4;
	private Station[] queues = new Station[nq];
	private boolean endcycle = false;		//true if a job finds the system empty when arrives
	private boolean endsimul=false;			
	//over a cycle
	private int completions = 0;				//jobs that have normally left the system
	private double totalResponseTime = 0.0;	
	private double timeCycleStarted = 0.0;
	private double cycleLength;
	//over the whole simulation
	static double clock = 0.0;
	private int numberEvents =0;
	private int extArrival = 0;				//external arrivals
	private int nc=0;						//number of completions (for the whole system)
	private int ncsq=0;						//number of completions squared
	private double rt=0.0;					//totalResponseTime
	private double rtsq=0.0;					//totalResponseTime squared
	private double rtxnc=0;					//totalResponseTime*completions
	private double avgResponseTime=0.0;
	private double varrt, varnc, covarncrt, dqt;
	private int withdrawals=0;
	private double withdrawalsPerc=0.0;
	private double[] avgUtil=new double[4];
	private int numberCycles=0;
	private int nocycm1=0;					//number of cycles - 1
	private double sumcl = 0.0;
	private double sqnc;
	
	public Simulation(){
		eventsList = new PriorityQueue<Event>();
		double [] prop = {19/32.0, 12/32.0, 1/32.0};
		queues[0] = new StationPS(0.018, prop); 	//CPU
		queues[1] = new StationFCFS(0.028); 		//diskA
		queues[2] = new StationFCFS(0.034); 		//diskB
		queues[3] = new StationFCFS(0.416); 		//exit
	}
	
		
	private void nextStation(Job job, Station[] queues ){
		if (job.getCurrentQueue() == 0){
			double prob = Math.random();
			if (prob<queues[0].getRouting()[0])
				job.setCurrentQueue(1);
			else if(prob<(queues[0].getRouting()[0]+queues[0].getRouting()[1]))

				job.setCurrentQueue(2);
			else
				job.setCurrentQueue(3);
		}
		else if (job.getCurrentQueue()==1 || job.getCurrentQueue()==2){
			job.setCurrentQueue(0);
		}
		else{
			completions++;
			totalResponseTime+=clock-job.getArrival();
			queues[3].exit(job,clock);
			job.setCurrentQueue(4);	//fake queue, indicates that the job has left the system
		}
	}
	
	private void simul(){
		Job initialJob=new Job(0,0.0,0.0,30*Math.pow(-Math.log(Math.random()),2.0/3.0));
		eventsList.add(new Event(0.0,initialJob,0));
		eventsList.add(new Event(initialJob.getTimeout(),initialJob,2));
		extArrival++;
		while(numberCycles<1000 && !endsimul){
			//System.out.println(eventsList.toString());
			Event event = eventsList.remove();
			//System.out.println("Removing next event...");
			numberEvents++;
			Job job = event.getJob();
			if (job!=null){	
				clock=event.getTime();
				//System.out.println("clock: "+clock);		
				
				if (event.getType()==0){				//εξωτερική άφιξη
					extArrival++;
					//System.out.println("external arrival");
					endcycle=queues[0].getLength()==0&&queues[1].getLength()==0&&queues[2].getLength()==0&&queues[3].getLength()==0;
					int cq = job.getCurrentQueue();
					queues[cq].extArrive(job,clock);
				}
				else if(event.getType()==1){			//εσωτερική μετακίνηση
					int cq = job.getCurrentQueue();
					//System.out.println("from queue: "+cq);
					queues[cq].complete(job,clock);
					nextStation(job,queues);
					//System.out.println("to queue: "+job.getCurrentQueue());
					if(job.getCurrentQueue()!=4)
						queues[job.getCurrentQueue()].arrive(job,clock);
				}
				else{								//ληξη διεργασίας
					//System.out.println(job+" has expired");
					int cq = job.getCurrentQueue();
					queues[cq].withdraw(job,clock);
					withdrawals++;
				}
			
			//System.out.println("============================================");
			
				//CHECKCYCLE
				if(endcycle && numberEvents>3){
					numberCycles++;
					cycleLength=clock-timeCycleStarted;
					timeCycleStarted=clock;
					sumcl+=cycleLength;
					for(int i=0;i<nq;i++){
						avgUtil[i]=queues[i].getSumBusyTime()/sumcl;
					}
					withdrawalsPerc=1.0*withdrawals/extArrival;
					//Statistics every 20 cycles
					if(numberCycles%20==0&&numberCycles>=1){
						rt+=totalResponseTime;
						nc+=completions;
						ncsq+=Math.pow(completions, 2.0);
						rtsq+=Math.pow(totalResponseTime, 2.0);
						rtxnc+=totalResponseTime*completions;
						completions=0;
						totalResponseTime=0.0;
						nocycm1=numberCycles-1;
						avgResponseTime=rt/nc;
						sqnc=Math.pow(nc, 2.0);
						varnc=(ncsq-sqnc/numberCycles)/nocycm1;
						varrt=(rtsq-Math.pow(rt,2.0)/numberCycles)/nocycm1;
						covarncrt=(rtxnc-rt*nc/numberCycles)/nocycm1;
						dqt=0.5*1.96*Math.sqrt((varrt-2.0*avgResponseTime*covarncrt+Math.pow(avgResponseTime, 2.0)*varnc)/numberCycles)/(nc/numberCycles);
						endcycle=false;
						endsimul=(dqt<avgResponseTime);
					}
				}
			}
		}
		System.out.println("RESULSTS");
		System.out.println("***********************************************************************");
		System.out.println("CPU utilization:        "+avgUtil[0]);
		System.out.println("Disk A utilization:     "+avgUtil[1]);
		System.out.println("Disk B utilization:     "+avgUtil[2]);
		System.out.println("Exit Queue utilization: "+avgUtil[3]);
		System.out.println("Jobs that expire:       "+withdrawalsPerc*100+"%");
		System.out.println("Average System Response Time: "+avgResponseTime);
		System.out.println("Confidence Interval:     ["+(avgResponseTime-dqt)+","+(avgResponseTime+dqt)+"]");
	}
		
	public static void main(String[] args){
		
		Simulation s = new Simulation();
		s.simul();
					
	}
	
}
