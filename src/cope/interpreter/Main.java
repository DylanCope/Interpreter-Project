package cope.interpreter;
import cope.interpreter.nodes.Function;

/** This class gives some examples of how to use the interpreter. */
public class Main {
	
	public static void main(String[] args)
	{
//		Function h = null;
//		try {
//			h = StringAnalysis.getFunction("4*7");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		Function f = null;
//		try {
//			f = StringAnalysis.getFunction("sin(pi * x)^2");
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			System.exit(1);
//		}
//		System.out.println(f);
//		System.out.println(f.toString(0.25f));
//		
//		System.out.println();
//		
		Function g = null;
		try {
			g = StringAnalysis.getFunction("(2*5)*(4x)");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		

		g.setName("g");
		Function diff = g.differentiate();
		System.out.println(g);
		try {
			g = g.simplify();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("g(x) = " + g.getString());
		System.out.println("g'(x) = " + diff.getString());
//		System.out.println(diff.x(6));
	}
}