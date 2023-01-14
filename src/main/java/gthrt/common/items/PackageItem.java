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


import gthrt.common.market.MarketHandler;
import gthrt.common.market.MarketBase;
import gthrt.GTHRTMod;

import javax.annotation.Nonnull;

public class PackageItem extends StandardMetaItem{
	private static final Material[] materialColors ={Materials.Steel,
												Materials.Aluminium,
												Materials.Titanium};

	@Override
    public void registerSubItems() {
    	int i =0;
        for (String m : MarketHandler.sellMarkets) {
        	addItem(i, String.format("%s_package",m)).addComponents(new MarketValueComponent(m,1f));
        	addItem(i+1, String.format("%s_crate_steel",m)).addComponents(new MarketValueComponent(m,4f));
        	addItem(i+2, String.format("%s_crate_aluminum",m)).addComponents(new MarketValueComponent(m,8f));
        	addItem(i+3, String.format("%s_crate_titanium",m)).addComponents(new MarketValueComponent(m,32f));
        	i+=4;
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
    			if(i%4>0){return materialColors[i%4-1].getMaterialRGB() | 0x88000000;}
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
			if(i%4<1){ metaItemsModels.put(i,new ModelResourceLocation(box, "inventory"));}
			else{metaItemsModels.put(i,new ModelResourceLocation(crate, "inventory"));}

		}
    }
    @Nonnull
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
    	if(stack.getItemDamage()%4==0){
    		return I18n.format("market.names."+((MarketValueComponent)getBehaviours(stack).get(0)).marketName)+" "+I18n.format("package.name").toLowerCase();
    	}
    	else{
    		return materialColors[stack.getItemDamage()%4-1].getLocalizedName()+" "+I18n.format("market.names."+((MarketValueComponent)getBehaviours(stack).get(0)).marketName)+" "+I18n.format("crate.name").toLowerCase();
    	}
    }

}
