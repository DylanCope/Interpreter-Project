package cope.interpreter;
import java.util.HashSet;
import java.util.Set;

import cope.interpreter.nodes.BinaryFunction;
import cope.interpreter.nodes.Function;
import cope.interpreter.nodes.UnaryFunction;
import cope.interpreter.patterns.BinaryInstruction;
import cope.interpreter.patterns.UnaryInstruction;

public class Interpreter 
{
	BinaryInstruction[] binops;
	UnaryInstruction[] unops;
	
	private final static BinaryInstruction lowestPriority = new BinaryInstruction() 
	{
		@Override
		public int getPriority() { return Integer.MIN_VALUE; }

		@Override
		public String getString() { return null; }

		@Override
		public float evaluate(float arg0, float arg1) { return 0; }
	
	};
	
	public Interpreter()
	{
		binops = BinaryFunction.standardInstructions;
		unops = UnaryFunction.standardInstructions;
	}
	
	public Interpreter(BinaryInstruction[] binops, UnaryInstruction[] unops)
	{
		this.binops = binops;
		this.unops = unops;
	}
	
	public Function parseString(String str)
	{
		return parseString(str, "x");
	}
	
	public Function parseString(String str, String v)
	{
		return parseString(str, new Variable(v));
	}
	
	public Function parseString(String str, Variable v)
	{
		Set<Variable> vars = new HashSet<Variable>();
		vars.add(v);
		return parseString(str, vars);	
	}
	
	public Function parseString(String str, Set<Variable> vars)
	{
		return null;
	}
	
	/**
	 *
	 * @param i0 The index that it is assumed a '(' character has been read.
	 * @param str The given string.
	 * @return The index of the close bracket corresponding to the open at i0.
	 * @throws Exception if the bracketing is unbalanced.
	 */
	public int bracketCloseIndex(int i0, String str) throws Exception
	{
		int openBrackets = 1;
		int len = str.length();
		int i;
		for (i = i0+1; i < len; i++) 
		{
			char c = str.charAt(i);
			if (c == ')') openBrackets--;
			if (c == '(') openBrackets++;
			if (openBrackets == 0)
				return i;
		}
		if (openBrackets != 0)
			throw new Exception("Unbalanced bracketting");
		return i;
	}
	
	/**
	 * Counts the number of binary operations outside brackets in str.
	 * For example, str="x+(3*x)" returns 1, whereas str="x+3*(x)" returns 2.
	 * @param str
	 * @return
	 * @throws Exception if the bracketing is mismatched.
	 */
	public int unbracketedBinops(String str) throws Exception
	{
		String result = str;
		int len = str.length();
		int unbracketed = 0;
		for (int i = 0; i < len; i++)
		{	
			char c = result.charAt(i);
			if (c == '(') 
			{
				i = bracketCloseIndex(i, str);
				continue;
			}

			for (BinaryInstruction op : binops)
			{
				String x = result.substring(i, i + op.getString().length());
				if (x.equals(op.getString())) 
					unbracketed++;
			}
		}
		return unbracketed;
	}
	
	/**
	 * Places parentheses on str to properly distinguish how the prioritization of
	 * binary operations is parsed. There is no regard for unary operators or
	 * variables. Superfluous brackets aren't removed.
	 * @param str - string to be bracketed.
	 * @return A new string that represents a properly bracketed str
	 * @throws Exception if there is a mismatch of brackets already in str.
	 */
	public String placeBrackets(String str) throws Exception
	{
		if (unbracketedBinops(str) <= 1)
			return str;
		
		String result = str;
		int len = str.length();
		
		BinaryInstruction best = lowestPriority;
		
		int bestIndex = -1; 
		for (int i = 0; i < len; i++)
		{	
			char c = result.charAt(i);
			if (c == '(') 
			{
				int close = bracketCloseIndex(i, str);
				String substr = result.substring(i+1, close);
				if (close != i+1) 
				{
					String temp = result.substring(0, i+1);
					temp += placeBrackets(substr);
					i = temp.length();
					temp += result.substring(close);
					result = temp;
					len = result.length();
				}
				else i = close;
//				i = close;
				continue;
			}

			for (BinaryInstruction op : binops)
			{
				String x = result.substring(i, i + op.getString().length());
				if (x.equals(op.getString())) {
					if (op.getPriority() > best.getPriority()) {
						best = op;
						bestIndex = i;
					}
				}
			}
		}
		
		if (bestIndex != -1)
		{
			String left = result.substring(0, bestIndex);
			int i = bestIndex + best.getString().length();
			String right = result.substring(i);
			
			left = placeBrackets(left);
			right = placeBrackets(right);
			
			String temp = "";
			if (unbracketedBinops(left) > 0) {
				temp += "(";
				temp += left;
				temp += ")";
			}
			else temp += left;
			
			temp += best.getString();

			if (unbracketedBinops(right) > 0) {
				temp += "(";
				temp += right;
				temp += ")";
			}
			else temp += right;
			
			result = temp;
		}
		return result;
	}
	
	/**
	 * Removes brackets that are unnecessary for distinguishing the
	 * structure of the statement str
	 * @param str - string to be parsed.
	 * @return A version of str without superfluous bracketing
	 * @exception Exception thrown if str contains mismatching brackets
	 */
	public String removeSuperfluousBrackets(String str) throws Exception
	{
		int len = str.length();
		String result = str;
		
		while (result.startsWith("((") && result.endsWith("))")) {
			result = result.substring(1, len-1);
			len = result.length();
		}
		
		for (int i = 0; i < len; i++)
		{
			char c = result.charAt(i);
			if (c == '(')
			{
				int close = bracketCloseIndex(i, str);
				System.out.println("index " + i + " to " + close + " in " + result);
				String substr = result.substring(i, close);
//				System.out.println(str + " -> " + substr);
				String temp = result.substring(0, i);
				temp += removeSuperfluousBrackets(substr);
				i = temp.length();
				temp += result.substring(close);
				result = temp;
				len = result.length();
			}
		}
		
		return result;
	}
}
