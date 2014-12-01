/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SLib;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ASUS-PC
 */
/*
Request Code (0) :
-1. Error
0. OK
2. List
3. File Send
4. Confirm
9. Exit
12.Multicast
13.Broadcast
*/
/* 
Response Type (1) :
-1. Error
0. OK
5. Accepted
6. Denied
7. File
8. List
10.Closed
11.Client Closed
*/
public class SRR implements Serializable{
        private int type;
        private ArrayList<String> ipList = new ArrayList();
        private int RequestCode;
        private String filenames;
        private byte[] files;
        private String IP;
        private int DestIndex;
        private int[] DestIndexes;
        private int ResponseType;
        private String Msg;
        private String SourceIP;
        
    public SRR(int type)
    {
        this.type=type;
    }
    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the ipList
     */
    public ArrayList<String> getIpList() {
        return ipList;
    }

    /**
     * @param ipList the ipList to set
     */
    public void setIpList(ArrayList<String> ipList) {
        this.ipList = ipList;
    }

    /**
     * @return the RequestCode
     */
    public int getRequestCode() {
        return RequestCode;
    }

    /**
     * @param RequestCode the RequestCode to set
     */
    public void setRequestCode(int RequestCode) {
        this.RequestCode = RequestCode;
    }

    /**
     * @return the filenames
     */
    public String getFilenames() {
        return filenames;
    }

    /**
     * @param filenames the filenames to set
     */
    public void setFilenames(String filenames) {
        this.filenames = filenames;
    }

    /**
     * @return the files
     */
    public byte[] getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(byte[] files) {
        this.files = files;
    }

    /**
     * @return the IP
     */
    public String getIP() {
        return IP;
    }

    /**
     * @param IP the IP to set
     */
    public void setIP(String IP) {
        this.IP = IP;
    }

    /**
     * @return the DestIndex
     */
    public int getDestIndex() {
        return DestIndex;
    }

    /**
     * @param DestIndex the DestIndex to set
     */
    public void setDestIndex(int DestIndex) {
        this.DestIndex = DestIndex;
    }

    /**
     * @return the ResponseType
     */
    public int getResponseType() {
        return ResponseType;
    }

    /**
     * @param ResponseType the ResponseType to set
     */
    public void setResponseType(int ResponseType) {
        this.ResponseType = ResponseType;
    }

    /**
     * @return the Msg
     */
    public String getMsg() {
        return Msg;
    }

    /**
     * @param Msg the Msg to set
     */
    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    /**
     * @return the SourceIP
     */
    public String getSourceIP() {
        return SourceIP;
    }

    /**
     * @param SourceIP the SourceIP to set
     */
    public void setSourceIP(String SourceIP) {
        this.SourceIP = SourceIP;
    }

    /**
     * @return the DestIndexes
     */
    public int[] getDestIndexes() {
        return DestIndexes;
    }

    /**
     * @param DestIndexes the DestIndexes to set
     */
    public void setDestIndexes(int[] DestIndexes) {
        this.DestIndexes = DestIndexes;
    }
}
