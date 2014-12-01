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
Response Type
-1. Error
0. OK
5. Accepted
6. Denied
7. File
*/
public class SResponse implements Serializable{
    private int ResponseType;
    private byte[] files;
    private String filename;
    private String Msg;
    private String SourceIP;

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
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
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
}
