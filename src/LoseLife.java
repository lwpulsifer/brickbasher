import javafx.scene.paint.Color;

public class LoseLife extends PowerUp{
	public LoseLife(double xpos, double ypos){
		super(xpos, ypos);
		this.type = "LoseLife";
		this.setFill(Color.CRIMSON);
	}
}
