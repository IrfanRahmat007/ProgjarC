/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SLib;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author ASUS-PC
 */
public class SList implements Serializable{
    private ArrayList<String> ipList = new ArrayList();

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
}
