package cope.interpreter;

import java.util.HashSet;
import java.util.Set;

public class CyclicGenerators
{
	public static void main(String[] args)
	{
		int order = Integer.parseInt(args[0]);
		Set<Integer> congruenceClasses = getCongruenceClasses(order);
		System.out.println(congruenceClasses);
		System.out.println(getGenerators(congruenceClasses, order));
	}

	public static Set<Integer> getGenerators(Set<Integer> congruenceClasses, int order)
	{
		Set<Integer> generators = new HashSet<Integer>();
		for (Integer congruenceClass : congruenceClasses) {
			Set<Integer> temp = new HashSet<Integer>();
			for (int i = 1; i < order; i++)
				temp.add((int) Math.pow(congruenceClass, i) % order);
			if (temp.containsAll(congruenceClasses))
				generators.add(congruenceClass);
		}
		return generators;
	}

	public static Set<Integer> getCongruenceClasses(int order)
	{
		Set<Integer> congruenceClasses = new HashSet<Integer>();
		for (int i = 1; i < order; i++)
			if (areCoprime(order, i))
				congruenceClasses.add(i);

		return congruenceClasses;
	}

	public static boolean areCoprime(int a, int b)
	{
		if (hcf(a, b) == 1) return true;
		else return false;
	}

	public static int hcf(int a, int b)
	{
		int max, min;

		max = a < b ? b : a;
		min = a < b ? a : b;

		int x = max, y = min, r;

		while (y != 0)
		{
			r = x % y;
			x = y;
			y = r;
		}

		return x;
	}
}