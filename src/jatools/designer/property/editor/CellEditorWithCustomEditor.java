package jatools.designer.property.editor;

import jatools.swingx.Chooser;
import jatools.swingx.MoreButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;



/**
 * @author   java9
 */
public class CellEditorWithCustomEditor extends AbstractCellEditor
    implements TableCellEditor {
    TableCellEditor inplaceEditor; //嵌入单元格中的编辑器
    Chooser chooser; //后应弹出的选择器
    Object value; // 当前编辑器的值
    JPanel editorContainer; // 嵌入单元格中的pane
    JButton chooserButton;
    JComponent owner;

    /**
    * Creates a new ZCellEditorWithCustomEditor object.
    * @param inplaceEditor_ 嵌入单元格中的编辑器
    * @param chooser_ 点击 [...] 后应弹出的选择器
    *
    * <p> 注意，构造函数不负责将实际的编辑器组件，如JTextField,JComboBox 对象加入到editorContainer中</p>
    * <p> 编辑部件将在  getTableCellEditorComponent 中加入</p>
    * @see #getTableCellEditorComponent
    */
    public CellEditorWithCustomEditor(JComponent owner, Chooser chooser) {
       this(owner,null, chooser);
    }

    /**
    * Creates a new ZCellEditorWithCustomEditor object.
    *
    * @param inplaceEditor DOCUMENT ME!
    * @param chooser DOCUMENT ME!
    */
    public CellEditorWithCustomEditor(JComponent owner,
                                       TableCellEditor inplaceEditor,
                                       Chooser chooser) {
        this.owner = owner;
        this.inplaceEditor = inplaceEditor;
        this.chooser = chooser;


        //建立ui/////////////////////////////////////////////////
        editorContainer = new JPanel(new BorderLayout()); // BorderLayout 东西组件忽略高度，取宽度，南北则反之
                                                          //      中间均忽略


        // BorderLayout 东西组件忽略高度，取宽度，南北则反之
        //      中间均忽略
        chooserButton = new MoreButton(); // 弹出按钮 //
       // chooserButton.setMargin(new Insets(0, 0, 0, 0));

        editorContainer.add(chooserButton, "East"); //
        editorContainer.setBackground(Color.white);

        chooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               

                // 弹出选择器
                if (CellEditorWithCustomEditor.this.chooser.showChooser(CellEditorWithCustomEditor.this.owner,value)) {
                    // 如果修改了属性，就告诉JTable
                    value = CellEditorWithCustomEditor.this.chooser.getValue();
                    stopCellEditing();
                } else {
                    // 如果不想修改，也告诉JTable
                    cancelCellEditing();
                }
            }
        });


        // 双击时，弹出chooser
        editorContainer.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    chooserButton.doClick();
                }
            }
        });

        if (this.inplaceEditor != null) {
            //从嵌入式编辑器中得到消息///////////////////////////////////////////////
            this.inplaceEditor.addCellEditorListener(new CellEditorListener() {
                public void editingStopped(ChangeEvent e) {
                    value = CellEditorWithCustomEditor.this.inplaceEditor.getCellEditorValue();


                    // 如果嵌入的编辑器编辑完成，请告诉我，以便我可以转告JTable
                    fireEditingStopped();
                }

                public void editingCanceled(ChangeEvent e) {
                    // 如果嵌入的编辑器取消了编辑也请告诉我
                    fireEditingCanceled();
                }
            });
        }
    }

    /**
    * JTable 要求一个cell编辑器
    *
    * 从嵌入编辑器中取得编辑器部件，并装入编辑器容器，并将容器返回给table,所以单元格中实际嵌入的是一个容器
    *
    * <p>TableCellEditor 方法</p>
    *
    * @param table 是哪一个table要求编辑器
    * @param value 单元格中的现值
    * @param isSelected 单元格是否被选中
    * @param row 单元格所在行!
    * @param column 单元格所在列
    *
    * @return 返回编辑器部件（editorContainer）
    */
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        Component ie = null;

        if (inplaceEditor == null) {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            ie = renderer.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        } else {
            ie = inplaceEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        editorContainer.add(ie, "Center"); //
        this.value = value;

        return editorContainer;
    }
    
	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject e) {
		
		if(inplaceEditor != null)
		{
			inplaceEditor.isCellEditable( e);
		}
		// TODO Auto-generated method stub
		return super.isCellEditable(e);
	}
    /**
    * table 得知编辑器编辑结束后，来提取现值
    *
    * <p>TableCellEditor 方法</p>
    * @return 现值
    */
    public Object getCellEditorValue() {
        return value;
    }
}
