/** License information:
 *    Component: javaslicer-common
 *    Package:   de.unisb.cs.st.javaslicer.common.classRepresentation.instructions
 *    Class:     MethodInvocationInstruction
 *    Filename:  javaslicer-common/src/main/java/de/unisb/cs/st/javaslicer/common/classRepresentation/instructions/MethodInvocationInstruction.java
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.asm.Opcodes;

import de.hammacher.util.StringCacheInput;
import de.hammacher.util.StringCacheOutput;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionType;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadMethod;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadMethod.MethodReadInformation;

/**
 * Class representing a method invokation instruction (INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
 * INVOKEINTERFACE).
 */
public class MethodInvocationInstruction extends AbstractInstruction {

    private final String internalClassName;
    private final String methodName;
    private final String methodDesc;
    public final String fullNameOfInvokedMethod;
    public final boolean[] parameterIsLong;
    public final byte returnedSize; // 0, 1 or 2
    public boolean invokedCanReach;
    public boolean unknownModification; // for the soundness of slicing, when we cannot get the invoked method info, it is set to true. 
    // key-->type of modification, 0,1,2 for local variable, array element and object field respectively  value--> the location of the modification
    public /*static*/  final Map<Integer,Integer> modifiedParam;//=new HashMap<Integer,Integer>();    
    // key-->the location of the modification, value--> the object name of the modified field, only have meaning for field type
    public /*static*/final  Map<Integer,String> locToVarName;//=new HashMap<Integer,String>(); 
    // key-->the location of the modification, value-->the array name of the modified array element
    public /*static*/ final Map<Integer,String> locToArrName;//=new HashMap<Integer,String>();
    
    public MethodInvocationInstruction(final ReadMethod readMethod, final int opcode,
            final int lineNumber, final String internalClassName, final String methodName,
            final String methodDesc) { 
        super(readMethod, opcode, lineNumber);
        assert opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKESPECIAL
            || opcode == Opcodes.INVOKESTATIC || opcode == Opcodes.INVOKEINTERFACE;
        this.internalClassName = internalClassName;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        final org.objectweb.asm.Type[] parameterTypes = org.objectweb.asm.Type.getArgumentTypes(methodDesc);
        this.parameterIsLong = new boolean[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            this.parameterIsLong[i] = parameterTypes[i].getSize() == 2;
        }
        org.objectweb.asm.Type returnType = org.objectweb.asm.Type.getReturnType(methodDesc);
        this.returnedSize = returnType == org.objectweb.asm.Type.VOID_TYPE ? 0 : (byte) returnType.getSize();
        this.invokedCanReach=false;
        this.unknownModification=false;
        modifiedParam=new HashMap<Integer,Integer>(); 
        locToVarName=new HashMap<Integer,String>(); 
        locToArrName=new HashMap<Integer,String>();
        fullNameOfInvokedMethod=null;
    }

    public MethodInvocationInstruction(final ReadMethod readMethod, final int lineNumber, final int opcode,
            final String internalClassName, final String methodName, final String methodDesc, final int index) {
        super(readMethod, opcode, lineNumber, index);
        assert opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKESPECIAL
            || opcode == Opcodes.INVOKESTATIC || opcode == Opcodes.INVOKEINTERFACE;
        this.internalClassName = internalClassName;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        final org.objectweb.asm.Type[] parameterTypes = org.objectweb.asm.Type.getArgumentTypes(methodDesc);
        this.parameterIsLong = new boolean[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            this.parameterIsLong[i] = parameterTypes[i].getSize() == 2;
        }
        org.objectweb.asm.Type returnType = org.objectweb.asm.Type.getReturnType(methodDesc);
        this.returnedSize = returnType == org.objectweb.asm.Type.VOID_TYPE ? 0 : (byte) returnType.getSize();
        this.invokedCanReach=false;
        this.unknownModification=false;
        modifiedParam=new HashMap<Integer,Integer>(); 
        locToVarName=new HashMap<Integer,String>(); 
        locToArrName=new HashMap<Integer,String>();
        fullNameOfInvokedMethod=null;
    }
    
    public MethodInvocationInstruction(final ReadMethod readMethod, final int lineNumber, final int opcode,
            final String internalClassName, final String methodName, final String methodDesc, final int index, final String fullName) {
        super(readMethod, opcode, lineNumber, index);
        assert opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKESPECIAL
            || opcode == Opcodes.INVOKESTATIC || opcode == Opcodes.INVOKEINTERFACE;
        this.internalClassName = internalClassName;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        final org.objectweb.asm.Type[] parameterTypes = org.objectweb.asm.Type.getArgumentTypes(methodDesc);
        this.parameterIsLong = new boolean[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            this.parameterIsLong[i] = parameterTypes[i].getSize() == 2;
        }
        org.objectweb.asm.Type returnType = org.objectweb.asm.Type.getReturnType(methodDesc);
        this.returnedSize = returnType == org.objectweb.asm.Type.VOID_TYPE ? 0 : (byte) returnType.getSize();
        this.invokedCanReach=false;
        this.unknownModification=false;
        modifiedParam=new HashMap<Integer,Integer>(); 
        locToVarName=new HashMap<Integer,String>(); 
        locToArrName=new HashMap<Integer,String>();
        fullNameOfInvokedMethod=fullName;
    }
    
    public String getFullNameOfInvokedMethod(){
    	return this.fullNameOfInvokedMethod; 
    }
    
    public void addModifiedParam(int a, int b){
    	modifiedParam.put(a, b);
    }
    public void addLocToVarName(int a, String b){
    	locToVarName.put(a, b);
    }
    public void addLocToArrName(int a, String b){
    	locToArrName.put(a, b);
    }
    
    public Map<Integer,Integer> getModifiedParam(){
    	return this.modifiedParam;
    }
    public Map<Integer,String> getLocToVarName(){
    	return this.locToVarName;
    }
    public Map<Integer,String> getLocToArrName(){
    	return this.locToArrName;
    }

    public String getInvokedInternalClassName() {
        return this.internalClassName;
    }

    public String getInvokedMethodName() {
        return this.methodName;
    }

    public String getInvokedMethodDesc() {
        return this.methodDesc;
    }

    public int getParameterCount() {
        return this.parameterIsLong.length;
    }

    public boolean parameterIsLong(final int paramIndex) {
        return this.parameterIsLong[paramIndex];
    }

    public byte getReturnedSize() {
        return this.returnedSize;
    }

    @Override
	public InstructionType getType() {
        return InstructionType.METHODINVOCATION;
    }

    @Override
    public void writeOut(final DataOutputStream out, final StringCacheOutput stringCache) throws IOException {
        super.writeOut(out, stringCache);
        stringCache.writeString(this.internalClassName, out);
        stringCache.writeString(this.methodName, out);
        stringCache.writeString(this.methodDesc, out);
    }

    public static MethodInvocationInstruction readFrom(final DataInputStream in, final MethodReadInformation methodInfo,
            final StringCacheInput stringCache,
            final int opcode, final int index, final int lineNumber) throws IOException {
        final String internalClassName = stringCache.readString(in);
        final String methodName = stringCache.readString(in);
        final String methodDesc = stringCache.readString(in);
        return new MethodInvocationInstruction(methodInfo.getMethod(), lineNumber, opcode, internalClassName, methodName, methodDesc, index);
    }

    @Override
    public String toString() {
        String type;
        switch (getOpcode()) {
        case Opcodes.INVOKEVIRTUAL:
            type = "INVOKEVIRTUAL";
            break;
        case Opcodes.INVOKESPECIAL:
            type = "INVOKESPECIAL";
            break;
        case Opcodes.INVOKESTATIC:
            type = "INVOKESTATIC";
            break;
        case Opcodes.INVOKEINTERFACE:
            type = "INVOKEINTERFACE";
            break;
        default:
            assert false;
            type = "--ERROR--";
        }

        final StringBuilder sb = new StringBuilder(type.length() + this.internalClassName.length() + this.methodName.length() + this.methodDesc.length() + 2);
        sb.append(type).append(' ').append(this.internalClassName).append('.').append(this.methodName).append(this.methodDesc);
        return sb.toString();
    }

}
