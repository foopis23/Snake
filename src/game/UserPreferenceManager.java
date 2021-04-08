package game;

import java.io.*;

public class UserPreferenceManager
{
    public static final String saveLoc = "userInfo.snake";
    public static UserPreferences USER_PREFERENCES;

    public static void loadUserInfo()
    {
        UserPreferences u = null;
        try {
            FileInputStream fileIn = new FileInputStream(saveLoc);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            u = (UserPreferences) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
        }

        USER_PREFERENCES = u;

        if (USER_PREFERENCES == null) {
            USER_PREFERENCES = new UserPreferences();
            USER_PREFERENCES.createUserInfo();
            saveUserInfo();
        }
    }

    public static void saveUserInfo()
    {
        try {
            FileOutputStream fileOut = new FileOutputStream(saveLoc);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(USER_PREFERENCES);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
