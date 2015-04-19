package com.creativemd.itemphysic;

import static org.objectweb.asm.Opcodes.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.DamageSource;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import scala.reflect.internal.Types.MethodType;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;

public class ItemTransformer implements IClassTransformer {

	public static boolean isLite = true;
	
	
	//NOTE: This doesn't work for some cases like EntityItem and EntityItemFrame
	public static final String[] names = new String[]{".", "doRender", "net/minecraft/entity/Entity", "renderWithColor",
		"net/minecraft/client/renderer/entity/RenderItem", "net/minecraft/entity/item/EntityItem", "onUpdate", "isBurning",
		"attackEntityFrom", "net/minecraft/util/DamageSource", "health", "onCollideWithPlayer",
		"net/minecraft/entity/player/EntityPlayer", "net/minecraft/util/AxisAlignedBB", "getCollisionBox", "canBeCollidedWith",
		"net/minecraft/client/entity/EntityClientPlayerMP", "dropOneItem", "setPositionAndRotation2"};
	public static final String[] namesOb = new String[]{"/", "a", "sa", "a",
		"bny", "xk", "h", "al", "a", "ro", "e", "b_", "yz", "azt", "h", "R", "bjk", "a", "a"};
	
	public static String patchT(String input, boolean obfuscated)
	{
		if(obfuscated)
			input = input.replace("health", "field_70291_e");
		return input;
	}
	
	public static String patch(String input)
	{
		for(int zahl = 0; zahl < names.length; zahl++)
			input = input.replace(names[zahl], namesOb[zahl]);
		return input;
	}
	
	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {
		if (arg0.equals("bny") | arg0.contains("net.minecraft.client.renderer.entity.RenderItem")) {
			System.out.println("[ItemPhysic] Patching " + arg0);
			arg2 = replaceMethodDoRender(arg0, arg2, ItemPatchingLoader.location, !arg0.contains("net.minecraft.client.renderer.entity.RenderItem"));
		}
		if(!isLite)
		{
			if(arg0.equals("xk") | arg0.equals("net.minecraft.entity.item.EntityItem"))
			{
				System.out.println("[ItemPhysic] Patching " + arg0);
				arg2 = replaceMethodOnUpdate(arg0, arg2, ItemPatchingLoader.location, !arg0.equals("net.minecraft.entity.item.EntityItem"));
				arg2 = addMethodIsBurning(arg0, arg2, ItemPatchingLoader.location, !arg0.equals("net.minecraft.entity.item.EntityItem"));
				arg2 = addMethodSetPosition(arg0, arg2, ItemPatchingLoader.location, !arg0.equals("net.minecraft.entity.item.EntityItem"));
				arg2 = replaceMethodAttack(arg0, arg2, ItemPatchingLoader.location, !arg0.equals("net.minecraft.entity.item.EntityItem"));
			}
			
			if(arg0.equals("bjk") | arg0.equals("net.minecraft.client.entity.EntityClientPlayerMP"))
			{
				System.out.println("[ItemPhysic] Patching " + arg0);
				arg2 = removeDrop(arg0, arg2, ItemPatchingLoader.location, !arg0.equals("net.minecraft.client.entity.EntityClientPlayerMP"));
			}
		}
		return arg2;
	}
	
	public byte[] removeDrop(String name, byte[] bytes, File location, boolean obfuscated)
	{
		String invokeName = "dropOneItem";
		String invokeDESC = "(Z)Lnet/minecraft/entity/item/EntityItem;";
		
		
		if(obfuscated == true)
		{
			invokeName = patch(invokeName);
			invokeDESC = patch(invokeDESC);
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if ((m.name.equals(invokeName) && m.desc.equals(invokeDESC)))
			{
				m.instructions.clear();
				m.instructions.add(new InsnNode(ACONST_NULL));
				m.instructions.add(new InsnNode(ARETURN));
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] addMethodSetPosition(String name, byte[] bytes, File location, boolean obfuscated)
	{
		String targetMethodName = "setPositionAndRotation2";
		String targetDESC = "(DDDFFI)V";
		String entity = "net/minecraft/entity/Entity";
		String item = "(Lnet/minecraft/entity/item/EntityItem;D)V";
		String item2 = "(Lnet/minecraft/entity/item/EntityItem;)V";
		
		if(obfuscated == true)
		{
			targetMethodName = patch(targetMethodName);
			targetDESC = patch(targetDESC);
			entity = patch(entity);
			item = patch(item);
			item2 = patch(item2);
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		MethodNode m = new MethodNode(ACC_PUBLIC, targetMethodName, targetDESC, null, null);
		LabelNode label = new LabelNode();
		m.instructions.add(label);
		m.instructions.add(new VarInsnNode(ALOAD, 0));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic",  "updatePositionBefore", item2));
		
		m.instructions.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
		m.instructions.add(new VarInsnNode(ALOAD, 0));
		m.instructions.add(new VarInsnNode(DLOAD, 1));
		m.instructions.add(new VarInsnNode(DLOAD, 3));
		m.instructions.add(new VarInsnNode(DLOAD, 5));
		m.instructions.add(new VarInsnNode(FLOAD, 7));
		m.instructions.add(new VarInsnNode(FLOAD, 8));
		m.instructions.add(new VarInsnNode(ILOAD, 9));
		m.instructions.add(new MethodInsnNode(INVOKESPECIAL, entity, targetMethodName, targetDESC));
		
		m.instructions.add(new VarInsnNode(ALOAD, 0));
		m.instructions.add(new VarInsnNode(DLOAD, 3));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic",  "updatePosition", item));		
		
		m.instructions.add(new InsnNode(RETURN));
		LabelNode label2 = new LabelNode();
		m.instructions.add(label2);
		m.localVariables.add(new LocalVariableNode("this", "L" + name.replace(".", "/") + ";", null, label, label2, 0));
		classNode.methods.add(m);
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] addMethodIsBurning(String name, byte[] bytes, File location, boolean obfuscated)
	{
		String targetMethodName = "isBurning";
		String targetDESC = "(Lnet/minecraft/entity/item/EntityItem;)Z";
		
		if(obfuscated == true)
		{
			targetMethodName = patch(targetMethodName);
			targetDESC = patch(targetDESC);
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		MethodNode m = new MethodNode(ACC_PUBLIC, targetMethodName, "()Z", null, null);
		LabelNode label = new LabelNode();
		m.instructions.add(label);
		m.instructions.add(new VarInsnNode(ALOAD, 0));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic",  "isItemBurning", targetDESC));
		m.instructions.add(new InsnNode(IRETURN));
		LabelNode label2 = new LabelNode();
		m.instructions.add(label2);
		m.localVariables.add(new LocalVariableNode("this", "L" + name.replace(".", "/") + ";", null, label, label2, 0));
		classNode.methods.add(m);
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	public byte[] replaceMethodDoRender(String name, byte[] bytes, File location, boolean obfuscated)
	{
		String targetMethodName = "doRender";
		String targetDESC = "(Lnet/minecraft/entity/Entity;DDDFF)V";
		String newDESC = "(Lnet/minecraft/entity/Entity;DDDFF)V";
		String targetVar = "renderWithColor";
		
		
		if(obfuscated == true)
		{
			targetMethodName = patch(targetMethodName);
			targetDESC = patch(targetDESC);
			newDESC = patch(newDESC);
			targetVar = patch(targetVar);
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if ((m.name.equals(targetMethodName) && m.desc.equals(targetDESC)))
			{
				AbstractInsnNode currentNode = null;
		
				@SuppressWarnings("unchecked")
				Iterator<AbstractInsnNode> iter = m.instructions.iterator();
				
				while (iter.hasNext())
				{
					currentNode = iter.next();
					if (currentNode instanceof MethodInsnNode)
					{
						/*m.instructions.insertBefore(currentNode, new VarInsnNode(ALOAD, 0));
						m.instructions.insertBefore(currentNode, new FieldInsnNode(Opcodes.GETFIELD, name.replace(".", "/"), targetVar, "Z"));*/
						((MethodInsnNode) currentNode).desc = newDESC;
						((MethodInsnNode) currentNode).owner = "com/creativemd/itemphysic/physics/ClientPhysic";
						((MethodInsnNode) currentNode).name = "doRender";
						((MethodInsnNode) currentNode).setOpcode(INVOKESTATIC);
					}
				}
				m.visitEnd();
				break;
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] replaceMethodAttack(String name, byte[] bytes, File location, boolean obfuscated)
	{
		String targetMethodName = "attackEntityFrom";
		String targetDESC = "(Lnet/minecraft/util/DamageSource;F)Z";
		String targetNewDESC = "(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/DamageSource;F)Z";
		String fieldName = "health";
		String targetMethodName2 = "onCollideWithPlayer";
		String targetDESC2 = "(Lnet/minecraft/entity/player/EntityPlayer;)V";
		String newDESC2 = "(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;)V";
		String newDESC3 = "(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;)Z";
		String newMethod = "interactFirst";
		String newMethodDESC = "(Lnet/minecraft/entity/player/EntityPlayer;)Z";
		String newMethodVar = "Lnet/minecraft/entity/player/EntityPlayer;";
		String newMethod2 = "canBeCollidedWith";
		//String newMethodDESC2 = "(Lnet/minecraft/entity/item/EntityItem;)Lnet/minecraft/util/AxisAlignedBB;";
		//String newMethodDESC3 = "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/AxisAlignedBB;";
		
		if(obfuscated == true)
		{
			targetMethodName = patch(targetMethodName);
			targetDESC = patch(targetDESC);
			fieldName = patch(fieldName);
			targetNewDESC = patch(targetNewDESC);
			targetMethodName2 = patch(targetMethodName2);
			targetDESC2 = patch(targetDESC2);
			newDESC3 = patch(newDESC3);
			newMethod = patch(newMethod);
			newMethodDESC = patch(newMethodDESC);
			newMethodVar = patch(newMethodVar);
			newMethod2 = patch(newMethod2);
			//newMethodDESC2 = patch(newMethodDESC2);
			//newMethodDESC3 = patch(newMethodDESC3);
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		Iterator<FieldNode> fields = classNode.fields.iterator();
		while(fields.hasNext())
		{
			FieldNode f = fields.next();		
			if(f.name.equals(fieldName))
			{
				f.access = ACC_PUBLIC;
			}
		}
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if ((m.name.equals(targetMethodName) && m.desc.equals(targetDESC)))
			{
				m.instructions.clear();
				
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				m.instructions.add(new VarInsnNode(ALOAD, 1));
				m.instructions.add(new VarInsnNode(FLOAD, 2));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "attackEntityFrom", targetNewDESC));
				m.instructions.add(new InsnNode(IRETURN));
			}
			if (m.name.equals(targetMethodName2) && m.desc.equals(targetDESC2))
			{
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				m.instructions.add(new VarInsnNode(ALOAD, 1));
				m.instructions.add(new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "onCollideWithPlayer", newDESC2));
				m.instructions.add(new InsnNode(RETURN));
			}
		}
		MethodNode m = new MethodNode(ACC_PUBLIC, newMethod, newMethodDESC, null, null);
		LabelNode label = new LabelNode();
		m.instructions.add(label);
		m.instructions.add(new VarInsnNode(ALOAD, 0));
		m.instructions.add(new VarInsnNode(ALOAD, 1));
		m.instructions.add(new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "interactFirst", newDESC3));
		LabelNode label2 = new LabelNode();
		m.instructions.add(label2);
		m.instructions.add(new InsnNode(ICONST_1));
		m.instructions.add(new InsnNode(IRETURN));
		
		m.localVariables.add(new LocalVariableNode("this", "L" + name.replace(".", "/") + ";", null, label, label2, 0));
		m.localVariables.add(new LocalVariableNode("par1EntityPlayer", newMethodVar, null, label, label2, 0));
		classNode.methods.add(m);
		
		/*MethodNode method2 = new MethodNode(ACC_PUBLIC, newMethod2, "()Z", null, null);
		LabelNode label3 = new LabelNode();
		method2.instructions.add(label3);
		method2.instructions.add(new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "canBeCollidedWith", "()Z"));
		//method2.instructions.add(new InsnNode(ICONST_1));
		method2.instructions.add(new InsnNode(IRETURN));
		LabelNode label4 = new LabelNode();
		method2.instructions.add(label4);
		method2.localVariables.add(new LocalVariableNode("this", "L" + name.replace(".", "/") + ";", null, label3, label4, 0));
		classNode.methods.add(method2);*/
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] replaceMethodOnUpdate(String name, byte[] bytes, File location, boolean obfuscated)
	{
		String targetMethodName = "onUpdate";
		String targetDESC = "(Lnet/minecraft/entity/item/EntityItem;)V";
		
		if(obfuscated == true)
		{
			targetMethodName = patch(targetMethodName);
			targetDESC = patch(targetDESC);
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if ((m.name.equals(targetMethodName) && m.desc.equals("()V")))
			{
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic",  "update", targetDESC));
				m.instructions.add(new InsnNode(RETURN));
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
