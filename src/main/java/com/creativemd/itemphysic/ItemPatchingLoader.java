package com.creativemd.itemphysic;

import java.io.File;
import java.util.Map;

import com.creativemd.creativecore.transformer.TransformerNames;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class ItemPatchingLoader implements IFMLLoadingPlugin {
	
	public static File location;
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] { ItemTransformer.class.getName() };
	}
	
	@Override
	public String getModContainerClass() {
		return ItemDummyContainer.class.getName();
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
		location = (File) data.get("coremodLocation");
		TransformerNames.obfuscated = (boolean) data.get("runtimeDeobfuscationEnabled");
	}
	
	@Override
	public String getAccessTransformerClass() {
		return null;
	}
	
}
