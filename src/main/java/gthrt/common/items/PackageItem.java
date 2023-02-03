package gthrt.common.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;

import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.Material;
import gregtech.common.metatileentities.storage.MetaTileEntityCrate;
import gregtech.common.metatileentities.MetaTileEntities;
import static gregtech.api.recipes.RecipeMaps.PACKER_RECIPES;
import static gregtech.api.GTValues.*;

import gthrt.common.market.MarketHandler;
import gthrt.common.market.MarketBase;
import gthrt.GTHRTMod;

import javax.annotation.Nonnull;

public class PackageItem extends StandardMetaItem{
	private static final Material[] materialColors={Materials.Steel,
													Materials.Aluminium,
													Materials.StainlessSteel,
													Materials.Titanium,
													Materials.TungstenSteel};
	private static final MetaTileEntityCrate[] crates ={MetaTileEntities.STEEL_CRATE,
														MetaTileEntities.ALUMINIUM_CRATE,
														MetaTileEntities.STAINLESS_STEEL_CRATE,
														MetaTileEntities.TITANIUM_CRATE,
														MetaTileEntities.TUNGSTENSTEEL_CRATE}; //TODO: make this configurable with generated fake crates

	@Override
    public void registerSubItems() {
    	int i =0;
        for (String m : MarketHandler.sellMarkets) {
        	addItem(i, String.format("%s_package",m)).addComponents(new MarketValueComponent(m,1f));
        	for(int x = 0;x<materialColors.length;x++){
        		addItem(i+1+x, String.format("%s_crate_%s",m,materialColors[x])).addComponents(new MarketValueComponent(m,(float)Math.pow(6,x+1)));
        	}
        	i+=materialColors.length+1;
        }
    }

	@Override
    @SideOnly(Side.CLIENT)
    protected int getColorForItemStack(ItemStack stack, int tintIndex) {
        MarketBase m = MarketHandler.marketTypes.get( ((MarketValueComponent)getBehaviours(stack).get(0)).marketName);
        int i = stack.getItemDamage();
    	if (m == null)
                return super.getColorForItemStack(stack, tintIndex);
    	switch(tintIndex){
    		case 0:
    			if(i%(materialColors.length+1)>0){return materialColors[i%(materialColors.length+1)-1].getMaterialRGB() | 0x88000000;}
    			break;
    		case 1:
            	return m.color | 0x88000000;
    	}

        return super.getColorForItemStack(stack, tintIndex);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("ConstantConditions")
    public void registerModels() {
    	ResourceLocation box = new ResourceLocation(GTHRTMod.MODID,"packages/box");
    	ResourceLocation crate = new ResourceLocation(GTHRTMod.MODID,"packages/crate");
		ModelBakery.registerItemVariants(this,box);
		ModelBakery.registerItemVariants(this,crate);
		for(short i : metaItems.keySet()){
			if(i%(materialColors.length+1)==0){ metaItemsModels.put(i,new ModelResourceLocation(box, "inventory"));}
			else{metaItemsModels.put(i,new ModelResourceLocation(crate, "inventory"));}

		}
    }
    @Nonnull
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
    	if(stack.getItemDamage()%(materialColors.length+1)==0){
    		return I18n.format("market.names."+((MarketValueComponent)getBehaviours(stack).get(0)).marketName)+" "+I18n.format("package.name");
    	}
    	else{
    		return materialColors[(stack.getItemDamage()%(materialColors.length+1))-1].getLocalizedName()+" "+I18n.format("market.names."+((MarketValueComponent)getBehaviours(stack).get(0)).marketName)+" "+I18n.format("crate.name");
    	}
    }


    public void generateRecipes(){
    	GTHRTMod.logger.info("Generating crate recipes");
    	int z = 1;
    	for(String m : MarketHandler.sellMarkets){
    		for(int i=0;i<materialColors.length;i++){
				PACKER_RECIPES.recipeBuilder()
						.input(this,4,z+i-1)//package or lower crate
						.input(crates[i])//empty crate
						.output(this,1,z+i)//new package
						.duration(80).EUt(VA[ULV]).buildAndRegister();
    			if(i!=0){//For Above Steel crates;lets you skip a tier
					PACKER_RECIPES.recipeBuilder()
						.input(this,25,z+i-2)//package
						.input(crates[i])//empty crate
						.output(this,1,z+i)//new package
						.duration(400).EUt(VA[LV]).buildAndRegister();

    			}

    		}
    	}
    	z+=materialColors.length+1;
    }



}
