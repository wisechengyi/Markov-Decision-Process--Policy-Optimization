
public class mdp {

	private final  double  WALL = 100;
	private final double  pEnd = 1;
	private final double  nEnd = -1;
	private final int HEIGHT =4;
	private final int WIDTH =6;
	private double [][] map;
	
	private final double lambda = .99;
	private final double nonTerminalReward = -.04;
	private final double probIntended = .8;
	private final double probUnintended = .1;
	
	
	private int [][] policy;
	
	private char[] s = {'<','>','^','v'};
	
//	private final int UNDIRECTIONED = -1;
	private final int LEFT = 0;
	private final int RIGHT = 1;
	private final int UP = 2;
	private final int DOWN = 3;
//	private final int nACTIONS =4;
	
	
	public mdp() {
		
		policy = new int[HEIGHT][WIDTH];
		map = new double[HEIGHT][WIDTH];
		for (int i=0;i<HEIGHT;i++)
		{
			for (int j = 0; j < WIDTH; j++) {
				map[i][j]=0;
			}
		}
		
		map[1][1] = WALL;
		map[2][1] = WALL;
		map[0][2]=WALL;
		map[2][4]=WALL;
		map[0][5]=pEnd;
		map[1][5]=nEnd;
	
	}
	
	private boolean isStaticState(int row, int col) {
		if (map[row][col] == WALL || map[row][col] == pEnd
				|| map[row][col] == nEnd) {
			return true;
		}

		return false;
	}
	
	private boolean isTerminal(int row, int col)
	{
		if (map[row][col]==nEnd ||map[row][col]==pEnd)
		{
			return true;
		}
		
		return false;
	}
	
	public void valueIteration(int nIterations) {
		for (int it=0;it<nIterations;it++)
		{
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				if (isStaticState(i, j)) {
					continue;
				}
				double left = getUtil(i, j - 1, i, j);
				double up = getUtil(i - 1, j, i, j);
				double right = getUtil(i, j + 1, i,j);
				double down = getUtil(i + 1, j,i, j);

				double leftUtil = (left * probIntended + up *probUnintended + down*probUnintended)*lambda+nonTerminalReward;
				double upUtil = (up * probIntended + left *probUnintended + right*probUnintended)*lambda + nonTerminalReward;
				double rightUtil = (right * probIntended + up *probUnintended + down*probUnintended)*lambda + nonTerminalReward;
				double downUtil = (down * probIntended + left *probUnintended + right*probUnintended)*lambda + nonTerminalReward;
				
//				double leftUtil = (left * probIntended + up *probUnintended + down*probUnintended)*lambda;
//				double upUtil = (up * probIntended + left *probUnintended + right*probUnintended)*lambda ;
//				double rightUtil = (right * probIntended + up *probUnintended + down*probUnintended)*lambda ;
//				double downUtil = (down * probIntended + left *probUnintended + right*probUnintended)*lambda ;
				
//				double maxActionUtil =Math.max( Math.max(leftUtil,rightUtil),Math.max(upUtil,downUtil));
//				double maxUtil = nonTerminalReward + maxActionUtil*lambda; 
//				if (i==1 && j==4)
//				{
//					System.out.println(upUtil);
//				}
				double maxUtil = Math.max( Math.max(leftUtil,rightUtil),Math.max(upUtil,downUtil));
				
				map[i][j] = maxUtil;
				if (maxUtil == leftUtil)
				{
					policy[i][j]=LEFT;
				}
				else if (maxUtil == rightUtil)
				{
					policy[i][j]=RIGHT;
				}
				else if (maxUtil == upUtil)
				{	
					policy[i][j]=UP;
				}
				else if (maxUtil == downUtil) 
				{
					policy[i][j]=DOWN;
				}
				else
				{
					System.out.println("ERROR");
				}

			}
		}
		}
	}

	private double getUtil(int row, int col, int selfRow, int selfCol) {
		if (row<0 || row >=HEIGHT || col<0 ||col>=WIDTH)
		{
			return map[selfRow][selfCol];
		}
		
		if ( map[row][col]==WALL)
		{
			return map[selfRow][selfCol];
		}
		
//		if (isTerminal(row, col))
		{
			return map[row][col];
		}
		
//		return nonTerminalReward;
	}
	
	public void print()
	{
		for (int i=0;i<HEIGHT;i++)
		{
			for (int j = 0; j < WIDTH; j++) {
				if (map[i][j]==WALL)
				{
					System.out.format("X\t");
				}else
				{
					System.out.format("%.4f\t",map[i][j] );
				}
			}
			System.out.println();
		}
	}
	
	public void getResults(double[] results)
	{
		int count =0;
		for (int i=0;i<HEIGHT;i++)
		{
			for (int j = 0; j < WIDTH; j++) {
				if (!isStaticState(i, j))
				{
					results[count] = map[i][j];
					count++;
				}
			}
//			System.out.println();
		}
		
		if (count!=18)
		{
			System.out.println("Error count: "+count);
		}
	}
	
	public void printPolicy()
	{
		
		for (int i=0;i<HEIGHT;i++)
		{
			for (int j = 0; j < WIDTH; j++) {
				if (isStaticState(i, j))
				{
					System.out.format("X\t");
				}else
				{
					System.out.format("%s\t", s[policy[i][j]]);
				}
			}
			System.out.println();
		}
	}
	

}
