/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesserver;

import SLib.SRR;
import java.io.IOException;
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
    private SocketAddress sa = null;
    private ObjectOutputStream OUS = null;
    private ObjectInputStream OIS = null;
    private Socket sockData = null;
    private int Index;
    public ThreadClient(Socket sockClient, ArrayList<ThreadClient> alThread)
    {
        this.sockClient=sockClient;
        this.alThread=alThread;
        this.sa = sockClient.getRemoteSocketAddress();
    }
    
    @Override
    public void run()
    {
        Index = this.alThread.indexOf(this);
        try {
            this.OUS = new ObjectOutputStream(getSockClient().getOutputStream());
            OUS.flush();
            this.OIS = new ObjectInputStream(getSockClient().getInputStream());

            while(!getSockClient().isClosed())
            {
                SRR SR = null;
                try {
                    
                    SR = (SRR)this.OIS.readObject();
                    System.out.println("Jenis "+SR.getType()+"Nama file "+SR.getFilenames()+"Jenis Request "+SR.getRequestCode());
                    if(SR.getType()==0)
                    {
                        switch(SR.getRequestCode())
                        {
                            case 2:
                                sendList();
                                break;
                            case 3:
                                send(SR);
                                break;
                            case 9:
                                SRR ExitResponse=new SRR(1);
                                ExitResponse.setResponseType(10);
                                OUS.writeObject(ExitResponse);
                                OUS.flush();
                                OIS.close();
                                OUS.close();
                                getSockClient().close();
                                break;
                            case 12:
                                sendMulticast(SR);
                                break;
                            case 13:
                                sendBroadcast(SR);
                                break;
                            default:
                                break;
                        }
                    }
                    
                    
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        synchronized(this.alThread)
        {
            System.out.println("Menutup Thread "+this.Index+"\n");
            this.alThread.remove(this);
        }
    }
    
    public void send(SRR SR) throws IOException
    {
        SR.setIP(this.getSockClient().getInetAddress().toString());
        int index= SR.getDestIndex();
        System.out.println("Thread tujuan : "+index);
        this.alThread.get(index).sendFile(SR, SR.getFilenames());
    }
    
    public synchronized void sendBroadcast(SRR SR) throws IOException
    {
        SR.setIP(this.getSockClient().getInetAddress().toString());
        for(int i =0;i<this.alThread.size();i++)
        {
            if(this.alThread.get(i)!=this)
            {
               this.alThread.get(i).sendFile(SR, SR.getFilenames());
            }
        }
    }
    
    public synchronized void sendMulticast(SRR SR)
    {
        SR.setIP(this.getSockClient().getInetAddress().toString());
        for(int i =0;i<SR.getDestIndexes().length;i++)
        {
            this.alThread.get(SR.getDestIndexes()[i]).sendFile(SR, SR.getFilenames());
        }
    }
    
    public synchronized void sendList()throws IOException
    {
        System.out.println("Mengirim List");
        SRR SL= new SRR(1);
        SL.setResponseType(8);
        for(int i = 0;i< this.alThread.size();i++)
        {
            SL.getIpList().add(this.alThread.get(i).getSockClient().getInetAddress().toString());
        }
        this.OUS.writeObject(SL);
        this.OUS.flush();
    }
    
    public void sendFile(SRR Content, String filename)
    {
        try {
            System.out.println("YES");
            this.OUS.writeObject(Content);
            this.OUS.flush();            
            this.OUS.reset();
        } catch (IOException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
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
