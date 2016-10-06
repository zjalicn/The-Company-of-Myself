//Milosh Zelembaba & Nikola Zjalic
//MovingGuy.java
//I HAVE TO ADD SO THAT SHADOWS CAN STAND ON THE PLAYER AND VISEVERSA
//if you fall off the map and keep pressing the arrow keys it still makes a walking sound
//if you jump under a moving block it propels you upwards
//center the buttons

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import javax.sound.sampled.*;
	
class MovingGuy extends JFrame implements ActionListener{
	javax.swing.Timer myTimer;   
	GamePanel game;
	GameMenu menu;
	Story plot;
	FreePlayEditor editor;
	boolean gameInSession;
	boolean editing = false;
	boolean pageTurn = false;
	String [] levels = {"empty journal","TutTwo","empty journal","FirstLevel","empty journal","SecondLevel","empty journal","ThirdLevel","sky","FourthLevel"};
	int level = 0;
		
    public MovingGuy(){
		super("The Company of Myself");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);

		myTimer = new javax.swing.Timer(10, this);	 // trigger every 100 ms

		menu = new GameMenu();
		add(menu);
		game = new GamePanel(this,"TutOne","sky");
		add(game);
		plot = new Story(this,"sky");
		add(plot);
		gameInSession = false;

		setResizable(false);
		setVisible(true);
    }
	
	public void start(){
		myTimer.start();
	}
	public void startMusic(){
		try {
			// Open an audio input stream.
		    URL url = this.getClass().getClassLoader().getResource("David Newlyn - Almost Lost Again (Ambient Music).wav");
		    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
		    // Get a sound clip resource.
		    Clip clip = AudioSystem.getClip();
		    // Open audio clip and load samples from the audio input stream.
		    clip.open(audioIn);
		    clip.start();
		    clip.loop(Clip.LOOP_CONTINUOUSLY);
		} 
		catch (UnsupportedAudioFileException e) {
		    e.printStackTrace();
	 	} 
		catch (IOException e) {
		    e.printStackTrace();
		} 
		catch (LineUnavailableException e) {
		    e.printStackTrace();
		}
	}
	public void pageTurnSound(){
		try {
			// Open an audio input stream.
		    URL url = this.getClass().getClassLoader().getResource("pageturn.wav");
		    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
		    // Get a sound clip resource.
		    Clip clip = AudioSystem.getClip();
		    // Open audio clip and load samples from the audio input stream.
		    clip.open(audioIn);
		    clip.start();
		} 
		catch (UnsupportedAudioFileException e) {
		    e.printStackTrace();
	 	} 
		catch (IOException e) {
		    e.printStackTrace();
		} 
		catch (LineUnavailableException e) {
		    e.printStackTrace();
		}
	}
	public void newLevelSound(){
		try {
			// Open an audio input stream.
		    URL url = this.getClass().getClassLoader().getResource("newlevel.wav");
		    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
		    // Get a sound clip resource.
		    Clip clip = AudioSystem.getClip();
		    // Open audio clip and load samples from the audio input stream.
		    clip.open(audioIn);
		    clip.start();
		} 
		catch (UnsupportedAudioFileException e) {
		    e.printStackTrace();
	 	} 
		catch (IOException e) {
		    e.printStackTrace();
		} 
		catch (LineUnavailableException e) {
		    e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent evt){
		System.out.println(menu.mode);
		if(menu.mode.equals("menu")){
			gameInSession = false;
			menu.repaint();//calls paintComponent();
		}
		//FREE PLAY MODE STUFF------------------------------------------------------------
		if(menu.mode.equals("free play")){
			if(editing == false){
				editor = new FreePlayEditor();
				menu.setVisible(false);
				editor.setVisible(true);
				editing = true;	
			}
			if(editing == true){
				editor.repaint();
			}
			if(editor.startGame == true){
				game = new GamePanel(this,editor.levelName,"sky");
				add(game);			
				game.gameStarting = true;
				game.setVisible(true);
				editor.startGame = false;
				editor.setVisible(false);									
			}
			if(editor.playing == true){
				game.move();
				game.repaint();
				game.requestFocusInWindow();
			}
			if(game.end == true && game.transitionShade == 255){
				editing = false;
				game = new GamePanel(this,editor.levelName,"sky");
				game.setVisible(false);
				menu.mode = "menu";
				menu.setVisible(true);
			}			
		}
		//^FREE PLAY MODE STUFF-----------------------------------------------------------
		
		//CAREER MODE STUFF---------------------------------------------------------------
		if(menu.mode.equals("career")){
			if(gameInSession == false){
				game = new GamePanel(this,"TutOne","sky");
				add(game);
				game.gameStarting = true;		
				menu.setVisible(false);
				game.setVisible(true);
				gameInSession = true;
			}
			if(gameInSession == true && level%2 == 0){
				game.move();
				game.repaint();
				game.requestFocusInWindow();
			}
			if(game.end == true && game.transitionShade == 255 && level%2 == 0){
				plot = new Story(this, levels[level]);
				add(plot);
				level ++;
				plot.reading = true;
				game.setVisible(false);
				plot.setVisible(true);
				pageTurn = true;
			}
			if(pageTurn){
				pageTurnSound();
			}
			if(plot.reading == true && level%2 == 1){
				pageTurn = false;
				plot.repaint();
				plot.requestFocusInWindow();
			}
			if(plot.end == true && level%2 == 1){
				plot.end = false;
				plot.setVisible(false);
				game = new GamePanel(this,levels[level],"sky");
				add(game);			
				level++;
				game.gameStarting = true;
				game.setVisible(true);
				newLevelSound();	
			}			
		}
		//^CAREER MODE STUFF---------------------------------------------------------------
	}
	public void paintComponent(Graphics g){
	}

    public static void main(String[] arguments){
		MovingGuy frame = new MovingGuy();
		frame.startMusic();
		
    }
}
class Story extends JPanel implements KeyListener{
	private boolean []keys;
	private Image pic;
	public boolean reading;
	public boolean end;
	private MovingGuy mainFrame;
	public double transitionShade;
	public boolean endStart = false;
	
	
	public Story(MovingGuy m,String p){
		transitionShade = 255.0;
		mainFrame = m;
		reading = false;
		end = false;
		setSize(800,600);
		pic =  new ImageIcon("images/" + p + ".png").getImage();
		keys = new boolean[KeyEvent.KEY_LAST+1];
		addKeyListener(this);
        setFocusable(true);
        setVisible(false);
	}
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e){
    	endStart = true;
        keys[e.getKeyCode()] = false;
    }
    
    public void paintComponent(Graphics g){	
    	g.drawImage(pic,0,0,this);
    	if(transitionShade != 0 && endStart == false){						
			g.setColor(new Color(20,20,20,(int)(transitionShade)));
			g.fillRect(0,0,800,600);
			transitionShade -= 5.0;
		}
		if(endStart){
			if(transitionShade > 255.0){
				transitionShade = 255.0;
				end = true;
				reading = false;
			}					
			g.setColor(new Color(0,0,0,(int)(transitionShade)));
			g.fillRect(0,0,800,600);
			transitionShade += 2.0;
		}				
    }	
		
}
class GameMenu extends JPanel implements MouseListener{   
	private Image homeScreen;
	private Rectangle freePlay;
	private Rectangle career;
	private Rectangle options;
	private int mouseX,mouseY;
	public String mode;
		
    public GameMenu(){
		setSize(800,600);
		addMouseListener(this);
		
		mode = "menu";
		freePlay = new Rectangle(50,190,150,75);
		career = new Rectangle(250,190,150,75);
		options = new Rectangle(450,190,150,75);
		homeScreen = new ImageIcon("images/homeScreen.png").getImage();
		setVisible(true);
    }
    
	public void mouseEntered(MouseEvent e) {
	}
    public void mouseExited(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {//when a button is released
    	mouseX = e.getX();
		mouseY = e.getY();
		Point mouse = new Point(mouseX,mouseY);
		if(freePlay.contains(mouse)){
			mode = "free play";
		}
		if(career.contains(mouse)){
			mode = "career";
		}
		if(options.contains(mouse)){
			mode = "options";
		} 		
    }    
    public void mouseClicked(MouseEvent e){}  
    	 
    public void mousePressed(MouseEvent e){}	
    			
	public void paintComponent(Graphics g){
		g.setColor(new Color(20,20,20,100));
		g.drawImage(homeScreen, 0, 0, null);
		g.fillRect(freePlay.x,freePlay.y,freePlay.width,freePlay.height);
		g.fillRect(career.x,career.y,career.width,career.height);	
		g.fillRect(options.x,options.y,options.width,options.height);		
	}
}
class GamePanel extends JPanel implements KeyListener{
	public boolean gameStarting;
	double vy = 0;
	double vyS = 0;
	private boolean []keys;
	private Image back;
	private MovingGuy mainFrame;
	public Guy guy;
	private boolean redSwitchOn;
	private boolean blueSwitchOn;
	public static final int JUMP = 4;
	public static final int ABUTTON = 5;
	private ArrayList <Shadow> shadows = new ArrayList<Shadow>();	
	private ArrayList <Block> disappearedBlocks = new ArrayList <Block>();
	private ArrayList <Block> rects = new ArrayList <Block>();//rectangles that a player can stand on/not walk through
	private ArrayList <Block> objects = new ArrayList <Block>();//rectangles that a player can't stand on/can walk through
	private ArrayList <Block> objectsReset = new ArrayList <Block>();
	private ArrayList <Image> blockImgs = new ArrayList<Image> ();
	private ArrayList <Integer> currentShadowPath = new ArrayList<Integer> ();
	private Rectangle ground;
	public boolean end;
	private Block endB;
	private Scanner inFile;
	public Point reset;
	public double transitionShade;

	
	public GamePanel(MovingGuy m,String level,String pic){
		try{
			inFile = new Scanner(new BufferedReader(new FileReader(level + ".txt")));///CHANGE LEVEL HERE~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
		catch(IOException ex){
			System.out.println("Oops, couldn't find LEVEL1.txt");
		}
		transitionShade = 255;
		gameStarting = false;
		end = false;
		redSwitchOn = false;
		blueSwitchOn = false;
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("images/" + pic + ".jpg").getImage();
		guy = new Guy(10,0,0,true);
		mainFrame = m;
		setSize(800,600);
        addKeyListener(this);
        setFocusable(true);
        blockImgs.add(new ImageIcon("images/grass.jpg").getImage());
		blockImgs.add(new ImageIcon("images/dirt.png").getImage());
		blockImgs.add(new ImageIcon("images/orangeBox.png").getImage());
		blockImgs.add(new ImageIcon("images/blueBox.png").getImage());
		blockImgs.add(new ImageIcon("images/support.png").getImage());
		blockImgs.add(new ImageIcon("images/support2.png").getImage());
		blockImgs.add(new ImageIcon("images/startBlock.png").getImage());
		blockImgs.add(new ImageIcon("images/endBlockGame.png").getImage());
		blockImgs.add(new ImageIcon("images/movingPlatform.png").getImage());
		blockImgs.add(new ImageIcon("images/redSwitchOff.png").getImage());
		blockImgs.add(new ImageIcon("images/blueSwitchOff.png").getImage());
		blockImgs.add(new ImageIcon("images/redDisappearingBlock.png").getImage());
		blockImgs.add(new ImageIcon("images/blueDisappearingBlock.png").getImage());
        while (inFile.hasNextLine()){
        	String allInfo[] = inFile.nextLine().split(" ");
        	String x = allInfo[0];
        	String y = allInfo[1];
        	String pos = allInfo[2];
        	String dev = allInfo[3];
        	if (!pos.equals("6") && !pos.equals("7") && !pos.equals("9") && !pos.equals("10") && !pos.equals("8") && !pos.equals("5000")){// we shoudl change the order so we can do this checking with one comparison
        		rects.add(new Block(new Rectangle(Integer.parseInt(x),Integer.parseInt(y),50,50),blockImgs.get(Integer.parseInt(pos)),Integer.parseInt(pos),Integer.parseInt(dev)));
        	}
        	if (pos.equals("8")){
        		rects.add(new Block(new Rectangle(Integer.parseInt(x),Integer.parseInt(y),50,10),blockImgs.get(Integer.parseInt(pos)),Integer.parseInt(pos),Integer.parseInt(dev)));
        	}
        	if(pos.equals("6")){//the starting position of the guy
     			guy.setX(Integer.parseInt(x));
     			guy.setY(Integer.parseInt(y));
     			reset = new Point(Integer.parseInt(x),Integer.parseInt(y));
        	}
        	if (pos.equals("7") || pos.equals("9") || pos.equals("10")){
        		Block tmp = new Block(new Rectangle(Integer.parseInt(x),Integer.parseInt(y),50,50),blockImgs.get(Integer.parseInt(pos)),Integer.parseInt(pos),Integer.parseInt(dev));
        		objects.add(tmp);
        		if(pos.equals("7")){
        			endB = tmp;
        		}
        	}
        }
        objectsReset = objects;
        setVisible(false);
	}
	public void checkGameOver(){
		for(Shadow tmp: shadows){
			if(tmp.body.intersects(endB.rect)){
				end = true;
			}			
		}
		if(guy.body.intersects(endB.rect)){
			end = true;
		}
	}
	public void newShadowSound(){
		try {
			// Open an audio input stream.
		    URL url = this.getClass().getClassLoader().getResource("newshadow.wav");
		    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
		    // Get a sound clip resource.
		    Clip clip = AudioSystem.getClip();
		    // Open audio clip and load samples from the audio input stream.
		    clip.open(audioIn);
		    clip.start();
		} 
		catch (UnsupportedAudioFileException e) {
		    e.printStackTrace();
	 	} 
		catch (IOException e) {
		    e.printStackTrace();
		} 
		catch (LineUnavailableException e) {
		    e.printStackTrace();
		}
	}
	public void switchClick(){
		try {
			// Open an audio input stream.
		    URL url = this.getClass().getClassLoader().getResource("switchclick.wav");
		    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
		    // Get a sound clip resource.
		    Clip clip = AudioSystem.getClip();
		    // Open audio clip and load samples from the audio input stream.
		    clip.open(audioIn);
		    clip.start();
		} 
		catch (UnsupportedAudioFileException e) {
		    e.printStackTrace();
	 	} 
		catch (IOException e) {
		    e.printStackTrace();
		} 
		catch (LineUnavailableException e) {
		    e.printStackTrace();
		}
	}
	public void movingBlock(){
		for(int i=0; i<rects.size(); i++){
			Block b = rects.get(i);
			if(b.deviation > 0){
				b.addX(b.speed);
				if(b.x == b.changeDirectionXRight){
					b.speed = -1;
				}
				if(b.x == b.changeDirectionXLeft){
					b.speed = 1;
				}
			}
		}
	}
	public void resetLevel(){
		redSwitchOn = false;
		blueSwitchOn = false;
		objects = objectsReset;
		for(int i=0; i<disappearedBlocks.size(); i++){
			rects.add(disappearedBlocks.get(i));
		}
		disappearedBlocks = new ArrayList<Block> ();
		for(int i=0; i<rects.size(); i++){//resets blocks
			Block b = rects.get(i);
			if (b.deviation > 0){
				b.reset();
			}
		}
		currentShadowPath = new ArrayList<Integer> ();//new path being recorded
		shadows = new ArrayList<Shadow> ();//shadows all gone
		guy.setX(reset.x);//starts at beginning
		guy.setY(reset.y);
		vy = 0;
	}
	public void reset(){
		currentShadowPath = new ArrayList<Integer> ();
		redSwitchOn = false;
		blueSwitchOn = false;
		objects = objectsReset;
		for(int i=0; i<disappearedBlocks.size(); i++){
			rects.add(disappearedBlocks.get(i));
		}
		disappearedBlocks = new ArrayList<Block> ();
		for(int i=0; i<rects.size(); i++){
			Block b = rects.get(i);
			if (b.deviation > 0){
				b.reset();
			}
		}
		for(Shadow tmp : shadows){
			tmp.setX(reset.x);
			tmp.setY(reset.y);
			tmp.pos = 0;
		}
		guy.setX(reset.x);
		guy.setY(reset.y);
		vy = 0;
	}	
	public void makeShadow(){
		newShadowSound();
		shadows.add(new Shadow((double)(reset.x), (double)(reset.y), 0, true, currentShadowPath));
		reset();
	}
	public void moveShadow(){
		for(Shadow tmp : shadows){
			tmp.changePic();
			if(tmp != null){
				tmp.move();
				if(tmp.speed == 2){
					tmp.side = "Right";
					for (int i=0; i<rects.size(); i++){
					Rectangle ground = rects.get(i).rect;
						if (tmp.body.intersects(ground) && tmp.y >= ground.y && rects.get(i).deviation == 0 && rects.get(i).imagePos != 3){
							tmp.addX(-2);
							tmp.setSpeed(0);
						}
					}
				}
				if(tmp.speed == -2){
					tmp.side = "Left";
					for (int i=0; i<rects.size(); i++){
					Rectangle ground = rects.get(i).rect;
						if (tmp.body.intersects(ground) && tmp.y >= ground.y && rects.get(i).deviation == 0 && rects.get(i).imagePos != 3){
							tmp.addX(2);
							tmp.setSpeed(0);
						}
					}
				}
				if(tmp.jump == true && tmp.inJump == false){
					tmp.velocity = -10.7 ;
					tmp.inJump = true;				
				}
				if(tmp.switchPress == true){
					changeSwitchShadow();
				}
				tmp.addY(tmp.velocity);
				for (int i=0; i<rects.size(); i++){
					Block b = rects.get(i);
					Rectangle ground = b.rect;
					if (tmp.body.intersects(ground) && tmp.y < ground.y + 30 && b.imagePos != 3 && tmp.velocity > 0){
						tmp.setY(ground.y-20);
						tmp.velocity = 0;
						tmp.inJump = false;
						tmp.setJump(false);
						if(b.deviation > 0 ){
							tmp.x+=(b.speed*2);
						}
					}
					if(tmp.body.intersects(ground) && tmp.y > ground.y + 31 && b.imagePos != 3 && b.imagePos != 8){
						tmp.setY(ground.y+50);
						tmp.velocity = 0;						
					}
				}
				checkSPIntersect(guy,tmp);
				for(Shadow tmp1: shadows){
					checkSSIntersect(tmp,tmp1);
				}
				tmp.velocity+=0.7;//gravity is always acting on the guy					
			}
		}
	}
	public void checkSPIntersect(Guy player, Shadow tmp){//check shadow-player intersect
		Rectangle guyBody = player.body;
		if (tmp.body.intersects(guyBody) && tmp.x+10 > player.x && tmp.x < player.x+10 && tmp.y < player.y-1){
			tmp.setY(guyBody.y-20);
			tmp.velocity = 0;
			tmp.inJump = false;
			tmp.setJump(false);
			tmp.addX(player.speed*2);
		}
	}
	public void checkPSIntersect(Guy player, Shadow tmp){//check player-shadow intersect
		Rectangle shadowBody = tmp.body;
		if (player.body.intersects(shadowBody) && player.x+10 > tmp.x && player.x < tmp.x+10 && player.y < tmp.y-1){
			player.setY(shadowBody.y-20);
			vy = 0;
			player.setOnGround(true);
			player.addX(tmp.speed*2);
		}
	}
	public void checkSSIntersect(Shadow tmp1, Shadow tmp2){//check shadow-shadow intersect
		Rectangle guyBody = tmp2.body;
		if (tmp1.body.intersects(guyBody) && tmp1.x+10 > tmp2.x && tmp1.x < tmp2.x+10 && tmp1.y < tmp2.y-1){
			tmp1.setY(guyBody.y-20);
			tmp1.velocity = 0;
			tmp1.inJump = false;
			tmp1.setJump(false);
			tmp1.addX(tmp2.speed*2);
		}
	}			
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    public void changeSwitchShadow(){
    	for(Block b: objects){
    		for(Shadow tmpS : shadows){
	    		if(tmpS.body.intersects(b.rect) && tmpS.switchPress == true){ 
	    			switchClick();			
					if(b.imagePos == 9){
						if(redSwitchOn == false){
							redSwitchOn = true;
							Image tmpPic = new ImageIcon("images/redSwitchOn.png").getImage();
							Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,9,0);
							objects.set(objects.indexOf(b),tmp);
							removeBlock();
						}
						else{
							Image tmpPic = new ImageIcon("images/redSwitchOff.png").getImage();
							redSwitchOn = false;
							Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,9,0);
							objects.set(objects.indexOf(b),tmp);
							putBlock();
						}
					}
					if(b.imagePos == 10){
						if(blueSwitchOn == false){
							Image tmpPic = new ImageIcon("images/blueSwitchOn.png").getImage();
							blueSwitchOn = true;
							Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,10,0);
							objects.set(objects.indexOf(b),tmp);
							removeBlock();
						}
						else{
							Image tmpPic = new ImageIcon("images/blueSwitchOff.png").getImage();
							blueSwitchOn = false;
							Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,10,0);
							objects.set(objects.indexOf(b),tmp);
							putBlock();
						}
					}			
	    		}
    		}
		}	
    }
    public void changeSwitch(){
    	for(Block b: objects){
    		if(guy.body.intersects(b.rect)){
    			switchClick(); 			
				if(b.imagePos == 9){
					if(redSwitchOn == false){
						redSwitchOn = true;
						Image tmpPic = new ImageIcon("images/redSwitchOn.png").getImage();
						Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,9,0);
						objects.set(objects.indexOf(b),tmp);
						removeBlock();
					}
					else{
						Image tmpPic = new ImageIcon("images/redSwitchOff.png").getImage();
						redSwitchOn = false;
						Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,9,0);
						objects.set(objects.indexOf(b),tmp);
						putBlock();
					}
				}
				if(b.imagePos == 10){
					if(blueSwitchOn == false){
						Image tmpPic = new ImageIcon("images/blueSwitchOn.png").getImage();
						blueSwitchOn = true;
						Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,10,0);
						objects.set(objects.indexOf(b),tmp);
						removeBlock();
					}
					else{
						Image tmpPic = new ImageIcon("images/blueSwitchOff.png").getImage();
						blueSwitchOn = false;
						Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,10,0);
						objects.set(objects.indexOf(b),tmp);
						putBlock();
					}
				}			
    		}
		}	
    }
    public void removeBlock(){
    	ArrayList <Block> tmp = new ArrayList <Block>();
    	if (redSwitchOn == true){
    		for(Block b : rects){
    			if (b.imagePos == 11){//red disappearing block
    				tmp.add(b);
    			}
    		}
    	}
    	if (blueSwitchOn == true){
    		for(Block b : rects){
    			if (b.imagePos == 12){//blue disappearing block
    				tmp.add(b);
    			}
    		}
    	}
    	for (Block b : tmp){
    		rects.remove(b);
    		disappearedBlocks.add(b);
    	}	
    }
	public void putBlock(){
		ArrayList <Block> tmp = new ArrayList <Block>();
		if (redSwitchOn == false){
    		for(Block b : disappearedBlocks){
    			if (b.imagePos == 11){
    				tmp.add(b);
    			}
    		}
    	}
    	if (blueSwitchOn == false){
    		for(Block b : disappearedBlocks){
    			if (b.imagePos == 12){
    				tmp.add(b);
    			}
    		}
    	}
    	for (Block b : tmp){
    		disappearedBlocks.remove(b);
    		rects.add(b);
    	}
	}
	public void finishTransition(){
		if (transitionShade == 0){
			gameStarting = false;
		}
	}    	
	public void move(){
		guy.changePic();
		finishTransition();
		checkGameOver();
		movingBlock();
		moveShadow();
		currentShadowPath.add(guy.speed);
		guy.setSpeed(0);
		if(keys[KeyEvent.VK_RIGHT] ){
			if (guy.x < 790){
				guy.side = "Right";
				guy.addX(2);
				guy.setSpeed(2);
				for (int i=0; i<rects.size(); i++){
				Rectangle ground = rects.get(i).rect;
					if (guy.body.intersects(ground) && guy.y >= ground.y && rects.get(i).deviation == 0 && rects.get(i).imagePos != 2){
						guy.addX(-2);
					}
				
				}
			}
		}
		if(keys[KeyEvent.VK_LEFT] ){
			if(guy.x > 1){
				guy.side = "Left";
				guy.addX(-2);
				guy.setSpeed(-2);
				for (int i=0; i<rects.size(); i++){
				Rectangle ground = rects.get(i).rect;
					if (guy.body.intersects(ground) && guy.y >= ground.y && rects.get(i).deviation == 0 && rects.get(i).imagePos != 2){
						guy.addX(2);
					}
				}
			}
		}
		if(keys[KeyEvent.VK_UP] && guy.onGround == true && vy == 0.7){
			vy = -10.0;				
			guy.setOnGround(false);
			currentShadowPath.remove(currentShadowPath.size()-1);
			currentShadowPath.add(JUMP);
		}
		guy.addY(vy);
		for (int i=0; i<rects.size(); i++){
			Block b = rects.get(i);
			Rectangle ground = b.rect;
			if (guy.body.intersects(ground) && guy.y < ground.y + 30 && b.imagePos != 2 && vy > 0){
				guy.setY(ground.y-20);
				vy = 0;
				guy.setOnGround(true);
				if(b.deviation > 0 ){
					guy.addX(b.speed*2);
				}
			}
			if(guy.body.intersects(ground) && guy.y > ground.y + 30 && b.imagePos != 2 && b.imagePos != 8){
				guy.setY(ground.y+50);
				vy = 0;						
			}
		}
		for (Shadow tmp : shadows){
			checkPSIntersect(guy,tmp);
		}
		vy += 0.7;//gravity is always acting on the guy
	}	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e) {
        if(keys[KeyEvent.VK_SPACE]){
			makeShadow();
		}
		if(keys[KeyEvent.VK_A]){
			changeSwitch();
			currentShadowPath.add(ABUTTON);
			
		}
		if(keys[KeyEvent.VK_R]){
			guy.side = "Right";
			resetLevel();
		}
        keys[e.getKeyCode()] = false;
    }
    
    public void paintComponent(Graphics g){ 	
    	g.drawImage(back,0,0,this);  
		paintBlocks(g); 
		g.drawImage(guy.pic,(int)(guy.x),(int)(guy.y), null);		 
		for (Shadow tmp : shadows){
			if (tmp != null){
				g.drawImage(tmp.pic,(int)(tmp.x),(int)(tmp.y), null);			
			}
		}
		if(gameStarting){					
			g.setColor(new Color(20,20,20,(int)(transitionShade)));
			g.fillRect(0,0,800,600);
			transitionShade -= 0.5;
		}
		if(end){
			if(transitionShade > 255.0){
				transitionShade = 255.0;
			}					
			g.setColor(new Color(0,0,0,(int)(transitionShade)));
			g.fillRect(0,0,800,600);
			transitionShade += 1.0;
		}	
				
    }
    public void paintBlocks(Graphics g){//paints the level blocks
		for(Block b: rects){
			g.drawImage(b.img, b.x, b.y, null);	
		}
		for(Block b: objects){
			g.drawImage(b.img, b.x, b.y, null);	
		}
    } 
}

class Guy{
	double x;
	double y;
	double velocity;
	boolean onGround;
	Rectangle body;
	public Point reset;
	public int speed;
	public Image pic;
	private Image standRight;
	private Image standLeft;
	private ArrayList <Image> rightWalk = new ArrayList<Image>();
	private int picPos = 0;	
	private ArrayList <Image> jumpRight = new ArrayList<Image>();
	private int jumpPos = 0;
	private ArrayList <Image> leftWalk = new ArrayList<Image>();
	private ArrayList <Image> jumpLeft = new ArrayList<Image>();
	public String side = "Right";
	private String[] walkNoise = {"walk1","walk2","walk3","walk4"};
	private int walkNoisePos = 0;
	private int walkTimer = 0;	
	
	
	public Guy(double x1, double y1, double v, boolean og){
		x = x1;
		y = y1;
		velocity = v;
		onGround = og;
		body = new Rectangle((int)x,(int)y,10,20);
		reset = new Point((int)(x),(int)(y));
		speed = 0;
		pic = new ImageIcon("images/sprites/stand1_right.png").getImage();
		standRight = pic;
		standLeft = new ImageIcon("images/sprites/stand1_left.png").getImage();
		for(int i=1;i<8;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/walk" + i + "_right.png").getImage();
				rightWalk.add(tmp);	
			}
		}
		for(int i=1;i<4;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/jump" + i + "_right.png").getImage();
				jumpRight.add(tmp);	
			}
		}
		for(int i=1;i<8;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/walk" + i + "_left.png").getImage();
				leftWalk.add(tmp);	
			}
		}
		for(int i=1;i<4;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/jump" + i + "_left.png").getImage();
				jumpLeft.add(tmp);	
			}
		}
	}
	public void walk(){
		if(walkTimer == 1){
			walkNoisePos++;
			try {
				// Open an audio input stream.
			    URL url = this.getClass().getClassLoader().getResource(walkNoise[walkNoisePos%4] + ".wav");
			    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			    // Get a sound clip resource.
			    Clip clip = AudioSystem.getClip();
			    // Open audio clip and load samples from the audio input stream.
			    clip.open(audioIn);
			    clip.start();
			} 
			catch (UnsupportedAudioFileException e) {
			    e.printStackTrace();
		 	} 
			catch (IOException e) {
			    e.printStackTrace();
			} 
			catch (LineUnavailableException e) {
			    e.printStackTrace();
			}
		}
	}
	public void changeWalkTimer(){
		if(walkTimer == 12){
			walkTimer = 0;
		}
	}
	public void changePic(){
		if(onGround == true){
			changeWalkTimer();
			jumpPos = 0;
			if(speed == 2){
				pic = rightWalk.get(picPos%rightWalk.size());
				picPos++;
				walkTimer ++;
				walk();
			}
			if(speed == -2){
				pic = leftWalk.get(picPos%leftWalk.size());
				picPos++;
				walkTimer ++;
				walk();
			}
			if(speed == 0 && side.equals("Right")){
				pic = standRight;
				picPos = 0;
				walkTimer = 0;
			}
			if(speed == 0 && side.equals("Left")){
				pic = standLeft;
				picPos = 0;
				walkTimer = 0;
			}			
		}
		else{
			if(speed == 2){
				pic = jumpRight.get(jumpPos%jumpRight.size());
				jumpPos ++;
			}
			if(speed == -2){
				pic = jumpLeft.get(jumpPos%jumpLeft.size());
				jumpPos ++;
			}
		}
	}
	
	public void addY(double num){//changes the y position and updates the box around the guy
		y+=num;
		body = new Rectangle((int)x,(int)y,10,20);
	}
	public void addX(double num){//changes the x position and updates the box around the guy
		x+=num;
		body = new Rectangle((int)x,(int)y,10,20);
	}
	public void setOnGround(boolean state){//changes whether the guy is on the ground or not
		onGround = state;
	}
	public void setY(int num){//set y position
		y = num;
	}
	public void setX(int num){//set x position
		x = num;
	}
	public void setSpeed(int num){
		speed = num;
	}
	public void reset(){
		setY(reset.y);
		setX(reset.x);
	}
}
class Shadow{
	double x;
	double y;
	double velocity;
	boolean jump;
	boolean switchPress = false;
	boolean inJump = false;
	Rectangle body;
	private Point reset;
	public int speed;
	public ArrayList <Integer> path = new ArrayList<Integer> ();
	public int pos;
	public Image pic;
	private Image standRight;
	private Image standLeft;
	private ArrayList <Image> rightWalk = new ArrayList<Image>();
	private int picPos = 0;	
	private ArrayList <Image> jumpRight = new ArrayList<Image>();
	private int jumpPos = 0;
	private ArrayList <Image> leftWalk = new ArrayList<Image>();
	private ArrayList <Image> jumpLeft = new ArrayList<Image>();
	public String side = "Right";
	
	public Shadow(double x1, double y1, double v, boolean j, ArrayList p){
		x = x1;
		y = y1;
		velocity = v;
		jump = j;
		body = new Rectangle((int)x,(int)y,10,20);
		reset = new Point((int)(x),(int)(y));
		speed = 0;
		path = p;
		pos = 0;
		pic = new ImageIcon("images/sprites/stand1_right.png").getImage();
		standRight = pic;
		standLeft = new ImageIcon("images/sprites/stand1_left.png").getImage();
		for(int i=1;i<8;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/walk" + i + "_right.png").getImage();
				rightWalk.add(tmp);	
			}
		}
		for(int i=1;i<4;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/jump" + i + "_right.png").getImage();
				jumpRight.add(tmp);	
			}
		}
		for(int i=1;i<8;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/walk" + i + "_left.png").getImage();
				leftWalk.add(tmp);	
			}
		}
		for(int i=1;i<4;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/jump" + i + "_left.png").getImage();
				jumpLeft.add(tmp);	
			}
		}
	}
	public void move(){
		switchPress = false;
		jump = false;
		if(pos < path.size()){
			int tmp = path.get(pos);
			if(tmp != 4 && tmp != 5){
				addX(tmp);
				speed = tmp;
				if(x>790){
					x -=2;
				}
				if(x<0){
					x +=2;
				}
			}
			if(tmp == 4){
				jump = true;
			}
			if(tmp == 5){
				switchPress = true;
			}	
		}
		
		pos++;
	}
	public void changePic(){
		if(jump == false){
			jumpPos = 0;
			if(speed == 2){
				pic = rightWalk.get(picPos%rightWalk.size());
				picPos++;
			}
			if(speed == -2){
				pic = leftWalk.get(picPos%leftWalk.size());
				picPos++;
			}
			if(speed == 0 && side.equals("Right")){
				pic = standRight;
				picPos = 0;
			}
			if(speed == 0 && side.equals("Left")){
				pic = standLeft;
				picPos = 0;
			}			
		}
		else{
			if(speed == 2){
				pic = jumpRight.get(jumpPos%jumpRight.size());
				jumpPos ++;
			}
			if(speed == -2){
				pic = jumpLeft.get(jumpPos%jumpLeft.size());
				jumpPos ++;
			}
		}
	}
	public void addY(double num){//changes the y position and updates the box around the guy
		y+=num;
		body = new Rectangle((int)x,(int)y,10,20);
	}
	public void addX(double num){//changes the x position and updates the box around the guy
		x+=num;
		body = new Rectangle((int)x,(int)y,10,20);
	}
	public void setJump(boolean state){//changes whether the guy is on the ground or not
		jump = state;
	}	
	public void setY(int num){//set y position
		y = num;
	}
	public void setX(int num){//set x position
		x = num;
	}
	public void setSpeed(int num){
		speed = num;
	}
	public void reset(){
		setY(reset.y);
		setX(reset.x);
	}
}