package game;
import jgame.*;
import jgame.platform.*;

import java.lang.Math;

public class Fishing extends JGEngine{
	
	private double gameTime;
	private double numFishSpawned;
	private double depthAndWeight;
	private double directionalTimer=0;
	private boolean win = false;
	private boolean noFish = false;
	JGFont gameFont = new JGFont("Arial",0,20);
	JGFont textFont = new JGFont("Arial",0,13);
	
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
		drawString("Fishing for Treasure!",pfWidth()/2,20,0,gameFont,JGColor.white);
		drawString("Press Enter to Begin",pfWidth()/2,40,0,gameFont,JGColor.white);
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
		if (noFish) removeObjects("fish",2);
		if (Math.pow(gameTime/35,1.5)>=numFishSpawned && !noFish){
			new JGObject("fish",true,random(8,pfWidth()-8),pfHeight(),2,"fish_u",0,scaleSpeed,-3);
			numFishSpawned++;
		}
		if (getKey(' ')) noFish=true;
		if (getKey(KeyEsc)) setGameState("Title");
		
		// Cheat
		if (getKey(KeyEnter)) nextState("SecondMode");
	}
	
	public void paintFrameFirstMode(){
		drawString("Current Depth: "+depthAndWeight,pfWidth()/2,10,0,gameFont,JGColor.white);
	}
	
	/** Second Game Mode */
	public void startSecondMode(){
		removeObjects(null,0);
		new Reel();
		new HookedFish();
		gameTime=0;
	}
	
	public void doFrameSecondMode(){
		gameTime++;
		directionalTimer=gameTime/35;
		moveObjects();
		if (getKey(KeyEsc)) setGameState("Title");
		
		// Cheat
		if (getKey(KeyEnter)){
			win=true;
			nextState("End");
		}
	}
	
	public void paintFrameSecondMode(){
	}
	
	/** End Mode */
	public void startEnd(){
		removeObjects(null,0);
	}
	
	public void doFrameEnd(){
		if (getKey(KeyEsc)||getKey(KeyEnter)) nextState("Title");
	}
	
	public void paintFrameEnd(){
		if (win) drawString("You caught a "+depthAndWeight+" pound flounder!",pfWidth()/2,40,0,textFont,JGColor.white);
		else drawString("You have lost the fish!",pfWidth()/2,40,0,textFont,JGColor.white);
		drawString("Press Enter to go back to main menu.",pfWidth()/2,60,0,textFont,JGColor.white);
	}
	
	/** Player Object */
	class Hook extends JGObject{
		Hook(){
			super("hook",true,pfWidth()/2,30,1,"hook");
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
	
	/** Reel Object */
	class Reel extends JGObject{
		Reel(){
			super("reel",true,pfWidth()/2,40,1,"reel");
		}
		public void move(){
			if (getKey(KeyLeft)||getKey(KeyRight)) setGraphic("reel_c");
			else setGraphic("reel");  
		}
	}
	
	/** Hooked Fish Object */
	class HookedFish extends JGObject{
		HookedFish(){
			super("hookedFish",true,pfWidth()/2,pfHeight()-48,2,"fish_r");
			xspeed=random(-1,1);
		}
		
		public void move(){
			if ((xspeed<0 && getKey(KeyLeft) && !getKey(KeyRight))||(xspeed>0 && getKey(KeyRight) && !getKey(KeyLeft))){
				yspeed=-.02;
				if (getKey(' ')) yspeed=-2; // Cheat
			}
			else yspeed=1+depthAndWeight*.01;
			if ((xspeed<0 && x<16)||(xspeed>0 && x>pfWidth()-16)||(directionalTimer%3==0)||(directionalTimer%5==0)) xspeed=-xspeed;
			if (xspeed<0) setGraphic("fish_l"); else setGraphic("fish_r");
			if (y<40){
				win=true;
				setGameState("End");
			}
			else if (y>pfHeight()+16){
				setGameState("End");
			}
		}
	}
	
	public void nextState(String stateName){
		clearKey(KeyEnter);
		setGameState(stateName);
	}
}