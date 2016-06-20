package com.lib.seatmodel;



public class Seat
{
  /**座位序号，当为走道时 为"Z"*/
  private String index = null;
  /**损坏标签*//*
  private String damagedFlg = null;
  *//**情侣座*//*
  private String loveInd = null;*/

//设置座位的标签
public void setIndex(String paramString)
{
  this.index = paramString;
}

  public String getIndex()
  {
    return this.index;
  }
  /*public boolean a()
  {
    return ("1".equals(this.loveInd)) || ("2".equals(this.loveInd));
  }*/



  /*public void setDamagedFlg(String paramString)
  {
    this.damagedFlg = paramString;
  }

  public String getDamagedFlg()
  {
    return this.damagedFlg;
  }

  public void setLoveInd(String paramString)
  {
    this.loveInd = paramString;
  }

  public String getLoveInd()
  {
    return this.loveInd;
  }*/
}