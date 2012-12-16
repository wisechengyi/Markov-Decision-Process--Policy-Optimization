import static org.junit.Assert.*;

import org.junit.Test;


public class ValueIterationTest {

	@Test
	public void test() {
		mdp test = new mdp();
		test.valueIteration(1000);
		test.print();
	}
	


}
