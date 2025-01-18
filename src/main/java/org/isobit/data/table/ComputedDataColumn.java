package org.isobit.data.table;

import javax.swing.table.TableModel;

public abstract class ComputedDataColumn extends AbstractDataColumn {
  String code;
  
  String codeCompiled;
  
  public ComputedDataColumn(String name) {
    setName(name);
  }
  
  public String getCodeCompiled() {
    return this.codeCompiled;
  }
  
  public void setCodeCompiled(String codeCompiled) {
    this.codeCompiled = codeCompiled;
  }
  
  public String getCode() {
    return this.code;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
  
  public String getName() {
    return this.name;
  }
  
  private void setName(String name) {
    this.name = name;
  }
  
  public abstract Object getValue(TableModel paramTableModel, int paramInt);
}
