/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;
public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	private void playGame() {
		/* You fill this in */
	
		playRound();
	}

	/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	
	
	private void diceRoll(int[] dice,boolean[] diceindex) {
				
	
		for(int i = 0;i<N_DICE;i++) {
		
		//	rgen.getInstance();
			if(diceindex[i]==true) dice[i]=rgen.nextInt(1,6);
			
		}
		display.displayDice(dice);
	}
	
	private void playRound() {
		
		int [][] category = new int[nPlayers][17];
		int[][] score = new int[nPlayers][17];
		boolean[] diceindex = new boolean[N_DICE];
		int[] dice = new int[N_DICE];
		int[] lastscores = new int [nPlayers];
		
		for(int j = 0;j<13;j++) {
			for(int i = 0;i<nPlayers;i++) {
				
				int score1=0;
				display.printMessage(playerNames[i] + "'s turn.");
				display.waitForPlayerToClickRoll(i+1);
				readyForRoll(diceindex,true);
				diceRoll(dice,diceindex);
				notFirstRoll(diceindex,dice);			
				int k = display.waitForPlayerToSelectCategory();
		
				if(category[i][k]!=0) {				
					 k = chooseAnother(k,category,i);
				}
				
				score1 = getScoreFromCategory(k,dice);
				display.updateScorecard(k, i+1, score1);
				score[i][k] = score1;
				category[i][k]++;
				
			}
		}
		
		addlastscores(score,nPlayers,lastscores);
		winner(score,nPlayers,playerNames,lastscores);
	}	
	/*
	 * with this i am making boolean array, for rolls.
	 */
	private void readyForRoll(boolean[] diceindex,boolean t) {
		
		for(int i=0;i<N_DICE;i++) {
			
			diceindex[i] = t;
		}

	}
	// selected dice's relevant boolean is turning into true while another is false,
	// that means, only selected dice while get another value.
	private void notFirstRoll(boolean[] diceindex, int[] dice) {
		
		for(int i=0;i<2;i++) {
			
			display.waitForPlayerToSelectDice();
			readyForRoll(diceindex,false);
			
			for(int j=0;j<N_DICE;j++) {
				
				if(display.isDieSelected(j)) diceindex[j] = true;
								
			}
			diceRoll(dice,diceindex);
			readyForRoll(diceindex,true);
		}
		
	}
	/*
	 * making test array with 6 variable, and adding 1 to testarray[n]. n equals to dice's value
	 *  
	 */
	private void makeTestArray(int[] dice,int [] test) {
		
		for(int i=0;i<N_DICE;i++) {
			
			test[dice[i]]++;								
		}
		
	}
	// finding longest sequence in test array, without zero values
		private int checkStreetNgetScore(int[] test,int[]dice,int k) {
			
		/*
		 * I know that k is always 10 or 11, so with this i am getting k = 4 or k = 5,
		 * and it is length of street, i am doing this to avoid lots of IF statments.
		 */
			 k=((k+1)/2)-1;
			int cnt = 0;
			int t=0;
			for(int i=0;i<7;i++) {
				
					
					if(test[i]>0) {
						t++; 
						cnt=Integer.max(t,cnt);

					}
					else {
						t=0;
						cnt=Integer.max(t,cnt);
					
				}
				
			}
			//if k==4 i need to return 30 pts, and if k==5 40 pts.
			if(k<=cnt) return (k-1)*10;
			else return 0;
			
		}
	/*
	 * returns score 
	 */
	private int  getScoreFromCategory(int k,int[] dice) {
		int score =0;
		
		// if selected category is 1-6, with this i make sums of selected number
		if(k<7) {			
			for(int i = 0;i<N_DICE;i++) {
				if(dice[i]==k) score+=dice[i];
			}
			return score;
		}
		
		// sum of every number
		if(k==THREE_OF_A_KIND ||k==FOUR_OF_A_KIND ||k==CHANCE) {
			
			int r  = checkEqual(dice);
			if(r==4&&k>=THREE_OF_A_KIND||k==CHANCE||r==3&&k==THREE_OF_A_KIND) {
				
				for(int i=0;i<N_DICE;i++) {
					score+=dice[i];
				}
			}
			
			else score = 0;
			
			return score;
		}
		
		// checking test array, it there is variables with values 2 and 3, 
		if(k==FULL_HOUSE) {
			
			int[] test = new int[8];
			
			makeTestArray(dice,test);
			
			int k2=0,k3=0;
			for(int i=0;i<7;i++) {
				
				if(test[i]==2)	k2++;
				if(test[i]==3)	k3++;
			}
			if(k2==1&&k3==1) score=25;
			
			else score = 0;		
			
			return score;
		}
		/*
		 * checking straights with test array 
		 */
		if(k==SMALL_STRAIGHT||k==LARGE_STRAIGHT) {
			int t=k-2;
			int[] test = new int[7];
			makeTestArray(dice,test);
			
			score=checkStreetNgetScore(test,dice,t);
			return score;
		}
		
		if(k==YAHTZEE&&checkEqual(dice)==5) score=50;
		else score = 0;
		
		return score;
	}
	
	// find maximum number of dice's with same value ussing test array 
	private int checkEqual(int[] dice) {
		int[] test = new int[7];
		makeTestArray(dice,test);
		int t = 0; 
		for(int i=0;i<7;i++) {
			
			if(test[i]>t) t=test[i];
		}
		return t;
	}	

	// sum of scores, with for loop, if position == 0 it means i need lower score,  and if 1 upper score.
	private int  sumScores(int[][] score,int player,int position) {
		int cnt=0;
		int l=0,r=0;
		if(position == 0) {
			l=0;
			r=7;
		}
		else {
			l=9;
			r=16;
		}
		for(int i=l;i<r;i++) {
			
			cnt+=score[player][i];
			
		}
		
		return cnt;
	}
	// add scores to display, and if lower score >63 add bonus.
	private void addlastscores(int[][] score,int nplayers,int[] lastscores) {
		
		
		for(int i=0;i<nplayers;i++) {
			int lower = 0, upper = 0;
			 upper = sumScores( score, i,0);
			 lower = sumScores(score,i,1);
			 display.updateScorecard(UPPER_SCORE,i+1,upper);
			 int bonus =0;
			 if(upper>63) {
				 
				 bonus+=35;
				 display.updateScorecard(UPPER_BONUS,i+1,35);
			 }
			 else {
				 display.updateScorecard(UPPER_BONUS,i+1,0);
				 score[i][8] =0;
			 }
			 display.updateScorecard(LOWER_SCORE,i+1,lower);		 
			 display.updateScorecard(TOTAL,i+1,upper+lower+bonus);
			 
			 lastscores[i] = upper + lower + bonus;
		}
		
	}
	// choose another category, while it is already taken,
	private int chooseAnother(int k,int[][] category,int player) {
		
		display.printMessage("Choose Another Category");
		while(category[player][k]>0) {
			
			k = display.waitForPlayerToSelectCategory();
			display.printMessage("Choose Another Category");
		}
		return k;
	}
	// finding max score and getting winners name
	private void winner(int[][] score,int nPlayers,String[] players,int[] lastscores) {
		
		int playerindex = 0;
		int maxscore  = 0;
		
		for(int i=0;i<nPlayers;i++) {
			
			
			if(lastscores[i]>maxscore) {
				
				playerindex = i;
				maxscore = lastscores[i];
			}
		}
		display.printMessage("The Winner Is: " + players[playerindex] + " With " + maxscore + " Point!");
	//	return;
	}

}
