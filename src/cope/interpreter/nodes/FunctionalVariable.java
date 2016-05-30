package cope.interpreter.nodes;

import java.util.ArrayList;
import java.util.Arrays;

import cope.interpreter.Variable;

public class FunctionalVariable extends Function
{
	private String name;
	private Function parent;
	
	public FunctionalVariable(String name)
	{
		this.name = name;
	}
	
	@Override
	public float evaluate(ArrayList<Variable> variables) throws Exception {
		for (Variable var : variables)
			if (var.getName().equals(name))
				return var.get();

		throw new Exception("Invalid variable: " + name + " cannot be matched to any variable in " + Arrays.toString(variables.toArray()));
	}
	
	public float evaluate(Variable var) throws Exception
	{
		if (var.getName().equals(name))
			return var.get();
		else throw new Exception("Invalid variable: " + name + " cannot be matched to " + var.getName());
	}

	@Override
	public Function getParent() { return parent; }
	@Override
	public Function setParent(Function parent) { this.parent = parent; return this; }
	
	public String getName() { return name; }
	
	@Override
	public Function differentiate(Variable var) 
	{
		if (var.getName().equals(name))
			return new Constant(1);
		else return new Constant(0);
	}
	
	public String getString() { return name; }

	@Override
	public String getType() {
		return "variable";
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof FunctionalVariable) {
			FunctionalVariable v = (FunctionalVariable) o;
			return v.getName().equals(name);
		}
		return false;
	}

}
