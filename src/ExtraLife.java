import javafx.scene.paint.Color;
public class ExtraLife extends PowerUp{
	private double speed;
	
	public ExtraLife(double d, double e){
		super(d, e);
		this.type = "ExtraLife";
		this.setFill(Color.HOTPINK);
	}
}
