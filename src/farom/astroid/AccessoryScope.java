package farom.astroid;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;



import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;


public class AccessoryScope extends Scope {
	
	
	UsbAccessory accessory;
	UsbManager manager;
	ParcelFileDescriptor  fileDescriptor;
	FileInputStream inputStream;
	FileOutputStream outputStream;
	
	public AccessoryScope(UsbManager manager, UsbAccessory accessory) {
		super();
		this.accessory=accessory;
		this.manager=manager;
	}
	
	@Override
	public void connect(){
		log("Connection en mode accessoire ...");
		log("");
		log("Info sur l'accessoire:");
		try{
			log(" - fabricant : "+accessory.getManufacturer());
			log(" - modèle : "+accessory.getModel());
			log(" - version : "+accessory.getVersion());
			log(" - description : "+accessory.getDescription());
			log(" - n° de série : "+accessory.getSerial());
			log(" - URL : "+accessory.getUri());
			log("");
			
			if (!manager.hasPermission(accessory)) {
				log("Erreur : Acces refusé");
				return;
			}
			
			log("Acces autorisé");
			
		    fileDescriptor = manager.openAccessory(accessory);
		    if (fileDescriptor != null) {
		        FileDescriptor fd = fileDescriptor.getFileDescriptor();
		        inputStream = new FileInputStream(fd);
		        outputStream = new FileOutputStream(fd);
		    }
		    else{
		    	log("Erreur : Impossible d'ouvrir le périphérique");
		    }
		} catch(Exception e){
			log(e.getMessage());
		}
		
		
		
		
	}
	
	@Override
	protected void sendCommand(String command){		
		try {			
			outputStream.write(command.getBytes());
			log("Send : " + command);
			
		} catch (IOException e) {
			log("Erreur lors de l'envoi de la commande : "+command);
			log(e.getMessage());
		}
		
		
	}
	
	@Override
	public void setLogBox(LogTextBox logBox){
		mLogBox=logBox;
		log("Log ");
		log("Type : AccessoryScope");
		
	}

}
