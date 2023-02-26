package gthrt.common.port;

import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import static gregtech.api.gui.GuiTextures.BACKGROUND;
import static gregtech.api.gui.GuiTextures.DISPLAY;

import net.minecraftforge.fluids.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import gthrt.common.market.MarketHandler;
import gthrt.client.widget.SwitchableMarketListWidget;
import gthrt.GTHRTMod;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public abstract class MetaTileEntityPortControllerAbstract extends RecipeMapMultiblockController {
    public boolean fuelSetting;

    public List<String> availableMarkets;
    public List<String> addedMarkets;

    public abstract Fluid getFuel();//fuel to use for transport; return null if using solid fuel
    public abstract int getFuelEfficiency();//fuel amount per item or burn time per if using solid fuel; should probs use a rounded float in the future
	protected abstract int getFreightInternal();//coin value per item for freight
    public abstract int getSpeed();//time in ticks per recipe
	public abstract int getCap();//absolute maximum capacity of the port


	public MetaTileEntityPortControllerAbstract(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, null);
		this.recipeMapWorkable = new PortLogic(this);
		availableMarkets = new ArrayList(MarketHandler.buyMarkets.keySet());
		addedMarkets = new ArrayList();
		fuelSetting = true;
	}

	public int getFreight(){
		if(fuelSetting){
			return 0;
		}
		return getFreightInternal();
	}

	//Remove all the calls to recipeMap since it's null
	@Override
	public SoundEvent getSound(){
		return null;
	}
	@Override
	public boolean hasMaintenanceMechanics() {
    	return false;
  	}

  	@Override
  	protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
  		SwitchableMarketListWidget switchable = new SwitchableMarketListWidget(0,0, this);
        return ModularUI.builder(BACKGROUND,220,229)
        		.widget(new LabelWidget(110,3,getMetaFullName()).setXCentered(true))
        		.widget(switchable)
        		.image(3,136,214,90,DISPLAY)
        		.widget(new AdvancedTextWidget(7,141,this::addDisplayText,0xFFFFFFFF).setMaxWidthLimit(208).setClickHandler(this::handleDisplayClick));


    }
    @Override
    protected void handleDisplayClick(String componentData, Widget.ClickData clickData) {
    	if(componentData.equals("fuelSetting")){
    		this.fuelSetting = !fuelSetting;
			markDirty();
			return;
    	}
    	super.handleDisplayClick(componentData,clickData);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList){
    	super.addDisplayText(textList);
    	ITextComponent button = AdvancedTextWidget.withButton(new TextComponentTranslation(fuelSetting ? "label.fuel_setting.yes" : "label.fuel_setting.no"),"fuelSetting");
    	ITextComponent tooltip = new TextComponentTranslation( fuelSetting ? "tooltip.fuel_setting.yes" : "tooltip.fuel_setting.no",
    														   fuelSetting ? getFuelEfficiency() : getFreight(),
    														   fuelSetting ? getFuel()!=null ? I18n.format(getFuel().getUnlocalizedName()): I18n.format("tooltip.burn_time") : null);
    	button.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,tooltip));
    	textList.add(button);
    	textList.add(new TextComponentTranslation("label.shipping_cap", getCap()));
    	textList.add(new TextComponentTranslation("label.shipping_speed", getSpeed()/20));
    }

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		NBTTagCompound markets = new NBTTagCompound();
		for(String s : addedMarkets){
			markets.setBoolean(s,true);
		}
		data.setTag("addedMarkets",markets);
		data.setBoolean("fuelSetting",fuelSetting);
		return super.writeToNBT(data);
	}
	@Override
	public boolean canBeDistinct() {
		return true;
	}


	@Override
	public void readFromNBT(NBTTagCompound data) {
		fuelSetting = data.getBoolean("fuelSetting");
		Set<String> markets = data.getCompoundTag("addedMarkets").getKeySet();
		for(String s : markets){
			addedMarkets.add(s);
			availableMarkets.remove(s);
		}
		super.readFromNBT(data);
	}
	@Override
	public void writeInitialSyncData(PacketBuffer buf) {
    	super.writeInitialSyncData(buf);
    	buf.writeBoolean(fuelSetting);
    	buf.writeVarInt(addedMarkets.size());
    	for(String s : addedMarkets){
    		buf.writeString(s);
    	}
  	}
	@Override
	public void receiveInitialSyncData(PacketBuffer buf) {
   		super.receiveInitialSyncData(buf);
    	fuelSetting = buf.readBoolean();
    	int count = buf.readVarInt();
    	for(int i =0;i<count;i++){
    		String s = buf.readString(32767);
			addedMarkets.add(s);
			availableMarkets.remove(s);

    	}
	}
	protected static TraceabilityPredicate energyHatches(int tierMin,int tierMax){
		return metaTileEntities(MultiblockAbility.REGISTRY.get(MultiblockAbility.INPUT_ENERGY).stream().filter(w -> ((ITieredMetaTileEntity)w).getTier() >=tierMin && ((ITieredMetaTileEntity)w).getTier() <= tierMax).toArray(MetaTileEntity[]::new));
	}

}

