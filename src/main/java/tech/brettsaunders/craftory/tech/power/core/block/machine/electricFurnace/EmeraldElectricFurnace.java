package tech.brettsaunders.craftory.tech.power.core.block.machine.electricFurnace;

import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.block.BaseElectricFurnace;

public class EmeraldElectricFurnace extends BaseElectricFurnace {

  private static final byte CLEVEL = 2;

  public EmeraldElectricFurnace(Location location){
    super(location, CLEVEL);
  }

  public EmeraldElectricFurnace() {
    super();
  }

}
