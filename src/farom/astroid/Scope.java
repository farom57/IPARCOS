package farom.astroid;



public class Scope {

	protected int speed;
	protected LogTextBox mLogBox;
	
	public Scope(LogTextBox logBox)
	{
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
