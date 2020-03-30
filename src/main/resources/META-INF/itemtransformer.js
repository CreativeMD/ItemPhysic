function initializeCoreMod() {
	print("Init ItemPhysic coremods ...")
    return {
        'renderer': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.renderer.entity.ItemRenderer',
				'methodName': 'func_225623_a_',
				'methodDesc': '(Lnet/minecraft/entity/item/ItemEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V'
            },
            'transformer': function(method) {
				var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
				var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
				var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
				var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				
				var renderMethodname = asmapi.mapMethod("func_225623_a_");
				
				var start = method.instructions.getFirst();
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ILOAD, 6));
				
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/ItemRenderer", asmapi.mapField("field_177080_a"), "Lnet/minecraft/client/renderer/ItemRenderer;"));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/ItemRenderer", asmapi.mapField("field_177079_e"), "Ljava/util/Random;"));
				
				method.instructions.insertBefore(start, asmapi.buildMethodCall("team/creative/itemphysic/client/ItemPhysicClient", "renderItem", "(Lnet/minecraft/entity/item/ItemEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/renderer/ItemRenderer;Ljava/util/Random;)Z", asmapi.MethodType.STATIC));
				
				method.instructions.insertBefore(start, new JumpInsnNode(Opcodes.IFEQ, start));
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ILOAD, 6));
				method.instructions.insertBefore(start, asmapi.buildMethodCall("net/minecraft/client/renderer/entity/EntityRenderer", renderMethodname, "(Lnet/minecraft/entity/Entity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", asmapi.MethodType.SPECIAL));
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new InsnNode(Opcodes.RETURN));
				
                return method;
            }
		},
		'dropItem': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.entity.player.ClientPlayerEntity',
				'methodName': 'func_225609_n_',
				'methodDesc': '(Z)Z'
            },
            'transformer': function(method) {
            	var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
            	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
            	var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
            	var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
            	var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
            	var Opcodes = Java.type('org.objectweb.asm.Opcodes');
            	
            	var start = method.instructions.getFirst();
            	
            	method.instructions.insertBefore(start, new LabelNode());
            	method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ILOAD, 1));
            	method.instructions.insertBefore(start, asmapi.buildMethodCall("team/creative/itemphysic/client/ItemPhysicClient", "dropItem", "(Z)Z", asmapi.MethodType.STATIC));
				
				method.instructions.insertBefore(start, new JumpInsnNode(Opcodes.IFEQ, start));
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new InsnNode(Opcodes.ICONST_1));
				method.instructions.insertBefore(start, new InsnNode(Opcodes.IRETURN));
				
				return method;        
            }
        },
        'attack': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.entity.item.ItemEntity',
				'methodName': 'func_70097_a',
				'methodDesc': '(Lnet/minecraft/util/DamageSource;F)Z'
            },
            'transformer': function(method) {
            	var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
            	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
            	var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
            	var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
            	var Opcodes = Java.type('org.objectweb.asm.Opcodes');
            	
            	method.instructions.clear();
            	
            	method.instructions.add(new LabelNode());
            	method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.add(asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "attackEntityFrom", "(Lnet/minecraft/entity/item/ItemEntity;Lnet/minecraft/util/DamageSource;F)Z", asmapi.MethodType.STATIC));
				method.instructions.add(new InsnNode(Opcodes.IRETURN));
				
				return method;
            }
        },
        'overrideMethod': {
            'target': {
                'type': 'CLASS',
				'name': 'net.minecraft.entity.item.ItemEntity'
            },
            'transformer': function(node) {
            	var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
            	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
            	var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
            	var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
            	var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
            	var LocalVariableNode = Java.type('org.objectweb.asm.tree.LocalVariableNode');
            	var Opcodes = Java.type('org.objectweb.asm.Opcodes');
            	
            	//processInitialInteract
				var method = new MethodNode(Opcodes.ACC_PUBLIC, asmapi.mapMethod("func_184230_a"), "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Z", null, null);
				var label = new LabelNode();	
				method.instructions.add(label);
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
				method.instructions.add(asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "processInitialInteract", "(Lnet/minecraft/entity/item/ItemEntity;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Z", asmapi.MethodType.STATIC));
				var label2 = new LabelNode();
				method.instructions.add(label2);
				method.instructions.add(new InsnNode(Opcodes.ICONST_1));
				method.instructions.add(new InsnNode(Opcodes.IRETURN));
				
				method.maxStack = 6;
				method.maxLocals = 4;
				method.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/entity/item/ItemEntity;", null, label, label2, 0));
				method.localVariables.add(new LocalVariableNode("player", "Lnet/minecraft/entity/player/PlayerEntity;", null, label, label2, 1));
				method.localVariables.add(new LocalVariableNode("hand", "Lnet/minecraft/util/Hand;", null, label, label2, 2));
				node.methods.add(method);
				
				//updateFallState
				method = new MethodNode(Opcodes.ACC_PUBLIC, asmapi.mapMethod("func_184231_a"), "(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V", null, null);
				label = new LabelNode();
				method.instructions.add(label);
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.add(asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "updateFallState", "(Lnet/minecraft/entity/item/ItemEntity;DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V", asmapi.MethodType.STATIC));
				method.instructions.add(new LabelNode());
				
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.add(asmapi.buildMethodCall("net/minecraft/entity/Entity", asmapi.mapMethod("func_184231_a"), "(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V", asmapi.MethodType.SPECIAL));
				label2 = new LabelNode();
				method.instructions.add(label2);
				method.instructions.add(new InsnNode(Opcodes.RETURN));
				
				method.maxLocals = 5;
				method.maxStack = 6;
				
				method.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/entity/item/ItemEntity;", null, label, label2, 0));
				method.localVariables.add(new LocalVariableNode("y", "D", null, label, label2, 1));
				method.localVariables.add(new LocalVariableNode("onGroundIn", "Z", null, label, label2, 3));
				method.localVariables.add(new LocalVariableNode("state", "Lnet/minecraft/block/BlockState;", null, label, label2, 4));
				method.localVariables.add(new LocalVariableNode("pos", "Lnet/minecraft/util/math/BlockPos;", null, label, label2, 5));
				node.methods.add(method);
				
				//Add Burning
				method = new MethodNode(Opcodes.ACC_PUBLIC, asmapi.mapMethod("func_70027_ad"), "()Z", null, null);
				var label = new LabelNode();
				method.instructions.add(label);
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "isItemBurning", "(Lnet/minecraft/entity/item/ItemEntity;)Z", asmapi.MethodType.STATIC));
				method.instructions.add(new InsnNode(Opcodes.IRETURN));
				var label2 = new LabelNode();
				method.instructions.add(label2);
				method.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/entity/item/ItemEntity;", null, label, label2, 0));
				node.methods.add(method);
				
				method.maxLocals = 1;
				method.maxStack = 3;
				
				return node;
            }
        },
        'collide': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.entity.item.ItemEntity',
				'methodName': 'func_70100_b_',
				'methodDesc': '(Lnet/minecraft/entity/player/PlayerEntity;)V'
            },
            'transformer': function(method) {
            	var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
            	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
            	var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
            	var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
            	var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
            	var Opcodes = Java.type('org.objectweb.asm.Opcodes');
            	
				var before = method.instructions.getFirst();
				
				method.instructions.insertBefore(before, new LabelNode());
				method.instructions.insertBefore(before, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(before, new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.insertBefore(before, asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "onCollideWithPlayer", "(Lnet/minecraft/entity/item/ItemEntity;Lnet/minecraft/entity/player/PlayerEntity;)Z", asmapi.MethodType.STATIC));
				
				method.instructions.insertBefore(before, new JumpInsnNode(Opcodes.IFEQ, before));
				
				method.instructions.insertBefore(before, new LabelNode());
				method.instructions.insertBefore(before, new InsnNode(Opcodes.RETURN));
				
				return method;
            }
        },
        'tick': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.entity.item.ItemEntity',
				'methodName': 'func_70071_h_',
				'methodDesc': '()V'
            },
            'transformer': function(method) {
            	var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
            	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
            	var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
            	var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
            	var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
            	var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
            	var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');
            	var LineNumberNode = Java.type('org.objectweb.asm.tree.LineNumberNode');
            	var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
            	var Opcodes = Java.type('org.objectweb.asm.Opcodes');
            	
            	// update pre
            	var pre = asmapi.findFirstMethodCall(method, asmapi.MethodType.VIRTUAL, "net/minecraft/entity/item/ItemEntity", asmapi.mapMethod("func_208600_a"), "(Lnet/minecraft/tags/Tag;)Z");
            	method.instructions.remove(pre.getPrevious());
            	method.instructions.insertBefore(pre, asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "updatePre", "(Lnet/minecraft/entity/item/ItemEntity;)V", asmapi.MethodType.STATIC));
				method.instructions.insertBefore(pre, new LabelNode());
				method.instructions.insertBefore(pre, new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
				
				
				var next = pre.getNext();
				while(!(next instanceof FieldInsnNode) || next.owner !== "net/minecraft/entity/item/ItemEntity" || next.name !== asmapi.mapField("field_70170_p")) {
					method.instructions.remove(next.getPrevious());
					next = next.getNext();
				}
				
				// update burn
				var burning = asmapi.findFirstMethodCall(method, asmapi.MethodType.VIRTUAL, "net/minecraft/world/World", asmapi.mapMethod("func_204610_c"), "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/IFluidState;");
				
				for(var i = 0; i < 5; i++)
					method.instructions.remove(burning.getPrevious());
				
				method.instructions.insertBefore(burning, asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "updateBurn", "(Lnet/minecraft/entity/item/ItemEntity;)V", asmapi.MethodType.STATIC));
				method.instructions.insertBefore(burning, new LabelNode());
				
				next = burning;
				while(!(next instanceof FrameNode)) {
					next = next.getNext();
					method.instructions.remove(next.getPrevious());
				}
            	
            	// update            	
            	var post = asmapi.findFirstMethodCall(method, asmapi.MethodType.VIRTUAL, "net/minecraft/block/BlockState", "getSlipperiness", "(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)F");
            	
            	next = post;
				while(!(next instanceof FrameNode)) {
					next = next.getNext();
				}
				
				var local = next.getNext().getNext().getNext().getNext().var;
				next = next.getNext();
				
				var fromIndex = method.instructions.indexOf(next);
				var call = asmapi.findFirstMethodCallAfter(method, asmapi.MethodType.VIRTUAL, "net/minecraft/entity/item/ItemEntity", asmapi.mapMethod("func_213317_d"), "(Lnet/minecraft/util/math/Vec3d;)V", fromIndex).getNext();
				
				while (next !== call) {
					next = next.getNext();
					method.instructions.remove(next.getPrevious());
				}
				
				method.instructions.insertBefore(next, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(next, new VarInsnNode(Opcodes.FLOAD, local));
				method.instructions.insertBefore(next, asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "update", "(Lnet/minecraft/entity/item/ItemEntity;F)V", asmapi.MethodType.STATIC));
            	return method;
            }
        }
    }
}
