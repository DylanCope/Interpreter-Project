package cope.interpreter;
import java.util.Set;

import cope.interpreter.nodes.*;

/** This class gives some examples of how to use the interpreter. */
public class Main 
{
	
	public static void main(String[] args)
	{
		try 
		{
			Interpreter interpreter = new Interpreter();
			Function h = interpreter.parse("x*ln(x)+x*4");
			System.out.println("Demonstrating factoring:");
			System.out.println(h);
			Set<Function> factors = h.getCommonFactors();
			System.out.println("Common factors = " + factors);
			System.out.println(h.simplify());
			System.out.println();
			
			h = interpreter.parse("x*4+x+x*5+10*x");
			System.out.println("Demonstrating gathering like terms:");
			System.out.println(h);
			factors = h.getCommonFactors();
			System.out.println("Common factors = " + factors);
			System.out.println(h.simplify());
			System.out.println("\n");
			
			Function f = interpreter.parse("(4*x^y - 2*sinh(y))*(9*x*y + cos(ln(x))^2)", "x", "y");//sin(pi*x)^2");
			f.setName("f");
			System.out.println("Let " + f);
			System.out.println(f.toString(0.25f));
			System.out.println(f.differentiate().simplify());
			System.out.println();

			Function g = interpreter.parse("ln(x*y) + x^4 + y", "x", "y");
			g.setName("g");
			System.out.println("\nLet " + g + "\n");
			Function diff = g.differentiate();
			System.out.println("Differentiating g with respect to x, without simplification:");
			System.out.println(diff);
			System.out.println();
			System.out.println("Simplifying g':");
			System.out.println(diff.simplify());
		} 
		catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}
}