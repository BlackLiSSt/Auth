package Auth.commands;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import Auth.Main;
import SpoutSDK.ChatColor;
import SpoutSDK.CraftCommand;
import SpoutSDK.CraftPlayer;

public class Login implements CraftCommand {

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		return "login";
	}

	@Override
	public String getHelpLine(CraftPlayer plr) {
		return ChatColor.GOLD + "/login " + ChatColor.WHITE + "--- Login with your password";
	}

	@Override
	public List<String> getTabCompletionList(CraftPlayer plr, String[] args) {
		return null;
	}

	@Override
	public void handleCommand(CraftPlayer plr, String[] args) {
		if(!(Main.blocked.contains(plr.getIPAddress()))) {
			if(hasPermissionToUse(plr)) {
				if(Main.passwords.containsKey(plr.getUUID().toString()) && (!(Main.usersLoggedIn.contains(plr)))) {
					if(!(args[0].isEmpty())) {
						try {
							String hashedPassword = Main.encodeHex(MessageDigest.getInstance("SHA-256").digest(args[0].getBytes(StandardCharsets.UTF_8)));
							if(hashedPassword.equals(Main.passwords.get(plr.getUUID().toString()))) {
								Main.usersLoggedIn.add(plr.getUUID().toString());
								plr.sendMessage(ChatColor.GOLD + "[Auth] You are now logged in.");
							} else {
								Main.attempts.put(plr.getUUID().toString(), Main.attempts.get(plr.getUUID().toString()) + 1);
								plr.sendMessage(ChatColor.RED + "[Auth] Incorrect password.");
								if(Main.attempts.get(plr.getUUID().toString()) >= Main.blockAfter) {
									Main.blocked.add(plr.getIPAddress());
									plr.kick(Main.blockMessage);
									System.out.println("[Auth/WARN]: Blocking " + plr.getName() + "...");
								}
							}
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
							plr.kick(ChatColor.RED + "[Auth] An exception occurred hashing your input.");
						}
					} else {
						plr.sendMessage(ChatColor.RED + "Usage: /login (password)");
					}
				} else if(Main.usersLoggedIn.contains(plr)) {
					plr.sendMessage(ChatColor.RED + "[Auth] You are already logged in.");
				} else {
					plr.sendMessage(ChatColor.RED + "[Auth] You are not registered.");
				}
			} else {
				plr.sendMessage(ChatColor.RED + "You do not have permission for that command.");
			}
		} else {
			plr.sendMessage(ChatColor.RED + "You are blocked from login attempts.");
		}
	}

	@Override
	public boolean hasPermissionToUse(CraftPlayer plr) {
		return plr.hasPermission("auth.login");
	}
}
