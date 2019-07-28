

import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Block extends Rectangle{
	private int health = 1;
	private Color paint = Color.ORANGE;
	private int breakval = 100;
	private int speed = 0;
	private String type;
	private Random rand;
	private PowerUp powerup;
	
	public Block(double xcoord, double ycoord, String type){
		this.setX(xcoord);
		this.setY(ycoord);
		this.setWidth(35);
		this.setHeight(20);
		this.type = type;
		rand = new Random();
		if (type.equals("multihit2")){
			health = 2;
			paint = Color.FIREBRICK;
			breakval *= 2;
		}
		else if (type.equals("multihit3")){
			health = 3;
			paint = Color.DARKSLATEGREY;
			breakval *= 3;
		}
		else if (type.equals("impenetrable")){
			health = Integer.MAX_VALUE;
			paint = Color.INDIGO;
			this.setWidth(40);
		}
		else if (type.equals("moving")){
			paint = Color.FORESTGREEN;
			speed = rand.nextInt(2);
			if (speed == 0){
				speed = -1;
			}
			health = 2;
		}
		else if (type.equals("impenetrablemover")){
			paint = Color.FORESTGREEN;
			speed = rand.nextInt(2);
			if (speed == 0){
				speed = -1;
			}
			health = Integer.MAX_VALUE;
		}
		this.setFill(paint);
		
	}
	public void updateColor(){
		if (health == 1){
			paint = Color.ORANGE;
		}
		else if (health == 2){
			paint = Color.FIREBRICK;
		}
		this.setFill(paint);
	}
	public void subtractHealth(){
		health--;
	}
	public int getBreakValue(){
		return breakval;
	}
	public String getKind(){
		return type;
	}
	public int getHealth() {
		return health;
	}
	public void reverse(){
		speed *= -1;
	}
	public int getSpeed(){
		return speed;
	}
	public void updateMover(){
		this.setX(this.getX() + speed);
		if (powerup.Connected()){
			this.powerup.setCenterX(this.getX());
		}
	}
	public void checkWalls(int windowsize){
		if (this.getX() <= 0 || this.getX() + this.getWidth() >= windowsize){
			this.reverse();
			this.updateMover();
		}
	}
	public void checkOtherBlock(Block b){
		if (this.getBoundsInParent().intersects(b.getBoundsInParent()) && this.getSpeed() * b.getSpeed() < 0){
			this.reverse();
			b.reverse();
		}
		else if (this.getBoundsInParent().intersects(b.getBoundsInParent())){
			this.reverse();
		}
	}
	public void setPowerUp(PowerUp powerup){
		this.powerup = powerup;
	}
	public PowerUp getPowerUp(){
		return this.powerup;
	}
}
