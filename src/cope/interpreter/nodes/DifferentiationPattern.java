package cope.interpreter.nodes;

import cope.interpreter.Variable;

public interface DifferentiationPattern 
{
	public abstract Function differentiate(Function f, Variable var);
}
