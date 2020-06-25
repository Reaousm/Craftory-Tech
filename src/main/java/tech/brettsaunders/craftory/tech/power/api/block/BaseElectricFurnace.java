package tech.brettsaunders.craftory.tech.power.api.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOneToOneMachine;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;
import tech.brettsaunders.craftory.utils.RecipeUtils;
import tech.brettsaunders.craftory.utils.VariableContainer;

public class BaseElectricFurnace extends BaseMachine implements IHopperInteract {

  /* Static Constants Protected */
  protected static final int[] COOKING_TIME_LEVEL = {200, 150, 100, 50}; //MC 200 ticks
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20, 30, 50, 100};
  protected static final int[] CAPACITY_LEVEL = {5000, 10000, 25000, 50000};
  /* Static Constants Private */
  private static final int INPUT_LOCATION = 21;
  private static final int OUTPUT_LOCATION = 25;
  private static final HashMap<BlockFace, Integer> inputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.NORTH, INPUT_LOCATION);
      put(BlockFace.EAST, INPUT_LOCATION);
      put(BlockFace.SOUTH, INPUT_LOCATION);
      put(BlockFace.WEST, INPUT_LOCATION);
      put(BlockFace.UP, INPUT_LOCATION);
    }
  };

  private static final HashMap<BlockFace, Integer> outputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.DOWN, OUTPUT_LOCATION);
    }
  };

  /* Per Object Variables Saved */

  /* Per Object Variables Not-Saved */

  private transient FurnaceRecipe currentRecipe = null;


  /* Construction */
  public BaseElectricFurnace(Location location, String blockName, byte level) {
    super(location, blockName, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);
    init();
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    inputSlots = new ArrayList<>();
    inputSlots.add(null);
    outputSlots = new ArrayList<>();
    outputSlots.add(null);
    inputLocations.add(INPUT_LOCATION);
    outputLocations.add(OUTPUT_LOCATION);
    setupGUI();
  }

  /* Saving, Setup and Loading */
  public BaseElectricFurnace() {
    super();
    init();
  }

  /* Common Load and Construction */
  public void init() {
    processTime = COOKING_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
    runningContainer = new VariableContainer<>(false);
    progressContainer = new VariableContainer<>(0d);
    interactableSlots = new HashSet<>(Arrays.asList(INPUT_LOCATION, OUTPUT_LOCATION));
  }

  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle("Electric Furnace",
        Font.FURNACE_GUI.label + ""); //TODO Furnance
    addGUIComponent(
        new GOneToOneMachine(inventory, 23, progressContainer, INPUT_LOCATION, OUTPUT_LOCATION));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 30));
    inventory.setItem(INPUT_LOCATION, inputSlots.get(0));
    inventory.setItem(OUTPUT_LOCATION, outputSlots.get(0));
    this.inventoryInterface = inventory;
  }


  @Override
  protected void processComplete() {
    inputSlots.get(0).setAmount(inputSlots.get(0).getAmount() - 1);
    if (outputSlots.get(0) == null) {
      outputSlots.set(0, currentRecipe.getResult());
    } else {
      outputSlots.get(0).setAmount(outputSlots.get(0).getAmount() + currentRecipe.getResult().getAmount());
    }
    inventoryInterface.setItem(OUTPUT_LOCATION, outputSlots.get(0));
  }

  /* Internal Helper Functions */
  @Override
  protected void updateSlots() {
    inputSlots.set(0, inventoryInterface.getItem(INPUT_LOCATION));
    outputSlots.set(0, inventoryInterface.getItem(OUTPUT_LOCATION));
  }


  @Override
  protected boolean validateContentes() {
    if (inputSlots.get(0) == null) {
      return false;
    }
    String inputType = CustomItemManager.getCustomItemName(inputSlots.get(0));
    //If the recipe is unchanged there is no need to find the recipe.
    if (currentRecipe != null && currentRecipe.getInput().getType().toString().equals(inputType)) {
      if (outputSlots.get(0) == null) {
        return true;
      }
      if (outputSlots.get(0).getType().toString().equals(currentRecipe.getResult().getType().toString())
          && outputSlots.get(0).getAmount() < outputSlots.get(0).getMaxStackSize()) {
        return true;
      }
    }
    FurnaceRecipe furnaceRecipe;
    for (Recipe recipe : RecipeUtils.getFurnaceRecipes()) {
      furnaceRecipe = (FurnaceRecipe) recipe;
      if (!furnaceRecipe.getInput().getType().toString().equals(inputType)) {
        continue;
      }
      currentRecipe = furnaceRecipe;
      if (outputSlots.get(0) == null) {
        return true;
      }
      if (CustomItemManager.getCustomItemName(outputSlots.get(0))
          .equals(recipe.getResult().getType().toString())
          && outputSlots.get(0).getAmount() < outputSlots.get(0).getMaxStackSize()) {
        return true;
      }
    }
    currentRecipe = null;
    return false;
  }

  @Override
  public HashMap<BlockFace, Integer> getInputFaces() {
    return inputFaces;
  }

  @Override
  public HashMap<BlockFace, Integer> getOutputFaces() {
    return outputFaces;
  }

}
