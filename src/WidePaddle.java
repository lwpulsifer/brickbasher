import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
public class WidePaddle extends PowerUp{
	private double speed;
	
	public WidePaddle(double xpos, double ypos){
		super(xpos, ypos);
		this.type = "WidePaddle";
		this.setFill(Color.GREEN);
	}
}