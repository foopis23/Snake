package game;

import java.io.*;

public class InfoFileHandler
{
    public static final String saveLoc = "userInfo.snake";

    public static UserInfo loadUserInfo()
    {
        UserInfo u = null;
        try {
            FileInputStream fileIn = new FileInputStream(saveLoc);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            u = (UserInfo) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
        }

        return u;
    }

    public static void saveUserInfo(UserInfo u)
    {
        try {
            FileOutputStream fileOut = new FileOutputStream(saveLoc);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(u);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
