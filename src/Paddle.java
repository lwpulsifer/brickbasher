

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle{
	private int speed;
	private double lives = 3;
	
	public Paddle(double xpos, double ypos){
		this.setWidth(100);
		this.setHeight(20);
		this.setX(xpos - this.getWidth() / 2); 
		this.setY(ypos - this.getHeight() - 2);
		this.speed = 30;
		this.setFill(Color.BLUE);
	}
	
	public void updatePos(char rightorleft){
		if (rightorleft == 'r') {this.setX(this.getX() + speed);}
		if (rightorleft == 'l') {this.setX(this.getX() - speed);}
	}
	public int getSpeed(){
		return this.speed;
	}
	public void addLife(){
		lives++;
	}
	public void subtractLife(){
		lives--;
	}
	public void addHalfLife(){
		lives += .5;
	}
	public void subHalfLife(){
		lives -= .5;
	}
	public double getLives(){
		return this.lives;
	}
	public void setLives(int lives){
		this.lives = lives;
	}
	public void setColor(Color color){
		this.setFill(color);
	}
	
}
