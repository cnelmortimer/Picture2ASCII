import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
* 	Core class of the Picture2ASCII software. Picture2ASCII is a simple and academical purpose oriented library to convert pictures to ASCII. It handles
* 	10 levels of resolution with and without sub-sampling (what leads to "result compression"). The conversion procedure is threaded in both cases to fully
* 	benefit from multi-core modern computers. The input images are expected to be in RGB with 24 bits of color depth. The library will perform a previous 
* 	internal conversion in any other case to fulfill this requirement.
*	This tool has been developed with academical purpose for "Sistemas Multimedia", a subject of the Computing Engineering Master at the University of Almeria.
*	The software is distributed to anyone interested at it without any kind of warranty. It can be used and modified by everybody with the only requirement of referring the original author. 
*	@author  Nicolás C., University of Almeria
*	@version 1.0
*/
public class Picture2ASCII {
	
	/**bytesPerPixel contains the color depth of compatible images. It is specified as a class member to make easier further modifications of this property*/
	private int bytesPerPixel = 3;//Working with 24 bpp pictures by default -> 3Bytes 
	
	/**pixels is the array of bytes where the input image will be saved before starting the conversion procedure. It will be accessed by all working threads*/
	private byte[] pixels;
	
	/**bytesPerLine is an auxiliary variable to store the number of bytes corresponding to a single input image row*/
    private int bytesPerLine;
    
    /**pictureHeight stores the height of the input image in lines/rows*/
    private int pictureHeight;
    
    /**output is the String array where each working thread will save its results before packing and returning the result*/
    private String[] output;
    
    /**assignments defines the equivalence between every grey-scale range to its corresponding ASCII character*/
    private static final char[] assignments = {'#', '@', '%', '+', '*', '=', ':', '-', '.', ' ', ' '};//The last one is for the division offset
    
    /**Default constructor to create a Picture2ASCII converter instance a Picture2ASCII converter instance*/
    public Picture2ASCII(){
        pixels = null;
        bytesPerLine = -1;
        pictureHeight = -1;
        this.output = null;
    }
    
    /**Entry-point to the Picture2ASCII library operation. This method must be called to convert pictures to ASCII with or without sub-sampling (or "compression")
     * This is the only real public method offered by Picture2ASCII apart from the default constructor.
     * @param 	inputImage 			The input image to be converted to ASCII
     * @param 	compression 		The flag to do a full pixel-to-char (when false) or sub-sampling/compressing (when true) conversion
     * @param	squareWindowSize	The size of the window to use for sub-sampling (it will be ignored when the compression flag is false)
     * @return 						A Picture2ASCIIResult containing both the integer result code and the generated ASCII art (when correctly done)
     * */
    public Picture2ASCIIResult convertPicture2ASCII(BufferedImage inputImage, boolean compression, int squareWindowSize){
    	Picture2ASCIIResult result = new Picture2ASCIIResult(0, "");//0: Not done;; 1: OK;; 2: Bad Input;; 3: Bad Parameters;; 4: Unexpected Error 
        if(inputImage.getColorModel().getPixelSize()!=24 || inputImage.getAlphaRaster() != null){//Bad format... try to solve//if(inputImage.getType()!=1){
        	inputImage = convert24(inputImage);
        }
        this.pixels = ((DataBufferByte) inputImage.getRaster().getDataBuffer()).getData();//Load pixels
        int numThreads = Runtime.getRuntime().availableProcessors();//numThreads = 1; //for sequential debugging
        Thread[] workingThreads = new Thread[numThreads];
        this.pictureHeight = inputImage.getHeight();
        this.bytesPerLine = inputImage.getWidth() * bytesPerPixel;
        if (!compression){
            this.output = new String[inputImage.getHeight()];      
            for (int i = 0; i < numThreads; i++){
            	workingThreads[i] = new Thread(new PlainConverter(i, numThreads));
                workingThreads[i].start();
            }
        }else{
        	if(squareWindowSize>1 && squareWindowSize%2!=0){//Size greater than 1 and odd
        		int windowedHeight = (int)Math.floor(inputImage.getHeight()/squareWindowSize);
            	int windowedWidth = ((int)(Math.floor(inputImage.getWidth()/squareWindowSize)))*bytesPerPixel*squareWindowSize;
            	if(windowedWidth>0 && windowedHeight>0){//The image is not smaller than the window: we can go on
            		this.output = new String[windowedHeight];//Clipping starting from the top left corner
            		for (int i = 0; i < numThreads; i++){
                    	workingThreads[i] = new Thread(new CompressingConverter(i, numThreads, squareWindowSize, windowedWidth, windowedHeight*squareWindowSize));
                        workingThreads[i].start();
                    }
            	}else{
            		result.setResultCode(3); return result;//Bad parameter: The library was asked to compress a small picture with a big window
            	}
        	}else{
        		result.setResultCode(3); return result;//Bad parameter: The library was asked to compress with an invalid window size
        	}        	
        }
        for(int i=0; i<numThreads; i++){//Wait for the deployed threads to finish
        	try {
				workingThreads[i].join();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
        }
        result.setResultCode(1);
        result.setResultData(packArrayString(output));
    	return result;
    }
    
    /**Internal class to model plain converter threads. This kind of thread just go over the image to get the ascii char of every pixel
    and save the resulting strings in the global output array. ASCII images generated will be very detailed but very big too*/
    private class PlainConverter implements Runnable {
    	/**The identifier of every thread*/
        private int threadId;
        /**The size of the team*/
        private int totalThreads;
        
        /**
         * The constructor to define PlainConverter threads.
         * @param threadId The identifier of the thread
         * @param totalThreads The size of the converting team
         * */
        public PlainConverter(int threadId, int totalThreads) {
            this.threadId = threadId;
            this.totalThreads = totalThreads;
        }

        /**Method that will be executed by every PlainConverted thread when started*/
        public void run() {
        	int line = threadId, pixelArrayFocus = bytesPerLine * threadId;
            int advancingArrayFactor = bytesPerLine * totalThreads, lineFocus, pixelArrayFocusPLineFocus;
            while(line<pictureHeight){
                output[line] = "";
                for (lineFocus = 0; lineFocus < bytesPerLine; lineFocus+=bytesPerPixel){
                    pixelArrayFocusPLineFocus = pixelArrayFocus + lineFocus;
                    output[line] += correspondingChar((int)(0.07 * (pixels[pixelArrayFocusPLineFocus] & 0xff)
                        + 0.72 * (pixels[pixelArrayFocusPLineFocus + 1]& 0xff) + 0.21 * (pixels[pixelArrayFocusPLineFocus + 2] & 0xff)));//PGR in memory...
                }
                output[line] += System.getProperty("line.separator");
                line += totalThreads;
                pixelArrayFocus += advancingArrayFactor;
            }
        }
    }
    
    /**Internal class to model sub-sampling converter threads. This kind of thread will go over the image to get the ASCII char of 
    * every sub-sampled region. This regions are squares defined by their side. Results will be smaller but also with less details. The
    * process will simply omit borders when windowing is not perfectly suitable*/
    private class CompressingConverter implements Runnable {

    	/**The identifier of every thread*/
        private int threadId;
        /**The size of the team*/
        private int totalThreads;
        /**The side of the square window*/
        private int sizeWindow;
        /**The number of bytes of an input image row after considering the windowing*/
        private int windowedWidth;
        /**The number of lines of an input image after considering the windowing*/
        private int maxLine;

        /**
         * The constructor to define CompressingConverter threads.
         * @param threadId 		The identifier of the thread
         * @param totalThreads 	The size of the converting team
         * @param sizeWindow	The side of the square window
         * @param windowedWidth	The number of bytes of an input image row after considering the windowing
         * @param maxLine		The number of lines of an input image after considering the windowing
         * */
        public CompressingConverter(int threadId, int totalThreads, int sizeWindow, int windowedWidth, int maxLine) {
            this.threadId = threadId;
            this.totalThreads = totalThreads;
            this.sizeWindow = sizeWindow;
            this.windowedWidth = windowedWidth;
            this.maxLine = maxLine;
        }
        
        /**Method that will be executed by every CompressingConverter thread when started*/
        public void run(){
        	int writtenLine = threadId;//Where to write the output
        	int line = threadId*sizeWindow, advancePixelArrayFocus = totalThreads*bytesPerLine*sizeWindow;//Focus in the input image
        	int squareWindow = sizeWindow*sizeWindow; int bytesPerWindowLine = sizeWindow*bytesPerPixel;
        	int pixelArrayFocus = bytesPerLine*line, advanceLine = sizeWindow*totalThreads;//Focus in the input data array
        	int i=0, j=0, acumRed, acumGreen, acumBlue, lineWindowSize = sizeWindow*bytesPerLine, iPlusFocusPlusj, iPlusFocus;
        	while(line<maxLine){
        		output[writtenLine] = "";//Setup the line
        		for(int focusInLine = 0; focusInLine<windowedWidth; focusInLine+=bytesPerWindowLine){//Iterate over the windows of the line
        			acumBlue = 0; acumGreen = 0; acumRed = 0;//Reset
        			for(i = pixelArrayFocus; i<(pixelArrayFocus+lineWindowSize); i+=bytesPerLine){//Iterate inside the window
        				iPlusFocus = i+focusInLine;
        				for(j=0; j<bytesPerWindowLine; j+=3){//Read the window line:
        					iPlusFocusPlusj = iPlusFocus + j; 
        					acumBlue += (pixels[iPlusFocusPlusj] & 0xff); 
        					acumGreen +=  (pixels[iPlusFocusPlusj + 1] & 0xff);
        					acumRed += (pixels[iPlusFocusPlusj + 2] & 0xff);        				
        				}
        			}//After collecting the window information: compute the corresponding average value:
        			output[writtenLine] += correspondingChar((int)(0.07 * acumBlue + 0.72 * acumGreen + 0.21 * acumRed)/squareWindow);//BGR in memory...
        		}//The line has been processed. Write the output and move to the next one        		
        		output[writtenLine] += System.getProperty("line.separator");
        		pixelArrayFocus += advancePixelArrayFocus;//totalThreads*sizeWindow; 
        		writtenLine += totalThreads;
        		line+=advanceLine;
        	}
        }
    }
    
    /**
     * This method computes the correspondence between a grey-level of the input image and its equivalent ASCII char
     * @param greyValue		The grey value of a pixel (when not sub-sampling) or a region (when sub-sampling) of the input image
     * @return 				The ASCII char equivalent to the input grey level
     * */
    private char correspondingChar(int greyValue){
        return assignments[greyValue/25];
    }
    
    /**
    * 	Auxiliary functions: The library needs a 24 bits color depth without alpha channel:
    *	Images should be in the expected format in order to avoid this conversion
    *	@param source	The input image
    *	@return 		The preliminary converted input image*/
    private static BufferedImage convert24(BufferedImage source) {
    	BufferedImage dest = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
    	Graphics2D g2d= dest.createGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        return dest;
    }
    
    /**
     * Auxiliary internal method to join and merge every string line in a single string to build the final conversion result.
     * This procedure could be simply avoided if needed (maybe to speedup the process) by adapting the Picture2ASCIIResult class to 
     * contain a String array as result instead of a single String.
     * @param content	The internal output array of strings generated by the converting threads along their operation
     * @return			The final packed String with the ASCII picture
     */
    private String packArrayString(String[] content){
    	String output = "";
    	for(int i = 0; i<content.length; i++){
    		for(int j=0; j<content[i].length(); j++){
    			output += content[i].charAt(j);
    		}
    	}
    	return output;
    }
}
