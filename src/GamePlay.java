

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;



import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GamePlay extends Application {
	private static final int WINDOWSIZE = 400;
	private static final String TITLE = "--------------    BrickBasher    ------------------";
	private static final Paint BACKGROUND = Color.DEEPSKYBLUE;
	private static final int FRAMES_PER_SECOND = 60;
	private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
	private static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND; 
	private static final double BALL_MULTIPLIER = 1.13;
	private static final String SPLASH_SCREEN = "Splash.png";
	
	private Stage primaryStage;
	private Scene myScene;
	private Paddle paddle;
	private Ball ball;
	private Group myRoot;
	private ArrayList<Block> blocklist;
	private int score;
	private int level;
	private Text scorefield;
	private Text levelfield;
	private Text livesfield;
	private Text catchtext;
	private ArrayList<PowerUp> powerups;
	private int currentscore;
	private boolean isbig;
	private boolean pause;
	private boolean catching;
	private Line targetline;
	private ImagePattern iview; 
	private boolean splash = true;
	private boolean gameover;
	private int ballspeed = 5;
	private boolean connected = true;
	
	@Override
	public void start(Stage stage) {
		paddle = new Paddle(WINDOWSIZE / 2, WINDOWSIZE);
		powerups = new ArrayList<PowerUp>();
		// attach scene to the stage and display it
		myScene = setUpLevel(WINDOWSIZE, WINDOWSIZE, BACKGROUND, 1, 0, ballspeed);
		primaryStage = stage;
		primaryStage.setScene(myScene);
		primaryStage.setTitle(TITLE);
		primaryStage.show();

		animate();
	}
	//Set Animation to timeline
	private void animate(){
		// attach "game loop" to timeline to play it
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
		Timeline animation = new Timeline();
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.getKeyFrames().add(frame);
		animation.play();
	}

	// Create the game's "scene": what shapes will be in the game and their
	// starting properties
	private Scene setUpLevel(int width, int height, Paint background, int level, int score, double ballspeed) {
		iview = new ImagePattern(new Image(getClass().getClassLoader().getResourceAsStream(SPLASH_SCREEN)));
		
		catching = false;
		catchtext = getText(WINDOWSIZE/2 - 150, WINDOWSIZE/2 + 20, "Catching Mode Activated \n "
				+ "(Your score is a multiple of 1000)");
		catchtext.setTextAlignment(TextAlignment.CENTER);
		targetline = new Line();
		this.score = score;
		currentscore = score;
		this.level = level;
		paddle.setWidth(200 / (level + 1));
		isbig = false;
		
		if (level >= 4){
			Scene newscene = youWin(WINDOWSIZE, WINDOWSIZE, Color.RED);	
			newscene.setOnKeyPressed(e -> getSpace(e.getCode()));
			return newscene;
		}
		
		//Initialize list of blocks
		blocklist = new ArrayList<Block>();
		// create one top level collection to organize the things in the scene
		myRoot = new Group();
		// create a place to see the shapes
		Scene scene = new Scene(myRoot, width, height, background);
		File file = new File("blockfiles/level" + level + ".txt");
		Scanner scan;
		try {
			scan = new Scanner(file);
			String line = new String("");
			while (scan.hasNextLine()){
				line = scan.nextLine();
				Scanner in = new Scanner(line);
				double xpos = in.nextDouble();
				double ypos = in.nextDouble();
				String type = in.next();
				Block addblock = new Block(xpos, ypos, type);
				initPowerUp(addblock);
				myRoot.getChildren().add(addblock);
				blocklist.add(addblock);
				in.close();
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (splash){
			scene.setFill(iview);
		}
		ball = new Ball(width / 2, height - (paddle.getHeight() + 17), ballspeed);
		myRoot.getChildren().add(paddle);
		myRoot.getChildren().add(ball);
		scorefield = getText(10,18,"Score: " + score);
		levelfield = getText(160, 18, "Level 1");
		livesfield = getText(317, 18, ("Lives: " + ((int) paddle.getLives())));
		myRoot.getChildren().add(scorefield);
		myRoot.getChildren().add(levelfield);
		myRoot.getChildren().add(livesfield);
		scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
		
		
		return scene;
	}

	/**
	 * Randomly decides whether to add a powerup to a block and then does so
	 */
	private void initPowerUp(Block block){
		Random rand = new Random();
		int randomval = rand.nextInt(9);
		if (randomval <= (3 - level)){
			randomval = rand.nextInt(level);
			if (randomval == 0){
				block.setPowerUp(new ExtraLife(block.getX() + block.getWidth() / 2, block.getY() + 
						block.getHeight() / 2));
			}
			else if (randomval == 1){
				block.setPowerUp(new WidePaddle(block.getX() + block.getWidth() / 2, block.getY() + 
						block.getHeight() / 2));
			}
			else if (randomval == 2){
				block.setPowerUp(new LoseLife(block.getX() + block.getWidth() / 2, block.getY() + 
						block.getHeight() / 2));
				FillTransition ft = new FillTransition(Duration.millis(600), block.getPowerUp(), Color.PURPLE, Color.WHITE);
			    ft.setCycleCount(40000);
			    ft.setAutoReverse(false);
			 
			    ft.play();
			}
			powerups.add(block.getPowerUp());
			
		}
		else {
			block.setPowerUp(new PowerUp(0,0));
		}
	}
	//Set text variables
	private Text getText(double xpos, double ypos, String val){
		Text text = new Text(xpos, ypos, val);
		text.setFont(new Font("Helvetica", 20));
		text.setFill(Color.WHITE);
		return text;
	}
	
	//Updates the text fields to reflect new score, level, and number of lives
	private void updateLabels(){
		scorefield.setText("Score: " + score);
		levelfield.setText("Level " + level);
		livesfield.setText("Lives: " + ((int) paddle.getLives()));
	}
	
	//Creates and displays rudimentary gameOver Screen
	private Scene gameOver(int width, int height, Paint background){
		HBox hbox = new HBox();
		Scene scene = new Scene(hbox, width, height, background);
		Label label = new Label("GAME OVER");
		label.setStyle("-fx-font-size: 40px;");
		hbox.getChildren().add(label);
		hbox.setAlignment(Pos.CENTER);
		scene.setOnKeyPressed(e -> getSpace(e.getCode()));
		
		return scene;
	}
	
	//Creates and displays rudimentary youWin Screen
	private Scene youWin(int width, int height, Paint background){
		HBox hbox = new HBox();
		Scene scene = new Scene(hbox, width, height, background);
		scene.setFill(Color.ROYALBLUE);
		Label label = new Label("YOU WIN!!!");
		label.setStyle("-fx-font-size: 40px;");
		label.setTextFill(Color.GREEN);
		hbox.getChildren().add(label);
		hbox.setAlignment(Pos.CENTER);
		
		return scene;
	}
	//Checks to see if the paddle is large and whether it should be large
	//due to a powerup
	private void checkBigPaddle(){
		if (score >= currentscore + 500){
			paddle.setWidth(200 / (level + 1));
			paddle.setFill(Color.BLUE);
			isbig = false;
		}
	}
	private void step(double elapsedTime) {
		
		checkBigPaddle();
		if (score % 1000 == 0 && score != 0){
			catching = true;
			if (!myRoot.getChildren().contains(catchtext)){
				myRoot.getChildren().add(catchtext);
			}
		}
		else {
			catching = false;
			myRoot.getChildren().remove(catchtext);
			
		}
		
		if (pause){
			return;
		}
		
		if (paddle.getLives() <= 0 && !gameover){
			gameover = true;
			myScene = gameOver(WINDOWSIZE, WINDOWSIZE, Color.RED);	
			primaryStage.setScene(myScene);
			primaryStage.show();
//			System.out.println("HI");
//			primaryStage.setScene(myScene);
//			primaryStage.show();
//			System.out.println("JENNY");
			//animation.stop();
			//restart();
		}
		for (PowerUp p : powerups){
			if (p != null){	
				if (!p.Connected()){
					p.updatePosition();
					//Gets effect of a powerup, then causes loop to break if an effect occurs
					if (getEffect(p)){
						break;
					}
				}
			}
		}
		if (hasConnect()){
			ball.setCenterX(paddle.getX() + paddle.getWidth() / 2);
			myRoot.getChildren().remove(targetline);
			createLine();
		}
		if (level == 3) {
			moveBlocks();	
		}
		if (!hasConnect()){
			myRoot.getChildren().remove(targetline);
			if (ball.getCenterY() >= WINDOWSIZE - ball.getRadius() / 2){
				subtractReset();
			}
			if (ball.bounce(paddle) && catching){
				connect();
			}
			ball.bounce(WINDOWSIZE);
			
			//Check block intersections
			for (Block block : blocklist){
				if (ball.bounce(block)){
					block.subtractHealth();
					if (block.getHealth() == 0){
						if (block.getPowerUp() != null && !block.getPowerUp().getKind().equals("Blank")){
							block.getPowerUp().Disconnect();
							if (!myRoot.getChildren().contains(block.getPowerUp())){
								myRoot.getChildren().add(block.getPowerUp());
								powerups.add(block.getPowerUp());
							}
						}
						removeBlock(block);
						score += block.getBreakValue();
					}
					block.updateColor();
					break;
				}
			}
			if (!areBlocks()){
				moveToLevel(++level);
			}
			
			ball.update();
			
		}//ball.getCenterY() >= WINDOWSIZE - ball.getRadius() / 2 ||
		updateLabels();        
	}
	private void createLine(){

		targetline = ball.createTargetLine();
		if (!myRoot.getChildren().contains(targetline)){
			myRoot.getChildren().add(targetline);
		}
	
	}
	//Checks to see if the powerup intersects with the paddle, and if 
	//so performs the relevant action
	//returns true if an action has been performed
	private boolean getEffect(PowerUp p){
		if (p != null) {
			if (p.getKind().equals("LoseLife")
					&& p.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
				paddle.subHalfLife();
				myRoot.getChildren().remove(p);
				powerups.remove(p);
				return true;
			} else if (p.getKind().equals("ExtraLife")
					&& p.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
				paddle.addHalfLife();
				myRoot.getChildren().remove(p);
				powerups.remove(p);
				return true;
			} else if (p.getKind().equals("WidePaddle")
					&& p.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
				if (!isbig) {
					paddle.setWidth(200);
					paddle.setColor(Color.AQUAMARINE);
				}
				currentscore = score;
				isbig = true;
				myRoot.getChildren().remove(p);
				powerups.remove(p);
				return true;
			} 
		}
		return false;
	}
	//Checks for moving blocks and updates their position, including collisions
	private void moveBlocks(){
		for (Block block : blocklist) {
			if (block.getKind().equals("moving")) {
				block.checkWalls(WINDOWSIZE);
				for (Block otherblock : blocklist) {
					if (block.getY() == otherblock.getY() && block != otherblock) {
						block.checkOtherBlock(otherblock);
					}
					
				}
				block.updateMover();
			} else {
				continue;
			}
		} 
	}
	//Takes away a life and resets the paddle and ball
	private void subtractReset(){

		Iterator<PowerUp> iterator = powerups.iterator();
		while (iterator.hasNext()){
			PowerUp temp = iterator.next();
			if (!temp.Connected()){
				iterator.remove();
				myRoot.getChildren().remove(temp);
			}
		}
		paddle.subtractLife();
		//if (paddle.getLives() != 0){
		paddle.setX(WINDOWSIZE / 2 - paddle.getWidth() / 2);
		myRoot.getChildren().remove(ball);
		ball = new Ball(WINDOWSIZE / 2, WINDOWSIZE - (paddle.getHeight() + 17), ballspeed);
		connect();
		myRoot.getChildren().add(ball);
		//myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
		//}
	}
	//Returns true if any blocks (other than impenetrable blocks) are left
	//and false if no penetrable blocks are left
	private boolean areBlocks(){
		if (blocklist.size() == 0){
			return false;
		}
		for (int i = 0; i < blocklist.size(); i++){
			if (!blocklist.get(i).getKind().equals("impenetrable")){
				return true;
			}
		}
		return false;
	}
	//Removes the block
	private void removeBlock(Block block){
		blocklist.remove(block);
		myRoot.getChildren().remove(block);
	}
	//moves to the indicated level
	//by setting correct scene and displaying that scene on the stage
	private void moveToLevel(int lvl){
		ballspeed *= BALL_MULTIPLIER;
		myScene = setUpLevel(WINDOWSIZE, WINDOWSIZE, BACKGROUND, lvl, score, ballspeed);
		primaryStage.setScene(myScene);
		primaryStage.show();
	}
	//Allows the ball to move
	//By disconnecting it from the paddle
	private void disconnect(){
		connected = false;
	}
	//Sets the connected variable to true
	//"Connects" the ball to the paddle
	private void connect(){
		connected = true;
	}
	//returns whether the ball is connected to the paddle or not.
	private boolean hasConnect(){
		return connected;
	}
	private void handleKeyInput(KeyCode code) {
		if (!pause) {
			if (code == KeyCode.RIGHT && !(paddle.getX() + paddle.getWidth() >= WINDOWSIZE)) {
				paddle.updatePos('r');
				if (hasConnect()) {
					ball.moveWithPaddle('r', paddle.getSpeed());
				}
			}
			if (code == KeyCode.LEFT && !(paddle.getX() <= 0)) {
				paddle.updatePos('l');
				if (hasConnect()) {
					ball.moveWithPaddle('l', paddle.getSpeed());
				}
			}
			if (code == KeyCode.SPACE) {
				disconnect();
				splash = false;
				myScene.setFill(BACKGROUND);
				ball.update();
			}
			if (code == KeyCode.L) {
				paddle.addLife();
			}
			if (code == KeyCode.K) {
				paddle.subtractLife();
			}
			if (code == KeyCode.DIGIT1) {
				restart();
			}
			if (code == KeyCode.DIGIT2) {
				moveToLevel(2);
			}
			if (code == KeyCode.DIGIT3) {
				moveToLevel(3);
			}
		}
		if (code == KeyCode.P) {
			pause = !pause;
		} 
	}
	private void getSpace(KeyCode code){
		if (code == KeyCode.SPACE){
			restart(); 
		}
	}
	private void restart(){
		connect();
		paddle = new Paddle(WINDOWSIZE / 2, WINDOWSIZE);
		myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
		myScene = setUpLevel(WINDOWSIZE, WINDOWSIZE, BACKGROUND, 1, 0, 5);
		gameover = false;
		primaryStage.setScene(myScene);
		primaryStage.show(); 
	}
	

	private static void main(String[] args) {
		launch(args);
	}
}
