package cope.interpreter.nodes;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cope.interpreter.SimplificationPattern;
import cope.interpreter.Variable;

public abstract class Function 
{	
	private String name = "f";
	private String function = "";
	
	public static Variable PI = new Variable("pi", (float) Math.PI);
	public static Variable E = new Variable("e", (float) Math.E);
	
	public Function setName(String name) { this.name = name; return this; }
	public Function setFunctionString(String function) { this.function = function; return this; }
	public String toString() { return this.name + "(x) = " + function; }
	public String toString(float x) {
		DecimalFormat df = new DecimalFormat("0.00"); 
		try {
			return this.name + "(" + x + ") = " + df.format(evaluate(new Variable("x", x)));
		} catch (Exception e) { } 
		return "";
	}
	
	public abstract float evaluate(Variable var) throws Exception;
	public abstract float evaluate(ArrayList<Variable> variables) throws Exception;
	public abstract Function[] getChildren();
	public abstract Function setChildren(Function[] children);
	public abstract Function getParent();
	public abstract Function setParent(Function parent);
	public abstract String getString();
	public abstract String getType();
	
	public Function differentiate() { return differentiate(new Variable("x")); }
	public abstract Function differentiate(Variable var);
	
	public float x(float a) { 
		try {
			return evaluate(new Variable("x", a));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public Function simplify() throws Exception
	{
		if (getType().equals("constant") && getType().equals("variable"))
			return this;
		
		for (SimplificationPattern pattern : SimplificationPattern.patterns)
			if (pattern.matches(this)) {
				if (this.getParent() != null) {
					switch (this.getParent().getType()) {
					case ("binary"):
						this.getParent().setChildren(
							new Function[]{
								((BinaryFunction) this.getParent()).getLeftChild().simplify(),
								((BinaryFunction) this.getParent()).getRightChild().simplify()
						});
						break;
					case ("unary"):
						this.getParent().setChildren(
							new Function[]{
								((UnaryFunction) this.getParent()).getChildren()[0].simplify()
						});
						break;
					}
				}
				else
					return pattern.transform(this);
			}
		
		if (this.getChildren() != null)
			for (Function child : this.getChildren())
				child = child.simplify();
		
		return this;
	}
}

/*
 	// This is an example of how to set up a function f from stratch.
 	// Where f(x) = (2^x) * cos((x + 4) 8):
 	// f could be a multi-variable function though.
 	
 	Function f = 
				new BinaryOperationNode(
					new BinaryOperationNode(
						new ConstantNode(2),
						new VariableNode("x"),
						BinaryOperationNode.exponentiation
						),
					new UnaryOperationNode(
						new BinaryOperationNode(
							new BinaryOperationNode(
								new VariableNode("x"),
								new ConstantNode(4),
								BinaryOperationNode.addition
							),
							new VariableNode("x"),
							BinaryOperationNode.division
						),
						UnaryOperationNode.cosine
					),
					BinaryOperationNode.multiplication
				);
		
		ArrayList<Variable> variables = new ArrayList<Variable>();
		variables.add(new Variable("x", 4));
		
		System.out.println(f.evaluate(variables));
*/
