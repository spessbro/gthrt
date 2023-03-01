package gthrt.common.port;

import gregtech.client.renderer.texture.Textures;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.util.GTUtility;
import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.Diesel;

import net.minecraftforge.fluids.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;




public class SteelHarbor extends MetaTileEntityPortControllerAbstract{


	public Fluid getFuel(){
		return Diesel.getFluid();
	}
	public int getFuelEfficiency(){
		return (int) (26/(GTUtility.getFloorTierByVoltage(energyContainer.getInputVoltage())+1));
	}
	public int getFreightInternal(){
		return 5;
	}
	public int getSpeed(){
		return 18000; //900 secs
	}
	public int getCap(){
		return 128*GTUtility.getFloorTierByVoltage(energyContainer.getInputVoltage());
	}

	public SteelHarbor(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return (MetaTileEntity)new SteelHarbor(this.metaTileEntityId);
	}


	@Override
		public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
		return Textures.SOLID_STEEL_CASING;
	}

	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
			.aisle(new String[] { "****WWW****","DDDDWWWDDDD","*XX*###*XX*","*XX*###*XX*"})
			.aisle(new String[] { "****WWW****","DDDDWWWDDDD","*XX*###*XX*","*XX*###*XX*"})
			.aisle(new String[] { "****WWW****","DDDDWWWDDDD","*XX*###*XX*","*XX*###*XX*"})
			.aisle(new String[] { "****WWW****","DDDDWWWDDDD","****###****","****###****"})
			.aisle(new String[] { "****WWW****","DDDDWWWDDDD","*XX*###*XX*","*XX*###*XX*"})
			.aisle(new String[] { "****WWW****","DDDDWWWDDDD","*XX*###*XX*","*XX*###*XX*"})
			.aisle(new String[] { "***********","DDDDDDDDDDD","*XX*****XX*","*XX*****XX*"})
			.aisle(new String[] { "***********","DDDDDDDDDDD","*****S*****","***********"})
			.where('S', selfPredicate())
			.where('W', blocks(Blocks.WATER))
			.where('X', states(new IBlockState[] { MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID) }).or(autoAbilities()))
			.where('D', states(new IBlockState[] { MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID) }))
			.where('#', air())
			.where('*', any())
			.build();
	}
	@Override //for helping building pattern; do later/handle on the multiblock subtypes
	public TraceabilityPredicate autoAbilities(){
		TraceabilityPredicate out = new TraceabilityPredicate()
			.or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
			.or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
			.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1))
			.or(energyHatches(LV,HV).setMinGlobalLimited(1).setMaxGlobalLimited(2).setPreviewCount(1));

		return out;
	}







}
