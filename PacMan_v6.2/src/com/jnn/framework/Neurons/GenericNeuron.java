/**
 * 
 */
package com.jnn.framework.Neurons;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.jnn.framework.Constants.*;

/**
 * @author shabbirhussain
 * Geenric Neuron class
 */
public abstract class GenericNeuron implements Neuron{
	// Default serialization ID
	private static final long serialVersionUID = 1L;
	protected Double myInputWeights[];
	protected Double myLastOutput, myInputVector[];
	
	/**
	 * Default Constructor
	 */
	public GenericNeuron(){
		myInputWeights  = new Double[0];
		myInputVector   = new Double[0];
	}
	
	/**
	 * @return The last output of neuron
	 */
 	public Double getLastOutput(){
 		return myLastOutput;
 	}
 	
 	/**
	 * @return The last inputs of neuron
	 */
 	public Double[] getLastInputs(){
 		return myInputVector;
 	}
 	
 	/////////////////////// Inputs //////////////////////////////////

	/**
	 * Stores input vector and add bias to the 
	 * @param inputVector
	 */
	private void initializeInputVector(Double[] inputVector){
		myInputVector = inputVector;
		
		// Increment length of weights vector if bigger inputs arrive
		if(myInputVector.length > myInputWeights.length){
			Double newWeights[] = new Double[myInputVector.length];
			
			// Copy old weights to new array
			System.arraycopy(myInputWeights, 0, newWeights, 0, myInputWeights.length);
					
			// Initialize new values 
			for(int i=myInputWeights.length; i<myInputVector.length; i++)
				newWeights[i] = (Math.random() - 0.5)/5;
			
			myInputWeights = newWeights;
		}
	}

	/////////////////////// Outputs //////////////////////////////////
		
	/**
	 * Maps given value to a output through a neural function
	 * @param x An input number
	 * @return A double number generated for given input
	 */
	protected abstract Double thresholdFun(Double x);

	/**
	 * Curbs the extreme wild outputs of neuron that can paralyze network
	 * @param x Current output of neuron
	 * @return Output adjusted to cope for extremities
	 */
	protected Double curbWildOutputs(Double x){
		return (x.isNaN() || x.isInfinite())? 0 : x;
	}
	
	/** 
	 * Checks for required precision in output is reached or not?
	 * @param x
	 * @return Returns true of required precision is reached by network
	 */
	protected Boolean requiredPrecisionReached(Double x){
		BigDecimal bd = new BigDecimal(x).setScale(ERROR_TOLLERANCE, RoundingMode.HALF_EVEN);
		return (bd.doubleValue() == 0.0);
	}

	/**
	 * Calculates weighted sum of input vector
	 * @return  
	 */
	private Double calculateInputWeightProduct(){
		myLastOutput=0.0;
		for(int i=0; i < myInputVector.length; i++)
			myLastOutput += (myInputVector[i] * myInputWeights[i]);
		return myLastOutput;
	}

	/**
	 * Function used to trigger a neural response. 
	 * @param inputVector Input array of values
	 * @return An neural output from neuron
	 */
	public Double fire(Double inputVector[]){
		initializeInputVector(inputVector);
		calculateInputWeightProduct();


		System.out.print("last o/p="+myLastOutput+"\t===>");
		myLastOutput = thresholdFun(myLastOutput);
		System.out.println(myLastOutput);
		return myLastOutput;
	}
	
	protected void debug(String msg){
		if(!DEBUG) return;
		System.out.print(msg);
		for(int i=0, l=myInputWeights.length;i<l;i++){
			System.out.print(myInputWeights[i] + " ");
		}System.out.println("");
	}
}
