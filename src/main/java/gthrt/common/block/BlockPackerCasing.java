package gthrt.common.block;

import gregtech.api.GTValues;
import gregtech.api.block.VariantActiveBlock;
import gregtech.client.model.SimpleStateMapper;
import gregtech.common.blocks.MetaBlocks;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.block.SoundType;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.UnmodifiableIterator;
import javax.annotation.Nonnull;

public class BlockPackerCasing extends VariantActiveBlock<BlockPackerCasing.CasingType>{
	public BlockPackerCasing(){
		super(Material.IRON);
    	setTranslationKey("packer_casing");
    	setHardness(4.0F);
    	setResistance(8.0F);
    	setSoundType(SoundType.METAL);
    	setHarvestLevel("wrench", 2);
    	setDefaultState(getState(CasingType.LV));
	}

  	public enum CasingType implements IStringSerializable {
    	LV(makeName(GTValues.VN[1])),
    	MV(makeName(GTValues.VN[2])),
    	HV(makeName(GTValues.VN[3])),
    	EV(makeName(GTValues.VN[4])),
    	IV(makeName(GTValues.VN[5])),
    	LuV(makeName(GTValues.VN[6])),
    	ZPM(makeName(GTValues.VN[7])),
    	UV(makeName(GTValues.VN[8]));

    	private final String name;

    	CasingType(String name) {
      		this.name = name;
    	}

    	@Nonnull
    	@Override
    	public String getName() {
      		return this.name;
    	}

    	private static String makeName(String voltageName) {
      		return voltageName.toLowerCase();
    	}
	}
	@Override
	protected boolean isBloomEnabled(CasingType value){
		return false;
	}
	//Helpers
	public ItemStack getItemVariant(int voltage,int count){
		return new ItemStack(this,count,voltage-1);
	}
	public IBlockState getState(int voltage){
		return super.getState(CasingType.class.getEnumConstants()[voltage-1]);
	}

}
