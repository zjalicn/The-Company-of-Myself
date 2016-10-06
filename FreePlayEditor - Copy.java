//Nikola Zjalic & Milosh Zelembaba
//FreePlayEditor.java

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class FreePlayEditor extends JFrame implements MouseMotionListener,MouseListener,MouseWheelListener,ActionListener{
	private int mouseX,mouseY,prevMouseX,prevMouseY;//mouse position
	private ImageIcon pic;
	private Image dbImage;
	private JLabel backGround;
	private Graphics dbg;
	private Image back;
	private Image sidebar;
	private PrintWriter outFile;
	private Scanner inFile;
	private String[] line; //line of information when reading from inFile		
	private Block blocks[][];//all the blocks currently on the screen
	private int blockImgPos = 0; //current block selected
	private ArrayList<Image> blockImgs = new ArrayList<Image> (); //list of al the possible block images
	private boolean onScreen;
	private boolean startBlockPlaced;
	private boolean endBlockPlaced;
	private boolean redSwitchPlaced;
	private boolean blueSwitchPlaced;
	private boolean movingBlock;
	private Block movingBlockReferencePoint;
	private int dist;
	public boolean startGame = false;
	public boolean playing = false;
	JButton importButton;
	JButton exportButton;
	JButton saveButton;
	JButton playButton;
	JTextField fileNameField;
	public String levelName = "";
	
	public FreePlayEditor(){
		super ("Level Editor");
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setSize(1000,587);
		addMouseListener(this);//these two pay keep track of all the mouse info
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		//background pic
		back = new ImageIcon("images/sky.jpg").getImage();
		pic = new ImageIcon("images/sky.jpg");
		sidebar = new ImageIcon("images/sidebar.png").getImage();
		backGround = new JLabel(pic);
		backGround.setSize(800, 600);
		backGround.setLocation(0,0);
		add(backGround);
		
		importButton = new JButton("Import");
		importButton.setSize(100,50);
		importButton.setLocation(844,150);
		importButton.addActionListener(this);
		add(importButton);
		
		playButton = new JButton("Play Now");
		playButton.setSize(100,50);
		playButton.setLocation(844,500);
		playButton.addActionListener(this);
		add(playButton);
		
		exportButton = new JButton("Export");
		exportButton.setSize(100,50);
		exportButton.setLocation(844,250);
		exportButton.addActionListener(this);
		add(exportButton);

		fileNameField = new JTextField();
		fileNameField.setSize(100,50);
		fileNameField.setLocation(844,450);
		fileNameField.addActionListener(this);
		add(fileNameField);
		
		dist = 0;
		startBlockPlaced = false;
		endBlockPlaced = false;
		redSwitchPlaced = false;
		blueSwitchPlaced = false;
		movingBlock = false;
		loadBlockImgs();
		blocks = new Block[11][16];
		setVisible(false);
	}
	public void importLevelData(){ //few minor errors but works for the most part, have to make it recognize the start and end blocks, etc.
		try{
			levelName = fileNameField.getText();			
		 	inFile= new Scanner(new BufferedReader(new FileReader(levelName + ".txt")));
		 	blocks = new Block[11][16];
		 	while(inFile.hasNextLine()){
		 		line = inFile.nextLine().split(" ");
		 		if(Integer.parseInt(line[2]) == 5000){
		 			Image tmpPic =  new ImageIcon("images/movingArea.png").getImage();
		 			Block tmp = new Block(new Rectangle(Integer.parseInt(line[0])+6, Integer.parseInt(line[1]), 50, 50), tmpPic, 5000, 0);
		 			addBlock(tmp, Integer.parseInt(line[0])+6, Integer.parseInt(line[1]));
		 		}
		 		else{
		 			Block tmp = new Block(new Rectangle(Integer.parseInt(line[0])+6, Integer.parseInt(line[1]), 50, 50), blockImgs.get(Integer.parseInt(line[2])), Integer.parseInt(line[2]), Integer.parseInt(line[3]));
		 			addBlock(tmp, Integer.parseInt(line[0])+6, Integer.parseInt(line[1]));
		 		}	
		 	}
		}
		catch(IOException ex){
			System.out.println("Couldn't open file " + fileNameField.getText() + ".txt");
		}
	}
	
	public void exportLevelData() {
		try{
			levelName = fileNameField.getText();		
		 	outFile= new PrintWriter(new BufferedWriter (new FileWriter (levelName + ".txt")));
		}
		catch(IOException ex){
			System.out.println("Couldn't save file " + levelName + ".txt");
		}		
		for(int y=0; y<11 ; y++){
			for(int x=0; x<16 ;x++){
				// the -6 is to compensate for the border
				Block b = blocks[y][x];
				if(b!=null){
					outFile.println(b.x-6 + " " + b.y + " " + b.imagePos + " " + b.deviation);
				}
			}
		} 
		outFile.close();		
	}  
	
	public void actionPerformed (ActionEvent evt) {
		JButton clicked = (JButton) evt.getSource();
		if (clicked == importButton){
			importLevelData();
		}
		if (clicked == exportButton){
			if(endBlockPlaced == false){
				System.out.println("you must place an end block");
			}
			if(startBlockPlaced == false){
				System.out.println("you must place a start block");
			}
			if(startBlockPlaced == true && endBlockPlaced == true){
				exportLevelData();
				System.out.println("Level name:" + levelName);
			}
		}
		if(clicked == playButton){
			if(levelName.equals("")){
				System.out.println("You must name and save the level first");
			}
			else{
				startGame = true;
				playing = true;				
			}
		}

	}
	
	public void loadBlockImgs(){
        blockImgs.add(new ImageIcon("images/grass.jpg").getImage());
		blockImgs.add(new ImageIcon("images/dirt.png").getImage());
		blockImgs.add(new ImageIcon("images/orangeBox.png").getImage());
		blockImgs.add(new ImageIcon("images/blueBox.png").getImage());
		blockImgs.add(new ImageIcon("images/support.png").getImage());
		blockImgs.add(new ImageIcon("images/support2.png").getImage());
		blockImgs.add(new ImageIcon("images/startBlock.png").getImage());
		blockImgs.add(new ImageIcon("images/endBlock.png").getImage());
		blockImgs.add(new ImageIcon("images/movingPlatform.png").getImage());
		blockImgs.add(new ImageIcon("images/redSwitchOff.png").getImage());
		blockImgs.add(new ImageIcon("images/blueSwitchOff.png").getImage());
		blockImgs.add(new ImageIcon("images/redDisappearingBlock.png").getImage());
		blockImgs.add(new ImageIcon("images/blueDisappearingBlock.png").getImage());
	}
   	public void paintHollowRectangle(int x, int y, int l, int w, int RED, int GREEN, int BLUE,Graphics g){//paints a rectangle with thickness 3
    	g.drawRect(x,y,l,w);
    	g.fillRect(x, y, 50, 3); //top border
	    g.fillRect(x+47, y, 3, 50); //right border
	    g.fillRect(x, y+47, 50, 3); //bottom border
	    g.fillRect(x, y, 3, 50); //left border   	
    }
    public void drawGrid(Graphics g){
	    for (int x=6; x<800; x+=50){//paints the grid
			for (int y=30; y<600; y+=50){
    			g.drawRect(x,y,50,50);
    			Rectangle box = new Rectangle(x,y,50,50);//makes a rectangle at every grid spot
				Point mouse = new Point(mouseX,mouseY);
				if (box.contains(mouse)){//if the mouse is inside that rectangle
					paintHollowRectangle(x,y,50,50,50,50,50,g);//draw the rect with a thicker border										
				} 
			}
    	}	
    }
    public void paintBlocks(Graphics g){
    	for(int y=0; y<11 ;y++){//paints the blocks
    		for(int x=0; x<16 ;x++){
    			Block b = blocks[y][x];
    			if (b != null){
     				g.drawImage(b.img, b.x, b.y, null);	 
    			}  			
    		}
		}
    } 
    public void paint(Graphics g){
    	dbImage = createImage (800,600);//double buffered image
    	dbg = dbImage.getGraphics ();
    	dbg.setColor(new Color(50,50,50));
    	dbg.drawImage(back,0,0,this);//paints background image
    	if (onScreen == true){//only paints grid when mouse is on the window
	  		drawGrid(dbg);
    	}
    	paintBlocks(dbg);
		g.drawImage(dbImage,0,0,this);//paints the final image
		
    }
	public static void main (String [] args){
		FreePlayEditor game = new FreePlayEditor();
		while (true){
			game.repaint();
			try{
				Thread.sleep (10);
			}
			catch (InterruptedException ex){
				System.out.println(ex);
			}
		}
	}
	public void addBlock(Block tmp,int posX, int posY){
		int x = (posX-6)/50;
		int y = (posY-30)/50;
		if(blockImgPos == 6 && startBlockPlaced == false){//so only one StartBLock can be on the board
			startBlockPlaced = true;
			blocks[y][x] = tmp;
		}
		if(blockImgPos == 7 && endBlockPlaced == false){//so only one EndBlock can be on the board
			endBlockPlaced = true;
			blocks[y][x] = tmp;
		}
		if(blockImgPos == 9 && redSwitchPlaced == false){//so only one redSwitch can be on the board
			redSwitchPlaced = true;
			blocks[y][x] = tmp;
		}
		if(blockImgPos == 10 && blueSwitchPlaced == false){//so only one BlueSwitch can be on the board
			blueSwitchPlaced = true;
			blocks[y][x] = tmp;;
		}
		if(blockImgPos == 8 && movingBlock == false){//to place the moving platform
			movingBlock = true;
			movingBlockReferencePoint = tmp;
			blocks[y][x] = tmp;
		}
		if(blockImgPos == 8 && movingBlock == true){//places the area the block can move
			blocks[y][x] = tmp;
		}
		if(blockImgPos < 6 || blockImgPos > 10){//any other block
			blocks[y][x] = tmp;
		}	
	}
	public void removeBlock(int posX, int posY){
		Block tmp = blocks[(posY-30)/50][(posX-6)/50];
		if(tmp.imagePos == 6){//start block removed
			startBlockPlaced = false;
		}
		if(tmp.imagePos == 7){//end block removed
			endBlockPlaced = false;
		}
		if(tmp.imagePos == 9){//start block removed
			redSwitchPlaced = false;
		}
		if(tmp.imagePos == 10){//end block removed
			blueSwitchPlaced = false;
		}
		if(tmp.imagePos == 8){
			int dev = tmp.deviation;
			System.out.println(dev);
			for (int i= -1*dev ; i<dev+1; i++){
				blocks[(posY-30)/50][((50*i+posX)-6)/50] = null;
			}
		}
		if(movingBlock == true && tmp.imagePos == 5000){
			blocks[(posY-30)/50][(posX-6)/50] = null;//removes block			
		}
		if(tmp.imagePos != 8 && tmp.imagePos != 5000){
			blocks[(posY-30)/50][(posX-6)/50] = null;//removes block			
		}
	}
	
// MOUSE LISTENER STUFF--------------------------------------------	
	public void mouseEntered(MouseEvent e) {
		onScreen = true;
	}
    public void mouseExited(MouseEvent e) {//when the mouse goes off the screen, it gives a preveiw of how the level will look
    	onScreen = false;
    }
    public void mouseReleased(MouseEvent e) {//when a button is released
    	if (e.getButton() == MouseEvent.BUTTON1){//left button
    		if (movingBlock == true){
    			int x = (movingBlockReferencePoint.x-6)/50;
				int y = (movingBlockReferencePoint.y-30)/50;
    			movingBlock = false;
    			Rectangle box = new Rectangle(movingBlockReferencePoint.x,movingBlockReferencePoint.y,50,50);
				Block tmp = new Block(box, blockImgs.get(8), 8, dist);
    			blocks[y][x] = tmp;
    			dist = 0;
    		}
    		else{
    			int x = ((mouseX-6)/50)*50+6;
    			int y = ((mouseY-30)/50)*50+30;
				Rectangle box = new Rectangle(x,y,50,50);
				Block tmp = new Block(box, blockImgs.get(blockImgPos), blockImgPos, 0);
				addBlock(tmp,mouseX,mouseY);				
    		}		
    	}
    	if (e.getButton() == MouseEvent.BUTTON3){//right button
    		if(blocks[(mouseY-30)/50][(mouseX-6)/50] != null){
    			removeBlock(mouseX,mouseY);	
    		}	
    	} 		
    }    
    public void mouseClicked(MouseEvent e){}  
    	 
    public void mousePressed(MouseEvent e){
		mouseX = e.getX();
		mouseY = e.getY();			
	}
// MOUSE MOTION LISTENER STUFF-------------------------------------
	public void mouseDragged(MouseEvent e){}
    public void mouseMoved(MouseEvent e){
    	mouseX = e.getX();
		mouseY = e.getY();
    }	
    	
//MOUSE WHEEL LISTENER -----------------------------------------------
	public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (movingBlock == false){
	        if (notches < 0) { //notches decreases if the wheel is scrolled forward
	        	if (blockImgPos+1 < blockImgs.size()){
		            blockImgPos++;
	        	}
	        } 
	        else { //increases when scrolled towards user
				if (blockImgPos-1 > -1){
	            	blockImgPos--;
				}
	        }
	        getGraphics().fillRect(875,50,50,50);
	        getGraphics().drawImage(blockImgs.get(blockImgPos), 875, 50, null);	
	    }
	    else{//moving block mode
			if (notches < 0) { //notches equals -1 if the wheel is scrolled forward
	        	if(dist < 3){
	        		dist++;
	        		
	        		int tmpX1 = movingBlockReferencePoint.x-50*dist;
					int tmpY1 = movingBlockReferencePoint.y;
					int tmpX2 = movingBlockReferencePoint.x+50*dist;
					int tmpY2 = movingBlockReferencePoint.y;

		        	Rectangle boxLeft = new Rectangle(tmpX1,tmpY1,50,50);
		        	Image tmpPic =  new ImageIcon("images/movingArea.png").getImage();
					Block tmpLeft = new Block(boxLeft,tmpPic, 5000, 0);//the 5000 doesnt change anything here
					
					Rectangle boxRight = new Rectangle(tmpX2,tmpY2,50,50);
					Block tmpRight = new Block(boxRight,tmpPic, 5000, 0);
					 
					if (blocks[(tmpY1-30)/50][(tmpX1-6)/50] == null && blocks[(tmpY2-30)/50][(tmpX2-6)/50] == null){
						addBlock(tmpRight,tmpX1,tmpY1);
						addBlock(tmpLeft,tmpX2,tmpY2);
					} 
					else{
						dist--;
					}
	        	}
	        } 
	        else { //equals 1 when scrolled towards user
				if (dist > 0){
	            	dist--;
					removeBlock(movingBlockReferencePoint.x-50*(dist+1),movingBlockReferencePoint.y);
					removeBlock(movingBlockReferencePoint.x+50*(dist+1),movingBlockReferencePoint.y);
				}
	        }
	    }
	}
	
}

class Block{//custon block object so we can keep track of more info than the Rectangle object can
	public int x;
	public int y;
	public int width;
	public int height;
	public Rectangle rect;
	public Image img;
	public int imagePos;
	public int deviation;
	public int changeDirectionXRight;
	public int changeDirectionXLeft;
	public int speed;
	public Point reset;
	
	public Block(Rectangle r, Image i, int p, int d){
		rect = r;
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
		img = i;
		imagePos = p;
		deviation = d;
		changeDirectionXRight = x+d*50;
		changeDirectionXLeft = x-d*50;
		speed = 1;
		reset = new Point(x,y);
	}
	public void addY(int num){//changes the y position and updates the box around the guy
		y+=num;
		rect = new Rectangle(x,y,50,50);
	}
	public void addX(int num){//changes the x position and updates the box around the guy
		x+=num;
		rect = new Rectangle(x,y,50,50);
	}
	public void setY(int num){//changes the y position and updates the box around the guy
		y=num;
		rect = new Rectangle(x,y,50,50);
	}
	public void setX(int num){//changes the x position and updates the box around the guy
		x=num;
		rect = new Rectangle(x,y,50,50);
	}
	public void reset(){
		setX(reset.x);
		setY(reset.y);
		speed = 1;
	}
}