package game;
import jgame.*;
import jgame.platform.*;

public class Fishing extends JGEngine{
	public static void main(String[]args){new Fishing(new JGPoint(240,480));}
	
	/** Application constructor. */
	public Fishing(JGPoint size){initEngine(size.x,size.y);}
	
	/** Applet constructor. */
	public Fishing(){initEngineApplet();}

	public void initCanvas(){setCanvasSettings(15,30,16,16,null,JGColor.blue,null);}

	public void initGame(){
		setFrameRate(35,2);
		//defineMedia("Fishing.tbl");
		//defineImage("hook","-",0,"ball20-red.gif","-");
		new Hook();
		for (int i=0; i<10; i++)
			new Fish(Fishing.this.random(0,pfWidth()),Fishing.this.random(0, pfHeight()));
	}
	
	public void doFrame(){
		moveObjects();
		//checkCollision(1,2);
	}
	
	public void paintFrame(){
	}
	
	class Hook extends JGObject{
		Hook(){
			super("hook",true,pfWidth()/2,10,1,null);
			yspeed=2;
		}
		
		public void paint(){
			setColor(JGColor.black);
			drawOval(x,y,16,16,true,true);
		}

		public void move(){
			if (getKey(KeyLeft) && getKey(KeyRight)) xspeed=0;
			else if (getKey(KeyLeft)) xspeed=-2;
			else if (getKey(KeyRight)) xspeed=2;
			if ((x>pfWidth()-8 && xspeed>0)||(x<8 && xspeed<0)) xspeed=0;
			if (y>pfHeight()-16 && yspeed>0) 	yspeed=0;
		}
	}
	
	class Fish extends JGObject{
		Fish(double x, double y){
			super("fish",true,x,y,2,null);
			x=random(-1,1);
			if (x>0) xspeed=y*.01;
			else if (x<0) xspeed=y*-.01;
		}
		
		public void paint(){
			setColor(JGColor.red);
			drawOval(x,y,y*.07,y*.07,true,true);
		}
		
		public void move(){
			if ((x > pfWidth()-8 && xspeed>0) || x <8 && xspeed<0) xspeed=-xspeed;	
		}
		
	}
}