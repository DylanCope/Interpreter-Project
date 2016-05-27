# Mathematical Interpreting

The aim of this project is to create a piece of software that parses a string as a mathematical function, then is able to 
perform various manipulations upon said function.

Functions are converted in tree form for manipulation and evaluation, consider the example function,

<p align="center">
  <img src="https://raw.githubusercontent.com/DylanCope/Interpreter-Project/master/images/example-func.png" />
</p>

The equivalent function tree is,

<p align="center">
  <img src="https://raw.githubusercontent.com/DylanCope/Interpreter-Project/master/images/example-tree.png" />
</p>

Evaluations are done by tail recursively evaluating the children of any given node by replacing values for given variables at
the leafs.
There are four distinct types of nodes, binary functions, unary functions, variables and constants.
