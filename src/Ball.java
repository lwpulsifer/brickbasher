

import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * I think this class is well designed because it uses the principles of encapsulation and polymorphism that 
 * we've been discussing in class. It has no getters or setters whatsoever, instead using polymorphism. The Ball
 *  object has complete control over all of its own data and functions, and it's very much an active class
 *  -- check out the bounce methods in which it is passed a Rectangle or a Paddle and then cheanges its own 
 *  speed to fulfill the bounce function. It also uses polymorphism in this methods by adapting its behavior
 *  to match the type of the object that is passed to it. I want slightly different kinds of bounces to happen
 *  when the ball bounces off of the paddle, when it bounces off of the bricks, and when it bounces off the walls,
 *  and this does that without any clunky if statements. Finally, all of the methods and variables are concisely 
 *  named and well-defined, such that they're almost as legible to read as pseudocode.
 *
 */
public class Ball extends Circle{
	private int startangle;
	private double xspeed;
	private double yspeed;
	private Random rand;
	
	public Ball(double xpos, double ypos, double speed){
		this.setCenterX(xpos);
		this.setCenterY(ypos);
		this.setFill(Color.RED);
		this.setRadius(8);
		rand = new Random();
		do {
			startangle = rand.nextInt(30) + 250;
		} while (startangle == 270);
		xspeed = speed * Math.cos(Math.toRadians(startangle));
		yspeed = speed * Math.sin(Math.toRadians(startangle));
	}
	//Called if 
	public void moveWithPaddle(char rightorleft, int paddlespeed){
		if (rightorleft == 'r') {this.setCenterX(this.getCenterX() + paddlespeed);}
		if (rightorleft == 'l') {this.setCenterX(this.getCenterX() - paddlespeed);}
	}
	/**
	 * Returns a line which is a vector of the ball's speed 
	 * Used to create the little laser pointer at the beginning of the game
	 * which tells the player where the ball will be launched towards
	 */
	public Line createTargetLine(){
		Line targetline = new Line();
		targetline.setStartX(this.getCenterX());
		targetline.setStartY(this.getCenterY());
		targetline.setEndX(this.getCenterX() + 5 * this.xspeed);
		targetline.setEndY(this.getCenterY() + 5 * this.yspeed);
		targetline.setStyle("-fx-stroke: chartreuse;");
		targetline.setStrokeWidth(2.0);
		return targetline;
	}
	//Bounces the ball off of the sides of the window
	public void bounce(int WINDOWSIZE){
		if (this.getCenterX() >= WINDOWSIZE - this.getRadius() / 2){
			this.setCenterX(WINDOWSIZE - this.getRadius() / 2);
			xspeed = -1 * this.xspeed;
		}
		if (this.getCenterX() <= 0 + this.getRadius() / 2){
			this.setCenterX(0 + this.getRadius());
			xspeed = -1 * this.xspeed;
		}
		if (this.getCenterY() <= 0 + this.getRadius() / 2){
			this.setCenterY(0 + this.getRadius());
			yspeed = -1 * this.yspeed;
		}
	}
	//Bounces the ball off of the block if necessary
	public boolean bounce(Rectangle block){
		if (this.getCenterX() + this.getRadius() / 2  >= block.getX() && 
				this.getCenterX() - this.getRadius() / 2 <= block.getX() + block.getWidth()){
			

			if (this.getBoundsInParent().intersects(block.getBoundsInParent())){
				yspeed = this.yspeed * -1;
				this.setCenterY(this.getCenterY() + 2 * this.yspeed);
				return true;
			}
		}
		if (this.getCenterY() + this.getRadius() / 2>= block.getY() && 
				this.getCenterY() - this.getRadius() / 2<= block.getY() + block.getHeight()){
			

			if (block.getBoundsInParent().intersects(this.getBoundsInParent())){
				xspeed = this.xspeed * -1;
				this.setCenterX(this.getCenterX() + 2 * this.xspeed);
				return true;
			}
		}
		return false;
	}
	public boolean bounce(Paddle paddle){
		if (paddle.getX() <= this.getCenterX() + this.getRadius() &&
				paddle.getX() + paddle.getWidth() >= this.getCenterX() - this.getRadius()){
			double ratio = (this.getCenterX() - paddle.getX()) / ((paddle.getX() + paddle.getWidth()) - paddle.getX()); 
			if (paddle.getBoundsInParent().intersects(this.getBoundsInParent())){
				this.xspeed = (ratio - .5) * 3 + this.xspeed;
				this.setCenterX(this.getCenterX() + this.xspeed);
				this.yspeed = this.yspeed * -1;
				return true;
			}
		}
		return false;
	}
	//Updates ball position based on x and y speed
	public void update(){
		this.setCenterX(this.getCenterX() + xspeed);
		this.setCenterY(this.getCenterY() + yspeed);
	}
}
