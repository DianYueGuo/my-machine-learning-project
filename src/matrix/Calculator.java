package matrix;

public interface Calculator {
	
	Object multiply(Object A, Object B) throws NumberMultiplicationException;
	Object add(Object A, Object B) throws NumberAdditionException;
		
}
