package gthrt.common.packingline;

import gregtech.api.pattern.BlockPattern;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.core.sound.GTSoundEvents;
import gregtech.api.util.RelativeDirection;
import static gregtech.api.GTValues.*;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;

import gthrt.common.HRTUtils;
import gthrt.common.block.HRTBlocks;
import gthrt.common.market.MarketHandler;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;

public class PackingLineController extends RecipeMapMultiblockController{

	public int tier;
	public PackingLineController(ResourceLocation metaTileEntityId,int _tier){
		super(metaTileEntityId, null);
		tier = _tier;
		recipeMapWorkable = new PackingLogic(this);
	}

	@Override
    protected BlockPattern createStructurePattern() {
    	return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                					.aisle("SSS", "SCS", "#S#")
                					.aisle("SIS", "GFG", "#P#").setRepeatable(3, 15)
    								.aisle("SSS", "SOS", "#S#")
                					.where('C', selfPredicate())
                					.where('I', metaTileEntities(MetaTileEntities.ITEM_IMPORT_BUS[0]).or(states(MetaBlocks.METAL_CASING.getState(getMetalCasing()))))
                					.where('O', abilities(MultiblockAbility.EXPORT_ITEMS))
                					.where('P', states(MetaBlocks.METAL_CASING.getState(getMetalCasing()))
																			  .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setPreviewCount(1))
																			  .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setMinGlobalLimited(1).setPreviewCount(1)))
                					.where('S', states(MetaBlocks.METAL_CASING.getState(getMetalCasing())))
                					.where('F', states(HRTBlocks.PACKER_CASING.getState(tier)))
									.where('G', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS)))
                					.where('#', any())
                					.build();

    }
    private BlockMetalCasing.MetalCasingType getMetalCasing(){
    	switch(tier){
    		case LV:
    			return BlockMetalCasing.MetalCasingType.STEEL_SOLID;
    		case MV:
    			return BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF;
    		case HV:
    			return BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN;
    		case EV:
    			return BlockMetalCasing.MetalCasingType.TITANIUM_STABLE;
    		default:
    			return BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST;
    	}
    }
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
    	switch(tier){
    		case LV:
    			return Textures.SOLID_STEEL_CASING;
    		case MV:
    			return Textures.FROST_PROOF_CASING;
    		case HV:
    			return Textures.CLEAN_STAINLESS_STEEL_CASING;
    		case EV:
    			return Textures.STABLE_TITANIUM_CASING;
    		default:
    			return Textures.ROBUST_TUNGSTENSTEEL_CASING;
    	}
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
