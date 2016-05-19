	/**
 * 
 */
package com.jnn.framework.Neurons;

import static com.jnn.framework.Constants.*;

/**
 * @author shabbirhussain
 *
 */
public class StepFunctionNeuron extends GenericNeuron {
	// Default serialization ID
	private static final long serialVersionUID = 1L;


	/**
	 * Maps given value to a output through a neural function
	 * @param x An input number
	 * @return A double number generated for given input
	 */
	@Override
	protected Double thresholdFun(Double x) {
		// TODO Auto-generated method stub
		return x;
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
		Double deltaError = (myLastOutput - expectedOutput[0]);
							// * 1 // Derivative of x is one;
		
		for(int i=0, l=myInputVector.length;i<l;i++){
			myInputWeights[i] -= deltaError * myInputVector[i] * LINEAR_LEARNING_RATE;
			outputVector[i]    = deltaError * myInputWeights[i];
		}

		debug("After = ");
		return outputVector;
		
		/*
		Double errThisPat = (expectedOutput[0] - super.myLastOutput) * LINEAR_LEARNING_RATE;
		
		for(int i=0, l=myInputVector.length;i<l;i++){
			double weightChange = errThisPat * myInputWeights[i];
			double newWeight    = myInputWeights[i] + weightChange; 
			myInputWeights[i] = Math.min(5, Math.max(-5, newWeight));
		}
		//*/
	}

}
