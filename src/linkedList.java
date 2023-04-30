
public class linkedList {
	listNode head;
	listNode tail;
	int length;
	
	linkedList(){
		length=0;
		head=new listNode(-1,-1,-1,-1,-1);//dummy node
		tail= head;
	}
	
	void add(listNode a) {
		length++;
		tail.next =a;
		tail= tail.next;
		
	}
	
	void removehead() {
		length--;
		if(head.next!=tail) {
			listNode temp=head.next;
			head.next=head.next.next;
			temp.next=null;
		}else {
			listNode temp=head.next;
			head.next=head.next.next;
			temp.next=null;
			tail=head;
		}
	}
	
	void insert(listNode a) {
		length++;
		listNode temp=head;
		while(temp.next!=null) {
			if (temp.next.arriveTime>a.arriveTime) {
				break;
			}else {
				temp=temp.next;
			}
		}a.next=temp.next;
		temp.next=a;
	}
}
