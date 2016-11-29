public class Event implements Comparable<Event>{
	
	private double time;
	private Job job;
	private int type; //0->εξωτερική άφιξη, 1-> τέλος εξυπηρέτησης

	public Event(double time, Job job, int type){
		this.time=time;
		this.job=job;
		this.type=type;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((job == null) ? 0 : job.hashCode());
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
		Event other = (Event) obj;
		if (job == null) {
			if (other.job != null)
				return false;
		} else if (!job.equals(other.job))
			return false;
		return true;
	}

	@Override
	public int compareTo(Event event) {
		if (this.getTime()<event.getTime())
			return -1;
		else if(this.getTime()>event.getTime())
			return 1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return "Event [time=" + time + ", job=" + job.getId()+ ", type=" + type + "]";
	}
	
}
