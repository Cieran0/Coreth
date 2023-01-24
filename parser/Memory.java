package parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Memory {
    
    private static final boolean GC_ENABLED = false;

    private static List<Variable> variables = new ArrayList<Variable>();

    public static int addVariable(Variable variable) {
        int index = 0;
        for (int i = 0; i < variables.size(); i++) {
            if(variables.get(i).isDead()) {
                index = i;
                variable.setIndex(index);
                variables.set(i, variable);
                return index;
            }
        }
        index = variables.size();
        variable.setIndex(index);
        variables.add(variable);
        return index;
    }

    public static Variable getVariable(int index) {
        return variables.get(index);
    }

    public static void killVariables(Collection<Integer> indexes) {
        if(GC_ENABLED) {
            for (Integer index : indexes) {
                variables.get(index).kill();
            }
        }
    }

}
