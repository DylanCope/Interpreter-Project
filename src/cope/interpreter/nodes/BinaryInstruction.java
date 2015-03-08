package cope.interpreter.nodes;

public interface BinaryInstruction
{
	public int getPriority();
	public String getString();
	public float evaluate(float arg0, float arg1);
	
}
