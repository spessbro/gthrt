package gthrt.common.items;


import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.common.items.MetaItems;


import gthrt.common.plugin.ValueGraphPluginBehavior;
import gthrt.common.items.MarketValueComponent;
import gthrt.common.items.PackageItem;


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
    public static void init(){ ///ids 0x00-0x40
    	//graph
    	PLUGIN_GRAPH = HRT_ITEMS.addItem(0,"plugin.graph").addComponents(new ValueGraphPluginBehavior());
    }
    public static MetaItem<?>.MetaValueItem addMarketItem(int id,String name,String market,float value){
    	return HRT_ITEMS.addItem(id,name).addComponents(new MarketValueComponent(market,value));

    }

}
