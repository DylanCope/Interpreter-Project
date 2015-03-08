package cope.interpreter;

import java.util.ArrayList;
import java.util.Arrays;

import cope.interpreter.nodes.BinaryFunction;
import cope.interpreter.nodes.BinaryInstruction;
import cope.interpreter.nodes.Constant;
import cope.interpreter.nodes.Function;
import cope.interpreter.nodes.FunctionalVariable;
import cope.interpreter.nodes.UnaryFunction;
import cope.interpreter.nodes.UnaryInstruction;

public class StringAnalysis 
{
	/** 
	 * The standardBinaryOperators are a sorted list of standard, predefined symbols for the various
	 * different operations defined in BinaryOperationNode.standardInterfaces. The index of the symbol in
	 * this list is the same as the index in the interfaces list.
	 * If more operations are wanted, then they need to be defined here as a symbol, as a function in the
	 * interfaces list, and ordered according to their ASCII value.
	 */
	public static char[] standardBinaryOperatorSymbols = 
		{
			'%', '*', '+', '-', '/', '^'
		};
	
	/**The binaryOperatoPriorities are used to apply priorities to the binary operators. By default, the
	 * BIDMAS priorities are programmed in.
	 * The priority refers to the corresponding operator at the same index in the
	 * operators array. 
	 * 
	 */
	public static int[] standardBinaryOpertorPriorities =
		{
			5, 2, 3, 4, 1, 0
		};

	/** 
	 * The standardUnaryOperators are a sorted list of standard, predefined strings for the various
	 * different operations defined in UnaryOperationNode.standardInterfaces. The index of the string in
	 * this list is the same as the index in the interfaces list.
	 * If more operations are wanted, then they need to be defined here as a string, as a function in the
	 * interfaces list, and ordered alphabetically.
	 */
	public static String[] standardUnaryOperatorNames =
		{
			"abs", "ceil", "cos", "cosh", "floor", "ln", "sin", "sinh", "sqrt", "tan", "tanh"
		};
	
	/**
	 * Generates a function tree from a string, based on the assumptions that the function is a single-variable function
	 * of x and there are no more than the standard unary (with standard priorities) and binary operations.
	 * @param strFunction
	 * @return
	 * @throws Exception
	 */
	public static Function getFunction(String strFunction) throws Exception
	{
		return getFunction(strFunction, 'x');
	}
	
	/**
	 * Generates a function tree from a string, based on the assumptions that the function is a single-variable function
	 * and there are no more than the standard unary (with standard priorities) and binary operations.
	 * @param strFunction @param variable
	 * @return
	 * @throws Exception
	 */
	public static Function getFunction(String strFunction, char variable) throws Exception 
	{ 
		String[] variables = new String[]{"" + variable, "pi", "e"};
		Arrays.sort(variables);
		return getFunction(strFunction, variables);
	}

	/**
	 * Generates a function tree from a string, based on the assumptions that the function is a single-variable function
	 * and there are no more than the standard unary (with standard priorities) and binary operations.
	 * @param strFunction @param variable
	 * @return
	 * @throws Exception
	 */
	public static Function getFunction(String strFunction, String variable) throws Exception
	{
		String[] variables = new String[]{variable, "pi", "e"};
		Arrays.sort(variables);
		return getFunction(strFunction, variables);
	}

	/**
	 * Generates a function tree from a string, based on the assumptions that the function is a multi-variable function
	 * and there are no more than the standard unary (with standard priorities) and binary operations.
	 * @param strFunction
	 * @param variable
	 * @return
	 * @throws Exception
	 */
	public static Function getFunction(String strFunction, String[] variables) throws Exception
	{
		String[] vars = new String[variables.length + 2];
		vars[0] =  "e"; vars[1] = "pi";
		for (int i = 2; i < vars.length; i++) vars[i] = variables[i - 2];
		Arrays.sort(vars);
		return getFunction(strFunction, vars, 
				standardBinaryOperatorSymbols, BinaryFunction.standardInstructions, standardBinaryOpertorPriorities, 
				standardUnaryOperatorNames, UnaryFunction.standardInstructions);
	}
	
	/**
	 * Generates a function tree from a string, based on the assumptions that the function is a multi-variable function
	 * and all the lists are sorted and all the lengths of the lists are what they should be (see the documentation for the
	 * lists for details).
	 * @param strFunction
	 * @param variables
	 * @param binarySymbols
	 * @param binaryInstructions
	 * @param binaryPriorities
	 * @param unaryNames
	 * @param unaryInstructions
	 * @return
	 * @throws Exception
	 */
	public static Function getFunction(
			String strFunction, String[] variables, char[] binarySymbols, BinaryInstruction[] binaryInstructions, 
			int[] binaryPriorities, String[] unaryNames, UnaryInstruction[] unaryInstructions)
		throws Exception
	{
		strFunction = removeSpaces(strFunction);
		if (!balancedBrackets(strFunction))
			throw new Exception("Brackets not balanced");
		if (checkForVariableAndBinaryOperatorOverlap(variables, binarySymbols))
			throw new Exception("There is an overlay between the variables and the binary operators:\n"
					+ "Binary Operators = " + Arrays.toString(binarySymbols) + "\n"
					+ "Variables = " + Arrays.toString(variables));
		if (checkForVariableAndUnaryOperatorOverlap(variables, unaryNames))
			throw new Exception("There is an overlay between the variables and the unary operators:\n"
					+ "Unary Operators = " + Arrays.toString(unaryNames) + "\n"
					+ "Variables = " + Arrays.toString(variables));
		
		String[] verifiedStrings = new String[variables.length + unaryNames.length];
		for (int i = 0; i < variables.length; i++)
			verifiedStrings[i] = variables[i];
		for (int i = variables.length; i < verifiedStrings.length; i++)
			verifiedStrings[i] = unaryNames[i - variables.length];
		
		strFunction = separateVerifiedStrings(verifiedStrings, strFunction);
		strFunction = transformToBinaryOperations(strFunction, binarySymbols, binaryPriorities);
		
		return recursivelyGenerateFunctionTree(
				strFunction, variables, 
				binarySymbols, binaryInstructions, 
				unaryNames, unaryInstructions);
	}
	
	private static Function recursivelyGenerateFunctionTree(
			String strFunction, String[] variables, char[] binarySymbols, BinaryInstruction[] binaryInstructions, 
			String[] unaryNames, UnaryInstruction[] unaryInstructions)
		throws Exception
	{
		if (strFunction.charAt(0) == '(' && strFunction.charAt(strFunction.length() - 1) == ')') 
			strFunction = findBracketedString(strFunction);
		try {
			String[] operation = isolateBinaryOperation(strFunction, binarySymbols);
			Function leftNode, rightNode;
			
			if (operation[0].charAt(0) == '(')
				leftNode = recursivelyGenerateFunctionTree(
						operation[0], variables, binarySymbols, binaryInstructions, unaryNames, unaryInstructions);
			else try {
				leftNode = new Constant(Integer.parseInt(operation[0]));
			} catch (Exception e0) {
				try {
					leftNode = parseAsUnaryFunction(
							operation[0], variables, 
							binarySymbols, binaryInstructions, 
							unaryNames, unaryInstructions);
				} catch (Exception e1){
					if (Arrays.binarySearch(variables, operation[0]) >= 0)
						leftNode = new FunctionalVariable(operation[0]).setFunctionString(operation[0]);
					else throw e1;
				}
			}
			
			if (operation[2].charAt(0) == '(')
				rightNode = recursivelyGenerateFunctionTree(
						operation[2], variables, 
						binarySymbols, binaryInstructions, 
						unaryNames, unaryInstructions);
			else try {
				rightNode = new Constant(Integer.parseInt(operation[2]));
			} catch (Exception e0) {
				try {
					rightNode = parseAsUnaryFunction(
							operation[2], variables, 
							binarySymbols, binaryInstructions, 
							unaryNames, unaryInstructions);
				} catch (Exception e1){
					if (Arrays.binarySearch(variables, operation[2]) >= 0)
						rightNode = new FunctionalVariable(operation[2]).setFunctionString(operation[2]);
					else throw e1;
				}
			}
			return new BinaryFunction(
					leftNode, 
					rightNode, 
					binaryInstructions[Arrays.binarySearch(binarySymbols, operation[1].charAt(0))])
						.setFunctionString(strFunction);
		} catch (Exception e) {
			try {
				return parseAsUnaryFunction(
						strFunction, variables, 
						binarySymbols, binaryInstructions, 
						unaryNames, unaryInstructions);
			} catch (Exception e0){
				throw e0;
			}
		}
	}
	
	private static Function parseAsUnaryFunction(
			String strFunction, String[] variables, 
			char[] binarySymbols, BinaryInstruction[] binaryInstructions, 
			String[] unaryNames, UnaryInstruction[] unaryInstructions)
		throws Exception
	{
		String function;
		for (int i = 0; i < strFunction.length(); i++)
			if (strFunction.charAt(i) == '(') {
				function = strFunction.substring(0, i);
				if (function.equals("")) break;
				try {
					return new UnaryFunction(
							recursivelyGenerateFunctionTree(
									strFunction.substring(i), variables, binarySymbols, binaryInstructions, unaryNames, unaryInstructions),
							unaryInstructions[Arrays.binarySearch(unaryNames, function)]).setFunctionString(strFunction);
				} catch (Exception e) {
					throw new Exception("Unidentified string: " + function);
				}
			}
		throw new Exception("Unidentified string: " + strFunction);
	}
	
	/** Takes a list of strings that have been verified to be a part of the function and isolates them, assuming
	 * the operation between the strings and numbers is multiplication. */
	private static String separateVerifiedStrings(String[] verifiedStrings, String function)
	{
		for (String str : verifiedStrings)
			for (int i = 0; i < function.length() - str.length(); i++) {
				String substring = function.substring(i, i + str.length());
				if (substring.equals(str)) {
					if (i-1 >= 0 && Character.isDigit(function.charAt(i-1)))
						for (int j = i - 1; j >= 0; j--)
							if (!Character.isDigit(function.charAt(j))) {
								function = function.substring(0, j+1) 
										+ function.substring(j+1, i) + "*" + function.substring(i, i+str.length())
										+ function.substring(i+str.length());
								i += str.length() + 1;
								break;
							}
							else if (j == 0) {
								function =  function.substring(0, i) + "*" + function.substring(i, i+str.length())
										+ function.substring(i+str.length());
								i += str.length() + 1;
								break;
							}
				}
			}
		return function;
	}
	
	private static boolean checkForVariableAndBinaryOperatorOverlap(String[] variables, char[] binaryOperators)
	{
		for (int i = 0; i < variables.length; i++)
			for (int j = i; j < binaryOperators.length; j++)
				if (variables[i].charAt(0) == binaryOperators[j]) return true;
		return false;
	}
	
	private static boolean checkForVariableAndUnaryOperatorOverlap(String[] variables, String[] unaryOperators)
	{
		for (int i = 0; i < variables.length; i++)
			for (int j = i; j < unaryOperators.length; j++)
				if (variables[i].equals(unaryOperators[j])) return true;
		return false;
	}
	
	private static boolean areThereBinaryOperations(String str, char[] operators)
	{
		int bracketCount = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '(') bracketCount++;
			else if (str.charAt(i) == ')') bracketCount--;
			if (Arrays.binarySearch(operators, str.charAt(i)) >= 0
					&& bracketCount == 0)
				return true;
		}
		return false;
	}
	
	private static String transformToBinaryOperations(String str, char[] operators, int[] priorities) 
		throws Exception
	{
		if (!areThereBinaryOperations(str, operators)) return str;
		
		ArrayList<String[]> possibilities = new ArrayList<String[]>();
		for (int i = 0; i < str.length(); i++) {
			
			if (str.charAt(i) == '(') {
				String bracketedString = findBracketedString(str.substring(i));
				int numberOfBinaryOperations = 0;
				int bracketCount = 0;
				for (int j = 0; j < bracketedString.length(); j++) {
					if (bracketedString.charAt(j) == '(') bracketCount++;
					else if (bracketedString.charAt(j) == ')') bracketCount--;
					else if (bracketCount == 0 && 
							Arrays.binarySearch(operators, bracketedString.charAt(j)) >= 0)
						numberOfBinaryOperations++;
				}
				if (numberOfBinaryOperations > 1) {
					
					String fst = str.substring(0, i+1);
					String snd = str.substring(i + bracketedString.length() + 1);
					
					bracketedString = transformToBinaryOperations(bracketedString, operators, priorities);
					str = fst + bracketedString + snd;
					return transformToBinaryOperations(str, operators, priorities);
				}
				else if (numberOfBinaryOperations == 0)
					return str;
				i += bracketedString.length();
			}
			else if (Arrays.binarySearch(operators, str.charAt(i)) >= 0)
				possibilities.add(new String[]{
						str.substring(0, i), 
						str.substring(i, i+1), 
						str.substring(i+1)});
		}
		
		possibilities = sortBasedOnPriorities(
				possibilities, operators, priorities);
		
		String mostImportantOperation = "(";
		String lessImportantRightSide = "";
		String lessImportantLeftSide = "";
		
		String fst = possibilities.get(0)[0];
		String snd = possibilities.get(0)[2];
		if (fst.length() == 0 || snd.length() == 0)
			throw new Exception("Non well-formed binary operation expressions found.");
		int bracketCount = 0;
		
		for (int i = fst.length() - 1; i >= 0; i--) {
			if (fst.charAt(i) == ')') bracketCount++;
			else if (fst.charAt(i) == '(') bracketCount--;
			if (bracketCount == 0 && 
					(Arrays.binarySearch(operators, fst.charAt(i)) >= 0 || i == 0)) {
				if (i == 0) i--;
				mostImportantOperation += fst.substring(i+1, fst.length());
				lessImportantLeftSide = fst.substring(0, i+1);
				break;
			}
		}
		mostImportantOperation += possibilities.get(0)[1];
		
		bracketCount = 0;
		for (int i = 0; i < snd.length(); i++) {
			if (snd.charAt(i) == ')') bracketCount++;
			else if (snd.charAt(i) == '(') bracketCount--;
			if (bracketCount == 0 && 
					(Arrays.binarySearch(operators, snd.charAt(i)) >= 0 || i == snd.length() - 1)) {
				if (i != snd.length() - 1) i--;
				mostImportantOperation += snd.substring(0, i+1);
				lessImportantRightSide = snd.substring(i+1, snd.length());
				break;
			}
		}
		mostImportantOperation += ")";
		
		String bracketedExpression = lessImportantLeftSide + mostImportantOperation + lessImportantRightSide;
		if (possibilities.size() > 2) return transformToBinaryOperations(bracketedExpression, operators, priorities);
		else return bracketedExpression;
	}
	
	/** This function orders a list of possible binary operations in an expression based on the priorities of the 
	 *  operations. The sort algorithm used is bubble sort: {@link http://en.wikipedia.org/wiki/Bubble_sort}
	 *  
	 *  @param ps is the possible binary operations.
	 *  @param ops is the array of existing binary operations.
	 *  @param prts is the corresponding priorities for ops.
	 */
	private static ArrayList<String[]> sortBasedOnPriorities(
			ArrayList<String[]> ps, 
			char[] ops,
			int[] prts)
	{
		ArrayList<String[]> ps_ = ps;
		int swaps;
		do {
			swaps = 0;
			for (int i = 1; i < ps.size(); i++) {
				String[] op0 = ps_.get(i);
				String[] op1 = ps_.get(i - 1);
				int op0Index = Arrays.binarySearch(ops, op0[1].charAt(0));
				int op1Index = Arrays.binarySearch(ops, op1[1].charAt(0));
 				
	 			if (op0Index >= 0 && op1Index >= 0 && prts[op0Index] < prts[op1Index]) {
	 				ps_.remove(op1);
	 				ps_.remove(op0);
	 				ps_.add(i - 1, op0);
	 				ps_.add(i, op1);
	 				swaps += 1;	 				
	 			}
			}
		} while (swaps != 0);
		
		return ps_;
	}
	
	/** 
	 * This function takes a string and list of operators in order to produce two
	 * terms joined by the operation. The operation should been previously defined
	 * as a BinaryOperationInterface and encoded with the appropriate symbol.
	 * For example "a+b" -> {"a","+","b"}.
	 * @param operators is an ordered list of binary operations
	 */
	private static String[] isolateBinaryOperation(String str, char[] operators)
		throws Exception
	{
		int numOfOperations	= 0;
		
		String[] op = new String[3];
		for (int i = 0; i < str.length(); i++) {
			char chr = str.charAt(i);
			if (chr == '(') {
				String bracketedTerm = findBracketedString(str.substring(i));
				i += bracketedTerm.length() - 1;
			}
			else if (Arrays.binarySearch(operators, chr) > 0) {
				numOfOperations++;
				op[0] = str.substring(0, i);
				op[1] = "" + chr;
				op[2] = str.substring(i + 1);
			}
		}
		
		if (numOfOperations == 0) op = new String[]{"1", "*", str};
		
		for (String s : op)
			if (s.equals(null) || s.equals("")) throw new Exception(
					"Failed attempted to create an expression with a binary operation: " + str);	
		return op;
	}	
	
	private static boolean balancedBrackets(String str)
	{
		int count = 0;
		for (int i = 0; i < str.length(); i++) {			
			if (str.charAt(i) == ')') count++;
			else if (str.charAt(i) == '(') count--;
		}
		if (count == 0) return true;
		else return false;
	}
	
	private static String removeSpaces(String str) 
	{
		for (int i = 0; i < str.length(); i++) {
			char chr = str.charAt(i);
			
			if (chr == ' ') {
				String fst = str.substring(0, i);
				if (i + 1 < str.length())
					str = fst.concat(str.substring(i + 1, str.length()));
				else
					str = fst;
			}
		}
		
		return str;
	}
	
	/** 
	 * This takes a string, where the first character is assumed to be the
	 * opening bracket character.
	 */
	private static String findBracketedString(String str) 
			throws Exception{
		Exception bracketingException = new Exception("Incorrect Bracketing: " + str);
		
		if (str.length() == 0) return "";
		if (str.charAt(0) != '(') return str;
		
		int bracketCount = 1;
		for (int i = 1; i < str.length(); i++) {
			char chr = str.charAt(i);
			if (chr == '(') bracketCount++;
			else if (chr == ')') bracketCount--;
			if (bracketCount == 0) return str.substring(1, i);
		}
		
		throw bracketingException;
	}

}