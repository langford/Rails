package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import game.*;
import ui.hexmap.*;

public class UpgradesPanel extends Box implements MouseListener, ActionListener
{

	private ArrayList upgrades;
	private JPanel upgradePanel;
	private Dimension preferredSize = new Dimension(75, 200);
	private Border border = new EtchedBorder();
	private JButton cancel;
	private JButton done;
	
	private String cancelButtonKey = "NoTile";
	private String doneButtonKey = "LayTile";
	private boolean doneEnabled = false;

	private boolean tileMode = false;
	private boolean tokenMode = false;

	private boolean lastEnabled = false;

	public UpgradesPanel()
	{
		super(BoxLayout.Y_AXIS);

		setSize(preferredSize);
		setVisible(true);

		upgrades = null;
		upgradePanel = new JPanel();

		//GameUILoader.orWindow.setMessage("SelectAHexForTile");

		upgradePanel.setOpaque(true);
		upgradePanel.setBackground(Color.DARK_GRAY);
		upgradePanel.setBorder(border);
		add(upgradePanel);

		showUpgrades();
	}
	
	public void repaint()
	{
		showUpgrades();
	}

	private void showUpgrades()
	{
		upgradePanel.removeAll();

		try
		{
		upgrades = (ArrayList) GameUILoader.getMapPanel()
				.getMap()
				.getSelectedHex()
				.getCurrentTile()
				.getValidUpgrades(GameUILoader.getMapPanel()
						.getMap()
						.getSelectedHex()
						.getHexModel(),
						GameManager.getCurrentPhase());
		}
		catch(NullPointerException e)
		{
			upgrades = null;
		}

		if (tokenMode)
		{
		}
		else if (upgrades == null)
		{
		}
		else if (upgrades.size() == 0)
		{
			GameUILoader.orWindow.setMessage("NoTiles");
		}
		else
		{
			Iterator it = upgrades.iterator();

			while (it.hasNext())
			{
				TileI tile = (TileI) it.next();
				BufferedImage hexImage = getHexImage(tile.getId());
				ImageIcon hexIcon = new ImageIcon(hexImage);

				// Cheap n' Easy rescaling.
				hexIcon.setImage(hexIcon.getImage()
						.getScaledInstance((int) (hexIcon.getIconHeight() * 0.3),
								(int) (hexIcon.getIconWidth() * 0.3),
								Image.SCALE_FAST));

				JLabel hexLabel = new JLabel(hexIcon);
				hexLabel.setName(tile.getName());
				hexLabel.setText("" + tile.getId());
				hexLabel.setOpaque(true);
				hexLabel.setVisible(true);
				hexLabel.setBorder(border);
				hexLabel.addMouseListener(this);

				upgradePanel.add(hexLabel);
			}
		}

		done = new JButton(doneButtonKey);
		done.setActionCommand("Done");
		done.setMnemonic(KeyEvent.VK_D);
		done.addActionListener(this);
		done.setEnabled(doneEnabled);
		upgradePanel.add(done);

		cancel = new JButton(cancelButtonKey);
		cancel.setActionCommand("Cancel");
		cancel.setMnemonic(KeyEvent.VK_C);
		cancel.addActionListener(this);
		cancel.setEnabled(true);
		upgradePanel.add(cancel);

		lastEnabled = doneEnabled;
	}

	private BufferedImage getHexImage(int tileId)
	{
		ImageLoader il = new ImageLoader();
		return il.getTile(tileId);
	}

	public Dimension getPreferredSize()
	{
		return preferredSize;
	}

	public void setPreferredSize(Dimension preferredSize)
	{
		this.preferredSize = preferredSize;
	}

	public ArrayList getUpgrades()
	{
		return upgrades;
	}

	public void setUpgrades(ArrayList upgrades)
	{
		this.upgrades = upgrades;
	}

	public void initTileLaying(boolean tileMode)
	{
		this.tileMode = tileMode;
		setUpgrades(null);
	}

	public void initBaseTokenLaying(boolean tokenMode)
	{
		this.tokenMode = tokenMode;
		setUpgrades(null);
	}

	public void setCancelText(String text)
	{
		cancel.setText(cancelButtonKey = text);
	}

	public void setDoneText(String text)
	{
		done.setText(doneButtonKey = text);
	}

	public void setDoneEnabled(boolean enabled)
	{
		done.setEnabled(doneEnabled = enabled);
	}

	public void actionPerformed(ActionEvent e)
	{

		String command = e.getActionCommand();

		if (command.equals("Cancel"))
		{
			GameUILoader.orWindow.processCancel();
		}
		else if (command.equals("Done"))
		{
			if (GameUILoader.getMapPanel().getMap().getSelectedHex() != null)
			{
				GameUILoader.orWindow.processDone();
			}
			else
			{
				GameUILoader.orWindow.processCancel();
			}

		}
		upgrades = null;
		showUpgrades();
	}

	public void mouseClicked(MouseEvent e)
	{
		HexMap map = GameUILoader.getMapPanel().getMap();

		int id = Integer.parseInt(((JLabel) e.getSource()).getText());
		if (map.getSelectedHex().dropTile(id))
		{
			/* Lay tile */
			map.repaint(map.getSelectedHex().getBounds());
			GameUILoader.orWindow.setSubStep(ORWindow.ROTATE_OR_CONFIRM_TILE);
		}
		else
		{
			/* Tile cannot be laid in a valid orientation: refuse it */
			JOptionPane.showMessageDialog(this,
					"This tile cannot be laid in a valid orientation.");
			upgrades.remove(TileManager.get().getTile(id));
			GameUILoader.orWindow.setSubStep(ORWindow.SELECT_TILE);
			showUpgrades();
		}

	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}
}
