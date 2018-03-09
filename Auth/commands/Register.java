package Auth.commands;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import Auth.Main;
import SpoutSDK.ChatColor;
import SpoutSDK.CraftCommand;
import SpoutSDK.CraftPlayer;

public class Register implements CraftCommand {

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		return "register";
	}

	@Override
	public String getHelpLine(CraftPlayer plr) {
		return ChatColor.GOLD + "/register " + ChatColor.WHITE + "--- Set your account password";
	}

	@Override
	public List<String> getTabCompletionList(CraftPlayer plr, String[] args) {
		return null;
	}

	@Override
	public void handleCommand(CraftPlayer plr, String[] args) {
		if(hasPermissionToUse(plr)) {
			if(Main.passwords.containsKey(plr.getUUID().toString()) && (!Main.usersLoggedIn.contains(plr.getUUID().toString()))) {
				plr.sendMessage(ChatColor.RED + "[Auth] Nice try.");
			} else {
				if(args[0].isEmpty()) {
					plr.sendMessage(ChatColor.RED + "Usage: /register (password)");
					return;
				} else {
					try {
						String hashedPassword = Main.encodeHex(MessageDigest.getInstance("SHA-256").digest(args[0].getBytes(StandardCharsets.UTF_8)));
						Main.passwords.put(plr.getUUID().toString(), hashedPassword);
						Main.usersLoggedIn.add(plr.getUUID().toString());
						plr.sendMessage(ChatColor.GOLD + "[Auth] You are now registered.");
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
						plr.kick(ChatColor.RED + "[Auth] An exception occurred hashing your password.");
					}
				}
			}
		} else {
			plr.sendMessage(ChatColor.RED + "You do not have permission for that command.");
		}
		
	}

	@Override
	public boolean hasPermissionToUse(CraftPlayer plr) {
		return plr.hasPermission("auth.register");
	}

}
