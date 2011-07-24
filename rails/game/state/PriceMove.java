/* $Header: /Users/blentz/rails_rcs/cvs/18xx/rails/game/move/PriceMove.java,v 1.5 2008/06/04 19:00:33 evos Exp $
 * 
 * Created on 18-Jul-2006
 * Change Log:
 */
package rails.game.state;

import rails.game.StockSpaceI;
import rails.game.model.PriceModel;

/**
 * @author Erik Vos
 */
@Deprecated
public class PriceMove implements Move {

    protected StockSpaceI from, to;
    protected PriceModel price;

    public PriceMove(PriceModel price, StockSpaceI from, StockSpaceI to) {
        this.from = from;
        this.to = to;
        this.price = price;
//        MoveSet.add(this);
    }

    public boolean execute() {
        price.setState(to);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see rails.rails.game.action.Move#undo()
     */
    public boolean undo() {
        price.setState(from);
        return true;
    }

    public Object getObject() {
        return price;
    }

    public String toString() {
        return "PriceMove: " + price.getName() + " from " + from + " to " + to;
    }

}