package game;
import jgame.*;
import jgame.platform.*;

import java.lang.Math;

public class Fishing extends JGEngine{
	
	private double gameTime;
	private double numFishSpawned;
	private double depthAndWeight;
	private double directionalTimer=0;
	
	public static void main(String[]args){new Fishing(new JGPoint(240,480));}
	
	/** Application constructor. */
	public Fishing(JGPoint size){initEngine(size.x,size.y);}

	public void initCanvas(){setCanvasSettings(15,30,16,16,null,JGColor.blue,null);}

	public void initGame(){
		setFrameRate(35,2);
		defineMedia("Fishing.tbl");
		setGameState("Title");
	}
	
	/** Title Screen */
	public void startTitle(){
		removeObjects(null,0);
	}
	
	public void doFrameTitle(){
		if (getKey(KeyEnter)) nextState("FirstMode");
	}
	
	public void paintFrameTitle(){
		drawString("Fishing for Treasure!",pfWidth()/2,20,0);
		drawString("Press Enter to Begin",pfWidth()/2,40,0);
	}
	
	/** First Game Mode */
	public void startFirstMode(){
		new Hook();
		gameTime=0;
		numFishSpawned=0;
	}
	
	public void doFrameFirstMode(){
		gameTime++;
		depthAndWeight=gameTime/2;
		moveObjects();
		checkCollision(2,1);
		double scaleSpeed = -3+gameTime*-.002;
		if (Math.pow(gameTime/35,1.5)>=numFishSpawned){
			new JGObject("fish",true,random(8,pfWidth()-8),pfHeight(),2,"fish_u",0,scaleSpeed,-3);
			numFishSpawned++;
		}
		if (getKey(KeyEsc)) setGameState("Title");
		
		// Cheat
		if (getKey(KeyEnter)) nextState("SecondMode");
	}
	
	public void paintFrameFirstMode(){
		drawString("Current Depth: "+depthAndWeight,pfWidth()/2,20,0);
	}
	
	/** Second Game Mode */
	public void startSecondMode(){
		removeObjects(null,0);
		new JGObject("reel",true,pfWidth()/2,40,1,"ball");
		new HookedFish();
		gameTime=0;
	}
	
	public void doFrameSecondMode(){
		gameTime++;
		directionalTimer=gameTime/35;
		moveObjects();
		if (getKey(KeyEsc)) setGameState("Title");
		
		// Cheat
		if (getKey(KeyEnter)) nextState("End");
	}
	
	public void paintFrameSecondMode(){
	}
	
	/** Winning End Mode */
	public void startWin(){
		removeObjects(null,0);
	}
	
	public void doFrameWin(){
		if (getKey(KeyEsc)||getKey(KeyEnter)) setGameState("Title");
	}
	
	public void paintFrameWin(){
		drawString("YOU HAVE WON THE FUCKING GAME",pfWidth()/2,40,0);
		drawString("You caught a "+depthAndWeight+" pound flounder!",pfWidth()/2,60,0);
		drawString("Press Enter to go back to main menu.",pfWidth()/2,80,0);
	}
	
	/** Losing End Mode */
	public void startLose(){
		removeObjects(null,0);
	}
	
	public void doFrameLose(){
		if (getKey(KeyEsc)||getKey(KeyEnter)) setGameState("Title");
	}
	
	public void paintFrameLose(){
		drawString("You have lost the fish!",pfWidth()/2,40,0);
		drawString("Press Enter to go back to main menu.",pfWidth()/2,80,0);
	}
	
	/** Player Object */
	class Hook extends JGObject{
		Hook(){
			super("hook",true,pfWidth()/2,30,1,"ball");
		}
		
		public void move(){
			if (getKey(KeyLeft)&& !getKey(KeyRight)) xspeed=-2;
			else if (getKey(KeyRight)&& !getKey(KeyLeft)) xspeed=2;
			else xspeed=0;
			if (getKey(KeyDown)) xspeed=xspeed*2; // Cheat
			if ((x>pfWidth()-8 && xspeed>0)||(x<8 && xspeed<0)) xspeed=0;
		}
		
		public void hit(JGObject obj){
			setGameState("SecondMode");
		}
	}
	
	/** Hooked Fish Object */
	class HookedFish extends JGObject{
		HookedFish(){
			super("hookedFish",true,pfWidth()/2,pfHeight()-48,2,"fish_r");
			xspeed=random(-1,1);
		}
		
		public void move(){
			if (xspeed<0 && getKey(KeyLeft)) yspeed=-2;
			else if (xspeed>0 && getKey(KeyRight)) yspeed=-2;
			else yspeed=1+depthAndWeight*.005;
			if ((xspeed<0 && x<16)||(xspeed>0 && x>pfWidth()-16)||(directionalTimer%3==0)||(directionalTimer%5==0)) xspeed=-xspeed;
			if (xspeed<0) setGraphic("fish_l"); else setGraphic("fish_r");
			if (y<30) setGameState("Win");
			else if (y>pfHeight()+16) setGameState("Lose");
		}
	}
	
	public void nextState(String stateName){
		clearKey(KeyEnter);
		setGameState(stateName);
	}
}