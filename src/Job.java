public class Job implements Comparable<Job>{
	
	static int count=0;
	private int id;
	private int currentQueue; //τρέχων σταθμός
	private double request; //υπολειπόμενος χρόνος εξυπηρέτησης
	private double timeout;
	private double arrival;
	
	public Job(int currentQueue, double request, double arrival, double timeout){
		count++;
		this.id=count;
		this.currentQueue=currentQueue;
		this.request=request;
		this.arrival=arrival;
		this.timeout=timeout;
	}
	
	public int getCurrentQueue() {
		return currentQueue;
	}

	public void setCurrentQueue(int currentQueue) {
		this.currentQueue = currentQueue;
	}

	public int getId() {
		return id;
	}

	public double getRequest() {
		return request;
	}

	public void setRequest(double request) {
		this.request = request;
	}

	public double getTimeout() {
		return timeout;
	}

	public void setTimeout(double timeout) {
		this.timeout = timeout;
	}
	
	public double getArrival() {
		return arrival;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Job other = (Job) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(Job job) {
		if (this.getRequest()<job.getRequest())
			return -1;
		else if(this.getRequest()>job.getRequest())
			return 1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return "Job [id=" + id + ", currentQueue=" + currentQueue
				+ ", request=" + request + ", timeout=" + timeout + "]";
	}
	


}
