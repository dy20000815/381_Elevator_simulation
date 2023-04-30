
public class elevator {
	int eID;
	int direction;//0 means idle, 1 means up, -1 means down.
	int location;
	linkedList[] aboard;
	int dest;
	
	elevator(int e){
		eID=e;
		direction=0;
		location = 1;// default is on the 1st floor
		aboard=new linkedList[11];
		aboard[0]=null;
		dest=0;
		for(int i=1; i<=10;i++) {
			aboard[i]=new linkedList();
		}
	}
	
	double gotoF(double t, int f) {
		int d=Math.abs(location-f);
		location=f;
		return (t+d*0.5); //return minutes the elevator will arrive the target floor
	}

	public void aboarding(listNode c) {
		aboard[c.destination].add(c);
		if(c.destination>location) {
			direction=1;
			dest=Math.max(c.destination,dest);
		}else if(c.destination<location) {
			direction=-1;
			dest=Math.min(c.destination,dest);
		}
	}
	
	int sum() {
		int s=0;
		for(int i=1; i<11;i++) {
			s+=aboard[i].length;
		}return s;
	}
}
