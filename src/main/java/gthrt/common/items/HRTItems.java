package gthrt.common;


import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.common.items.MetaItems;


import gthrt.common.plugin.ValueGraphPluginBehavior;
import gthrt.common.items.MarketValueComponent;
import gthrt.common.items.PackageItem;

import gthrt.common.items.chains.PersonalHygieneChain;

import java.util.List;

public class HRTItems{

	private HRTItems(){}
	public static MetaItem<?>.MetaValueItem PLUGIN_GRAPH;

    public static StandardMetaItem HRT_ITEMS;

    public static PackageItem HRT_PACKAGES;

    public static void preInit() {
        HRT_ITEMS = new StandardMetaItem();
        HRT_ITEMS.setRegistryName("hrt_meta_item");
        HRT_PACKAGES = new PackageItem();
        HRT_PACKAGES.setRegistryName("hrt_package");
    }
    public static void init(){
    	//graph
    	PLUGIN_GRAPH = HRT_ITEMS.addItem(0,"plugin.graph").addComponents(new ValueGraphPluginBehavior());


    	//explosives
		MetaItems.DYNAMITE.addComponents(new MarketValueComponent("explosives",0.05f));

		PersonalHygieneChain.registerItems();

    }


}
