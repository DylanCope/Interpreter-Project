package cope.interpreter.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cope.interpreter.Variable;

public class Constant extends Function
{
	private float constant;
	private Function parent;
	
	public Constant(float constant)
	{
		this.constant = constant;
	}
	
	public Constant() {}
	
	public float evaluate(ArrayList<Variable> variables) { return this.constant; }
	public float evaluate(Variable var) { return this.constant; }

	@Override
	public Function getParent() { return parent; }
	@Override
	public Function setParent(Function parent) { this.parent = parent; return this; }

	@Override
	public Function differentiate(Variable var) {
		return new Constant(0);
	}

	@Override
	public String getString() {
		return Float.toString(constant);
	}

	@Override
	public String getType() {
		return "constant";
	}
	
	@Override
	public Set<String> getVariables()
	{
		return new HashSet<String>();
	}
	
	public float getValue() { return constant; }
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Constant) {
			Constant c = (Constant) o;
			return c.getValue() == getValue();
		}
		return false;
	}

}

