import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

public class model {
	int ct;
	BufferedWriter debug;
	BufferedWriter output;
	BufferedWriter extra;
	linkedList futureArrive;
	linkedList waiting;
	double[][] avgTime;
	int[][] counter;
	double Lambda;
	double Mu;
	double clock;
	int[] randomFloor= {1,2,3,4,5,6,7,7,8,9,10};
	int numberOfCustomers;
	elevator[] elevators;
	floor[]  floors;
	
	model(BufferedWriter d, BufferedWriter o, BufferedWriter e,double l, double m, int n){
		debug=d;
		ct=0;
		output=o;
		extra=e;
		Lambda=l;
		Mu=m;
		numberOfCustomers=n;
		clock=0.0;
		avgTime= new double[11][11];
		counter= new int[12][12];
		futureArrive=new linkedList();
		waiting=new linkedList();
		elevators = new elevator[6];
		for (int j = 1; j <=5; j++) {
			elevators[j]=new elevator(j);
			
		}
		floors =new floor[11];
		for (int k = 1; k <= 10; k++) {
			floors[k]=new floor(k);
		}
	}
	
	
	
	double getRandomInterarrival() {
		double u=new Random().nextDouble();
		double ans=(-Math.log(1-u))/Lambda;
		return ans;
	}
	
	double getRandomMu() {
		double u=new Random().nextDouble();
		double ans=(-Math.log(1-u))/Mu;
		return ans;
	}
	
	int getRandomFloor(int f) { // f is floor that customer located
		Random r=new Random();
		int x=r.nextInt(11-0);
		int ans = randomFloor[x];
		while(ans==f) {
			x=r.nextInt(11-0);
			ans = randomFloor[x];
		}
		return ans;
	}
	
	void standBy() {
		int count=0;
		double time=0.0;
		while (count<numberOfCustomers) {
			double nextA=getRandomInterarrival();
			time+=nextA;
			listNode c =new listNode(1,count,1, time,getRandomFloor(1));
			futureArrive.add(c);
			count++;
		}
	}
	
	int findIdleEle(int a) {
		int mind = Integer.MAX_VALUE;
		int ans=6;
		int i=1;
		while(i<6) {
			if(elevators[i].direction==0) {
				if(Math.abs(elevators[i].location-a)<mind) {
					mind=Math.abs(elevators[i].location-a);
					ans=i;
				}
			}i++;
		}return ans;
	}
	
	private void dothing1(listNode curr) throws IOException {//customer press button
		int e=findIdleEle(curr.location);
		if(e!=6&&curr.location<curr.destination) {
			floors[curr.location].up.add(curr);
			elevators[e].direction=1;
		}else if(e!=6){
			floors[curr.location].down.add(curr);
			elevators[e].direction=-1;
		}
		if(e!=6) {
			elevators[e].dest=curr.location;
			debug.write(clock+": Customer "+curr.cID+" has pressed button on "+curr.location+ " floor.\n");
			listNode n=new listNode(2,e,curr.location,clock+0.5);
			futureArrive.insert(n);
		}else {
			debug.write(clock+": Customer "+curr.cID+" has pressed button on "+curr.location+ " floor, placed in waiting list.\n");
			waiting.add(curr);
		}
	}
	

	private void dothing2(listNode curr) throws IOException {//move
		if(elevators[curr.eID].location<curr.destination) {
			elevators[curr.eID].location++;
			debug.write(clock+": Elevator "+curr.eID+" has reached "+elevators[curr.eID].location+ " floor.\n");
			listNode n=new listNode(2,curr.eID,curr.destination,clock+0.5);
			futureArrive.insert(n);
		}else if(elevators[curr.eID].location>curr.destination) {
			elevators[curr.eID].location--;
			debug.write(clock+": Elevator "+curr.eID+" has reached "+elevators[curr.eID].location+ " floor.\n");
			listNode n=new listNode(2,curr.eID,curr.destination,clock+0.5);
			futureArrive.insert(n);
		}else {
			debug.write(clock+": Elevator "+curr.eID+" has reached "+elevators[curr.eID].location+ " floor.\n");
			listNode n=new listNode(3,curr.eID,curr.destination,clock+1.0/6);
			futureArrive.insert(n);
		}
	}
	
	private void dothing3(listNode curr) throws IOException {//aboard
		if(elevators[curr.eID].direction==1&&floors[elevators[curr.eID].location].up.head.next!=null) {
			elevators[curr.eID].aboarding(floors[elevators[curr.eID].location].up.head.next);
			debug.write(clock+": Customer "+floors[elevators[curr.eID].location].up.head.next.cID+" has entered elevator "+curr.eID+ " .\n");
			floors[elevators[curr.eID].location].up.removehead();
			listNode n=new listNode(3,curr.eID,curr.direction,clock+1.0/6);
			futureArrive.insert(n);
		}else if(elevators[curr.eID].direction==-1&&floors[elevators[curr.eID].location].down.head.next!=null) {
			elevators[curr.eID].aboarding(floors[elevators[curr.eID].location].down.head.next);
			debug.write(clock+": Customer "+floors[elevators[curr.eID].location].down.head.next.cID+" has entered elevator "+curr.eID+ " .\n");
			floors[elevators[curr.eID].location].down.removehead();
			listNode n=new listNode(3,curr.eID,curr.direction,clock+1.0/6);
			futureArrive.insert(n);
		}else {
			debug.write(clock+": No Customer enters elevator "+curr.eID+ " .\n");
			listNode n=new listNode(4,curr.eID,curr.direction,clock+0.5);
			futureArrive.insert(n);
		}
	}
	
	private void dothing4(listNode curr) throws IOException {//recursion disembark and move
		if(elevators[curr.eID].direction==1&&elevators[curr.eID].dest!=elevators[curr.eID].location) {
			elevators[curr.eID].location++;
			debug.write(clock+": Elevator "+curr.eID+" has reached "+elevators[curr.eID].location+ " floor.\n");
			if(elevators[curr.eID].aboard[elevators[curr.eID].location].length>0){
				listNode n=new listNode(5,curr.eID,curr.direction,clock+1.0/6);
				futureArrive.insert(n);
			}else {
				listNode n=new listNode(4,curr.eID,curr.direction,clock+0.5);
				futureArrive.insert(n);
			}
		}else if(elevators[curr.eID].direction==-1&&elevators[curr.eID].dest!=elevators[curr.eID].location) {
			elevators[curr.eID].location--;		
			debug.write(clock+": Elevator "+curr.eID+" has reached "+elevators[curr.eID].location+ " floor.\n");
			if(elevators[curr.eID].aboard[elevators[curr.eID].location].length>0){
				listNode n=new listNode(5,curr.eID,curr.direction,clock+1.0/6);
				futureArrive.insert(n);
			}else {
				listNode n=new listNode(4,curr.eID,curr.direction,clock+0.5);
				futureArrive.insert(n);
			}
		}else if(elevators[curr.eID].dest==elevators[curr.eID].location) {
			if(elevators[curr.eID].aboard[elevators[curr.eID].location].length>0){
				listNode n=new listNode(5,curr.eID,curr.direction,clock+1.0/6);
				futureArrive.insert(n);
			}else {
				debug.write(clock+": Elevator "+curr.eID+" has stopped.\n");
				elevators[curr.eID].direction=0;
			}
		}
	}
	
	private void dothing5(listNode curr) throws IOException {//disembark
		int i=elevators[curr.eID].aboard[elevators[curr.eID].location].head.next.location;
		int j=elevators[curr.eID].aboard[elevators[curr.eID].location].head.next.destination;
		double t=clock-elevators[curr.eID].aboard[elevators[curr.eID].location].head.next.arriveTime;
		avgTime[i][j]+=t;
		counter[i][j]++;
		counter[i][11]++;
		counter[11][j]++;
		if(elevators[curr.eID].location!=1) {
			debug.write(clock+": Customer "+elevators[curr.eID].aboard[elevators[curr.eID].location].head.next.cID+" has leaved elevator "+curr.eID+" to "+elevators[curr.eID].location+ " floor.\n");
			listNode ncust=new listNode(1,elevators[curr.eID].aboard[elevators[curr.eID].location].head.next.cID,elevators[curr.eID].location,clock+getRandomMu(),getRandomFloor(elevators[curr.eID].location));
			elevators[curr.eID].aboard[elevators[curr.eID].location].removehead();
			futureArrive.insert(ncust);
		}else { // assume customer who reach 1st floor will leave building
			debug.write(clock+": Customer "+elevators[curr.eID].aboard[elevators[curr.eID].location].head.next.cID+" has leaved elevator "+curr.eID+" to "+elevators[curr.eID].location+ " floor.\n");
			elevators[curr.eID].aboard[elevators[curr.eID].location].removehead();
		}
		if(elevators[curr.eID].aboard[elevators[curr.eID].location].length>0){
			listNode n=new listNode(5,curr.eID,curr.direction,clock);
			futureArrive.insert(n);
		}else {
			listNode n=new listNode(4,curr.eID,curr.direction,clock+0.5);
			futureArrive.insert(n);
		}
	}
	
	void start() throws IOException {
		while(futureArrive.length>0||waiting.length>0) {
			int e=checkIdleEle();
			if(waiting.length==0||e==6) {
				listNode curr=futureArrive.head.next;
				futureArrive.removehead();
				clock=curr.arriveTime;
				if(curr.type==1) {
					dothing1(curr);
				}else if(curr.type==2) {
					dothing2(curr);
				}else if(curr.type==3) {
					dothing3(curr);
				}else if(curr.type==4) {
					dothing4(curr);
				}else if(curr.type==5) {
					dothing5(curr);
				}
			}else if(e!=6&&waiting.length!=0){
				listNode curr=waiting.head.next;
				waiting.removehead();
				if(curr.type==1) {
					dothing1(curr);
				}else if(curr.type==2) {
					dothing2(curr);
				}else if(curr.type==3) {
					dothing3(curr);
				}else if(curr.type==4) {
					dothing4(curr);
				}else if(curr.type==5) {
					dothing5(curr);
				}
			}printEX();
			if(futureArrive.head.next!=null) {
				debug.write("\tNext Arrival: "+futureArrive.head.next.printNode()+"\n");
			}else {
				debug.write("\tNext Arrival: Empty\n");
			}
			printDelay();
		}printOUT();
	}



	private void printDelay() throws IOException {
		listNode curr=waiting.head;
		debug.write("\tDelay List: ");
		while(curr.next!=null) {
			debug.write(curr.next.printNode()+" -> ");
			curr=curr.next;
		}
		debug.write("End\n");
	}



	private int checkIdleEle() {
		int i=1;
		while(i<6) {
			if(elevators[i].direction==0) {
				break;
			}i++;
		}
		return i;
	}

	private void printOUT() throws IOException {
		output.write("Avg Time: \n");
		output.write("0--|---1--|---2--|---3--|---4--|---5--|---6--|---7--|---8--|---9--|---10--| \n");
		for(int i=1; i<11;i++) {
			output.write(i+": | ");
			for(int j=1;j<11;j++) {
				double v=Math.round(avgTime[i][j]/counter[i][j]);
				output.write(v+" | ");
			}output.write("\n");
		}output.write("====================================================================\n");
		output.write("Customer number: \n");
		output.write("0\t\t1\t2\t3\t4\t5\t6\t7\t8\t9\t10\tTotal \n");
		for(int i=1; i<12;i++) {
			if(i==11) {
				output.write("Total: ");
			}else {
				output.write(i+":\t\t");
			}
			for(int j=1;j<12;j++) {
				output.write(counter[i][j]+"\t");
			}output.write("\n");
		}
	}
	
	private void printEX() throws IOException {
		if(ct<=100) {
			ct++;
			extra.write(ct+": Clock: "+clock+"\n");
			extra.write("0|\te1|\te2|\te3|\te4|\te5| \n");
			for(int i=10; i>0;i--) {
				extra.write(i+":\t");
				for(int j=1;j<6;j++) {
					if(elevators[j].location==i) {
						extra.write("|*|\t");
					}else {
						extra.write("o\t");
					}
				}extra.write("\n");
			}for(int i=10; i>0;i--) {
				extra.write("Floor "+i+": "+floors[i].sum()+" customers.\n");
			}
			for(int i=1; i<6;i++) {
				extra.write("Elevator "+i+": "+elevators[i].sum()+" customers.\n");
			}
			extra.write("====================================================================\n");
		}
	}
} 
