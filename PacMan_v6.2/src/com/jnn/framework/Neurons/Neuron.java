/**
 * 
 */
package com.jnn.framework.Neurons;

import java.io.Serializable;

/**
 * @author shabbirhussain
 * An generic interface for a neuron
 */
public interface Neuron extends Serializable{
	
	/**
	 * Function used to trigger a neural response. 
	 * @param inputVector Input array of values
	 * @return An neural output from neuron
	 */
	Double fire(Double inputVector[]);
	
	/**
	 * Adjusts weights of current neuron to cope for error.
	 * @param expectedOutput Expected outputs in the last run
	 * @return Gradient error to be applied to the previous layer
	 */
	Double[] train(Double expectedOutput[]);
	
	/**
	 * @return The last output of neuron
	 */
	Double getLastOutput();
	
	/**
	 * @return The last inputs of neuron
	 */
	Double[] getLastInputs();
}
