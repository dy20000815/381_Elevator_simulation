
public class floor {
	int fID;
	linkedList up;
	linkedList down;
	
	floor(int f){
		fID=f;
		if(f!=10) {
			up=new linkedList();
		}else up=null;
		if(f!=1) {
			down = new linkedList();
		}else down = null;
	}

	int sum() {
		if(up==null) {
			return down.length;
		}else if(down==null) {
			return up.length;
		}else {
			return up.length+down.length;
		}
	}
	
}
