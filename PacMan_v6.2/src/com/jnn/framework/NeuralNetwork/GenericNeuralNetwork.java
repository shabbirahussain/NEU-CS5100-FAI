/**
 * 
 */
package com.jnn.framework.NeuralNetwork;
import static com.jnn.framework.Constants.*;
import java.util.ArrayList;
import java.util.List;

import com.jnn.framework.Neurons.*;

/**
 * @author shabbirhussain
 * Holds the network and methods required to manage one
 */
public class GenericNeuralNetwork extends GenericNeuron{
	// Serialization version id
	private static final long serialVersionUID = 1L;
	
	private List<List<Neuron>> network; // Stores the neural network

	
	/**
	 * Default constructor
	 * @param numHiddenLayers Number of hidden layers to build. Minimum is one 
	 * @param numHiddenNeuron Total number of hidden neurons to build
	 * @param numOutputNeurons Number of neurons in the output layer
	 */
 	public GenericNeuralNetwork(Integer numHiddenLayers, Integer numHiddenNeuron, Integer numOutputNeurons){
 		network = new ArrayList<List<Neuron>>();
 		List<Neuron> layer; 
		
		if(numHiddenLayers>0){
			Integer neuronsPerLayer = (int) Math.ceil(numHiddenNeuron / numHiddenLayers);
			// Create Hidden layers
			for(int i=0; i<numHiddenLayers; i++){
				layer =  new ArrayList<Neuron>();
				for(int j=0; j<neuronsPerLayer; j++)
					layer.add(new SigmoidFunctionNeuron());
				
				// Add completed layer to network
				network.add(layer);
			}
		}
		
		// Create Output Layer
		layer  =  new ArrayList<Neuron>();
		for(int i=0; i<numOutputNeurons; i++)
			layer.add(new StepFunctionNeuron());
			//layer.add(new SigmoidFunctionNeuron());
		network.add(layer);
	}
	
	/**
	 * Trains the network with one pattern
	 * @param expectedOutput Expected Outputs of the neural network
	 * @return Gradient error to be applied to the previous layer
	 */
	public Double[] train(Double expectedOutput[]){
		
		if(DEBUG) System.out.println("==========================");
		// Adjust weights with percentage error for each neuron in each layer
		for(int l=(network.size()-1); l>=0; l--){
			List<Neuron>   layer     = network.get(l);
			List<Double[]> gradients = new ArrayList<Double[]>();
			
			if(DEBUG){
				System.out.print("\nInput vector=");
				for(int i=0;i<myInputVector.length; i++) System.out.print(myInputVector[i] + " ");
				
				System.out.print("\nExpected op=");
				for(int i=0;i<layer.size(); i++) 
					System.out.println("\t"+expectedOutput[i] + " - "+layer.get(i).getLastOutput());
				
			}
			
			for(int n=0, nl=layer.size(); n<nl; n++){
				// Get neuron from layer
				Neuron neuron = layer.get(n);
				
				// Get expected output 
				Double neuronExpectedOutput[] = new Double[1];
				neuronExpectedOutput[0] = expectedOutput[n];
				
				// Train inner layer
				gradients.add(neuron.train(neuronExpectedOutput));
			}
			
			// Calculate total for next layer backpropagation
			expectedOutput = new Double[gradients.get(0).length];
			for(int n=0; n<expectedOutput.length; n++)
				expectedOutput[n] = 0.0;
			
			for(Double[] gradient : gradients){
				for(int n=0; n<gradient.length; n++){
					expectedOutput[n] += gradient[n];
				}
			}
		}
		return expectedOutput;
	}

	/**
	 * Given an input array of fields 
	 * @param inputVector Input array of values
	 * @return An output from network
	 */
	@Override
	public Double fire(Double inputVector[]){
		Double neuralOutput[] = new Double[inputVector.length + 1];
		System.arraycopy(inputVector, 0, neuralOutput, 1, inputVector.length);
		neuralOutput[0] = 1.0;						// Add initial bias to input vector
		
		myInputVector = inputVector = neuralOutput;
		
		for(int l=0;l<network.size();l++){
			List<Neuron> layer  =  network.get(l);
			neuralOutput = new Double[layer.size() + 1];
			neuralOutput[0] = 1.0; 					// Add bias input
			
			for(int n=0, ls=layer.size(); n<ls; n++){
				neuralOutput[n + 1] = layer.get(n).fire(inputVector);
			}
			inputVector = neuralOutput;	
		}
		myLastOutput = inputVector[1];
		
		return myLastOutput;
	}
	

	private void printInputs(Double expectedOutput[]){
		System.out.print("Cycle o/p= " + myLastOutput + " [ ");
		for(Double i : expectedOutput) System.out.print(i +" ");
		System.out.println("] n/o= " + " Error= " );
	}

	/**
	 * No use in a network yet
	 * @param x Value after threshold function
	 */
	@Override
	protected Double thresholdFun(Double x) {
		// TODO Auto-generated method stub
		return 0.0;
	}
}



