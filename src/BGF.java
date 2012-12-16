import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class BGF {

	private final  double  WALL = 100;
	private final double  B = -.1;
	private final double  S = -.1;
	private final double F = 5;
	private final int HEIGHT =8;
	private final int WIDTH =7;
	private double [][] map;
	
	private final double gamma = .99;
	private final double nonTerminalReward = -.1;
	private final double probIntended = .8;
	private final double probUnintended = .1;
	
	
	private int [][] policy;
	private char[] s = {'<','>','^','v'};
	
	private final int UNDIRECTIONED = -1;
	private final int LEFT = 0;
	private final int RIGHT = 1;
	private final int UP = 2;
	private final int DOWN = 3;
	private final int nACTIONS =4;
	
	private double [][][] Q; //[state row][state col][action]
	
	Random generator ;
	
	public BGF() {
		
		policy = new int[HEIGHT][WIDTH];
		Q = new double[HEIGHT][WIDTH][nACTIONS];
		
		map = new double[HEIGHT][WIDTH];
		
		for (int i=0;i<HEIGHT;i++)
		{
			for (int j = 0; j < WIDTH; j++) {
				map[i][j]=0;
				policy[i][j]=UNDIRECTIONED;
			}
		}
		
		map[0][3] = WALL;
		map[1][3] = WALL;
		map[2][3]=WALL;
		
		map[4][4]=WALL;
		map[4][5]=WALL;
		map[4][6]=WALL;
		
		map[1][1]=S;
		map[1][5]=B;
		map[6][5]=F;
		
		
		for (int i=0;i<HEIGHT;i++)
		{
			for (int j = 0; j < WIDTH; j++) {
				Q[i][j][LEFT]=0;
				Q[i][j][RIGHT]=0;
				Q[i][j][UP]=0;
				Q[i][j][DOWN]=0;
			}
		}
		
//		Q[1][1] = {WALL,WALL,WALL,WALL};
//		Q[2][1] = WALL;
//		Q[0][2]=WALL;
//		Q[2][4]=WALL;
//		for (int i=0;i<)
//		Q[0][5]={pEnd,pEnd,pEnd,pEnd};
//		Q[1][5]={nEnd,nEnd,nEnd,nEnd};

		
		generator = new Random(  );
	
	}
	
	private boolean isStaticState(int row, int col) {
		if (map[row][col] == WALL || isTerminal(row, col)) {
			return true;
		}

		return false;
	}
	
	private boolean isTerminal(int row, int col)
	{
		if (row<0 || row >=HEIGHT || col<0 || col>=WIDTH)
		{
			return false;
		}
		
		if (map[row][col]==S ||map[row][col]==B || map[row][col]==F)
		{
			return true;
		}
		
		return false;
	}
	
	public int pickAnAction()
	{
		
		int action = Math.abs(generator.nextInt() % nACTIONS);
		return action;
	}
	
	public int pickARow()
	{
		
		int row = Math.abs(generator.nextInt() % HEIGHT);
		return row;
	}
	
	public int pickAColumn()
	{
		
		int col = Math.abs(generator.nextInt() % WIDTH);
		return col;
	}
	
	public void QLearning(int nIterations) {

		Set<Integer> actions = new HashSet<Integer>();
		actions.add(LEFT);
		actions.add(RIGHT);
		actions.add(UP);
		actions.add(DOWN);

		int t = 1;
		for (int it =0; it < 50000; it++) {
			double alpha = (double) 60 / (double) (59+t);

			int action = pickAnAction();
			int i = pickARow();
			int j = pickAColumn();

//			double reward = getReward(i, j, action);

			if (isStaticState(i, j)) {
				continue;
			}
			t++;
			double Qdown = getFutureMaxQ(i,j,DOWN);
			double Qup = getFutureMaxQ(i,j,UP);
			double Qleft = getFutureMaxQ(i,j,LEFT);
			double Qright = getFutureMaxQ(i,j,RIGHT);
						
			double maxQ = Math.max( Math.max(Qdown,Qup), Math.max(Qleft,Qright));
			if (maxQ == Qdown)
			{
				policy[i][j]=DOWN;
			}
			else if (maxQ == Qup)
			{
				policy[i][j]=UP;
			}
			else if (maxQ == Qleft)
			{
				policy[i][j]=LEFT;
			}
			else if (maxQ == Qright)
			{
				policy[i][j]=RIGHT;
			}
			
//			System.out.println("maxQ: "+maxQ);
			Q[i][j][action] = Q[i][j][action]
					+ alpha
					* (nonTerminalReward + gamma * maxQ - Q[i][j][action]);
			
			map[i][j] =  Math.max( Math.max(Q[i][j][LEFT],Q[i][j][RIGHT]), Math.max(Q[i][j][UP],Q[i][j][DOWN]));


		}

		// if (needtobreak)
		// {break;}

		// System.out.println(action);
	}
	
	double getReward(int row, int col, int action)
	{
		int destRow=row;
		int destCol=col;
		switch (action) {
		case LEFT:
			destCol = Math.max(col - 1, 0);
			break;
		case RIGHT:
			destCol = Math.min(col + 1, WIDTH - 1);
			break;
		case UP:
			destRow = Math.max(row - 1, 0);
			break;
		case DOWN:
			destRow = Math.min(row + 1, HEIGHT - 1);
			break;
		}
		
		
		//arrived at dest
		
		if (isTerminal(destRow, destCol))
		{
			return map[destRow][destCol]*probIntended + nonTerminalReward*probUnintended*2;
		}
		
		if (action==LEFT || action==RIGHT)
		{
			if (isTerminal(destRow-1, destCol))
			{
				return nonTerminalReward*.9 + .1 * map[destRow-1][destCol];
			}
			
			if (isTerminal(destRow+1, destCol))
			{
				return nonTerminalReward*.9 + .1 * map[destRow+1][destCol];
			}
			
		}
		
		if (action==UP || action==DOWN)
		{
			if (isTerminal(destRow, destCol-1))
			{
				return nonTerminalReward*.9 + .1 * map[destRow][destCol-1];
			}
			
			if (isTerminal(destRow, destCol+1))
			{
				return nonTerminalReward*.9 + .1 * map[destRow][destCol+1];
			}
		}
		
		return nonTerminalReward;
	}
	
	double getFutureMaxQ( int  stateRow,  int stateCol, int action)
	{
		int destCol = stateCol;
		int destRow = stateRow;
		if (action == LEFT) {
			destCol = Math.max(destCol - 1, 0);
		} else if (action == RIGHT) {
			destCol = Math.min(destCol + 1, WIDTH-1);
		} else if (action == UP) {
			destRow = Math.max(destRow - 1, 0);
		} else if (action == DOWN) {
			destRow = Math.min(destRow + 1, HEIGHT-1);
		}
		else
		{
			System.out.println("DIRECTION ERROR");
		}
		
		if (map[destRow][destCol]!=WALL)
		{
			return map[destRow][destCol];
		}
		else
		{
			return map[stateRow][stateCol];
		}
		
//		if (isTerminal(destRow,destCol))
//		{
//			return map[destRow][destCol];
//		}
//		
//		
////		
//		if (map[destRow][destCol]==WALL)
//		{
//			 destCol = stateCol;
//			 destRow = stateRow;
//		}
////		
//		return Math.max(Math.max(Q[destRow][destCol][LEFT],Q[destRow][destCol][RIGHT]),
//				Math.max(Q[destRow][destCol][UP],Q[destRow][destCol][DOWN]));
//		
		
	}
	
//	double getQArond(int row, int col)
//	{
//		int tempI; 
//		int tempJ;
//		double tempMax = Double.NEGATIVE_INFINITY;
//		
//		tempI = row -1;
//		tempJ = col;
//		
//		if(legal(tempI,tempJ)) 
//		{
//			tempMax = max(tempMax,Q[])
//		}
//		
//	}
	
	private boolean legal(int row, int col)
	{
		if (row>=0 && row < HEIGHT && col >=0 &&col<WIDTH)
		{
			return true;
		}
		return false;
	}
	

//	private double getUtil(int row, int col, int selfRow, int selfCol) {
//		if (row<0 || row >=HEIGHT || col<0 ||col>=WIDTH)
//		{
//			return map[selfRow][selfCol];
//		}
//		
//		if ( map[row][col]==WALL)
//		{
//			return map[selfRow][selfCol];
//		}
//		
////		if (isTerminal(row, col))
//		{
//			return map[row][col];
//		}
//		
////		return nonTerminalReward;
//	}
	
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
				System.out.format("%.4f\t", map[i][j]);
				}
			}
			System.out.println();
		}
	}
	
	public void printPolicy()
	{
		
		for (int i=0;i<HEIGHT;i++)
		{
			for (int j = 0; j < WIDTH; j++) {
				if (map[i][j]==B)
				{
					System.out.format("B\t");
				}
				else if (map[i][j]==S)
				{
					System.out.format("S\t");
				}else if (map[i][j]==F)
				{
					System.out.format("F\t");
				}
				else if (isStaticState(i, j))
				{
					System.out.format("X\t");
				}
				else
				{
					System.out.format("%s\t", s[policy[i][j]]);
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
//					results[count] = Math.max(Math.max(Q[i][j][LEFT],Q[i][j][RIGHT]),
//						Math.max(Q[i][j][UP],Q[i][j][DOWN]));
					results[count] = map[i][j];
//					System.out.println(count);
					count++;					
				}
			}
//			System.out.println();
		}
		
		if (count!=18)
		{
			System.out.println("Error count");
		}
	}

}
