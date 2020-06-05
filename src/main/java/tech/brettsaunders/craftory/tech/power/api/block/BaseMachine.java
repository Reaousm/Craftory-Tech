package tech.brettsaunders.craftory.tech.power.api.block;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.bukkit.Location;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyReceiver;

public abstract class BaseMachine extends PoweredBlock implements IEnergyReceiver, Externalizable {

  /* Static Constants Protected */
  protected int amountReceive;
  /* Static Constants Private */
  private static final long serialVersionUID = 10007L;

  /* Per Object Variables Saved */


  /* Per Object Variables Not-Saved */


  /* Construction */
  public BaseMachine(Location location, byte level, int amountReceive) {
    super(location, level);
    this.amountReceive = amountReceive;
    init();
  }

  /* Saving, Setup and Loading */
  public BaseMachine() {
    super();
    init();

  }

  /* Common Load and Construction */
  private void init() {
    isReceiver = true;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
  }

  /* IEnergyReceiver */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return energyStorage.receiveEnergy(Math.min(maxReceive, amountReceive), simulate);
  }

  /* IEnergyHandler */
  @Override
  public int getEnergyStored() {
    return energyStorage.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return energyStorage.getMaxEnergyStored();
  }

  /* IEnergyConnection */
  @Override
  public boolean canConnectEnergy() {
    return true;
  }

  /* External Methods */
  public int maxReceiveEnergy() {
    return amountReceive;
  }
}
