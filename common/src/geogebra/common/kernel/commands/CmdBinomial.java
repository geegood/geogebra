package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

/**
 * Binomial[ <Number>, <Number> ]
 * Michael Borcherds 2008-04-12
 */
public class CmdBinomial extends CmdTwoNumFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBinomial(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernelA.Binomial(a, b, c);
	}
}
