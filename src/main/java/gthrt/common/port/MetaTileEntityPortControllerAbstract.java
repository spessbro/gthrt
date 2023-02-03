package gthrt.common.port;

import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;



import net.minecraftforge.fluids.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;


public abstract class MetaTileEntityPortControllerAbstract extends RecipeMapMultiblockController {
    public boolean fuelSetting = true;
    private ItemHandlerList itemImportInventories;


    public abstract Fluid getFuel();//fuel to use for transport; return null if using solid fuel
    public abstract int getFuelEfficiency();//fuel amount per item or burn time per if using solid fuel; should probs use a rounded float in the future
    public abstract int getFreight();//coin value per item for freight
    public abstract int getSpeed();//time in ticks per recipe
	public abstract int getCap();//absolute maximum capacity of the port

	public MetaTileEntityPortControllerAbstract(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, null);
		this.recipeMapWorkable = new PortLogic(this);
		toggleDistinct();//always distinct buses for the recipe logic to work correctly
	}



	//Remove all the calls to recipeMap since it's null
	@Override
	public SoundEvent getSound(){
		return null;
	}

	@Override //for helping building pattern; do later/handle on the multiblock subtypes
	public TraceabilityPredicate autoAbilities(){
		TraceabilityPredicate out = new TraceabilityPredicate();
		out = out.or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1));
		out = out.or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1));
		out = out.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(1));
		out = out.or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1));
		return out;
	}
	TraceabilityPredicate autoAbilities(boolean... in){
		return null;
	}
	@Override
	public boolean hasMaintenanceMechanics() {
    	return false;
  	}



}

