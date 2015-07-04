/**
*	This class encapsulates the result of converting an input picture to ASCII by using the library Pictre2ASCII.
*	It is composed by the result code of the operation and the String with the ASCII art generated.
*	This tool has been developed with academical purpose for "Sistemas Multimedia", a subject of the Computing Engineering Master at the University of Almeria.
*	The software is distributed to anyone interested at it without any kind of warranty. It can be used and modified with the only requirement of referring the original author. 
*	@author  Nicolás C., University of Almeria
*	@version 1.0
*/

public class Picture2ASCIIResult {
	
    /** resultCode contains the operation result of the request. The proposed values are: 0: Not done;; 1: OK;; 2: Bad Input;; 3: Bad Parameters;; 4: Unexpected Error */
    private int resultCode;
    /** resultData should contains the input image converted to ASCII when the request was correctly processed*/
    private String resultData;

    /**Constructor to generate a Picture2ASCIIResult by specifying it result code and data
     * @param resultCode	The result linked to the generation of the ASCII picture
     * @param resultData	The converted ASCII picture
     * @return 				A Picture2ASCIIResult object*/
    public Picture2ASCIIResult(int resultCode, String resultData)    {
        this.resultCode = resultCode;
        this.resultData = resultData;
    }
    
    /**Getter to the result code of the request
     * @return				The integer code linked to the result of the operation (0: Not done;; 1: OK;; 2: Bad Input;; 3: Bad Parameters;; 4: Unexpected Error)*/
    public int getResultCode(){
        return this.resultCode;
    }
    
    /**Setter to write the result code. It should be only used by the core class Pictre2ASCII
     * @param resultCode	The integer code linked to the operation (0: Not done;; 1: OK;; 2: Bad Input;; 3: Bad Parameters;; 4: Unexpected Error)*/
    protected void setResultCode(int resultCode){
        this.resultCode = resultCode;
    }
    
    /**Getter to read the converted image when correctly created
     * @return 				The image converted to ASCII*/
    public String getResultData(){
        return this.resultData;
    }
    
    /**Setter to write the result code. It should be only used by the core class Pictre2ASCII
     * @param resultData	The String data with the converted to ASCII image*/
    protected void setResultData(String resultData){
        this.resultData = resultData;
    }
}
