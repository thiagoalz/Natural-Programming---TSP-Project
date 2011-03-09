package controle.arquivo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jgap.Configuration;
import org.jgap.Genotype;
import org.jgap.xml.XMLManager;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.Method;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.SerializerFactory;
import com.sun.org.apache.xml.internal.serialize.DOMSerializer;
import com.sun.org.apache.xml.internal.serialize.Serializer;

public class FileManager{	


	public static float[][] carregaDados(File selectedFile) {
		float[][] retorno=null;
		try {			
			FileReader fr=new FileReader(selectedFile);
			BufferedReader br=new BufferedReader(fr);
			
			String linha=br.readLine();
			int qtd=Integer.parseInt(linha);
			
			retorno=new float[qtd][2];
			
			for(int i=0;i<qtd;i++){
				linha=br.readLine();
				StringTokenizer st=new StringTokenizer(linha," ");
				Float.parseFloat(st.nextToken());//Ignora o primero termo do arquivo( numero sequencial)
				retorno[i][0]=Float.parseFloat(st.nextToken());
				retorno[i][1]=Float.parseFloat(st.nextToken());
			}
			
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
				
		return retorno;
	}
}