package cope.interpreter.nodes;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cope.interpreter.SimplificationPattern;
import cope.interpreter.Variable;

public abstract class Function implements Cloneable
{	
	private String name = "f";
	
	protected Function[] children = new Function[]{};
	
	public static Variable PI = new Variable("pi", (float) Math.PI);
	public static Variable E = new Variable("e", (float) Math.E);
	
	public Function setName(String name) { this.name = name; return this; }
	public String toString() { return this.name + "(x) = " + getString(); }
	public String toString(float x) {
		DecimalFormat df = new DecimalFormat("0.00"); 
		try {
			return this.name + "(" + x + ") = " + df.format(evaluate(new Variable("x", x)));
		} catch (Exception e) {} 
		return "";
	}
	public Function[] getChildren() { return children; }
	public void setChildren(Function[] children) { this.children = children; }
	
	public abstract float evaluate(Variable var) throws Exception;
	public abstract float evaluate(ArrayList<Variable> variables) throws Exception;
	public abstract Function getParent();
	public abstract Function setParent(Function parent);
	public abstract String getString();
	public abstract String getType();
	
	public Function differentiate() 
	{ 
		Function f = differentiate(new Variable("x")); 
		f.name = name + "'";
		return f;
	}
	
	public abstract Function differentiate(Variable var);
	
	public Function simplify()
	{	
		Function[] children = getChildren();
		int len = children.length;
		Function[] newChildren = new Function[len];
		for (int i = 0; i < len; i++) {
			newChildren[i] = children[i].simplify();
		}
		
		Function newThis = (Function) clone();
		newThis.setChildren(newChildren);
		
		for (SimplificationPattern p : SimplificationPattern.patterns)
		{
			if (p.matches(newThis)) {
				newThis = p.transform(newThis);
				newThis = newThis.simplify();
			}
		}

		return newThis;
	}
	
	public float x(float a) { 
		try {
			return evaluate(new Variable("x", a));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public Function clone()
	{
		try {
			return (Function) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}

/*
 	// This is an example of how to set up a function f from stratch.
 	// Where f(x) = (2^x) * cos(x + 4):
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
