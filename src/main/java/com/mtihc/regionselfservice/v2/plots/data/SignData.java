package com.mtihc.regionselfservice.v2.plots.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

public class SignData implements ISignData {
	
	private BlockVector blockVector;
	private BlockFace attachedFace;
	protected final SignType type;
	

	public SignData(SignType type, BlockVector coords, BlockFace attachedFace) {
		this.type = type;
		this.blockVector = coords;
		this.attachedFace = attachedFace;
	}
	
	
	
	
	
	public SignData(Map<String, Object> values) {
		this.type = SignType.valueOf(
				(String) values.get("type"));
		this.blockVector = (BlockVector) values.get("coords");
		this.attachedFace = BlockFace.valueOf(
				(String) values.get("attached-face"));
		
		
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("type", type.name());
		values.put("coords", blockVector);
		values.put("attached-face", attachedFace.name());
		return values;
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.data.ISignData#getBlockVector()
	 */
	@Override
	public BlockVector getBlockVector() {
		return blockVector;
	}
	
	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.data.ISignData#getAttachedFace()
	 */
	@Override
	public BlockFace getAttachedFace() {
		return attachedFace;
	}
	
	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.data.ISignData#getSignType()
	 */
	@Override
	public SignType getSignType() {
		return type;
	}

}
