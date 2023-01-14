package gthrt.common.port;

import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.ItemHandlerList;


import net.minecraftforge.fluids.Fluid;
import net.minecraft.util.ResourceLocation;


public abstract class MetaTileEntityPortControllerAbstract extends MultiblockControllerBase {


	protected IEnergyContainer energyContainer;

    protected PortLogic recipeLogic;
    public boolean fuelSetting;
    private ItemHandlerList itemImportInventories;


    public abstract Fluid getFuel();
    public abstract int getFuelEfficiency();
    public abstract int getFreight();
    public abstract int getSpeed();
	public abstract int getCap();

	public MetaTileEntityPortControllerAbstract(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
	}
	@Override
    protected void updateFormedValid() {
        this.recipeLogic.update();
	}

}

