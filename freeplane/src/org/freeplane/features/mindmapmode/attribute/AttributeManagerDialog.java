/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.mindmapmode.attribute;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.freeplane.core.controller.Controller;
import org.freeplane.core.frame.IMapSelectionListener;
import org.freeplane.core.model.MapModel;
import org.freeplane.core.resources.ResourceBundles;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.collection.IListModel;
import org.freeplane.features.common.attribute.AttributeRegistry;
import org.freeplane.features.mindmapmode.MModeController;

/**
 * @author Dimitry Polivaev
 */
public class AttributeManagerDialog extends JDialog implements IMapSelectionListener {
	private class ApplyAction extends AFreeplaneAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		ApplyAction() {
			super("ApplyAction", controller);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(final ActionEvent e) {
			applyChanges();
			controller.getModeController().startTransaction();
		}
	}

	private class CancelAction extends AFreeplaneAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		CancelAction() {
			super("CancelAction", controller);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(final ActionEvent e) {
			resetChanges();
			AttributeManagerDialog.this.setVisible(false);
		}
	}

	private class ClosingListener extends WindowAdapter {
		@Override
		public void windowClosing(final WindowEvent e) {
			resetChanges();
			super.windowClosing(e);
			setVisible(false);
		}
	}

	class EditListAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String labelText;
		private IListModel listBoxModel;
		private int row = 0;
		private String title;

		public EditListAction() {
			super(null, AttributeManagerDialog.editButtonImage);
		}

		public void actionPerformed(final ActionEvent e) {
			ListDialog.showDialog((Component) e.getSource(), AttributeManagerDialog.this, labelText, title,
			    listBoxModel, "xxxxxxxxxxxxxxxxxxxxx");
		}

		public int getRow() {
			return row;
		}

		public void setListBoxModel(final String title, final String labelText, final IListModel listBoxModel) {
			this.title = title;
			this.labelText = labelText;
			this.listBoxModel = listBoxModel;
		}

		public void setRow(final int row) {
			this.row = row;
		}
	}

	private class ImportAction extends AFreeplaneAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		ImportAction() {
			super("ImportAction", controller);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(final ActionEvent e) {
			if (importDialog == null) {
				importDialog = new ImportAttributesDialog(controller, AttributeManagerDialog.this);
			}
			importDialog.show();
		}
	}

	private class OKAction extends AFreeplaneAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		OKAction() {
			super("OKAction", controller);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(final ActionEvent e) {
			applyChanges();
			AttributeManagerDialog.this.setVisible(false);
		}
	}

	static final Icon editButtonImage = new ImageIcon(ResourceController.getResourceController().getResource(
	    "/images/edit12.png"));
	private static final String[] fontSizes = { "6", "8", "10", "12", "14", "16", "18", "20", "24" };
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final private Controller controller;
	private ImportAttributesDialog importDialog = null;
	private AttributeRegistry model;
	final private JComboBox size;
	final private JTable view;

	public AttributeManagerDialog(final Controller controller, final Frame frame) {
		super(frame, ResourceBundles.getText("attributes_dialog_title"), true);
		this.controller = controller;
		view = new AttributeRegistryTable(new EditListAction());
		model = AttributeRegistry.getRegistry(controller.getMap());
		view.setModel(model.getTableModel());
		view.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		view.getTableHeader().setReorderingAllowed(false);
		final JScrollPane scrollPane = new JScrollPane(view);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		final Box southButtons = Box.createHorizontalBox();
		southButtons.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(southButtons, BorderLayout.SOUTH);
		southButtons.add(Box.createHorizontalGlue());
		final JButton ok = new JButton(new OKAction());
		southButtons.add(ok);
		southButtons.add(Box.createHorizontalGlue());
		final JButton apply = new JButton(new ApplyAction());
		southButtons.add(apply);
		southButtons.add(Box.createHorizontalGlue());
		final JButton cancel = new JButton(new CancelAction());
		southButtons.add(cancel);
		southButtons.add(Box.createHorizontalGlue());
		size = new JComboBox(AttributeManagerDialog.fontSizes);
		size.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent e) {
				model.setAttributeLayoutChanged();
			}
		});
		size.setToolTipText(ResourceBundles.getText("attribute_font_size_tooltip"));
		southButtons.add(size);
		southButtons.add(Box.createHorizontalGlue());
		final JButton importBtn = new JButton(new ImportAction());
		importBtn.setToolTipText(ResourceBundles.getText("attributes_import_tooltip"));
		southButtons.add(importBtn);
		southButtons.add(Box.createHorizontalGlue());
		UITools.addEscapeActionToDialog(this);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new ClosingListener());
		controller.getMapViewManager().addMapSelectionListener(this);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(final ComponentEvent e) {
				size.setSelectedItem(Integer.toString(model.getFontSize()));
			}
		});
	}

	public void afterMapChange(final MapModel oldMap, final MapModel newMap) {
		if (newMap != null) {
			model = AttributeRegistry.getRegistry(newMap);
			if (model != null) {
				view.setModel(model.getTableModel());
			}
			else {
				setVisible(false);
			}
		}
	}

	public void afterMapClose(final MapModel pOldMapView) {
	}

	private void applyChanges() {
		final Object size = this.size.getSelectedItem();
		final int iSize = Integer.parseInt(size.toString());
		model.getAttributeController().performSetFontSize(model, iSize);
		model.applyChanges();
		((MModeController) controller.getModeController()).delayedCommit();
	}

	public void beforeMapChange(final MapModel oldMap, final MapModel newMap) {
	}

	private void resetChanges() {
		final int iSize = model.getFontSize();
		size.setSelectedItem(Integer.toString(iSize));
		model.resetChanges();
		controller.getModeController().rollback();
	}

	@Override
	public void show() {
		controller.getModeController().startTransaction();
		super.show();
	}
}