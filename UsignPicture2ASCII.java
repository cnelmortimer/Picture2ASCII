import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * 	Simple test class to test the Picture2ASCII software (a library o convert pictures to ASCII in 10 levels of resolution with/without sub-sampling ("result compression")).
 * 	This work has been developed for "Multimedia Systems", a subject of the "Computer Engineering" Master at the University of Almeria under
 * 	the supervision of Dr. D Vicente Gonzalez Ruiz
 * 	@author Nicolás C., University of Almeria
 * */
public class UsignPicture2ASCII {
	/** Simple example of usage of the Picture2ASCII Library. It asks for a image file to read. Then convert it to ASCII and shows the result in the console*/
	public static void main(String[] args){
		System.out.println("Please, introduce the image filename:");
		Scanner scanner = new Scanner(System.in);
		try {
			BufferedImage image = ImageIO.read(new File(scanner.next()));
			Picture2ASCII conversor = new Picture2ASCII();
			//Picture2ASCIIResult resultado = conversor.convertPicture2ASCII(image, false, 1);//Not compressing: Sie of the window will be ignored
			Picture2ASCIIResult resultado = conversor.convertPicture2ASCII(image, true, 3);//Compressing
			//System.out.println(resultado.getResultData());
			if(resultado.getResultCode()==1){
				FileWriter output = new FileWriter("output.txt");
				PrintWriter pw = new PrintWriter(output);
				String[] result = resultado.getResultData();
				for(int i = 0; i<result.length; i++){
					pw.print(result[i]);
				}
				output.close();
				System.out.println("Done!");
			}else{
				System.out.println("Unexpected error...");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
	}
	
}
