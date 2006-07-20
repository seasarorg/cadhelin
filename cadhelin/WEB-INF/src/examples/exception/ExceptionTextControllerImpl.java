package examples.exception;

public class ExceptionTextControllerImpl {
	public void showExeptionTest() throws Exception{
		throw new Exception("Exception Handler Test");
	}
	public void showRuntimeExceptionTest(){
		throw new RuntimeException("RuntimeException Handler Test");
	}
}
