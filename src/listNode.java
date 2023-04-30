
public class listNode {
	int type;  //1 for customer, 2 for elevator, 3 for aboard, 4 for moving, 5 for disembark
	double arriveTime;
	int cID;
	int eID;
	int destination;
	int location;
	int direction;
	listNode next;
	
	listNode(int t, int c, int l,double a, int f){//1
		type=t;
		cID=c;
		arriveTime=a;
		destination=f;
		location=l;
		next=null;
	}
	
	listNode(int t,int e,int d,double a){//2
		type=t;
		eID=e;
		arriveTime=a;
		destination=d;
		next=null;
	}
	
}
