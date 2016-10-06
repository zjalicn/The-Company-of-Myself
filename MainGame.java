		//Milosh Zelembaba & Nikola Zjalic
//MainGame.java

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import javax.sound.sampled.*;
	
class MainGame extends JFrame implements ActionListener{
	javax.swing.Timer myTimer;   
	GamePanel game;// stores all the necessary components in memory, but most are not visible
	GameMenu menu;
	Story plot;
	FreePlayEditor editor;
	boolean gameInSession;//when the game is in career mode
	boolean editing = false;//when the player is creating a level in freeplay
	boolean pageTurn = false;
	String [] levels = {"Journals/info journal","TutTwo","Journals/journal1","Level1","Journals/journal2","Level2","Journals/journal3","Level3","Journals/journal4","Level4","Journals/journal5","Level5","Journals/journal6","Level6","Journals/journal7","Level7","Journals/journal8","Level8","Journals/journal9","Level9","Journals/journal10","Level10","Journals/journal12","TutOne","Journals/journal13","wife","Journals/journal14","Level11","Journals/journal15","Level12","Journals/journal16","Level13","Journals/journal17","Level14","Journals/journal18","Level15","Journals/journal19","Level16","Journals/journal20","Level17","Journals/journal21","Level18","Journals/journal22"};//all the levels/diary entries that the player goes through
	String [] levelPics = {"","tutorial2","","sky","","sky","","sky","","sky","","sky","","forest1","","forest1","","forest1","","forest1","","forest1","","forest1","","house","","forest2","","forest2","","forest2","","forest2","","house","","forest1","","sky","","sky",""};// the pictures to each level	
	int level = 0;
	public boolean music = true;
	public boolean FX = true;
		
    public MainGame(){
		super("The Company of Myself");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);

		myTimer = new javax.swing.Timer(10, this);	 // trigger every 100 ms

		menu = new GameMenu(this);//makes and stores all the necessary components in memory, but most are not visible
		add(menu);
		game = new GamePanel(this,"TutOne","sky",FX);
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
	public void startMusic(){//the game music
		try {
			System.out.println(music);
			// Open an audio input stream.		
			URL url = this.getClass().getClassLoader().getResource("David Newlyn - Almost Lost Again (Ambient Music).wav");	    	
		    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
		    // Get a sound clip resource.
		    Clip clip = AudioSystem.getClip();
		    // Open audio clip and load samples from the audio input stream.
		    clip.open(audioIn);
		    clip.start();
		    clip.loop(Clip.LOOP_CONTINUOUSLY);
		    if(music == false){
		    	clip.start();
		    }
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
	public void pageTurnSound(){//when theres a new diary entry, this sound plays
		if(FX){
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
	}
	public void newLevelSound(){// for every new level, a slow fade plays
		if(FX){
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
	}

	public void actionPerformed(ActionEvent evt){
		if(menu.mode.equals("menu") || menu.mode.equals("options")){//while the player is in the menu, paints the menu contents
			gameInSession = false;
			menu.repaint();//calls paintComponent();
		}
		//FREE PLAY MODE STUFF------------------------------------------------------------
		if(menu.mode.equals("free play")){
			if(editing == false){//creates a new FreePlayEditor for the user to create a level
				editor = new FreePlayEditor();
				menu.setVisible(false);//menu is not visible anymore
				editor.setVisible(true);
				editing = true;
			}
			if(editing == true){//repaints the editor contents while the user is creating a level
				editor.repaint();
			}
			if(editor.startGame == true){//when the user has finished making a level and wants to play it
				game = new GamePanel(this,editor.levelName,"sky",FX);//creates a level based off what the user just made
				add(game);
				game.gameStarting =
					true;//starts the fade in effect
				game.setVisible(true);//the game level becomes visible
				editor.startGame = false;
				editor.setVisible(false);
			}
			if(editor.playing == true){//while the user is playing their level
				game.move();//moves everything
				game.repaint();//paints everything
				game.requestFocusInWindow();//gives focus
			}
			if(game.end == true && game.transitionShade == 255){//when the game has finished(and when the fade out effect is done), goes back to the main menu
				editing = false;//not editing anymore
				game.end = false;
				game.setVisible(false);
				menu.mode = "menu";
				menu.setVisible(true);
			}
		}
		//^FREE PLAY MODE STUFF-----------------------------------------------------------
		
		//CAREER MODE STUFF---------------------------------------------------------------
		if(menu.mode.equals("career")){//when the player is playing in career mode
			if(gameInSession == false){//if career mode hasnt started yet, 
				game = new GamePanel(this,"TutOne","tutorial1",FX);//first level
				add(game);
				game.gameStarting = true;//starts fade in effect		
				menu.setVisible(false);//not looking at menu anymore
				game.setVisible(true);//looking at the game
				gameInSession = true;
			}
			if(gameInSession == true && level%2 == 0){//while playing a level
				game.move();//moves everything
				game.repaint();//paints everything
				game.requestFocusInWindow();//gives focus
				System.out.println(game.blueSwitchOn);
			}
			if(game.end == true && game.transitionShade == 255 && level%2 == 0){//when a level is finished(and fade effect), brings up the story line
				plot = new Story(this, levels[level]);//creates the diary entry
				add(plot);
				level ++;//to load the next level when its time
				plot.reading = true;//player is reading diary
				game.setVisible(false);
				plot.setVisible(true);
				pageTurn = true;//for soound effect
			}
			if(pageTurn){//plays sound effect
				pageTurnSound();
			}
			if(plot.reading == true && level%2 == 1){//while player is reading the diary
				pageTurn = false;
				plot.repaint();//repaints contents
				plot.requestFocusInWindow();//gets focus
			}
			if(plot.end == true && level%2 == 1){//when user is done reading the story
				if(level == 43){
					menu.mode = "menu";
					gameInSession = false;
					plot.setVisible(false);
					menu.setVisible(true);
					level = 0;
				}
				else{
					plot.end = false;
					plot.setVisible(false);
					game = new GamePanel(this,levels[level],levelPics[level],FX);//creates new level
					add(game);			
					level++;//for the next diary entry when its time
					game.gameStarting = true;//starts fade effect
					game.setVisible(true);
					newLevelSound();//plays the newlevel sound		
				}
			}			
		}
		//^CAREER MODE STUFF---------------------------------------------------------------
	}
	public void paintComponent(Graphics g){
	}

    public static void main(String[] arguments){
		MainGame frame = new MainGame();
		frame.startMusic();//starts music
		
    }
}
class Story extends JPanel implements KeyListener{// all the diary entries are their own class
	private boolean []keys;
	private Image pic;// diary pic
	private Image candlepic =  new ImageIcon("Journals/candlelight.png").getImage();
	public boolean reading;// if the diary is being read
	public boolean end;//if the user is done reading the diary and fade effect has finished
	private MainGame mainFrame;
	public double transitionShade;//fade effects transparency
	public boolean endStart = false;//to start the fade effect
	
	
	public Story(MainGame m,String p){
		transitionShade = 255.0;//opaque screen for fade effect
		mainFrame = m;
		reading = false;
		end = false;
		setSize(800,600);
		pic =  new ImageIcon(p + ".png").getImage();
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
    	endStart = true;//starts transition
        keys[e.getKeyCode()] = false;
    }
    
    public void paintComponent(Graphics g){//paints contents
       	g.drawImage(candlepic,0,0,this);//diary pic
    	g.drawImage(pic,00,0,this);//diary pic
    	if(transitionShade != 0 && endStart == false){//fade in effect					
			g.setColor(new Color(20,20,20,(int)(transitionShade)));
			g.fillRect(0,0,800,600);
			transitionShade -= 5.0;
		}
		if(endStart){//fade out effects
			if(transitionShade > 255.0){
				transitionShade = 255.0;
				end = true;//fade is finished
				reading = false;//user is not reading diary anymore
			}					
			g.setColor(new Color(0,0,0,(int)(transitionShade)));
			g.fillRect(0,0,800,600);
			transitionShade += 2.0;
		}				
    }
    public static void main(String[] arguments){		
    }	
		
}
class GameMenu extends JPanel implements MouseListener{//the menu for the game   
	private Image homeScreen;//menu picture
	private Image freePlayPic;//button pictures
	private Image storyModePic;
	private Image optionsPic;
	private Image backPic;
	private Image musicOffPic;
	private Image musicOnPic;
	private Image FXOffPic;
	private Image FXOnPic;
	private Image musicPic;
	private Image FXPic;
	private Rectangle freePlay;//the 3 buttons
	private Rectangle career;
	private Rectangle options;
	private Rectangle musicRect;
	private Rectangle FXRect;
	private Rectangle backRect;
	private int mouseX,mouseY;//mouse pos
	public String mode;//the mode the user wishes to play
	public boolean music = true;
	public boolean FXmusic = true;
	public MainGame mainFrame;
		
    public GameMenu(MainGame m){
		setSize(800,600);
		addMouseListener(this);
		
		mainFrame = m;
		mode = "menu";
		freePlayPic = new ImageIcon("images/FreePlayButton.png").getImage();//button pictures
		storyModePic = new ImageIcon("images/StoryModeButton.png").getImage();
		optionsPic = new ImageIcon("images/optionsButton.png").getImage();
		backPic = new ImageIcon("images/backButton.png").getImage();
		musicOffPic = new ImageIcon("images/musicOff.png").getImage();
		musicOnPic = new ImageIcon("images/musicOn.png").getImage();
		FXOffPic = new ImageIcon("images/FxOff.png").getImage();
		FXOnPic = new ImageIcon("images/FxOn.png").getImage();
		musicPic = musicOnPic;
		FXPic = FXOnPic;
		career = new Rectangle(110,150,250,115);//gives the buttons their positions
		freePlay = new Rectangle(400,150,250,115);
		musicRect = new Rectangle(110,150,250,115);
		FXRect = new Rectangle(400,150,250,115);
		options = new Rectangle(600,475,175,88);
		backRect = new Rectangle(450,475,175,88);
		homeScreen = new ImageIcon("images/homeScreen.png").getImage();
		setVisible(true);//menu is the first thing that is visible when the game is turned on
    }
    
	public void mouseEntered(MouseEvent e) {
	}
    public void mouseExited(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {//when mouse is released
    	mouseX = e.getX();
		mouseY = e.getY();
		Point mouse = new Point(mouseX,mouseY);
		//checks for which rectangle has been clicked
		if(mode.equals("menu")){//menu area		
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
		if(mode.equals("options")){//options area
			if(musicRect.contains(mouse)){
				if(music){//music on/off
					music = false;
					mainFrame.music = false;
					musicPic = musicOffPic;
				}
				else{
					music = true;
					mainFrame.music = true;
					musicPic = musicOnPic;
				}
				
				//mainFrame.startMusic();
			}
			if(FXRect.contains(mouse)){//fx on/off
				if(FXmusic){
					FXmusic = false;
					mainFrame.FX = false;
					FXPic = FXOffPic;
				}
				else{
					FXmusic = true;
					mainFrame.FX = true;
					FXPic = FXOnPic;
				}
			}
			if(backRect.contains(mouse)){
				mode = "menu";
			} 
		}		
    }    
    public void mouseClicked(MouseEvent e){}  
    	 
    public void mousePressed(MouseEvent e){}	
    			
	public void paintComponent(Graphics g){ //paints the contents
		if(mode.equals("menu")){
			g.setColor(new Color(20,20,20,100));
			g.drawImage(homeScreen, 0, 0, null);
			g.drawImage(freePlayPic,freePlay.x,freePlay.y,null);
			g.drawImage(storyModePic,career.x,career.y,null);	
			g.drawImage(optionsPic,options.x,options.y,null);		
		}
		if(mode.equals("options")){
			g.drawImage(homeScreen, 0, 0, null);
			g.drawImage(musicPic,musicRect.x,musicRect.y,null);
			g.drawImage(FXPic,FXRect.x,FXRect.y,null);
			g.drawImage(backPic,backRect.x,backRect.y,null);			
		}		
	}
}
class GamePanel extends JPanel implements KeyListener{//the level
	public boolean gameStarting;//for the fade in effect
	double vy = 0;//the gravity acting on the player
	private boolean []keys;
	private Image back;//background image
	private MainGame mainFrame;
	public Guy guy;//the player
	private boolean redSwitchOn;//if the red Switch is on
	public boolean blueSwitchOn;//if the blue switch is on
	public static final int JUMP = 4;//shadow comands
	public static final int ABUTTON = 5;//shadow comands
	private ArrayList <Shadow> shadows = new ArrayList<Shadow>();//a list of all the shadows currently in the game	
	private ArrayList <Block> disappearedBlocks = new ArrayList <Block>();//list of the blocks that have dissapeared b/c their switch was turned on
	private ArrayList <Block> rects = new ArrayList <Block>();//rectangles that both the player/shadow can stand on/not walk through
	private ArrayList <Block> objects = new ArrayList <Block>();//rectangles that both the player/shadow can't stand on/can walk through
	private ArrayList <Image> blockImgs = new ArrayList<Image> ();//images for all the blocks
	private ArrayList <Integer> currentShadowPath = new ArrayList<Integer> ();//records the path for the current shadow
	private Rectangle ground;
	public boolean end;//for starting the fade out effect
	private Block endB;//the block the player needs to reach to beat the game
	private Scanner inFile;//for inputing the level information
	public Point reset;//where the player gets reseted
	public double transitionShade;//for the fade effects
	public boolean FX;

	
	public GamePanel(MainGame m,String level,String pic,boolean fx){
		try{
			inFile = new Scanner(new BufferedReader(new FileReader(level + ".txt")));//reads in the file for the level information
		}
		catch(IOException ex){
			System.out.println("Oops, couldn't find" +level+ ".txt");
		}
		FX = fx;
		transitionShade = 255;
		gameStarting = false;
		end = false;
		redSwitchOn = false;
		blueSwitchOn = false;
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("images/" + pic + ".png").getImage();//background image
		guy = new Guy(10,0,0,true);
		mainFrame = m;
		setSize(800,600);
        addKeyListener(this);
        setFocusable(true);
        //all the images for the blocks
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
        while (inFile.hasNextLine()){//reading in the info for the level
        	String allInfo[] = inFile.nextLine().split(" ");//each line is a block
        	String x = allInfo[0];//x pos of the block
        	String y = allInfo[1];//y pos of the block
        	String pos = allInfo[2];//the type of image(gives reference to the image position in the list of block images)
        	String dev = allInfo[3];//if its a moving block, how far it moves
        	//the blocks that either dont show up(5000), or more than just adding them to a list needs to be done with them
        	if (!pos.equals("6") && !pos.equals("7") && !pos.equals("9") && !pos.equals("10") && !pos.equals("8") && !pos.equals("5000")){// we shoudl change the order so we can do this checking with one comparison
        		rects.add(new Block(new Rectangle(Integer.parseInt(x),Integer.parseInt(y),50,50),blockImgs.get(Integer.parseInt(pos)),Integer.parseInt(pos),Integer.parseInt(dev)));
        	}
        	if (pos.equals("8")){//moving platform block
        		rects.add(new Block(new Rectangle(Integer.parseInt(x),Integer.parseInt(y),50,10),blockImgs.get(Integer.parseInt(pos)),Integer.parseInt(pos),Integer.parseInt(dev)));
        	}
        	if(pos.equals("6")){//the starting position of the guy
     			guy.setX(Integer.parseInt(x));
     			guy.setY(Integer.parseInt(y)+30);
     			reset = new Point(Integer.parseInt(x),Integer.parseInt(y)+30);
        	}
        	if (pos.equals("7") || pos.equals("9") || pos.equals("10")){//end block, red/blue switch(player/shadow cant walk on these blocks)
        		Block tmp = new Block(new Rectangle(Integer.parseInt(x),Integer.parseInt(y),50,50),blockImgs.get(Integer.parseInt(pos)),Integer.parseInt(pos),Integer.parseInt(dev));
        		objects.add(tmp);
        		if(pos.equals("7")){//the end block
        			endB = tmp;
        		}
        	}
        }
        setVisible(false);
	}
	public void checkGameOver(){//checks for when the player reaches the end block
		for(Shadow tmp: shadows){//if a shadow reaches the end block
			if(tmp.body.intersects(endB.rect)){
				end = true;
			}			
		}
		if(guy.body.intersects(endB.rect)){//if the player reaches the end block
			end = true;
		}
	}
	public void newShadowSound(){//the sound for when a new shadow is made
		if (FX){
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
	}
	public void switchClick(){//the sound for when a switch is clicked
		if(FX){
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
	}
	public void movingBlock(){//moves all the moving blocks
		for(int i=0; i<rects.size(); i++){
			Block b = rects.get(i);
			if(b.deviation > 0){//checks if the block is a moving block
				b.addX(b.speed);//moves block
				if(b.x == b.changeDirectionXRight){//when the limit is reached, changes direction in which the block moves
					b.speed = -1;
				}
				if(b.x == b.changeDirectionXLeft){//when the limit is reached, changes direction in which the block moves
					b.speed = 1;
				}
			}
		}
	}
	public void resetLevel(){//resets level entirely
		redSwitchOn = false;
		blueSwitchOn = false;
		for(int i=0; i<disappearedBlocks.size(); i++){//readds the blocks that have disappeared to the list of blocks the player cna come in contact with
			rects.add(disappearedBlocks.get(i));
		}
		disappearedBlocks = new ArrayList<Block> ();
		for(int i=0; i<rects.size(); i++){//resets all moving blocks
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
	public void reset(){//when the player makes a shadow and gets reset
		currentShadowPath = new ArrayList<Integer> ();//starts recording the path for the next shadow
		redSwitchOn = false;
		blueSwitchOn = false;
		for(int i=0; i<disappearedBlocks.size(); i++){//readds the blocks that have disappeared to the list of blocks the player cna come in contact with
			rects.add(disappearedBlocks.get(i));
		}
		disappearedBlocks = new ArrayList<Block> ();
		for(int i=0; i<rects.size(); i++){//resets all the moving blocks
			Block b = rects.get(i);
			if (b.deviation > 0){
				b.reset();
			}
		}
		for(Shadow tmp : shadows){//all shadows get reset to the beginning
			tmp.setX(reset.x);
			tmp.setY(reset.y);
			tmp.pos = 0;
		}
		guy.setX(reset.x);//player gets reset
		guy.setY(reset.y);
		vy = 0;//resets the gravity
	}	
	public void makeShadow(){//makes new shadow
		newShadowSound();//new shadow sound
		shadows.add(new Shadow((double)(reset.x), (double)(reset.y), 0, true, currentShadowPath));//constructs a new shadow
		reset();//resets level
	}
	public void moveShadow(){//moves shadow
		for(Shadow tmp : shadows){
			tmp.changePic();//new walking picture
			if(tmp != null){
				tmp.move();//moves the shadow based on the path
				if(tmp.speed == 2){//if the shadow is moving right
					tmp.side = "Right";//the side that the shadow is going to be facing when it stops moving
					for (int i=0; i<rects.size(); i++){//checks collisions witt blocks so it cant walk through them
					Rectangle ground = rects.get(i).rect;
						if (tmp.body.intersects(ground) && tmp.y + 20 >= ground.y && rects.get(i).deviation == 0 && rects.get(i).imagePos != 3){//shadows can walk through block #3
							tmp.addX(-2);//moves them back as much as they moved forward so they stay in place
							tmp.setSpeed(0);//not moving so speed is 0
						}
					}
				}
				if(tmp.speed == -2){//if the shadow is moving left
					tmp.side = "Left";//the side the shadow will be facing when it stops moving
					for (int i=0; i<rects.size(); i++){
					Rectangle ground = rects.get(i).rect;
						if (tmp.body.intersects(ground) && tmp.y +20 >= ground.y && rects.get(i).deviation == 0 && rects.get(i).imagePos != 3){
							tmp.addX(2);
							tmp.setSpeed(0);
						}
					}
				}
				if(tmp.jump == true && tmp.inJump == false){//when the shadow jumps
					tmp.velocity = -10.7 ;//starting jump
					tmp.inJump = true;//so the shadow cant jump while its already in a jump				
				}
				if(tmp.switchPress == true){//if the shadow is told to press a switch
					changeSwitchShadow();
				}
				tmp.addY(tmp.velocity);//gravity
				for (int i=0; i<rects.size(); i++){//checks for ground
					Block b = rects.get(i);
					Rectangle ground = b.rect;
					if (tmp.body.intersects(ground) && tmp.y < ground.y + 30 && b.imagePos != 3 && tmp.velocity > 0){//if it lands on a block while either falling or already on one
						tmp.setY(ground.y-20);//sets character to stand on the block
						tmp.velocity = 0;//so it doesnt fall through the block
						tmp.inJump = false;//so the shadow can jump again
						tmp.setJump(false);
						if(b.deviation > 0 ){//if the shadow is on a moving block
							tmp.x+=(b.speed*2);//moves with the block
						}
					}
					if(tmp.body.intersects(ground) && tmp.y > ground.y + 31 && b.imagePos != 3 && b.imagePos != 8){//if the shadow jumps and hits a block from underneath
						tmp.setY(ground.y+50);//sets the shadow right below the block
						tmp.velocity = 0;//shadow starts to fall						
					}
				}
				checkSPIntersect(guy,tmp);//checks is the shadow jumps onto a player
				for(Shadow tmp1: shadows){//checks if a shadow jumps onto another shadow(inefficient but works since not many shadows are made)
					checkSSIntersect(tmp,tmp1);
				}
				tmp.velocity+=0.7;//gravity increasing					
			}
		}
	}
	public void checkSPIntersect(Guy player, Shadow tmp){//check shadow-player intersect
		Rectangle guyBody = player.body;
		if (tmp.body.intersects(guyBody) && tmp.x+10 > player.x && tmp.x < player.x+10 && tmp.y < player.y-1){//if the shadow came from above the player
			tmp.setY(guyBody.y-20);//sets shadow ontop of player
			tmp.velocity = 0;//gravity wont pull the shadow down 
			tmp.inJump = false;
			tmp.setJump(false);
			tmp.addX(player.speed*2);//moves if the player moves
		}
	}
	public void checkPSIntersect(Guy player, Shadow tmp){//check player-shadow intersect
		Rectangle shadowBody = tmp.body;
		if (player.body.intersects(shadowBody) && player.x+10 > tmp.x && player.x < tmp.x+10 && player.y < tmp.y-1){//if the player came from above the shadow
			player.setY(shadowBody.y-20);//sets player ontop of shadow
			vy = 0;//gravity ownt pull the player down
			player.setOnGround(true);
			player.addX(tmp.speed*2);//moves the player if the shadow moves
		}
	}
	public void checkSSIntersect(Shadow tmp1, Shadow tmp2){//check shadow-shadow intersect
		Rectangle guyBody = tmp2.body;
		if (tmp1.body.intersects(guyBody) && tmp1.x+10 > tmp2.x && tmp1.x < tmp2.x+10 && tmp1.y < tmp2.y-1){//if the shadow came from above the other shadow
			tmp1.setY(guyBody.y-20);//sets the jumping shadow on top of the othe shadow
			tmp1.velocity = 0;//gravity wont pull it down
			tmp1.inJump = false;
			tmp1.setJump(false);
			tmp1.addX(tmp2.speed*2);//moves if other shadow moves
		}
	}			
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    public void changeSwitchShadow(){//checks for all shadows if its changing a switch
    	for(Block b: objects){
    		for(Shadow tmpS : shadows){
	    		if(tmpS.body.intersects(b.rect) && tmpS.switchPress == true){//if shadow is at a switch and if its been commanded to press a switch 
	    			switchClick();// sound for when a switch clicks			
					if(b.imagePos == 9){//for the red switch
						if(redSwitchOn == false){//if the switch is off, it turns it on
							redSwitchOn = true;
							Image tmpPic = new ImageIcon("images/redSwitchOn.png").getImage();//new picture for switch
							Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,9,0);//adds the new switch to the list
							objects.set(objects.indexOf(b),tmp);
							removeBlock();//removes all the red blocks
						}
						else{
							Image tmpPic = new ImageIcon("images/redSwitchOff.png").getImage();
							redSwitchOn = false;
							Block tmp = new Block(new Rectangle(b.x,b.y,50,50),tmpPic,9,0);
							objects.set(objects.indexOf(b),tmp);
							putBlock();//places all the red blocks
						}
					}
					if(b.imagePos == 10){//for blue switch
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
    public void changeSwitch(){//changes the switches for the player, exact same as the shadow switch, just checks for the player instead
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
    public void removeBlock(){//removes blocks controlled by a switch
    	ArrayList <Block> tmp = new ArrayList <Block>();
    	if (redSwitchOn == true){
    		for(Block b : rects){
    			if (b.imagePos == 11){//red disappearing block
    				tmp.add(b);//adds all the red blocks to a tmp list
    			}
    		}
    	}
    	if (blueSwitchOn == true){
    		for(Block b : rects){
    			if (b.imagePos == 12){//blue disappearing block
    				tmp.add(b);//adds all the blue blocks to a tmp list
    			}
    		}
    	}
    	for (Block b : tmp){
    		rects.remove(b);//removes the blocks from "rects" so that they cant be interacted with anymore
    		disappearedBlocks.add(b);//stores them in a seperate list so that the blocks can be brought back
    	}	
    }
	public void putBlock(){//places the blocks that have been removed back into the game
		ArrayList <Block> tmp = new ArrayList <Block>();
		if (redSwitchOn == false){
    		for(Block b : disappearedBlocks){
    			if (b.imagePos == 11){//for the red blocks
    				tmp.add(b);//adds them to a temporary list
    			}
    		}
    	}
    	if (blueSwitchOn == false){
    		for(Block b : disappearedBlocks){
    			if (b.imagePos == 12){//for the blue blocks
    				tmp.add(b);//adds them to a temporary list
    			}
    		}
    	}
    	for (Block b : tmp){
    		disappearedBlocks.remove(b);//removes them from the list of blocks that have been removes
    		rects.add(b);//puts them back into the game so they can be interacted with again
    	}
	}
	public void finishTransition(){//when the fade in is finished
		if (transitionShade == 0){
			gameStarting = false;
		}
	}    	
	public void move(){//moves the player
		guy.changePic();//changes pic for when the player walks/jumps/stands
		finishTransition();//checks when the transition is finished
		checkGameOver();//checks when the player reaches the end block
		movingBlock();//moves all the moving blocks
		moveShadow();//moves the shadows
		currentShadowPath.add(guy.speed);//recording the path for the next shadow
		guy.setSpeed(0);
		if(keys[KeyEvent.VK_RIGHT] ){//moving right
			if (guy.x < 790){//so he doesnt keep walking off the screen
				guy.side = "Right";//the direction the player will face when standing still
				guy.addX(2);//moves player
				guy.setSpeed(2);//the characters speed
				for (int i=0; i<rects.size(); i++){
				Rectangle ground = rects.get(i).rect;//so the player cant walk through blocks
					if (guy.body.intersects(ground) && guy.y >= ground.y && rects.get(i).deviation == 0 && rects.get(i).imagePos != 2){//the player is alowd to walk through block #2
						guy.addX(-2);//so the player stays still if hes running into a block from the side
					}				
				}
			}
		}
		if(keys[KeyEvent.VK_LEFT] ){//moving the player left
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
		if(keys[KeyEvent.VK_UP] && guy.onGround == true && vy == 0.7){//when the player jumps
			vy = -10.0;				
			guy.setOnGround(false);//so the player cant jump while jumping
			currentShadowPath.remove(currentShadowPath.size()-1);//removes the 0 that was added and replaces it with a jump command
			currentShadowPath.add(JUMP);
		}
		guy.addY(vy);//gravity
		for (int i=0; i<rects.size(); i++){//checks each rectangle for a collision
			Block b = rects.get(i);
			Rectangle ground = b.rect;
			if (guy.body.intersects(ground) && guy.y < ground.y + 30 && b.imagePos != 2 && vy > 0){//if the player fell onto to block or was already on it
				guy.setY(ground.y-20);//sets player standing on block
				vy = 0;//so gravity doesnt pull the player through
				guy.setOnGround(true);//player is on ground
				if(b.deviation > 0 ){//if the player is on a moving platform
					guy.addX(b.speed*2);//moves player with platform
				}
			}
			if(guy.body.intersects(ground) && guy.y > ground.y + 30 && b.imagePos != 2 && b.imagePos != 8){//if the player jumps into a block from underneath
				guy.setY(ground.y+50);//sets player underneath block
				vy = 0;//player falls						
			}
		}
		for (Shadow tmp : shadows){//checks if the player jumps on any shadows
			checkPSIntersect(guy,tmp);
		}
		vy += 0.7;//gravity is always acting on the guy
	}	
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }  
    public void keyReleased(KeyEvent e) {
        if(keys[KeyEvent.VK_SPACE]){//when the player makes a shadow
			makeShadow();
		}
		if(keys[KeyEvent.VK_A]){//when the player clicks a switch
			changeSwitch();
			currentShadowPath.add(ABUTTON);
			
		}
		if(keys[KeyEvent.VK_R]){//when the player wants to reset the level
			guy.side = "Right";
			resetLevel();
		}
        keys[e.getKeyCode()] = false;
    }   
    public void paintComponent(Graphics g){ //aints the contents of the game	
    	g.drawImage(back,0,0,this);  
		paintBlocks(g); 
		g.drawImage(guy.pic,(int)(guy.x),(int)(guy.y), null);//draws player		 
		for (Shadow tmp : shadows){//draws shadows
			if (tmp != null){
				g.drawImage(tmp.pic,(int)(tmp.x),(int)(tmp.y), null);			
			}
		}
		if(gameStarting){//fade in effect					
			g.setColor(new Color(20,20,20,(int)(transitionShade)));
			g.fillRect(0,0,800,600);
			transitionShade -= 0.5;
		}
		if(end){//fade out effect
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
	double x;//x pos
	double y;//y pos
	double velocity;//y velocity
	boolean onGround;
	Rectangle body;//body area
	public Point reset;
	public int speed;//the speed of the guy
	public Image pic;
	private Image standRight;
	private Image standLeft;
	private ArrayList <Image> rightWalk = new ArrayList<Image>();//right walking images
	private int picPos = 0;	
	private ArrayList <Image> jumpRight = new ArrayList<Image>();//jumping to the right pictures
	private int jumpPos = 0;
	private ArrayList <Image> leftWalk = new ArrayList<Image>();//left walking images
	private ArrayList <Image> jumpLeft = new ArrayList<Image>();//jumping to the left pictures
	public String side = "Right";
	private String[] walkNoise = {"walk1","walk2","walk3","walk4"};//multiple walking noises
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
		for(int i=1;i<8;i++){//loads all the walking pictures multiple times to make the walking effect more realistic
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
	public void changeWalkTimer(){//controls the walk noise to make it realistic
		if(walkTimer == 12){
			walkTimer = 0;
		}
	}
	public void changePic(){
		if(onGround == true){//only can walk while on the ground
			changeWalkTimer();
			jumpPos = 0;
			if(speed == 2){//walking to the right
				pic = rightWalk.get(picPos%rightWalk.size());
				picPos++;
				walkTimer ++;
				walk();
			}
			if(speed == -2){//walking left
				pic = leftWalk.get(picPos%leftWalk.size());
				picPos++;
				walkTimer ++;
				walk();
			}
			if(speed == 0 && side.equals("Right")){//standing faceing right
				pic = standRight;
				picPos = 0;
				walkTimer = 0;
			}
			if(speed == 0 && side.equals("Left")){//standing facing left
				pic = standLeft;
				picPos = 0;
				walkTimer = 0;
			}			
		}
		else{//jumping
			if(speed == 2){//juming to the right
				pic = jumpRight.get(jumpPos%jumpRight.size());
				jumpPos ++;
			}
			if(speed == -2){//jumping to the left
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
		pic = new ImageIcon("images/sprites/stand1_right_shadow.png").getImage();
		standRight = pic;
		standLeft = new ImageIcon("images/sprites/stand1_left_shadow.png").getImage();
		for(int i=1;i<8;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/walk" + i + "_right_shadow.png").getImage();
				rightWalk.add(tmp);	
			}
		}
		for(int i=1;i<4;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/jump" + i + "_right_shadow.png").getImage();
				jumpRight.add(tmp);	
			}
		}
		for(int i=1;i<8;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/walk" + i + "_left_shadow.png").getImage();
				leftWalk.add(tmp);	
			}
		}
		for(int i=1;i<4;i++){
			for(int x = 0; x<5 ;x++){
				Image tmp = new ImageIcon("images/sprites/jump" + i + "_left_shadow.png").getImage();
				jumpLeft.add(tmp);	
			}
		}
	}
	public void move(){
		switchPress = false;
		jump = false;
		if(pos < path.size()){
			int tmp = path.get(pos);
			if(tmp != 4 && tmp != 5){//4 and 5 are comands for switch and jump
				addX(tmp);
				speed = tmp;
				if(x>790){//cant go past the screen
					x -=2;
				}
				if(x<0){
					x +=2;
				}
			}
			if(tmp == 4){//jump
				jump = true;
			}
			if(tmp == 5){//switch press
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