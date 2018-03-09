package Auth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import Auth.commands.Login;
import Auth.commands.Register;
import SpoutSDK.CraftDamageType;
import SpoutSDK.CraftDirectionNESWUD;
import SpoutSDK.CraftEntity;
import SpoutSDK.CraftEntityType;
import SpoutSDK.CraftEventInfo;
import SpoutSDK.CraftHand;
import SpoutSDK.CraftLocation;
import SpoutSDK.CraftPlayer;
import SpoutSDK.CraftServer;
import SpoutSDK.ModBase;
import SpoutSDK.ModInfo;
import SpoutSDK.SpoutHelper;

public class Main extends ModBase {
	public static String modName = "Auth";
	public static String version = "1.0.0";
	public static File dataFile = new File("mods/Auth/logins.dat");
	// Variables set in config file
	public static String blockMessage;
	public static String registerMessage;
	public static String loginMessage;	
	public static Integer blockAfter;
	public static List<String> cannotJoin;
	// Storing data
	public static List<String> usersLoggedIn = new ArrayList<>();
	public static List<String> blocked = new ArrayList<>();
	public static Map<String, Integer> attempts = new ConcurrentHashMap<>();
	public static Map<String, String> passwords = new ConcurrentHashMap<>();
	
	public void onStartup(CraftServer argServer) {
		System.out.println("[Auth/INFO]: Auth version " + version + " starting up...");
		Config.readConfig();
		loadData();
		SpoutHelper.getServer().registerCommand(new Login());
		SpoutHelper.getServer().registerCommand(new Register());
	}
	
	public void onShutdown() {
		saveData();
	}
	
    public ModInfo getModInfo() {
		ModInfo info = new ModInfo();
		info.description = "Account passwords for extra security (" + version + ")";
		info.name = modName;
		info.version = version;
		info.eventSortOrder = -11111D;
		return info;
    }
    
    public void onPlayerJoin(CraftPlayer plr) {
    	ensureAttempts(plr);
    	if(passwords.containsKey(plr.getUUID().toString())) {
    		plr.sendMessage(loginMessage);
    	} else {
    		plr.sendMessage(registerMessage);
    	}
    }
    
    public void onPlayerLogout(String playerName, UUID uuid) {
    	usersLoggedIn.remove(uuid.toString());
    }
    
    public void onAttemptPlayerMove(CraftPlayer plr, CraftLocation locFrom, CraftLocation locTo, CraftEventInfo ei) {
    	if(!(usersLoggedIn.contains(plr.getUUID().toString()))) {
    		ei.isCancelled = true;
    	}
    }
    
    public void onPlayerInput(CraftPlayer plr, String msg, CraftEventInfo ei) {
    	if(!(usersLoggedIn.contains(plr.getUUID().toString()))) {
    		if(!(msg.startsWith("/login") || msg.startsWith("/register"))) {
    			ei.isCancelled = true;
    		}
    	}
    }
    
    public void onAttemptBlockBreak(CraftPlayer plr, CraftLocation loc, CraftEventInfo ei) {
    	if(!(usersLoggedIn.contains(plr.getUUID().toString()))) {
    		ei.isCancelled = true;
    	}
    }
    
    public void onAttemptPlaceOrInteract(CraftPlayer plr, CraftLocation loc, CraftDirectionNESWUD dir, CraftHand hand, CraftEventInfo ei) {
    	if(!(usersLoggedIn.contains(plr.getUUID().toString()))) {
    		ei.isCancelled = true;
    	}
    }
    
    public void onAttemptEntityDamage(CraftEntity ent, CraftDamageType dmgType, double amt, CraftEventInfo ei) {
    	if(ent.getType().equals(CraftEntityType.PLAYER)) {
    		if(!(usersLoggedIn.contains(ent.getUUID().toString()))) {
    			ei.isCancelled = true;
    		}
    	}
    }
    
    public void ensureAttempts(CraftPlayer plr) {
    	if(!(attempts.containsKey(plr.getUUID().toString()))) {
    		attempts.put(plr.getUUID().toString(), 0);
    	}
    }
    
    public void saveData() {
		try {
            final FileOutputStream f = new FileOutputStream(dataFile);
            final ObjectOutputStream s = new ObjectOutputStream(new BufferedOutputStream(f));
            s.writeObject(passwords);
            s.close();
            System.out.println("[Auth/INFO]: Successfully saved passwords to file.");
        } catch (Throwable exc) {
            System.out.println("[Auth/ERROR]: Could not save passwords to file.");
            System.out.println("[Auth/ERROR]: " + exc);
        }
    }
    
    @SuppressWarnings("unchecked")
	public void loadData() {
		try {
			final FileInputStream f = new FileInputStream(dataFile);
			final ObjectInputStream s = new ObjectInputStream(new BufferedInputStream(f));
			Main.passwords = (ConcurrentHashMap<String, String>) s.readObject();
			s.close();
            System.out.println("[Auth/INFO]: Successfully loaded passwords from file.");
        } catch (Throwable exc) {
            System.out.println("[Auth/ERROR]: Could not load passwords from file.");
            System.out.println("[Auth/ERROR]: " + exc);
		}
    }
    
    public static String encodeHex(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
