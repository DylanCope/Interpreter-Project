package cope.interpreter;
import java.util.Set;

import cope.interpreter.nodes.*;

/** This class gives some examples of how to use the interpreter. */
public class Main {
	
	public static void printTree(Function f, int depth)
	{
		for (int i = 0; i < depth; i++)
			System.out.print("\t");
		System.out.println(f.getString());
		for (Function c : f.getChildren())
			printTree(c, depth + 1);
	}
	
	public static void main(String[] args)
	{
		try 
		{
			Interpreter interpreter = new Interpreter();
			System.out.println(interpreter.placeBrackets("4.0*2+pi/x^y-z"));
			System.out.println(interpreter.placeBrackets("4.0*sin(2+pi)/x^y-ln(z)"));
			System.out.println(interpreter.removeSuperfluousBrackets("((4.0*sin((2+pi))/x^y-ln(z)))"));
			
			Function h = StringAnalysis.getFunction("(x*ln(x))+(x*4)");
//			printTree(h, 0);
			System.out.println(h);
			Set<Function> factors = h.getCommonFactors();
			System.out.println("Common factors = " + factors);
			System.out.println(h.simplify());
			System.out.println();
			
			h = StringAnalysis.getFunction("(x*4)+(x+x)");
			System.out.println(h);
			factors = h.getCommonFactors();
			System.out.println("Common factors = " + factors);
			System.out.println(h.simplify());
			System.out.println();
			
			Function f = StringAnalysis.getFunction("sin(pi*x)^2");
			System.out.println(f);
			System.out.println(f.toString(0.25f));
			System.out.println();
			System.out.println(f.differentiate().simplify());
			System.out.println();

			Function g = StringAnalysis.getFunction("ln(x) + (x^4)");
			
			g.setName("g");
			System.out.println(g);
			System.out.println(g.toString(5));
			System.out.println();
			
			Function diff = g.differentiate();
			System.out.println(diff);
			System.out.println();

			Function simp = diff.simplify();
			System.out.println();
			System.out.println(simp);
			System.out.println(simp.toString(2));
		} 
		catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}
}