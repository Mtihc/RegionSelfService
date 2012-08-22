package com.mtihc.regionselfservice.v2.plots.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

public class SignData implements ISignData {

	protected final SignType type;
	private BlockVector blockVector;
	private BlockFace attachedFace;
	private String regionId;
	

	public SignData(SignType type, BlockVector coords, BlockFace attachedFace, String regionId) {
		this.type = type;
		this.blockVector = coords;
		this.attachedFace = attachedFace;
		this.regionId = regionId;
	}
	
	
	
	
	
	public SignData(Map<String, Object> values) {
		this.type = SignType.valueOf(
				(String) values.get("type"));
		this.blockVector = (BlockVector) values.get("coords");
		this.attachedFace = BlockFace.valueOf(
				(String) values.get("attached-face"));
		this.regionId = (String) values.get("region");
		
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("type", type.name());
		values.put("coords", blockVector);
		values.put("attached-face", attachedFace.name());
		values.put("region", regionId);
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
	
	/* (non-Javadoc)
	 * @see com.mtihc.regionselfservice.v2.plots.data.ISignData#getRegionId()
	 */
	@Override
	public String getRegionId() {
		return regionId;
	}

}
