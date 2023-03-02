package gthrt.common.block;

import net.minecraft.item.Item;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraft.block.properties.IProperty;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraft.item.ItemBlock;

import gthrt.common.block.BlockPackerCasing;
import gthrt.client.ClientHandler;
import gthrt.GTHRTMod;

import gregtech.common.blocks.MetaBlocks;

import java.util.Map;
import java.util.List;
import java.lang.Comparable;
import java.util.stream.Collectors;
import java.util.Map.Entry;
import java.util.Comparator;
import java.util.function.Function;

public class HRTBlocks{
	public static BlockPackerCasing PACKER_CASING;

	public static void preInit() {
		PACKER_CASING = new BlockPackerCasing();
		PACKER_CASING.setRegistryName("packer_casing");
	}
	public static void init(RegistryEvent.Register<Block> event){
		GTHRTMod.logger.info("Registering HRTBlocks");
		IForgeRegistry<Block> registry = event.getRegistry();
		registry.register(PACKER_CASING);
	}

	public static void registerBlockItems(RegistryEvent.Register<Item> event){
		IForgeRegistry<Item> registry = event.getRegistry();
    	registry.register(createItemBlock(PACKER_CASING, gregtech.api.block.VariantItemBlock::new));
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModels() {
		PACKER_CASING.onModelRegister();
	}

  	@SideOnly(Side.CLIENT)
  	public static void registerColors(){
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(ClientHandler.PACKER_CASING_BLOCK_COLOR,PACKER_CASING);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ClientHandler.PACKER_CASING_ITEM_COLOR,PACKER_CASING);
	}
	private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
    	ItemBlock itemBlock = producer.apply(block);
    	itemBlock.setRegistryName(block.getRegistryName());
    	return itemBlock;
 	}
}
