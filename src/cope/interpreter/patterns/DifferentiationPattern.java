package cope.interpreter.patterns;

import cope.interpreter.Variable;
import cope.interpreter.nodes.Function;

public interface DifferentiationPattern 
{
	public abstract Function differentiate(Function f, Variable var);
}
