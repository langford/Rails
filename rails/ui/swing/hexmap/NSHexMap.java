/* $Header: /Users/blentz/rails_rcs/cvs/18xx/rails/ui/swing/hexmap/NSHexMap.java,v 1.9 2009/11/02 23:30:36 evos Exp $*/
package rails.ui.swing.hexmap;

import java.awt.Dimension;
import java.util.ArrayList;

import rails.game.MapHex;
import rails.ui.swing.Scale;

/**
 * Class NSHexMap displays a basic hex map with NS exit orientation.
 */

public class NSHexMap extends HexMap {
    private static final long serialVersionUID = 1L;

    public NSHexMap() {
        cx = 0;
        cy = -scale / 2;
    }

    @Override
	protected void setupHexesGUI() {
        hexes = new ArrayList<GUIHex>();

        scale = Scale.get();

        hexArray = mapManager.getHexes();
        MapHex mh;
        h = new GUIHex[hexArray.length][hexArray[0].length];
        for (int i = 0; i < hexArray.length; i++) {
            for (int j = 0; j < hexArray[0].length; j++) {
                mh = hexArray[i][j];
                if (mh != null) {
                    GUIHex hex =
                            new GUIHex(this, Math.round(cx + 3 * i
                                                              * scale),
                                    (int) Math.round(cy + (2 * j + (i & 1))
                                                     * GUIHex.SQRT3 * scale),
                                    scale, i, j);

                    hex.setHexModel(mh);
                    hex.originalTileId = hex.currentTileId;
                    hexesByName.put(mh.getName(), hex);

                    h[i][j] = hex;
                    hexes.add(hex);
                }
            }
        }
        preferredSize =
                new Dimension((hexArray.length + 1) * 3 * scale,
                        (int) Math.round((hexArray[0].length + 1) * 2
                                         * GUIHex.SQRT3 * scale));
    }
}