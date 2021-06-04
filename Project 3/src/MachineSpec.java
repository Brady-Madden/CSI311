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
	//initialize getter and setter for tenantId field
	private int tenantId;
	public int getTenantId() { return tenantId; }
	public void setTenantId(int tenantId) { this.tenantId = tenantId; } 
	
	//this validates that the machine spec is in a valid start state
		public boolean validateStartState(String startState) {
			String start = startState;
			StateTransitions key = machineSpec.get(0);
			if (key.getTransitions().contains(start)) {
				return true;
			}
			return false;
		}
	//this checks the validity of 
		public boolean verifyMachine(String last_state, String next_state) {
			//while these states aren't empty
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
		// Does the adjacancy list for state1 include state2?  i.e. can you get to state2 from state1?
		public boolean stateTransitionsContain(String state1, String state2) {
			List<String> transitions = null; 
			for (StateTransitions sts : getMachineSpec()) {
				if (sts.getState().equals(state1)) {
					transitions = sts.getTransitions();
					break;
				}
			}
			if (transitions == null) {
				return false;
			}
			return transitions.contains(state2);
		}
 
		// Given the state machine "spec", is there a transition from state1 to state2?  If this 
		// is a new order, then the value of state1 must be a start state.
		public static boolean isValidTransition(MachineSpec spec, String state1, String state2, boolean isNew) {
			if (isNew) {
				// A new order's state is valid if its a start state
				return spec.stateTransitionsContain("start", state1);
			}
			// Its not a new order, so the state is valid if state1 --> state2 is a valid transition
			return spec.stateTransitionsContain(state1, state2); 
		}
		public static boolean isTerminalState(MachineSpec spec, String state) {
			for (StateTransitions sts : spec.getMachineSpec()) {
				if (sts.getState().equals(state)) {
					if (sts.getTransitions().size() > 1) {
						// The state is in the table, and it has a non-zero adjacency list.  
						// Its not a terminal state.  
						return false; 
					}
					if (sts.getTransitions().get(0).equals(state)) {
						// Transition to itself is allowed.
						return true; 
					}
				}
			}
			// Its not in the list of state transitions.  Its a terminal state.
			return true; 
		}
		
	
} //end machineSpec class 
