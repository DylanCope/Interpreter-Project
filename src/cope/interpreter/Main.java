package cope.interpreter;
import cope.interpreter.nodes.Function;

/** This class gives some examples of how to use the interpreter. */
public class Main {
	
	public static void main(String[] args)
	{

		try {
			StringAnalysis.getFunction("4*7");
			StringAnalysis.getFunction("x+x-x/x*x");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Function f = null;
		try {
			f = StringAnalysis.getFunction("sin(pi * x)^2");
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		System.out.println(f);
		System.out.println(f.toString(0.25f));
		System.out.println(f.differentiate().simplify());
		
		System.out.println();
		
		Function g = null;
		try {
			g = StringAnalysis.getFunction("ln(x) + (x^4)");
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
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
}