package tech.brettsaunders.craftory.tech.power.api.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.CoreHolder;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GTwoToOneMachine;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IHopperInteract;
import tech.brettsaunders.craftory.utils.RecipeUtils;
import tech.brettsaunders.craftory.utils.RecipeUtils.CustomMachineRecipe;

public class BaseFoundry extends BaseMachine implements IHopperInteract {

  /* Static Constants Protected */
  protected static final int[] PROCESSING_TIME_LEVEL = {400, 300, 200, 100}; //MC 200 ticks
  protected static final int[] ENERGY_CONSUMPTION_LEVEL = {20, 30, 50, 100};
  protected static final int[] CAPACITY_LEVEL = {5000, 10000, 25000, 50000};
  /* Static Constants Private */
  private static final long serialVersionUID = 10023L;
  private static final int INPUT_LOCATION1 = 12;
  private static final int INPUT_LOCATION2 = 30;
  private static final int OUTPUT_LOCATION = 25;
  private static final HashMap<BlockFace, Integer> inputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.NORTH, INPUT_LOCATION1);
      put(BlockFace.EAST, INPUT_LOCATION2);
      put(BlockFace.SOUTH, INPUT_LOCATION2);
      put(BlockFace.WEST, INPUT_LOCATION1);
      put(BlockFace.UP, INPUT_LOCATION1);
    }
  };

  private static final HashMap<BlockFace, Integer> outputFaces = new HashMap<BlockFace, Integer>() {
    {
      put(BlockFace.DOWN, OUTPUT_LOCATION);
    }
  };
  /* Per Object Variables Saved */

  /* Per Object Variables Not-Saved */

  private transient CustomMachineRecipe currentRecipe = null;


  /* Construction */
  public BaseFoundry(Location location, String blockName, byte level) {
    super(location,blockName, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);
    init();
    energyStorage = new EnergyStorage(CAPACITY_LEVEL[level]);
    inputSlots = new ArrayList<>();
    inputSlots.add(null);
    inputSlots.add(null);
    outputSlots = new ArrayList<>();
    outputSlots.add(null);
    inputLocations.add(INPUT_LOCATION1);
    inputLocations.add(INPUT_LOCATION2);
    outputLocations.add(OUTPUT_LOCATION);
    setupGUI();
  }

  /* Saving, Setup and Loading */
  public BaseFoundry() {
    super();
    init();
  }

  /* Common Load and Construction */
  public void init() {
    processTime = PROCESSING_TIME_LEVEL[level];
    energyConsumption = ENERGY_CONSUMPTION_LEVEL[level];
    interactableSlots = new HashSet<>(
        Arrays.asList(INPUT_LOCATION1, INPUT_LOCATION2, OUTPUT_LOCATION));
  }

  @Override
  public void setupGUI() {
    Inventory inventory = setInterfaceTitle("Foundry", Font.FOUNDRY_GUI.label + "");
    addGUIComponent(
        new GTwoToOneMachine(inventory, 23, progressContainer, INPUT_LOCATION1, INPUT_LOCATION2,
            OUTPUT_LOCATION));
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GIndicator(inventory, runningContainer, 21));
    inventory.setItem(INPUT_LOCATION1, inputSlots.get(0));
    inventory.setItem(INPUT_LOCATION2, inputSlots.get(1));
    inventory.setItem(OUTPUT_LOCATION, outputSlots.get(0));
    this.inventoryInterface = inventory;
  }


  @Override
  protected void processComplete() {
    inputSlots.get(0).setAmount(inputSlots.get(0).getAmount() - 1);
    inputSlots.get(1).setAmount(inputSlots.get(1).getAmount() - 1);
    if (outputSlots.get(0) == null) {
      outputSlots.set(0, CustomItemManager.getCustomItem(CoreHolder.Items.STEEL_INGOT));
    } else {
      outputSlots.get(0).setAmount(outputSlots.get(0).getAmount() + 1);
    }
    inventoryInterface.setItem(OUTPUT_LOCATION, outputSlots.get(0));
  }

  /* Internal Helper Functions */
  @Override
  protected void updateSlots() {
    inputSlots.set(0, inventoryInterface.getItem(INPUT_LOCATION1));
    inputSlots.set(1, inventoryInterface.getItem(INPUT_LOCATION2));
    outputSlots.set(0, inventoryInterface.getItem(OUTPUT_LOCATION));
  }


  @Override
  protected boolean validateContentes() {
    if (inputSlots.get(0) == null || inputSlots.get(1) == null) {
      return false;
    }
    String inputType1 = CustomItemManager.getCustomItemName(inputSlots.get(0));
    String inputType2 = CustomItemManager.getCustomItemName(inputSlots.get(1));
    int inputAmount1 = inputSlots.get(0).getAmount();
    int inputAmount2 = inputSlots.get(1).getAmount();
    String outputType = null;
    if (outputSlots.get(0) != null) {
      outputType = CustomItemManager.getCustomItemName(outputSlots.get(0));
    }
    //If the recipe is unchanged there is no need to find the recipe.

    if (currentRecipe != null) {
      boolean valid = true;
      for (Map.Entry<String, Integer> entry : currentRecipe.getIngredients().entrySet()) {
        String item = entry.getKey();
        int number = entry.getValue();
        if (!((item.equals(inputType1) && inputAmount1 >= number) || (item.equals(inputType2)
            && inputAmount2 >= number))) {
          valid = false;
          break;
        }
      }
      if (valid && outputSlots.get(0) != null) {
        if (currentRecipe.getProducts().containsKey(outputType)
            && (outputSlots.get(0).getAmount() + currentRecipe.getProducts().get(outputType))
            <= outputSlots.get(0).getMaxStackSize()) {
          return true;
        }
      }
    }
    for (CustomMachineRecipe recipe : RecipeUtils.getTwoToOneRecipes()) {
      boolean valid = true;
      for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
        String item = entry.getKey();
        int number = entry.getValue();
        if (!((item.equals(inputType1) && inputAmount1 >= number) || (item.equals(inputType2)
            && inputAmount2 >= number))) {
          valid = false;
          break;
        }
      }
      if (valid) {
        currentRecipe = recipe;
        if (outputSlots.get(0) == null || (currentRecipe.getProducts().containsKey(outputType)
            && (outputSlots.get(0).getAmount() + currentRecipe.getProducts().get(outputType))
            <= outputSlots.get(0).getMaxStackSize())) {
          return true;
        }
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
