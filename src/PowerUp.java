import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PowerUp extends Circle{
	private double speed;
	private boolean connected;
	String type;
	public PowerUp(double xpos, double ypos){
		this.setCenterX(xpos);
		this.setCenterY(ypos);
		this.setRadius(8);
		this.speed = .5;
		this.setFill(Color.WHITE);
		connected = true;
		type = "Blank";
	}
	public void updatePosition(){
		this.setCenterY(this.getCenterY() + speed);
	}
	public boolean Connected(){
		return connected;
	}
	public void Disconnect(){
		connected = false;
	}
	public String getKind(){
		return type;
	}
	
}
