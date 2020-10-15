package jake.kitchenmod.village;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;

public class KMTrade implements ITrade {

  private ItemStack cost1;
  private ItemStack cost2;
  private ItemStack result;
  private int maxUses;
  private int xpGain;
  private float priceMult;

  public KMTrade(Item cost, int cAmt, Item cost2, int c2Amt, Item result, int rAmt, int mu,
      int xp) {
    this.cost1 = new ItemStack(cost, cAmt);
    this.cost2 = cost2 == null ? null : new ItemStack(cost2, c2Amt);
    this.result = new ItemStack(result, rAmt);

    this.maxUses = mu;
    this.xpGain = xp;
    this.priceMult = 1f;
  }

  public KMTrade(Item cost, int cAmt, Item result, int rAmt, int mu, int xp) {
    this(cost, cAmt, null, 0, result, rAmt, mu, xp);
  }

  public KMTrade(int emeraldCost, Item result, int rAmt, int mu, int xp) {
    this(Items.EMERALD, emeraldCost, result, rAmt, mu, xp);
  }

  public KMTrade(Item cost, int cAmt, int emeraldsGiven, int mu, int xp) {
    this(cost, cAmt, Items.EMERALD, emeraldsGiven, mu, xp);
  }


  public KMTrade setPriceMult(float priceMult) {
    this.priceMult = priceMult;
    return this;
  }

  @SuppressWarnings("NullableProblems")
  @Nullable
  @Override
  public MerchantOffer getOffer(Entity merchant, Random randGen) {
    return cost2 == null ? new MerchantOffer(cost1, result, maxUses, xpGain, priceMult)
        : new MerchantOffer(cost1, cost2, result, maxUses, xpGain, priceMult);
  }

  @Override
  public String toString() {
    if (cost2 == null) {
      return String.format("[%s] -> [%s] {Max Uses: %d, XP: %d, Price Mult.: %f}",
          cost1.toString(), result.toString(), maxUses, xpGain, priceMult);
    } else {
      return String.format("([%s], [%s]) -> [%s] {Max Uses: %d, XP: %d, Price Mult.: %f}",
          cost1.toString(), cost2.toString(), result.toString(), maxUses, xpGain, priceMult);
    }
  }
}
