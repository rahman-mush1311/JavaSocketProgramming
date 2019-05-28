/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithreadingtcp;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;

/**
 *
 * @author Mushfika
 */
public class MultithreadingTCP {

    public static void main(String[] args) {

        try {
            ServerSocket welcomeSocket = new ServerSocket(7899);
            Socket connectionSocket = null;

            while ((connectionSocket = welcomeSocket.accept()) != null) {

                Thread clientHandler = new Thread(new clientHandler(connectionSocket));
                clientHandler.start();
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}

class clientHandler implements Runnable {

    Socket connectionSocket = null;

    clientHandler(Socket _clientSocket) {
        connectionSocket = _clientSocket;
    }

    @Override
    public void run() {
        try {
            String clientSentence = null;
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            while ((clientSentence = inFromClient.readLine()) != null) {
                String response = null;
                //as user is inputting with space so it is splitted and stores in a string of array.
                String[] arrOfStr = clientSentence.split(" ");
                //various condition is written user gives command such as login or upload or anything stored in first index of the array.
                
                if (arrOfStr[0].equals("login")) {
                    String userValidity = arrOfStr[1] + arrOfStr[2];
                    //checks user validity this a method.
                    response = loginValidity(userValidity);

                } else if (arrOfStr[0].equals("mkdir") && arrOfStr[2].equals("1")) {
                    //creates directory for user.
                    response = directoryMake(arrOfStr[1]);
                } else if (arrOfStr[0].equals("cd") && arrOfStr[2].equals("1")) {
                    //changes directory for user.
                    response = directoryChange(arrOfStr[1]);
                } else if (arrOfStr[0].equals("upload") && arrOfStr[3].equals("1")) {
                    //uploads a file in resourse folder.
                    response = uploadFile(arrOfStr[1], arrOfStr[2]);
                } else if (arrOfStr[0].equals("download") && arrOfStr[3].equals("1")) {
                 //download file from resourse folder and saves to user directory.
                    response = downloadFile(arrOfStr[1], arrOfStr[2]);
                }


                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes(response + "\n");
            }
           
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public String loginValidity(String userValidity) {

        String validity = null;
        //this particular file location needed to be changed to search for login txt. input your login txt location while creating file object.
        File file = new File("D:\\Applications\\Semestercoursework\\4.2\\CNLab\\login.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;

            while ((st = br.readLine()) != null) {
                //checks for user validity and creates a directory for user.
                
                if (st.equals(userValidity)) {
                    validity = "ok";
                    String createPath = file.getParent().toString();
                    createPath += "\\" + userValidity;

                    File f = new File(createPath);
                    if (!f.exists()) {
                        if (f.mkdir()) {
                            System.out.println("Directory is created!");
                        }
                    }
                    System.setProperty("user.dir", f.getAbsolutePath());
                    String cwd = System.getProperty("user.dir");
                 
                    break;
                } else {
                    validity = "not ok";
                }

            }

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return validity;
    }

    public String directoryMake(String userDirectory) {

        String created = "not ok";
        File file = new File("D:\\Applications\\Semestercoursework\\4.2\\CNLab");
        String createPath = file.getPath().toString();
        createPath += "\\" + userDirectory;
   

        File f = new File(createPath);
        if (!f.exists()) {
            if (f.mkdir()) {
                created = "ok";
                System.out.println("Directory is created!");
            }
        }

        if (f.exists()) {
            created = "ok";
        }

        return created;
    }

    public String directoryChange(String userDirectory) {

        String changed = "not ok";
        File f = new File(userDirectory).getAbsoluteFile();
   
        String cwd = System.getProperty("user.dir");
        System.out.println("Before working directory : " + cwd);
     
        if (f.exists()) {
            String createPath = f.getAbsolutePath();
            System.setProperty("user.dir", createPath);
            cwd = System.getProperty("user.dir");
            System.out.println("Current working directory : " + cwd);
            changed = "ok";
        }
  
        return changed;

    }

    public String downloadFile(String sourceFile, String fileName) {
        String uploaded = "not ok";
        try {
            String cwd = System.getProperty("user.dir");
            //enter your file location where resources are saved.
            
            File file = new File("D:\\Applications\\Semestercoursework\\4.2\\CNLab\\networkinglab1\\multithreadingTCP\\src\\resources");
            System.setProperty("user.dir", file.getAbsolutePath());
            String findFile = file.getPath().toString() + "\\" + sourceFile;
            File f = new File(findFile).getAbsoluteFile();
            String downFile = cwd + "\\" + fileName;
            File nFile = new File(downFile);
            if (f.exists()) {
                
                CharSequence seq[] = new CharSequence[]{".txt", ".jpg", ".pdf"};
                boolean bool[] = new boolean[]{false, false, false};
                bool[0] = fileName.contains(seq[0]);
                bool[1] = fileName.contains(seq[1]);
                bool[2] = fileName.contains(seq[2]);
                
                if (bool[0]) {
                FileInputStream fi = new FileInputStream(findFile);
                byte b[] = new byte[2002];
                fi.read(b, 0, b.length);
                boolean blnCreated = false;
                blnCreated = nFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(downFile);
                fo.write(b, 0, b.length);

                if (blnCreated) 
                    uploaded = "ok";
                }
                else if (bool[1]){
                    int width = 963;    //width of the image
                    int height = 640;   //height of the image
                    BufferedImage image = null;
                    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    image = ImageIO.read(f);
                    ImageIO.write(image, "jpg", nFile);

                    uploaded = "ok"; 
                }
            }
        } catch (Exception ex) {

        }

        return uploaded;

    }

    public String uploadFile(String sourceFile, String fileName) {

        String uploaded = "not ok";

        try {
            String cwd = System.getProperty("user.dir");
            //System.out.println("Before working directory : " + cwd);
            File file = new File("D:\\Applications\\Semestercoursework\\4.2\\CNLab\\networkinglab1\\multithreadingTCP\\src\\resources");
            System.setProperty("user.dir", file.getAbsolutePath());
        
            File fr = new File(sourceFile);

            if (fr.exists()) {
                String createFile = file.getPath().toString();
                createFile += "\\" + fileName;
                File f = new File(createFile);
                //System.out.println("Current working directory : " + cwd);
                CharSequence seq[] = new CharSequence[]{".txt", ".jpg", ".pdf"};
                boolean bool[] = new boolean[]{false, false, false};
                bool[0] = fileName.contains(seq[0]);
                bool[1] = fileName.contains(seq[1]);
                bool[2] = fileName.contains(seq[2]);

                if (bool[0]) {
                    FileInputStream fi = new FileInputStream(sourceFile);
                    byte b[] = new byte[2002];
                    fi.read(b, 0, b.length);

                    boolean blnCreated = false;
                    blnCreated = f.createNewFile();

                    FileOutputStream fo = new FileOutputStream(createFile);
                    fo.write(b, 0, b.length);

                    if (blnCreated) {
                        uploaded = "ok";
                    }

                } else if (bool[1]) {
                    int width = 963;    //width of the image
                    int height = 640;   //height of the image
                    BufferedImage image = null;
                    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    image = ImageIO.read(fr);
                    ImageIO.write(image, "jpg", f);

                    uploaded = "ok";
                }
            } else {
                System.out.println("invalid file");
            }

            System.setProperty("user.dir", cwd);
            System.out.println("Current working directory : " + cwd);

        } catch (Exception ex) {

        }

        return uploaded;
    }
}
