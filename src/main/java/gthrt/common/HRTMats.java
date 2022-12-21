package gthrt.common;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;

//24000-24500 mats

public class HRTMats{
	public static final Material Gamer = new Material.Builder(24000, "sodium_fluorophosphate")
            											.dust()
            											.components(Sodium, 2,Phosphorus, 1, Fluorine, 1, Oxygen, 3)
            											.build();

}
