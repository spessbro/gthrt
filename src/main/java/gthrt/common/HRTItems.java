package gthrt.common;


import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.StandardMetaItem;
import gthrt.common.plugin.ValueGraphPluginBehavior;
import gregtech.common.items.MetaItems;


import java.util.List;

public class HRTItems{

	private HRTItems(){}
	public static MetaItem<?>.MetaValueItem PLUGIN_GRAPH;

    private static StandardMetaItem HRT_ITEMS;


    public static void preInit() {
        HRT_ITEMS = new StandardMetaItem();
        HRT_ITEMS.setRegistryName("hrt_meta_item");
    }
    public static void init(){
    	PLUGIN_GRAPH = HRT_ITEMS.addItem(0,"plugin.graph").addComponents(new ValueGraphPluginBehavior());
		MetaItems.COIN_DOGE.addComponents(new ValueGraphPluginBehavior());
    }


}
