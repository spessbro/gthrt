package gthrt.common.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;

import gregtech.api.items.metaitem.StandardMetaItem;

import gthrt.common.market.MarketHandler;
import gthrt.common.market.MarketBase;
import gthrt.GTHRTMod;

import javax.annotation.Nonnull;

public class PackageItem extends StandardMetaItem{

	@Override
    public void registerSubItems() {
    	int i =0;
        for (MarketBase m : MarketHandler.marketTypes.values()) {
        	addItem(i, String.format("%s_package",m.name)).addComponents(new MarketValueComponent(m.name,1f));;
        	i++;
        }
    }

	@Override
    @SideOnly(Side.CLIENT)
    protected int getColorForItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            MarketBase m = MarketHandler.marketTypes.get( ((MarketValueComponent)getBehaviours(stack).get(0)).marketName);
            if (m == null)
                return 0xFFFFFF;
            return m.color | 0xFF000000;
        }
        return super.getColorForItemStack(stack, tintIndex);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("ConstantConditions")
    public void registerModels() {
    	ResourceLocation rL = new ResourceLocation(GTHRTMod.MODID,"packages/box");
		ModelBakery.registerItemVariants(this,rL);
		for(short i : metaItems.keySet()){
			metaItemsModels.put(i,new ModelResourceLocation(rL, "inventory"));
		}
    }
    @Nonnull
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
    	return I18n.format("market.names."+((MarketValueComponent)getBehaviours(stack).get(0)).marketName)+" "+I18n.format("package.name").toLowerCase();
    }

}
