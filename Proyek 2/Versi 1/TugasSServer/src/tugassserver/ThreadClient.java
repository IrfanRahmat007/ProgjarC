/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tugassserver;

import RLib.SRR;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASUS-PC
 */
public class ThreadClient implements Runnable {
    private Socket sockClient;
    private ArrayList<ThreadClient> alThread;
    private ArrayList<String> FileNames;
    private SocketAddress sa = null;
    private ObjectOutputStream OUS = null;
    private ObjectInputStream OIS = null;
    private Socket sockData = null;
    private int Index;
    private String basePath = new File("").getAbsolutePath();
    
    public ThreadClient(Socket sockClient, ArrayList<ThreadClient> alThread)
    {
        this.sockClient=sockClient;
        this.alThread=alThread;
        this.sa = sockClient.getRemoteSocketAddress();
    }
    
    @Override
    public void run()
    {
        try {
            Index = this.alThread.indexOf(this);
            this.OUS = new ObjectOutputStream(getSockClient().getOutputStream());
            OUS.flush();
            this.OIS = new ObjectInputStream(getSockClient().getInputStream());
            SRR SR = null;
            while(!getSockClient().isClosed())
            {
                try {
                    SR = (SRR) OIS.readObject();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                switch (SR.getRequestCode()) {
                    case 1:
                        SendList();
                        break;
                    case 2:
                        UploadFile(SR);
                        break;
                    case 3:
                        DownloadFile(SR);
                        break;
                    case 4:
                        RespondExit();
                        break;
                    default:
                        break;
                }
            }
            synchronized (this.alThread) {
                System.out.println("Menutup Thread " + this.Index + "\n");
                this.alThread.remove(this);
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void RespondExit() throws IOException
    {
        SRR ExitResponse = new SRR();
        ExitResponse.setType(1);
        ExitResponse.setResponseCode(10);
        this.sendObject(ExitResponse);
        OUS.flush();
        OIS.close();
        OUS.close();
        getSockClient().close();
    }
    
    public void SendList()
    {
        try {
            File D=new File(basePath+"\\Data\\");
            SRR SR=new SRR();
            SR.setType(1);
            SR.setResponseCode(10);
            for(int i=0;i<D.listFiles().length;i++)
            {
                SR.getFilelist().add(D.list()[i]);
            }
            this.sendObject(SR);
            D=null;
        } catch (IOException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public void UploadFile(SRR msg) throws IOException
    {
        File Dir=new File(basePath+"\\Data\\");
        File Content=new File(basePath+"\\Data\\"+msg.getFilename());
        if(Content.exists())
        {
            Content.delete();
        }
        Content.createNewFile();
        FileOutputStream FS = new FileOutputStream(Content);
        FS.write(msg.getContent());
        FS.close();
    }
    
    public void DownloadFile(SRR msg) throws IOException
    {
        int FileIndex = msg.getFileIndex();
        File Dir=new File(basePath+"\\Data\\");
        File Content=new File(basePath+"\\Data\\"+Dir.list()[FileIndex]);
        FileInputStream FS = new FileInputStream(Content);
        int len = (int)Content.length();
        byte temp[] = new byte[len];
        FS.read(temp);
        SRR Response = new SRR();
        Response.setType(1);
        Response.setResponseCode(11);
        Response.setFilename(Dir.list()[FileIndex]);
        Response.setContent(temp);
        sendObject(Response);
        FS.close();
    }

    public void sendObject(SRR msg) throws IOException
    {
        this.OUS.writeObject(msg);
        this.OUS.flush();
        this.OUS.reset();
    }
    
    /**
     * @return the sockClient
     */
    public Socket getSockClient() {
        return sockClient;
    }

    /**
     * @param sockClient the sockClient to set
     */
    public void setSockClient(Socket sockClient) {
        this.sockClient = sockClient;
    }
}