package examples;

import java.io.IOException;
import java.io.Writer;

import org.seasar.cadhelin.ControllerContext;

public class DownloadControllerImpl {
	public void showDownload() throws IOException{
		ControllerContext context = ControllerContext.getContext();
		Writer writer = null;
		try {
			writer = context.createWriter("download.txt","text/plain");
			writer.write("this is download test\n");
			writer.write("this is download test\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(writer!=null){writer.close();}
		}
	}
}
