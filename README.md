# Mathematical Interpreting

The aim of this project is to create a piece of software that parses a string as a mathematical function, and is able to 
perform various manipulations upon said function.

Functions are converted in tree form for manipulation and evaluation, consider the example function,

<p align="center">
  <img src="https://raw.githubusercontent.com/DylanCope/Interpreter-Project/master/images/example-func.png" />
</p>

With all the default parameters, the `StringAnalysis` class converts the string `"(4*x^y -2*sinh(y))*(9*x*y + cos(ln(x))^2)"` into the function tree,

<p align="center">
  <img src="https://raw.githubusercontent.com/DylanCope/Interpreter-Project/master/images/example-tree.png" />
</p>

Evaluations are done by tail recursively evaluating the children of any given node by replacing values for given variables at
the leafs.
There are four distinct types of operations, binary operations, unary functions, variables and constants. These are implemented as functional nodes in the classes `BinaryFunction`, `UnaryFunction`, `FunctionalVariable` and `Constant`, all of which extend
`Function`.

## Example Usage

The following code parses the string `"sin(pi*x)^2"` to create a function tree, evaluates the function at `x = 0.25`,
differentiates and then simplifies.

```java
try {
	Function f = StringAnalysis.getFunction("sin(pi*x)^2");
	System.out.println(f);
	System.out.println(f.toString(0.25f));
	System.out.println(f.differentiate().simplify());
} catch (Exception e1) {
	e1.printStackTrace();
	System.exit(1);
}
```

The output is then,

```cmd
>> f(x) = sin(pi*x)^2.0
>> f(0.25) = 0.50
>> f(x) = (sin(pi*x)^2.0*2.0*pi*cos(pi*x))/sin(pi*x)
```

The full simplification algorithm isn't implemented, the primary problem being with the lack of cancellation of terms across fractions. As of now multiplication and additions of zero makes terms disappears, multiplication and division of one is removed and binary operations between constants are coalesced.

More information about the algorithms and patterns used for simplification and differentiation can be found in the pdf in the latex folder of this repository, https://github.com/DylanCope/Interpreter-Project/blob/master/latex/function_analysis.pdf.

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
* Build a more formal LL(k) grammar for parsing, and implement a more rigorous parsing algorithm. (Current parsing doesn't recognise some valid strings)
* Add a differential operator that can be used to create differential equations.
* Implement numerical approximation to integrals
