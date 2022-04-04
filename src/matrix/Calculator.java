package matrix;

public interface Calculator {
	
	Object multiply(Object a, Object b) throws NumberMultiplicationException;
	Object add(Object a, Object b) throws NumberAdditionException;
		
}
