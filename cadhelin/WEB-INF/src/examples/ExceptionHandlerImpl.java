package examples;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.cadhelin.ExceptionHandler;


public class ExceptionHandlerImpl implements ExceptionHandler{
	Log log = LogFactory.getLog(ExceptionHandlerImpl.class);
	public void handleThrowable(Throwable e){
		log.error("Throwable",e);
		e.printStackTrace();
	}
	public void handleRuntimeException(RuntimeException e){
		log.error("RuntimeException",e);
		e.printStackTrace();
	}
}
