package game;
import jgame.*;
import jgame.platform.*;

import java.lang.Math;

public class Fishing extends JGEngine{
	private double gameTime;
	private double numFishSpawned;
	private double depthAndWeight;
	private double directionalTimer;
	private String win;
	private boolean noFishCheat = false;
	// Magic Number Removal 
	private double frameRate;
	private double halfWidth;
	private double margin;
	private int titleSpaceAndSize = 20;
	private int lineSpaceAndSize = 13;
	private int playerCID = 1;
	private int fishCID = 4;
	private int bootCID = 3;

	public static void main(String[]args){new Fishing(new JGPoint(240,480));}
	
	/** Application constructor. */
	public Fishing(JGPoint size){initEngine(size.x,size.y);}

	JGFont gameFont = new JGFont("Arial",0,titleSpaceAndSize);
	JGFont textFont = new JGFont("Arial",0,lineSpaceAndSize);
	
	public void initCanvas(){
		setCanvasSettings(15,30,16,16,null,JGColor.blue,null);
		halfWidth=pfWidth()/2;
		margin=16;
	}

	public void initGame(){
		frameRate=35; int frameSkip=2;
		setFrameRate(frameRate,frameSkip);
		defineMedia("Fishing.tbl");
		nextState("Title");
	}
	
	/** Title Screen */
	public void startTitle(){
		noFishCheat=false;
		win="no";
	}
	
	public void doFrameTitle(){
		if (getKey(KeyEnter)) nextState("FirstMode");
	}
	
	// Instructions on Title
	public void paintFrameTitle(){
		drawString("Fishing for Treasure!",halfWidth,titleSpaceAndSize,0,gameFont,JGColor.white);
		drawString("In the first mode, move left and right",halfWidth,lineSpaceAndSize*5,0,textFont,JGColor.white); // Are these magic numbers? How do I get rid them without making a constant for each line?
		drawString("using the arrow keys to avoid the fish.",halfWidth,lineSpaceAndSize*6,0,textFont,JGColor.white);
		drawString("The deeper you get, the bigger the fish",halfWidth,lineSpaceAndSize*7,0,textFont,JGColor.white);
		drawString("are. Hitting a fish will enter the second",halfWidth,lineSpaceAndSize*8,0,textFont,JGColor.white);
		drawString("mode, while hitting a boot will make you",halfWidth,lineSpaceAndSize*9,0,textFont,JGColor.white);
		drawString("lose.",halfWidth,lineSpaceAndSize*10,0,textFont,JGColor.white);
		drawString("In the second mode, the fish will move",halfWidth,lineSpaceAndSize*12,0,textFont,JGColor.white);
		drawString("left and right. Press left and right",halfWidth,lineSpaceAndSize*13,0,textFont,JGColor.white);
		drawString("using the arrow keys to match the fish's",halfWidth,lineSpaceAndSize*14,0,textFont,JGColor.white);
		drawString("movement, and press space rapidly to",halfWidth,lineSpaceAndSize*15,0,textFont,JGColor.white);
		drawString("reel the fish in.",halfWidth,lineSpaceAndSize*16,0,textFont,JGColor.white);
		drawString("Press Enter to Begin",halfWidth,lineSpaceAndSize*19,0,gameFont,JGColor.white);
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
		checkCollision(bootCID+fishCID,playerCID);
		double scaleSpeed = -.002;
		int firstModeExpiry = -3; // Objects expire when off view
		int fishSpeed = -3;
		double scaledFishSpeed = fishSpeed+gameTime*scaleSpeed;
		double fishSpawnRate = Math.pow(gameTime/frameRate, 1.5);
		int bootSpawnRate = 10;
		int bootSpeed = -1;
		double scaledBootSpeed = bootSpeed+gameTime*scaleSpeed;
		if (noFishCheat) removeObjects("fish",fishCID);
		if (fishSpawnRate>=numFishSpawned && !noFishCheat){
			new JGObject("fish",true,random(0,pfWidth()),pfHeight(),fishCID,"fish_u",0,scaledFishSpeed,firstModeExpiry);
			numFishSpawned++;
			if (numFishSpawned%bootSpawnRate==0) new JGObject("boot",true,random(0,pfWidth()),pfHeight(),bootCID,"boot",0,scaledBootSpeed,firstModeExpiry);
		}
		// Cheat
		if (getKey(' ')) noFishCheat=true;
		if (getKey(KeyEsc)) nextState("Title");
		if (getKey(KeyEnter)) nextState("StartSecondMode");
	}
	
	public void paintFrameFirstMode(){
		depthAndWeight=gameTime/2;
		drawString("Current Depth: "+depthAndWeight,halfWidth,lineSpaceAndSize,0,gameFont,JGColor.white);
	}
	
	/** Start Second Game Mode */
	public void startStartSecondMode(){
		removeObjects(null,0);
	}
	
	public void doFrameStartSecondMode(){
		new JGTimer(70, true, "StartSecondMode"){
			public void alarm() {
				nextState("InSecondMode");
			}
		};
	}
	
	public void paintFrameStartSecondMode(){
		drawString("Get ready to reel the fish!", halfWidth, lineSpaceAndSize,0,gameFont,JGColor.white);
	}
	
	/** In Second Game Mode */
	public void startInSecondMode(){
		new JGObject("reel",true,halfWidth-4,titleSpaceAndSize*2,playerCID,"reel_c");
		new HookedFish();
		gameTime=0;
	}
	
	public void doFrameInSecondMode(){
		gameTime++;
		directionalTimer=gameTime/frameRate;
		moveObjects();
		if (getKey(KeyEsc)) nextState("Title");
		// Cheat
		if (getKey(KeyEnter)){
			win="yes";
			nextState("End");
		}
	}
	
	public void paintFrameInSecondMode(){
		drawString("Reel!",halfWidth, margin,0,gameFont,JGColor.white);
	}
	
	/** End Mode */
	public void startEnd(){
	}
	
	public void doFrameEnd(){
		if (getKey(KeyEsc)||getKey(KeyEnter)) nextState("Title");
	}
	
	public void paintFrameEnd(){
		if (win.equals("yes")) drawString("You caught a "+depthAndWeight+" pound flounder!",halfWidth,titleSpaceAndSize*2,0,textFont,JGColor.white);
		else if (win.equals("no")) drawString("You have lost the fish!",halfWidth,titleSpaceAndSize*2,0,textFont,JGColor.white);
		else if (win.equals("boot")) drawString("You have hit a boot!",halfWidth,titleSpaceAndSize*3,0,textFont,JGColor.white);
		drawString("Press Enter to go back to main menu.",halfWidth,titleSpaceAndSize*4,0,textFont,JGColor.white);
	}
	
	/** Player Object */
	class Hook extends JGObject{
		Hook(){
			super("hook",true,halfWidth-margin,titleSpaceAndSize*2,playerCID,"hook");
		}
		
		public void move(){
			double hookSpeed = 2;
			if (getKey(KeyLeft) && !getKey(KeyRight)) xspeed=-hookSpeed;
			else if (getKey(KeyRight) && !getKey(KeyLeft)) xspeed=hookSpeed;
			else xspeed=0;
			if ((x>pfWidth()-margin && xspeed>0)||(x<margin && xspeed<0)) xspeed=0;
			// Cheat
			double cheatSpeed = xspeed*2;
			if (getKey(KeyDown)) xspeed=cheatSpeed;
		}
		
		public void hit(JGObject obj){
			if (obj.colid==fishCID){
				nextState("StartSecondMode");
			}
			else if (obj.colid==bootCID){
				win="boot";
				nextState("End");
			}
		}
	}
	
	/** Hooked Fish Object */
	class HookedFish extends JGObject{
		HookedFish(){
			super("hookedFish",true,halfWidth,pfHeight()-titleSpaceAndSize*4,fishCID,"fish_r");
			xspeed=random(-1,1);
		}
		
		public void move(){
			double hookedSpeed = -.02; double reelSpeed = -6; double escapeSpeed = 1+depthAndWeight*.003;
			int turnTimer1 = (int)(Math.floor(random(4,6))); // Randomize when the fish changes direction
			int turnTimer2 = (int)(Math.floor(random(7,9)));
			if (getKey(KeyShift)) yspeed=-6; // Cheat
			else if ((xspeed<0 && getKey(KeyLeft) && !getKey(KeyRight))||(xspeed>0 && getKey(KeyRight) && !getKey(KeyLeft))){
				yspeed=hookedSpeed; // Speed for pressing left and right matching fish orientation
				if (getKey(' ')) {
					clearKey(' ');
					yspeed=reelSpeed; // Speed for pressing space rapidly
				}
			}
			else yspeed=escapeSpeed; // Speed for incorrect reeling
			if ((xspeed<0 && x<margin)||(xspeed>0 && x>pfWidth()-margin)||(directionalTimer%turnTimer1==0)||(directionalTimer%turnTimer2==0)) xspeed=-xspeed;
			if (xspeed<0) setGraphic("fish_l"); else setGraphic("fish_r");
			if (y<margin){
				win="yes";
				nextState("End");
			}
			else if (y>pfHeight()+margin) nextState("End");
		}
	}
	
	// Cheat to skip levels
	public void nextState(String stateName){
		clearKey(KeyEnter);
		removeObjects(null,0);
		setGameState(stateName);
	}
}