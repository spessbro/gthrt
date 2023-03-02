package gthrt.common.packingline;

import gregtech.api.pattern.BlockPattern;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.core.sound.GTSoundEvents;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.SoundEvent;

import gthrt.common.HRTUtils;
import gthrt.common.block.HRTBlocks;

import java.util.List;

public class PackingLineController extends RecipeMapMultiblockController{
	public float packageProgress = 0;
	public int tier;
	public PackingLineController(ResourceLocation metaTileEntityId,int _tier){
		super(metaTileEntityId, null);
		tier = _tier;
		recipeMapWorkable = new PackingLogic(this);
	}


    @Override
    protected void addDisplayText(List<ITextComponent> textList){
    	super.addDisplayText(textList);
    	if(isStructureFormed()){
    		textList.add(new TextComponentTranslation("multiblock.packingline.packageProgress",HRTUtils.variableRound(packageProgress)));
    	}
    }

	@Override
    protected BlockPattern createStructurePattern() {
    	return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.UP, RelativeDirection.BACK)
    								.aisle("SSS", "SCS", "#P#")
                					.aisle("SIS", "GFG", "#P#").setRepeatable(3, Math.min(3*tier,15))
                					.aisle("SSS", "SOS", "#P#")
                					.where('C', selfPredicate())
                					.where('I', metaTileEntities(MetaTileEntities.ITEM_IMPORT_BUS[0]))
                					.where('O', abilities(MultiblockAbility.EXPORT_ITEMS))
                					.where('P', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID))
																			  .or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1)))
                					.where('S', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID)))
                					.where('F', states(HRTBlocks.PACKER_CASING.getState(tier)))
									.where('G', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS)))
                					.where('#', any())
                					.build();

    }
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
    	return Textures.VOLTAGE_CASINGS[tier];
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return new PackingLineController(metaTileEntityId,tier);
	}
	@Override
	public SoundEvent getSound(){
		return GTSoundEvents.ASSEMBLER;
	}
	@Override
	public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn, boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut, boolean checkMuffler) {
		return null;
	}

}
