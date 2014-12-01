import javax.swing.JFileChooser;
import SLib.SRequest;
import SLib.SResponse;
import SLib.SList;
import SLib.SRR;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Dialog;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JOptionPane;

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
    private int Index;
    private ArrayList<String> IPList;
    public ThreadClient(Socket sockClient, ArrayList<ThreadClient> alThread)
    {
        this.sockClient=sockClient;
        this.alThread=alThread;
        Index = this.alThread.indexOf(this);
        this.sa = sockClient.getRemoteSocketAddress();
    }
    
    @Override
    public void run()
    {
        try {
            OUS = new ObjectOutputStream(getSockClient().getOutputStream());
            OIS = new ObjectInputStream(getSockClient().getInputStream());
            this.GetIPListFromServer();
            while(!getSockClient().isClosed())
            {
                SRR SR = null;
                try {
                    SR = (SRR)this.OIS.readObject();
                    if(SR.getType()==0)
                    {
                        switch(SR.getRequestCode())
                        {
                            case 3: case 12: case 13:
                                int result = JOptionPane.showConfirmDialog(null, "IP " + SR.getIP() + " want to send " + SR.getFilenames() + "\n\nAccept?", "Confirm", JOptionPane.YES_NO_OPTION);
                                if (result == JOptionPane.YES_OPTION)
                                {
                                    JFileChooser OPF = new JFileChooser();
                                    OPF.setCurrentDirectory(new File(System.getProperty("user.home")));
                                    OPF.setName(SR.getFilenames());
                                    int result2 = OPF.showSaveDialog(null);
                                    if (result2 == JFileChooser.APPROVE_OPTION) {
                                        File selectedFile = OPF.getSelectedFile();
                                        if(selectedFile.exists())
                                        {
                                            selectedFile.delete();
                                        }
                                        selectedFile.createNewFile();
                                        FileOutputStream FOS = new FileOutputStream(selectedFile);
                                        FOS.write(SR.getFiles());
                                        FOS.close();
                                        // user selects a file
                                    }
                                }
                                else
                                {
                                    SRR SR2 = new SRR(1);
                                    SR2.setResponseType(6);
                                    this.OUS.writeObject(SR2);
                                    this.OUS.flush();
                                    this.OUS.reset();
                                }
                                break;
                            default:
                                break; 
                        }
                    }
                    else
                    {
                        switch(SR.getResponseType())
                        {
                            case 8:
                                System.out.println("Masuk Pilihan 8");
                                this.setIPList(SR.getIpList());
                                break;
                            case 10:
                                this.OIS.close();
                                this.OUS.close();
                                getSockClient().close();
                            default :
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
        try {
            getSockClient().close();
        } catch (IOException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        synchronized(this.alThread)
        {
            System.out.println("Menutup Koneksi");
            this.alThread.remove(this);
        }
    }
    
    public void GetIPListFromServer() throws IOException
    {
            System.out.println("Mengambil List");
            SRR SR = new SRR(0);
            SR.setRequestCode(2);
            this.OUS.writeObject(SR);
            this.OUS.flush();
            this.OUS.reset();
            SRR Response = null;
    }
    
    public void sendFile(SRR Content)
    {
        try {
            this.OUS.writeObject(Content);
            this.OUS.flush();
            this.OUS.reset();
        } catch (IOException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void ExitThread()
    {
        try {
            SRR ExitResponse = new SRR(0);
            ExitResponse.setRequestCode(9);
            OUS.writeObject(ExitResponse);
            OUS.flush();
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

    /**
     * @return the IPList
     */
    public ArrayList<String> getIPList() {
        return IPList;
    }

    /**
     * @param IPList the IPList to set
     */
    public void setIPList(ArrayList<String> IPList) {
        this.IPList = IPList;
    }
    
}