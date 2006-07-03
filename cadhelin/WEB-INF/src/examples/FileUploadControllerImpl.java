package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.fileupload.FileItem;

public class FileUploadControllerImpl {
	public void showUpload(){
		
	}
	public void doUpload(String name,FileItem item) throws IOException{
		System.out.println("name="+name);
		
		System.out.println("fieldName="+item.getFieldName());
		System.out.println("name="+item.getName());
		InputStream is = item.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while((line=reader.readLine())!=null){
			System.out.println(line);
		}
		reader.close();
		showUpload();
	}
}
