package gthrt.common;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;

import gthrt.common.items.chains.PersonalHygieneChain;
//24000-24500 mats

public class HRTMats{

	public static void handleChains(){
		PersonalHygieneChain.handleMaterial();
	}
}
