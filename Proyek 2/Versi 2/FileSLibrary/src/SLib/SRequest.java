/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SLib;

import java.io.Serializable;

/**
 *
 * @author ASUS-PC
 */
/*
Request Code:
-1. Error
0. OK
2. List
3. File Send
4.Confirm   
*/

public class SRequest implements Serializable{
    private int RequestCode;
    private String filenames;
    private byte[] files;
    private String IP;
    private int DestIndex;

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
    
}
