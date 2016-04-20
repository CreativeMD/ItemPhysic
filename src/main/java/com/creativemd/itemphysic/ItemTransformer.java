package com.creativemd.itemphysic;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import java.io.File;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.ibm.icu.text.ChineseDateFormat.Field;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ItemTransformer implements IClassTransformer {

	public static boolean isLite = false;
	
	public static boolean obfuscated = false;
	
	public static final String[] names = new String[]{".", "net/minecraft/client/renderer/entity/RenderEntityItem", "doRender", "net/minecraft/entity/item/EntityItem", "net/minecraft/entity/Entity",
			"net/minecraft/client/renderer/entity/Render", "setPositionAndRotation2", "onUpdate", "isBurning", "attackEntityFrom", "net/minecraft/util/DamageSource", "health",
			"onCollideWithPlayer", "net/minecraft/entity/player/EntityPlayer", "processInitialInteract", "net/minecraft/item/ItemStack", "net/minecraft/util/EnumHand", "canBeCollidedWith",
			"net/minecraft/client/entity/EntityPlayerSP", "dropOneItem"};
	public static final String[] namesOb = new String[]{"/", "brx", "a", "yd", "rr", "brn", "a", "m", "aH", "a", "rc", "f", "d", "zj", "a", "adq", "qm", "ap", "bmt", "a"};
	
	public static String patch(String input)
	{
		if(obfuscated)
		{
			for(int zahl = 0; zahl < names.length; zahl++)
				input = input.replace(names[zahl], namesOb[zahl]);
		}
		return input;
	}
	
	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {
		if (arg0.equals("brx") | arg0.contains("net.minecraft.client.renderer.entity.RenderEntityItem")) {
			obfuscated = !arg0.contains("net.minecraft.client.renderer.entity.RenderEntityItem");
			ItemDummyContainer.logger.info("[ItemPhysic] Patching " + arg0);
			arg2 = replaceMethodDoRender(arg0, arg2);
		}
		if(arg0.equals("yd") | arg0.equals("net.minecraft.entity.item.EntityItem"))
		{
			obfuscated = !arg0.contains("net.minecraft.entity.item.EntityItem");
			if(FMLCommonHandler.instance().getEffectiveSide().isClient())
				arg2 = addPositionMethod(arg0, arg2);
		}
		if(!isLite)
		{
			if(arg0.equals("yd") | arg0.equals("net.minecraft.entity.item.EntityItem"))
			{
				ItemDummyContainer.logger.info("[ItemPhysic] Patching " + arg0);
				arg2 = replaceMethodOnUpdate(arg0, arg2);
				arg2 = addMethodIsBurning(arg0, arg2);
				arg2 = replaceMethods(arg0, arg2);
			}

			if(arg0.equals("bmt") | arg0.equals("net.minecraft.client.entity.EntityPlayerSP"))
			{
				obfuscated = !arg0.contains("net.minecraft.client.entity.EntityPlayerSP");
				ItemDummyContainer.logger.info("[ItemPhysic] Patching " + arg0);
				arg2 = removeDrop(arg0, arg2);
			}
		}
		return arg2;
	}
	
	public byte[] replaceMethodDoRender(String name, byte[] bytes)
	{
		String targetMethodName = patch("doRender");
		String targetDESC = patch("(Lnet/minecraft/entity/item/EntityItem;DDDFF)V");	
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if ((m.name.equals(targetMethodName) && m.desc.equals(targetDESC)))
			{
				m.localVariables.clear();
				
				m.instructions.clear();
				
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				m.instructions.add(new VarInsnNode(ALOAD, 1));
				m.instructions.add(new VarInsnNode(DLOAD, 2));
				m.instructions.add(new VarInsnNode(DLOAD, 4));
				m.instructions.add(new VarInsnNode(DLOAD, 6));
				m.instructions.add(new VarInsnNode(FLOAD, 8));
				m.instructions.add(new VarInsnNode(FLOAD, 9));
				m.instructions.add(new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ClientPhysic", "doRender", patch("(Lnet/minecraft/client/renderer/entity/RenderEntityItem;Lnet/minecraft/entity/Entity;DDDFF)V"), false));
				
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				m.instructions.add(new VarInsnNode(ALOAD, 1));
				m.instructions.add(new VarInsnNode(DLOAD, 2));
				m.instructions.add(new VarInsnNode(DLOAD, 4));
				m.instructions.add(new VarInsnNode(DLOAD, 6));
				m.instructions.add(new VarInsnNode(FLOAD, 8));
				m.instructions.add(new VarInsnNode(FLOAD, 9));
				m.instructions.add(new MethodInsnNode(INVOKESPECIAL, patch("net/minecraft/client/renderer/entity/Render"), targetMethodName, patch("(Lnet/minecraft/entity/Entity;DDDFF)V"), false));
				
				
				m.instructions.add(new InsnNode(RETURN));;
				break;
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	/*public byte[] makeFieldsPublic(String name, byte[] bytes, boolean ofuscated, String... fieldNames)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		Iterator<FieldNode> fields = classNode.fields.iterator();
		if(fieldNames.length > 0 && fieldNames.length % 2 == 0)
		{
			while(fields.hasNext())
			{
				FieldNode m = fields.next();
				for (int i = 0; i < fieldNames.length; i += 2) {
					String fieldName = patch(fieldNames[i]);
					String fieldDesc = patch(fieldNames[i+1]);
					if(m.name.equals(fieldName) && (fieldDesc == null || fieldDesc.equals("") || m.desc.equals(fieldDesc)))
						m.access = ACC_PUBLIC;
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}*/
	
	public byte[] addPositionMethod(String name, byte[] bytes)
	{
		String targetMethodName = patch("setPositionAndRotation2");
		String targetMethodDesc = "(DDDFFIZ)V";
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		MethodNode m = new MethodNode(ACC_PUBLIC, targetMethodName, targetMethodDesc, null, null);
		
		LabelNode label = new LabelNode();
		m.instructions.add(label);
		
		m.instructions.add(new VarInsnNode(ALOAD, 0));
		m.instructions.add(new VarInsnNode(DLOAD, 1));
		m.instructions.add(new VarInsnNode(DLOAD, 3));
		m.instructions.add(new VarInsnNode(DLOAD, 5));
		m.instructions.add(new VarInsnNode(FLOAD, 7));
		m.instructions.add(new VarInsnNode(FLOAD, 8));
		m.instructions.add(new VarInsnNode(ILOAD, 9));
		m.instructions.add(new VarInsnNode(ILOAD, 10));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ClientPhysic",  "setPositionAndRotation2", patch("(Lnet/minecraft/entity/item/EntityItem;DDDFFIZ)V"), false));	
		
		m.instructions.add(new InsnNode(RETURN));
		LabelNode label2 = new LabelNode();
		m.instructions.add(label2);
		m.localVariables.add(new LocalVariableNode("this", patch("Lnet/minecraft/entity/item/EntityItem;"), null, label, label2, 0));
		m.localVariables.add(new LocalVariableNode("x", "D", null, label, label2, 1));
		m.localVariables.add(new LocalVariableNode("y", "D", null, label, label2, 3));
		m.localVariables.add(new LocalVariableNode("z", "D", null, label, label2, 5));
		m.localVariables.add(new LocalVariableNode("yaw", "F", null, label, label2, 7));
		m.localVariables.add(new LocalVariableNode("pitch", "F", null, label, label2, 8));
		m.localVariables.add(new LocalVariableNode("posRotationIncrements", "I", null, label, label2, 9));
		m.localVariables.add(new LocalVariableNode("p_180426_10_", "Z", null, label, label2, 10));
		
		classNode.methods.add(m);
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] removeDrop(String name, byte[] bytes)
	{
		String invokeName = patch("dropOneItem");
		String invokeDESC = patch("(Z)Lnet/minecraft/entity/item/EntityItem;");
		
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
	
	public byte[] addMethodIsBurning(String name, byte[] bytes)
	{
		String targetMethodName = patch("isBurning");
		String targetDESC = patch("(Lnet/minecraft/entity/item/EntityItem;)Z");
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		MethodNode m = new MethodNode(ACC_PUBLIC, targetMethodName, "()Z", null, null);
		LabelNode label = new LabelNode();
		m.instructions.add(label);
		m.instructions.add(new VarInsnNode(ALOAD, 0));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic",  "isItemBurning", targetDESC, false));
		m.instructions.add(new InsnNode(IRETURN));
		LabelNode label2 = new LabelNode();
		m.instructions.add(label2);
		m.localVariables.add(new LocalVariableNode("this", "L" + name.replace(".", "/") + ";", null, label, label2, 0));
		classNode.methods.add(m);
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public byte[] replaceMethods(String name, byte[] bytes)
	{
		String targetMethodName = patch("attackEntityFrom");
		String targetDESC = patch("(Lnet/minecraft/util/DamageSource;F)Z");
		String targetNewDESC = patch("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/DamageSource;F)Z");
		String fieldName = patch("health");
		String targetMethodName2 = patch("onCollideWithPlayer");
		String targetDESC2 = patch("(Lnet/minecraft/entity/player/EntityPlayer;)V");
		String newDESC2 = patch("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;)V");
		String newDESC3 = patch("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)Z");
		String newMethod = patch("processInitialInteract");
		String newMethodDESC = patch("(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)Z");
		String newMethodVar = patch("Lnet/minecraft/entity/player/EntityPlayer;");
		//String newMethod2 = patch("canBeCollidedWith");
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		/*Iterator<FieldNode> fields = classNode.fields.iterator();
		while(fields.hasNext())
		{
			FieldNode f = fields.next();		
			if(f.name.equals(fieldName))
			{
				f.access = ACC_PUBLIC;
			}
		}*/
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
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "attackEntityFrom", targetNewDESC, false));
				m.instructions.add(new InsnNode(IRETURN));
			}
			if (m.name.equals(targetMethodName2) && m.desc.equals(targetDESC2))
			{
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				m.instructions.add(new VarInsnNode(ALOAD, 1));
				m.instructions.add(new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "onCollideWithPlayer", newDESC2, false));
				m.instructions.add(new InsnNode(RETURN));
			}
		}
		MethodNode m = new MethodNode(ACC_PUBLIC, newMethod, newMethodDESC, null, null);
		LabelNode label = new LabelNode();
		m.instructions.add(label);
		m.instructions.add(new VarInsnNode(ALOAD, 0));
		m.instructions.add(new VarInsnNode(ALOAD, 1));
		m.instructions.add(new VarInsnNode(ALOAD, 2));
		m.instructions.add(new VarInsnNode(ALOAD, 3));
		m.instructions.add(new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "processInitialInteract", newDESC3, false));
		LabelNode label2 = new LabelNode();
		m.instructions.add(label2);
		m.instructions.add(new InsnNode(ICONST_1));
		m.instructions.add(new InsnNode(IRETURN));
		
		m.maxLocals = 4;
		m.localVariables.add(new LocalVariableNode("this", "L" + name.replace(".", "/") + ";", null, label, label2, 0));
		m.localVariables.add(new LocalVariableNode("par1EntityPlayer", newMethodVar, null, label, label2, 1));
		m.localVariables.add(new LocalVariableNode("stack", patch("Lnet/minecraft/item/ItemStack;"), null, label, label2, 2));
		m.localVariables.add(new LocalVariableNode("hand", patch("Lnet/minecraft/util/EnumHand;"), null, label, label2, 3));
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
	
	public byte[] replaceMethodOnUpdate(String name, byte[] bytes)
	{
		String targetMethodName = patch("onUpdate");
		String targetDESC = patch("(Lnet/minecraft/entity/item/EntityItem;)V");
		
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
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic",  "update", targetDESC, false));
				m.instructions.add(new InsnNode(RETURN));
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
