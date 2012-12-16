import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TDtest {
	private static double viResults[];
	private static double tdResults[];
	
	private final static int SIZE = 18;

	@BeforeClass
	public static void setup()
	{
		System.out.println("Set Up");
		viResults = new double[SIZE];
		tdResults = new double[SIZE];
	}
	
	@Test
	public void q() {
		System.out.println("Q Learning:");
		TD test = new TD();
		test.QLearning(100);
		
		test.print();
		test.printPolicy();
		test.getResults(tdResults);
	}
	
	@Test
	public void vi() {
		System.out.println("Value Iteration:");
		mdp test = new mdp();
		test.valueIteration(1000);
		test.getResults(viResults);
		test.print();
		test.printPolicy();

	}

	@AfterClass
	public static void getStat()
	{
		double[] sq = new double[SIZE];
		for (int i=0;i<SIZE;i++)
		{
			sq[i]= (tdResults[i]-viResults[i])*(tdResults[i]-viResults[i]);
		}
		
		double sum = 0;
		for (int i=0;i<SIZE;i++)
		{
			sum+=sq[i];
		}
		sum/=SIZE;

		double rms = Math.sqrt(sum);
		System.out.format("RMS: %.4f\n",rms);
	}
}
