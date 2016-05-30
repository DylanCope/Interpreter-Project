package cope.interpreter.nodes;

import java.util.ArrayList;

import cope.interpreter.Variable;
import cope.interpreter.patterns.*;

public class BinaryFunction extends Function 
{
	private BinaryInstruction instruction;
	private Function parent;
	
	public BinaryFunction(Function childNode0, Function childNode1, BinaryInstruction operation)
	{
		this.instruction = operation;
		childNode0.setParent(this);
		childNode1.setParent(this);
		children = new Function[]{childNode0, childNode1};
	}
	
	public float evaluate(Variable var) throws Exception
	{
		ArrayList<Variable> variables = new ArrayList<Variable>();
		variables.add(var); variables.add(Function.E); variables.add(Function.PI);
		return instruction.evaluate(getLeftChild().evaluate(variables), getRightChild().evaluate(variables));
	}
	
	public float evaluate(ArrayList<Variable> variables) throws Exception
	{
		if (!variables.contains(Function.E))
			variables.add(Function.E); 
		if (!variables.contains(Function.PI))
			variables.add(Function.PI);
		return instruction.evaluate(getLeftChild().evaluate(variables), getRightChild().evaluate(variables));
	}

	@Override
	public Function getParent() { return parent; }
	@Override
	public Function setParent(Function parent) { this.parent = parent; return this; }
	
	public Function differentiate(Variable var)
	{
		DifferentiationPattern pattern = null;
		for (int i = 0; i < standardInstructions.length; i++)
			if (instruction.equals(standardInstructions[i])) {
				pattern = standardDifferentiationPatterns[i];
				break;
			}
		Function diff = pattern.differentiate(this, var);
		return diff;
	}
	
	public Function getLeftChild()  { return children[0];  }
	public Function getRightChild() { return children[1]; }
	
	public String getString()
	{
		String str = "";
		
		if (needsBracketting(getLeftChild()))
			str += "(" + getLeftChild().getString() + ")";
		else
			str += getLeftChild().getString();

		String op = instruction.getString();
		if (op.equals("+") || op.equals("-"))
			str += " " + op + " ";
		else str += op;
		
		if (needsBracketting(getRightChild()))
			str += "(" + getRightChild().getString() + ")";
		else
			str += getRightChild().getString();
		
		return str;
	}
	
	private boolean needsBracketting(Function child) 
	{
		if (child.getType().equals("binary")) {
			return instruction.getPriority() < ((BinaryFunction) child).getInstruction().getPriority();
		}
		else return false;
	}
	
	public String getType() { return "binary"; }
	public BinaryInstruction getInstruction() { return instruction; }
	
	
	public static final BinaryInstruction addition = new BinaryInstruction() {
		@Override
		public float evaluate(float arg0, float arg1) { return arg0 + arg1; }
		@Override
		public String getString() { return "+"; } 
		public int getPriority() { return 3; }
	};
	
	public static final BinaryInstruction subtraction = new BinaryInstruction() {
		@Override
		public float evaluate(float arg0, float arg1) { return arg0 - arg1; } 
		@Override
		public String getString() { return "-"; } 
		public int getPriority() { return 4; }
	};
	
	public static final BinaryInstruction multiplication = new BinaryInstruction() {
		@Override
		public float evaluate(float arg0, float arg1) { return arg0 * arg1; } 
		@Override
		public String getString() { return "*"; } 
		public int getPriority() { return 2; }
	};
	
	public static final BinaryInstruction division = new BinaryInstruction() {
		@Override
		public float evaluate(float arg0, float arg1) { return arg0 / arg1; }
		@Override
		public String getString() { return "/"; }  
		public int getPriority() { return 1; }
	};
	
	public static final BinaryInstruction exponentiation = new BinaryInstruction() {
		@Override
		public float evaluate(float arg0, float arg1) { return (float) Math.pow(arg0, arg1); }
		@Override
		public String getString() { return "^"; } 
		public int getPriority() { return 0; }
	};
	
	public static final BinaryInstruction modulo = new BinaryInstruction() {
		@Override
		public float evaluate(float arg0, float arg1) { return arg0 % arg1; }
		@Override
		public String getString() { return "%"; } 
		public int getPriority() { return 5; }
	};
	
	/** Applies the rule (d/dx)(f(x) + g(x)) = f'(x) + g'(x). */
	public static final DifferentiationPattern dAddition = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			return new BinaryFunction(
					f.getChildren()[0].differentiate(var),
					f.getChildren()[1].differentiate(var),
					addition);
		}
	};
	
	/** Applies the rule (d/dx)(f(x) - g(x)) = f'(x) - g'(x). */
	public static final DifferentiationPattern dSubtraction = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			Function c0 = f.getChildren()[0];
			Function c1 = f.getChildren()[1];
			return new BinaryFunction(
				c0.differentiate(var),
				c1.differentiate(var),
				subtraction
				);
		}
	};
	
	/** Applies the rule (d/dx)(f(x)g(x)) = f(x)g'(x) + f'(x)g(x). */
	public static final DifferentiationPattern productRule = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			Function c0 = f.getChildren()[0];
			Function c1 = f.getChildren()[1];
			return new BinaryFunction(
				new BinaryFunction(
						c0, c1.differentiate(var),
						multiplication),
				new BinaryFunction(
						c1, c0.differentiate(var),
						multiplication),
				addition
				);
		}
	};
	
	/** Applies the rule (d/dx)(f(x)/g(x)) = [f'g - fg'] / (g^2). */
	public static final DifferentiationPattern quotientRule = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			Function c0 = f.getChildren()[0];
			Function c1 = f.getChildren()[1];
			return new BinaryFunction(
				new BinaryFunction(
					new BinaryFunction(
						c1, c0.differentiate(var), multiplication
					),
					new BinaryFunction(
						c0, c1.differentiate(var), multiplication
					),
					subtraction
				),
				new BinaryFunction(
					c1, new Constant(2),
					exponentiation
				),
				division
				);
		}
	};
	
	/** Applies the rule (d/dx)(f(x)^g(x)) = (f^g)[g'ln(f) + g(f'/f)]. */
	public static final DifferentiationPattern dExponentiation = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			Function c0 = f.getChildren()[0];
			Function c1 = f.getChildren()[1];
			return new BinaryFunction(
				new BinaryFunction(
					new BinaryFunction(
						c1.differentiate(var),
						new UnaryFunction(
							c0, UnaryFunction.ln
						),
						multiplication
					),
					new BinaryFunction(
						c1,
						new BinaryFunction(
							c0.differentiate(var),
							c0, division),
						multiplication
					),
					addition
				),
				f,
				multiplication
				);
		}
	};
	
	/** Applying that modulo is non-differentiable. */
	public static final DifferentiationPattern dModulo = null;
	
	
	public static BinaryInstruction[] standardInstructions =
		{
			modulo, multiplication, addition, subtraction, division, exponentiation
		};
	
	public static DifferentiationPattern[] standardDifferentiationPatterns =
		{
			dModulo, productRule, dAddition, dSubtraction, quotientRule, dExponentiation
		};
	
}
