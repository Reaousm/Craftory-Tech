package tech.brettsaunders.craftory.tech.power.api.block;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IEnergyStorage;

/**
 * Implementation of {@link IEnergyStorage}
 */
public class EnergyStorage implements IEnergyStorage, Externalizable {

  private static transient final long serialVersionUID = -1692723296529286331L;

  protected int energy;
  protected int capacity;
  protected int maxReceive;
  protected int maxExtract;

  public EnergyStorage(int capacity) {

    this(capacity, capacity, capacity);
  }

  public EnergyStorage(int capacity, int maxTransfer) {

    this(capacity, maxTransfer, maxTransfer);
  }

  public EnergyStorage(int capacity, int maxReceive, int maxExtract) {

    this.capacity = capacity;
    this.maxReceive = maxReceive;
    this.maxExtract = maxExtract;
  }

  /* Saving and Loading */
  public EnergyStorage(){

  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(energy);
    out.writeInt(capacity);
    out.writeInt(maxReceive);
    out.writeInt(maxExtract);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    energy = in.readInt();
    capacity = in.readInt();
    maxReceive = in.readInt();
    maxExtract = in.readInt();
  }

  /* Common Methods */
  public EnergyStorage readFromNBT(NBTCompound nbt) {

    this.energy = nbt.getInteger("Energy");

    if (energy > capacity) {
      energy = capacity;
    }
    return this;
  }

  public NBTCompound writeToNBT(NBTCompound nbt) {

    if (energy < 0) {
      energy = 0;
    }
    nbt.setInteger("Energy", energy);
    return nbt;
  }

  public EnergyStorage setCapacity(int capacity) {

    this.capacity = capacity;

    if (energy > capacity) {
      energy = capacity;
    }
    return this;
  }

  public EnergyStorage setMaxTransfer(int maxTransfer) {

    setMaxReceive(maxTransfer);
    setMaxExtract(maxTransfer);
    return this;
  }

  public EnergyStorage setMaxReceive(int maxReceive) {

    this.maxReceive = maxReceive;
    return this;
  }

  public EnergyStorage setMaxExtract(int maxExtract) {

    this.maxExtract = maxExtract;
    return this;
  }

  public int getMaxReceive() {

    return maxReceive;
  }

  public int getMaxExtract() {

    return maxExtract;
  }

  public void setEnergyStored(int energy) {

    this.energy = energy;

    if (this.energy > capacity) {
      this.energy = capacity;
    } else if (this.energy < 0) {
      this.energy = 0;
    }
  }

  public void modifyEnergyStored(int energy) {

    this.energy += energy;

    if (this.energy > capacity) {
      this.energy = capacity;
    } else if (this.energy < 0) {
      this.energy = 0;
    }
  }

  /* IEnergyStorage */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {

    int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

    if (!simulate) {
      energy += energyReceived;
    }
    return energyReceived;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {

    int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

    if (!simulate) {
      energy -= energyExtracted;
    }
    return energyExtracted;
  }

  @Override
  public int getEnergyStored() {

    return energy;
  }

  @Override
  public int getMaxEnergyStored() {

    return capacity;
  }
}
