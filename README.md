# Mathematical Interpreting

The aim of this project is to create a piece of software that parses a string as a mathematical function, and is able to 
perform various manipulations upon said function.

Functions are converted in tree form for manipulation and evaluation, consider the example function,

<p align="center">
  <img src="https://raw.githubusercontent.com/DylanCope/Interpreter-Project/master/images/example-func.png" />
</p>

With all the default parameters, a default instance of `Interpreter` class would convert the string `"(4*x^y -2*sinh(y))*(9*x*y + cos(ln(x))^2)"` into the function tree,

<p align="center">
  <img src="https://raw.githubusercontent.com/DylanCope/Interpreter-Project/master/images/example-tree.png" />
</p>

Evaluations are done by tail recursively evaluating the children of any given node by replacing values for given variables at
the leafs.
There are four distinct types of operations, binary operations, unary functions, variables and constants. These are implemented as functional nodes in the classes `BinaryFunction`, `UnaryFunction`, `FunctionalVariable` and `Constant`, all of which extend
`Function`.

## Example Usage

The following code is example usage of the intepreter's default settings, 

```java
try {
	Intepreter interpreter = new Interpreter();
	Function f = interpreter.parse("sin(pi*x)^2");
	f.setName("f");
	System.out.println("Let " + f);
	System.out.println(f.toString(0.25f));
	System.out.println(f.differentiate().simplify());
	System.out.println();

	Function g = interpreter.parse("ln(x*y) + x^4 + y", "x", "y");
	g.setName("g");
	System.out.println("\nLet " + g + "\n");
	Function diff = g.differentiate();
	System.out.println("Differentiating g with respect to x, without simplification:");
	System.out.println(diff);
	System.out.println();
	System.out.println("Simplifying g':");
	System.out.println(diff.simplify());
} catch (Exception e1) {
	e1.printStackTrace();
	System.exit(1);
}
```

The output is then,

```cmd
>> Let f(x) = sin(pi*x)^2.0
>> f(0.25) = 0.50
>> f'(x) = (2.0*pi*cos(pi*x)*sin(pi*x)^2.0)/sin(pi*x)
>> 
>> 
>> Let g(x, y) = ln(x*y) + x^4.0 + y
>> 
>> Differentiating g with respect to x, without simplification:
>> g'(x, y) = (x*0.0 + y*1.0)/(x*y) + (0.0*ln(x) + 4.0*1.0/x)*x^4.0 + 0.0
>> 
>> Simplifying g':
>> g'(x, y) = y/(x*y) + (4.0*x^4.0)/x
```

The full simplification algorithm isn't implemented, the primary problem being the lack of cancellation of terms across fractions. 
More information about the algorithms and patterns used for simplification and differentiation can be found in the pdf in the latex folder of this repository, https://github.com/DylanCope/Interpreter-Project/blob/master/latex/function_analysis.pdf.

The constructors for the `Interpreter` class set up the collections of binary operations, unary operations and standard
variables (such as `pi` and `e`) that are used to parse input. Calling the constructor with no parameters will instantiate the binary and unary operations in accordance with the lists defined by `BinaryFunction.standardInstructions` and `UnaryFunction.standardInstructions`, and the default standard variables are `Function.PI` and `Function.E`.

When parsing a string the interpreter needs a collection of variables that it is trying to identify, if none are provided
it will assume the string is a univariate function of "x".

## Features

* Binary Operations: Addition `+`, subtraction `-`, multiplication `*`, division `/`, modulo division `%` and exponentiation `^`.
* These adhere to the BIDMAS order of operations.
* Unary Operations: `sin`, `cos`, `tan`, `sinh`, `cosh`, `tanh`, `abs`, `floor`, `ceil`, `sqrt`, `ln`.
* `pi` and `e` are by variables by default.

## TODO:

* Memoization of function trees.
* Reduction of fractions through cancellation.
* Convert to LaTeX (mathmode or qtree representations).
* Generalise functions for arbitrary dimensionality of the domain and range.
* All functions currently operate across the real numbers, this should be generalised to arbitrary domains and ranges. 
* Add a differential operator that can be used to create differential equations.
* Implement numerical approximation to integrals
