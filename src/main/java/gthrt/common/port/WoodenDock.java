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
import gregtech.common.blocks.BlockSteamCasing;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.util.GTUtility;
import static gregtech.api.GTValues.*;

import net.minecraftforge.fluids.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;




public class WoodenDock extends MetaTileEntityPortControllerAbstract{


	public Fluid getFuel(){
		return null;
	}
	public int getFuelEfficiency(){
		return (int)(400 / (energyContainer!=null ? GTUtility.getFloorTierByVoltage(energyContainer.getInputVoltage())+1 : 0.5));
	}
	public int getFreightInternal(){
		return 12;
	}
	public int getSpeed(){
		return 9000; //450 secs
	}
	public int getCap(){
		return (int)(24 *(energyContainer!=null ? GTUtility.getFloorTierByVoltage(energyContainer.getInputVoltage())+1 : 0.5)) ;
	}

	public WoodenDock(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return new WoodenDock(metaTileEntityId);
	}


	@Override
		public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
		return Textures.VOLTAGE_CASINGS[ULV];
	}

	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
			.aisle(new String[] { "WWWW**","WWWW**","******","******"})
			.aisle(new String[] { "WWWW**","WDDW**","*##***","*##***"})
			.aisle(new String[] { "******","*DD***","*##***","*##***"})
			.aisle(new String[] { "******","*DD***","*##***","*##***"})
			.aisle(new String[] { "******","*DDDDD","*###XX","####XX"})
			.aisle(new String[] { "******","*DDDDD","*#S#XX","####XX"})
			.where('S', selfPredicate())
			.where('W', blocks(Blocks.WATER))
			.where('X', states(new IBlockState[] { MetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV) }).or(autoAbilities()))
			.where('D', states(new IBlockState[] { MetaBlocks.STEAM_CASING.getState(BlockSteamCasing.SteamCasingType.PUMP_DECK) }))
			.where('#', air())
			.where('*', any())
			.build();
	}

	@Override
	public TraceabilityPredicate autoAbilities(){
		TraceabilityPredicate out = new TraceabilityPredicate()
			.or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
			.or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
			.or(energyHatches(ULV,LV).setMaxGlobalLimited(2).setPreviewCount(1));

		return out;
	}






}
