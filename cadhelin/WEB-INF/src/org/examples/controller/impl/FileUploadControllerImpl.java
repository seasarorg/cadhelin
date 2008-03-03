package org.examples.controller.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.fileupload.FileItem;
import org.seasar.cadhelin.annotation.ParamNames;

public class FileUploadControllerImpl {
	public void showUpload(){
		
	}
	@ParamNames({"name","item"})
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
	public void showUpload2(){
		
	}
	@ParamNames("params")
	public void doUpload2(Map<String,Object[]> params) throws IOException{
		for (Entry<String,Object[]> entry : params.entrySet()) {
			System.out.print(entry.getKey()+"=");
			for (Object object : entry.getValue()) {
				if (object instanceof FileItem) {
					FileItem fileItem = (FileItem) object;
					InputStream is = fileItem.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while((line=reader.readLine())!=null){
						System.out.println(line);
					}
					reader.close();
					
				}else{
					System.out.print(object.toString());
				}
				System.out.print(", ");
			}
			System.out.println();
		}
		showUpload2();
	}
}
