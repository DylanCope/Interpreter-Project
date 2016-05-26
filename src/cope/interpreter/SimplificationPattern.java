package cope.interpreter;

import cope.interpreter.nodes.BinaryFunction;
import cope.interpreter.nodes.Constant;
import cope.interpreter.nodes.Function;

public interface SimplificationPattern 
{
	public Function transform(Function f);
	public boolean matches(Function f);
	
	public static final SimplificationPattern zeroMult = new SimplificationPattern()
	{

		@Override
		public Function transform(Function f) 
		{
			return new Constant(0);
		}

		@Override
		public boolean matches(Function f) 
		{
			if (!(f instanceof BinaryFunction))
				return false;
			
			BinaryFunction b = (BinaryFunction) f;
			if (!b.getInstruction().getString().equals("*"))
				return false;
			
			for (Function c : b.getChildren()) 
			{
				if (c instanceof Constant && 
						((Constant) c).getValue() == 0)
					return true;
			}
			
			return false;
		}
		
	};
	
	public static final SimplificationPattern oneMult = new SimplificationPattern()
	{

		@Override
		public Function transform(Function f) {
			BinaryFunction b = (BinaryFunction) f;
			if (b.getLeftChild() instanceof Constant) {
				Constant c = (Constant) b.getLeftChild();
				if (c.getValue() == 1)
					return b.getRightChild().clone();
			}
			return b.getLeftChild().clone();
		}

		@Override
		public boolean matches(Function f) {
			if (!(f instanceof BinaryFunction))
				return false;
			
			BinaryFunction b = (BinaryFunction) f;
			if (!b.getInstruction().getString().equals("*"))
				return false;
			
			for (Function c : b.getChildren()) 
			{
				if (c instanceof Constant && 
						((Constant) c).getValue() == 1)
					return true;
			}
			
			return false;
		}
		
	};
	
	public static final SimplificationPattern divOne = new SimplificationPattern()
	{

		@Override
		public Function transform(Function f) {
			BinaryFunction b = (BinaryFunction) f;
			return b.getLeftChild().clone();
		}

		@Override
		public boolean matches(Function f) {
			if (!(f instanceof BinaryFunction))
				return false;
			
			BinaryFunction b = (BinaryFunction) f; 
			if (!b.getInstruction().getString().equals("/"))
				return false;
			
			if (b.getRightChild() instanceof Constant) {
				Constant c = (Constant) b.getRightChild();
				return c.getValue() == 1;
			}
			return false;
		}
		
	};
	
	public static final SimplificationPattern zeroAdd = new SimplificationPattern()
	{

		@Override
		public Function transform(Function f) 
		{
			BinaryFunction b = (BinaryFunction) f;
			if (b.getLeftChild() instanceof Constant) {
				Constant c = (Constant) b.getLeftChild();
				if (c.getValue() == 0)
					return b.getRightChild().clone();
			}
			return b.getLeftChild().clone();
		}

		@Override
		public boolean matches(Function f) 
		{
			if (!(f instanceof BinaryFunction))
				return false;
			
			BinaryFunction b = (BinaryFunction) f;
			if (!b.getInstruction().getString().equals("+"))
				return false;
			
			for (Function c : b.getChildren()) 
			{
				if (c instanceof Constant && 
						((Constant) c).getValue() == 0)
					return true;
			}
			
			return false;
		}
		
	};
	
	public static final SimplificationPattern constsMult = new SimplificationPattern()
	{

		@Override
		public Function transform(Function f) 
		{
			BinaryFunction b = (BinaryFunction) f;
			Constant c1 = (Constant) b.getLeftChild();
			Constant c2 = (Constant) b.getRightChild();
			return new Constant(c1.getValue() * c2.getValue());
		}

		@Override
		public boolean matches(Function f) 
		{
			if (!(f instanceof BinaryFunction))
				return false;
			
			BinaryFunction b = (BinaryFunction) f;
			if (!b.getInstruction().getString().equals("*"))
				return false;
			
			return b.getLeftChild() instanceof Constant &&
					b.getRightChild() instanceof Constant;
		}
		
	};
	
	public static final SimplificationPattern constsAdd = new SimplificationPattern()
	{

		@Override
		public Function transform(Function f) 
		{
			BinaryFunction b = (BinaryFunction) f;
			Constant c1 = (Constant) b.getLeftChild();
			Constant c2 = (Constant) b.getRightChild();
			return new Constant(c1.getValue() + c2.getValue());
		}

		@Override
		public boolean matches(Function f) 
		{
			if (!(f instanceof BinaryFunction))
				return false;
			
			BinaryFunction b = (BinaryFunction) f;
			if (!b.getInstruction().getString().equals("+"))
				return false;
			
			return b.getLeftChild().getType().equals("constant") &&
					b.getRightChild().getType().equals("constant");
		}
		
	};

	public static final SimplificationPattern mulIntoDiv = new SimplificationPattern()
	{

		@Override
		public Function transform(Function f) {
			BinaryFunction b1 = (BinaryFunction) f;
			BinaryFunction b2 = (BinaryFunction) b1.getRightChild();
			return new BinaryFunction(
				new BinaryFunction(
						b1.getLeftChild().clone(),
						b2.getLeftChild().clone(),
						BinaryFunction.multiplication),
				b2.getRightChild().clone(),
				BinaryFunction.division
			);
		}

		@Override
		public boolean matches(Function f) 
		{
			if (!(f instanceof BinaryFunction))
				return false;

			BinaryFunction b1 = (BinaryFunction) f;
			if (!b1.getInstruction().getString().equals("*"))
				return false;
			
			if (b1.getRightChild() instanceof BinaryFunction) {
				BinaryFunction b2 = (BinaryFunction) b1.getRightChild();
				return b2.getInstruction().getString().equals("/");
			}
			
			return false;
		}
		
	};
	
	public static SimplificationPattern[] patterns = new SimplificationPattern[] 
	{
		zeroMult, zeroAdd, oneMult, divOne, constsMult, constsAdd, mulIntoDiv
	};
}
