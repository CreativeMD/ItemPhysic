function initializeCoreMod() {
	print("Init ItemPhysic coremods ...")
    return {
        'renderer': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.renderer.entity.ItemEntityRenderer',
				'methodName': 'm_7392_',
				'methodDesc': '(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V'
            },
            'transformer': function(method) {
				var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
				var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
				var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
				var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				
				var renderMethodname = asmapi.mapMethod("m_7392_");
				
				var start = method.instructions.getFirst();
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ILOAD, 6));
				
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/ItemEntityRenderer", asmapi.mapField("f_115019_"), "Lnet/minecraft/client/renderer/entity/ItemRenderer;"));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/ItemEntityRenderer", asmapi.mapField("f_115020_"), "Ljava/util/Random;"));
				
				method.instructions.insertBefore(start, asmapi.buildMethodCall("team/creative/itemphysic/client/ItemPhysicClient", "render", "(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/ItemRenderer;Ljava/util/Random;)Z", asmapi.MethodType.STATIC));
				
				method.instructions.insertBefore(start, new JumpInsnNode(Opcodes.IFEQ, start));
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.insertBefore(start, new VarInsnNode(Opcodes.ILOAD, 6));
				method.instructions.insertBefore(start, asmapi.buildMethodCall("net/minecraft/client/renderer/entity/EntityRenderer", renderMethodname, "(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", asmapi.MethodType.SPECIAL));
				
				method.instructions.insertBefore(start, new LabelNode());
				method.instructions.insertBefore(start, new InsnNode(Opcodes.RETURN));
				
                return method;
            }
		},
		'dropItem': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.player.LocalPlayer',
				'methodName': 'm_108700_',
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
				'class': 'net.minecraft.world.entity.item.ItemEntity',
				'methodName': 'm_6469_',
				'methodDesc': '(Lnet/minecraft/world/damagesource/DamageSource;F)Z'
            },
            'transformer': function(method) {
            	var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
            	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
            	var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
            	var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
            	var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
            	var Opcodes = Java.type('org.objectweb.asm.Opcodes');
            	
                method.instructions.clear();
            	
            	method.instructions.add(new LabelNode());
            	method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.FLOAD, 2));
				method.instructions.add(asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "hurt", "(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/damagesource/DamageSource;F)Z", asmapi.MethodType.STATIC));
				method.instructions.add(new InsnNode(Opcodes.IRETURN));
            	
				
				return method;
            }
        },
        'overrideMethod': {
            'target': {
                'type': 'CLASS',
				'name': 'net.minecraft.world.entity.item.ItemEntity'
            },
            'transformer': function(node) {
            	var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
            	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
            	var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
            	var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
            	var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
            	var FieldNode = Java.type('org.objectweb.asm.tree.FieldNode');
            	var LocalVariableNode = Java.type('org.objectweb.asm.tree.LocalVariableNode');
            	var Opcodes = Java.type('org.objectweb.asm.Opcodes');
            	
            	//interact
				var method = new MethodNode(Opcodes.ACC_PUBLIC, asmapi.mapMethod("m_6096_"), "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", null, null);
				var label = new LabelNode();	
				method.instructions.add(label);
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
				method.instructions.add(asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "interact", "(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", asmapi.MethodType.STATIC));
				var label2 = new LabelNode();
				method.instructions.add(label2);
				method.instructions.add(new InsnNode(Opcodes.ARETURN));
				
				method.maxStack = 6;
				method.maxLocals = 4;
				method.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/world/entity/item/ItemEntity;", null, label, label2, 0));
				method.localVariables.add(new LocalVariableNode("player", "Lnet/minecraft/world/entity/player/Player;", null, label, label2, 1));
				method.localVariables.add(new LocalVariableNode("hand", "Lnet/minecraft/world/InteractionHand;", null, label, label2, 2));
				node.methods.add(method);
				
				//checkFallDamage
				method = new MethodNode(Opcodes.ACC_PUBLIC, asmapi.mapMethod("m_7840_"), "(DZLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V", null, null);
				label = new LabelNode();
				method.instructions.add(label);
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.add(asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "checkFallDamage", "(Lnet/minecraft/world/entity/item/ItemEntity;DZLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V", asmapi.MethodType.STATIC));
				method.instructions.add(new LabelNode());
				
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
				method.instructions.add(asmapi.buildMethodCall("net/minecraft/world/entity/Entity", asmapi.mapMethod("m_7840_"), "(DZLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V", asmapi.MethodType.SPECIAL));
				label2 = new LabelNode();
				method.instructions.add(label2);
				method.instructions.add(new InsnNode(Opcodes.RETURN));
				
				method.maxLocals = 5;
				method.maxStack = 6;
				
				method.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/world/entity/item/ItemEntity;", null, label, label2, 0));
				method.localVariables.add(new LocalVariableNode("y", "D", null, label, label2, 1));
				method.localVariables.add(new LocalVariableNode("onGroundIn", "Z", null, label, label2, 3));
				method.localVariables.add(new LocalVariableNode("state", "Lnet/minecraft/world/level/block/state/BlockState;", null, label, label2, 4));
				method.localVariables.add(new LocalVariableNode("pos", "Lnet/minecraft/core/BlockPos;", null, label, label2, 5));
				node.methods.add(method);
				
				//Add updateFluidHeightAndDoFluidPushing
				var method = new MethodNode(Opcodes.ACC_PUBLIC, asmapi.mapMethod("m_19943_"), "(Lnet/minecraft/tags/TagKey;D)Z", null, null);
				var label = new LabelNode();	
				method.instructions.add(label);
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.DLOAD, 2));
				method.instructions.add(asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "updateFluidHeightAndDoFluidPushing", "(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/tags/TagKey;D)Z", asmapi.MethodType.STATIC));
				var label2 = new LabelNode();
				method.instructions.add(label2);
				method.instructions.add(new InsnNode(Opcodes.IRETURN));
				
				method.maxStack = 6;
				method.maxLocals = 4;
				method.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/world/entity/item/ItemEntity;", null, label, label2, 0));
				method.localVariables.add(new LocalVariableNode("tag", "Lnet/minecraft/tags/TagKey;", null, label, label2, 1));
				method.localVariables.add(new LocalVariableNode("var", "D", null, label, label2, 2));
				node.methods.add(method);
				
				node.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "skipPhysicRenderer", "Z", null, false));
				
				return node;
            }
        },
        'collide': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.world.entity.item.ItemEntity',
				'methodName': 'm_6123_',
				'methodDesc': '(Lnet/minecraft/world/entity/player/Player;)V'
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
				method.instructions.insertBefore(before, asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "playerTouch", "(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/entity/player/Player;)Z", asmapi.MethodType.STATIC));
				
				method.instructions.insertBefore(before, new JumpInsnNode(Opcodes.IFEQ, before));
				
				method.instructions.insertBefore(before, new LabelNode());
				method.instructions.insertBefore(before, new InsnNode(Opcodes.RETURN));
				
				return method;
            }
        },
        'fireImmune': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.world.entity.item.ItemEntity',
				'methodName': 'm_5825_',
				'methodDesc': '()Z'
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
				method.instructions.insertBefore(before, asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "fireImmune", "(Lnet/minecraft/world/entity/item/ItemEntity;)Z", asmapi.MethodType.STATIC));
				
				method.instructions.insertBefore(before, new JumpInsnNode(Opcodes.IFEQ, before));
				
				method.instructions.insertBefore(before, new LabelNode());
				method.instructions.insertBefore(before, new InsnNode(Opcodes.ICONST_1));
				method.instructions.insertBefore(before, new InsnNode(Opcodes.IRETURN));
				
				return method;
            }
        },
        'tick': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.world.entity.item.ItemEntity',
				'methodName': 'm_8119_',
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
            	var pre = asmapi.findFirstMethodCall(method, asmapi.MethodType.VIRTUAL, "net/minecraft/world/entity/item/ItemEntity", asmapi.mapMethod("m_20069_"), "()Z").getPrevious();
            	var end = asmapi.findFirstMethodCall(method, asmapi.MethodType.VIRTUAL, "net/minecraft/world/entity/item/ItemEntity", asmapi.mapMethod("m_20068_"), "()Z");
            	
            	for(var i = 0; i < 12; i++) {
            		end = end.getNext();
            	}
            	var current = pre;
            	while(current != end) {
            		var temp = current;
            		current = current.getNext();
            		method.instructions.remove(temp);
            	}
            	method.instructions.insertBefore(end, new VarInsnNode(Opcodes.ALOAD, 0));
            	method.instructions.insertBefore(end, asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "updatePre", "(Lnet/minecraft/world/entity/item/ItemEntity;)V", asmapi.MethodType.STATIC));
            	
            	// update            	
            	var post = asmapi.findFirstMethodCall(method, asmapi.MethodType.VIRTUAL, "net/minecraft/world/level/block/state/BlockState", "getFriction", "(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F");
            	
            	asmapi.log("INFO", "find block call");
            	next = post;
				while(!(next instanceof FrameNode)) {
					next = next.getNext();
				}				
				
				var local = next.getNext().getNext().getNext().getNext().var;
				next = next.getNext();
				
				var fromIndex = method.instructions.indexOf(next);
				var call = asmapi.findFirstMethodCallAfter(method, asmapi.MethodType.VIRTUAL, "net/minecraft/world/entity/item/ItemEntity", asmapi.mapMethod("m_20256_"), "(Lnet/minecraft/world/phys/Vec3;)V", fromIndex).getNext();		
				
				while (next !== call) {
					next = next.getNext();
					method.instructions.remove(next.getPrevious());
				}
				
				method.instructions.insertBefore(next, new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.insertBefore(next, new VarInsnNode(Opcodes.FLOAD, local));
				method.instructions.insertBefore(next, asmapi.buildMethodCall("team/creative/itemphysic/server/ItemPhysicServer", "update", "(Lnet/minecraft/world/entity/item/ItemEntity;F)V", asmapi.MethodType.STATIC));
				
            	return method;
            }
        }
    }
}
