package com.jnn.framework.Neurons;

import static com.jnn.framework.Constants.*;

public class SigmoidFunctionNeuron extends GenericNeuron{

	// Serialization version ID
	private static final long serialVersionUID = 1L;

	
	/**
	 * Maps given value to a output through a neural function
	 * @param x An input number
	 * @return A double number generated for given input
	 */
	@Override
	protected Double thresholdFun(Double x){
		//Double output = 1 /(1 - Math.exp(-x));
		Double output = Math.tanh(x);
		return output;
	}

	
	/**
	 * Adjusts weights of current neuron to cope for error.
	 * @param expectedOutput Expected outputs in the last run(Neuron has just one output)
	 * @return Gradient error to be applied to the previous layer
	 */
	public Double[] train(Double expectedOutput[]){
		Double outputVector[] = new Double[myInputVector.length];
		debug("Before= ");
		// deltaError = (out - target) * out (1 - out) * input[i]
		Double deltaError = (myLastOutput - expectedOutput[0])
							* sech2(myLastOutput);
				// * myLastOutput * (1 - myLastOutput); // Derivative of sigmoid function
		
		for(int i=0, l=myInputVector.length;i<l;i++){
			myInputWeights[i] -= deltaError * myInputVector[i] * EXP_LEARNING_RATE;
			outputVector[i]    = deltaError * myInputWeights[i];
		}
		debug("After = ");
		return outputVector;
	}
	
	/**
	 * @param x A number
	 * @return Square of hyperbolic tan inverse of given number
	 */
	private static Double sech2(Double x){
		Double temp = Math.exp(x);
		temp = 2 / (temp + 1 / temp);
		temp = Math.pow(temp, 2);
	    return temp;
	}
}
