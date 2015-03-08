package cope.interpreter;

import cope.interpreter.nodes.Constant;
import cope.interpreter.nodes.Function;

public class FunctionAnalysis 
{
	public static void main(String[] args)
	{
		Function f = new Constant(3);
		System.out.println(f.getClass().getSimpleName());
	}
	
	
//	private static isCaseOne(Function f) 
//	{
//	}
//
//	public static Function differentiate(Function f)
//	{
//		
//	}
	
}
