package cope.interpreter;

import cope.interpreter.nodes.BinaryFunction;
import cope.interpreter.nodes.Constant;
import cope.interpreter.nodes.Function;

public interface SimplificationPattern 
{
	public Function transform(Function f) throws Exception;
	public boolean matches(Function f);
	
	/** This pattern simplifies a scenario where there are two constants connected 
	 *  by a binary operation to a scenario where there is one constant.
	 * */
	public static final SimplificationPattern pattern0 = new SimplificationPattern() 
	{
		@Override
		public Function transform(Function f) throws Exception{
			if (matches(f)) {
				// f is constant, so evaluating it with no variable will just return
				// the operation between the two constants.
				System.out.println(new Constant(f.evaluate(new Variable(""))).getString());
				return new Constant(f.evaluate(new Variable("")));
			}
			return f;
		}
		
		@Override
		public boolean matches(Function f) {
			if (f.getType().equals("binary")
					&& f.getChildren()[0].getType().equals("constant")
					&& f.getChildren()[1].getType().equals("constant"))
				return true;
			else return false;
		}
	};
	
	public static final SimplificationPattern pattern1 = new SimplificationPattern() 
	{
		@Override
		public Function transform(Function f) throws Exception {
			if (matches(f)) {
				
			}
			return f;
		}

		@Override
		public boolean matches(Function f) {
			boolean matches = f.getType().equals("binary");
			matches = matches && ((BinaryFunction) f)
					.getInstruction()
					.getString()
					.equals("*");
			if (matches)
				return true;
			return false;
		}
		
	};
	
	public static SimplificationPattern[] patterns = new SimplificationPattern[] 
		{
			pattern0
		};
}
