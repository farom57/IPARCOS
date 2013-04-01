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
	}
	
	public void connect()
	{
		mLogBox.append("Connection ...");
	}
	
	public void moveNorth()
	{
		sendCommand("Mn");
	}
	
	public void moveSouth()
	{
		sendCommand("Ms");
	}
	
	public void moveEast()
	{
		sendCommand("Me");
	}
	
	public void moveWest()
	{
		sendCommand("Mw");
	}
	
	public void stopMove()
	{
		sendCommand("Q");
	}

	protected String sendCommand(String command)
	{
		mLogBox.append("Send : " + command);
		return "";
	}

	
	
}
