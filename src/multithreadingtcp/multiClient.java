/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithreadingtcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author Mushfika
 */
public class multiClient {

    public static void main(String[] args) {

        try {

            Socket clientSocket = new Socket("localhost", 7899);

            System.out.println("Write login enter username & password with space");
            BufferedReader bfUser;
            String fromUser = null;
            boolean flag = false;
            bfUser = new BufferedReader(new InputStreamReader(System.in));

            while (!(fromUser = bfUser.readLine()).equals("exit")) {
                //System.out.println(fromUser);

                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                if (flag) {
                    //dataOutputStream.writeBytes(fromUser + "\n");
                    fromUser += " 1";
                }

                dataOutputStream.writeBytes(fromUser + "\n");
                BufferedReader bdf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                fromUser = bdf.readLine();
                System.out.println(fromUser);
        //fromUser = null;

                if (fromUser.equals("ok")) {
                    flag = true;
                    System.out.println("Write mkdir space name for creating directory");
                    System.out.println("Write cd space path for changing directory");
                    System.out.println("Write upload space source file space filename.extension for uploading file");
                    System.out.println("Write download space sourcefilename.extension space downloadfilename.extension for uploading file");
                }

            }
            clientSocket.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
