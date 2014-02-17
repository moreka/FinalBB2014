package javachallenge.util;

import javachallenge.units.Unit;

/**
 * Created by peyman on 2/9/14.
 */
public class MineCell extends Cell {

    private int amount;
    private Unit secUnit;
    private Unit thirdUnit;



    public MineCell(int x, int y) {
        super(x, y, CellType.MINE);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Unit getSecUnit() {
        return secUnit;
    }

    public void setSecUnit(Unit secUnit) {
        this.secUnit = secUnit;
    }

    public Unit getThirdUnit() {
        return thirdUnit;
    }

    public void setThirdUnit(Unit thirdUnit) {
        this.thirdUnit = thirdUnit;
    }
}
