import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;



/* Author: David Yang, Tanzil Eshan
 * Project: Elevator Simulation
 * 2023/08/24
 */
public class main {
	public static void main (String args[]) throws IOException {
		int avelambda=Integer.parseInt(args[0]);
		int avemu=Integer.parseInt(args[1]);
		double lambda=1.0/avelambda;
		double mu=1.0/avemu;
		int c=Integer.parseInt(args[2]);
		BufferedWriter debug=new BufferedWriter(new FileWriter(args[3]));
		BufferedWriter output=new BufferedWriter(new FileWriter(args[4]));
		BufferedWriter extra=new BufferedWriter(new FileWriter(args[5]));
		model mm1=new model(debug,output,extra,lambda,mu,c);
		mm1.standBy();
		mm1.start();
		debug.close();
		output.close();
		extra.close();
	}
}
