	/* 
			I saw that my three JGObjects, Hook, Fish, and HookedFish all had interactions with the playfield boundaries, 
		so I implemented a checkBoundary method to eliminate the duplicate code within the three.
			There were also several times in different JGObjects when I needed to know if the user input was either left 
		or right and not both, so I created a checkLeftOrRight method to check for that, eliminating duplicity as well.
			I also noticed that the HookedFish class was similar to Fish but more specific, so I used inheritance and made 
		Fish a superclass while HookedFish extended it, thus removing the identical code that HookedFish could simply 
		take from the Fish superclass.
		Finally, I had two cheats, one where the escape key would return the user to the title screen from any game mode,
		while the enter key would allow the user to automatically win the current mode they were in. I found the implementation
		for these two methods very similar, so I consolidated them into one general method that would accomodate both cheats
		while not losing any functionality.
	*/

	/** Player Object */
	class Hook extends JGObject{
		private final static int hookHeight = 40;
		private double hookSpeed = 3;
		private double cheatSpeedScale = 2;
		Hook(){
			super("hook",true,hookWidth,hookHeight,playerCID,"hook");
		}
		
		public void move(){
			if (!checkLeftOrRight||checkBoundary(getObject("Hook"))) xspeed=0;
			else if (getKey(KeyLeft)) xspeed=-hookSpeed;
			else if (getKey(KeyRight)) xspeed=hookSpeed;
			// Cheats
			double cheatSpeed = xspeed*cheatSpeedScale;
			if (getKey(KeyDown)) xspeed=cheatSpeed;
		}
		
		public void hit(JGObject obj){
			if (obj.colid==bootCID){
				win="boot";
				setGameState("End");
			}
			else setGameState("StartSecondMode");
		}
	}

	/** Fish Object */
	class Fish extends JGObject{
		private static String myObjName = "fish";
		private static String myGraphic="fish_r";
		private static int myExpiry=-3;
		Fish(String objName, double xpos, double ypos, double xspeed, double yspeed){
			super(myObjName,true,xpos,ypos,fishCID,myGraphic,xspeed,yspeed,myExpiry);
		}
		
		public void move(){
			if (checkBoundary(getObject("Fish")) xspeed = -xspeed;
			if (xspeed>0) setGraphic("fish_r"); else setGraphic("fish_l");
		}
	}
	
	/** Hooked Fish Object */
	class HookedFish extends Fish{
		private static double myXpos=halfWidth; 
		private static double myYpos=hookedFishHeight; 
		private static double myXspeed=random(-rangeOfSpeed,rangeOfSpeed); 
		private static double myYspeed=escapeSpeed;
		private static String myObjName = "hookedFish";
		
		HookedFish(){
			super(myObjName,myXpos,myYpos,myXspeed,myYspeed);
		}
		
		public void move(){			
			int turnTimer = (int)(Math.floor(random(rangeOfTurnMin,rangeOfTurnMax))); // Randomize when the fish changes direction
			if ((xspeed<0 && getKey(KeyLeft))||(xspeed>0 && getKey(KeyRight))&&checkLeftOrRight()){
				yspeed=hookedSpeed; // Speed for pressing left and right matching fish orientation
				if (getKey(' ')) yspeed=reelSpeed; // Speed for pressing space rapidly
			}
			else yspeed=escapeSpeed; // Speed for incorrect reeling
			super.move(); // Use superclass move()
			if ((directionalTimer%turnTimerOne==0)||(directionalTimer%turnTimerTwo==0)) xspeed = -xspeed;
			if (y<margin){
				win="yes";
				setGameState("End");
			}
			else if (y>pfHeight()) setGameState("End");
			// Cheat
			if (getKey(KeyShift)) yspeed= reelSpeed;
		}
	}

	public boolean checkBoundary(JGObject obj){}
		return ((obj.x<0 && obj.xspeed<0)||(obj.x>pfWidth() && obj.xspeed>0);
	}

	public boolean checkLeftOrRight(){
		return (getKey(KeyLeft) && !getKey(KeyRight))||(getKey(KeyRight) && !getKey(KeyLeft));
	}

	// Cheats for user to return to title screen and skip levels
	public void checkKeyAndSetState(String stateName, keyObject key){
		clearKey(key);
		removeObjects(null,0);
		if (key==KeyEsc) setGameState("Title");
		setGameState(stateName);
	}
}