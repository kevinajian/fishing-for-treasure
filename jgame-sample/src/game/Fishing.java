package game;
import jgame.*;
import jgame.platform.*;

import java.lang.Math;

public class Fishing extends JGEngine{
	
	private double gameTime;
	private double numFishSpawned;
	private double depthAndWeight;
	private double directionalTimer;
	private boolean win = false;
	private boolean noFish = false;

	public static void main(String[]args){new Fishing(new JGPoint(240,480));}
	
	/** Application constructor. */
	public Fishing(JGPoint size){initEngine(size.x,size.y);}

	JGFont gameFont = new JGFont("Arial",0,20);
	JGFont textFont = new JGFont("Arial",0,13);
	
	public void initCanvas(){setCanvasSettings(15,30,16,16,null,JGColor.blue,null);}

	public void initGame(){
		setFrameRate(35,2);
		defineMedia("Fishing.tbl");
		setGameState("Title");
	}
	
	/** Title Screen */
	public void startTitle(){
		removeObjects(null,0);
		noFish=false;
		win=false;
	}
	
	public void doFrameTitle(){
		if (getKey(KeyEnter)) nextState("FirstMode");
	}
	
	// Instructions on Title
	public void paintFrameTitle(){
		drawString("Fishing for Treasure!",pfWidth()/2,20,0,gameFont,JGColor.white);
		drawString("Press Enter to Begin",pfWidth()/2,40,0,gameFont,JGColor.white);
		drawString("In the first mode, move left and right",pfWidth()/2,80,0,textFont,JGColor.white);
		drawString("using the arrow keys to avoid the fish.",pfWidth()/2,95,0,textFont,JGColor.white);
		drawString("The deeper you get, the bigger the fish",pfWidth()/2,110,0,textFont,JGColor.white);
		drawString("are.",pfWidth()/2,125,0,textFont,JGColor.white);
		drawString("In the second mode, the fish will move",pfWidth()/2,150,0,textFont,JGColor.white);
		drawString("left and right. Press left and right",pfWidth()/2,165,0,textFont,JGColor.white);
		drawString("using the arrow keys to match the fish's",pfWidth()/2,180,0,textFont,JGColor.white);
		drawString("movement, and press space rapidly to",pfWidth()/2,195,0,textFont,JGColor.white);
		drawString("reel the fish in.",pfWidth()/2,210,0,textFont,JGColor.white);
	}
	
	/** First Game Mode */
	public void startFirstMode(){
		new Hook();
		gameTime=0;
		numFishSpawned=0;
	}
	
	public void doFrameFirstMode(){
		gameTime++;
		moveObjects();
		checkCollision(2,1);
		double scaleSpeed = -3+gameTime*-.002;
		double fishSpawnRate = Math.pow(gameTime/35, 1.5);
		if (noFish) removeObjects("fish",2);
		if (fishSpawnRate>=numFishSpawned && !noFish){
			new JGObject("fish",true,random(8,pfWidth()-8),pfHeight(),2,"fish_u",0,scaleSpeed,-3);
			numFishSpawned++;
		}
		// Cheat
		if (getKey(' ')) noFish=true;
		if (getKey(KeyEsc)) setGameState("Title");
		if (getKey(KeyEnter)) nextState("SecondMode");
	}
	
	public void paintFrameFirstMode(){
		depthAndWeight=gameTime/2;
		drawString("Current Depth: "+depthAndWeight,pfWidth()/2,10,0,gameFont,JGColor.white);
	}
	
	/** Second Game Mode */
	public void startSecondMode(){
		removeObjects(null,0);
		try { Thread.sleep(1000); }
		catch (InterruptedException e) {}
		new JGObject("reel",true,pfWidth()/2,20,1,"reel_c");
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
//	class Reel extends JGObject{
//		Reel(){
//			super("reel",true,pfWidth()/2,20,1,"reel");
//		}
//		public void move(){
//			if (getKey(KeyLeft)||getKey(KeyRight)) setGraphic("reel_c");
//			else setGraphic("reel");  
//		}
//	}
	
	/** Hooked Fish Object */
	class HookedFish extends JGObject{
		HookedFish(){
			super("hookedFish",true,pfWidth()/2,pfHeight()-60,2,"fish_r");
			xspeed=random(-1,1);
		}
		
		public void move(){
			double hookedSpeed = -.02;
			double reelSpeed = -6;
			double escapeSpeed = 1+depthAndWeight*.005;
			if ((xspeed<0 && getKey(KeyLeft) && !getKey(KeyRight))||(xspeed>0 && getKey(KeyRight) && !getKey(KeyLeft))){
				yspeed=hookedSpeed;
				if (getKey(' ')) {
					clearKey(' ');
					yspeed=reelSpeed;
				}
			}
			else yspeed=escapeSpeed;
			if ((xspeed<0 && x<16)||(xspeed>0 && x>pfWidth()-16)||(directionalTimer%3==0)||(directionalTimer%5==0)) xspeed=-xspeed;
			if (xspeed<0) setGraphic("fish_l"); else setGraphic("fish_r");
			if (y<20){
				win=true;
				setGameState("End");
			}
			else if (y>pfHeight()+8){
				setGameState("End");
			}
		}
	}
	
	public void nextState(String stateName){
		clearKey(KeyEnter);
		setGameState(stateName);
	}
	
	
}