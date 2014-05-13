package farom.astroid;



public class Scope {

	protected int speed=1;
	protected LogTextBox mLogBox;
	protected static Scope lastInstance=null;
	
	public Scope()	{
		lastInstance=this;
		
	}
	
	public static Scope getLastInstance(){
		if(lastInstance==null)
			new Scope();
		return lastInstance;
	}
	
	public void setLogBox(LogTextBox logBox){
		mLogBox=logBox;
		log("Log ");
		log("Type : Scope");
	}
	
	public void connect()
	{
		log("Connection ...");
	}
	
	public void moveNorth()
	{
		sendCommand(":Mn#");
	}
	
	public void moveSouth()
	{
		sendCommand(":Ms#");
	}
	
	public void moveEast()
	{
		sendCommand(":Me#");
	}
	
	public void moveWest()
	{
		sendCommand(":Mw#");
	}
	
	public void stopMove()
	{
		sendCommand(":Q#");
	}
	
	public void speedUp()
	{
		if(speed < 4)
			speed++;
		setSpeed();
	}
	
	public void speedDown()
	{
		if(speed > 1)
			speed--;
		setSpeed();
	}
	
	protected void setSpeed()
	{
		switch(speed){
		case 1: 
			sendCommand(":RG#");
			break;
		case 2: 
			sendCommand(":RC#");
			break;
		case 3: 
			sendCommand(":RM#");
			break;
		case 4: 
			sendCommand(":RS#");
			break;
		}
	}
	
	public String getSpeedName(){
		switch(speed){
		case 1: 
			return "Guidage";
		case 2: 
			return "Centrage";
		case 3: 
			return "Recherche";
		case 4: 
			return "Max";
		}
		return "???";
	}
	

	protected void sendCommand(String command)
	{
		log("Send : " + command);

	}
	
	public void log(String text){
		mLogBox.append(text);
	}

	
	
}
