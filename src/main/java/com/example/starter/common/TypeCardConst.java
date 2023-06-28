package com.example.starter.common;
//1:atm, 2: credit, 3: đảm bảo, 4: prepaid(trả trước), 5: ghi nợ

public enum TypeCardConst {
  ATM("ATM", 1),
  CREDIT("CREDIT",2),
  SECURE_CREDIT_CARD("SECURE CREDIT CARD",3),
  PREPAID("PREPAID",4),
  DEBIT_CARD("DEBIT CARD",5);

  private String typecard;
  private int codeCard;

  TypeCardConst(String typecard, int codeCard) {
    this.typecard = typecard;
    this.codeCard = codeCard;
  }

  public String getTypecard() {
    return typecard;
  }

  public void setTypecard(String typecard) {
    this.typecard = typecard;
  }

  public int getCodeCard() {
    return codeCard;
  }

  public void setCodeCard(int codeCard) {
    this.codeCard = codeCard;
  }
}
