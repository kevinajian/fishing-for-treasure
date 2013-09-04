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
	private boolean noFishAndBootCheat = false;
	// Initializing Magic Numbers
	private double frameRate = 35;
	private double frameSkip = 2;
	private static int displayWidth = 240;
	private static int displayHeight = 480;
	private int refreshBackground = 240;
	private static int canvasWidth = 15;
	private static int canvasHeight = 30;
	private static int spriteSize = 16;
	private int half = 2;
	private double halfWidth;
	private double margin = 16;
	private int titleSpaceAndSize = 20;
	private int lineSpaceAndSize = 13;
	private int playerCID = 1;
	private int bootCID = 3;
	private int fishCID = 4;
	private int timer = 70;
	private final int secondLine = 2;
	private final int thirdLine = 3;
	private final int fourthLine = 4;
	// First Mode Magic Numbers
	private int spawnHeightBoundary = 200;
	private double scaleSpeed = -.002;
	private int firstModeExpiry = -3; // Objects expire when off view
	private double yFishSpeed = -2.5;
	private double fishSpawnRatePower = 1.1;
	private int bootSpawnRate = 4;
	private double bootSpeed = -1.5;
	private int scaleGameTime = 2;
	// Second Mode Magic Numbers
	private int shiftReelYPosition = 4;
	// End Mode Magic Numbers
	private int scoreMargin = 3;
	private int leftWinPhraseScale = 5;
	private int rightWinPhraseScale = 4;
	private double leftWinPhraseOffset = displayWidth/leftWinPhraseScale;
	private double rightWinPhraseOffset = rightWinPhraseScale*displayWidth/leftWinPhraseScale;
	
	
	JGFont gameFont = new JGFont("Arial",0,titleSpaceAndSize);
	JGFont textFont = new JGFont("Arial",0,lineSpaceAndSize);	

	public static void main(String[]args){new Fishing(new JGPoint(displayWidth,displayHeight));}
	
	/** Application constructor. */
	public Fishing(JGPoint size){initEngine(size.x,size.y);}
	
	public void initCanvas(){
		setCanvasSettings(canvasWidth,canvasHeight,spriteSize,spriteSize,null,JGColor.blue,null);
		halfWidth=pfWidth()/half;
		margin=16;
	}

	public void initGame(){
		setFrameRate(frameRate,frameSkip);
		defineMedia("Fishing.tbl");
		setBGImage("fixedBackground");
		nextState("Title");
	}
	
	/** Title Screen */
	public void startTitle(){
		new JGObject("titleBackground",true,0,0,0,"titleBackground");
		noFishAndBootCheat=false;
		win="no";
	}
	
	public void doFrameTitle(){
		if (getKey(KeyEnter)) nextState("FirstMode");
	}
	
	/** First Game Mode */
	public void startFirstMode(){
		new Hook();
		new aBackground(0,0);
		gameTime=0;
		numFishSpawned=0;
	}
	
	public void doFrameFirstMode(){
		gameTime++;
		moveObjects();
		checkCollision(bootCID+fishCID,playerCID);
		double xScaledFishSpeed = yFishSpeed+gameTime*scaleSpeed;
		double fishSpawnRate = Math.pow(gameTime/frameRate, fishSpawnRatePower);
		double scaledBootSpeed = bootSpeed+gameTime*scaleSpeed;
		if (gameTime%refreshBackground==0) new aBackground(0,pfHeight()); // Create new scrolling background
		if (fishSpawnRate>=numFishSpawned){ // Randomly spawn fish from the left and right, boots from the bottom
			numFishSpawned++;
			if (!noFishAndBootCheat){
				new Fish(0,random(spawnHeightBoundary,pfHeight()),"fish_l",-xScaledFishSpeed,yFishSpeed,firstModeExpiry);
				new Fish(pfWidth(),random(spawnHeightBoundary,pfHeight()),"fish_r",-xScaledFishSpeed,yFishSpeed,firstModeExpiry);
				if (numFishSpawned%bootSpawnRate==0) new JGObject("boot",true,random(0,pfWidth()),pfHeight(),bootCID,"boot",0,scaledBootSpeed,firstModeExpiry);
			}
		}
		// Cheats
		if (noFishAndBootCheat) {removeObjects("fish",fishCID); removeObjects("boot",bootCID);}
		if (getKey(' ')) {
			noFishAndBootCheat=!noFishAndBootCheat;
			clearKey(' '); // Prevent it from toggling multiple times from one press
		}
		checkEscape();
		if (getKey(KeyEnter)) nextState("StartSecondMode");
	}
	
	public void paintFrameFirstMode(){
		depthAndWeight=gameTime/scaleGameTime;
		drawString("Current Depth: "+depthAndWeight,halfWidth,lineSpaceAndSize,0,gameFont,JGColor.white);
	}
	
	/** Start Second Game Mode */
	public void startStartSecondMode(){
		new JGObject("reelBackground",true,0,0,0,"reelBackground");
	}
	
	public void doFrameStartSecondMode(){
		new JGTimer(timer, true, "StartSecondMode"){
			public void alarm() {
				nextState("InSecondMode");
			}
		};
	}

	/** In Second Game Mode */
	public void startInSecondMode(){
		new JGObject("reel",true,halfWidth-shiftReelYPosition,titleSpaceAndSize*secondLine,playerCID,"reel_c");
		new HookedFish();
		new JGObject("fixedBackground",true,0,0,0,"fixedBackground");
		gameTime=0;
	}
	
	public void doFrameInSecondMode(){
		gameTime++;
		directionalTimer=gameTime/frameRate;
		moveObjects();
		// Cheats
		checkEscape();
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
		new JGObject("fixedBackground",true,0,0,0,"fixedBackground");
	}
	
	public void doFrameEnd(){
		if (getKey(KeyEnter)) nextState("Title");
		checkEscape();
	}
	
	public void paintFrameEnd(){
		if (win.equals("yes")) {
			drawString("You caught a: ",leftWinPhraseOffset+scoreMargin,titleSpaceAndSize*secondLine,0,textFont,JGColor.white);
			drawString(""+depthAndWeight,halfWidth,titleSpaceAndSize*secondLine-scoreMargin,0,gameFont,JGColor.white);
			drawString(" pound bass!",rightWinPhraseOffset-scoreMargin,titleSpaceAndSize*secondLine,0,textFont,JGColor.white);
		}
		else if (win.equals("no")) drawString("You lost the fish!",halfWidth,titleSpaceAndSize*secondLine,0,gameFont,JGColor.white);
		else drawString("You hit a boot!",halfWidth,titleSpaceAndSize*secondLine,0,gameFont,JGColor.white);
		drawString("Press Enter to go back to main menu.",halfWidth,titleSpaceAndSize*fourthLine,0,textFont,JGColor.white);
	}
	
	/** Background Object */
	class aBackground extends JGObject{
		private final static int backgroundExpiry = -3;
		private final static double yspeed = -2;
		aBackground(int x, int y){
			super("abackground",true,x,y,0,"background",0,yspeed,backgroundExpiry);
		}
	}
	
	/** Player Object */
	class Hook extends JGObject{
		private double hookSpeed = 3;
		private final static int hookHeight = 40;
		private double cheatSpeedScale = 2;
		Hook(){
			super("hook",true,halfWidth-margin,hookHeight,playerCID,"hook");
		}
		
		public void move(){
			if (getKey(KeyLeft) && !getKey(KeyRight)) xspeed=-hookSpeed;
			else if (getKey(KeyRight) && !getKey(KeyLeft)) xspeed=hookSpeed;
			else xspeed=0;
			if ((x>pfWidth()-margin && xspeed>0)||(x<margin && xspeed<0)) xspeed=0;
			// Cheat
			double cheatSpeed = xspeed*cheatSpeedScale;
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
	
	/** Fish Object */
	class Fish extends JGObject{
		Fish(double xpos, double ypos, String graphic, double xspeed, double yspeed, int expiry){
			super("fish",true,xpos,ypos,fishCID,graphic,xspeed,yspeed,expiry);
		}
		
		public void move(){
			if ((xspeed<=0 && x<=0)||(xspeed>=0 && x>=pfWidth())) xspeed = -xspeed;
			if (xspeed>0) setGraphic("fish_r"); else setGraphic("fish_l");
		}
	}
	
	/** Hooked Fish Object */
	class HookedFish extends JGObject{
		private int rangeOfSpeed = 2;
		private double hookedSpeed = -.02;
		private double reelSpeed = -7;
		private double baseEscapeSpeed = 1;
		private double escapeSpeedScale = .005; 
		private int rangeOfFirstTurnMin = 4; 
		private int rangeOfFirstTurnMax = 6; 
		private int rangeOfSecondTurnMin = 7; 
		private int rangeOfSecondTurnMax = 9;
		
		HookedFish(){
			super("hookedFish",true,halfWidth,pfHeight()-titleSpaceAndSize*fourthLine,fishCID,"fish_r");
			xspeed=random(-rangeOfSpeed,rangeOfSpeed);
		}
		
		public void move(){		
			double escapeSpeed = baseEscapeSpeed+depthAndWeight*escapeSpeedScale;
			final int hookedFishHeight = displayHeight - titleSpaceAndSize*fourthLine;
			int turnTimer1 = (int)(Math.floor(random(rangeOfFirstTurnMin,rangeOfFirstTurnMax))); // Randomize when the fish changes direction
			int turnTimer2 = (int)(Math.floor(random(rangeOfSecondTurnMin,rangeOfSecondTurnMax)));
			if (getKey(KeyShift)) yspeed= reelSpeed; // Cheat
			else if ((xspeed<0 && getKey(KeyLeft) && !getKey(KeyRight))||(xspeed>0 && getKey(KeyRight) && !getKey(KeyLeft))){
				yspeed=hookedSpeed; // Speed for pressing left and right matching fish orientation
				if (getKey(' ')) {
					clearKey(' ');
					yspeed=reelSpeed; // Speed for pressing space rapidly
				}
			}
			else yspeed=escapeSpeed; // Speed for incorrect reeling
			if ((xspeed<0 && x<margin)||(xspeed>0 && x>pfWidth()-margin)||(directionalTimer%turnTimer1==0)||(directionalTimer%turnTimer2==0)) xspeed = -xspeed;
			if (xspeed>0) setGraphic("fish_r"); else setGraphic("fish_l");
			if (y<margin*thirdLine){
				win="yes";
				nextState("End");
			}
			else if (y>pfHeight()+margin) nextState("End");
		}
	}
	
	public void checkEscape(){
		if (getKey(KeyEsc)){
			removeObjects(null,0);
			setGameState("Title");
		}
	}
	
	// Cheat
	public void nextState(String stateName){
		clearKey(KeyEnter); // Prevent it from changing states multiple times from one press
		removeObjects(null,0);
		setGameState(stateName);
	}
}