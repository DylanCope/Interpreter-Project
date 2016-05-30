package cope.interpreter.patterns;

public interface UnaryInstruction
{
	public float evaluate(double arg);
	public String getString();

	/**
	 * getLatex assumes the returned string is to be 
	 * used in the LaTeX mathmode environment.
	 */
	public String getLatex();
}
