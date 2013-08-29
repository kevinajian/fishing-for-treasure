package game;
import jgame.*;
import jgame.platform.*;

public class Fishing extends JGEngine{
	public static void main(String[]args){ new Fishing(new JGPoint(240,480));}
	
	/** Application constructor. */
	public Fishing(JGPoint size){initEngine(size.x,size.y);}
	
	/** Applet constructor. */
	public Fishing() { initEngineApplet(); }

	public void initCanvas(){setCanvasSettings(15,30,16,16,null,JGColor.blue,null);}

	public void initGame(){
		setFrameRate(35,2);
		defineImage("hook","-",0,"ball20-red.gif","-");
		
	}


	Class Fish extends JGObject{
		Fish(){
			super("hook",true,5,10,1,"ball20-red.gif","-")
		}
	}
}