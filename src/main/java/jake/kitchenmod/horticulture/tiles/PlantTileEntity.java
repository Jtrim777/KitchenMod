package jake.kitchenmod.horticulture.tiles;

import jake.kitchenmod.horticulture.PlantGene;
import jake.kitchenmod.tiles.ModTileEntities;
import net.minecraft.tileentity.TileEntity;

public class PlantTileEntity extends TileEntity {

  private PlantGene gene;

  public PlantTileEntity() {
    super(ModTileEntities.PLANT_TILE);
  }

  public PlantGene getGene() {
    return gene;
  }

  public void setGene(PlantGene gene) {
    this.gene = gene;
  }
}
