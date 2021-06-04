package csi311;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class MachineSpec implements Serializable {

	// Since defined as an inner class, must be declared static or Jackson can't deal.
	public static class StateTransitions implements Serializable {
		private String state; 
		private List<String> transitions;
		public StateTransitions() { }
		public String getState() { return state; }
		public void setState(String state) { this.state = state.toLowerCase(); } 
		public List<String> getTransitions() { return transitions; } 
		public void setTransitions(List<String> transitions) { 
			this.transitions = transitions;
			if (this.transitions != null) {
				for (int i = 0; i < transitions.size(); i++) {
					transitions.set(i, transitions.get(i).toLowerCase()); 
				}
			}
		} 
	}

	private List<StateTransitions> machineSpec;
	public MachineSpec() { }
	public List<StateTransitions> getMachineSpec() { return machineSpec; } 
	public void setMachineSpec(List<StateTransitions> machineSpec) { this.machineSpec = machineSpec; } 

	//this checks the validity of 
	public boolean verifyMachine(String last_state, String next_state) {
		//while these states arent empty
		while (last_state != null && next_state != null) {
			for(int i = 0; i < machineSpec.size(); i++) {
				StateTransitions st = machineSpec.get(i);
				String states = st.getState();
				List <String> trans = st.getTransitions();
				if(states.equals(last_state)) {
					for(int x = 0; x < trans.size(); x++) {
						if(trans.get(x).contains(next_state)) {
							return true;  
						}
					}
				}
		
			} //End for loop for .size of machine
			//else
			return false;

		}
		return false;
	} //End verifyMachine
	//this validates that the machine spec is in a valid start state
	public boolean validateStartState(String startState) {
		String start = startState;
		StateTransitions key = machineSpec.get(0);
		if (key.getTransitions().contains(start)) {
			return true;
		}
		return false;
	}

} //end machineSpec class
