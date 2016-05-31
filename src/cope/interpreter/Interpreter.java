package cope.interpreter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import cope.interpreter.nodes.BinaryFunction;
import cope.interpreter.nodes.Constant;
import cope.interpreter.nodes.Function;
import cope.interpreter.nodes.FunctionalVariable;
import cope.interpreter.nodes.UnaryFunction;
import cope.interpreter.patterns.BinaryInstruction;
import cope.interpreter.patterns.UnaryInstruction;

public class Interpreter 
{
	private BinaryInstruction[] binops;
	private UnaryInstruction[] unops;
	private Set<Variable> standardVars;
	
	private final static BinaryInstruction lowestPriority = new BinaryInstruction() {
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
		
		// arrays are sorted so that conflicts such as sinh and sin are handled naturally
		Arrays.sort(binops, new Comparator<BinaryInstruction>() {

			@Override
			public int compare(BinaryInstruction arg0, BinaryInstruction arg1) {
				return arg0.getString().compareTo(arg1.getString());
			}
			
		});
		Arrays.sort(unops, new Comparator<UnaryInstruction>() {

			@Override
			public int compare(UnaryInstruction arg0, UnaryInstruction arg1) {
				return arg1.getString().compareTo(arg0.getString());
			}
			
		});
		
		standardVars = new HashSet<Variable>();
		standardVars.add(Function.PI); 
		standardVars.add(Function.E); 
	}
	
	public Interpreter(BinaryInstruction[] binops, UnaryInstruction[] unops, Collection<Variable> vars)
	{
		this.binops = binops;
		this.unops = unops;
	}
	
	public Function parse(String str) throws Exception
	{
		return parse(str, "x");
	}
	
	public Function parse(String str, String...vars) throws Exception
	{
		Set<Variable> vs = new HashSet<Variable>();
		for (String v : vars)
			vs.add(new Variable(v));
		return parse(str, vs);
	}
	
	public Function parse(String str, Set<Variable> vars) throws Exception
	{
		str = removeSpaces(str);
		str = removeSuperfluousBrackets(str);
		str = placeBrackets(str);
		vars.addAll(standardVars);
		return parseFormatted(str, vars);
	}
	
	private String removeSpaces(String str)
	{
		String[] split = str.split("\\s++");
		String result = "";
		for (String s : split)
			result += s;
		return result;
	}
	
	/**
	 * Attempts to build a function tree that describes a given string, assuming
	 * the string has been formatted properly. A properly formatted
	 * string should be correctly bracketed according the the prioritization of
	 * the binary operations.
	 * @param str - String to be parsed.
	 * @param vars
	 * @return
	 * @throws Exception
	 */
	private Function parseFormatted(String str, Set<Variable> vars) throws Exception
	{
		int len = str.length();
		if (str.startsWith("(") && bracketCloseIndex(0, str) == len - 1)
			return parseFormatted(str.substring(1, len-1), vars);

		try {
			return buildBinop(str, vars);
		}
		catch (Exception e1) {
			try {
				return buildUnop(str, vars);
			} 
			catch (Exception e2) {
				try {
					return parseVariable(str, vars);
				} 
				catch (Exception e3) {
					try {
						return parseConstant(str);
					}
					catch (Exception e4){
						throw new Exception("Cannot parse " + str);
					}
				}
			}
		}
		
	}
	
	/**
	 * Attempts to parse a string a binary operation. This assumes the given
	 * string has been perfected bracketed according to the prioritizations of
	 * binary operations that have been given to the interpreter.
	 * @param str - the String to be parsed.
	 * @param vars - A list of possible variables in str.
	 * @return A "BinaryFunction" node that describes str.
	 * @throws Exception if str cannot be parsed as a binary operation.
	 */
	private Function buildBinop(String str, Set<Variable> vars) throws Exception
	{
		int len = str.length();
		for (int i = 0; i < len; i++)
		{
			if (str.charAt(i) == '(') {
				i = bracketCloseIndex(i, str);
				continue;
			}
			
			for (BinaryInstruction op : binops)
			{
				String opstr = op.getString();
				int j = i + opstr.length();
				String substr = str.substring(i, j);
				if (substr.equals(opstr))
				{
					String lstr = str.substring(0, i);
					String rstr = str.substring(j);
					return new BinaryFunction(
						parseFormatted(lstr, vars),
						parseFormatted(rstr, vars),
						op
					);
				}
			}
		}
		throw new Exception("Cannot build binary function from " + str);
	}
	
	/**
	 * Attempts to parse a string as a unary operator, based on the list
	 * of unary operators the interpreter has been told to accept.
	 * @param str - String to be parsed.
	 * @param vars - A list of possible variables in str.
	 * @return A "UnaryFunction" node describing str.
	 * @throws Exception if str cannot be parsed as a unary operation.
	 */
	private Function buildUnop(String str, Set<Variable> vars) throws Exception
	{
		for (UnaryInstruction op : unops)
		{
			String opstr = op.getString();
			int i = opstr.length();
			String substr = str.substring(0, i);
			if (substr.equals(opstr))
				return new UnaryFunction(
					parseFormatted(str.substring(i), vars), op
				);
		}
		throw new Exception("Cannot build unary function from " + str);
	}
	
	/**
	 * Attempts to parse a string as a variable, given a list of possible
	 * variables.
	 * @param str - String to be parsed.
	 * @param vars - List of possible variables.
	 * @return A "FunctionalVariable" node describing str.
	 * @throws Exception if str cannot be parsed as a variable.
	 */
	private Function parseVariable(String str, Set<Variable> vars) throws Exception
	{
		for (Variable v : vars) {
			if (str.equals(v.getName()))
				return new FunctionalVariable(v.getName());
		}
		throw new Exception("Cannot parse + \"" + str + "\" as a variable.");
	}
	
	/**
	 * Attempts to pass a given string a floating point constant.
	 * @param str - String to be parsed.
	 * @return A "Constant" function node with a value described by str 
	 * @throws Exception if str cannot be parsed a floating point number.
	 */
	private Function parseConstant(String str) throws Exception
	{
		try {
			float value = Float.parseFloat(str);
			return new Constant(value);
		}
		catch (Exception e) {
			throw new Exception("Cannot parse + \"" + str + "\" as a constant.");
		}
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
			throw new Exception("Unbalanced bracketting in \"" + str + "\" from " + i0);
		return i-1;
	}
	
	/**
	 * Counts the number of binary operations outside brackets in str.
	 * For example, str="x+(3*x)" returns 1, whereas str="x+3*(x)" returns 2.
	 * @param str
	 * @return
	 * @throws Exception if the bracketing is mismatched.
	 */
	private int unbracketedBinops(String str) throws Exception
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
	private String placeBrackets(String str) throws Exception
	{
		if (unbracketedBinops(str) == 0)
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
				int close = bracketCloseIndex(i, result);
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
//			if (unbracketedBinops(left) > 0) {
				temp += "(" + left + ")";
//			}
//			else temp += left;
			
			temp += best.getString();

//			if (unbracketedBinops(right) > 0) {
				temp += "(" + right +  ")";
//			}
//			else temp += right;
			
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
	private String removeSuperfluousBrackets(String str) throws Exception
	{
		int len = str.length();
		String result = str;
		
		for (int i = 0; i < len; i++)
		{
			char c1 = result.charAt(i);
			if (c1 == '(' && i+1 < len)
			{
				int close1 = bracketCloseIndex(i, result);
				char c2 = result.charAt(i+1);
				if (c2 != '(') 
					continue;
				int close2 = bracketCloseIndex(i+1, result);
				if (close2 != close1 - 1)
					continue;
				
				String substr = result.substring(i+1, close2);
				String temp = result.substring(0, i);
				temp += substr;
				temp += result.substring(close1);
				result = temp;
				len = result.length();
			}
		}
		
		return result;
	}
}
