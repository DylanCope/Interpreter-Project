package cope.interpreter;

public class Variable {
	private String name;
	private float value = 0;
	
	public Variable(String name) 
	{
		this.name = name;
	}
	
	public Variable(String name, float value)
	{
		this.name = name; this.value = value;
	}
	
	public String getName() { return this.name; }
	public float get() { return this.value; }
	
	public void setName(String name) { this.name = name; }
	public void set(float value) { this.value = value; }
	
	public String toString() { return name; }
}
