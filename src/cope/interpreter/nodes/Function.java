package cope.interpreter.nodes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cope.interpreter.Variable;
import cope.interpreter.patterns.BinaryInstruction;
import cope.interpreter.patterns.SimplificationPattern;

public abstract class Function implements Cloneable
{	
	private String name = "";
	
	protected Function[] children = new Function[]{};
	
	public static Variable PI = new Variable("pi", (float) Math.PI);
	public static Variable E = new Variable("e", (float) Math.E);
	
	public String toString() 
	{ 
		Set<String> vars = getVariables();
		vars.remove(PI.getName());
		vars.remove(E.getName());
		String varStr = "";
		Iterator<String> iterator = vars.iterator();
		while (iterator.hasNext())
		{
			String next = iterator.next();
			varStr += next;
			if (iterator.hasNext())
				varStr += ", ";
		}
		if (name != "")
			return this.name + "(" + varStr + ") = " + getString(); 
		return getString();
	}
	
	public String toString(float x) 
	{
		DecimalFormat df = new DecimalFormat("0.00"); 
		try {
			return this.name + "(" + x + ") = " + df.format(evaluate(new Variable("x", x)));
		} catch (Exception e) {} 
		return "";
	}
	
	public Function[] getChildren() { return children; }
	public void setChildren(Function[] children) { this.children = children; }
	public Function setName(String name) { this.name = name; return this; }
	
	public abstract float evaluate(Variable var) throws Exception;
	public abstract float evaluate(ArrayList<Variable> variables) throws Exception;
	public abstract Function getParent();
	public abstract Function setParent(Function parent);
	public abstract String getString();
	public abstract String getType();
	public abstract Set<String> getVariables();
	
	public Function differentiate() 
	{ 
		Function f = differentiate(new Variable("x")); 
		f.name = name + "'";
		return f;
	}
	
	public abstract Function differentiate(Variable var);
	
	public Set<Function> getCommonFactors()
	{
		Set<Function> factors = new HashSet<Function>();
		if (this instanceof BinaryFunction)
		{
			BinaryFunction b = (BinaryFunction) this;
			Set<Function> leftSubFactors = b.getLeftChild().getCommonFactors();
			Set<Function> rightSubFactors = b.getRightChild().getCommonFactors();
			
			BinaryInstruction i = b.getInstruction();
			
			if (i.equals(BinaryFunction.multiplication)) 
			{
				factors.addAll(leftSubFactors);
				factors.addAll(rightSubFactors);
			}
			else if (i.equals(BinaryFunction.exponentiation)) 
			{
				factors.add(b.getLeftChild());
			}
			else if (i.equals(BinaryFunction.subtraction)
					|| i.equals(BinaryFunction.addition))
			{
				for (Function f1 : leftSubFactors)
					for (Function f2 : rightSubFactors)
						if (f1.equals(f2))
							factors.add(f1);
			}
		}
		else
			factors.add(this);
			
		return factors;
	}
	
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
				Function transformed = p.transform(newThis).simplify();
//				System.out.println(newThis.getString() + " -> " + transformed.getString());
				newThis = transformed;
			}
		}

		newThis.setName(name);
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
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Function) 
		{
			Function f = (Function) o;
			int len = children.length;
			if (f.getChildren().length != len)
				return false;
			
			boolean eq = true;
			for (int i = 0; i < len; i++)
				eq &= children[i].equals(f.getChildren()[i]);
			return eq;
		}
		return false;
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
