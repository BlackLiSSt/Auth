package Auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;

import Auth.Main;

public class Config {
	static Properties prop = new Properties();
    static OutputStream output = null;
    public static File config = new File("mods/Auth/config.properties");

    public static boolean readConfig() {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            try {
                input = new FileInputStream(config);
            } catch (FileNotFoundException e) {
                createConfig();
                return false;
            }

            prop.load(input);

            Main.blockMessage = prop.getProperty("block-message").replace("&", "§");
            Main.registerMessage = prop.getProperty("register-message").replace("&", "§");
            Main.loginMessage = prop.getProperty("login-message").replace("&", "§");
            Main.blockAfter = Integer.parseInt(prop.getProperty("attempts-until-block"));
            Main.cannotJoin = Arrays.asList(prop.getProperty("banned-account-names").split(","));
            Main.blocked.add("0.0.0.0");

        } catch (IOException ex) {
            System.out.println("[Auth/WARN]: Disabled! Configuration error. " + ex.getMessage());
        }
        try {
            input.close();
        } catch (IOException e) {
        	System.out.println("[Auth/WARN]: Disabled! Configuration error. " + e.getMessage());
            return false;
        }
        System.out.println("[Auth/INFO]: Config: OK");
        return true;
    }

    public static void createConfig() {
        try {
            config.getParentFile().mkdirs();

            output = new FileOutputStream(config);

            prop.setProperty("block-message", "&cYou have made too many login attempts.");
            prop.setProperty("register-message", "&cWelcome! Please register with /register (password)");
            prop.setProperty("login-message", "&cWelcome back! Please login with /login (password)");
            prop.setProperty("attempts-until-block", "3");
            prop.setProperty("banned-account-names", "notch,_jeb,SethBling");

            prop.store(output, "Auth Configuration");

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                    System.out.println("[Auth/INFO]: Configuration file created.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
