package tech.brettsaunders.craftory.tech.power.core.block.cell;

import org.bukkit.Location;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.tech.power.api.block.BaseCell;

/**
 * Energy Cell
 * <p>
 * Capacity: 20,000,000 Max Input: 8000 Max Output: 8000 Level: 2 (DIAMOND)
 */
public class DiamondCell extends BaseCell {

  /* Static Constants Private */
  private static final long serialVersionUID = 10012L;
  private static final byte C_LEVEL = 2;
  private static final int C_OUTPUT_AMOUNT = 8000;

  /* Construction */
  public DiamondCell(Location location) {
    super(location, Blocks.DIAMOND_CELL, C_LEVEL, C_OUTPUT_AMOUNT);
  }

  /* Saving, Setup and Loading */
  public DiamondCell() {
    super();
  }

}
