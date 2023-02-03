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
		return 100;
	}
	public int getFreight(){
		return 5;
	}
	public int getSpeed(){
		return 200; //450 secs
	}
	public int getCap(){
		return 64; //a stack at most
	}

	public WoodenDock(ResourceLocation metaTileEntityId){
		super(metaTileEntityId);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return (MetaTileEntity)new WoodenDock(this.metaTileEntityId);
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
			.aisle(new String[] { "******","*DDDDD","*####X","#####X"})
			.aisle(new String[] { "******","*DDDDD","*##S#X","#####X"})
			.where('S', selfPredicate())
			.where('W', blocks(Blocks.WATER))
			.where('X', states(new IBlockState[] { MetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV) }).or(autoAbilities()))
			.where('D', states(new IBlockState[] { MetaBlocks.STEAM_CASING.getState(BlockSteamCasing.SteamCasingType.PUMP_DECK) }))
			.where('#', air())
			.where('*', any())
			.build();
	}







}
