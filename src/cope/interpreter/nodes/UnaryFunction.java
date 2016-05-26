package cope.interpreter.nodes;

import java.util.ArrayList;

import cope.interpreter.Variable;

public class UnaryFunction extends Function
{
	private UnaryInstruction instruction;
	private Function childFunction;
	private Function parent;
	
	public UnaryFunction(Function childNode, UnaryInstruction operation)
	{
		this.instruction = operation; this.childFunction = childNode;
		childFunction.setParent(this);
		children = new Function[]{childFunction};
	}
	
	@Override
	public float evaluate(ArrayList<Variable> variables) throws Exception
	{
		return instruction.evaluate(childFunction.evaluate(variables));
	}
	
	@Override
	public float evaluate(Variable var) throws Exception {
		return instruction.evaluate(childFunction.evaluate(var));
	}

	@Override
	public Function getParent() { return parent; }
	@Override
	public Function setParent(Function parent) { this.parent = parent; return this; }
	public UnaryInstruction getInstruction() { return instruction; }
	
	@Override
	public Function differentiate(Variable var) {
		DifferentiationPattern pattern = null;
		for (int i = 0; i < standardInstructions.length; i++)
			if (instruction.equals(standardInstructions[i])) {
				pattern = standardDifferentiationPatterns[i];
				break;
			}

		Function diff = pattern.differentiate(this, var);
		return diff;
	}
	
	public String getString()
	{
		return instruction.getString() + "(" + childFunction.getString() + ")";
	}
	
	public String getType() { return "unary"; }
	
	public static final UnaryInstruction sin = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.sin(arg); }
		@Override
		public String getString() { return "sin"; }
	};

	public static final UnaryInstruction cos = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.cos(arg); }
		@Override
		public String getString() { return "cos"; }
	};

	public static final UnaryInstruction tan = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.tan(arg); }
		@Override
		public String getString() { return "tan"; }
	};

	public static final UnaryInstruction abs = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.abs(arg); }
		@Override
		public String getString() { return "abs"; }
	};

	public static final UnaryInstruction sinh = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.sinh(arg); }
		@Override
		public String getString() { return "sinh"; }
	};

	public static final UnaryInstruction cosh = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.cosh(arg); }
		@Override
		public String getString() { return "cosh"; }
	};

	public static final UnaryInstruction tanh = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.tanh(arg); }
		@Override
		public String getString() { return "tanh"; }
	};

	public static final UnaryInstruction floor = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.floor(arg); }
		@Override
		public String getString() { return "floor"; }
	};

	public static final UnaryInstruction ceil = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.ceil(arg); }
		@Override
		public String getString() { return "ceil"; }
	};

	public static final UnaryInstruction sqrt = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.sqrt(arg); }
		@Override
		public String getString() { return "sqrt"; }
	};
	
	public static final UnaryInstruction ln = new UnaryInstruction() {
		@Override
		public float evaluate(double arg) { return (float) Math.log(arg); }
		@Override
		public String getString() { return "ln"; }
	};
	
	public static Function chainRule(UnaryInstruction i, Function f, Variable var) {
		Function child = f.getChildren()[0];
		return new BinaryFunction(
			child.differentiate(var),
			new UnaryFunction(child, i),
			BinaryFunction.multiplication
		);
	}
	
	/** Applying (d/dx)[sin(f(x))] = f'cos(f). */
	public static final DifferentiationPattern dSin = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			return chainRule(cos, f, var);
		}
	};

	/** Applying (d/dx)[cos(f(x))] = -f'sin(f). */
	public static final DifferentiationPattern dCos = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			return new BinaryFunction(
				chainRule(sin, f, var),
				new Constant(-1),
				BinaryFunction.multiplication
			);
		}
	};

	/** Applying (d/dx)[tan(f(x))] = f'/(cos^2(f)). */
	public static final DifferentiationPattern dTan = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			Function child = f.getChildren()[0];
			return new BinaryFunction(
				child.differentiate(var),
				new BinaryFunction(
					new UnaryFunction(child, cos),
					new Constant(2),
					BinaryFunction.exponentiation
				),
				BinaryFunction.division
			);
		}
	};
	
	public static final DifferentiationPattern dSinh = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			return chainRule(cosh, f, var);
		}
	};
	
	public static final DifferentiationPattern dCosh = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			return chainRule(sinh, f, var);
		}
	};

	public static final DifferentiationPattern dTanh = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			Function child = f.getChildren()[0];
			return new BinaryFunction(
				child.differentiate(var),
				new BinaryFunction(
					new UnaryFunction(child, cosh),
					new Constant(2),
					BinaryFunction.exponentiation
				),
				BinaryFunction.division
			);
		}
	};
	
	public static final DifferentiationPattern dSqrt = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			Function child = f.getChildren()[0];
			return new BinaryFunction(
				child.differentiate(var),
				f, BinaryFunction.division
			);
		}
	};
	
	public static final DifferentiationPattern dLn = new DifferentiationPattern() {
		@Override
		public Function differentiate(Function f, Variable var) {
			Function child = f.getChildren()[0];
			return new BinaryFunction(
					child.differentiate(var), 
					child, BinaryFunction.division
			);
		}
	};
	
	public static UnaryInstruction[] standardInstructions =
		{
			abs, ceil, cos, cosh, floor, ln, sin, sinh, sqrt, tan, tanh
		};

	public static DifferentiationPattern[] standardDifferentiationPatterns =
		{
			null, null, dCos, dCosh, null, dLn, dSin, dSinh, dSqrt, dTan, dTanh
		};

}
