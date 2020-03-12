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

import java.util.ArrayList;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.creativemd.creativecore.transformer.CreativeTransformer;
import com.creativemd.creativecore.transformer.Transformer;
import com.creativemd.creativecore.transformer.TransformerNames;

public class ItemTransformer extends CreativeTransformer {
	
	public ItemTransformer() {
		super("itemphysic");
	}
	
	public static boolean isLite = false;
	
	@Override
	protected void initTransformers() {
		addTransformer(new Transformer("net.minecraft.client.renderer.entity.RenderEntityItem") {
			
			@Override
			public void transform(ClassNode node) {
				String desc = patchDESC("(Lnet/minecraft/entity/item/EntityItem;DDDFF)V");
				String method = TransformerNames.patchMethodName("doRender", patchDESC("(Lnet/minecraft/entity/Entity;DDDFF)V"), patchClassName("net/minecraft/client/renderer/entity/Render"));
				MethodNode m = findMethod(node, method, desc);
				m.localVariables.clear();
				
				m.instructions.add(new LabelNode());
				
				AbstractInsnNode before = m.instructions.getFirst();
				
				//If
				m.instructions.insertBefore(before, new VarInsnNode(ALOAD, 0));
				m.instructions.insertBefore(before, new VarInsnNode(ALOAD, 1));
				m.instructions.insertBefore(before, new VarInsnNode(DLOAD, 2));
				m.instructions.insertBefore(before, new VarInsnNode(DLOAD, 4));
				m.instructions.insertBefore(before, new VarInsnNode(DLOAD, 6));
				m.instructions.insertBefore(before, new VarInsnNode(FLOAD, 8));
				m.instructions.insertBefore(before, new VarInsnNode(FLOAD, 9));
				m.instructions.insertBefore(before, new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ClientPhysic", "doRender", patchDESC("(L" + patchClassName("net/minecraft/client/renderer/entity/RenderEntityItem") + ";Lnet/minecraft/entity/Entity;DDDFF)Z"), false));
				
				LabelNode elseNode = new LabelNode();
				m.instructions.insertBefore(before, new JumpInsnNode(Opcodes.IFEQ, elseNode));
				
				//Then
				m.instructions.insertBefore(before, new LabelNode());
				
				m.instructions.insertBefore(before, new VarInsnNode(ALOAD, 0));
				m.instructions.insertBefore(before, new VarInsnNode(ALOAD, 1));
				m.instructions.insertBefore(before, new VarInsnNode(DLOAD, 2));
				m.instructions.insertBefore(before, new VarInsnNode(DLOAD, 4));
				m.instructions.insertBefore(before, new VarInsnNode(DLOAD, 6));
				m.instructions.insertBefore(before, new VarInsnNode(FLOAD, 8));
				m.instructions.insertBefore(before, new VarInsnNode(FLOAD, 9));
				m.instructions.insertBefore(before, new MethodInsnNode(INVOKESPECIAL, patchClassName("net/minecraft/client/renderer/entity/Render"), method, patchDESC("(Lnet/minecraft/entity/Entity;DDDFF)V"), false));
				
				m.instructions.insertBefore(before, new InsnNode(RETURN));
				
				//Else
				m.instructions.insertBefore(before, elseNode);
				m.instructions.insertBefore(before, new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
				
			}
		});
		addTransformer(new Transformer("net.minecraft.entity.item.EntityItem") {
			
			@Override
			public void transform(ClassNode node) {
				
				node.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "skipPhysicRenderer", "Z", null, false));
				
				String targetMethodDesc = "(DDDFFIZ)V";
				String targetMethodName = TransformerNames.patchMethodName("setPositionAndRotationDirect", targetMethodDesc, patchClassName("net/minecraft/entity/Entity"));
				
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
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ClientPhysic", "setPositionAndRotationDirect", patchDESC("(Lnet/minecraft/entity/item/EntityItem;DDDFFIZ)V"), false));
				
				m.instructions.add(new InsnNode(RETURN));
				LabelNode label2 = new LabelNode();
				m.instructions.add(label2);
				m.localVariables.add(new LocalVariableNode("this", patchDESC("Lnet/minecraft/entity/item/EntityItem;"), null, label, label2, 0));
				m.localVariables.add(new LocalVariableNode("x", "D", null, label, label2, 1));
				m.localVariables.add(new LocalVariableNode("y", "D", null, label, label2, 3));
				m.localVariables.add(new LocalVariableNode("z", "D", null, label, label2, 5));
				m.localVariables.add(new LocalVariableNode("yaw", "F", null, label, label2, 7));
				m.localVariables.add(new LocalVariableNode("pitch", "F", null, label, label2, 8));
				m.localVariables.add(new LocalVariableNode("posRotationIncrements", "I", null, label, label2, 9));
				m.localVariables.add(new LocalVariableNode("p_180426_10_", "Z", null, label, label2, 10));
				
				node.methods.add(m);
			}
		});
		if (!isLite) {
			addTransformer(new Transformer("net.minecraft.entity.item.EntityItem") {
				
				public ArrayList<AbstractInsnNode> getCallingNodes(String methodName, String desc) {
					ArrayList<AbstractInsnNode> nodes = new ArrayList<>();
					//nodes.add(new LabelNode());
					nodes.add(new VarInsnNode(ALOAD, 0));
					MethodInsnNode callingNode = new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", methodName, desc, false);
					nodes.add(callingNode);
					//nodes.add(new InsnNode(RETURN));
					return nodes;
				}
				
				@Override
				public void transform(ClassNode node) {
					
					//On UPDATE
					MethodNode m = findMethod(node, TransformerNames.patchMethodName("onUpdate", "()V", patchClassName("net/minecraft/entity/Entity")), "()V");
					
					String updateDESC = patchDESC("(Lnet/minecraft/entity/item/EntityItem;)V");
					
					//Pre
					MethodInsnNode methodNode = (MethodInsnNode) findNode(m.instructions, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, patchDESC("net/minecraft/entity/item/EntityItem"), TransformerNames.patchMethodName("hasNoGravity", "()Z", patchClassName("net/minecraft/entity/Entity")), "()Z", false));
					methodNode.setOpcode(Opcodes.INVOKESTATIC);
					methodNode.owner = "com/creativemd/itemphysic/physics/ServerPhysic";
					methodNode.name = "updatePre";
					methodNode.desc = patchDESC("(Lnet/minecraft/entity/item/EntityItem;)Z");
					
					//Burning
					String materialClassName = patchClassName("net/minecraft/block/material/Material");
					ArrayList<AbstractInsnNode> nodes = getCallingNodes("updateBurn", updateDESC);
					replaceLabel(m.instructions, new FieldInsnNode(Opcodes.GETSTATIC, materialClassName, TransformerNames.patchFieldName("LAVA", materialClassName), "L" + materialClassName + ";"), nodes, 5, true);
					
					//Post
					removeLabel(m.instructions, new LdcInsnNode(new Double("0.9800000190734863")), 1);
					nodes = getCallingNodes("updatePost", updateDESC);
					//new LineNumberNode(155, null)
					replaceLabelBefore(m.instructions, new LdcInsnNode(new Double("-0.5")), nodes, 1, 1, true, false);
					
					//Add Burning
					m = new MethodNode(ACC_PUBLIC, TransformerNames.patchMethodName("isBurning", "()Z", patchClassName("net/minecraft/entity/Entity")), "()Z", null, null);
					LabelNode label = new LabelNode();
					m.instructions.add(label);
					m.instructions.add(new VarInsnNode(ALOAD, 0));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "isItemBurning", patchDESC("(Lnet/minecraft/entity/item/EntityItem;)Z"), false));
					m.instructions.add(new InsnNode(IRETURN));
					LabelNode label2 = new LabelNode();
					m.instructions.add(label2);
					m.localVariables.add(new LocalVariableNode("this", "L" + patchClassName(className.replace(".", "/")) + ";", null, label, label2, 0));
					node.methods.add(m);
					
					//AttackEntityFrom
					String attackDESC = patchDESC("(Lnet/minecraft/util/DamageSource;F)Z");
					m = findMethod(node, TransformerNames.patchMethodName("attackEntityFrom", attackDESC, patchClassName("net/minecraft/entity/Entity")), attackDESC);
					m.instructions.clear();
					
					m.instructions.add(new VarInsnNode(ALOAD, 0));
					m.instructions.add(new VarInsnNode(ALOAD, 1));
					m.instructions.add(new VarInsnNode(FLOAD, 2));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "attackEntityFrom", patchDESC("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/DamageSource;F)Z"), false));
					m.instructions.add(new InsnNode(IRETURN));
					
					//onCollideWithPlayer
					String collideDESC = patchDESC("(Lnet/minecraft/entity/player/EntityPlayer;)V");
					m = findMethod(node, TransformerNames.patchMethodName("onCollideWithPlayer", collideDESC, patchClassName("net/minecraft/entity/Entity")), collideDESC);
					
					//m.instructions.clear();
					AbstractInsnNode before = m.instructions.getFirst();
					LabelNode label0 = new LabelNode();
					LabelNode labelElse = new LabelNode();
					LabelNode labelIf = new LabelNode();
					m.instructions.insertBefore(before, label0);
					m.instructions.insertBefore(before, new VarInsnNode(ALOAD, 0));
					m.instructions.insertBefore(before, new VarInsnNode(ALOAD, 1));
					m.instructions.insertBefore(before, new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "onCollideWithPlayer", patchDESC("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;)Z"), false));
					
					m.instructions.insertBefore(before, new JumpInsnNode(Opcodes.IFEQ, labelElse));
					
					m.instructions.insertBefore(before, labelIf);
					m.instructions.insertBefore(before, new InsnNode(RETURN));
					
					m.instructions.insertBefore(before, labelElse);
					m.instructions.remove(before);
					
					//processInitialInteract
					String interactDESC = patchDESC("(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)Z");
					
					m = new MethodNode(ACC_PUBLIC, TransformerNames.patchMethodName("processInitialInteract", interactDESC, patchClassName("net/minecraft/entity/Entity")), interactDESC, null, null);
					label = new LabelNode();
					m.instructions.add(label);
					m.instructions.add(new VarInsnNode(ALOAD, 0));
					m.instructions.add(new VarInsnNode(ALOAD, 1));
					m.instructions.add(new VarInsnNode(ALOAD, 2));
					m.instructions.add(new VarInsnNode(ALOAD, 3));
					m.instructions.add(new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "processInitialInteract", patchDESC("(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)Z"), false));
					label2 = new LabelNode();
					m.instructions.add(label2);
					m.instructions.add(new InsnNode(ICONST_1));
					m.instructions.add(new InsnNode(IRETURN));
					
					m.maxLocals = 4;
					m.localVariables.add(new LocalVariableNode("this", patchDESC("Lnet/minecraft/entity/item/EntityItem;"), null, label, label2, 0));
					m.localVariables.add(new LocalVariableNode("player", patchDESC("Lnet/minecraft/entity/player/EntityPlayer;"), null, label, label2, 1));
					m.localVariables.add(new LocalVariableNode("stack", patchDESC("Lnet/minecraft/item/ItemStack;"), null, label, label2, 2));
					m.localVariables.add(new LocalVariableNode("hand", patchDESC("Lnet/minecraft/util/EnumHand;"), null, label, label2, 3));
					node.methods.add(m);
					
					//updateFallState
					String updateFallStateDESC = patchDESC("(DZLnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)V");
					String updateFallStateName = TransformerNames.patchMethodName("updateFallState", updateFallStateDESC, patchClassName("net/minecraft/entity/Entity"));
					m = new MethodNode(ACC_PUBLIC, updateFallStateName, updateFallStateDESC, null, null);
					label = new LabelNode();
					m.instructions.add(label);
					m.instructions.add(new VarInsnNode(ALOAD, 0));
					m.instructions.add(new VarInsnNode(DLOAD, 1));
					m.instructions.add(new VarInsnNode(ILOAD, 3));
					m.instructions.add(new VarInsnNode(ALOAD, 4));
					m.instructions.add(new VarInsnNode(ALOAD, 5));
					m.instructions.add(new MethodInsnNode(INVOKESTATIC, "com/creativemd/itemphysic/physics/ServerPhysic", "updateFallState", patchDESC("(Lnet/minecraft/entity/item/EntityItem;DZLnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)V"), false));
					m.instructions.add(new LabelNode());
					
					m.instructions.add(new VarInsnNode(ALOAD, 0));
					m.instructions.add(new VarInsnNode(DLOAD, 1));
					m.instructions.add(new VarInsnNode(ILOAD, 3));
					m.instructions.add(new VarInsnNode(ALOAD, 4));
					m.instructions.add(new VarInsnNode(ALOAD, 5));
					m.instructions.add(new MethodInsnNode(INVOKESPECIAL, patchClassName("net/minecraft/entity/Entity"), updateFallStateName, updateFallStateDESC, false));
					label2 = new LabelNode();
					m.instructions.add(label2);
					m.instructions.add(new InsnNode(RETURN));
					
					m.maxLocals = 5;
					m.maxStack = 6;
					
					m.localVariables.add(new LocalVariableNode("this", patchDESC("Lnet/minecraft/entity/item/EntityItem;"), null, label, label2, 0));
					m.localVariables.add(new LocalVariableNode("y", "D", null, label, label2, 1));
					m.localVariables.add(new LocalVariableNode("onGroundIn", "Z", null, label, label2, 3));
					m.localVariables.add(new LocalVariableNode("state", patchDESC("Lnet/minecraft/block/state/IBlockState;"), null, label, label2, 4));
					m.localVariables.add(new LocalVariableNode("pos", patchDESC("Lnet/minecraft/util/math/BlockPos;"), null, label, label2, 5));
					node.methods.add(m);
				}
			});
			addTransformer(new Transformer("net.minecraft.client.entity.EntityPlayerSP") {
				
				@Override
				public void transform(ClassNode node) {
					String desc = patchDESC("(Z)Lnet/minecraft/entity/item/EntityItem;");
					MethodNode m = findMethod(node, TransformerNames.patchMethodName("dropItem", desc, patchClassName("net/minecraft/entity/player/EntityPlayer")), desc);
					m.instructions.clear();
					m.instructions.add(new LabelNode());
					m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/itemphysic/EventHandler", "dropItem", "(Z)V", false));
					m.instructions.add(new LabelNode());
					m.instructions.add(new InsnNode(ACONST_NULL));
					m.instructions.add(new InsnNode(ARETURN));
				}
			});
		}
	}
}
