/** License information:
 *    Component: javaslicer-common
 *    Package:   de.unisb.cs.st.javaslicer.common.classRepresentation.instructions
 *    Class:     JumpInstruction
 *    Filename:  javaslicer-common/src/main/java/de/unisb/cs/st/javaslicer/common/classRepresentation/instructions/JumpInstruction.java
 *
 * This file is part of the JavaSlicer tool, developed by Clemens Hammacher at Saarland University.
 * See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * JavaSlicer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaSlicer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaSlicer. If not, see <http://www.gnu.org/licenses/>.
 */
package de.unisb.cs.st.javaslicer.common.classRepresentation.instructions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Opcodes;

import de.hammacher.util.StringCacheInput;
import de.hammacher.util.StringCacheOutput;
import de.hammacher.util.streams.OptimizedDataInputStream;
import de.hammacher.util.streams.OptimizedDataOutputStream;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionType;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadMethod;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadMethod.MethodReadInformation;


/**
 * Class representing a conditional or unconditional jump instructions (one of
 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE,
 * IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL and IFNONNULL).
 *
 * @author Clemens Hammacher
 */
public class JumpInstruction2 extends AbstractInstruction {

    private int target;

    public JumpInstruction2(final ReadMethod readMethod, final int opcode, final int lineNumber,
            final int label) {
        super(readMethod, opcode, lineNumber);
        this.target= label;
    }

    public  JumpInstruction2(final ReadMethod readMethod, final int lineNumber,
            final int opcode, final int label, final int index) {
        super(readMethod, opcode, lineNumber, index);
        this.target = label;
    }

    
    public Instruction getLabel() {
        ReadMethod readmethod=this.getMethod();
        List<AbstractInstruction> instrs=readmethod.getInstructions();
    	Iterator<AbstractInstruction> iter=instrs.iterator();
    	Instruction result=null;
    	while(iter.hasNext()){
    		AbstractInstruction abs=iter.next();
    		if(abs.getIndex()==this.target){
    			result=abs;
    		}
    	}
    	assert(result!=null);
    	return result;
    }
    public int  getTarget() {
        return this.target;
    }

    public void setTarget(final int tar) {
        this.target = tar;
    }

    @Override
	public InstructionType getType() {
        return InstructionType.JUMP;
    }

    @Override
    public String toString() {
        String condition;
        switch (getOpcode()) {
        case Opcodes.IFEQ:
            condition = "IFEQ";
            break;
        case Opcodes.IFNE:
            condition = "IFNE";
            break;
        case Opcodes.IFLT:
            condition = "IFLT";
            break;
        case Opcodes.IFGE:
            condition = "IFGE";
            break;
        case Opcodes.IFGT:
            condition = "IFGT";
            break;
        case Opcodes.IFLE:
            condition = "IFLE";
            break;
        case Opcodes.IF_ICMPEQ:
            condition = "IF_ICMPEQ";
            break;
        case Opcodes.IF_ICMPNE:
            condition = "IF_ICMPNE";
            break;
        case Opcodes.IF_ICMPLT:
            condition = "IF_ICMPLT";
            break;
        case Opcodes.IF_ICMPGE:
            condition = "IF_ICMPGE";
            break;
        case Opcodes.IF_ICMPGT:
            condition = "IF_ICMPGT";
            break;
        case Opcodes.IF_ICMPLE:
            condition = "IF_ICMPLE";
            break;
        case Opcodes.IF_ACMPEQ:
            condition = "IF_ACMPEQ";
            break;
        case Opcodes.IF_ACMPNE:
            condition = "IF_ACMPNE";
            break;
        case Opcodes.GOTO:
            condition = "GOTO";
            break;
        case Opcodes.JSR:
            condition = "JSR";
            break;
        case Opcodes.IFNULL:
            condition = "IFNULL";
            break;
        case Opcodes.IFNONNULL:
            condition = "IFNONNULL";
            break;
        default:
            assert false;
            condition = "--ERROR--";
            break;
        }

        return condition + " " + this.target;
    }
}
