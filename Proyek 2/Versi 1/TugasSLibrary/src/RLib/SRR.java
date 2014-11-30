/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RLib;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ASUS-PC
 */
/*
Request Code(0) : 
-1.Error
0. OK
1. List
2. Upload
3. Download
4. Exit
Response Code(1) : 
-1.Error
0. OK
10.List
11.File
12.Exit
*/
public class SRR implements Serializable{
    private int Type;
    private int RequestCode;
    private int ResponseCode;
    private byte[] Content;
    private String Filename;
    private ArrayList<String> Filelist = new ArrayList();
    private int FileIndex;
    private String ErrorDescription;

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
     * @return the ResponseCode
     */
    public int getResponseCode() {
        return ResponseCode;
    }

    /**
     * @param ResponseCode the ResponseCode to set
     */
    public void setResponseCode(int ResponseCode) {
        this.ResponseCode = ResponseCode;
    }

    /**
     * @return the Content
     */
    public byte[] getContent() {
        return Content;
    }

    /**
     * @param Content the Content to set
     */
    public void setContent(byte[] Content) {
        this.Content = Content;
    }

    /**
     * @return the Filename
     */
    public String getFilename() {
        return Filename;
    }

    /**
     * @param Filename the Filename to set
     */
    public void setFilename(String Filename) {
        this.Filename = Filename;
    }

    /**
     * @return the Filelist
     */
    public ArrayList<String> getFilelist() {
        return Filelist;
    }

    /**
     * @param Filelist the Filelist to set
     */
    public void setFilelist(ArrayList<String> Filelist) {
        this.Filelist = Filelist;
    }

    /**
     * @return the FileIndex
     */
    public int getFileIndex() {
        return FileIndex;
    }

    /**
     * @param FileIndex the FileIndex to set
     */
    public void setFileIndex(int FileIndex) {
        this.FileIndex = FileIndex;
    }

    /**
     * @return the ErrorDescription
     */
    public String getErrorDescription() {
        return ErrorDescription;
    }

    /**
     * @param ErrorDescription the ErrorDescription to set
     */
    public void setErrorDescription(String ErrorDescription) {
        this.ErrorDescription = ErrorDescription;
    }

    /**
     * @return the Type
     */
    public int getType() {
        return Type;
    }

    /**
     * @param Type the Type to set
     */
    public void setType(int Type) {
        this.Type = Type;
    }
}
